package by.clevertec.cleverbank.entity;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {

  private long id;
  private String firstName;
  private String lastName;
  private String patronymic;
  private List<Account> accounts;

}
