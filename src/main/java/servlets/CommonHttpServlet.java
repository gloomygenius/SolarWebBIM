package servlets;

import lombok.extern.log4j.Log4j;

import javax.servlet.http.HttpServlet;

@Log4j
public abstract class CommonHttpServlet extends HttpServlet {
    public static String CURRENT_USER = "currentUser";
    public static final String INCLUDED_PAGE = "includedPage";
    public static final String ERROR_MSG = "errorMsg";
    static final String SUCCESS_MSG = "successMsg";

    @Override
    public void init() {
    }
}