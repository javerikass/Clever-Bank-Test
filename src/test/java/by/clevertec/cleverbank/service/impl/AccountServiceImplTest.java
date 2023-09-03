package by.clevertec.cleverbank.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import by.clevertec.cleverbank.dao.AccountDAO;
import by.clevertec.cleverbank.dao.TransactionDAO;
import by.clevertec.cleverbank.entity.Account;
import by.clevertec.cleverbank.entity.Bank;
import by.clevertec.cleverbank.entity.Transaction;
import by.clevertec.cleverbank.entity.TypeTransaction;
import by.clevertec.cleverbank.entity.User;
import by.clevertec.cleverbank.service.TransactionService;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AccountServiceImplTest {

  @Mock
  private AccountDAO accountDAO;

  @Mock
  private TransactionDAO transactionDAO;

  @Mock
  private TransactionService transactionService;

  @InjectMocks
  private AccountServiceImpl accountService;


  @Test
  void testSave() {
    Account account = Account.builder().id(1L).accountNumber("123456789").build();
    when(accountDAO.save(account)).thenReturn(1L);
    long id = accountService.save(account);
    assertEquals(1L, id);
    verify(accountDAO, times(1)).save(account);
  }

  @Test
  void testUpdate() {
    Account account = Account.builder().id(1L).accountNumber("123456789").build();
    when(accountDAO.update(account)).thenReturn(true);
    boolean updated = accountService.update(account);
    assertTrue(updated);
    verify(accountDAO, times(1)).update(account);
  }

  @Test
  void testDelete() {
    Account account = Account.builder().id(1L).accountNumber("123456789").build();
    when(accountDAO.delete(account)).thenReturn(true);
    boolean deleted = accountService.delete(account);
    assertTrue(deleted);
    verify(accountDAO, times(1)).delete(account);
  }

  @Test
  void testFindAll() {
    List<Account> accounts = Arrays.asList(
        Account.builder().id(1L).accountNumber("123456789").build(),
        Account.builder().id(2L).accountNumber("987654321").build()
    );
    when(accountDAO.findAll()).thenReturn(accounts);
    List<Account> result = accountService.findAll();
    assertEquals(accounts.size(), result.size());
    verify(accountDAO, times(1)).findAll();
  }

  @Test
  void testFindById() {
    long accountId = 1L;
    Account account = Account.builder().id(accountId).accountNumber("123456789").build();
    when(accountDAO.findById(accountId)).thenReturn(Optional.of(account));
    Optional<Account> result = accountService.findById(accountId);
    assertTrue(result.isPresent());
    assertEquals(accountId, result.get().getId());
    verify(accountDAO, times(1)).findById(accountId);
  }

  @Test
  void testGetAccountsWithBalance() {
    List<Account> accounts = Arrays.asList(
        Account.builder().id(1L).accountNumber("123456789").balance(BigDecimal.valueOf(100))
            .build(),
        Account.builder().id(2L).accountNumber("987654321").balance(BigDecimal.valueOf(200)).build()
    );
    when(accountDAO.getAccountsWithBalance()).thenReturn(accounts);
    List<Account> result = accountService.getAccountsWithBalance();
    assertEquals(accounts.size(), result.size());
    verify(accountDAO, times(1)).getAccountsWithBalance();
  }

  @Test
  void testGetAccountsByUserId() {
    long userId = 1L;
    long user2Id = 2L;
    List<Account> accounts = Arrays.asList(
        Account.builder().id(1L).accountNumber("123456789").user(User.builder().id(userId).build())
            .build(),
        Account.builder().id(2L).accountNumber("987654321").user(User.builder().id(user2Id).build())
            .build()
    );
    when(accountDAO.getAccountsByUserId(userId)).thenReturn(accounts);
    List<Account> result = accountService.getAccountsByUserId(userId);
    assertEquals(accounts.size(), result.size());
    verify(accountDAO, times(1)).getAccountsByUserId(userId);
  }

  @Test
  void testGetAccountsByBankId() {
    long bankId = 1L;
    long bank2Id = 2L;
    List<Account> accounts = Arrays.asList(
        Account.builder().id(1L).accountNumber("123456789").bank(Bank.builder().id(bankId).build())
            .build(),
        Account.builder().id(2L).accountNumber("987654321").bank(Bank.builder().id(bank2Id).build())
            .build()
    );
    when(accountDAO.getAccountsByBankId(bankId)).thenReturn(accounts);
    List<Account> result = accountService.getAccountsByBankId(bankId);
    assertEquals(accounts.size(), result.size());
    verify(accountDAO, times(1)).getAccountsByBankId(bankId);
  }

  @Test
  void testGetAccountByAccountNumber() {
    String accountNumber = "123456789";
    Account account = Account.builder().id(1L).accountNumber(accountNumber).build();
    when(accountDAO.getAccountByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

    Optional<Account> result = accountService.getAccountByAccountNumber(accountNumber);
    assertTrue(result.isPresent());
    assertEquals(accountNumber, result.get().getAccountNumber());
    verify(accountDAO, times(1)).getAccountByAccountNumber(accountNumber);
  }

  @Test
  void testDepositOrWithdrawFunds() {
    BigDecimal amount = BigDecimal.valueOf(100);
    String accountNumber = "123456789";
    TypeTransaction typeTransaction = TypeTransaction.DEPOSIT;

    Bank bank = Bank.builder().name("bank").id(1).build();
    Account account = Account.builder().id(1L).accountNumber(accountNumber).bank(bank)
        .balance(BigDecimal.valueOf(200)).build();
    Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());

    Transaction transaction = Transaction.builder()
        .type(typeTransaction)
        .sender(account)
        .recipient(account)
        .amount(amount)
        .timestamp(timestamp)
        .build();

    when(accountDAO.getAccountByAccountNumber(accountNumber)).thenReturn(Optional.of(account));
    when(accountDAO.update(account)).thenReturn(true);
    when(transactionService.getAndSaveTransaction(amount, account, account, typeTransaction))
        .thenReturn(transaction);

    Transaction result = accountService.depositOrWithdrawFunds(amount, accountNumber, typeTransaction);
    result.setTimestamp(timestamp);
    assertNotNull(result);
    assertEquals(transaction, result);
    assertEquals(BigDecimal.valueOf(300), account.getBalance());
    verify(accountDAO, times(1)).update(account);
    verify(transactionService, times(1))
        .getAndSaveTransaction(amount, account, account, typeTransaction);
  }

  @Test
  void testTransferWithinBank() {
    String fromAccount = "123456789";
    String toAccount = "987654321";
    BigDecimal amount = BigDecimal.valueOf(100);
    Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());

    Bank bank = Bank.builder().name("bank").id(1).build();
    Bank bank2 = Bank.builder().name("bank2").id(2).build();

    Account account1 = Account.builder().id(1L).accountNumber(fromAccount).bank(bank)
        .balance(BigDecimal.valueOf(200)).build();
    Account account2 = Account.builder().id(2L).accountNumber(toAccount).bank(bank2)
        .balance(BigDecimal.valueOf(300)).build();

    Transaction transaction = Transaction.builder().type(TypeTransaction.TRANSFER).amount(amount)
        .sender(account1).recipient(account2).timestamp(timestamp).build();

    when(accountDAO.getAccountByAccountNumber(fromAccount)).thenReturn(Optional.of(account1));
    when(accountDAO.getAccountByAccountNumber(toAccount)).thenReturn(Optional.of(account2));
    when(transactionService.getAndSaveTransaction(amount, account1, account2, TypeTransaction.TRANSFER))
        .thenReturn(transaction);

    Transaction result = accountService.transferWithinBank(fromAccount, toAccount, amount);
    result.setTimestamp(timestamp);
    assertNotNull(result);
    assertEquals(transaction, result);
    assertEquals(BigDecimal.valueOf(100), account1.getBalance());
    assertEquals(BigDecimal.valueOf(400), account2.getBalance());
    verify(accountDAO, times(2)).update(any(Account.class));

  }

  @Test
  void testTransferToExternalBank() {
    String fromAccount = "123456789";
    String toAccount = "externalAccount";
    Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
    Bank bank = Bank.builder().name("bank").id(1).build();
    Bank bank2 = Bank.builder().name("bank2").id(2).build();
    Account account1 = Account.builder().id(1L).accountNumber(fromAccount).bank(bank)
        .balance(BigDecimal.valueOf(200)).build();
    Account account2 = Account.builder().id(2L).accountNumber(toAccount).bank(bank2)
        .balance(BigDecimal.valueOf(300)).build();
    BigDecimal amount = BigDecimal.valueOf(100);
    Transaction transaction = Transaction.builder().id(1L).recipient(account2).sender(account1).type(TypeTransaction.TRANSFER).timestamp(timestamp).amount(amount).build();
    when(accountDAO.transferToExternalBank(fromAccount, toAccount, amount)).thenReturn(transaction);
    when(transactionService.getAndSaveTransaction(amount, account1, account2, TypeTransaction.TRANSFER))
        .thenReturn(transaction);

    Transaction result = accountService.transferToExternalBank(fromAccount, toAccount, amount);
    assertNotNull(result);
    assertEquals(transaction, result);
    verify(accountDAO, times(1)).transferToExternalBank(fromAccount, toAccount, amount);
    verify(transactionService, times(1)).getAndSaveTransaction(amount, account1, account2, TypeTransaction.TRANSFER);
  }
}
