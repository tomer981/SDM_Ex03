package order;

import dto.OrderDTO;
import xml.schema.generated.Location;


import java.util.Date;
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
    Double deliveryPrice = getDeliveryPrice();

    private Double getDeliveryPrice() {

    }

    public OrderDTO getOrderDTO() {

        return null;
    }
}
