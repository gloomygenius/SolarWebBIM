package edu.spbpu.logic.actions.modules;

import edu.spbpu.dao.SolarModuleDAO;
import edu.spbpu.logic.Action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static edu.spbpu.Constants.SOLAR_MODULE_DAO;

/**
 * Created by Vasiliy Bobkov on 29.03.2017.
 */
public class ModuleList implements Action {
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        SolarModuleDAO solarModuleDAO = (SolarModuleDAO) request.getServletContext().getAttribute(SOLAR_MODULE_DAO);
        request.setAttribute("module_list",solarModuleDAO.getAll());
        return "/WEB-INF/jsp/modules/list.jsp";
    }
}