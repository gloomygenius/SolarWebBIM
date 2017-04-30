package edu.spbpu.filters;

import lombok.extern.log4j.Log4j;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Log4j
//@WebFilter(urlPatterns = {"/*"})
public class SecurityFilter extends HttpFilter {
    private static final String CURRENT_USER = "currentUser";
    private Pattern notAuthPattern = Pattern.compile("^((\\/static\\/.*)|(\\/j_security_check$)|(^\\/not_auth\\/.*))");

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String path = Optional.ofNullable(request.getRequestURI()).orElse("");
        Matcher newMatcher = notAuthPattern.matcher(path);
        if (!newMatcher.find()) {
            //если запрос в авторизованную зону, то проверяем авторизацию
            HttpSession session = request.getSession(true);
            if (session.getAttribute(CURRENT_USER) != null)
                chain.doFilter(request, response);
            else {
                request.getRequestDispatcher("/WEB-INF/jsp/not_auth/login.jsp").forward(request, response);
            }
        } else chain.doFilter(request, response);
    }
}