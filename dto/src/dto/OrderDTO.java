package dto;

import order.SubOrder;
import store.Store;
import xml.schema.generated.Location;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class OrderDTO {
    private Integer id;
    private Date date;
    private Location customerLocation;
    private String CustomerName;
    private Map<Integer, SubOrderDTO> KStoreVSubStore = new HashMap<>();

    private Double deliveryPrice;
    private Double ProductsPrice;

    public Location getCustomerLocation() {
        return customerLocation;
    }
    public String getCustomerName() {
        return CustomerName;
    }
    public Integer getId() {
        return id;
    }
    public Double getProductsPrice() {
        return ProductsPrice;
    }
    public Map<Integer, SubOrderDTO> getKStoreVSubStore() {
        return KStoreVSubStore;
    }
    public Double getDeliveryPrice() {
        return deliveryPrice;
    }

    public void setKStoreVSubStore(Map<Integer, SubOrderDTO> KStoreVSubStore) {
        this.KStoreVSubStore = KStoreVSubStore;
    }

    public Date getDate() {
        return date;
    }


}
