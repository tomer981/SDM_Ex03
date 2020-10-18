package market;

import dto.*;
import order.Action;
import order.Order;
import position.Customer;
import position.Manager;
import position.Managers;
import xml.schema.SchemaBaseJaxbObjects;
import xml.schema.generated.SDMItem;
import xml.schema.generated.SDMStore;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.util.*;


public final class Market {
    private static volatile Market marketInstance;

    private Map<String, ZoneMarket> KNameZoneVZone;
    private Map<ZoneMarket, Managers> KZoneVManagers;
    private Map<String, Manager> KManagerNameVManger;
    private Map<String, Customer> KCustomerNameVCustomer;

    //c'tor - instance
    private Market(){
        KNameZoneVZone = new HashMap<>();
        KZoneVManagers = new HashMap<>();
        KManagerNameVManger = new HashMap<>();
        KCustomerNameVCustomer = new HashMap<>();
    }
    private static Market getMarketInstance(){
        Market market = marketInstance;
        if (market != null){
            return market;
        }
        synchronized (Market.class){
            if(marketInstance == null){
                marketInstance = new Market();
            }
        }

        return marketInstance;
    }


    //get
    public Map<String, String> getNamePosition(){
        Map<String, String> KNameVPosition = new HashMap<>();
        for (Customer customer : KCustomerNameVCustomer.values()){
            KNameVPosition.put(customer.getCustomerDTO().getName(), "Customer");
        }

        for (Manager manager : KManagerNameVManger.values()){
            KNameVPosition.put(manager.getName(), "Customer");
        }

        return KNameVPosition;
    }
    public List<ZoneMarketDTO> getZonesMarketDTO(){
        List<ZoneMarketDTO> zoneMarketDTOs = new ArrayList<>();
        KZoneVManagers.keySet().forEach(zoneMarket -> zoneMarketDTOs.add(zoneMarket.getZoneMarketDTO()));
        return zoneMarketDTOs;
    }
    public ZoneMarketDTO getZoneMarketDTO(String name){
        return  KNameZoneVZone.get(name).getZoneMarketDTO();
    }


    //method

    //////method complete

    //////////method private
    private synchronized void addZoneMarket(File file, Manager manager) throws JAXBException {
        SchemaBaseJaxbObjects schema = new SchemaBaseJaxbObjects(file);
        if (KNameZoneVZone.containsKey(schema.getXmlZoneMarket().getSDMZone().getName())){
            throw new IllegalArgumentException("the zone with the same name already exist");
        }

        ZoneMarket zoneMarket = new ZoneMarket(schema,manager);
        Managers managers = new Managers(zoneMarket.getZoneName());


        KZoneVManagers.put(zoneMarket, managers);
        KNameZoneVZone.put(zoneMarket.getZoneName(),zoneMarket);
        managers.addManager(manager);
    }

    //////////method public
    public void addStoreToManager(String managerName, String zoneName, SDMStore sdmStore){
        Manager manager = KManagerNameVManger.get(managerName);
        ZoneMarket zoneMarket = KNameZoneVZone.get(managerName);
        Managers managers = KZoneVManagers.get(zoneMarket);

        zoneMarket.addStoreToManager(sdmStore,manager);
        managers.addManager(manager);
    }
    public synchronized void addManager(String name, File file) throws JAXBException {
        if (KManagerNameVManger.containsKey(name)){
            throw new RuntimeException("the manager already in the system");
        }
        Manager manager = new Manager(name);
        addZoneMarket(file,manager);
    }
    public synchronized void addCustomer(String name){
        if (KCustomerNameVCustomer.containsKey(name)){
            throw new RuntimeException("the Customer already in the system");
        }
        Customer customer = new Customer(name);
        KCustomerNameVCustomer.put(name,customer);
    }
    private synchronized void customerMakeTransaction(Action action,Customer customer, Date date, Double transactionAmount){
        TransactionDTO transaction = Action.invokeAction(action,customer.getCustomerDTO().getMoney(),transactionAmount,date);
        customer.addTransaction(transaction);
    }
    public void customerMakeDeposit(String customerName, Date date,Double transactionAmount){
        Customer customer = KCustomerNameVCustomer.get(customerName);
        customerMakeTransaction(Action.DEPOSIT,customer,date,transactionAmount);
    }

    //////method incomplete

    //////////method private

    //////////method public
    public void addOrder(String zoneName,String customerName, OrderDTO orderDTO){//zoneName, customerName from headers
        Customer customer = KCustomerNameVCustomer.get(customerName);
        ZoneMarket zoneMarket = KNameZoneVZone.get(zoneName);
        Managers managers = KZoneVManagers.get(zoneMarket);
        Double transactionAmount = orderDTO.getTotalDeliveryPrice() + orderDTO.getProductsPrice();

        customerMakeTransaction(Action.TRANSFER,customer,orderDTO.getDate(),transactionAmount);
        customer.addOrderId(zoneName,orderDTO.getId());

        Order order = managers.addOrder(orderDTO);
        zoneMarket.addOrder(order);
    }
    public OrderDTO getMinOrder(String zoneName,OrderDTO orderDTO, Map<SDMItem,ProductDTO> KProductInfoVProductDTO){
        ZoneMarket zoneMarket = KNameZoneVZone.get(zoneName);
        Managers managers = KZoneVManagers.get(zoneMarket);

        return managers.getMinOrder(orderDTO, KProductInfoVProductDTO);
    }



}
