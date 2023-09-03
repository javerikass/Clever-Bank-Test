package by.clevertec.cleverbank.dao;

import by.clevertec.cleverbank.entity.Bank;
import java.util.Optional;

public interface BankDAO extends BaseDAO<Bank> {

  Optional<Bank> findBankByAccountNumber(String accountNumber);

}
