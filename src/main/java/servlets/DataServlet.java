package servlets;

import models.DataSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/data/get")
public class DataServlet extends CommonHttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String dataSetName = request.getParameter("dataSetName");
        if (dataSetName != null) {
            HashMap<String, DataSet> dataSetMap = (HashMap<String, DataSet>) getServletContext().getAttribute("dataSetMap");
            request.setAttribute("dataSet", dataSetMap.get(dataSetName));
            request.setAttribute(INCLUDED_PAGE, "data/get");
        } else request.setAttribute(INCLUDED_PAGE, "data/list_of_data_set");
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}