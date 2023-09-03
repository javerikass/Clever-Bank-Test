package by.clevertec.cleverbank.dao.impl;

import by.clevertec.cleverbank.dao.UserDAO;
import by.clevertec.cleverbank.entity.User;
import by.clevertec.cleverbank.jdbc.ConnectionPool;
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

public class UserDAOImpl implements UserDAO {

  private static final UserDAOImpl INSTANCE = new UserDAOImpl();
  private final HikariDataSource dataSource = ConnectionPool.getDataSource();

  private static final String SAVE_USER = "INSERT INTO bank_system.users(first_name, last_name, patronymic) VALUES (?,?,?)";
  private static final String UPDATE_USER = "UPDATE bank_system.users SET first_name = ?, last_name = ?, patronymic = ? WHERE user_id=?";
  private static final String DELETE_USER = "DELETE FROM bank_system.users WHERE user_id=?";
  private static final String FIND_ALL = "SELECT * FROM bank_system.users";
  private static final String FIND_USER_BY_NAME = "SELECT * FROM bank_system.users WHERE first_name=? AND last_name=?";
  private static final String FIND_USER_BY_ID = "SELECT * FROM bank_system.users WHERE user_id=?";
  private static final String FIND_USER_BY_ACCOUNT_NUMBER = "SELECT * FROM bank_system.users JOIN bank_system.account a ON users.user_id = a.user_id WHERE a.account_number=?";

  private UserDAOImpl() {
  }

  public static UserDAOImpl getInstance() {
    return INSTANCE;
  }

//  private static UserDAOImpl instance;
////  private AccountDAO accountDAO;
//  private final HikariDataSource dataSource = ConnectionPool.getDataSource();
//
//  private UserDAOImpl() {
//  }
//
//  public static synchronized UserDAOImpl getInstance() {
//    if (instance == null) {
//      instance = new UserDAOImpl();
//    }
//    return instance;
//  }

//  public AccountDAO getAccountDAO() {
//    if (accountDAO == null) {
//      accountDAO = AccountDAOImpl.getInstance();
//    }
//    return accountDAO;
//  }

  @Override
  public long save(User user) {
    long userId = 0;
    Connection connection = null;
    try {
      connection = dataSource.getConnection();
      connection.setAutoCommit(false);
      try (PreparedStatement statement = connection.prepareStatement(SAVE_USER,
          Statement.RETURN_GENERATED_KEYS)) {
        statement.setString(1, user.getFirstName());
        statement.setString(2, user.getLastName());
        statement.setString(3, user.getPatronymic());
        statement.executeUpdate();
        ResultSet generatedKeys = statement.getGeneratedKeys();
        if (generatedKeys.next()) {
          userId = generatedKeys.getLong("user_id");
          user.setId(userId);
        }
        connection.commit();
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
    return userId;
  }

  @Override
  public boolean update(User user) {
    Connection connection = null;
    try {
      connection = dataSource.getConnection();
      connection.setAutoCommit(false);
      try (PreparedStatement statement = connection.prepareStatement(
          UPDATE_USER,
          Statement.RETURN_GENERATED_KEYS)) {
        statement.setString(1, user.getFirstName());
        statement.setString(2, user.getLastName());
        statement.setString(3, user.getPatronymic());
        statement.setObject(4, user.getId());
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
  public boolean delete(User entity) {
    Connection connection = null;
    try {
      connection = dataSource.getConnection();
      connection.setAutoCommit(false);
      try (PreparedStatement userStatement = connection.prepareStatement(DELETE_USER)) {
        userStatement.setLong(1, entity.getId());
        userStatement.executeUpdate();
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
  public List<User> findAll() {
    List<User> userList = new ArrayList<>();
    Connection connection = null;
    try {
      connection = dataSource.getConnection();
      connection.setAutoCommit(false);
      try (PreparedStatement userStatement = connection.prepareStatement(FIND_ALL)) {
        ResultSet resultSet = userStatement.executeQuery();
        while (resultSet.next()) {
          userList.add(buildUser(resultSet));
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
    return userList;
  }

  @Override
  public Optional<User> findById(long id) {
    User user = null;
    Connection connection = null;
    try {
      connection = dataSource.getConnection();
      connection.setAutoCommit(false);
      try (PreparedStatement userStatement = connection.prepareStatement(FIND_USER_BY_ID)) {
        userStatement.setLong(1, id);
        ResultSet resultSet = userStatement.executeQuery();
        while (resultSet.next()) {
          user = buildUser(resultSet);
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
    return Optional.ofNullable(user);
  }

  @Override
  public Optional<User> findUserByAccountNumber(String accountNumber) {
    User user = null;
    Connection connection = null;
    try {
      connection = dataSource.getConnection();
      connection.setAutoCommit(false);
      try (PreparedStatement userStatement = connection.prepareStatement(
          FIND_USER_BY_ACCOUNT_NUMBER)) {
        userStatement.setString(1, accountNumber);
        ResultSet resultSet = userStatement.executeQuery();
        while (resultSet.next()) {
          user = buildUser(resultSet);
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
    return Optional.ofNullable(user);
  }

  @Override
  public Optional<User> findUserByName(String firstName, String lastName) {
    User user = null;
    Connection connection = null;
    try {
      connection = dataSource.getConnection();
      connection.setAutoCommit(false);
      try (PreparedStatement userStatement = connection.prepareStatement(FIND_USER_BY_NAME)) {
        userStatement.setString(1, firstName);
        userStatement.setString(2, lastName);
        ResultSet resultSet = userStatement.executeQuery();
        while (resultSet.next()) {
          user = buildUser(resultSet);
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
    return Optional.ofNullable(user);
  }

  private User buildUser(ResultSet resultSet) throws SQLException {
    long id = resultSet.getLong("user_id");
    return User.builder()
        .id(id)
        .firstName(resultSet.getString("first_name"))
        .lastName(resultSet.getString("last_name"))
        .patronymic(resultSet.getString("patronymic"))
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
