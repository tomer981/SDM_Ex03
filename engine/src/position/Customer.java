package position;


import dto.CustomerDTO;
import dto.TransactionDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Customer {
    private CustomerDTO customerDTO;

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

    public void addOrderId(String zoneName, Integer id) {
        Map<String, List<Integer>> KZoneVOrdersId = customerDTO.getKZoneNameVListOrderIds();
        List<Integer> ordersId = KZoneVOrdersId.get(zoneName);
        ordersId.add(id);
        KZoneVOrdersId.put(zoneName,ordersId);
        customerDTO.setKZoneNameVListOrderIds(KZoneVOrdersId);
    }
}
