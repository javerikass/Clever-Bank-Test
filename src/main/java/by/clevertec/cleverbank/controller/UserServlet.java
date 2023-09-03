package by.clevertec.cleverbank.controller;

import by.clevertec.cleverbank.controller.util.UtilServlet;
import by.clevertec.cleverbank.dao.impl.UserDAOImpl;
import by.clevertec.cleverbank.entity.User;
import by.clevertec.cleverbank.service.UserService;
import by.clevertec.cleverbank.service.impl.UserServiceImpl;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/users/save", "/users/delete", "/users/update", "/users/get",
    "/users/getAll"})
public class UserServlet extends HttpServlet {

  private UserService userService;

  @Override
  public void init() {
    userService = new UserServiceImpl(UserDAOImpl.getInstance());
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
        case "/save" -> saveUser(request, response);
        case "/delete" -> deleteUser(request, response);
        case "/update" -> updateUser(request, response);
        case "/get" -> getUser(request, response);
        case "/getAll" -> getAllUser(request, response);
        default -> UtilServlet.defaultMethod(response);
      }
    } catch (IOException | ServletException ex) {
      ex.printStackTrace();
    }
  }

  private void saveUser(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    String firstName = request.getParameter("firstName");
    String lastName = request.getParameter("lastName");
    String patronymic = request.getParameter("patronymic");
    userService.save(
        User.builder().firstName(firstName).lastName(lastName).patronymic(patronymic).build());
    response.sendRedirect("users");
  }

  private void getAllUser(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    List<User> listUser = userService.findAll();
    request.setAttribute("listUser", listUser);
    RequestDispatcher dispatcher = request.getRequestDispatcher("users");
    dispatcher.forward(request, response);
  }

  private void getUser(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    String id = request.getParameter("id");
    Optional<User> user = userService.findById(Long.parseLong(id));
    request.setAttribute("user", user.orElse(User.builder().build()));
    response.sendRedirect("users");
  }

  private void updateUser(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    long id = Long.parseLong(request.getParameter("id"));
    String firstName = request.getParameter("firstName");
    String lastName = request.getParameter("lastName");
    String patronymic = request.getParameter("patronymic");
    User user = User.builder().id(id).firstName(firstName).lastName(lastName)
        .patronymic(patronymic).build();
    userService.update(user);
    response.sendRedirect("users");
  }

  private void deleteUser(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    long id = Long.parseLong(request.getParameter("id"));
    String firstName = request.getParameter("firstName");
    String lastName = request.getParameter("lastName");
    String patronymic = request.getParameter("patronymic");
    User user = User.builder().id(id).firstName(firstName).lastName(lastName)
        .patronymic(patronymic).build();
    userService.delete(user);
    response.sendRedirect("list");
  }

}

