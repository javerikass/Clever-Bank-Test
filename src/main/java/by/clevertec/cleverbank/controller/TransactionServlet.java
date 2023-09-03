package by.clevertec.cleverbank.controller;

import by.clevertec.cleverbank.CheckHandler;
import by.clevertec.cleverbank.controller.util.UtilServlet;
import by.clevertec.cleverbank.dao.impl.TransactionDAOImpl;
import by.clevertec.cleverbank.entity.Account;
import by.clevertec.cleverbank.entity.Transaction;
import by.clevertec.cleverbank.entity.TypeTransaction;
import by.clevertec.cleverbank.service.TransactionService;
import by.clevertec.cleverbank.service.impl.TransactionServiceImpl;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/transactions/save","/transactions/delete","/transactions/update","/transactions/get","/transactions/getAll"})
public class TransactionServlet extends HttpServlet {

  private TransactionService transactionService;
  private CheckHandler checkHandler;

  @Override
  public void init() {
    checkHandler = new CheckHandler();
    transactionService = new TransactionServiceImpl(TransactionDAOImpl.getInstance(),checkHandler);
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
        case "/delete" -> deleteTransaction(request, response);
        case "/update" -> updateTransaction(request, response);
        case "/get" -> getTransaction(request, response);
        case "/getAll" -> getAllTransactions(request, response);
        default -> UtilServlet.defaultMethod(response);
      }
    } catch (IOException | ServletException ex) {
      ex.printStackTrace();
    }
  }

  private void getAllTransactions(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    List<Transaction> transactionList = transactionService.findAll();
    request.setAttribute("transactionList", transactionList);
    RequestDispatcher dispatcher = request.getRequestDispatcher("transactions");
    dispatcher.forward(request, response);
  }

  private void getTransaction(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    String id = request.getParameter("id");
    Optional<Transaction> transaction = transactionService.findById(Long.parseLong(id));
    request.setAttribute("transaction", transaction.orElse(Transaction.builder().build()));
    response.sendRedirect("transactions");
  }

  private void updateTransaction(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    long id = Long.parseLong(request.getParameter("id"));
    TypeTransaction type = TypeTransaction.valueOf(request.getParameter("type"));
    Account sender = Account.builder().id(Long.parseLong(request.getParameter("senderId"))).build();
    Account recipient = Account.builder().id(Long.parseLong(request.getParameter("recipientId")))
        .build();
    BigDecimal amount = new BigDecimal(request.getParameter("amount"));
    String timestamp = request.getParameter("timestamp");

    Transaction transaction = Transaction.builder()
        .id(id)
        .type(type)
        .sender(sender)
        .recipient(recipient)
        .amount(amount)
        .timestamp(Timestamp.valueOf(timestamp))
        .build();

    transactionService.update(transaction);
    response.sendRedirect("transactions?id=" + id);
  }

  private void deleteTransaction(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    long id = Long.parseLong(request.getParameter("id"));
    transactionService.delete(Transaction.builder()
        .id(id)
        .build());
    response.sendRedirect("transactions");
  }

}
