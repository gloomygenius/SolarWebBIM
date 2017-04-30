package edu.spbpu.logic.actions.project;

import edu.spbpu.logic.Action;
import edu.spbpu.logic.services.PowerStationService;
import edu.spbpu.models.Cable;
import edu.spbpu.models.PowerStation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static edu.spbpu.Constants.POWER_STATION_OPERATION;
import static edu.spbpu.Constants.POWER_STATION_SERVICE;

/**
 * Created by Vasiliy Bobkov on 09.04.2017.
 */
public class AnnualInfo implements Action {
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        long stationId = Long.parseLong(request.getParameter("id"));
        PowerStationService service = (PowerStationService) request.getServletContext().getAttribute(POWER_STATION_SERVICE);
        PowerStation.Operation operation = service.getAnnualOperation(stationId);
        Map<String, Double> capexMap = service.calculateCapex(request.getParameterMap());
        request.setAttribute(POWER_STATION_OPERATION, operation);
        request.setAttribute("capex_map",capexMap);
        setStation(request);
        request.setAttribute("cross_sections", Cable.getSections());
        return "/WEB-INF/jsp/projects/annual_info.jsp";
    }
}