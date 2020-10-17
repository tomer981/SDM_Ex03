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
    private List<TransactionDTO> Transactions = new ArrayList<>();

    //get
    public String getName() {
        return name;
    }
    public Double getMoney() {
        return money;
    }
    public Map<String, List<Integer>> getKZoneNameVListOrderIds() {
        return KZoneNameVListOrderIds;
    }
    public List<TransactionDTO> getTransactions() {
        return Transactions;
    }

    //set
    public void setMoney(Double money) {
        this.money = money;
    }


    public Customer(String name) {
        this.name = name;
        this.money = 0.0;
    }

    public void addTransaction(TransactionDTO transaction) {
        Transactions.add(transaction);
        money = transaction.getMoneyAfterTransaction();
    }

    public void addOrderId(String zoneName, Integer id) {
        List<Integer> ordersId = KZoneNameVListOrderIds.get(zoneName);
        ordersId.add(id);
        KZoneNameVListOrderIds.put(zoneName,ordersId);
    }
}
