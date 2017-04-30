package edu.spbpu.filters;


import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class SetCharFilter extends HttpFilter {

    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // чтение кодировки из запроса
        String encoding = request.getCharacterEncoding();

        // установка UTF-8, если не установлена
        if (!"UTF-8".equals(encoding))
            request.setCharacterEncoding("UTF-8");
        chain.doFilter(request, response);
    }
}