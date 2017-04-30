package edu.spbpu.logic.actions.project.module;

import edu.spbpu.logic.Action;
import edu.spbpu.logic.services.PowerStationService;
import edu.spbpu.models.PowerStation;
import edu.spbpu.models.SolarModule;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.ZonedDateTime;
import java.util.SortedMap;
import java.util.TreeMap;

import static edu.spbpu.Constants.POWER_STATION;
import static edu.spbpu.Constants.POWER_STATION_SERVICE;

public class OperationsInfo implements Action {
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        PowerStationService service = (PowerStationService) request.getServletContext().getAttribute(POWER_STATION_SERVICE);
        long id = Long.parseLong(request.getParameter("id"));
        PowerStation station = service.getById(id).orElseThrow(RuntimeException::new);
        if (station.getCachedOperations() == null) station.calculateMaxOperation();
        SortedMap<ZonedDateTime, SolarModule.Operation> sortedMap = new TreeMap<ZonedDateTime, SolarModule.Operation>((t1, t2) -> {
            if (t1.isBefore(t2)) return -1;
            else return 1;
        });
        sortedMap.putAll(station.getCachedOperations());
        request.setAttribute("operation_map", sortedMap);
        request.setAttribute(POWER_STATION, station);
        return "/WEB-INF/jsp/projects/module/operations.jsp";
    }
}