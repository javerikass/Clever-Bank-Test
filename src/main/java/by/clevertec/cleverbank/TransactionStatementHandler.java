package by.clevertec.cleverbank;

import by.clevertec.cleverbank.dao.impl.AccountDAOImpl;
import by.clevertec.cleverbank.dao.impl.TransactionDAOImpl;
import by.clevertec.cleverbank.entity.Account;
import by.clevertec.cleverbank.entity.Transaction;
import by.clevertec.cleverbank.entity.TypeTransaction;
import by.clevertec.cleverbank.entity.User;
import by.clevertec.cleverbank.service.AccountService;
import by.clevertec.cleverbank.service.TransactionService;
import by.clevertec.cleverbank.service.impl.AccountServiceImpl;
import by.clevertec.cleverbank.service.impl.TransactionServiceImpl;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

@Slf4j
public class TransactionStatementHandler {

  private static TransactionStatementHandler instance;

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(
      "yyyy.MM.dd HH:mm:ss");

  private TransactionService transactionService;
  private CheckHandler checkHandler;
  private final AccountService accountService;

  private TransactionStatementHandler() {
    this.checkHandler = new CheckHandler();
    this.transactionService = new TransactionServiceImpl(
        TransactionDAOImpl.getInstance(), checkHandler);
    this.accountService = new AccountServiceImpl(
        AccountDAOImpl.getInstance(),
        transactionService);
  }

  public static TransactionStatementHandler getInstance() {
    if (instance == null) {
      instance = new TransactionStatementHandler();
    }
    return instance;
  }

  public void generateStatementForUser(User user, Timestamp from, Timestamp to, String format) {
    List<Transaction> transactions = transactionService.getTransactionsByPeriod(user, from, to);
    List<Account> userAccounts = accountService.getAccountsByUserId(user.getId());
    String statement = formatStatement(userAccounts, transactions, from, to);
    if (format.equalsIgnoreCase("pdf")) {
       generatePDFStatement(statement, "transactions");
    } else if (format.equalsIgnoreCase("txt")) {
      generateTXTStatement(statement);
    } else {
      log.info("Invalid format specified");
    }
  }


  private String formatStatement(List<Account> accounts, List<Transaction> transactions,
      Timestamp from,
      Timestamp to) {
    StringBuilder statementBuilder = new StringBuilder();
    for (Account account : accounts) {
      statementBuilder.append("                        Money statement")
          .append("\n                            ")
          .append(account.getBank().getName())
          .append("\n")
          .append("Клиент                      | ")
          .append(account.getUser().getFirstName())
          .append(" ")
          .append(account.getUser().getLastName())
          .append(" ")
          .append(account.getUser().getPatronymic())
          .append("\n")
          .append("Счёт                        | ")
          .append(account.getAccountNumber())
          .append("\n")
          .append("Валюта                      | ")
          .append(account.getCurrency())
          .append("\n")
          .append("Дата открытия               | ")
          .append(account.getOpenDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")))
          .append("\n")
          .append("Период                      | ")
          .append(DateTimeFormatter.ofPattern("yyyy.MM.dd").format(from.toLocalDateTime()))
          .append(" - ")
          .append(DateTimeFormatter.ofPattern("yyyy.MM.dd").format(from.toLocalDateTime()))
          .append("\n")
          .append("Дата и время формирования   | ")
          .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd, HH:mm")))
          .append("\n")
          .append("Остаток                     | ")
          .append(account.getBalance())
          .append("\n\n")
          .append("Дата       |        Примечание        |   Сумма\n")
          .append("------------------------------------------------------\n");

      for (Transaction transaction : transactions) {
        if (account.getId() == transaction.getRecipient().getId()
            || account.getId() == transaction.getSender().getId()) {
          statementBuilder.append(
                  DateTimeFormatter.ofPattern("yyyy.MM.dd").format(from.toLocalDateTime()))
              .append(" | ")
              .append(transaction.getType().getDescription());
          if (transaction.getType().getDescription().equals("Deposit")) {
            statementBuilder.append("                  | ");
          } else {
            statementBuilder.append("               | -");
          }
          statementBuilder.append(transaction.getAmount())
              .append(" ")
              .append(account.getCurrency())
              .append("\n");
        }
      }
    }
    return statementBuilder.toString();
  }

  public void generateExpensesAndIncomeStatement(String accountNumber, Timestamp from,
      Timestamp to) {
    Account account = accountService.getAccountByAccountNumber(
        accountNumber).orElse(Account.builder().build());
    BigDecimal spentAmount = transactionService.calculateAmountFundsMovement(accountNumber,
        from, to, TypeTransaction.WITHDRAWAL);
    BigDecimal receivedAmount = transactionService.calculateAmountFundsMovement(accountNumber,
        from, to, TypeTransaction.DEPOSIT);
    String statement = formatExpensesAndIncomeStatement(account, spentAmount, receivedAmount, from,
        to);
    generatePDFStatement(statement, "statement-money");
  }

  public String formatExpensesAndIncomeStatement(Account account, BigDecimal spentAmount,
      BigDecimal receivedAmount, Timestamp from, Timestamp to) {
    StringBuilder statementBuilder = new StringBuilder();
    statementBuilder.append("                                           Money statement")
        .append("\n\n")
        .append("Клиент                                                                |  ")
        .append(account.getUser().getFirstName())
        .append(" ")
        .append(account.getUser().getLastName())
        .append(" ")
        .append(account.getUser().getPatronymic())
        .append("\n")
        .append("Счёт                                                                     |  ")
        .append(account.getAccountNumber())
        .append("\n")
        .append("Валюта                                                                |  ")
        .append(account.getCurrency())
        .append("\n")
        .append("Дата открытия                                                 |  ")
        .append(account.getOpenDate())
        .append("\n")
        .append("Период                                                                |  ")
        .append(FORMATTER.format(from.toLocalDateTime()))
        .append(" - ")
        .append(FORMATTER.format(to.toLocalDateTime()))
        .append("\n")
        .append("Дата и время формирования                       |  ")
        .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
        .append("\n")
        .append("Остаток                                                               |  ")
        .append(account.getBalance())
        .append("\n")
        .append("     Приход                |                Уход")
        .append("\n")
        .append("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -")
        .append("\n")
        .append(receivedAmount)
        .append(" ")
        .append(account.getCurrency())
        .append("          |         ")
        .append(spentAmount)
        .append(" ")
        .append(account.getCurrency());

    return statementBuilder.toString();
  }

  private void generatePDFStatement(String statement, String folder) {
    try (PDDocument document = new PDDocument()) {
      PDRectangle pageRectangle = PDRectangle.A4;
      int lineHeight = 20;
      int maxLinesPerPage = 40;
      int lineAtPageIndex = 0;

      ClassLoader classLoader = getClass().getClassLoader();
      PDType0Font font = PDType0Font.load(document,
          classLoader.getResourceAsStream("Roboto-Light.ttf"));

      String[] lines = statement.split("\n");

      int lineIndex = 0;
      while (lineIndex < lines.length) {
        PDPage page = new PDPage(pageRectangle);
        document.addPage(page);

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
          contentStream.setFont(font, 14);
          contentStream.beginText();
          contentStream.newLineAtOffset(50, 700);

          boolean isNewPage = false;

          while (lineIndex < lines.length) {
            String line = lines[lineIndex];
            if (lineIndex > 0 && line.equals("                        Money statement")) {
              isNewPage = true;
              lineAtPageIndex = 0;
              lineIndex++;
              break;
            }
            contentStream.showText(line);
            contentStream.newLineAtOffset(0, -lineHeight);
            lineIndex++;
            lineAtPageIndex++;
            if (maxLinesPerPage == lineAtPageIndex) {
              isNewPage = true;
              lineAtPageIndex = 0;
              break;
            }
          }
          if (isNewPage) {
            contentStream.endText();
            continue;
          }
          contentStream.endText();
        }
      }
      String fileName = "statement_" + System.currentTimeMillis() + ".pdf";
      String filePath = Path.of(createFolder(folder).toString(), fileName).toString();
      document.save(filePath);
      log.info("PDF statement generated successfully.");

    } catch (IOException e) {
      log.error("An error occurred while generating the PDF statement: " + e.getMessage());
    }
  }

  private void generateTXTStatement(String statement) {
    FileWriter writer = null;
    try {
      String fileName = "transaction_" + System.currentTimeMillis() + ".txt";
      File checkFile = new File(createFolder("transactions"), fileName);
      writer = new FileWriter(checkFile);
      writer.write(statement);
    } catch (IOException e) {
      log.error("An error occurred while generating the TXT statement: " + e.getMessage());
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

  private File createFolder(String folder) {
    File transactionsFolder = new File(folder);
    if (!transactionsFolder.exists()) {
      transactionsFolder.mkdir();
    }
    return transactionsFolder;
  }
}
