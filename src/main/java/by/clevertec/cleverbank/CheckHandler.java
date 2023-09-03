package by.clevertec.cleverbank;

import by.clevertec.cleverbank.entity.Transaction;
import by.clevertec.cleverbank.entity.TypeTransaction;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CheckHandler {

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(
      "yyyy-MM-dd HH:mm:ss");

  public void generateCheck(Transaction transaction) {
    saveCheckToFile(generateCheckContent(transaction), transaction.getTimestamp().getNanos());
  }

  private String generateCheckContent(Transaction transaction) {
    StringBuilder check = new StringBuilder();
    check.append("Банковский чек")
        .append("\n")
        .append("Чек: ").append(transaction.getTimestamp().getNanos())
        .append("\n")
        .append(formatTimestamp(transaction.getTimestamp()))
        .append("\nТип транзакции: ").append(transaction.getType());
    if (transaction.getType().getDescription().equals(TypeTransaction.TRANSFER.getDescription())) {
      check.append("\nБанк отправителя: ")
          .append(transaction.getSender().getBank().getName());
    }
    check.append("\nБанк получателя: ")
        .append(transaction.getRecipient().getBank().getName());
    if (transaction.getType().getDescription().equals(TypeTransaction.TRANSFER.getDescription())) {
      check.append("\nСчёт отправителя: ")
          .append(transaction.getSender().getAccountNumber());
    }
    check.append("\nСчёт получателя: ")
        .append(transaction.getRecipient().getAccountNumber())
        .append("\nСумма: ")
        .append(transaction.getAmount());

    return check.toString();

  }

  private void saveCheckToFile(String checkContent, int number) {
    FileWriter writer = null;
    try {
      File checkFolder = new File("check");
      if (!checkFolder.exists()) {
        checkFolder.mkdir();
      }
      String fileName = "check_" + number + ".txt";
      File checkFile = new File(checkFolder, fileName);
      writer = new FileWriter(checkFile);
      writer.write(checkContent);

      log.info("Чек сохранен в файл: " + checkFile.getAbsolutePath());
    } catch (IOException e) {
      log.error("Ошибка при сохранении чека: " + e.getMessage());
    } finally {
      try {
        if (writer != null) {
          writer.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

  }

  private String formatTimestamp(Timestamp timestamp) {
    return FORMATTER.format(timestamp.toLocalDateTime());
  }
}
