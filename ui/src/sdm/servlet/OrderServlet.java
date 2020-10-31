package sdm.servlet;

import com.google.gson.*;
import dto.*;
import market.Market;
import xmlBuild.schema.generated.*;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static sdm.constants.Constants.*;

@WebServlet(name = "OrderServlet", urlPatterns = {"/OrderServlet"})
public class OrderServlet extends HttpServlet {

    private void processRequestGetOrderInProcess(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        HttpSession session = req.getSession(false);

        int[] ordersIds = {-1};

        try (PrintWriter out = resp.getWriter()) {
            out.println(new Gson().toJson(ordersIds));
            out.flush();
        }
    }

    private void processRequestGetDisplayOrder(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        HttpSession session = req.getSession(false);
        Market engine = Market.getMarketInstance();
        String userName = (String) session.getAttribute(USER_NAME);

        String ordersIdsObject = req.getParameter("orders");
        int[] ordersIds = new Gson().fromJson(ordersIdsObject, int[].class);

        List<OrderDTO> orders = new ArrayList<>();

        if (ordersIds[0] == -1) {
            orders.add((OrderDTO) session.getAttribute(CUSTOMER_ORDER));
        } else {
            List<Integer> OrdersIds = new ArrayList<>();
            for (int i = 0; i < ordersIds.length; i++) {
                OrdersIds.add(ordersIds[i]);
            }

            orders = engine.getOrdersByCustomerName(userName);
            orders = orders.stream().filter(orderDTO -> !OrdersIds.contains(orderDTO.getId())).collect(Collectors.toList());
        }

        JsonArray jsonOrders = new JsonArray();
        for (OrderDTO order : orders) {
            jsonOrders.add(getOrderJsonObject(order));
        }

        try (PrintWriter out = resp.getWriter()) {
            out.println(jsonOrders);
            out.flush();
        }
    }

    private JsonObject getOrderJsonObject(OrderDTO order) {
        Integer id = order.getId();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String strDate = dateFormat.format(order.getDate());
        String customerLocation = order.getCustomerLocation().getX() + "," + order.getCustomerLocation().getY();
        Integer numberOfProducts = order.getKStoreIdVSubOrder().values().stream().mapToInt(subOrder -> subOrder.getKProductIdVProductsSoldInfo().size()).sum();
        Double totalProductsCost = order.getProductsPrice();
        Double deliveryOrder = order.getTotalDeliveryPrice();
        Double totalOrderCost = totalProductsCost + deliveryOrder;
        JsonObject orderDisplayObject = new JsonObject();

        orderDisplayObject.addProperty("orderId", id);
        orderDisplayObject.addProperty("date", strDate);
        orderDisplayObject.addProperty("customerName", order.getCustomerName());
        orderDisplayObject.addProperty("customerLocation", customerLocation);
        orderDisplayObject.addProperty("numberOfProducts", numberOfProducts);
        orderDisplayObject.addProperty("totalProductsCost", totalProductsCost);
        orderDisplayObject.addProperty("deliveryOrder", deliveryOrder);
        orderDisplayObject.addProperty("totalOrderCost", totalOrderCost);
        return orderDisplayObject;
    }

    private JsonObject getSubOrderInfoJsonObject(SubOrderDTO subOrderDTO, StoreDTO storeDTO) {
        JsonObject subOrderInfo = new JsonObject();
        String storeLocation = storeDTO.getSdmStore().getLocation().getX() + "," + storeDTO.getSdmStore().getLocation().getY();
        Double distance = Math.pow(
                Math.pow(
                        storeDTO.getSdmStore().getLocation().getX() -
                                subOrderDTO.getCustomerLocation().getX(), 2) +
                        Math.pow(
                                storeDTO.getSdmStore().getLocation().getY() -
                                        subOrderDTO.getCustomerLocation().getY(), 2),
                0.5);
        subOrderInfo.addProperty("storeId", storeDTO.getSdmStore().getId());
        subOrderInfo.addProperty("storeName", storeDTO.getSdmStore().getName());
        subOrderInfo.addProperty("storeLocation", storeLocation);
        subOrderInfo.addProperty("PPK", storeDTO.getSdmStore().getDeliveryPpk());
        subOrderInfo.addProperty("distance", distance);
        subOrderInfo.addProperty("products", subOrderDTO.getKProductIdVProductsSoldInfo().size());
        subOrderInfo.addProperty("productsCosts", subOrderDTO.getProductsPrice());
        subOrderInfo.addProperty("deliveryCosts", subOrderDTO.getDeliveryPrice());
        subOrderInfo.addProperty("totalCost", subOrderDTO.getProductsPrice() + subOrderDTO.getDeliveryPrice());
        return subOrderInfo;
    }


    private JsonArray getSubOrdersJsonArray(Map<Integer, SubOrderDTO> subOrders, String zoneName) {
        Market engine = Market.getMarketInstance();
        JsonArray subOrdersJsonArray = new JsonArray();
        for (Map.Entry<Integer, SubOrderDTO> entry : subOrders.entrySet()) {
            Integer storeId = entry.getKey();
            SubOrderDTO subOrderDTO = entry.getValue();
            StoreDTO storeDTO = engine.getStoreDTO(zoneName, storeId);
            subOrdersJsonArray.add(getSubOrderInfoJsonObject(subOrderDTO, storeDTO));
        }

        return subOrdersJsonArray;
    }


    private void processRequestGetSubOrdersCustomer(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        HttpSession session = req.getSession(false);
        Integer orderId = Integer.valueOf(req.getParameter("orderId"));
        String zoneName = (String) session.getAttribute(ZONE_NAME);

        Map<Integer, SubOrderDTO> subOrders = null;
        Market engine = Market.getMarketInstance();
        if (orderId == -1) {
            OrderDTO order = (OrderDTO) session.getAttribute(CUSTOMER_ORDER);
            subOrders = order.getKStoreIdVSubOrder();
        } else {
            OrderDTO order = engine.getOrderByZoneAndOrderId(zoneName, orderId);
            subOrders = order.getKStoreIdVSubOrder();
        }
        JsonArray subOrdersJsonArray = getSubOrdersJsonArray(subOrders, zoneName);
        try (PrintWriter out = resp.getWriter()) {
            out.println(subOrdersJsonArray);
            out.flush();
        }
    }

    private void processRequestGetSubOrdersProducts(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        HttpSession session = req.getSession(false);
        String zoneName = (String) session.getAttribute(ZONE_NAME);

        Integer orderId = Integer.valueOf(req.getParameter("orderId"));
        Integer storeId = Integer.valueOf(req.getParameter("storeId"));
        Market engine = Market.getMarketInstance();

        Map<Integer, SubOrderDTO> subOrders = null;

        if (orderId == -1) {
            OrderDTO order = (OrderDTO) session.getAttribute(CUSTOMER_ORDER);
            subOrders = order.getKStoreIdVSubOrder();
        } else {
            OrderDTO order = engine.getOrderByZoneAndOrderId(zoneName, orderId);
            subOrders = order.getKStoreIdVSubOrder();
        }
        JsonArray subOrdersProductsJsonArray = getSubOrdersProductsJsonArray(subOrders.get(storeId), zoneName);

        try (PrintWriter out = resp.getWriter()) {
            out.println(subOrdersProductsJsonArray);
            out.flush();
        }
    }

    private JsonObject getNormalProductInfo(SDMItem productInfo, ProductDTO productCostAndAmount) {
        JsonObject product = new JsonObject();
        product.addProperty("productId", productInfo.getId());
        product.addProperty("productName", productInfo.getName());
        product.addProperty("purchaseCategory", productInfo.getPurchaseCategory());
        product.addProperty("productAmount", productCostAndAmount.getAmount());
        product.addProperty("pricePerUnit", productCostAndAmount.getPrice());
        product.addProperty("totalCost", productCostAndAmount.getPrice() * productCostAndAmount.getAmount());
        product.addProperty("discount", "No");
        return product;
    }

    private JsonObject getDiscountProductInfo(SDMItem product, SDMOffer offer, Integer timeUse) {
        JsonObject jsonProduct = new JsonObject();
        jsonProduct.addProperty("productId", product.getId());
        jsonProduct.addProperty("productName", product.getName());
        jsonProduct.addProperty("purchaseCategory", product.getPurchaseCategory());
        jsonProduct.addProperty("productAmount", offer.getQuantity() * timeUse);
        jsonProduct.addProperty("pricePerUnit", offer.getForAdditional() / offer.getQuantity());
        jsonProduct.addProperty("totalCost", offer.getForAdditional() * timeUse);
        jsonProduct.addProperty("discount", "Yes");

        return jsonProduct;
    }

    private JsonArray getSubOrdersProductsJsonArray(SubOrderDTO subOrder, String zoneName) {

        Market engine = Market.getMarketInstance();
        ZoneMarketDTO zoneMarket = engine.getZoneMarketDTO(zoneName);
        JsonArray subOrdersProductsJsonArray = new JsonArray();

        for (Map.Entry<SDMItem, ProductDTO> product : subOrder.getKProductVForPriceAndAmountInfo().entrySet()) {
            JsonObject normalProduct = getNormalProductInfo(product.getKey(), product.getValue());
            subOrdersProductsJsonArray.add(normalProduct);
        }
        for (Map.Entry<SDMDiscount, Integer> discount : subOrder.getKDiscountVTimeUse().entrySet()) {
            for (SDMOffer offerProduct : discount.getKey().getThenYouGet().getSDMOffer()) {
                SDMItem sdmItem = zoneMarket.getProductsInfo().getSDMItem().stream().filter(item -> item.getId() == offerProduct.getItemId()).findFirst().orElse(null);
                JsonObject discountProduct = getDiscountProductInfo(sdmItem, offerProduct, discount.getValue());
                subOrdersProductsJsonArray.add(discountProduct);
            }
        }
        Map<SDMItem, Map<SDMOffer, Integer>> KOnOfDiscountVMapOffersTime = subOrder.getKOnOfDiscountVMapOffersTime();
        for (SDMItem product : KOnOfDiscountVMapOffersTime.keySet()) {
            Map<SDMOffer, Integer> mapOffersTime = KOnOfDiscountVMapOffersTime.get(product);
            for (SDMOffer offer : mapOffersTime.keySet()) {
                JsonObject discountProduct = getDiscountProductInfo(product, offer, mapOffersTime.get(offer));
                subOrdersProductsJsonArray.add(discountProduct);

            }
        }

        return subOrdersProductsJsonArray;

    }

    private void processRequestConfirmOrder(HttpServletRequest req, HttpServletResponse resp) {
        HttpSession session = req.getSession(false);
        String zoneName = (String) session.getAttribute(ZONE_NAME);
        OrderDTO order = (OrderDTO) session.getAttribute(CUSTOMER_ORDER);
        String userName = (String) session.getAttribute(USER_NAME);
        Market engine = Market.getMarketInstance();

        engine.addOrder(zoneName, userName, order);

        session.removeAttribute(CUSTOMER_ORDER);

    }

    private void processRequestGetAllOrdersIds(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        HttpSession session = req.getSession(false);
        String userName = (String) session.getAttribute(USER_NAME);

        Market engine = Market.getMarketInstance();
        List<OrderDTO> ordersList = engine.getOrdersByCustomerName(userName);
        List listOrdersIds = ordersList.stream().map(OrderDTO::getId).collect(Collectors.toList());

        int[] ordersIds = new int[listOrdersIds.size()];
        for (int i = 0; i < ordersIds.length; i++) {
            ordersIds[i] = listOrdersIds.indexOf(i);
        }

        try (PrintWriter out = resp.getWriter()) {
            out.println(new Gson().toJson(ordersIds));
            out.flush();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter(ACTION);
        switch (action) {
            case GET_IN_PROGRESS_ORDER_ACTION:
                processRequestGetOrderInProcess(req, resp);
                break;
            case CONFIRM_ORDER_ACTION:
                processRequestConfirmOrder(req, resp);
                break;
            case GET_ALL_ORDERS_IDS:
                processRequestGetAllOrdersIds(req, resp);
                break;
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter(ACTION);
        switch (action) {
            case GET_DISPLAY_ORDER_ACTION:
                processRequestGetDisplayOrder(req, resp);
                break;
            case GET_SUB_ORDERS_CUSTOMER_ACTION:
                processRequestGetSubOrdersCustomer(req, resp);
                break;
            case GET_SUB_ORDERS_PRODUCTS_ACTION:
                processRequestGetSubOrdersProducts(req, resp);
                break;
        }
    }


}
