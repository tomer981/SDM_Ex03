package market;

import dto.ManagerDTO;
import dto.OrderDTO;
import dto.ZoneMarketDTO;
import order.Order;
import position.Manager;
import xml.schema.SchemaBaseJaxbObjects;
import xml.schema.generated.SDMDiscount;
import xml.schema.generated.SDMItem;
import xml.schema.generated.SDMItems;
import xml.schema.generated.SDMStore;

import java.util.*;
import java.util.stream.Collectors;

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
    public List<OrderDTO> getOrdersDTO(){
        List<OrderDTO> ordersDTO = new ArrayList<>();
        for (Order order : orders){
            ordersDTO.add(order.getOrderDTO());
        }

        return ordersDTO;
    }
    public List<OrderDTO> getOrdersDTOByIds(List<Integer> ordersId){
        return getOrdersDTO().stream().filter(orderDTO -> ordersId.contains(orderDTO.getId())).collect(Collectors.toList());
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

        Double avgPriceOrder = 0.0;
        int numberOfStores = KManagerNameVManager.values().stream().mapToInt(manager -> manager.getNumberOfStores(zoneName)).sum();

        for (SDMItem product : productsInfo.getSDMItem()){
            Integer storesSellProduct = 0;
            Double totalPriceOfProducts = 0.0;
            Double totalAmountSold = 0.0;

            for (Manager manager : KManagerNameVManager.values()){
                storesSellProduct += manager.getNumberOfStoreSellProductByZone(product, zoneName);
                totalPriceOfProducts += manager.getTotalCostProductByZone(product, zoneName);
                totalAmountSold += manager.getTotalAmountProductSoldByZone(product, zoneName);
            }

            KProductVNumberOfStoreSellProduct.put(product, storesSellProduct);
            KProductVAvgPriceOfProduct.put(product,totalPriceOfProducts / storesSellProduct);
            KProductVTotalAmountSold.put(product, totalAmountSold);
        }

        if (orders.size() > 0){
            avgPriceOrder = orders.stream().mapToDouble(order->order.getOrderDTO().getProductsPrice()).sum() / orders.size() ;
        }

        return new ZoneMarketDTO(managerDefineZone,
                zoneName,
                productsInfo,
                KProductVNumberOfStoreSellProduct,
                KProductVAvgPriceOfProduct,
                KProductVTotalAmountSold,
                KManagerNameVManagerDTO,
                orders.size(),
                avgPriceOrder,
                numberOfStores);
    }

    public void addOrder(Order order) {
        orders.add(order);
    }
}
