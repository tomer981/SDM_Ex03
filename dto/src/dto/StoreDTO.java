package dto;

import xmlBuild.schema.generated.*;


import java.util.HashMap;
import java.util.Map;

public class StoreDTO implements Cloneable{
    private SDMStore sdmStore;//1,2,4,8 getId,getName(),getLocation(),getPpk()
    private String StoreOwnerName;//3 - getStoreOwnerName
    private Map<Integer, SubOrderDTO> KIdOrderVSubOrderDTO;//6 - getSize
    private Map<SDMItem, ProductDTO> KProductIdVPriceAndAmount = new HashMap<>();

    private Double moneyEarnFromProducts;//getMoneyEarnFromProducts
    private Double moneyEarnFromDelivery;//getMoneyEarnFromDelivery

    @Override
    public Object clone() throws CloneNotSupportedException {
        return (StoreDTO)super.clone();
    }



    public SDMStore getSdmStore() {
        return sdmStore;
    }
    public String getStoreOwnerName() {
        return StoreOwnerName;
    }
    public Map<Integer, SubOrderDTO> getKIdOrderVSubOrderDTO() {
        return KIdOrderVSubOrderDTO;
    }
    public Map<SDMItem, ProductDTO> getKProductIdVPriceAndAmount() {
        return KProductIdVPriceAndAmount;
    }
    public Double getMoneyEarnFromProducts() {
        return moneyEarnFromProducts;
    }
    public Double getMoneyEarnFromDelivery() {
        return moneyEarnFromDelivery;
    }

    public StoreDTO(SDMStore sdmStore, String storeOwnerName, Map<Integer, SubOrderDTO> KIdOrderVSubOrderDTO, Map<SDMItem, ProductDTO> KProductIdVPriceAndAmount, Double moneyEarnFromProducts, Double moneyEarnFromDelivery) {
        this.sdmStore = sdmStore;
        StoreOwnerName = storeOwnerName;
        this.KIdOrderVSubOrderDTO = KIdOrderVSubOrderDTO;
        this.KProductIdVPriceAndAmount = KProductIdVPriceAndAmount;
        this.moneyEarnFromProducts = moneyEarnFromProducts;
        this.moneyEarnFromDelivery = moneyEarnFromDelivery;
    }
}
