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
    public static Market getMarketInstance(){
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
            KNameVPosition.put(manager.getName(), "Manager");
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
    public CustomerDTO getCustomerDTO(String name){
        return KCustomerNameVCustomer.get(name).getCustomerDTO();
    }
    public ManagerDTO getManagerDTO(String name){ return KManagerNameVManger.get(name).getManagerDTO();}
    public ManagerDTO getManagerDTOZone(String name, String zone){ return KManagerNameVManger.get(name).getManagerDTO(zone);}
    public List<OrderDTO> getOrder(){
        List<OrderDTO> ordersDTO = new ArrayList<>();
        for (ZoneMarket zoneMarket : KNameZoneVZone.values()){
            ordersDTO.addAll(zoneMarket.getOrdersDTO());
        }

        return ordersDTO;
    }
    public List<OrderDTO> getOrdersByZoneAndIds(String name, List<Integer> ordersId){
        ZoneMarket zoneMarket = KNameZoneVZone.get(name);
        return zoneMarket.getOrdersDTOByIds(ordersId);
    }
    public List<TransactionDTO> getUserTransactionsDTO(String userName){
//        List<TransactionDTO> transactions = new ArrayList<>();
//        Date date = new Date();
//        TransactionDTO transactionDTO = new TransactionDTO("Deposit",date,100.0,100.0,200.0);
//        transactions.add(transactionDTO);
//        transactionDTO = new TransactionDTO("Deposit",date,150.0,150.0,300.0);
//        transactions.add(transactionDTO);
//        return transactions;
        if (KManagerNameVManger.containsKey(userName)){
            return KManagerNameVManger.get(userName).getManagerDTO().getTransactions();
        }

        return KCustomerNameVCustomer.get(userName).getCustomerDTO().getTransactions();
    }
    public StoreDTO getStoreDTO(String zoneName, Integer storeId){
        ZoneMarket zoneMarket = KNameZoneVZone.get(zoneName);
        Managers managers = KZoneVManagers.get(zoneMarket);
        return managers.getStoreDTO(storeId);
    }

    //////////method private
    private synchronized void addZoneMarket(File file, Manager manager) {
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
    private synchronized void customerMakeTransaction(Action action,Customer customer, Date date, Double transactionAmount){
        TransactionDTO transaction = Action.invokeAction(action,customer.getCustomerDTO().getMoney(),transactionAmount,date);
        customer.addTransaction(transaction);
    }

    //////////method public
    public void addStoreToManager(String managerName, String zoneName, SDMStore sdmStore){
        Manager manager = KManagerNameVManger.get(managerName);
        ZoneMarket zoneMarket = KNameZoneVZone.get(managerName);
        Managers managers = KZoneVManagers.get(zoneMarket);

        zoneMarket.addStoreToManager(sdmStore,manager);
        managers.addManager(manager);
    }
    public synchronized void addManager(String name, File file) {
        Manager manager = null;
        if (KManagerNameVManger.containsKey(name)){
            manager = KManagerNameVManger.get(name);
        }
        else{
            manager = new Manager(name);
        }
        addZoneMarket(file,manager);
        KManagerNameVManger.put(name,manager);
    }
    public synchronized void addCustomer(String name){
        if (KCustomerNameVCustomer.containsKey(name)){
            throw new RuntimeException("the Customer already in the system");
        }
        Customer customer = new Customer(name);
        KCustomerNameVCustomer.put(name,customer);
    }
    public void customerMakeDeposit(String customerName, Date date,Double transactionAmount){
        Customer customer = KCustomerNameVCustomer.get(customerName);
        customerMakeTransaction(Action.DEPOSIT,customer,date,transactionAmount);
    }
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
    public Boolean isUserExist(String name){
        return KCustomerNameVCustomer.containsKey(name) || KManagerNameVManger.containsKey(name);
    }
}
