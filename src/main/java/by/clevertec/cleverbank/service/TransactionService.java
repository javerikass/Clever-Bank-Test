package by.clevertec.cleverbank.service;

import by.clevertec.cleverbank.entity.Account;
import by.clevertec.cleverbank.entity.Transaction;
import by.clevertec.cleverbank.entity.TypeTransaction;
import by.clevertec.cleverbank.entity.User;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface TransactionService {

  long save(Transaction transaction);

  boolean update(Transaction transaction);

  boolean delete(Transaction transaction);

  Optional<Transaction> findById(long id);

  List<Transaction> findAll();

  Transaction getAndSaveTransaction(BigDecimal amount, Account sender, Account recipient,
      TypeTransaction type);

  List<Transaction> getTransactionsByPeriod(User user, Timestamp startDate, Timestamp endDate);

  List<Transaction> getTransactionByPeriodByAccountNumber(String accountNumber, Timestamp startDate,
      Timestamp endDate);

  BigDecimal calculateAmountFundsMovement(String accountNumber, Timestamp startDate,
      Timestamp endDate, TypeTransaction type);
}
