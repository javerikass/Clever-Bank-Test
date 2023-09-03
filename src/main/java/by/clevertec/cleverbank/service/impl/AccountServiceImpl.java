package by.clevertec.cleverbank.service.impl;

import by.clevertec.cleverbank.dao.AccountDAO;
import by.clevertec.cleverbank.dao.impl.TransactionDAOImpl;
import by.clevertec.cleverbank.entity.Account;
import by.clevertec.cleverbank.entity.Transaction;
import by.clevertec.cleverbank.entity.TypeTransaction;
import by.clevertec.cleverbank.service.AccountService;
import by.clevertec.cleverbank.service.TransactionService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class AccountServiceImpl implements AccountService {

  private final AccountDAO accountDAO;
  private final TransactionService transactionService;

  private static final String SUPERIOR_CALLED_METHOD = "transferWithinBank_aroundBody20";

  public AccountServiceImpl(AccountDAO accountDAO, TransactionService transactionService) {
    this.accountDAO = accountDAO;
    this.transactionService = transactionService;
//    this.transactionService = new TransactionServiceImpl(TransactionDAOImpl.getInstance());
  }

  @Override
  public long save(Account account) {
    return accountDAO.save(account);
  }

  @Override
  public boolean update(Account account) {
    return accountDAO.update(account);
  }

  @Override
  public boolean delete(Account account) {
    return accountDAO.delete(account);
  }

  @Override
  public List<Account> findAll() {
    return accountDAO.findAll();
  }

  @Override
  public Optional<Account> findById(long id) {
    return accountDAO.findById(id);
  }

  @Override
  public List<Account> getAccountsWithBalance() {
    return accountDAO.getAccountsWithBalance();
  }


  @Override
  public List<Account> getAccountsByUserId(long userId) {
    return accountDAO.getAccountsByUserId(userId);
  }

  @Override
  public List<Account> getAccountsByBankId(long bankId) {
    return accountDAO.getAccountsByBankId(bankId);
  }

  @Override
  public Optional<Account> getAccountByAccountNumber(String accountNumber) {
    return accountDAO.getAccountByAccountNumber(accountNumber);
  }

  @Override
  public Transaction depositOrWithdrawFunds(BigDecimal amount, String accountNumber,
      TypeTransaction typeTransaction) {
    Optional<Account> optionalAccount = accountDAO.getAccountByAccountNumber(accountNumber);
    if (optionalAccount.isPresent()) {
      Account account = optionalAccount.get();
      account.setBalance(calculateBalance(account.getBalance(), amount, typeTransaction));
      update(account);
      StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
      if (!stackTraceElements[4].getMethodName().equals(SUPERIOR_CALLED_METHOD)) {
        return transactionService.getAndSaveTransaction(amount, account, account, typeTransaction);
      }
    }
    return Transaction.builder().build();
  }

  private BigDecimal calculateBalance(BigDecimal balance, BigDecimal amount,
      TypeTransaction typeTransaction) {
    return typeTransaction.getDescription().equals(TypeTransaction.DEPOSIT.getDescription())
        ? balance.add(amount) : balance.subtract(amount);
  }

  @Override
  public Transaction transferWithinBank(String fromAccount, String toAccount, BigDecimal amount) {
    depositOrWithdrawFunds(amount, fromAccount, TypeTransaction.WITHDRAWAL);
    depositOrWithdrawFunds(amount, toAccount, TypeTransaction.DEPOSIT);
    Account sender = accountDAO.getAccountByAccountNumber(fromAccount)
        .orElse(Account.builder().build());
    Account recipient = accountDAO.getAccountByAccountNumber(toAccount)
        .orElse(Account.builder().build());
    return transactionService.getAndSaveTransaction(amount, sender, recipient,
        TypeTransaction.TRANSFER);
  }

  @Override
  public Transaction transferToExternalBank(String fromAccount, String toAccount,
      BigDecimal amount) {
    Transaction transaction = accountDAO.transferToExternalBank(fromAccount, toAccount, amount);
    return transactionService.getAndSaveTransaction(amount, transaction.getSender(),
        transaction.getRecipient(), TypeTransaction.TRANSFER);
  }
}
