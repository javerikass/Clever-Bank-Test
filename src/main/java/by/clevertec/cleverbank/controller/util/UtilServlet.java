package by.clevertec.cleverbank.controller.util;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

public class UtilServlet {

  public static void defaultMethod(HttpServletResponse response) throws IOException {
    response.sendRedirect("error");
  }
}
