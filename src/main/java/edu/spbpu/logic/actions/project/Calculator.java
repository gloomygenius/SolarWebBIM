package edu.spbpu.logic.actions.project;

import edu.spbpu.logic.Action;
import edu.spbpu.logic.services.PowerStationService;
import edu.spbpu.models.PowerStation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static edu.spbpu.Constants.ERROR_MSG;
import static edu.spbpu.Constants.POWER_STATION_SERVICE;

/**
 * Created by Vasiliy Bobkov on 07.04.2017.
 */
public class Calculator implements Action {
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        Long id = Long.valueOf(request.getParameter("id"));
        String parameter = request.getParameter("parameter");
        PowerStationService powerStationService = (PowerStationService) request.getServletContext().getAttribute(POWER_STATION_SERVICE);
        PowerStation station = powerStationService.getById(id).orElseThrow(RuntimeException::new);
        switch (parameter) {
            case "opt_betta":
                powerStationService.optimizeBettaAndSave(station);
                break;
            case "max_strings":

                try {
                    powerStationService.pickUpStings(station);
                } catch (Exception e) {
                    request.setAttribute(ERROR_MSG, "Inverter and solar panel are incompatible with current");
                    setStation(request);
                    return "/WEB-INF/jsp/projects/info.jsp";
                }
        }
        sendToReferer(request, response);
        return null;
    }
}