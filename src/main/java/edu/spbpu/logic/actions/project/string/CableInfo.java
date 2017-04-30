package edu.spbpu.logic.actions.project.string;

import edu.spbpu.logic.Action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Vasiliy Bobkov on 12.04.2017.
 */
public class CableInfo implements Action {
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        setStation(request);
        return "/WEB-INF/jsp/projects/string/info.jsp";
    }
}