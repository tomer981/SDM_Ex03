package position;


import dto.CustomerDTO;
import dto.TransactionDTO;
import order.Order;

import java.util.*;
import java.util.stream.Collectors;

public class Customer {
    private CustomerDTO customerDTO;

    public Customer(String name) {
        customerDTO = new CustomerDTO(name);
    }
    private Map<String, List<Order>> KZoneNameVListOrders = new HashMap<>();


    public CustomerDTO getCustomerDTO() {
        return customerDTO;
    }
    public void setCustomerDTO(CustomerDTO customerDTO) {
        this.customerDTO = customerDTO;
    }

    public void addTransaction(TransactionDTO transaction) {
        List<TransactionDTO> transactionDTOS = customerDTO.getTransactions();
        transactionDTOS.add(transaction);
        customerDTO.setMoney(transaction.getMoneyAfterTransaction());
        customerDTO.setTransactions(transactionDTOS);
    }

    public void addOrder(String zoneName, Order order) {
        if (!KZoneNameVListOrders.containsKey(zoneName)){
            KZoneNameVListOrders.put(zoneName,new ArrayList<>());
        }
        KZoneNameVListOrders.get(zoneName).add(order);
        Map<String, List<Integer>> KZoneVOrdersId = customerDTO.getKZoneNameVListOrderIds();
        if (!KZoneVOrdersId.containsKey(zoneName)){
            KZoneVOrdersId.put(zoneName, new ArrayList<>());
        }

        List<Integer> ordersId = KZoneVOrdersId.get(zoneName);
        ordersId.add(order.getOrderDTO().getId());
        KZoneVOrdersId.put(zoneName,ordersId);
        customerDTO.setKZoneNameVListOrderIds(KZoneVOrdersId);
    }

    public Map<String, List<Order>> getKZoneNameVListOrders() {
        return KZoneNameVListOrders;
    }

    public List<Order> getListOrdersByZoneName(String zoneName) {
        return KZoneNameVListOrders.get(zoneName);
    }


    public List<Order> getOrders() {
        return KZoneNameVListOrders.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }
}
