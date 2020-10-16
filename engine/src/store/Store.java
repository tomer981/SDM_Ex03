package store;


import dto.StoreDTO;
import dto.SubOrderDTO;
import order.SubOrder;
import xml.schema.generated.Location;
import xml.schema.generated.SDMItem;
import xml.schema.generated.SDMSell;
import xml.schema.generated.SDMStore;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Store {
    private String StoreOwnerName;
    private SDMStore storeInfo;
    private Map<Integer, SubOrder> KIdOrderVSubOrder = new HashMap<>();


    public String getStoreOwnerName() {
        return StoreOwnerName;
    }
    public SDMStore getStoreInfo() {
        return storeInfo;
    }
    public Map<Integer, SubOrder> getKIdOrderVSubOrder() {
        return KIdOrderVSubOrder;
    }


    public Store(SDMStore storeInfo, String StoreOwnerName) {
        this.storeInfo = storeInfo;
        this.StoreOwnerName = StoreOwnerName;
    }


    public StoreDTO getStoreDTO(){
        //storeInfo 1-2,4,8 maybe 5
        //StoreOwnerName 3
        Map<Integer, SubOrderDTO> KIdOrderVSubOrderDTO = new HashMap<>();//6
        Double moneyEarnFromProducts = 0.0;//7
        Double moneyEarnFromDelivery = 0.0;//9

        for (SubOrder subOrder : KIdOrderVSubOrder.values()){
            SubOrderDTO subOrderDTO = subOrder.getSubOrderDTO();
            KIdOrderVSubOrderDTO.put(subOrderDTO.getId(), subOrderDTO);
        }

        return new StoreDTO(storeInfo, StoreOwnerName, KIdOrderVSubOrderDTO ,moneyEarnFromProducts ,moneyEarnFromDelivery);
    }

    public boolean isProductSold(SDMItem product) {
        return storeInfo.getSDMPrices().getSDMSell().stream()
                .map(SDMSell::getItemId).collect(Collectors.toList()).stream()
                .anyMatch(productId-> productId.equals(product.getId()));
    }
    public Double getProductPrice(SDMItem product) {
        SDMSell storeProduct = storeInfo.getSDMPrices().getSDMSell().stream().
                filter(storePrice->storePrice.getItemId() == product.getId()).findFirst().orElse(null);
        return (double)storeProduct.getPrice();
    }

    public Double getTotalAmountProductSold(SDMItem product) {
        Double amountProductSold = 0.0;
        for (SubOrder subOrder : KIdOrderVSubOrder.values()){
            if (subOrder.isProductSold(product)){
                amountProductSold += subOrder.getAmountSold(product);
            }
        }

        return amountProductSold;
    }

    public void addSubOrder(SubOrder subOrder) {
        KIdOrderVSubOrder.put(subOrder.getSubOrderDTO().getId(), subOrder);
        Location customerLocation =  subOrder.getSubOrderDTO().getCustomerLocation();
        Double distance = SubOrder.getDistance(storeInfo.getLocation(),customerLocation);
        Double deliveryCost = distance * storeInfo.getDeliveryPpk();
        subOrder.getSubOrderDTO().setDeliveryCost(deliveryCost);
    }
}
