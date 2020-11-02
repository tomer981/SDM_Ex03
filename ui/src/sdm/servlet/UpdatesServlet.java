package sdm.servlet;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dto.StoreDTO;
import dto.ZoneMarketDTO;
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
        String userName = (String) req.getSession().getAttribute(Constants.USER_NAME);

        try (PrintWriter out = resp.getWriter()) {
            List<StoreDTO> result = market.getLastStoresNotificationsForUser(userName);

            JsonArray json = new JsonArray();

            result.forEach(storeDTO -> {
                SDMStore store = storeDTO.getSdmStore();
                ZoneMarketDTO storeZone = market.getZoneMarketDTO(storeDTO.getZoneName());

                JsonObject newStoreObject = new JsonObject();
                newStoreObject.addProperty("id", store.getId());
                newStoreObject.addProperty("name", store.getName());
                newStoreObject.addProperty("coordX", store.getLocation().getX());
                newStoreObject.addProperty("coordY", store.getLocation().getY());
                newStoreObject.addProperty("itemsSold", store.getSDMPrices().getSDMSell().size());
                newStoreObject.addProperty("itemsInZone", storeZone.getProductsInfo().getSDMItem().size());

                json.add(newStoreObject);
            });

            out.println(json);
        }
    }
}
