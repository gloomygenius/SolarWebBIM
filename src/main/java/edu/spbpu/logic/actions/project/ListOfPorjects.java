package edu.spbpu.logic.actions.project;

import edu.spbpu.dao.PowerStationDAO;
import edu.spbpu.logic.services.PowerStationService;
import edu.spbpu.models.PowerStation;
import edu.spbpu.logic.Action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static edu.spbpu.Constants.POWER_STATION_DAO;
import static edu.spbpu.Constants.POWER_STATION_SERVICE;
import static edu.spbpu.Constants.PROJECT_LIST;

/**
 * Created by Vasiliy Bobkov on 29.03.2017.
 */
public class ListOfPorjects implements Action {
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        PowerStationService service = (PowerStationService) request.getServletContext().getAttribute(POWER_STATION_SERVICE);
        List<PowerStation> stationList = service.getAll();
        request.setAttribute(PROJECT_LIST, stationList);
        return "/WEB-INF/jsp/projects/list.jsp";
    }
}