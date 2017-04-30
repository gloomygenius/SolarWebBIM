package edu.spbpu.logic.actions.project;

import edu.spbpu.logic.Action;
import edu.spbpu.logic.services.PowerStationService;
import lombok.SneakyThrows;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static edu.spbpu.Constants.POWER_STATION_SERVICE;

/**
 * Created by Vasiliy Bobkov on 29.03.2017.
 */
public class NewProjectPost implements Action {
    @Override
    @SneakyThrows
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        PowerStationService service = (PowerStationService) request.getServletContext().getAttribute(POWER_STATION_SERVICE);
        service.createStation(getSingleParameterMap(request));

        response.sendRedirect("/projects/list");
        return null;
    }
}