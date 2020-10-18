package dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerDTO {
    private String name;
    private Double money = 0.0;
    private Map<String, List<Integer>> KZoneNameVListOrderIds = new HashMap<>();
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
    public void setName(String name) {
        this.name = name;
    }
    public void setMoney(Double money) {
        this.money = money;
    }
    public void setKZoneNameVListOrderIds(Map<String, List<Integer>> KZoneNameVListOrderIds) {
        this.KZoneNameVListOrderIds = KZoneNameVListOrderIds;
    }
    public void setTransactions(List<TransactionDTO> transactions) {
        Transactions = transactions;
    }

    //set
    //c'tor
    public CustomerDTO(String name) {
        this.name = name;
    }

}
