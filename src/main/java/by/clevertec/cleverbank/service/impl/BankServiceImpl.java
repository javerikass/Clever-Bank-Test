package by.clevertec.cleverbank.service.impl;

import by.clevertec.cleverbank.dao.BankDAO;
import by.clevertec.cleverbank.entity.Bank;
import by.clevertec.cleverbank.service.BankService;
import java.util.List;
import java.util.Optional;

public class BankServiceImpl implements BankService {

  private final BankDAO bankDAO;

  public BankServiceImpl(BankDAO bankDAO) {
    this.bankDAO = bankDAO;
  }

  @Override
  public long save(Bank bank) {
    return bankDAO.save(bank);
  }

  @Override
  public boolean update(Bank bank) {
    return bankDAO.update(bank);
  }

  @Override
  public boolean delete(Bank bank) {
    return bankDAO.delete(bank);
  }

  @Override
  public List<Bank> findAll() {
    return bankDAO.findAll();
  }

  @Override
  public Optional<Bank> findById(long id) {
    return bankDAO.findById(id);
  }

  @Override
  public Optional<Bank> findBankByAccountNumber(String accountNumber) {
    return bankDAO.findBankByAccountNumber(accountNumber);
  }
}
