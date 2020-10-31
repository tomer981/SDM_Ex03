package store;


import dto.ProductDTO;
import dto.StoreDTO;
import dto.SubOrderDTO;
import order.SubOrder;
import xmlBuild.schema.generated.*;

import java.util.HashMap;
import java.util.Map;

public class Store {
    private String StoreOwnerName;
    private SDMStore storeInfo;
    private Map<Integer, SubOrder> KIdOrderVSubOrder = new HashMap<>();
    private Map<SDMItem, ProductDTO> KProductInfoVProductAmount = new HashMap<>();

    public String getStoreOwnerName() {
        return StoreOwnerName;
    }
    public SDMStore getStoreInfo() {
        return storeInfo;
    }
    public Map<Integer, SubOrder> getKIdOrderVSubOrder() {
        return KIdOrderVSubOrder;
    }

    public Store(SDMStore storeInfo, String StoreOwnerName,SDMItems products) {
        this.storeInfo = storeInfo;
        this.StoreOwnerName = StoreOwnerName;
        for (SDMItem product : products.getSDMItem()){
            if (isProductSold(product)){
                ProductDTO productDTO = new ProductDTO();
                productDTO.setPrice(getProductPrice(product));
                productDTO.setAmount(0.0);
                KProductInfoVProductAmount.put(product,productDTO);
            }
        }
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
            moneyEarnFromProducts += subOrderDTO.getProductsPrice();
            moneyEarnFromDelivery += subOrderDTO.getDeliveryPrice();
        }

        return new StoreDTO(storeInfo, StoreOwnerName, KIdOrderVSubOrderDTO ,KProductInfoVProductAmount,moneyEarnFromProducts ,moneyEarnFromDelivery);
    }

    private boolean isProductSold(Integer productId){
        return storeInfo.getSDMPrices().getSDMSell().stream()
                .anyMatch(product -> product.getItemId() == productId);
    }
    public boolean isProductSold(SDMItem product) {
        return isProductSold(product.getId());
    }
    private Double getProductPrice(Integer productId){

        SDMSell storeProduct = storeInfo.getSDMPrices().getSDMSell().stream().
                filter(storePrice->storePrice.getItemId() == productId).findFirst().orElse(null);
        return (double)storeProduct.getPrice();

    }
    public Double getProductPrice(SDMItem product) {
        return getProductPrice(product.getId());
    }

    private Double getTotalAmountProductSold(Integer productId){
        Double amountProductSold = 0.0;
        for (SubOrder subOrder : KIdOrderVSubOrder.values()){
            if (subOrder.isProductSold(productId)){
                amountProductSold += subOrder.getAmountSold(productId);
            }
        }

        return amountProductSold;
    }
    public Double getTotalAmountProductSold(SDMItem product) {
        return getTotalAmountProductSold(product.getId());
    }

    public void addSubOrder(SubOrder subOrder) {
        KIdOrderVSubOrder.put(subOrder.getSubOrderDTO().getId(), subOrder);

        Location customerLocation =  subOrder.getSubOrderDTO().getCustomerLocation();
        Double distance = SubOrder.getDistance(storeInfo.getLocation(),customerLocation);
        Double deliveryCost = distance * storeInfo.getDeliveryPpk();
        subOrder.getSubOrderDTO().setDeliveryCost(deliveryCost);

        updateAmount(subOrder);

    }

    private void updateAmount(SubOrder subOrder) {
        Map<SDMItem, ProductDTO> KProductVForPriceAndAmountInfo = subOrder.getSubOrderDTO().getKProductVForPriceAndAmountInfo();
        for (SDMItem product : KProductVForPriceAndAmountInfo.keySet()){
            Double newAmount = KProductVForPriceAndAmountInfo.get(product).getAmount() + KProductInfoVProductAmount.get(product).getAmount();
            Map<SDMDiscount, Integer> KDiscountVTimeUse = subOrder.getSubOrderDTO().getKDiscountVTimeUse();
            for (SDMDiscount discount : KDiscountVTimeUse.keySet()){
                if (discount.getIfYouBuy().getItemId() == product.getId()){
                    newAmount += discount.getIfYouBuy().getQuantity() * KDiscountVTimeUse.get(discount);
                }
            }

            KProductInfoVProductAmount.get(product).setAmount(newAmount);
        }
    }
}
