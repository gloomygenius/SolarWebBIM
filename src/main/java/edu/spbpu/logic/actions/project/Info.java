package edu.spbpu.logic.actions.project;

import edu.spbpu.logic.Action;
import edu.spbpu.logic.services.PowerStationService;
import edu.spbpu.models.PowerStation;
import lombok.extern.log4j.Log4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static edu.spbpu.Constants.POWER_STATION;
import static edu.spbpu.Constants.POWER_STATION_SERVICE;

@Log4j
public class Info implements Action {

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        PowerStationService service = (PowerStationService) request.getServletContext().getAttribute(POWER_STATION_SERVICE);
        long id = Long.parseLong(request.getParameter("id"));
        PowerStation station = service.getById(id).orElseThrow(RuntimeException::new);
        request.setAttribute(POWER_STATION, station);
        log.debug("Station included!");
        return "/WEB-INF/jsp/projects/info.jsp";
    }
}
