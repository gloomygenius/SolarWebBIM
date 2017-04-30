package edu.spbpu.logic.actions.inverters;

import edu.spbpu.logic.Action;
import edu.spbpu.logic.services.InverterService;
import edu.spbpu.models.Inverter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static edu.spbpu.Constants.INVERTER_SERVICE;

/**
 * Created by Vasiliy Bobkov on 08.04.2017.
 */
public class InverterInfo implements Action {
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        InverterService service = (InverterService) request.getServletContext().getAttribute(INVERTER_SERVICE);

        long id = Long.parseLong(request.getParameter("id"));
        Inverter inverter = service.getById(id).orElseThrow(RuntimeException::new);

        request.setAttribute("inverter", inverter);
        return "/WEB-INF/jsp/inverters/info.jsp";
    }
}
