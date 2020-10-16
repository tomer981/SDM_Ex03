package market;

import dto.ManagerDTO;
import dto.OrderDTO;
import dto.ZoneMarketDTO;
import order.Order;
import position.Manager;
import xml.schema.SchemaBaseJaxbObjects;
import xml.schema.generated.SDMItem;
import xml.schema.generated.SDMItems;
import xml.schema.generated.SDMStore;

import java.util.*;

public class ZoneMarket {
    private Manager managerDefine;
    private String zoneName;
    private SDMItems productsInfo;
    private Map<String, Manager> KManagerNameVManager = new HashMap<>();
    private List<Order> orders = new ArrayList<>();//amount of order + avg price per order + number of time each product sold


    //get
    public Manager getManagerDefine() {
        return managerDefine;
    }
    public Map<String, Manager> getKManagerNameVManager() {
        return KManagerNameVManager;
    }
    public String getZoneName() {
        return zoneName;
    }
    public SDMItems getProductsInfo() {
        return productsInfo;
    }
    public List<Order> getOrders() {
        return orders;
    }


    //c'tor
    public ZoneMarket(SchemaBaseJaxbObjects schema, Manager manager){
        zoneName = schema.getXmlZoneMarket().getSDMZone().getName();
        productsInfo = schema.getXmlZoneMarket().getSDMItems();
        managerDefine = manager;

        List <SDMStore> storesInfo = schema.getXmlZoneMarket().getSDMStores().getSDMStore();
        for(SDMStore sdmStore : storesInfo){
            addStoreToManager(sdmStore,manager);
        }
    }

    //Method
    private Map<String,ManagerDTO> getKManagerNameVManagerDTO(){
        Map<String,ManagerDTO> KManagerNameVManagerDTO = new HashMap<>();

        for (Manager manager : KManagerNameVManager.values()) {
            ManagerDTO managerDTO = manager.getManagerDTO(zoneName);
            KManagerNameVManagerDTO.put(manager.getName(),managerDTO);
        }

        return KManagerNameVManagerDTO;
    }
    public void addStoreToManager(SDMStore store, Manager manager){
        if (!KManagerNameVManager.containsValue(manager)){
            KManagerNameVManager.put(manager.getName(),manager);
        }

        manager.addStoreToManager(zoneName, store, productsInfo);
    }


    //DTO method
    public ZoneMarketDTO getZoneMarketDTO(){
        //zoneInfo
        //productsInfo
        Map<SDMItem, Integer> KProductVNumberOfStoreSellProduct = new HashMap<>();
        Map<SDMItem, Double> KProductVAvgPriceOfProduct = new HashMap<>();
        Map<SDMItem, Double> KProductVTotalAmountSold = new HashMap<>();
        Map<String,ManagerDTO> KManagerNameVManagerDTO = getKManagerNameVManagerDTO();
        ManagerDTO managerDefineZone = KManagerNameVManagerDTO.get(managerDefine.getName());

        Integer numberOfOrders = orders.size();
        Double avgPriceOrder = 0.0;
        int numberOfStores = 0;

        for (SDMItem product : productsInfo.getSDMItem()){
            Integer storesSellProduct = 0;
            Double totalPriceOfProducts = 0.0;
            Double totalAmountSold = 0.0;

            for (Manager manager : KManagerNameVManager.values()){
                numberOfStores += manager.getKZoneNameVStores().get(zoneName).size();//TODO: check what for
                storesSellProduct += manager.getNumberOfStoreSellProductByZone(product, zoneName);
                totalPriceOfProducts += manager.getTotalCostProductByZone(product, zoneName);
                totalAmountSold += manager.getTotalAmountProductSoldByZone(product, zoneName);
            }

            KProductVNumberOfStoreSellProduct.put(product, storesSellProduct);
            KProductVAvgPriceOfProduct.put(product,totalPriceOfProducts / storesSellProduct);
            KProductVTotalAmountSold.put(product, totalAmountSold);
        }

        for (Order order : orders){
            OrderDTO orderDTO = order.getOrderDTO();
            avgPriceOrder += orderDTO.getProductsPrice();
        }

        avgPriceOrder = avgPriceOrder / orders.size();

        return new ZoneMarketDTO(managerDefineZone,
                zoneName,
                productsInfo,
                KProductVNumberOfStoreSellProduct,
                KProductVAvgPriceOfProduct,
                KProductVTotalAmountSold,
                KManagerNameVManagerDTO,
                numberOfOrders,
                avgPriceOrder,
                numberOfStores);
    }
}
