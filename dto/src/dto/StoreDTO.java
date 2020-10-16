package dto;

import xml.schema.generated.SDMStore;

import java.util.Map;

public class StoreDTO {
    private SDMStore sdmStore;//1,2,4,8 getId,getName(),getLocation(),getPpk()
    private String StoreOwnerName;//3 - getStoreOwnerName
    private Map<Integer, SubOrderDTO> kIdOrderVSubOrderDTO;//6 - getSize
    private Double moneyEarnFromProducts;//getMoneyEarnFromProducts
    private Double moneyEarnFromDelivery;//getMoneyEarnFromDelivery

    public StoreDTO(SDMStore sdmStore, String storeOwnerName, Map<Integer, SubOrderDTO> kIdOrderVSubOrderDTO, Double moneyEarnFromProducts, Double moneyEarnFromDelivery) {
        this.sdmStore = sdmStore;
        StoreOwnerName = storeOwnerName;
        this.kIdOrderVSubOrderDTO = kIdOrderVSubOrderDTO;
        this.moneyEarnFromProducts = moneyEarnFromProducts;
        this.moneyEarnFromDelivery = moneyEarnFromDelivery;
    }


//private dto.OrderDTO orders;
}
