package position;


import dto.TransactionDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Customer {
    private String name;
    private Double money = 0.0;
    private Map<String,List<Integer>> KZoneNameVListOrderIds = new HashMap<>();
    private List<TransactionDTO> depositTransactions = new ArrayList<>();

    public String getName() {
        return name;
    }
    public Double getMoney() {
        return money;
    }
    public Map<String, List<Integer>> getKZoneNameVListOrderIds() {
        return KZoneNameVListOrderIds;
    }
    public List<TransactionDTO> getDepositTransactions() {
        return depositTransactions;
    }

    public Customer(String name) {
        this.name = name;
        this.money = 0.0;
    }
}
