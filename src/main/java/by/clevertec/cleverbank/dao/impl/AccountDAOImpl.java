package by.clevertec.cleverbank.dao.impl;

import by.clevertec.cleverbank.jdbc.ConnectionPool;
import by.clevertec.cleverbank.dao.AccountDAO;
import by.clevertec.cleverbank.dao.BankDAO;
import by.clevertec.cleverbank.dao.UserDAO;
import by.clevertec.cleverbank.entity.Account;
import by.clevertec.cleverbank.entity.Bank;
import by.clevertec.cleverbank.entity.Transaction;
import by.clevertec.cleverbank.entity.User;
import by.clevertec.cleverbank.entity.AccountOperations;
import com.zaxxer.hikari.HikariDataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


public class AccountDAOImpl implements AccountDAO {

  private static AccountDAOImpl instance;
  private UserDAO userDAO;
  private BankDAO bankDAO;
  private final HikariDataSource dataSource = ConnectionPool.getDataSource();

  private static final String SAVE_ACCOUNT = "INSERT INTO bank_system.account(account_number,currency, balance,bank_id,user_id,open_date) VALUES (?,?,?,?,?,?)";
  private static final String UPDATE_ACCOUNT = "UPDATE bank_system.account SET account_number=?,currency=?, balance=?,bank_id=?,user_id=?,open_date=? WHERE id=?";
  private static final String DELETE_ACCOUNT = "DELETE FROM bank_system.account WHERE id=?";
  private static final String FIND_ALL = "SELECT * FROM bank_system.account";
  private static final String FIND_ACCOUNT_WITH_BALANCE = "SELECT * FROM bank_system.account WHERE account.balance>0";
  private static final String FIND_ACCOUNT_BY_ID = "SELECT * FROM bank_system.account WHERE id=?";
  private static final String FIND_ACCOUNT_BY_ACCOUNT_NUMBER = "SELECT * FROM bank_system.account WHERE account_number=?";
  private static final String FIND_ACCOUNTS_BY_USER_ID = "SELECT * FROM bank_system.account WHERE user_id = ?";
  private static final String FIND_ACCOUNT_BY_BANK_ID = "SELECT * FROM bank_system.account WHERE bank_id=?";

  public static synchronized AccountDAOImpl getInstance() {
    if (instance == null) {
      instance = new AccountDAOImpl();
    }
    return instance;
  }

  public UserDAO getUserDAO() {
    if (userDAO == null) {
      userDAO = UserDAOImpl.getInstance();
    }
    return userDAO;
  }

  public BankDAO getBankDAO() {
    if (bankDAO == null) {
      bankDAO = BankDAOImpl.getInstance();
    }
    return bankDAO;
  }

  @Override
  public long save(Account account) {
    long accountId = 0;
    Connection connection = null;
    try {
      connection = dataSource.getConnection();
      connection.setAutoCommit(false);
      try (PreparedStatement statement = connection.prepareStatement(SAVE_ACCOUNT,
          Statement.RETURN_GENERATED_KEYS)) {
        statement.setString(1, account.getAccountNumber());
        statement.setString(2, account.getCurrency());
        statement.setBigDecimal(3, BigDecimal.valueOf(0));
        statement.setLong(4, account.getBank().getId());
        statement.setLong(5, account.getUser().getId());
        statement.setDate(6, Date.valueOf(LocalDate.now()));
        statement.executeUpdate();
        ResultSet generatedKeys = statement.getGeneratedKeys();
        if (generatedKeys.next()) {
          accountId = generatedKeys.getLong("id");
          account.setId(accountId);
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
    return accountId;
  }

  @Override
  public boolean update(Account account) {
    Connection connection = null;
    try {
      connection = dataSource.getConnection();
      connection.setAutoCommit(false);
      try (
          PreparedStatement statement = connection.prepareStatement(
              UPDATE_ACCOUNT,
              Statement.RETURN_GENERATED_KEYS)) {
        statement.setString(1, account.getAccountNumber());
        statement.setString(2, account.getCurrency());
        statement.setBigDecimal(3, account.getBalance());
        statement.setLong(4, account.getBank().getId());
        statement.setLong(5, account.getUser().getId());
        statement.setDate(6, Date.valueOf(account.getOpenDate()));
        statement.setLong(7, account.getId());
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
  public boolean delete(Account account) {
    Connection connection = null;
    try {
      connection = dataSource.getConnection();
      connection.setAutoCommit(false);
      try (PreparedStatement statement = connection.prepareStatement(DELETE_ACCOUNT)) {
        statement.setLong(1, account.getId());
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
  public List<Account> findAll() {
    List<Account> accountList = new ArrayList<>();
    Connection connection = null;
    try {
      connection = dataSource.getConnection();
      connection.setAutoCommit(false);
      try (
          PreparedStatement statement = connection.prepareStatement(FIND_ALL)) {
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
          accountList.add(buildAccount(resultSet));
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
    return accountList;
  }

  @Override
  public Optional<Account> findById(long id) {
    Account account = null;
    Connection connection = null;
    try {
      connection = dataSource.getConnection();
      try (PreparedStatement statement = connection.prepareStatement(FIND_ACCOUNT_BY_ID)) {
        statement.setLong(1, id);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
          account = buildAccount(resultSet);
        }
      } catch (SQLException e) {
        rollbackConnection(connection);
        e.printStackTrace();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      closeConnection(connection);
    }
    return Optional.ofNullable(account);
  }

  @Override
  public Optional<Account> getAccountByAccountNumber(String accountNumber) {
    Account account = null;
    Connection connection = null;
    try {
      connection = dataSource.getConnection();
      connection.setAutoCommit(false);
      try (PreparedStatement statement = connection.prepareStatement(FIND_ACCOUNT_BY_ACCOUNT_NUMBER)) {
        statement.setString(1, accountNumber);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
          account = buildAccount(resultSet);
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
    return Optional.ofNullable(account);
  }

  @Override
  public List<Account> getAccountsWithBalance() {
    List<Account> accountList = new ArrayList<>();
    Connection connection = null;
    try {
      connection = dataSource.getConnection();
      connection.setAutoCommit(false);
      try (
          PreparedStatement statement = connection.prepareStatement(FIND_ACCOUNT_WITH_BALANCE)) {
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
          accountList.add(buildAccount(resultSet));
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
    return accountList;
  }

  @Override
  public List<Account> getAccountsByUserId(long userId) {
    List<Account> accounts = new ArrayList<>();
    Connection connection = null;
    try {
      connection = dataSource.getConnection();
      connection.setAutoCommit(false);
      try (
          PreparedStatement statement = connection.prepareStatement(FIND_ACCOUNTS_BY_USER_ID)) {
        statement.setLong(1, userId);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
          accounts.add(buildAccount(resultSet));
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
    return accounts;
  }

  @Override
  public List<Account> getAccountsByBankId(long bankId) {
    List<Account> accounts = new ArrayList<>();
    Connection connection = null;
    try {
      connection = dataSource.getConnection();
      connection.setAutoCommit(false);
      try (
          PreparedStatement statement = connection.prepareStatement(FIND_ACCOUNT_BY_BANK_ID)) {
        statement.setLong(1, bankId);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
          accounts.add(buildAccount(resultSet));
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
    return accounts;
  }

  @Override
  public Transaction transferToExternalBank(String fromAccount, String toAccount,
      BigDecimal amount) {
    Connection connection = null;
    try {
      connection = dataSource.getConnection();
      connection.setAutoCommit(false);
      Account from = changeAccountBalance(fromAccount, amount, AccountOperations.SUBTRACT,
          connection);
      Account to = changeAccountBalance(toAccount, amount, AccountOperations.ADD, connection);
      connection.commit();
      return Transaction.builder().sender(from).recipient(to).amount(amount).timestamp(
          Timestamp.valueOf(LocalDateTime.now())).build();
    } catch (SQLException e) {
      rollbackConnection(connection);
      e.printStackTrace();
    } finally {
      closeConnection(connection);
    }
    return Transaction.builder().build();
  }

  private Account changeAccountBalance(String accountNumber, BigDecimal amount,
      AccountOperations operations, Connection connection) {
    Account account = null;
    try (PreparedStatement statement = connection.prepareStatement(FIND_ACCOUNT_BY_ACCOUNT_NUMBER)) {
      statement.setString(1, accountNumber);
      ResultSet resultSet = statement.executeQuery();
      while (resultSet.next()) {
        account = buildAccount(resultSet);
      }
      if (account != null) {
        account.setBalance(operations.name().equals("ADD") ?
            account.getBalance().add(amount) : account.getBalance().subtract(amount));
        updateWithinOneTransaction(account, connection);
        return account;
      }
    } catch (SQLException e) {
      rollbackConnection(connection);
      e.printStackTrace();
    }
    return Account.builder().build();
  }

  private void updateWithinOneTransaction(Account account, Connection connection) {
    try (PreparedStatement statement = connection.prepareStatement(
        UPDATE_ACCOUNT,
        Statement.RETURN_GENERATED_KEYS)) {
      statement.setString(1, account.getAccountNumber());
      statement.setString(2, account.getCurrency());
      statement.setBigDecimal(3, account.getBalance());
      statement.setLong(4, account.getBank().getId());
      statement.setLong(5, account.getUser().getId());
      statement.setDate(6, Date.valueOf(account.getOpenDate()));
      statement.setLong(7, account.getId());
      statement.executeUpdate();
    } catch (SQLException e) {
      rollbackConnection(connection);
      e.printStackTrace();
    }
  }

  private Account buildAccount(ResultSet resultSet) throws SQLException {
    String accountNumber = resultSet.getString("account_number");
    return Account.builder()
        .id(resultSet.getLong("id"))
        .accountNumber(accountNumber)
        .balance(resultSet.getBigDecimal("balance"))
        .currency(resultSet.getString("currency"))
        .openDate(LocalDate.now())
        .bank(getBankDAO().findById(resultSet.getLong("bank_id")).orElse(Bank.builder().build()))
        .user(getUserDAO().findById(resultSet.getLong("user_id")).orElse(User.builder().build()))
        .build();
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
