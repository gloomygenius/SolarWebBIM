package edu.spbpu.logic.actions.modules;

import edu.spbpu.logic.Action;
import edu.spbpu.logic.services.SolarModuleService;
import edu.spbpu.models.SolarModule;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static edu.spbpu.Constants.SOLAR_MODULE_SERVICE;

/**
 * Created by Vasiliy Bobkov on 08.04.2017.
 */
public class ModuleInfo implements Action {
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        SolarModuleService service = (SolarModuleService) request.getServletContext().getAttribute(SOLAR_MODULE_SERVICE);
        long id = Long.parseLong(request.getParameter("id"));
        SolarModule module = service.getById(id).orElseThrow(RuntimeException::new);
        request.setAttribute("module", module);
        return "/WEB-INF/jsp/modules/info.jsp";
    }
}