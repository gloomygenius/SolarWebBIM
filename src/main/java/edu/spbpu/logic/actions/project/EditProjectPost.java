package edu.spbpu.logic.actions.project;

import edu.spbpu.logic.Action;
import edu.spbpu.logic.services.InverterService;
import edu.spbpu.logic.services.PowerStationService;
import edu.spbpu.logic.services.SolarModuleService;
import edu.spbpu.models.PowerStation;
import lombok.SneakyThrows;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static edu.spbpu.Constants.*;

public class EditProjectPost implements Action {
    @Override
    @SneakyThrows
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        PowerStationService powerStationService = (PowerStationService) request.getServletContext().getAttribute(POWER_STATION_SERVICE);
        long id = Long.parseLong(request.getParameter("id"));
        PowerStation.PowerStationBuilder builder = powerStationService.getBuilderById(id);
        Optional.ofNullable(request.getParameter("name"))
                .filter(s -> !s.isEmpty())
                .ifPresent(builder::name);

        InverterService inverterService = (InverterService) request.getServletContext().getAttribute(INVERTER_SERVICE);
        Optional.ofNullable(request.getParameter("inverter_id"))
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .map(inverterService::getById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .ifPresent(builder::inverter);

        SolarModuleService moduleService = (SolarModuleService) request.getServletContext().getAttribute(SOLAR_MODULE_SERVICE);
        Optional.ofNullable(request.getParameter("module_id"))
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .map(moduleService::getById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .ifPresent(builder::solarModule);
        builder.cachedOperations(null);
        builder.maxOperation(null);
        PowerStation station = builder.build();
        powerStationService.update(station);
        response.sendRedirect(request.getHeader("referer"));
        return null;
    }
}