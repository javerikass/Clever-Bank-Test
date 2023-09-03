package by.clevertec.cleverbank.service;

import by.clevertec.cleverbank.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserService {

  long save(User user);

  boolean update(User user);

  boolean delete(User user);

  List<User> findAll();

  Optional<User> findById(long id);

  Optional<User> findUserByName(String firstName, String lastName);

  Optional<User> findUserByAccountNumber(String accountNumber);
}
