package edu.spbpu.logic.actions.project.economics;

import edu.spbpu.logic.Action;
import edu.spbpu.logic.services.PowerStationService;
import edu.spbpu.models.PowerStation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static edu.spbpu.Constants.POWER_STATION_SERVICE;

/**
 * Created by Vasiliy Bobkov on 18.04.2017.
 */
public class CapexInfo implements Action {

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        long id = Long.parseLong(request.getParameter("id"));
        PowerStationService service = (PowerStationService) request.getServletContext().getAttribute(POWER_STATION_SERVICE);
//        service.calculateCableCapex(request.getParameterMap());
        return null;
    }
}
