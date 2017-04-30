package edu.spbpu.logic.actions.project.local_data;

import edu.spbpu.logic.Action;
import edu.spbpu.logic.services.PowerStationService;
import edu.spbpu.models.LocalData;
import edu.spbpu.models.PowerStation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import static edu.spbpu.Constants.LOCAL_DATA;
import static edu.spbpu.Constants.POWER_STATION_SERVICE;

/**
 * Created by Vasiliy Bobkov on 13.04.2017.
 */
public class LocalDataInfo implements Action {
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        long id = Long.parseLong(request.getParameter("id"));
        String param = Optional.ofNullable(request.getParameter("param"))
                .filter(s -> !s.isEmpty())
                .orElse("");
        PowerStationService service = (PowerStationService) request.getServletContext().getAttribute(POWER_STATION_SERVICE);
        PowerStation station = service.getById(id).orElseThrow(NullPointerException::new);
        LocalData data;
        switch (param) {
            case "direct_radiation":
                data = station.getDirectRadiation();
                break;
            case "diffuse_radiation":
                data = station.getDiffuseRadiation();
                break;
            case "temperature":
                data = station.getAmbientTemperature();
                break;
            case "wind_speed":
                data = station.getWindSpeedData();
                break;
            default:
                data = null;
        }
        if (data != null) {
            Map<Instant, Float> map = new TreeMap<>((t1, t2) -> {
                if (t1.isAfter(t2)) return 1;
                else return -1;
            });
            map.putAll(data.getData());
            data = new LocalData(data.getParameter(),map);
        }
        request.setAttribute(LOCAL_DATA, data);
        setStation(request);
        return "/WEB-INF/jsp/projects/local_data/info.jsp";
    }
}