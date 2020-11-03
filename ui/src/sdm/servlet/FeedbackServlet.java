package sdm.servlet;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dto.FeedbackDTO;
import dto.StoreDTO;
import market.Market;
import sdm.constants.Constants;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static sdm.constants.Constants.*;

@WebServlet(name = "FeedbackServlet", urlPatterns = {"/FeedbackServlet"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class FeedbackServlet extends HttpServlet {

    private JsonObject getStoreInfo(StoreDTO store) {
        JsonObject json = new JsonObject();
        json.addProperty("value", store.getSdmStore().getId());
        json.addProperty("storeName", store.getSdmStore().getName());
        return json;
    }

    private JsonObject getFeedBackInfo(FeedbackDTO feedback) {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String strDate = dateFormat.format(feedback.getDate());
        JsonObject json = new JsonObject();
        json.addProperty("customerName", feedback.getCustomerName());
        json.addProperty("date", strDate);
        json.addProperty("rating", feedback.getRate());
        json.addProperty("msg", feedback.getMsg());
        return json;
    }


    private void processRequestGetFeedbacksInSystem(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        HttpSession session = req.getSession(false);
        Market engine = Market.getMarketInstance();
        String zoneName = (String) session.getAttribute(ZONE_NAME);
        Date date = (Date) session.getAttribute(FEEDBACK_STORES_DATE);
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String strDate = dateFormat.format(date);
        Set<Integer> storesId = (Set<Integer>) session.getAttribute(FEEDBACK_STORES_IDS);

        JsonArray storesIdsAndDate = new JsonArray();
        JsonArray storesInfo = new JsonArray();

        for (Integer storeId : storesId) {
            StoreDTO store = engine.getStoreDTO(zoneName, storeId);
            storesInfo.add(getStoreInfo(store));
        }
        storesIdsAndDate.add(storesInfo);
        JsonObject jsonDate = new JsonObject();
        jsonDate.addProperty("date", strDate);
        storesIdsAndDate.add(jsonDate);

        session.removeAttribute(FEEDBACK_STORES_DATE);

        try (PrintWriter out = resp.getWriter()) {
            out.println(storesIdsAndDate);
            out.flush();
        }
    }

    private void processRequestAddFeedback(HttpServletRequest req, HttpServletResponse resp) throws UnsupportedEncodingException {
        resp.setContentType("text/html;charset=UTF-8");
        HttpSession session = req.getSession(false);
        String userName = (String) session.getAttribute(USER_NAME);
        String zoneName = (String) session.getAttribute(ZONE_NAME);
        Market engine = Market.getMarketInstance();

        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        Date date = null;
        try {
            date = new SimpleDateFormat("dd-MM-yyyy").parse(req.getParameter("date"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String[] pairs = req.getParameter("data").split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        FeedbackDTO feedbackDTO = new FeedbackDTO(userName, date, Integer.valueOf(query_pairs.get("rating")), query_pairs.get("feedbackMsg"));

        Integer storeId = Integer.valueOf(query_pairs.get("store-selector"));
        engine.addFeedbackDTO(zoneName, storeId, feedbackDTO);

    }

    private void processRequestGetFeedbacksInZoneToManager(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        HttpSession session = req.getSession(false);
        String userName = (String) session.getAttribute(Constants.USER_NAME);
        String zoneName = (String) session.getAttribute(ZONE_NAME);

        Market engine = Market.getMarketInstance();
        List<FeedbackDTO> feedbacks = engine.getManagerFeedbacksInZone(zoneName, userName);
        if (feedbacks != null) {
            JsonArray jsonFeedbacks = new JsonArray();
            for (FeedbackDTO feedback : feedbacks) {
                jsonFeedbacks.add(getFeedBackInfo(feedback));
            }

            try (PrintWriter out = resp.getWriter()) {
                out.println(jsonFeedbacks);
                out.flush();
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter(ACTION);
        switch (action) {
            case GET_FEEDBACKS_IN_ZONE_TO_MANAGER_ACTION:
                processRequestGetFeedbacksInZoneToManager(req, resp);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter(ACTION);
        switch (action) {
            case GET_FEEDBACK_IN_SYSTEM_ACTION:
                processRequestGetFeedbacksInSystem(req, resp);
                break;
            case ADD_FEEDBACK_ACTION:
                processRequestAddFeedback(req, resp);
                break;
        }
    }


}
