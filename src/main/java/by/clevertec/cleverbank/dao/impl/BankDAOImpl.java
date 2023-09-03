package by.clevertec.cleverbank.dao.impl;

import by.clevertec.cleverbank.jdbc.ConnectionPool;
import by.clevertec.cleverbank.dao.BankDAO;
import by.clevertec.cleverbank.entity.Bank;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class BankDAOImpl implements BankDAO {

  private static final BankDAOImpl INSTANCE = new BankDAOImpl();
  private final HikariDataSource dataSource = ConnectionPool.getDataSource();

  private static final String SAVE_BANK = "INSERT INTO bank_system.bank(name) VALUES (?)";
  private static final String UPDATE_BANK = "UPDATE bank_system.bank SET name = ? WHERE bank_id=?";
  private static final String DELETE_BANK = "DELETE FROM bank_system.bank WHERE bank_id=?";
  private static final String FIND_ALL_BANKS = "SELECT * FROM bank_system.bank";
  private static final String FIND_BANK_BY_ID = "SELECT * FROM bank_system.bank WHERE bank_id=?";
  private static final String FIND_BANK_BY_ACCOUNT_NUMBER = "SELECT * FROM bank_system.bank JOIN bank_system.account a ON bank.bank_id = a.bank_id WHERE a.account_number=?";

  private BankDAOImpl() {
  }

  public static BankDAOImpl getInstance() {
    return INSTANCE;
  }

  @Override
  public long save(Bank bank) {
    long bankId = 0;
    Connection connection = null;
    try {
      connection = dataSource.getConnection();
      connection.setAutoCommit(false);
      try (
          PreparedStatement statement = connection.prepareStatement(SAVE_BANK,
              Statement.RETURN_GENERATED_KEYS)) {
        statement.setString(1, bank.getName());
        statement.executeUpdate();
        ResultSet generatedKeys = statement.getGeneratedKeys();
        if (generatedKeys.next()) {
          bankId = generatedKeys.getLong("bank_id");
          bank.setId(bankId);
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
    return bankId;
  }

  @Override
  public boolean update(Bank bank) {
    Connection connection = null;
    try {
      connection = dataSource.getConnection();
      connection.setAutoCommit(false);
      try (
          PreparedStatement statement = connection.prepareStatement(
              UPDATE_BANK,
              Statement.RETURN_GENERATED_KEYS)) {
        statement.setString(1, bank.getName());
        statement.setLong(2, bank.getId());
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
  public boolean delete(Bank bank) {
    Connection connection = null;
    try {
      connection = dataSource.getConnection();
      connection.setAutoCommit(false);
      try (
          PreparedStatement statement = connection.prepareStatement(DELETE_BANK)) {
        statement.setLong(1, bank.getId());
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
  public List<Bank> findAll() {
    List<Bank> bankList = new ArrayList<>();
    Connection connection = null;
    try {
      connection = dataSource.getConnection();
      connection.setAutoCommit(false);
      try (PreparedStatement statement = connection.prepareStatement(FIND_ALL_BANKS)) {
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
          bankList.add(buildBank(resultSet));
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
    return bankList;
  }

  @Override
  public Optional<Bank> findById(long id) {
    Bank bank = null;
    Connection connection = null;
    try {
      connection = dataSource.getConnection();
      connection.setAutoCommit(false);
      try (
          PreparedStatement statement = connection.prepareStatement(FIND_BANK_BY_ID)) {
        statement.setLong(1, id);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
          bank = buildBank(resultSet);
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
    return Optional.ofNullable(bank);
  }

  @Override
  public Optional<Bank> findBankByAccountNumber(String accountNumber) {
    Bank bank = null;
    Connection connection = null;
    try {
      connection = dataSource.getConnection();
      connection.setAutoCommit(false);
      try (
          PreparedStatement statement = connection.prepareStatement(FIND_BANK_BY_ACCOUNT_NUMBER)) {
        statement.setString(1, accountNumber);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
          bank = buildBank(resultSet);
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
    return Optional.ofNullable(bank);
  }

  private Bank buildBank(ResultSet resultSet) throws SQLException {
    long id = resultSet.getLong("bank_id");
    return Bank.builder()
        .id(id)
        .name(resultSet.getString("name"))
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
