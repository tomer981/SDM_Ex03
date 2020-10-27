package sdm.servlet;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dto.ManagerDTO;
import dto.ProductDTO;
import dto.StoreDTO;
import dto.ZoneMarketDTO;
import market.Market;
import xml.schema.generated.SDMItem;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import static sdm.constants.Constants.*;
import static sdm.constants.Constants.GET_PRODUCTS_IN_STORE;

@WebServlet(name = "CustomerServlet", urlPatterns = {"/CustomerServlet"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class CustomerServlet extends HttpServlet {

    private void processRequestGetStoreInfo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        HttpSession session = request.getSession(false);
        String zoneName = (String) session.getAttribute(ZONE_NAME);
        Market engine = Market.getMarketInstance();
        JsonArray array = new JsonArray();

        try (PrintWriter out = response.getWriter()) {
            for (StoreDTO store : engine.getStoresDTO(zoneName)) {
                JsonObject json = new JsonObject();
                json.addProperty("value", store.getSdmStore().getId());
                json.addProperty("storeName", store.getSdmStore().getName());
                array.add(json);
            }
            out.println(array);
            out.flush();
        }
    }

    private void processRequestGetProductsInStore(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        HttpSession session = req.getSession(false);
        String zoneName = (String) session.getAttribute(ZONE_NAME);
        String storeId = req.getParameter("store-selector");
        Market engine = Market.getMarketInstance();
        StoreDTO store = engine.getStoreDTO(zoneName, Integer.parseInt(storeId));

        try (PrintWriter out = resp.getWriter()) {
            JsonArray array = new JsonArray();
            for (Map.Entry<SDMItem, ProductDTO> product : store.getKProductIdVPriceAndAmount().entrySet()) {
                JsonObject json = new JsonObject();
                json.addProperty("productId", product.getKey().getId());
                json.addProperty("productName", product.getKey().getName());
                json.addProperty("purchaseCategoryName", product.getKey().getPurchaseCategory());
                json.addProperty("productCost", product.getValue().getPrice());
                json.addProperty("amount", 0);
                array.add(json);
            }
            out.println(array);
            out.flush();
        }


    }

    private void processRequestGetProductsInZone(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        HttpSession session = req.getSession(false);
        String zoneName = (String) session.getAttribute(ZONE_NAME);
        Market engine = Market.getMarketInstance();
        ZoneMarketDTO zoneMarket = engine.getZoneMarketDTO(zoneName);

        JsonArray array = new JsonArray();
        try (PrintWriter out = resp.getWriter()) {
            for (SDMItem sdmProduct : zoneMarket.getProductsInfo().getSDMItem()) {
                JsonObject json = new JsonObject();
                json.addProperty("productId", sdmProduct.getId());
                json.addProperty("productName", sdmProduct.getName());
                json.addProperty("productPurchaseCategory", sdmProduct.getPurchaseCategory());
                json.addProperty("amount", 0);
                array.add(json);
            }
            out.println(array);
            out.flush();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter(ACTION);
        switch (action) {
            case GET_STORE_PRODUCTS_ACTION:
                processRequestGetProductsInStore(req, resp);
                break;
            case GET_ZONE_PRODUCTS_ACTION:
                processRequestGetProductsInZone(req, resp);
                break;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter(ACTION);
        switch (action) {
            case GET_STORE_INFO:
                processRequestGetStoreInfo(req, resp);
                break;
//            case GET_PRODUCTS_IN_STORE:
//                processRequestGetStoreProductsInfo(req, resp);
//                break;
        }
    }


}
