package sdm.servlet;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dto.ManagerDTO;
import dto.ProductDTO;
import dto.StoreDTO;
import dto.ZoneMarketDTO;
import market.Market;
import xmlBuild.schema.generated.SDMItem;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import static sdm.constants.Constants.*;

@WebServlet(name = "StoresServlet", urlPatterns = {"/StoresServlet", "/pages/storesInfo/StoresServlet"})
public class StoresServlet extends HttpServlet {
    private final String NEW_ORDER_URL = "customer-actions-page/new-order/new-order.html";
    private final String CUSTOMER_SHOW_ORDER_URL = "customer-actions-page/show-orders/show-orders.html";
    private final String MANAGER_SHOW_ORDER_URL = "manager-actions-page/show-orders/show-orders.html";
    private final String SHOW_FEEDBACKS_URL = "manager-actions-page/show-feedbacks/show-feedbacks.html";
    private final String NEW_STORE_URL = "manager-actions-page/add-store/add-store.html";

    private void processRequestGetProductsInZone(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        String zoneName = request.getParameter(ZONE_NAME);
        Market engine = Market.getMarketInstance();
        ZoneMarketDTO zoneMarket = engine.getZoneMarketDTO(zoneName);

        JsonArray array = new JsonArray();
        try (PrintWriter out = response.getWriter()) {
            for (SDMItem sdmProduct : zoneMarket.getProductsInfo().getSDMItem()) {
                JsonObject json = new JsonObject();
                json.addProperty("productId", sdmProduct.getId());
                json.addProperty("productName", sdmProduct.getName());
                json.addProperty("productPurchaseCategory", sdmProduct.getPurchaseCategory());
                json.addProperty("numberOfStoresSellProduct", zoneMarket.getKProductVNumberOfStoreSellProduct().get(sdmProduct));
                json.addProperty("AveragePriceOfProduct", zoneMarket.getKProductVAvgPriceOfProduct().get(sdmProduct));
                json.addProperty("amountSellProduct", zoneMarket.getKProductVTotalAmountSold().get(sdmProduct));
                array.add(json);
            }
            out.println(array);
            out.flush();
        }
    }

    private void processRequestGetStoreInfo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        String zoneName = request.getParameter("zoneName");
        Market engine = Market.getMarketInstance();
        ZoneMarketDTO zoneMarket = engine.getZoneMarketDTO(zoneName);

        try (PrintWriter out = response.getWriter()) {
            JsonArray array = new JsonArray();
            for (ManagerDTO manager : zoneMarket.getKManagerNameVManager().values()) {
                for (StoreDTO store : manager.getKStoreIdVStore().get(zoneName)) {
                    JsonObject json = new JsonObject();
                    json.addProperty("storeId", store.getSdmStore().getId());
                    json.addProperty("storeName", store.getSdmStore().getName());
                    json.addProperty("ownerName", store.getStoreOwnerName());
                    json.addProperty("location", store.getSdmStore().getLocation().getX() + "," + store.getSdmStore().getLocation().getY());
                    json.addProperty("numberOfOrders", store.getKIdOrderVSubOrderDTO().size());
                    json.addProperty("productsEarning", store.getMoneyEarnFromProducts());
                    json.addProperty("ppk", store.getSdmStore().getDeliveryPpk());
                    json.addProperty("deliveryEarning", store.getMoneyEarnFromDelivery());
                    array.add(json);
                }
            }
            out.println(array);
            out.flush();
        }

    }

    private void processRequestGetStoreProductsInfo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        String zoneName = request.getParameter("zoneName");
        String storeId = request.getParameter("storeId");
        Market engine = Market.getMarketInstance();
        StoreDTO store = engine.getStoreDTO(zoneName, Integer.parseInt(storeId));

        try (PrintWriter out = response.getWriter()) {
            JsonArray array = new JsonArray();
            for (Map.Entry<SDMItem, ProductDTO> product : store.getKProductIdVPriceAndAmount().entrySet()) {
                JsonObject json = new JsonObject();
                json.addProperty("productId", product.getKey().getId());
                json.addProperty("productName", product.getKey().getName());
                json.addProperty("purchaseCategoryName", product.getKey().getPurchaseCategory());
                json.addProperty("productCost", product.getValue().getPrice());
                json.addProperty("numberOfSold", product.getValue().getAmount());
                array.add(json);
            }
            out.println(array);
            out.flush();
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter(ACTION);
        String zoneName = req.getParameter(ZONE_NAME);
        HttpSession session = req.getSession(false);
        session.setAttribute(ZONE_NAME, zoneName);
        switch (action){
            case NEW_ORDER_ACTION:
                resp.sendRedirect(NEW_ORDER_URL);
                break;
            case CUSTOMER_SHOW_ORDER_ACTION:
                resp.sendRedirect(CUSTOMER_SHOW_ORDER_URL);
                break;
            case MANAGER_SHOW_ORDER_ACTION:
                resp.sendRedirect(MANAGER_SHOW_ORDER_URL);
                break;
            case SHOW_FEEDBACKS_ACTION:
                resp.sendRedirect(SHOW_FEEDBACKS_URL);
                break;
            case NEW_STORE_ACTION:
                resp.sendRedirect(NEW_STORE_URL);
                break;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter(ACTION);
        switch (action) {
            case GET_PRODUCTS_IN_ZONE_ACTION:
                processRequestGetProductsInZone(req, resp);
                break;
            case GET_STORE_INFO_ACTION:
                processRequestGetStoreInfo(req, resp);
                break;
            case GET_PRODUCTS_IN_STORE_ACTION:
                processRequestGetStoreProductsInfo(req, resp);
                break;
        }

    }
}
