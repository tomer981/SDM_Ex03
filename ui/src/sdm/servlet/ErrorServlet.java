package sdm.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/ErrorHandler")
public class ErrorServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Exception exception = (Exception)req.getAttribute("javax.servlet.error.exception");
        // Integer statusCode = (Integer)req.getAttribute("javax.servlet.error.status_code");
        // String servletName = (String)req.getAttribute("javax.servlet.error.servlet_name");
        String requestUri = (String)req.getAttribute("javax.servlet.error.request_uri");

        // TODO: Redirect anywhere you want with the exception in the previous variables
        resp.setHeader("Error", exception.getMessage());
        resp.sendRedirect(requestUri);
    }
}