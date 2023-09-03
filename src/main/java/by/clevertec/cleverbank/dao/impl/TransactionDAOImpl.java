package by.clevertec.cleverbank.dao.impl;

import by.clevertec.cleverbank.dao.AccountDAO;
import by.clevertec.cleverbank.dao.TransactionDAO;
import by.clevertec.cleverbank.entity.Account;
import by.clevertec.cleverbank.entity.Transaction;
import by.clevertec.cleverbank.entity.TypeTransaction;
import by.clevertec.cleverbank.entity.User;
import by.clevertec.cleverbank.jdbc.ConnectionPool;
import com.zaxxer.hikari.HikariDataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TransactionDAOImpl implements TransactionDAO {

  private static final TransactionDAOImpl INSTANCE = new TransactionDAOImpl();
  private final AccountDAO accountDAO;
  private final HikariDataSource dataSource = ConnectionPool.getDataSource();

  private static final String SAVE_TRANSACTION = "INSERT INTO bank_system.transaction(type, sender_id, recipient_id, amount, timestamp) VALUES (?,?,?,?,?)";
  private static final String UPDATE_TRANSACTION = "UPDATE bank_system.transaction SET type = ?, sender_id = ?, recipient_id = ?,amount = ?,timestamp = ? WHERE id=?";
  private static final String DELETE_TRANSACTION = "DELETE FROM bank_system.transaction WHERE id=?";
  private static final String FIND_ALL = "SELECT * FROM bank_system.transaction";
  private static final String FIND_TRANSACTION_BY_ID = "SELECT * FROM bank_system.transaction WHERE id=?";
  private static final String FIND_TRANSACTION_BY_PERIOD = "SELECT * FROM bank_system.transaction JOIN bank_system.account a ON a.id = transaction.sender_id OR a.id = transaction.recipient_id WHERE a.user_id = ? AND transaction.timestamp >= ? AND transaction.timestamp <= ?";
  private static final String FIND_TRANSACTION_BY_PERIOD_BY_ACCOUNT_NUMBER = "SELECT * FROM bank_system.transaction JOIN bank_system.account a ON a.id = transaction.sender_id OR a.id = transaction.recipient_id WHERE a.account_number=? AND transaction.timestamp >= ? AND transaction.timestamp <= ?";
  private static final String FIND_AMOUNT_FUNDS = "SELECT SUM(amount) AS total_amount FROM bank_system.transaction WHERE type = ? AND timestamp >= ? AND timestamp <= ?";

  private TransactionDAOImpl() {
    accountDAO = AccountDAOImpl.getInstance();
  }

  public static TransactionDAOImpl getInstance() {
    return INSTANCE;
  }

  @Override
  public long save(Transaction transaction) {
    long transactionId = 0;
    Connection connection = null;
    try {
      connection = dataSource.getConnection();
      connection.setAutoCommit(false);
      try (
          PreparedStatement statement = connection.prepareStatement(SAVE_TRANSACTION,
              Statement.RETURN_GENERATED_KEYS)) {
        statement.setString(1, transaction.getType().getDescription());
        statement.setLong(2, transaction.getSender().getId());
        statement.setLong(3, transaction.getRecipient().getId());
        statement.setBigDecimal(4, transaction.getAmount());
        statement.setTimestamp(5, transaction.getTimestamp());
        statement.executeUpdate();
        ResultSet generatedKeys = statement.getGeneratedKeys();
        if (generatedKeys.next()) {
          transactionId = generatedKeys.getLong("id");
          transaction.setId(transactionId);
        }
      } catch (SQLException e) {
        rollbackConnection(connection);
        e.printStackTrace();
      }
      connection.commit();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      closeConnection(connection);
    }
    return transactionId;
  }

  @Override
  public boolean update(Transaction transaction) {
    Connection connection = null;
    try {
      connection = dataSource.getConnection();
      connection.setAutoCommit(false);
      try (
          PreparedStatement statement = connection.prepareStatement(
              UPDATE_TRANSACTION,
              Statement.RETURN_GENERATED_KEYS)) {
        statement.setString(1, transaction.getType().getDescription());
        statement.setLong(2, transaction.getSender().getId());
        statement.setLong(3, transaction.getRecipient().getId());
        statement.setBigDecimal(4, transaction.getAmount());
        statement.setTimestamp(5, transaction.getTimestamp());
        statement.setLong(6, transaction.getId());
        statement.executeUpdate();
      } catch (SQLException e) {
        rollbackConnection(connection);
        e.printStackTrace();
      }
      connection.commit();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      closeConnection(connection);
    }
    return false;
  }

  @Override
  public boolean delete(Transaction transaction) {
    Connection connection = null;
    try {
      connection = dataSource.getConnection();
      connection.setAutoCommit(false);
      try (
          PreparedStatement statement = connection.prepareStatement(DELETE_TRANSACTION)) {
        statement.setLong(1, transaction.getId());
        statement.executeUpdate();
      } catch (SQLException e) {
        rollbackConnection(connection);
        e.printStackTrace();
      }
      connection.commit();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      closeConnection(connection);
    }
    return false;
  }

  @Override
  public List<Transaction> findAll() {
    List<Transaction> transactionList = new ArrayList<>();
    Connection connection = null;
    try {
      connection = dataSource.getConnection();
      connection.setAutoCommit(false);
      try (PreparedStatement statement = connection.prepareStatement(FIND_ALL)) {
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
          transactionList.add(buildTransaction(resultSet));
        }
      } catch (SQLException e) {
        rollbackConnection(connection);
        e.printStackTrace();
      }
      connection.commit();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      closeConnection(connection);
    }
    return transactionList;
  }

  @Override
  public Optional<Transaction> findById(long id) {
    Transaction transaction = null;
    Connection connection = null;
    try {
      connection = dataSource.getConnection();
      connection.setAutoCommit(false);
      try (PreparedStatement statement = connection.prepareStatement(FIND_TRANSACTION_BY_ID)) {
        statement.setLong(1, id);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
          transaction = buildTransaction(resultSet);
        }
      } catch (SQLException e) {
        rollbackConnection(connection);
        e.printStackTrace();
      }
      connection.commit();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      closeConnection(connection);
    }
    return Optional.ofNullable(transaction);
  }

  @Override
  public List<Transaction> getTransactionsByPeriod(User user, Timestamp startDate,
      Timestamp endDate) {
    List<Transaction> transactionList = new ArrayList<>();
    Connection connection = null;
    try {
      connection = dataSource.getConnection();
      connection.setAutoCommit(false);
      try (PreparedStatement statement = connection.prepareStatement(FIND_TRANSACTION_BY_PERIOD)) {
        statement.setLong(1, user.getId());
        statement.setTimestamp(2, startDate);
        statement.setTimestamp(3, endDate);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
          transactionList.add(buildTransaction(resultSet));
        }
      } catch (SQLException e) {
        rollbackConnection(connection);
        e.printStackTrace();
      }
      connection.commit();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      closeConnection(connection);
    }
    return transactionList;
  }

  @Override
  public List<Transaction> getTransactionByPeriodByAccountNumber(String accountNumber,
      Timestamp startDate,
      Timestamp endDate) {
    List<Transaction> transactionList = new ArrayList<>();
    Connection connection = null;
    try {
      connection = dataSource.getConnection();
      connection.setAutoCommit(false);
      try (PreparedStatement statement = connection.prepareStatement(
          FIND_TRANSACTION_BY_PERIOD_BY_ACCOUNT_NUMBER)) {
        statement.setString(1, accountNumber);
        statement.setTimestamp(2, startDate);
        statement.setTimestamp(3, endDate);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
          transactionList.add(buildTransaction(resultSet));
        }
      } catch (SQLException e) {
        rollbackConnection(connection);
        e.printStackTrace();
      }
      connection.commit();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      closeConnection(connection);
    }
    return transactionList;
  }

  @Override
  public BigDecimal calculateAmountFundsMovement(String accountNumber, Timestamp startDate,
      Timestamp endDate, TypeTransaction type) {
    BigDecimal spentAmount = BigDecimal.ZERO;
    Connection connection = null;
    try {
      connection = dataSource.getConnection();
      connection.setAutoCommit(false);
      try (PreparedStatement statement = connection.prepareStatement(FIND_AMOUNT_FUNDS)) {
        statement.setString(1, type.getDescription());
        statement.setTimestamp(2, startDate);
        statement.setTimestamp(3, endDate);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
          spentAmount = resultSet.getBigDecimal("total_amount");
        }
      }
      connection.commit();
    } catch (SQLException e) {
      rollbackConnection(connection);
      e.printStackTrace();
    } finally {
      closeConnection(connection);
    }
    return spentAmount;
  }

  private Transaction buildTransaction(ResultSet resultSet) throws SQLException {
    String type = resultSet.getString("type");
    return Transaction.builder()
        .id(resultSet.getLong("id"))
        .amount(resultSet.getBigDecimal("amount"))
        .recipient(
            accountDAO.findById(resultSet.getLong("recipient_id")).orElse(
                Account.builder().build()))
        .sender(
            accountDAO.findById(resultSet.getLong("sender_id")).orElse(Account.builder().build()))
        .timestamp(resultSet.getTimestamp("timestamp"))
        .type(TypeTransaction.valueOf(type.toUpperCase())).build();
  }

  protected void closeConnection(Connection connection) {
    if (Objects.nonNull(connection)) {
      try {
        connection.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  protected void rollbackConnection(Connection connection) {
    if (Objects.nonNull(connection)) {
      try {
        connection.rollback();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }
}
