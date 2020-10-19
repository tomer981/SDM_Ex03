package dto;

import xml.schema.generated.Location;
import xml.schema.generated.SDMDiscount;
import xml.schema.generated.SDMItem;

import java.util.*;

public class SubOrderDTO {
    private Integer id = -1;
    private Date date;
    private String customerName;
    private Location customerLocation;

    private Map<Integer,SDMItem> KProductIdVProductsSoldInfo = new HashMap<>();
    private Map<SDMItem, ProductDTO> KProductVForPriceAndAmountInfo = new HashMap<>();
    private Map<SDMDiscount, Integer> KDiscountVTimeUse = new HashMap<>();

    private Double deliveryPrice;
    private Double productsPrice;

    //get
    public Integer getId() {
        return id;
    }
    public Date getDate() {
        return date;
    }
    public String getCustomerName() {
        return customerName;
    }
    public Location getCustomerLocation() {
        return customerLocation;
    }
    public Map<Integer, SDMItem> getKProductIdVProductsSoldInfo() {
        return KProductIdVProductsSoldInfo;
    }
    public Map<SDMItem, ProductDTO> getKProductVForPriceAndAmountInfo() {
        return KProductVForPriceAndAmountInfo;
    }
    public Map<SDMDiscount, Integer> getKDiscountVTimeUse() {
        return KDiscountVTimeUse;
    }
    public Double getDeliveryPrice() {
        return deliveryPrice;
    }
    public Double getProductsPrice() {
        return productsPrice;
    }

    //set
    public void setDeliveryCost(Double deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }
    public void setProductsPrice(Double productsPrice) {
        this.productsPrice = productsPrice;
    }
    public void setId(Integer orderId) {
        id = orderId;
    }
    //c'tor
    public SubOrderDTO(Integer id, Date date, String customerName, Location customerLocation) {
        this.id = id;
        this.date = date;
        this.customerName = customerName;
        this.customerLocation = customerLocation;
    }

}
