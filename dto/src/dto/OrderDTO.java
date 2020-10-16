package dto;

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

    private Double totalDeliveryPrice;
    private Double ProductsPrice;

    //get
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
    public Double getTotalDeliveryPrice() {
        return totalDeliveryPrice;
    }
    public Date getDate() {
        return date;
    }

    //set
    public void setTotalDeliveryPrice(Double totalDeliveryPrice) {
        this.totalDeliveryPrice = totalDeliveryPrice;
    }
    public void setProductsPrice(Double productsPrice) {
        ProductsPrice = productsPrice;
    }

    public void setKStoreVSubStore(Map<Integer, SubOrderDTO> KStoreVSubStore) {
        this.KStoreVSubStore = KStoreVSubStore;
    }



}
