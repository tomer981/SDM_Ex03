package servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "LogicServlet", urlPatterns = "/LogicServlet" )
public class LoginServlet extends HttpServlet {
    private final String CHAT_ROOM_URL = "../chatroom/chatroom.html";//TODO: next Page Display
    private final String SIGN_UP_URL = "../signup/signup.html";//TODO: in error msg

    private void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }
}


