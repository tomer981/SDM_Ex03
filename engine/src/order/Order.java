package order;

import dto.OrderDTO;
import dto.SubOrderDTO;
import java.util.HashMap;
import java.util.Map;

public class Order {
    public static Integer orderId = 1;
    private OrderDTO orderDTO;
    private Map<Integer,SubOrder> KStoreIdVSubOrder = new HashMap<>();


    public Order(OrderDTO orderDTO){
        orderDTO.setId(orderId);
        this.orderDTO = orderDTO;
        for (Integer storeId : orderDTO.getKStoreIdVSubOrder().keySet()){
            SubOrderDTO subOrderDTO = orderDTO.getKStoreIdVSubOrder().get(storeId);
            subOrderDTO.setId(orderId);
            SubOrder subOrder =  new SubOrder(subOrderDTO);
            KStoreIdVSubOrder.put(storeId, subOrder);
        }
    }
    public Map<Integer, SubOrder> getKStoreIdVSubOrder() {
        return KStoreIdVSubOrder;
    }


    public OrderDTO getOrderDTO() {
        Double totalDeliveryPrice = 0.0;
        Double totalProductsPrice = 0.0;
        for (SubOrder subOrder : KStoreIdVSubOrder.values()){
            totalDeliveryPrice += subOrder.getSubOrderDTO().getDeliveryPrice();
            totalProductsPrice += subOrder.getSubOrderDTO().getProductsPrice();
        }

        orderDTO.setTotalDeliveryCost(totalDeliveryPrice);
        orderDTO.setProductsPrice(totalProductsPrice);

        return orderDTO;
    }
}
