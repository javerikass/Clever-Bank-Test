package by.clevertec.cleverbank.sceduler;

import by.clevertec.cleverbank.CheckHandler;
import by.clevertec.cleverbank.dao.impl.AccountDAOImpl;
import by.clevertec.cleverbank.dao.impl.TransactionDAOImpl;
import by.clevertec.cleverbank.entity.Account;
import by.clevertec.cleverbank.jdbc.PropertiesManager;
import by.clevertec.cleverbank.service.AccountService;
import by.clevertec.cleverbank.service.TransactionService;
import by.clevertec.cleverbank.service.impl.AccountServiceImpl;
import by.clevertec.cleverbank.service.impl.TransactionServiceImpl;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AccrualScheduler {

  private final ScheduledExecutorService executorService;
  private final double percentRate;
  private LocalDateTime lastInterestCalculationTime;
  private final AccountService accountService;
  private TransactionService transactionService;
  private CheckHandler checkHandler;


  public AccrualScheduler() {
    this.executorService = Executors.newSingleThreadScheduledExecutor();
    this.percentRate = loadPercentRateFromConfiguration();
    this.lastInterestCalculationTime = LocalDateTime.now();
    this.checkHandler =  new CheckHandler();
    this.transactionService = new TransactionServiceImpl(TransactionDAOImpl.getInstance(),checkHandler);
    this.accountService = new AccountServiceImpl(
        AccountDAOImpl.getInstance(), transactionService);
  }

  public void startScheduler() {
    executorService.scheduleAtFixedRate(this::checkAndCalculatePercentage, 0, 30, TimeUnit.SECONDS);
  }

  private void checkAndCalculatePercentage() {
    LocalDateTime currentTime = LocalDateTime.now();
    CompletableFuture<Boolean> shouldCalculateFuture = CompletableFuture.supplyAsync(
        () -> shouldCalculatePercentage(currentTime));
    shouldCalculateFuture.thenAcceptAsync(shouldCalculate -> {
      if (shouldCalculate) {
        CompletableFuture<List<Account>> accountBalanceFuture = CompletableFuture.supplyAsync(
            this::getAccountBalanceFromDatabase);
        accountBalanceFuture.thenAcceptAsync(accounts -> {
          accounts.forEach(account -> account.setBalance(
              account.getBalance().multiply(BigDecimal.valueOf(percentRate))));
          CompletableFuture<Void> updateBalanceFuture = CompletableFuture.runAsync(
              () -> updateAccountBalanceInDatabase(accounts));
          updateBalanceFuture.thenRun(() -> {
            lastInterestCalculationTime = currentTime;
            log.info("Проценты начислены на остаток счета в конце месяца.");
          });
        });
      }
    });
  }

  public void stopScheduler() {
    executorService.shutdown();
  }

  private boolean shouldCalculatePercentage(LocalDateTime currentTime) {
    return currentTime.getMonth() != lastInterestCalculationTime.getMonth();
  }

  private List<Account> getAccountBalanceFromDatabase() {
    return accountService.getAccountsWithBalance();
  }

  private void updateAccountBalanceInDatabase(List<Account> accounts) {
    for (Account account : accounts) {
      accountService.update(account);
    }
  }

  private double loadPercentRateFromConfiguration() {
    return (100 + Double.parseDouble(PropertiesManager.getProperty("percent"))) / 100;
  }
}
