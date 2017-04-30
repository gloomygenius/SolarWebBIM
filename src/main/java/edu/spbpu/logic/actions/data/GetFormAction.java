package edu.spbpu.logic.actions.data;

import edu.spbpu.models.old_data_accessors.DataSet;
import edu.spbpu.logic.Action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

/**
 * Created by Vasiliy Bobkov on 20.01.2017.
 */
public class GetFormAction implements Action {
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        String dataSetName = request.getParameter("dataSetName");
        if (dataSetName != null) {

            HashMap<String, DataSet> dataSetMap = (HashMap<String, DataSet>) request
                    .getServletContext()
                    .getAttribute("dataSetMap");
            request.setAttribute("dataSet", dataSetMap.get(dataSetName));
            return "/WEB-INF/jsp/data/get.jsp";
        } else return "/WEB-INF/jsp/data/list_of_data_set.jsp";
    }
}