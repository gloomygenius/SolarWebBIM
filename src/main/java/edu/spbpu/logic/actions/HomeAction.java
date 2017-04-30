package edu.spbpu.logic.actions;

import edu.spbpu.logic.Action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Vasiliy Bobkov on 19.01.2017.
 */
public class HomeAction implements Action {
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        return request.getServletPath()+request.getPathInfo();
    }
}