package edu.spbpu.logic;

import edu.spbpu.logic.services.PowerStationService;
import edu.spbpu.models.PowerStation;
import lombok.SneakyThrows;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.stream.Collectors;

import static edu.spbpu.Constants.POWER_STATION;
import static edu.spbpu.Constants.POWER_STATION_SERVICE;

@FunctionalInterface
public interface Action {
    String execute(HttpServletRequest request, HttpServletResponse response);

    @SneakyThrows
    default void sendToReferer(HttpServletRequest request, HttpServletResponse response) {
        response.sendRedirect(request.getHeader("referer"));
    }

    default void setStation(HttpServletRequest request) {
        long id = Long.parseLong(request.getParameter("id"));
        PowerStationService service = (PowerStationService) request.getServletContext().getAttribute(POWER_STATION_SERVICE);
        PowerStation station = service.getById(id).orElseThrow(RuntimeException::new);
        request.setAttribute(POWER_STATION, station);
    }

    default Map<String, String> getSingleParameterMap(HttpServletRequest request) {
        return request.getParameterMap()
                .entrySet()
                .parallelStream()
                .collect(Collectors.toMap(Map.Entry::getKey, s -> s.getValue()[0]));
    }
}