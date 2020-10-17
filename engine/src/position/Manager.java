package position;

import dto.ManagerDTO;
import dto.StoreDTO;
import dto.TransactionDTO;
import store.Store;
import xml.schema.generated.SDMItem;
import xml.schema.generated.SDMItems;
import xml.schema.generated.SDMStore;

import java.util.*;

public class Manager {
    private final String name;
    private Double money = 0.0;
    private final Map<String, List<Store>> KZoneNameVStores = new HashMap<>();
    private final List<TransactionDTO> transactions = new ArrayList<>();

    //get
    public Double getMoney() {
        return money;
    }
    public Map<String, List<Store>> getKZoneNameVStores() {
        return KZoneNameVStores;
    }
    public String getName() {
        return name;
    }



    private List<StoreDTO> getStoresDTOByZone(String zoneName) {
        List<Store> storesZone =  KZoneNameVStores.get(zoneName);
        List<StoreDTO> storesZoneDTO = new ArrayList<>();

        for(Store store : storesZone){
            StoreDTO storeDTO = store.getStoreDTO();
            storesZoneDTO.add(storeDTO);
        }

        return storesZoneDTO;
    }
    public ManagerDTO getManagerDTO(){
        Map<String, List<StoreDTO>> KZoneNameVStoresDTO = new HashMap<>();
        for (String zoneName : KZoneNameVStores.keySet()){
            KZoneNameVStoresDTO.put(zoneName,getStoresDTOByZone(zoneName));
        }

        return new ManagerDTO(name,money,KZoneNameVStoresDTO,transactions);
    }

    public ManagerDTO getManagerDTO(String zoneName){
        Map<String, List<StoreDTO>> KZoneNameVStoresDTO = new HashMap<>();
        KZoneNameVStoresDTO.put(zoneName,getStoresDTOByZone(zoneName));

        return new ManagerDTO(name,money,KZoneNameVStoresDTO,transactions);
    }

    //c'tor
    public Manager(String name) {
        this.name = name;
    }

    //methods
    public void addStoreToManager(String zone, SDMStore sdmStore, SDMItems productsInfo) {
        if (!KZoneNameVStores.containsKey(zone)){
            KZoneNameVStores.put(zone,new ArrayList());
        }

        Store store = new Store(sdmStore, name,productsInfo);
        List<Store> stores = KZoneNameVStores.get(zone);
        stores.add(store);
        KZoneNameVStores.put(zone,stores);
    }
    public Integer getNumberOfStoreSellProductByZone(SDMItem product, String zoneInfo) {
        List<Store> stores = KZoneNameVStores.get(zoneInfo);
        int numberOfStoreSellProduct = 0;
        for (Store store : stores){
            numberOfStoreSellProduct = (store.isProductSold(product)) ? ++numberOfStoreSellProduct : numberOfStoreSellProduct;
        }

        return numberOfStoreSellProduct;
    }
    public Double getTotalCostProductByZone(SDMItem product, String zoneInfo) {
        List<Store> stores = KZoneNameVStores.get(zoneInfo);
        Double totalCostProductByZone = 0.0;

        for (Store store : stores){{
            if (store.isProductSold(product)){
                totalCostProductByZone += store.getProductPrice(product);
            }
        }}

        return totalCostProductByZone;
    }
    public Double getTotalAmountProductSoldByZone(SDMItem product, String zoneInfo) {
        List<Store> stores = KZoneNameVStores.get(zoneInfo);
        Double totalAmountProductSold = 0.0;
        for (Store store : stores){
            totalAmountProductSold += store.getTotalAmountProductSold(product);
        }

        return totalAmountProductSold;
    }

    public void addTransaction(TransactionDTO transaction) {
        transactions.add(transaction);
        money = transaction.getMoneyAfterTransaction();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Manager)) return false;
        Manager manager = (Manager) o;
        return Objects.equals(name, manager.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }


}
