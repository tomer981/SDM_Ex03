package order;

import dto.OrderDTO;
import dto.SubOrderDTO;


import java.util.HashMap;
import java.util.Map;

public class Order {
    private OrderDTO orderDTO;
    private Map<Integer,SubOrder> KStoreIdVSubOrder = new HashMap<>();


    public Order(OrderDTO orderDTO){
        this.orderDTO = orderDTO;
        for (Integer storeId : orderDTO.getKStoreVSubStore().keySet()){
            SubOrder subOrder =  new SubOrder(orderDTO.getKStoreVSubStore().get(storeId));
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

        orderDTO.setTotalDeliveryPrice(totalDeliveryPrice);
        orderDTO.setProductsPrice(totalProductsPrice);

        return orderDTO;
    }
}
