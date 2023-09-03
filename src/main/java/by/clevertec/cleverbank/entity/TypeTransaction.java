package by.clevertec.cleverbank.entity;

public enum TypeTransaction {

  TRANSFER("Transfer"),
  DEPOSIT("Deposit"),
  WITHDRAWAL("Withdrawal");

  private final String description;

  TypeTransaction(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

}
