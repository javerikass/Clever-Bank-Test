package by.clevertec.cleverbank.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Account {

  private long id;
  private String accountNumber;
  private String currency;
  private LocalDate openDate;
  private Bank bank;
  private User user;
  private List<Transaction> transactions;
  private BigDecimal balance;

}
