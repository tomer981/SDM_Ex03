package sdm.servlet;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dto.*;
import market.Market;
import sdm.constants.Constants;
import xmlBuild.schema.generated.SDMStore;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
//CHECK_FOR_UPDATES
@WebServlet(name = "UpdatesServlet", urlPatterns = {"/UpdatesServlet"})
public class UpdatesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Market market = Market.getMarketInstance();
        String userName = (String) req.getSession().getAttribute(Constants.USER_NAME);

        try (PrintWriter out = resp.getWriter()) {
            List<AlertDTO> result = market.getLastStoresNotificationsForUser(userName);

            JsonArray jsonAlert = new JsonArray();

            result.forEach(alertDTO -> {
                if (alertDTO.getStoreDTO() != null){
                    JsonArray newStoreAlert = new JsonArray();
                    JsonObject typeJson = new JsonObject();
                    typeJson.addProperty("type","newStoreAlertMsg");

                    SDMStore store = alertDTO.getStoreDTO().getSdmStore();
                    ZoneMarketDTO storeZone = market.getZoneMarketDTO(alertDTO.getStoreDTO().getZoneName());

                    newStoreAlert.add(typeJson);
                    newStoreAlert.add(addNewStoreAlert(store,storeZone));


                    jsonAlert.add(newStoreAlert);
                }
                else if(alertDTO.getFeedbackDTO() !=null){
                    JsonArray newFeedbackAlert = new JsonArray();
                    JsonObject typeJson = new JsonObject();
                    typeJson.addProperty("type","newFeedbackAlertMsg");

                    newFeedbackAlert.add(typeJson);
                    newFeedbackAlert.add(addNewFeedbackAlert(alertDTO.getFeedbackDTO()));

                    jsonAlert.add(newFeedbackAlert);

                }else if(alertDTO.getSubOrderDTO() !=null){
                    JsonArray newSubOrderAlert = new JsonArray();
                    JsonObject typeJson = new JsonObject();
                    typeJson.addProperty("type","newSubOrderAlertMsg");

                    newSubOrderAlert.add(typeJson);
                    newSubOrderAlert.add(addNewSubOrderAlert(alertDTO.getSubOrderDTO()));

                    jsonAlert.add(newSubOrderAlert);
                }
            });

            out.println(jsonAlert);
        }
    }

    private JsonObject addNewSubOrderAlert(SubOrderDTO subOrderDTO) {
        JsonObject newSubOrder = new JsonObject();

        newSubOrder.addProperty("orderId", subOrderDTO.getId());
        newSubOrder.addProperty("customerName", subOrderDTO.getCustomerName());
        newSubOrder.addProperty("productsForSale", subOrderDTO.getKProductIdVProductsSoldInfo().size());
        newSubOrder.addProperty("productsCost", subOrderDTO.getProductsPrice());
        newSubOrder.addProperty("deliveryCost", subOrderDTO.getDeliveryPrice());
        return newSubOrder;
    }

    private JsonObject addNewFeedbackAlert(FeedbackDTO feedbackDTO) {
        JsonObject newFeedbackObject = new JsonObject();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String strDate = dateFormat.format(feedbackDTO.getDate());
        newFeedbackObject.addProperty("customerName", feedbackDTO.getCustomerName());
        newFeedbackObject.addProperty("date", strDate);
        newFeedbackObject.addProperty("rate", feedbackDTO.getRate());
        newFeedbackObject.addProperty("msg", feedbackDTO.getMsg());
        return newFeedbackObject;
    }

    private JsonObject addNewStoreAlert(SDMStore store, ZoneMarketDTO storeZone) {
        JsonObject newStoreObject = new JsonObject();
        newStoreObject.addProperty("id", store.getId());
        newStoreObject.addProperty("name", store.getName());
        newStoreObject.addProperty("coordX", store.getLocation().getX());
        newStoreObject.addProperty("coordY", store.getLocation().getY());
        newStoreObject.addProperty("itemsSold", store.getSDMPrices().getSDMSell().size());
        newStoreObject.addProperty("itemsInZone", storeZone.getProductsInfo().getSDMItem().size());
        return newStoreObject;
    }
}
