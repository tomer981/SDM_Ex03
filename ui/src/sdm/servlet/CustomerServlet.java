package sdm.servlet;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dto.*;
import market.Market;
import xmlBuild.schema.generated.*;

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

    private JsonObject getStoreInfo(StoreDTO store){
        JsonObject json = new JsonObject();
        json.addProperty("value", store.getSdmStore().getId());
        json.addProperty("storeName", store.getSdmStore().getName());
        return json;
    }


    private void processRequestGetStoreInfo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        HttpSession session = request.getSession(false);
        String zoneName = (String) session.getAttribute(ZONE_NAME);
        Market engine = Market.getMarketInstance();
        JsonArray array = new JsonArray();

        try (PrintWriter out = response.getWriter()) {
            for (StoreDTO store : engine.getStoresDTO(zoneName)) {
                array.add(getStoreInfo(store));
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
        Set<Integer> storesId = order.getKStoreIdVSubOrder().keySet();
        for (Integer storeId : storesId) {
            StoreDTO store = engine.getStoreDTO(zoneName, storeId);
            SubOrderDTO subOrder = order.getKStoreIdVSubOrder().get(storeId);
            if (store.getSdmStore().getSDMDiscounts() != null) {
                List<SDMDiscount> discounts = store.getSdmStore().getSDMDiscounts().getSDMDiscount();
                for (SDMDiscount discount : discounts) {
                    Integer needToBuyProductId = discount.getIfYouBuy().getItemId();

                    if (subOrder.getKProductIdVProductsSoldInfo().containsKey(needToBuyProductId)) {
                        SDMItem product = subOrder.getKProductIdVProductsSoldInfo().get(needToBuyProductId);
                        if (subOrder.getKProductVForPriceAndAmountInfo().containsKey(product)) {
                            ProductDTO productDTO = subOrder.getKProductVForPriceAndAmountInfo().get(product);
                            Double amountLeft = productDTO.getAmount() - productDTO.getAmountUsInDiscounts();
                            Integer needToBuyProductAmount = discount.getIfYouBuy().getQuantity();
                            if (amountLeft >= needToBuyProductAmount) {
                                JsonArray arrayDiscount = new JsonArray();//[buyDiscount,nameVGetDiscountInfo]
                                JsonObject buyDiscount = new JsonObject();// {} V
                                JsonObject nameVGetDiscountInfo = new JsonObject();//{discount.getName() : getDiscountInfo}
                                JsonArray getDiscountInfo = new JsonArray();// [typeDiscount,getDiscountProducts] V
                                JsonObject typeDiscount = new JsonObject();//{"typeDiscount" : op} V
                                JsonArray getDiscountProducts = new JsonArray();//[getDiscountProduct1,getDiscountProduct2,...] V

                                buyDiscount.addProperty("discountName", discount.getName());
                                buyDiscount.addProperty("buyProductId", needToBuyProductId);
                                buyDiscount.addProperty("productName", product.getName());
                                buyDiscount.addProperty("needToBuyProductAmount", needToBuyProductAmount);


                                typeDiscount.addProperty("typeDiscount", discount.getThenYouGet().getOperator());

                                for (SDMOffer offer : discount.getThenYouGet().getSDMOffer()) {
                                    JsonObject getDiscountProduct = new JsonObject();
                                    SDMItem productGet = getSDMItem(zoneName, offer.getItemId());
                                    getDiscountProduct.addProperty("productId", offer.getItemId());
                                    getDiscountProduct.addProperty("productName", productGet.getName());
                                    getDiscountProduct.addProperty("getAmount", offer.getQuantity());
                                    getDiscountProduct.addProperty("additionalPrice", offer.getForAdditional());
                                    getDiscountProducts.add(getDiscountProduct);
                                }

                                getDiscountInfo.add(getDiscountProducts);
                                getDiscountInfo.add(typeDiscount);

                                nameVGetDiscountInfo.addProperty(discount.getName(), getDiscountInfo.toString());

                                arrayDiscount.add(buyDiscount);
                                arrayDiscount.add(nameVGetDiscountInfo);

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
    }

    private SDMItem getSDMItem(String zoneName, Integer ProductId) {
        Market engine = Market.getMarketInstance();
        ZoneMarketDTO zoneMarket = engine.getZoneMarketDTO(zoneName);
        return zoneMarket.getProductsInfo().getSDMItem().stream().filter(item -> item.getId() == ProductId).findFirst().orElse(null);
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

    private JsonObject getStoreDetailsFromOrder(StoreDTO store, SubOrderDTO subOrderDTO, Location storeLocation, Location customerLocation) {
        Double distance = Math.pow(
                Math.pow(storeLocation.getX() - customerLocation.getX(), 2) +
                        Math.pow(storeLocation.getY() - customerLocation.getY(), 2),
                0.5);
        JsonObject jsonStore = new JsonObject();

        jsonStore.addProperty("storeId", store.getSdmStore().getId());
        jsonStore.addProperty("storeName", store.getSdmStore().getName());
        jsonStore.addProperty("location", storeLocation.getX() + "," + storeLocation.getY());
        jsonStore.addProperty("distance", distance);
        jsonStore.addProperty("ppk", store.getSdmStore().getDeliveryPpk());
        jsonStore.addProperty("deliveryCost", subOrderDTO.getDeliveryPrice());
        jsonStore.addProperty("typeProducts", subOrderDTO.getKProductIdVProductsSoldInfo().size());
        jsonStore.addProperty("productsOrderCost", subOrderDTO.getProductsPrice());

        return jsonStore;
    }

    private void processRequestAddDynamicProducts(HttpServletRequest req, HttpServletResponse resp) {
        HttpSession session = req.getSession(false);
        String zoneName = (String) session.getAttribute(ZONE_NAME);

        String jsonObject = req.getParameter("jsonData");
        JsonObject jsonData = new JsonParser().parse(jsonObject).getAsJsonObject();

        Market engine = Market.getMarketInstance();

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
        HttpSession session = req.getSession(false);
        String zoneName = (String) session.getAttribute(ZONE_NAME);

        Market engine = Market.getMarketInstance();

        String jsonObject = req.getParameter("jsonData");
        JsonObject jsonData = new JsonParser().parse(jsonObject).getAsJsonObject();
        Integer storeId = jsonData.get("storeId").getAsInt();

        StoreDTO store = null;
        try {
            store = (StoreDTO) (engine.getStoreDTO(zoneName, storeId)).clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        Map<Integer, Integer> KProductIdVStore = new HashMap<>();
        Map<SDMItem, ProductDTO> KProductInfoVProductDTO = store.getKProductIdVPriceAndAmount();
        Map<SDMItem, ProductDTO> KProductInfoVProduct = new HashMap<>();

        for (SDMItem productInfo : KProductInfoVProductDTO.keySet()) {
            ProductDTO product = new ProductDTO();
            String productId = String.valueOf(productInfo.getId());
            Double productAmount = jsonData.get(productId).getAsDouble();
            product.setAmount(productAmount);
            double price = KProductInfoVProductDTO.get(productInfo).getPrice();
            product.setPrice(price);

            KProductInfoVProduct.put(productInfo, product);
            KProductIdVStore.put(productInfo.getId(), storeId);
        }

        OrderDTO order = getOrderData(req);
        engine.getOrderDTO(zoneName, order, KProductInfoVProduct, KProductIdVStore);
        session.setAttribute(CUSTOMER_ORDER, order);
    }


    private Set<Integer> getStoreIdsInOrder(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        OrderDTO order = (OrderDTO) session.getAttribute(CUSTOMER_ORDER);
        Set<Integer> storesId = order.getKStoreIdVSubOrder().keySet();
        return storesId;
    }

    private void processRequestGetDynamicDetails(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        HttpSession session = req.getSession(false);
        Integer storeId = Integer.valueOf(req.getParameter("storeId"));
        OrderDTO order = (OrderDTO) session.getAttribute(CUSTOMER_ORDER);
        String zoneName = (String) session.getAttribute(ZONE_NAME);

        Market engine = Market.getMarketInstance();
        try (PrintWriter out = resp.getWriter()) {
            StoreDTO store = engine.getStoreDTO(zoneName, storeId);
            SubOrderDTO subOrderDTO = order.getKStoreIdVSubOrder().get(storeId);
            Location storeLocation = store.getSdmStore().getLocation();
            Location customerLocation = order.getCustomerLocation();
            JsonObject jsonStore = getStoreDetailsFromOrder(store, subOrderDTO, storeLocation, customerLocation);
            out.println(jsonStore);
            out.flush();
        }
    }


    private void processRequestAddDiscountToOrder(HttpServletRequest req, HttpServletResponse resp) {
        HttpSession session = req.getSession(false);
        Market engine = Market.getMarketInstance();
        String zoneName = (String) session.getAttribute(ZONE_NAME);
        OrderDTO order = (OrderDTO) session.getAttribute(CUSTOMER_ORDER);
        String discountName = req.getParameter("discountName");

        Map<SDMDiscount, Integer> KDiscountVStoreId = engine.getStoresDiscounts(zoneName, getStoreIdsInOrder(req));//getDiscountsToStoresId
        SDMDiscount discount = KDiscountVStoreId.keySet().stream().filter(sdmDiscount -> sdmDiscount.getName().equals(discountName)).findFirst().get();//getDiscountMatch

        Integer storeId = KDiscountVStoreId.get(discount);//getStoreIdMachForDiscount
        SDMItem productCondition = getSDMItem(zoneName, discount.getIfYouBuy().getItemId());//IfYouBuyProduct

        SubOrderDTO subOrderDTO = order.getKStoreIdVSubOrder().get(storeId);//theSubOrderToUpdate
        ProductDTO productInfo = subOrderDTO.getKProductVForPriceAndAmountInfo().get(productCondition);//IfYouBuyProduct ProductDTO update

        List<SDMItem> getProducts = new ArrayList<>();//List Of Offers To Update

        if (discount.getThenYouGet().getOperator().equals("ONE-OF")){
            Integer getProductId = Integer.parseInt(req.getParameter("productId"));
            SDMOffer offer = getOfferFromDiscount(discount,getProductId);
            SDMItem product = getSDMItem(zoneName, offer.getItemId());
            Map<SDMItem,Map<SDMOffer,Integer>> KOnOfDiscountVMapOffersTime = subOrderDTO.getKOnOfDiscountVMapOffersTime();
            addOneOfDiscount(offer,product,KOnOfDiscountVMapOffersTime);
            getProducts.add(getSDMItem(zoneName, getProductId));

            Double productsPriceSubOrder = offer.getForAdditional() + subOrderDTO.getProductsPrice();
            Double productsPriceOrder = offer.getForAdditional() + order.getProductsPrice();

            subOrderDTO.getKProductIdVProductsSoldInfo().put(product.getId(),product);
            subOrderDTO.setProductsPrice(productsPriceSubOrder);
            order.setProductsPrice(productsPriceOrder);
        } else {
            SubOrderDTO finalSubOrderDTO = subOrderDTO;
            Map<SDMDiscount, Integer> KDiscountVTimeUse = subOrderDTO.getKDiscountVTimeUse();
            discount.getThenYouGet().getSDMOffer().forEach(
                    sdmOffer -> {
                        getProducts.add(getSDMItem(zoneName, sdmOffer.getItemId()));
                        Double productsPriceSubOrder = sdmOffer.getForAdditional() + finalSubOrderDTO.getProductsPrice();
                        Double productsPriceOrder = sdmOffer.getForAdditional() + order.getProductsPrice();
                        finalSubOrderDTO.setProductsPrice(productsPriceSubOrder);
                        order.setProductsPrice(productsPriceOrder);
                    });
            subOrderDTO = finalSubOrderDTO;
            subOrderDTO = updateOrderByApplyDiscount(getProducts, subOrderDTO, discount, productInfo, productCondition);
        }

        order.getKStoreIdVSubOrder().put(storeId, subOrderDTO);
        session.setAttribute(CUSTOMER_ORDER, order);
    }

    private void addOneOfDiscount(SDMOffer offer, SDMItem product, Map<SDMItem, Map<SDMOffer, Integer>> kOnOfDiscountVMapOffersTime) {
        Map<SDMOffer,Integer> offerVTimeUse = null;
        if (!kOnOfDiscountVMapOffersTime.containsKey(product)){
            kOnOfDiscountVMapOffersTime.put(product,new HashMap<>());
        }
        offerVTimeUse = kOnOfDiscountVMapOffersTime.get(product);
        if (!offerVTimeUse.containsKey(offer)){
            offerVTimeUse.put(offer,1);
        }else {
            offerVTimeUse.put(offer,offerVTimeUse.get(offer) + 1);
        }
    }


    private SDMOffer getOfferFromDiscount(SDMDiscount discount, Integer getProductId) {
        return discount.getThenYouGet().getSDMOffer().stream().filter(sdmOffer ->
                getProductId.equals(sdmOffer.getItemId())).findFirst().get();
    }

    private SubOrderDTO updateOrderByApplyDiscount(List<SDMItem> products, SubOrderDTO subOrderDTO, SDMDiscount discount, ProductDTO buyProductInfo, SDMItem productCondition) {
        Double amountUseInDiscount = buyProductInfo.getAmountUsInDiscounts() + discount.getIfYouBuy().getQuantity();
        if (!subOrderDTO.getKDiscountVTimeUse().containsKey(discount)) {
            subOrderDTO.getKDiscountVTimeUse().put(discount, 0);
        }
        Integer timeUseDiscount = subOrderDTO.getKDiscountVTimeUse().get(discount) + 1;
        buyProductInfo.setAmountUsInDiscounts(amountUseInDiscount);

        for (SDMItem product : products) {
            subOrderDTO.getKProductIdVProductsSoldInfo().put(product.getId(), product);
        }

        subOrderDTO.getKProductVForPriceAndAmountInfo().put(productCondition, buyProductInfo);
        subOrderDTO.getKDiscountVTimeUse().put(discount, timeUseDiscount);
        return subOrderDTO;
    }



    private void processRequestGetStoreInOrderInfo(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        HttpSession session = req.getSession(false);
        OrderDTO order = (OrderDTO) session.getAttribute(CUSTOMER_ORDER);
        String zoneName = (String) session.getAttribute(ZONE_NAME);
        Market engine = Market.getMarketInstance();


        JsonArray array = new JsonArray();

        try (PrintWriter out = resp.getWriter()) {
            for (Integer storeId : order.getKStoreIdVSubOrder().keySet()) {
                StoreDTO store = engine.getStoreDTO(zoneName,storeId);
                array.add(getStoreInfo(store));
            }
            out.println(array);
            out.flush();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter(ACTION);
        switch (action) {
            case GET_DISCOUNTS_ACTION:
                processRequestGetDiscounts(req, resp);
                break;
            case GET_STORE_ORDER_DETAILS_ACTION:
                processRequestGetDynamicDetails(req, resp);
                break;
            case GET_STORES_IN_ORDER_DETAILS_ACTION:
                processRequestGetStoreInOrderInfo(req, resp);
                break;
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
            case ADD_NEW_ORDER_STATIC_PRODUCTS_ACTION:
                processRequestAddStoreProducts(req, resp);
                break;
            case ADD_NEW_ORDER_DYNAMIC_PRODUCTS_ACTION:
                processRequestAddDynamicProducts(req, resp);
                break;
            case ADD_DISCOUNT_TO_ORDER_ACTION:
                processRequestAddDiscountToOrder(req, resp);
                break;
            case GET_STORE_INFO_ACTION:
                processRequestGetStoreInfo(req, resp);
                break;
        }
    }
}
