package position;


import dto.CustomerDTO;
import dto.TransactionDTO;
import order.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Customer {
    private CustomerDTO customerDTO;

    private List<Order> orders = new ArrayList<>();

    public Customer(String name) {
        customerDTO = new CustomerDTO(name);
    }

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
        orders.add(order);
        Map<String, List<Integer>> KZoneVOrdersId = customerDTO.getKZoneNameVListOrderIds();
        if (!KZoneVOrdersId.containsKey(zoneName)){
            KZoneVOrdersId.put(zoneName, new ArrayList<>());
        }

        List<Integer> ordersId = KZoneVOrdersId.get(zoneName);
        ordersId.add(order.getOrderDTO().getId());
        KZoneVOrdersId.put(zoneName,ordersId);
        customerDTO.setKZoneNameVListOrderIds(KZoneVOrdersId);
    }

    public List<Order> getOrders() {
        return orders;
    }
}
