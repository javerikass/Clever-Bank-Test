package by.clevertec.cleverbank.controller;

import by.clevertec.cleverbank.CheckHandler;
import by.clevertec.cleverbank.controller.util.UtilServlet;
import by.clevertec.cleverbank.dao.impl.AccountDAOImpl;
import by.clevertec.cleverbank.dao.impl.TransactionDAOImpl;
import by.clevertec.cleverbank.entity.Account;
import by.clevertec.cleverbank.entity.Bank;
import by.clevertec.cleverbank.entity.User;
import by.clevertec.cleverbank.service.AccountService;
import by.clevertec.cleverbank.service.TransactionService;
import by.clevertec.cleverbank.service.impl.AccountServiceImpl;
import by.clevertec.cleverbank.service.impl.TransactionServiceImpl;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/accounts/save", "/accounts/delete", "/accounts/update",
    "/accounts/get", "/accounts/getAll"})
public class AccountServlet extends HttpServlet {

  private AccountService accountService;
  private CheckHandler checkHandler = new CheckHandler();
  private final TransactionService transactionService = new TransactionServiceImpl(TransactionDAOImpl.getInstance(),checkHandler);

  @Override
  public void init() {
    accountService = new AccountServiceImpl(
        AccountDAOImpl.getInstance(), transactionService);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) {
    doGet(request, response);
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) {
    String action = request.getServletPath();

    try {
      switch (action) {
        case "/save" -> saveAccount(request, response);
        case "/delete" -> deleteAccount(request, response);
        case "/update" -> updateAccount(request, response);
        case "/get" -> getAccount(request, response);
        case "/getAll" -> getAllAccounts(request, response);
        default -> UtilServlet.defaultMethod(response);
      }
    } catch (IOException | ServletException ex) {
      ex.printStackTrace();
    }
  }

  private void saveAccount(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    String accountNumber = request.getParameter("accountNumber");
    String currency = request.getParameter("currency");
    String openDateStr = request.getParameter("openDate");
    LocalDate openDate = LocalDate.parse(openDateStr);
    long bankId = Long.parseLong(request.getParameter("bankId"));
    long userId = Long.parseLong(request.getParameter("userId"));

    Account account = Account.builder()
        .accountNumber(accountNumber)
        .currency(currency)
        .openDate(openDate)
        .bank(Bank.builder().id(bankId).build())
        .user(User.builder().id(userId).build())
        .balance(BigDecimal.ZERO)
        .build();

    accountService.save(account);
    response.sendRedirect("accounts");
  }

  private void getAllAccounts(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    List<Account> accountList = accountService.findAll();
    request.setAttribute("accountList", accountList);
    RequestDispatcher dispatcher = request.getRequestDispatcher("accounts");
    dispatcher.forward(request, response);
  }

  private void getAccount(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    String id = request.getParameter("id");
    Optional<Account> account = accountService.findById(Long.parseLong(id));
    request.setAttribute("account", account.orElse(Account.builder().build()));
    response.sendRedirect("accounts");
  }

  private void updateAccount(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    long id = Long.parseLong(request.getParameter("id"));
    String accountNumber = request.getParameter("accountNumber");
    String currency = request.getParameter("currency");
    String openDateStr = request.getParameter("openDate");
    String balance = request.getParameter("balance");
    LocalDate openDate = LocalDate.parse(openDateStr);
    long bankId = Long.parseLong(request.getParameter("bankId"));
    long userId = Long.parseLong(request.getParameter("userId"));

    Account account = Account.builder()
        .id(id)
        .accountNumber(accountNumber)
        .currency(currency)
        .openDate(openDate)
        .bank(Bank.builder().id(bankId).build())
        .user(User.builder().id(userId).build())
        .balance(BigDecimal.valueOf(Long.parseLong(balance)))
        .build();

    accountService.update(account);
    response.sendRedirect("accounts");
  }

  private void deleteAccount(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    long id = Long.parseLong(request.getParameter("id"));
    String accountNumber = request.getParameter("accountNumber");
    String currency = request.getParameter("currency");
    String openDateStr = request.getParameter("openDate");
    LocalDate openDate = LocalDate.parse(openDateStr);
    long bankId = Long.parseLong(request.getParameter("bankId"));
    long userId = Long.parseLong(request.getParameter("userId"));

    Account account = Account.builder().id(id)
        .accountNumber(accountNumber)
        .currency(currency)
        .openDate(openDate)
        .bank(Bank.builder().id(bankId).build())
        .user(User.builder().id(userId).build())
        .build();

    accountService.delete(account);
    response.sendRedirect("accounts");
  }

}
