package order;

import dto.OrderDTO;
import dto.SubOrderDTO;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Order {
    public static Integer orderId = 1;
    private OrderDTO orderDTO;
    private Map<Integer,SubOrder> KStoreIdVSubOrder = new HashMap<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        Order order = (Order) o;
        return Objects.equals(orderDTO, order.orderDTO) &&
                Objects.equals(KStoreIdVSubOrder, order.KStoreIdVSubOrder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderDTO, KStoreIdVSubOrder);
    }

    public static Integer getOrderId() {
        return orderId;
    }

    public static void setOrderId(Integer orderId) {
        Order.orderId = orderId;
    }

    public void setOrderDTO(OrderDTO orderDTO) {
        this.orderDTO = orderDTO;
    }

    public void setKStoreIdVSubOrder(Map<Integer, SubOrder> KStoreIdVSubOrder) {
        this.KStoreIdVSubOrder = KStoreIdVSubOrder;
    }

    public Order(OrderDTO orderDTO){
        orderDTO.setId(orderId);
        this.orderDTO = orderDTO;
        for (Integer storeId : orderDTO.getKStoreIdVSubOrder().keySet()){
            SubOrderDTO subOrderDTO = orderDTO.getKStoreIdVSubOrder().get(storeId);
            subOrderDTO.setId(orderId);
            SubOrder subOrder =  new SubOrder(subOrderDTO);
            KStoreIdVSubOrder.put(storeId, subOrder);
        }
        orderId++;
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
