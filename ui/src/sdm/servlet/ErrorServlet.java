package sdm.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@WebServlet("/ErrorHandler")
public class ErrorServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Exception exception = (Exception) req.getAttribute("javax.servlet.error.exception");
        // Integer statusCode = (Integer)req.getAttribute("javax.servlet.error.status_code");
        // String servletName = (String)req.getAttribute("javax.servlet.error.servlet_name");
        String requestUri = (String) req.getAttribute("javax.servlet.error.request_uri");

        String errorParameter = "errorMessage=" + URLEncoder.encode(exception.getMessage(), "ASCII");
        String redirectedUri = requestUri.contains("?")
                ? "&" + errorParameter
                : "?" + errorParameter;

        resp.sendRedirect(redirectedUri);
    }
}