package by.clevertec.cleverbank.dao;

import by.clevertec.cleverbank.entity.Transaction;
import by.clevertec.cleverbank.entity.TypeTransaction;
import by.clevertec.cleverbank.entity.User;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public interface TransactionDAO extends BaseDAO<Transaction> {

  List<Transaction> getTransactionsByPeriod(User user, Timestamp startDate, Timestamp endDate);

  List<Transaction> getTransactionByPeriodByAccountNumber(String accountNumber, Timestamp startDate,
      Timestamp endDate);

  BigDecimal calculateAmountFundsMovement(String accountNumber, Timestamp startDate,
      Timestamp endDate, TypeTransaction type);

}
