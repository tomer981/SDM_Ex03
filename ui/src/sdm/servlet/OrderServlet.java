package sdm.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import dto.OrderDTO;
import dto.StoreDTO;
import order.Order;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.jar.JarEntry;

import static sdm.constants.Constants.*;

@WebServlet(name = "OrderServlet", urlPatterns = {"/OrderServlet"})
public class OrderServlet extends HttpServlet {

    private void processRequestGetOrderInProcess(HttpServletRequest req, HttpServletResponse resp) throws IOException {
//        resp.setContentType("application/json");
        HttpSession session = req.getSession(false);
        OrderDTO order = (OrderDTO) session.getAttribute(CUSTOMER_ORDER);
        Type listType = new TypeToken<ArrayList<OrderDTO>>(){}.getType();

        List<OrderDTO> orders = new ArrayList<>();
        orders.add(order);


        Gson gson = new Gson();
//        Arrays.asList(orders)
        String jsonObject = gson.toJson(Arrays.asList(orders));

        try (PrintWriter out = resp.getWriter()) {
            out.println(jsonObject);
            out.flush();
        }
    }


    private void processRequestGetDisplayOrder(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

//        Gson gson = new Gson();
//        Type listType = new TypeToken<Collection<OrderDTO>>(){}.getType();
//
//        List<OrderDTO> orders = gson.fromJson(req.getParameter("orders"), listType);
        HttpSession session = req.getSession(false);
        OrderDTO order = (OrderDTO) session.getAttribute(CUSTOMER_ORDER);

        JsonArray orders = new JsonArray();
        orders.add(getOrderJsonObject(order));
        try (PrintWriter out = resp.getWriter()) {
            out.println(orders);
            out.flush();
        }
    }

    private JsonObject getOrderJsonObject(OrderDTO order) {
        Integer id = order.getId();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh");
        String strDate = dateFormat.format(order.getDate());
        String customerLocation  = order.getCustomerLocation().getX() + "," + order.getCustomerLocation().getY();
        Integer numberOfProducts = order.getKStoreIdVSubOrder().values().stream().mapToInt(subOrder->subOrder.getKProductIdVProductsSoldInfo().size()).sum();
        Double totalProductsCost =  order.getProductsPrice();
        Double deliveryOrder = order.getTotalDeliveryPrice();
        Double totalOrderCost = totalProductsCost + deliveryOrder;
        JsonObject orderDisplayObject = new JsonObject();

        orderDisplayObject.addProperty("orderId",id);
        orderDisplayObject.addProperty("date",strDate);
        orderDisplayObject.addProperty("customerName",order.getCustomerName());
        orderDisplayObject.addProperty("customerLocation",customerLocation);
        orderDisplayObject.addProperty("numberOfProducts",numberOfProducts);
        orderDisplayObject.addProperty("totalProductsCost",totalProductsCost);
        orderDisplayObject.addProperty("deliveryOrder",deliveryOrder);
        orderDisplayObject.addProperty("totalOrderCost",totalOrderCost);
        return orderDisplayObject;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter(ACTION);
        switch (action) {
            case GET_IN_PROGRESS_ORDER_ACTION:
                processRequestGetOrderInProcess(req, resp);
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

        }
    }
}
