package sdm.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.*;

import market.Market;
import sdm.constants.*;

import static sdm.constants.Constants.*;

@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet", "/pages/signup/LoginServlet"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class LoginServlet extends HttpServlet {
    private final String ZONES_URL = "../zones/zones.html";
    private final String USER_EXIST_URL = "/pages/signup/signup.html";

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

        Collection<Part> parts = req.getParts();
        String userName = req.getParameter(USER_NAME);
        String position = req.getParameter(USER_POSITION);
        Market engine = Market.getMarketInstance();

        try {
            synchronized (this) {
                if (req.getSession(false) == null) {
                    if (!engine.isUserExist(userName) && userName != null) {
                        if (position.equals("manager")) {
                            addManager(userName, parts);
                        } else {
                            engine.addCustomer(userName);
                        }

                        HttpSession session = req.getSession(true);
                        session.setAttribute(USER_NAME, userName);
                        session.setAttribute(USER_POSITION, position);
                        resp.getWriter().write(ZONES_URL);
                    } else {
                        throw new IllegalStateException("User name already exist.");
                    }
                }
            }
        } catch (IllegalStateException e) {
            String msg = e.getMessage();
            if (e.getMessage().indexOf(':') != -1) {
                msg = msg.substring(msg.indexOf(':') + 2);
            }
            throw new IllegalStateException(msg, e);
        }
    }

    private void addManager(String userName, Collection<Part> parts) {
        Market engine = Market.getMarketInstance();
        for (Part part : parts) {
            if (part.getName().equals("uploadfiles")) {
                try {
                    InputStream inputStream = part.getInputStream();
                    List<String> fileContent = new ArrayList<>();
                    fileContent.add(new Scanner(inputStream).useDelimiter("\\Z").next());
                    Path tempFile = Files.createTempFile("SDM-", ".xml");

                    Files.write(tempFile, fileContent, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                    engine.addManager(userName, tempFile.toFile());
                } catch (IOException e) {
                    throw new IllegalStateException(e.getMessage(), e);
                }
            }
        }
    }

    private void isCustomerUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html;charset=UTF-8");

        HttpSession session = req.getSession(false);
        if (session.getAttribute(USER_POSITION).equals("customer")) {
            resp.getWriter().write(String.valueOf(true));
        } else {
            resp.getWriter().write(String.valueOf(false));
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter(Constants.ACTION);
        switch (action) {
            case IS_CUSTOMER_USER_ACTION:
                isCustomerUser(req, resp);
                break;
            case IS_SESSION_EXIST_ACTION:
                processRequestIfSessionExist(req, resp);
                break;

        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequestCreateSession(req, resp);
    }
}


