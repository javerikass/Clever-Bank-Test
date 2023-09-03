package by.clevertec.cleverbank.dao;

import by.clevertec.cleverbank.entity.Account;
import by.clevertec.cleverbank.entity.Transaction;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountDAO extends BaseDAO<Account> {

  Optional<Account> getAccountByAccountNumber(String accountNumber);

  List<Account> getAccountsWithBalance();

  List<Account> getAccountsByUserId(long userId);

  List<Account> getAccountsByBankId(long bankId);

  Transaction transferToExternalBank(String fromAccount, String toAccount,
      BigDecimal amount);

}
