package by.clevertec.cleverbank.controller;

import by.clevertec.cleverbank.controller.util.UtilServlet;
import by.clevertec.cleverbank.dao.impl.BankDAOImpl;
import by.clevertec.cleverbank.entity.Bank;
import by.clevertec.cleverbank.service.BankService;
import by.clevertec.cleverbank.service.impl.BankServiceImpl;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/banks/delete","/banks/save","/banks/get","/banks/getAll"})
public class BankServlet extends HttpServlet {

  private BankService bankService;

  @Override
  public void init() {
    bankService = new BankServiceImpl(BankDAOImpl.getInstance());
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
        case "/save" -> saveBank(request, response);
        case "/delete" -> deleteBank(request, response);
        case "/update" -> updateBank(request, response);
        case "/get" -> getBank(request, response);
        case "/getAll" -> getAllBanks(request, response);
        default -> UtilServlet.defaultMethod(response);
      }
    } catch (IOException | ServletException e) {
      e.printStackTrace();
    }

  }

  private void saveBank(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    String name = request.getParameter("name");
    bankService.save(Bank.builder().name(name).build());
    response.sendRedirect("banks");
  }

  private void getAllBanks(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    List<Bank> bankList = bankService.findAll();
    request.setAttribute("bankList", bankList);
    RequestDispatcher dispatcher = request.getRequestDispatcher("banks");
    dispatcher.forward(request, response);
  }

  private void getBank(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    String id = request.getParameter("id");
    Optional<Bank> bank = bankService.findById(Long.parseLong(id));
    request.setAttribute("bank", bank.orElse(Bank.builder().build()));
    response.sendRedirect("banks");
  }

  private void updateBank(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    long id = Long.parseLong(request.getParameter("id"));
    String name = request.getParameter("name");
    bankService.update(Bank.builder().id(id).name(name).build());
    response.sendRedirect("banks");
  }

  private void deleteBank(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    long id = Long.parseLong(request.getParameter("id"));
    String name = request.getParameter("name");
    bankService.delete(Bank.builder().id(id).name(name).build());
    response.sendRedirect("banks");
  }

}
