package dto;


import xmlBuild.schema.generated.*;

import java.util.List;
import java.util.Map;


public class ZoneMarketDTO {
    //general
    private ManagerDTO managerDefineName;
    private String zoneName;

    //products Info
    private SDMItems productsInfo;
    private Map<SDMItem, Integer> KProductVNumberOfStoreSellProduct;
    private Map<SDMItem, Double> KProductVAvgPriceOfProduct;
    private Map<SDMItem, Double> KProductVTotalAmountSold;

    private Map<String,ManagerDTO> KManagerNameVManager;
    private List<OrderDTO> ordersDTO;

    private Double avgProductsPriceOrders;//Delivery not included
    private Integer numberOfStores;
    private Integer numberOfOrders;

    //get
    public ManagerDTO getManagerDefineName() {
        return managerDefineName;
    }
    public String getZoneName() {
        return zoneName;
    }
    public SDMItems getProductsInfo() {
        return productsInfo;
    }
    public Map<SDMItem, Integer> getKProductVNumberOfStoreSellProduct() {
        return KProductVNumberOfStoreSellProduct;
    }
    public Map<SDMItem, Double> getKProductVAvgPriceOfProduct() {
        return KProductVAvgPriceOfProduct;
    }
    public Map<SDMItem, Double> getKProductVTotalAmountSold() {
        return KProductVTotalAmountSold;
    }
    public Map<String, ManagerDTO> getKManagerNameVManager() {
        return KManagerNameVManager;
    }
    public List<OrderDTO> getOrdersDTO() {
        return ordersDTO;
    }
    public Double getAvgProductsPriceOrders() {
        return avgProductsPriceOrders;
    }
    public int getNumberOfStores() {
        return numberOfStores;
    }
    public Integer getNumberOfOrders() {
        return numberOfOrders;
    }

    //c'tor
    public ZoneMarketDTO(ManagerDTO managerDefineName, String zoneName, SDMItems productsInfo, Map<SDMItem, Integer> KProductVNumberOfStoreSellProduct, Map<SDMItem, Double> KProductVAvgPriceOfProduct, Map<SDMItem, Double> KProductVTotalAmountSold, Map<String, ManagerDTO> KManagerNameVManager, Integer numberOfOrders, Double avgProductsPriceOrders, int numberOfStores) {
        this.managerDefineName = managerDefineName;
        this.zoneName = zoneName;
        this.productsInfo = productsInfo;
        this.KProductVNumberOfStoreSellProduct = KProductVNumberOfStoreSellProduct;
        this.KProductVAvgPriceOfProduct = KProductVAvgPriceOfProduct;
        this.KProductVTotalAmountSold = KProductVTotalAmountSold;
        this.KManagerNameVManager = KManagerNameVManager;
        this.numberOfOrders = numberOfOrders;
        this.avgProductsPriceOrders = avgProductsPriceOrders;
        this.numberOfStores = numberOfStores;
    }
}
