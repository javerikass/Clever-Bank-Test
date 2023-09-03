package by.clevertec.cleverbank.entity;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Bank {

  private long id;
  private String name;
  private List<Account> accounts;

}
