package edu.spbpu.logic.actions.project;

import edu.spbpu.dao.InverterDAO;
import edu.spbpu.dao.PowerStationDAO;
import edu.spbpu.logic.Action;
import edu.spbpu.logic.services.InverterService;
import edu.spbpu.logic.services.PowerStationService;
import edu.spbpu.logic.services.SolarModuleService;
import edu.spbpu.models.Inverter;
import edu.spbpu.models.PowerStation;
import edu.spbpu.models.SolarModule;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static edu.spbpu.Constants.*;

/**
 * Created by Vasiliy Bobkov on 30.03.2017.
 */
public class EditProjectGet implements Action {
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        ServletContext context = request.getServletContext();

        PowerStationService powerStationService = (PowerStationService) context.getAttribute(POWER_STATION_SERVICE);
        long id = Long.parseLong(request.getParameter("id"));
        PowerStation station = powerStationService.getById(id)
                .orElseThrow(() -> new RuntimeException("Station with id " + id + " not found"));
        request.setAttribute(POWER_STATION, station);

        InverterService inverterService = (InverterService) context.getAttribute(INVERTER_SERVICE);
        List<Inverter> inverterList = inverterService.getAll();
        request.setAttribute(INVERTER_LIST, inverterList);

        SolarModuleService moduleService = (SolarModuleService) context.getAttribute(SOLAR_MODULE_SERVICE);
        List<SolarModule> moduleList = moduleService.getAll();
        request.setAttribute(MODULE_LIST,moduleList);

        return "/WEB-INF/jsp/projects/edit.jsp";
    }
}