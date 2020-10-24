package sdm.servlet;

import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

@WebServlet("/ErrorHandler")
public class ErrorServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Exception exception = (Exception) req.getAttribute("javax.servlet.error.exception");


        resp.setStatus(500);

        // TODO: Send JSON of the error
        JsonObject json = new JsonObject();
        json.addProperty("error",exception.getMessage());
        resp.getWriter().write(exception.getMessage());
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doGet(req, resp);
    }
}