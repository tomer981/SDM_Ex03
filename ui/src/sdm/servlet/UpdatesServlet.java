package sdm.servlet;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "UpdatesServlet", urlPatterns = {"/UpdatesServlet", "/updates/StoresServlet"})
public class UpdatesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Thread.sleep(5000);

            resp.getWriter().write("{'text': 'hello'");
        } catch (InterruptedException e) {
            throw new RuntimeException("Who woke me up");
        }
    }
}
