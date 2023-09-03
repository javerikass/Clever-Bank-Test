package by.clevertec.cleverbank.service;

import by.clevertec.cleverbank.entity.Bank;
import java.util.List;
import java.util.Optional;

public interface BankService {

  long save(Bank bank);

  boolean update(Bank bank);

  boolean delete(Bank bank);

  List<Bank> findAll();

  Optional<Bank> findById(long id);

  Optional<Bank> findBankByAccountNumber(String accountNumber);

}
