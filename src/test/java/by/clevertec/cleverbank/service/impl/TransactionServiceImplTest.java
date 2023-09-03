package by.clevertec.cleverbank.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import by.clevertec.cleverbank.CheckHandler;
import by.clevertec.cleverbank.dao.TransactionDAO;
import by.clevertec.cleverbank.entity.Account;
import by.clevertec.cleverbank.entity.Bank;
import by.clevertec.cleverbank.entity.Transaction;
import by.clevertec.cleverbank.entity.TypeTransaction;
import by.clevertec.cleverbank.entity.User;
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
class TransactionServiceImplTest {

  @Mock
  private TransactionDAO transactionDAO;

  @Mock
  private CheckHandler checkHandler;

  @InjectMocks
  private TransactionServiceImpl transactionService;

  @Test
  void testSave() {
    Transaction transaction = Transaction.builder().amount(BigDecimal.valueOf(100)).build();
    when(transactionDAO.save(transaction)).thenReturn(1L);
    long id = transactionService.save(transaction);
    assertEquals(1L, id);
    verify(transactionDAO, times(1)).save(transaction);
  }

  @Test
  void testUpdate() {
    Transaction transaction = Transaction.builder().id(1L).amount(BigDecimal.valueOf(100)).build();
    when(transactionDAO.update(transaction)).thenReturn(true);
    boolean updated = transactionService.update(transaction);
    assertTrue(updated);
    verify(transactionDAO, times(1)).update(transaction);
  }

  @Test
  void testDelete() {
    Transaction transaction = Transaction.builder().id(1L).amount(BigDecimal.valueOf(100)).build();
    when(transactionDAO.delete(transaction)).thenReturn(true);
    boolean deleted = transactionService.delete(transaction);
    assertTrue(deleted);
    verify(transactionDAO, times(1)).delete(transaction);
  }

  @Test
  void testFindAll() {
    List<Transaction> transactions = Arrays.asList(
        Transaction.builder().id(1L).amount(BigDecimal.valueOf(100)).build(),
        Transaction.builder().id(2L).amount(BigDecimal.valueOf(200)).build()
    );
    when(transactionDAO.findAll()).thenReturn(transactions);
    List<Transaction> result = transactionService.findAll();
    assertEquals(transactions.size(), result.size());
    verify(transactionDAO, times(1)).findAll();
  }

  @Test
  void testFindById() {
    long transactionId = 1L;
    Transaction transaction = Transaction.builder().id(transactionId)
        .amount(BigDecimal.valueOf(100)).build();
    when(transactionDAO.findById(transactionId)).thenReturn(Optional.of(transaction));
    Optional<Transaction> result = transactionService.findById(transactionId);
    assertTrue(result.isPresent());
    assertEquals(transactionId, result.get().getId());
    verify(transactionDAO, times(1)).findById(transactionId);
  }

  @Test
  void testGetTransactionsByPeriod() {
    User user = User.builder().id(1L).firstName("John").lastName("Doe").build();
    Timestamp startDate = Timestamp.valueOf(LocalDateTime.now().minusDays(7));
    Timestamp endDate = Timestamp.valueOf(LocalDateTime.now());
    List<Transaction> transactions = Arrays.asList(
        Transaction.builder().id(1L).amount(BigDecimal.valueOf(100)).build(),
        Transaction.builder().id(2L).amount(BigDecimal.valueOf(200)).build()
    );
    when(transactionDAO.getTransactionsByPeriod(user, startDate, endDate)).thenReturn(transactions);
    List<Transaction> result = transactionService.getTransactionsByPeriod(user, startDate, endDate);
    assertEquals(transactions.size(), result.size());
    verify(transactionDAO, times(1)).getTransactionsByPeriod(user, startDate, endDate);
  }

  @Test
  void testGetTransactionByPeriodByAccountNumber() {
    String accountNumber = "123456789";
    Timestamp startDate = Timestamp.valueOf(LocalDateTime.now().minusDays(7));
    Timestamp endDate = Timestamp.valueOf(LocalDateTime.now());
    List<Transaction> transactions = Arrays.asList(
        Transaction.builder().id(1L).amount(BigDecimal.valueOf(100)).build(),
        Transaction.builder().id(2L).amount(BigDecimal.valueOf(200)).build()
    );
    when(transactionDAO.getTransactionByPeriodByAccountNumber(accountNumber, startDate,
        endDate)).thenReturn(transactions);
    List<Transaction> result = transactionService.getTransactionByPeriodByAccountNumber(
        accountNumber, startDate, endDate);
    assertEquals(transactions.size(), result.size());
    verify(transactionDAO, times(1)).getTransactionByPeriodByAccountNumber(accountNumber, startDate,
        endDate);
  }

  @Test
  void testCalculateAmountFundsMovement() {
    String accountNumber = "123456789";
    Timestamp startDate = Timestamp.valueOf(LocalDateTime.now().minusDays(7));
    Timestamp endDate = Timestamp.valueOf(LocalDateTime.now());
    BigDecimal amount = BigDecimal.valueOf(500);
    TypeTransaction type = TypeTransaction.DEPOSIT;
    when(transactionDAO.calculateAmountFundsMovement(accountNumber, startDate, endDate,
        type)).thenReturn(amount);
    BigDecimal result = transactionService.calculateAmountFundsMovement(accountNumber, startDate,
        endDate, type);
    assertEquals(amount, result);
    verify(transactionDAO, times(1)).calculateAmountFundsMovement(accountNumber, startDate, endDate,
        type);
  }

  @Test
  void testGetAndSaveTransaction() {
    BigDecimal amount = BigDecimal.valueOf(100);
    Bank bank = Bank.builder().name("bank").id(1).build();
    Bank bank2 = Bank.builder().name("bank2").id(2).build();
    Account sender = Account.builder().bank(bank).id(1L).accountNumber("123456789").build();
    Account recipient = Account.builder().bank(bank2).id(2L).accountNumber("987654321").build();
    doNothing().when(checkHandler).generateCheck(Transaction.builder().build());
    Transaction transaction = transactionService.getAndSaveTransaction(amount, sender, recipient,
        TypeTransaction.TRANSFER);

    assertNotNull(transaction);
    assertEquals(amount, transaction.getAmount());
    assertEquals(sender, transaction.getSender());
    assertEquals(recipient, transaction.getRecipient());
    assertEquals(TypeTransaction.TRANSFER, transaction.getType());
    assertNotNull(transaction.getTimestamp());
  }
}
