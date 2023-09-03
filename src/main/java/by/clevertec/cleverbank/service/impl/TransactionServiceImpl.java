package by.clevertec.cleverbank.service.impl;

import by.clevertec.cleverbank.CheckHandler;
import by.clevertec.cleverbank.dao.TransactionDAO;
import by.clevertec.cleverbank.entity.Account;
import by.clevertec.cleverbank.entity.Transaction;
import by.clevertec.cleverbank.entity.TypeTransaction;
import by.clevertec.cleverbank.entity.User;
import by.clevertec.cleverbank.service.TransactionService;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class TransactionServiceImpl implements TransactionService {

  private final TransactionDAO transactionDAO;
  private final CheckHandler checkHandler;

  public TransactionServiceImpl(TransactionDAO transactionDAO, CheckHandler checkHandler) {
    this.transactionDAO = transactionDAO;
    this.checkHandler = checkHandler;
  }

  @Override
  public long save(Transaction transaction) {
    return transactionDAO.save(transaction);
  }

  @Override
  public boolean update(Transaction transaction) {
    return transactionDAO.update(transaction);
  }

  @Override
  public boolean delete(Transaction transaction) {
    return transactionDAO.delete(transaction);
  }

  @Override
  public List<Transaction> findAll() {
    return transactionDAO.findAll();
  }

  @Override
  public Optional<Transaction> findById(long id) {
    return transactionDAO.findById(id);
  }

  @Override
  public List<Transaction> getTransactionsByPeriod(User user, Timestamp startDate,
      Timestamp endDate) {
    return transactionDAO.getTransactionsByPeriod(user, startDate, endDate);
  }

  @Override
  public List<Transaction> getTransactionByPeriodByAccountNumber(String accountNumber,
      Timestamp startDate, Timestamp endDate) {
    return transactionDAO.getTransactionByPeriodByAccountNumber(accountNumber, startDate, endDate);
  }

  @Override
  public BigDecimal calculateAmountFundsMovement(String accountNumber, Timestamp startDate,
      Timestamp endDate, TypeTransaction type) {
    return transactionDAO.calculateAmountFundsMovement(accountNumber, startDate, endDate, type);
  }

  @Override
  public Transaction getAndSaveTransaction(BigDecimal amount, Account sender, Account recipient,
      TypeTransaction type) {
    Transaction transaction = Transaction.builder()
        .type(type)
        .sender(sender)
        .recipient(recipient)
        .amount(amount)
        .build();
    transaction.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
    long id = transactionDAO.save(transaction);
    transaction.setId(id);
    checkHandler.generateCheck(transaction);
    return transaction;
  }
}
