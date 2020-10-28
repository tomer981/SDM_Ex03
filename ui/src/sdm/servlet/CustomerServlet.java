package sdm.servlet;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dto.*;
import market.Market;
import xml.schema.generated.Location;
import xml.schema.generated.SDMDiscount;
import xml.schema.generated.SDMItem;
import xml.schema.generated.SDMOffer;

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
import java.util.*;

import static sdm.constants.Constants.*;

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


    private void processRequestGetDiscounts(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        Market engine = Market.getMarketInstance();
        String zoneName = (String) session.getAttribute(ZONE_NAME);
        ZoneMarketDTO zoneMarket = engine.getZoneMarketDTO(zoneName);
        OrderDTO order = (OrderDTO) session.getAttribute(CUSTOMER_ORDER);

        JsonArray arrayDiscounts = new JsonArray();
        Set<Integer> storesId = order.getKStoreVSubOrder().keySet();
        for (Integer storeId : storesId) {
            StoreDTO store = engine.getStoreDTO(zoneName, storeId);
            SubOrderDTO subOrder = order.getKStoreVSubOrder().get(storeId);
            if (store.getSdmStore().getSDMDiscounts() != null){
                List<SDMDiscount> discounts = store.getSdmStore().getSDMDiscounts().getSDMDiscount();
                for (SDMDiscount discount : discounts) {
                    Integer needToBuyProductId = discount.getIfYouBuy().getItemId();

                    if (subOrder.getKProductIdVProductsSoldInfo().containsKey(needToBuyProductId)) {
                        SDMItem product = subOrder.getKProductIdVProductsSoldInfo().get(needToBuyProductId);
                        if (subOrder.getKProductVForPriceAndAmountInfo().containsKey(product)){
                            ProductDTO productDTO = subOrder.getKProductVForPriceAndAmountInfo().get(product);
                            Double amountLeft = productDTO.getAmount() - productDTO.getAmountUsInDiscounts();
                            Integer needToBuyProductAmount = discount.getIfYouBuy().getQuantity();
                            if (amountLeft - needToBuyProductAmount >= needToBuyProductAmount ){
                                JsonArray arrayDiscount = new JsonArray();
                                JsonObject buyDiscount = new JsonObject();
                                JsonArray getDiscount = new JsonArray();
                                JsonObject typeDiscount = new JsonObject();

                                buyDiscount.addProperty("nameDiscount",discount.getName());
                                buyDiscount.addProperty("buyProductId",needToBuyProductId);
                                buyDiscount.addProperty("productName",product.getName());
                                buyDiscount.addProperty("needToBuyProductAmount",needToBuyProductAmount);

                                arrayDiscount.add(buyDiscount);
                                typeDiscount.addProperty("typeDiscount",discount.getThenYouGet().getOperator());
                                arrayDiscount.add(typeDiscount);

                                for (SDMOffer offer :discount.getThenYouGet().getSDMOffer()){
                                    JsonObject getDiscountProduct = new JsonObject();
                                    SDMItem productGet = zoneMarket.getProductsInfo().getSDMItem().stream().filter(item-> item.getId() == offer.getItemId()).findFirst().orElse(null);
                                    getDiscountProduct.addProperty("productId",offer.getItemId());
                                    getDiscountProduct.addProperty("productName",productGet.getName());
                                    getDiscountProduct.addProperty("getAmount",offer.getQuantity());
                                    getDiscountProduct.addProperty("additionalPrice",offer.getForAdditional());
                                    getDiscount.add(getDiscountProduct);
                                }
                                arrayDiscount.add(getDiscount);
                                arrayDiscounts.add(arrayDiscount);
                            }
                        }
                    }
                }
            }
        }
        PrintWriter out = resp.getWriter();
        out.println(arrayDiscounts);
        out.flush();

//        {//arrayDiscounts
//            {//arrayDiscount
//                {//buyDiscount (first Table)
//                    "nameDiscount" : "half-Price",
//                        "buyProductId" : 1,
//                        "productName" : "banana",
//                        "needToBuyProductAmount" : 10
//                }
//                {//typeDiscount (headLine)
//                    "typeDiscount":"one of"
//                }
//                {//getDiscount (click On row First Get this Table)
//                    {//getDiscountProduct
//                        "productId": 1,
//                            "productName": "banana",
//                            "getAmount" : 5,
//                            "additionalPrice" : 10
//                    }
//                    //.
//                    //.
//                    //.
//                }
//                //.
//                //.
//                //.
//            }
//            //.
//            //.
//            //.
//        }

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

    private OrderDTO getOrderData(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        String userName = (String) session.getAttribute(USER_NAME);
        String jsonObject = req.getParameter("jsonData");
        JsonObject jsonData = new JsonParser().parse(jsonObject).getAsJsonObject();

        OrderDTO orderDTO = null;
        try {
            Integer cordX = jsonData.get("cordX").getAsInt();
            Integer cordY = jsonData.get("cordY").getAsInt();
            Location location = new Location();
            location.setX(cordX);
            location.setY(cordY);
            String stringDate = jsonData.get("date").getAsString();
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(stringDate);
            orderDTO = new OrderDTO(-1, date, location, userName);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return orderDTO;
    }

    private void processRequestAddDynamicProducts(HttpServletRequest req, HttpServletResponse resp) {
        HttpSession session = req.getSession(false);
        Market engine = Market.getMarketInstance();

        String jsonObject = req.getParameter("jsonData");
        JsonObject jsonData = new JsonParser().parse(jsonObject).getAsJsonObject();

        String zoneName = (String) session.getAttribute(ZONE_NAME);

        ZoneMarketDTO zoneMarket = engine.getZoneMarketDTO(zoneName);
        Map<SDMItem, ProductDTO> KProductInfoVProductDTO = new HashMap<>();
        List<SDMItem> productsInfo = zoneMarket.getProductsInfo().getSDMItem();

        for (SDMItem productInfo : productsInfo) {
            String productId = String.valueOf(productInfo.getId());
            Double productAmount = jsonData.get(productId).getAsDouble();
            if (productAmount > 0) {
                ProductDTO product = new ProductDTO();
                product.setAmount(productAmount);
                KProductInfoVProductDTO.put(productInfo, product);
            }
        }

        OrderDTO order = getOrderData(req);

        order = engine.getMinOrder(zoneName, order, KProductInfoVProductDTO);
        session.setAttribute(CUSTOMER_ORDER, order);
    }

    private void processRequestAddStoreProducts(HttpServletRequest req, HttpServletResponse resp) {

    }//TODO: complete this after Dynamic

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
            case ADD_NEW_ORDER_STATIC_PRODUCTS_ACTION:
                processRequestAddStoreProducts(req, resp);
                break;
            case ADD_NEW_ORDER_DYNAMIC_PRODUCTS_ACTION:
                processRequestAddDynamicProducts(req, resp);
                break;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter(ACTION);
        switch (action) {
            case GET_STORE_INFO_ACTION:
                processRequestGetStoreInfo(req, resp);
                break;
            case GET_DISCOUNTS_ACTION:
                processRequestGetDiscounts(req, resp);
                break;

        }
    }


}
