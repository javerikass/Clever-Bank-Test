package by.clevertec.cleverbank.service;

import by.clevertec.cleverbank.entity.Account;
import by.clevertec.cleverbank.entity.Transaction;
import by.clevertec.cleverbank.entity.TypeTransaction;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountService {

  long save(Account account);

  boolean update(Account account);

  boolean delete(Account account);

  Optional<Account> findById(long id);

  List<Account> findAll();

  List<Account> getAccountsWithBalance();

  Optional<Account> getAccountByAccountNumber(String accountNumber);

  Transaction depositOrWithdrawFunds(BigDecimal amount, String account,
      TypeTransaction typeTransaction);

  Transaction transferWithinBank(String fromAccount, String toAccount, BigDecimal amount);

  Transaction transferToExternalBank(String fromAccount, String toAccount, BigDecimal amount);

  List<Account> getAccountsByUserId(long userId);

  List<Account> getAccountsByBankId(long bankId);
}
