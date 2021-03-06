package market;

import dto.*;
import order.Action;
import order.Order;
import position.Customer;
import position.Manager;
import position.Managers;
import store.Store;
import xmlBuild.SchemaBaseJaxbObjects;
import xmlBuild.schema.generated.Location;
import xmlBuild.schema.generated.SDMDiscount;
import xmlBuild.schema.generated.SDMItem;
import xmlBuild.schema.generated.SDMStore;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public final class Market {
    private static volatile Market marketInstance;

    private final Map<String, ZoneMarket> KNameZoneVZone;
    private final Map<ZoneMarket, Managers> KZoneVManagers;
    private final Map<String, Manager> KManagerNameVManger;
    private final Map<String, Customer> KCustomerNameVCustomer;

    //c'tor - instance
    private Market() {
        KNameZoneVZone = new HashMap<>();
        KZoneVManagers = new HashMap<>();
        KManagerNameVManger = new HashMap<>();
        KCustomerNameVCustomer = new HashMap<>();
    }

    public static Market getMarketInstance() {
        Market market = marketInstance;
        if (market != null) {
            return market;
        }
        synchronized (Market.class) {
            if (marketInstance == null) {
                marketInstance = new Market();
            }
        }

        return marketInstance;
    }


    // notifications

    private final Map<String, List<AlertDTO>> notificationsForUser = new ConcurrentHashMap<>();

    public synchronized List<AlertDTO> getLastStoresNotificationsForUser(String userName) {
        List<AlertDTO> notifications = notificationsForUser.get(userName);
        List<AlertDTO> result = new LinkedList<>(notifications);
        notifications.clear();
        return result;
    }

    private void addToHandle(Map<String, List<AlertDTO>> newNotificationsForUser) {
        for (Map.Entry<String, List<AlertDTO>> entry : newNotificationsForUser.entrySet()) {
            if (!notificationsForUser.containsKey(entry.getKey())){
                notificationsForUser.put(entry.getKey(), new LinkedList<>());
            }

            notificationsForUser.get(entry.getKey()).addAll(entry.getValue());
        }
    }

    private void handleStoreAdded(String storeManagerName, String zoneName, StoreDTO store) {
        ZoneMarket zone = KNameZoneVZone.get(zoneName);
        Map<String, List<AlertDTO>> newNotificationsForUser = new ConcurrentHashMap<>();
        KZoneVManagers
                .get(zone)
                .getManagerNames()
                .stream()
                .filter(managerName -> !storeManagerName.equals(managerName))
                .forEach(managerName -> {
                    if (!newNotificationsForUser.containsKey(managerName)) {
                        newNotificationsForUser.put(managerName, new LinkedList<>());
                    }

                    newNotificationsForUser.get(managerName).add(new AlertDTO(store));
                });

        addToHandle(newNotificationsForUser);
    }


    private void handleOrderAdded(String zoneName, OrderDTO orderDTO) {
        ZoneMarket zone = KNameZoneVZone.get(zoneName);
        Managers managers = KZoneVManagers.get(zone);
        Map<String, List<AlertDTO>> newNotificationsForUser = managers.getManagerNamesToSubOrder(orderDTO.getKStoreIdVSubOrder());
        addToHandle(newNotificationsForUser);
    }


    private void handleFeedbackAdded(String zoneName, Integer storeId, FeedbackDTO feedbackDTO) {
        ZoneMarket zoneMarket = KNameZoneVZone.get(zoneName);
        Managers managers = KZoneVManagers.get(zoneMarket);
        StoreDTO store = managers.getStoreById(storeId).getStoreDTO();
        String managerName = store.getStoreOwnerName();
        Map<String, List<AlertDTO>> newNotificationsForUser = new ConcurrentHashMap<>();
        newNotificationsForUser.put(managerName, new LinkedList<>());
        newNotificationsForUser.get(managerName).add(new AlertDTO(feedbackDTO));
        addToHandle(newNotificationsForUser);
    }


    //get
    public Map<String, String> getNamePosition() {
        Map<String, String> KNameVPosition = new HashMap<>();
        for (Customer customer : KCustomerNameVCustomer.values()) {
            KNameVPosition.put(customer.getCustomerDTO().getName(), "Customer");
        }

        for (Manager manager : KManagerNameVManger.values()) {
            KNameVPosition.put(manager.getName(), "Manager");
        }

        return KNameVPosition;
    }

    public List<ZoneMarketDTO> getZonesMarketDTO() {
        List<ZoneMarketDTO> zoneMarketDTOs = new ArrayList<>();
        KZoneVManagers.keySet().forEach(zoneMarket -> zoneMarketDTOs.add(zoneMarket.getZoneMarketDTO()));
        return zoneMarketDTOs;
    }

    public ZoneMarketDTO getZoneMarketDTO(String name) {
        return KNameZoneVZone.get(name).getZoneMarketDTO();
    }

    public CustomerDTO getCustomerDTO(String name) {
        return KCustomerNameVCustomer.get(name).getCustomerDTO();
    }

    public ManagerDTO getManagerDTO(String name) {
        return KManagerNameVManger.get(name).getManagerDTO();
    }

    public ManagerDTO getManagerDTOZone(String name, String zone) {
        return KManagerNameVManger.get(name).getManagerDTO(zone);
    }

    public List<OrderDTO> getOrders() {
        List<OrderDTO> ordersDTO = new ArrayList<>();
        for (ZoneMarket zoneMarket : KNameZoneVZone.values()) {
            ordersDTO.addAll(zoneMarket.getOrdersDTO());
        }

        return ordersDTO;
    }

    public List<OrderDTO> getOrdersByZoneAndIds(String name, List<Integer> ordersId) {
        ZoneMarket zoneMarket = KNameZoneVZone.get(name);
        return zoneMarket.getOrdersDTOByIds(ordersId);
    }

    public List<OrderDTO> getOrdersByCustomerNameInZone(String customerName, String zoneName) {
        Customer customer = KCustomerNameVCustomer.get(customerName);
        return customer.getListOrdersByZoneName(zoneName).
                stream().
                map(Order::getOrderDTO).
                collect(Collectors.toList());
    }

    public Map<Integer, SubOrderDTO> getSubOrderDTOByZoneAndStoreId(String zoneName, Integer storeId) {
        ZoneMarket zoneMarket = KNameZoneVZone.get(zoneName);
        Managers managers = KZoneVManagers.get(zoneMarket);
        Store store = managers.getStoreById(storeId);
        return store.getStoreDTO().getKIdOrderVSubOrderDTO();
    }

    public List<TransactionDTO> getUserTransactionsDTO(String userName) {
//        List<TransactionDTO> transactions = new ArrayList<>();
//        Date date = new Date();
//        TransactionDTO transactionDTO = new TransactionDTO("Deposit",date,100.0,100.0,200.0);
//        transactions.add(transactionDTO);
//        transactionDTO = new TransactionDTO("Deposit",date,150.0,150.0,300.0);
//        transactions.add(transactionDTO);
//        return transactions;
        if (KManagerNameVManger.containsKey(userName)) {
            return KManagerNameVManger.get(userName).getManagerDTO().getTransactions();
        }

        return KCustomerNameVCustomer.get(userName).getCustomerDTO().getTransactions();
    }

    public StoreDTO getStoreDTO(String zoneName, Integer storeId) {
        ZoneMarket zoneMarket = KNameZoneVZone.get(zoneName);
        Managers managers = KZoneVManagers.get(zoneMarket);
        return managers.getStoreDTO(storeId);
    }

    public List<StoreDTO> getStoresDTO(String zoneName) {
        ZoneMarket zoneMarket = KNameZoneVZone.get(zoneName);
        Managers managers = KZoneVManagers.get(zoneMarket);
        return managers.getStoresDTO();

    }

    public List<Location> getStoresLocation(String zoneName) {
        List<StoreDTO> stores = getStoresDTO(zoneName);
        return stores.stream().map(StoreDTO::getSdmStore).map(SDMStore::getLocation).collect(Collectors.toList());
    }

    public Map<SDMDiscount, Integer> getStoresDiscounts(String zoneName, Set<Integer> storesId) {
        ZoneMarket zoneMarket = KNameZoneVZone.get(zoneName);
        Managers managers = KZoneVManagers.get(zoneMarket);
        return managers.getStoresDiscounts(storesId);
    }

    public OrderDTO getOrderByZoneAndOrderId(String zoneName, Integer orderID) {
        ZoneMarket zoneMarket = KNameZoneVZone.get(zoneName);
        return zoneMarket.getOrderDTOById(orderID);
    }

    //////////method private
    private synchronized void addZoneMarket(File file, Manager manager) {
        SchemaBaseJaxbObjects schema = new SchemaBaseJaxbObjects(file);
        if (KNameZoneVZone.containsKey(schema.getXmlZoneMarket().getSDMZone().getName())) {
            throw new IllegalArgumentException("the zone with the same name already exist");
        }

        ZoneMarket zoneMarket = new ZoneMarket(schema, manager);
        Managers managers = new Managers(zoneMarket.getZoneName());


        KZoneVManagers.put(zoneMarket, managers);
        KNameZoneVZone.put(zoneMarket.getZoneName(), zoneMarket);
        managers.addManager(manager);
    }

    private synchronized void customerMakeTransaction(Action action, Customer customer, Date date, Double transactionAmount) {
        TransactionDTO transaction = Action.invokeAction(action, customer.getCustomerDTO().getMoney(), transactionAmount, date);
        customer.addTransaction(transaction);
    }

    //////////method public
    public StoreDTO addStoreToManager(String managerName, String zoneName, SDMStore sdmStore) throws IllegalStateException {
        Manager manager = KManagerNameVManger.get(managerName);
        ZoneMarket zoneMarket = KNameZoneVZone.get(zoneName);
        Managers managers = KZoneVManagers.get(zoneMarket);

        if (getStoresLocation(zoneName).stream().anyMatch(location -> location.getX() == sdmStore.getLocation().getX() && location.getY() == sdmStore.getLocation().getY())) {
            throw new IllegalStateException("the Location is already taken by anther store");
        }

        StoreDTO storeDTO = zoneMarket.addStoreToManager(sdmStore, manager, zoneName);
        managers.addManager(manager);

        handleStoreAdded(managerName, zoneName, storeDTO);
        return storeDTO;
    }

    public synchronized void addManager(String name, File file) {
        Manager manager = null;
        if (KManagerNameVManger.containsKey(name)) {
            manager = KManagerNameVManger.get(name);
        } else {
            manager = new Manager(name);
        }
        addZoneMarket(file, manager);
        KManagerNameVManger.put(name, manager);
    }

    public synchronized void addCustomer(String name) {
        if (KCustomerNameVCustomer.containsKey(name)) {
            throw new RuntimeException("the Customer already in the system");
        }
        Customer customer = new Customer(name);
        KCustomerNameVCustomer.put(name, customer);
    }

    public void customerMakeDeposit(String customerName, Date date, Double transactionAmount) {
        Customer customer = KCustomerNameVCustomer.get(customerName);
        customerMakeTransaction(Action.DEPOSIT, customer, date, transactionAmount);
    }

    public void addOrder(String zoneName, String customerName, OrderDTO orderDTO) {//zoneName, customerName from headers
        Customer customer = KCustomerNameVCustomer.get(customerName);
        ZoneMarket zoneMarket = KNameZoneVZone.get(zoneName);
        Managers managers = KZoneVManagers.get(zoneMarket);
        Double transactionAmount = orderDTO.getTotalDeliveryPrice() + orderDTO.getProductsPrice();

        customerMakeTransaction(Action.TRANSFER, customer, orderDTO.getDate(), transactionAmount);

        Order order = managers.addOrder(orderDTO);
        customer.addOrder(zoneName, order);
        zoneMarket.addOrder(order);
        handleOrderAdded(zoneName, order.getOrderDTO());
    }

    public OrderDTO getMinOrder(String zoneName, OrderDTO orderDTO, Map<SDMItem, ProductDTO> KProductInfoVProductDTO) {
        ZoneMarket zoneMarket = KNameZoneVZone.get(zoneName);
        Managers managers = KZoneVManagers.get(zoneMarket);
        Map<Integer, Integer> KProductIdVStore = managers.getProductIdToChipsetStore(KProductInfoVProductDTO);

        return managers.getOrderDTO(orderDTO, KProductInfoVProductDTO, KProductIdVStore);
    }

    public OrderDTO getOrderDTO(String zoneName, OrderDTO orderDTO, Map<SDMItem, ProductDTO> KProductInfoVProductDTO, Map<Integer, Integer> KProductIdVStore) {
        ZoneMarket zoneMarket = KNameZoneVZone.get(zoneName);
        Managers managers = KZoneVManagers.get(zoneMarket);

        return managers.getOrderDTO(orderDTO, KProductInfoVProductDTO, KProductIdVStore);
    }

    public Boolean isUserExist(String name) {
        return KCustomerNameVCustomer.containsKey(name) || KManagerNameVManger.containsKey(name);
    }

    public void addFeedbackDTO(String zoneName, Integer storeId, FeedbackDTO feedbackDTO) {
        ZoneMarket zoneMarket = KNameZoneVZone.get(zoneName);
        Managers managers = KZoneVManagers.get(zoneMarket);
        managers.addFeedbackDTO(storeId, feedbackDTO);
        handleFeedbackAdded(zoneName, storeId, feedbackDTO);
    }

    public List<FeedbackDTO> getManagerFeedbacksInZone(String zoneName, String managerName) {
        Manager manager = KManagerNameVManger.get(managerName);
        return manager.getFeedbacksInZone(zoneName);
    }
}
