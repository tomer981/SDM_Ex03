package sdm.servlet;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dto.StoreDTO;
import market.Market;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import static sdm.constants.Constants.*;

@WebServlet(name = "FeedbackServlet", urlPatterns = {"/FeedbackServlet"})
public class FeedbackServlet extends HttpServlet {

    private JsonObject getStoreInfo(StoreDTO store){
        JsonObject json = new JsonObject();
        json.addProperty("value", store.getSdmStore().getId());
        json.addProperty("storeName", store.getSdmStore().getName());
        return json;
    }

    private void processRequestGetFeedbacksInSystem(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        HttpSession session = req.getSession(false);
        Market engine = Market.getMarketInstance();
        String zoneName = (String) session.getAttribute(ZONE_NAME);
        Set<Integer> storesId = (Set<Integer>) session.getAttribute(FEEDBACK_STORES_IDS);
        JsonArray storesInfo = new JsonArray();
        for (Integer storeId : storesId){
            StoreDTO store = engine.getStoreDTO(zoneName,storeId);
            storesInfo.add(getStoreInfo(store));
        }

        try (PrintWriter out = resp.getWriter()) {
            out.println(storesInfo);
            out.flush();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter(ACTION);
        switch (action) {
            case GET_FEEDBACK_IN_SYSTEM:
                processRequestGetFeedbacksInSystem(req, resp);
                break;

        }
    }


}
