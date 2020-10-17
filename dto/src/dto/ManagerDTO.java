package dto;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ManagerDTO {
    private final String name;
    private Double money;
    private final Map<String, List<StoreDTO>> KStoreIdVStore;
    private List<TransactionDTO> transactions = new ArrayList<>();


    public String getName() {
        return name;
    }
    public Double getMoney() {
        return money;
    }
    public Map<String, List<StoreDTO>> getKStoreIdVStore() {
        return KStoreIdVStore;
    }
    public List<TransactionDTO> getTransactions() {
        return transactions;
    }

    public ManagerDTO(String name, Double money, Map<String, List<StoreDTO>> KStoreIdVStore, List<TransactionDTO> transactions) {
        this.name = name;
        this.money = money;
        this.KStoreIdVStore = KStoreIdVStore;
        this.transactions = transactions;
    }
}
