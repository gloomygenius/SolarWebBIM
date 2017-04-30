package edu.spbpu.logic.actions.data;

import lombok.SneakyThrows;
import edu.spbpu.models.old_data_accessors.DataSeries;
import edu.spbpu.logic.Action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by Vasiliy Bobkov on 20.01.2017.
 */

public class GetFileAction implements Action {
    @SneakyThrows
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = null;

        out = response.getWriter();

        HttpSession session = request.getSession();
        ArrayList<DataSeries> list = (ArrayList<DataSeries>) session.getAttribute("arrayOfDataSet");
        DataSeries dataSeries = list.get(Integer.parseInt(request.getParameter("index")));
        String text = dataSeries.convertToCSV();
        response.addHeader("Content-Length", String.valueOf(text.length()));
        response.addHeader("Content-Disposition", "attachment;filename="
                + dataSeries.getParameter()
                + "_lat_" + dataSeries.getLatitude()
                + "_lon_" + dataSeries.getLongitude()
                + ".csv");
        response.setContentType("text/csv");
        out.write(text);
        out.flush();
        out.close();
        return null;
    }
}
