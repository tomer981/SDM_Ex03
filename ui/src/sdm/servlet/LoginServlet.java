package sdm.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import market.Market;
import sdm.utils.*;
import sdm.constants.*;

@WebServlet(name = "LogicServlet", urlPatterns = {"/LogicServlet","/pages/signup/LogicServlet"})
public class LoginServlet extends HttpServlet {
    private final String ZONES_URL = "../zones/Zones.html";

    private void processRequestIfSessionExist(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        String userNotFoundPage = req.getParameter("userNotFoundPage");
        String userFoundPage = req.getParameter("userFoundPage");

        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.getWriter().write(userNotFoundPage);
        } else {
            resp.getWriter().write(userFoundPage);
        }
    }
    
    private void processRequestCreateSession(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");

        String userName = req.getParameter(Constants.USER_NAME);
        String position = req.getParameter(Constants.USER_POSITION);
        Market engine = Market.getMarketInstance();

        synchronized (this){
            if(SessionUtils.getUsername(req) == null){
                if (!engine.isUserExist(userName)) {
                    HttpSession session = req.getSession(true);
                    session.setAttribute(Constants.USER_NAME, userName);
                    session.setAttribute(Constants.USER_POSITION,position);
                    engine.addCustomer(userName);
                    resp.sendRedirect(ZONES_URL);
                }
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequestCreateSession(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequestIfSessionExist(req, resp);
    }
}


