package by.clevertec.cleverbank.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import by.clevertec.cleverbank.dao.UserDAO;
import by.clevertec.cleverbank.entity.Account;
import by.clevertec.cleverbank.entity.User;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

  @Mock
  private UserDAO userDAO;

  @InjectMocks
  private UserServiceImpl userService;

  @Test
  void testSave() {
    User user = User.builder().firstName("John").lastName("Doe").build();
    when(userDAO.save(user)).thenReturn(1L);
    long id = userService.save(user);
    assertEquals(1L, id);
    verify(userDAO, times(1)).save(user);
  }

  @Test
  void testUpdate() {
    User user = User.builder().id(1L).firstName("John").lastName("Doe").build();
    when(userDAO.update(user)).thenReturn(true);
    boolean updated = userService.update(user);
    assertTrue(updated);
    verify(userDAO, times(1)).update(user);
  }

  @Test
  void testDelete() {
    User user = User.builder().id(1L).firstName("John").lastName("Doe").build();
    when(userDAO.delete(user)).thenReturn(true);
    boolean deleted = userService.delete(user);
    assertTrue(deleted);
    verify(userDAO, times(1)).delete(user);
  }

  @Test
  void testFindAll() {
    List<User> users = Arrays.asList(
        User.builder().id(1L).firstName("John").lastName("Doe").build(),
        User.builder().id(2L).firstName("Jane").lastName("Smith").build()
    );
    when(userDAO.findAll()).thenReturn(users);
    List<User> result = userService.findAll();
    assertEquals(users.size(), result.size());
    verify(userDAO, times(1)).findAll();
  }

  @Test
  void testFindById() {
    long userId = 1L;
    User user = User.builder().id(userId).firstName("John").lastName("Doe").build();
    when(userDAO.findById(userId)).thenReturn(Optional.of(user));
    Optional<User> result = userService.findById(userId);
    assertTrue(result.isPresent());
    assertEquals(userId, result.get().getId());
    verify(userDAO, times(1)).findById(userId);
  }

  @Test
  void testFindUserByName() {
    String firstName = "John";
    String lastName = "Doe";
    User user = User.builder().id(1L).firstName(firstName).lastName(lastName).build();
    when(userDAO.findUserByName(firstName, lastName)).thenReturn(Optional.of(user));
    Optional<User> result = userService.findUserByName(firstName, lastName);
    assertTrue(result.isPresent());
    assertEquals(firstName, result.get().getFirstName());
    assertEquals(lastName, result.get().getLastName());
    verify(userDAO, times(1)).findUserByName(firstName, lastName);
  }

  @Test
  void testFindUserByAccountNumber() {
    String accountNumber = "123456789";
    Account account = Account.builder().accountNumber(accountNumber).build();
    User user = User.builder().id(1L).firstName("John").lastName("Doe").accounts(List.of(account))
        .build();
    when(userDAO.findUserByAccountNumber(accountNumber)).thenReturn(Optional.of(user));
    Optional<User> result = userService.findUserByAccountNumber(accountNumber);
    assertTrue(result.isPresent());
    assertEquals(accountNumber, result.get().getAccounts().get(0).getAccountNumber());
    verify(userDAO, times(1)).findUserByAccountNumber(accountNumber);
  }
}