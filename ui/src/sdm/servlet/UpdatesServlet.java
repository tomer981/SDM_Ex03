package sdm.servlet;


import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "UpdatesServlet", urlPatterns = {"/UpdatesServlet"})
public class UpdatesServlet extends HttpServlet {
    int stopAfter = 3;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (PrintWriter out = resp.getWriter()) {

            // TODO: Insert real update code here
            System.out.println("Sleep started");
            Thread.sleep(500);
            System.out.println("Sleep ended");

            JsonObject json = new JsonObject();
            json.addProperty("text", "hello");

            if (stopAfter > 0) {
                stopAfter--;
            } else {
                json.addProperty("stopChecking", true);
            }

            out.println(json);
        } catch (InterruptedException e) {
            throw new RuntimeException("Who woke me up");
        }
    }
}
