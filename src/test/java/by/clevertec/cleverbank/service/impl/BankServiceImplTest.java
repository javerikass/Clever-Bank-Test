package by.clevertec.cleverbank.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import by.clevertec.cleverbank.dao.BankDAO;
import by.clevertec.cleverbank.entity.Account;
import by.clevertec.cleverbank.entity.Bank;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BankServiceImplTest {

  @InjectMocks
  private BankServiceImpl bankService;
  @Mock
  private BankDAO bankDAO;

  @Test
  void testSave() {
    Bank bank = Bank.builder().name("name").build();
    when(bankDAO.save(bank)).thenReturn(1L);
    long id = bankService.save(bank);
    assertEquals(1L, id);
    verify(bankDAO, times(1)).save(bank);
  }

  @Test
  void update() {
    Bank bank = Bank.builder().id(1).name("updatedName").build();
    when(bankDAO.update(bank)).thenReturn(true);
    boolean updated = bankService.update(bank);
    assertTrue(updated);
    verify(bankDAO, times(1)).update(bank);
  }

  @Test
  void delete() {
    Bank bank = Bank.builder().id(1).name("bank").build();
    when(bankDAO.delete(bank)).thenReturn(true);
    boolean deleted = bankService.delete(bank);
    assertTrue(deleted);
    verify(bankDAO, times(1)).delete(bank);
  }

  @Test
  void findAll() {
    List<Bank> banks = Arrays.asList(
        Bank.builder().id(1).name("Bank 1").build(),
        Bank.builder().id(2).name("Bank 2").build());
    when(bankDAO.findAll()).thenReturn(banks);
    List<Bank> result = bankService.findAll();
    assertEquals(banks.size(), result.size());
    verify(bankDAO, times(1)).findAll();
  }

  @Test
  void findById() {
    int bankId = 1;
    Bank bank = Bank.builder().id(bankId).name("Bank 1").build();
    when(bankDAO.findById(bankId)).thenReturn(Optional.of(bank));
    Optional<Bank> result = bankService.findById(bankId);
    assertTrue(result.isPresent());
    assertEquals(bankId, result.get().getId());
    verify(bankDAO, times(1)).findById(bankId);
  }

  @Test
  void findBankByAccountNumber() {
    String accountNumber = "123456789";
    Account account = Account.builder().accountNumber(accountNumber).build();
    Bank bank = Bank.builder().id(1).name("Bank 1").accounts(List.of(account)).build();
    when(bankDAO.findBankByAccountNumber(accountNumber)).thenReturn(Optional.of(bank));
    Optional<Bank> result = bankService.findBankByAccountNumber(accountNumber);
    assertTrue(result.isPresent());
    assertEquals(accountNumber, result.get().getAccounts().get(0).getAccountNumber());
    verify(bankDAO, times(1)).findBankByAccountNumber(accountNumber);
  }
}