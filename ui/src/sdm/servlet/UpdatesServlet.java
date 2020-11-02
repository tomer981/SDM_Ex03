package sdm.servlet;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import market.Market;
import sdm.constants.Constants;
import xmlBuild.schema.generated.SDMStore;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "UpdatesServlet", urlPatterns = {"/UpdatesServlet"})
public class UpdatesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Market market = Market.getMarketInstance();
        String zoneName = (String) req.getSession().getAttribute(Constants.ZONE_NAME);
        Integer lastStoreUpdateId = (Integer) req.getSession().getAttribute(Constants.LAST_STORE_UPDATE_ID);
        if (lastStoreUpdateId == null) {
            lastStoreUpdateId = -1;
        }

        try (PrintWriter out = resp.getWriter()) {
            List<SDMStore> result = market.getStoresAddedSince(lastStoreUpdateId, zoneName);
            req.getSession().setAttribute(Constants.LAST_STORE_UPDATE_ID, result.get(result.size() - 1).getId());

            JsonArray json = new JsonArray();

            result.forEach(store -> {
                JsonObject newStoreObject = new JsonObject();
                newStoreObject.addProperty("id", store.getId());
                newStoreObject.addProperty("name", store.getName());
                json.add(newStoreObject);
            });

            out.println(json);
        }
    }
}
