package position;

import dto.*;
import order.Action;
import order.Order;
import order.SubOrder;
import store.Store;
import xmlBuild.schema.generated.*;


import java.util.*;

public class Managers {
    private String zoneName;
    private Map <String, Manager> KManagerNameVManger = new HashMap<>();
    private Map<Integer, Store> KStoreIdVStore = new HashMap<>();

    public StoreDTO getStoreDTO(Integer storeId){
        return KStoreIdVStore.get(storeId).getStoreDTO();
    }

    public Managers(String zoneName) {
        this.zoneName = zoneName;
    }

    public void addManager(Manager manager){
        KManagerNameVManger.put(manager.getName(), manager);
        List<Store> storesZone = manager.getKZoneNameVStores().get(zoneName);
        for (Store store : storesZone){
            KStoreIdVStore.put(store.getStoreInfo().getId(), store);
        }
    }

    public synchronized Order addOrder(OrderDTO orderDTO) {
        Order order = new Order(orderDTO);
        Map<Integer, SubOrder> KStoreIdVSubOrder = order.getKStoreIdVSubOrder();
        for (Integer storeId : KStoreIdVSubOrder.keySet()){
            SubOrder subOrder = KStoreIdVSubOrder.get(storeId);
            Store store = KStoreIdVStore.get(storeId);
            Manager storeManager = KManagerNameVManger.get(store.getStoreOwnerName());

            store.addSubOrder(subOrder);
            Double transferAmount = subOrder.getSubOrderDTO().getDeliveryPrice() + subOrder.getSubOrderDTO().getProductsPrice();
            TransactionDTO transaction = new TransactionDTO(Action.TRANSFER.name(), orderDTO.getDate(),transferAmount,storeManager.getMoney(),storeManager.getMoney() + transferAmount);
            storeManager.addTransaction(transaction);

        }

        return order;
    }

    public Map<Integer,Integer> getProductIdToChipsetStore(Map<SDMItem, ProductDTO> productsToBuy){
        Map<Integer,Integer> KProductIdVStoreId = new HashMap<>();
        for (SDMItem product : productsToBuy.keySet()){
            for (Store store : KStoreIdVStore.values()){
                if (store.isProductSold(product)){
                    if (!KProductIdVStoreId.containsKey(product.getId())){
                        KProductIdVStoreId.put(product.getId(),store.getStoreInfo().getId());
                        productsToBuy.get(product).setPrice(store.getProductPrice(product));
                    }
                    else {
                        if(productsToBuy.get(product).getPrice() > store.getProductPrice(product)){
                            productsToBuy.get(product).setPrice(store.getProductPrice(product));
                        }
                    }
                }
            }
        }

        return KProductIdVStoreId;
    }

    public OrderDTO getOrderDTO(OrderDTO orderDTO, Map<SDMItem, ProductDTO> productsToBuyDTO, Map<Integer, Integer> KProductIdVStoreId) {
        Map<Integer, SubOrderDTO> KStoreIdVSubOrderDTO = new HashMap<>();

        for (SDMItem product : productsToBuyDTO.keySet()){
            Integer chipsetStoreProduct_StoreId = KProductIdVStoreId.get(product.getId());
            if (!KStoreIdVSubOrderDTO.containsKey(chipsetStoreProduct_StoreId)){
                SubOrderDTO subOrderDTO = new SubOrderDTO(orderDTO.getId(),orderDTO.getDate(),orderDTO.getCustomerName(),orderDTO.getCustomerLocation());
                KStoreIdVSubOrderDTO.put(chipsetStoreProduct_StoreId, subOrderDTO);
            }

            SubOrderDTO subOrderDTO = KStoreIdVSubOrderDTO.get(chipsetStoreProduct_StoreId);
            Double costProductXAmount = productsToBuyDTO.get(product).getPrice() * productsToBuyDTO.get(product).getAmount();
            Double subOrderProductsPrices = subOrderDTO.getProductsPrice() + costProductXAmount;
            Double orderProductsPrices = costProductXAmount + orderDTO.getProductsPrice();

            subOrderDTO.getKProductIdVProductsSoldInfo().put(product.getId(),product);
            subOrderDTO.getKProductVForPriceAndAmountInfo().put(product,productsToBuyDTO.get(product));
            subOrderDTO.setProductsPrice(subOrderProductsPrices);
            orderDTO.setProductsPrice(orderProductsPrices);
        }


        Double totalDelivery = 0.0;
        for (Integer storeId : new HashSet<>(KProductIdVStoreId.values())){
            Store store = KStoreIdVStore.get(storeId);
            Location storeLocation = store.getStoreInfo().getLocation();
            Double deliveryCost = SubOrder.getDistance(orderDTO.getCustomerLocation(), storeLocation) * store.getStoreInfo().getDeliveryPpk();
            totalDelivery += deliveryCost;
            KStoreIdVSubOrderDTO.get(storeId).setDeliveryCost(deliveryCost);
        }

        orderDTO.setTotalDeliveryCost(totalDelivery);
        orderDTO.setKStoreIdVSubOrder(KStoreIdVSubOrderDTO);

        return orderDTO;
    }

    public List<StoreDTO> getStoresDTO() {
        List<StoreDTO> storesDTO = new ArrayList<>();
        KStoreIdVStore.values().forEach(store -> storesDTO.add(store.getStoreDTO()));
        return storesDTO;
    }

    public Map<SDMDiscount, Integer> getStoresDiscounts(Set<Integer> storesId) {
        Map<SDMDiscount, Integer> KDiscountVStoreId = new HashMap<>();

        for (Integer storeId : storesId){
            StoreDTO store = getStoreDTO(storeId);
            SDMDiscounts storeDiscounts = store.getSdmStore().getSDMDiscounts();
            if (storeDiscounts !=null){
                storeDiscounts.getSDMDiscount().forEach(sdmDiscount -> KDiscountVStoreId.put(sdmDiscount,storeId));
            }
        }
        return KDiscountVStoreId;
    }

    public Store getStoreById(Integer storeId) {
        return KStoreIdVStore.get(storeId);
    }

    public void addFeedbackDTO(Integer storeId, FeedbackDTO feedbackDTO) {
        String storeOwnerName = KStoreIdVStore.get(storeId).getStoreOwnerName();
        Manager manager = KManagerNameVManger.get(storeOwnerName);
        manager.addFeedbackDTO(zoneName,feedbackDTO);
    }
}
