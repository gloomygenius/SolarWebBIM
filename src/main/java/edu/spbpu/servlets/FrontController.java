package edu.spbpu.servlets;

import edu.spbpu.logic.Action;
import edu.spbpu.logic.ActionFactory;
import lombok.SneakyThrows;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {
        "/data/*",
        "/projects/*",
        "/modules/*",
        "/inverters/*"})
public class FrontController extends HttpServlet {
    private ActionFactory actionFactory;

    @Override
    @SneakyThrows
    public void init(ServletConfig config) {
        super.init(config);
        actionFactory = ActionFactory.INSTANCE;
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request,
                                HttpServletResponse response)
            throws ServletException, IOException {
        Action action = actionFactory.getAction(request);

        String view = action.execute(request, response);
        if (view != null)
            getServletContext().getRequestDispatcher(view).forward(request, response);
    }
}