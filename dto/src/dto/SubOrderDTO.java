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

    private Map<Integer,SDMItem> KProductIdVProductsSoldInfo;
    private Map<SDMItem, ProductDTO> KProductVForPriceAndAmountInfo;
    private Map<SDMDiscount, Integer> KDiscountVTimeUse;

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


    public SubOrderDTO(Integer id, Date date, String customerName, Location customerLocation) {
        this.id = id;
        this.date = date;
        this.customerName = customerName;
        this.customerLocation = customerLocation;
    }
}
