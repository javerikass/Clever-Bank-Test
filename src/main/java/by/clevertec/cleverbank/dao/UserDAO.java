package by.clevertec.cleverbank.dao;

import by.clevertec.cleverbank.entity.User;
import java.util.Optional;

public interface UserDAO extends BaseDAO<User> {

  Optional<User> findUserByName(String firstName, String lastName);

  Optional<User> findUserByAccountNumber(String accountNumber);

}
