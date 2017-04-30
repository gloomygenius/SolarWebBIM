package edu.spbpu.logic.actions.project.string;

import edu.spbpu.logic.Action;
import edu.spbpu.logic.services.PowerStationService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static edu.spbpu.Constants.POWER_STATION_SERVICE;

/**
 * Created by Vasiliy Bobkov on 08.04.2017.
 */
public class CableEditPOST implements Action {
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        PowerStationService service = (PowerStationService) request.getServletContext().getAttribute(POWER_STATION_SERVICE);
        service.setCableLength(request.getParameterMap());
        setStation(request);
        return "/WEB-INF/jsp/projects/string/info.jsp";
    }
}