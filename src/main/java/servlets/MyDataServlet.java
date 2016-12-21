package servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/data/my")
public class MyDataServlet extends CommonHttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute(INCLUDED_PAGE, "data/my");
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}