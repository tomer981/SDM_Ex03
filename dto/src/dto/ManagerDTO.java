package dto;
import java.util.List;
import java.util.Map;

public class ManagerDTO {
    private String name;
    private Double money;
    private Map<String, List<StoreDTO>> KStoreIdVStore;

    public String getName() {
        return name;
    }
    public Double getMoney() {
        return money;
    }
    public Map<String, List<StoreDTO>> getKStoreIdVStore() {
        return KStoreIdVStore;
    }

    public ManagerDTO(String name, Double money, Map<String, List<StoreDTO>> KStoreIdVStore) {
        this.name = name;
        this.money = money;
        this.KStoreIdVStore = KStoreIdVStore;
    }
}
