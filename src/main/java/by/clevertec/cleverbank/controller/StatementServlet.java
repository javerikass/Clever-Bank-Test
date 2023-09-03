package by.clevertec.cleverbank.controller;

import by.clevertec.cleverbank.TransactionStatementHandler;
import by.clevertec.cleverbank.controller.util.UtilServlet;
import by.clevertec.cleverbank.entity.User;
import java.io.IOException;
import java.sql.Timestamp;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/statement/transactions", "/statement/expensesAndIncome"})
public class StatementServlet extends HttpServlet {

  private TransactionStatementHandler generator;

  @Override
  public void init() {
    generator = TransactionStatementHandler.getInstance();
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
        case "/transactions" -> getTransactionStatements(request, response);
        case "/expensesAndIncome" -> getExpensesAndIncomeStatements(request, response);
        default -> UtilServlet.defaultMethod(response);
      }
    } catch (IOException | ServletException ex) {
      ex.printStackTrace();
    }
  }

  private void getTransactionStatements(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    String startDate = request.getParameter("start_date");
    User user = User.builder().id(Long.parseLong(request.getParameter("user_id"))).build();
    String endDate = request.getParameter("end_date");
    String format = request.getParameter("format");
    generator.generateStatementForUser(user, Timestamp.valueOf(startDate),
        Timestamp.valueOf(endDate), format);
    RequestDispatcher dispatcher = request.getRequestDispatcher("statement");
    dispatcher.forward(request, response);
  }

  private void getExpensesAndIncomeStatements(HttpServletRequest request,
      HttpServletResponse response)
      throws IOException, ServletException {
    String startDate = request.getParameter("start_date");
    String accountNumber = request.getParameter("account_number");
    String endDate = request.getParameter("end_date");
    generator.generateExpensesAndIncomeStatement(accountNumber, Timestamp.valueOf(startDate),
        Timestamp.valueOf(endDate));

    RequestDispatcher dispatcher = request.getRequestDispatcher("statement");
    dispatcher.forward(request, response);
  }


}
