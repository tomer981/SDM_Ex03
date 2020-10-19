package dto;

import xml.schema.generated.Location;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class OrderDTO {
    private Integer id;
    private Date date;
    private Location customerLocation;
    private String customerName;

    private Map<Integer, SubOrderDTO> KStoreVSubStore = new HashMap<>();
    private Double totalDeliveryPrice = 0.0;
    private Double ProductsPrice = 0.0;

    //get
    public Location getCustomerLocation() {
        return customerLocation;
    }
    public String getCustomerName() {
        return customerName;
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
    public void setTotalDeliveryCost(Double totalDeliveryPrice) {
        this.totalDeliveryPrice = totalDeliveryPrice;
    }
    public void setProductsPrice(Double productsPrice) {
        ProductsPrice = productsPrice;
    }
    public void setKStoreVSubStore(Map<Integer, SubOrderDTO> KStoreVSubStore) {
        this.KStoreVSubStore = KStoreVSubStore;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    //c'tor
    public OrderDTO(Integer id, Date date, Location customerLocation, String customerName) {
        this.id = id;
        this.date = date;
        this.customerLocation = customerLocation;
        this.customerName = customerName;
    }
}
