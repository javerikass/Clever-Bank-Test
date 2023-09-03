package by.clevertec.cleverbank.service.impl;

import by.clevertec.cleverbank.dao.UserDAO;
import by.clevertec.cleverbank.entity.User;
import by.clevertec.cleverbank.service.UserService;
import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {

  private final UserDAO userDAO;

  public UserServiceImpl(UserDAO userDAO) {
    this.userDAO = userDAO;
  }

  @Override
  public long save(User user) {
    return userDAO.save(user);
  }

  @Override
  public boolean update(User user) {
    return userDAO.update(user);
  }

  @Override
  public boolean delete(User user) {
    return userDAO.delete(user);
  }

  @Override
  public List<User> findAll() {
    return userDAO.findAll();
  }

  @Override
  public Optional<User> findById(long id) {
    return userDAO.findById(id);
  }

  @Override
  public Optional<User> findUserByName(String firstName, String lastName) {
    return userDAO.findUserByName(firstName, lastName);
  }

  @Override
  public Optional<User> findUserByAccountNumber(String accountNumber) {
    return userDAO.findUserByAccountNumber(accountNumber);
  }
}
