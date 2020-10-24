package sdm.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dto.TransactionDTO;
import dto.ZoneMarketDTO;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static sdm.constants.Constants.*;


@WebServlet(name = "ZonesServlet", urlPatterns = {"/ZonesServlet"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class ZonesServlet extends HttpServlet {

    private void processRequestGetUsersPositions(HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            Market engine = Market.getMarketInstance();
            JsonArray array = new JsonArray();

            Map<String, String> KNameVPosition = engine.getNamePosition();
            for (Map.Entry<String, String> entry : KNameVPosition.entrySet()) {
                JsonObject json = new JsonObject();
                json.addProperty("userName", entry.getKey());
                json.addProperty("position", entry.getValue());
                array.add(json);
            }
            out.println(array);
            out.flush();
        }
    }

    private void processRequestGetUserTransactions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        HttpSession session = request.getSession(false);
        String userName = String.valueOf(session.getAttribute(Constants.USER_NAME));

        Market engine = Market.getMarketInstance();
        List<TransactionDTO> transactions = engine.getUserTransactionsDTO(userName);

        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            String json = gson.toJson(transactions);
            out.println(json);
            out.flush();
        }

    }

    private void processRequestGetZoneInfo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        Market engine = Market.getMarketInstance();
        List<ZoneMarketDTO> zoneMarkets = engine.getZonesMarketDTO();
        JsonArray array = new JsonArray();

        try (PrintWriter out = response.getWriter()) {
            for (ZoneMarketDTO zoneMarketDTO : zoneMarkets){
                JsonObject json = new JsonObject();
                json.addProperty("zoneName",zoneMarketDTO.getZoneName());
                json.addProperty("zoneOwner",zoneMarketDTO.getManagerDefineName().getName());
                json.addProperty("numberOfProductsInZone",zoneMarketDTO.getProductsInfo().getSDMItem().size());
                json.addProperty("numberOfStoresInZone",zoneMarketDTO.getNumberOfStores());
                json.addProperty("numberOfOrders",zoneMarketDTO.getNumberOfOrders());
                json.addProperty("averagePricePerOrderOfProducts",zoneMarketDTO.getAvgProductsPriceOrders());
                array.add(json);
            }
            out.println(array);
            out.flush();
        }
    }

    private void processRequestGetStoresUrl(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
//        String url = req.getParameter("url");
//        String zoneName = req.getParameter("zoneName");
//        String newQuery = url + ;
        //other solution :
        // https://stackoverflow.com/questions/26177749/how-can-i-append-a-query-parameter-to-an-existing-url

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        String userName = String.valueOf(session.getAttribute(Constants.USER_NAME));
        try{
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(req.getParameter("date"));
            Double depositAmount = Double.parseDouble(req.getParameter("depositAmount"));
            Market engine = Market.getMarketInstance();
            engine.customerMakeDeposit(userName,date,depositAmount);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter(ACTION);
        switch (action) {
            case GET_USERS_POSITION_ACTION:
                processRequestGetUsersPositions(resp);
                break;
            case GET_USER_TRANSACTIONS_ACTION:
                processRequestGetUserTransactions(req, resp);
                break;
            case GET_ZONE_INFO_ACTION:
                processRequestGetZoneInfo(req, resp);
                break;
            case GET_STORE_URL_ACTION:
                processRequestGetStoresUrl(req, resp);
                break;
        }
    }
}
