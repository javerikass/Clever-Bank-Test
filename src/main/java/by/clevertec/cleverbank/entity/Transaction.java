package by.clevertec.cleverbank.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Transaction {

  private long id;
  private TypeTransaction type;
  private Account sender;
  private Account recipient;
  private BigDecimal amount;
  private Timestamp timestamp;

}
