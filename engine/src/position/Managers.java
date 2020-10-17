package position;

import dto.OrderDTO;
import dto.SubOrderDTO;
import dto.ProductDTO;
import dto.TransactionDTO;
import order.Action;
import order.Order;
import order.SubOrder;
import store.Store;
import xml.schema.generated.Location;
import xml.schema.generated.SDMItem;

import java.util.*;

public class Managers {
    private String zoneName;
    private Map <String, Manager> KManagerNameVManger = new HashMap<>();
    private Map<Integer, Store> KStoreIdVStore = new HashMap<>();

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

    public Order addOrder(OrderDTO orderDTO) {
        Order order = new Order(orderDTO);
        Map<Integer, SubOrder> KStoreIdVSubOrder = order.getKStoreIdVSubOrder();
        for (Integer storeId : KStoreIdVSubOrder.keySet()){
            SubOrder subOrder = KStoreIdVSubOrder.get(storeId);
            Store store = KStoreIdVStore.get(storeId);
            Manager storeManager = KManagerNameVManger.get(store.getStoreOwnerName());

            store.addSubOrder(subOrder);
            Double transferAmount = subOrder.getSubOrderDTO().getDeliveryPrice() + subOrder.getSubOrderDTO().getProductsPrice();
            TransactionDTO transaction = Action.invokeAction(Action.TRANSFER,storeManager.getMoney(),transferAmount,orderDTO.getDate());
            storeManager.addTransaction(transaction);

        }

        return order;
    }

    private Map<Integer,Integer> getProductIdToChipsetStore(Map<SDMItem, ProductDTO> productsToBuy){
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

    public OrderDTO getMinOrder(OrderDTO orderDTO, Map<SDMItem, ProductDTO> productsToBuyDTO) {

        Map<Integer,Integer> KProductIdVStoreId = getProductIdToChipsetStore(productsToBuyDTO);
        Map<Integer, SubOrderDTO> KStoreIdVSubOrderDTO = new HashMap<>();

        for (SDMItem product : productsToBuyDTO.keySet()){
            if (!KStoreIdVSubOrderDTO.containsKey(KProductIdVStoreId.get(product.getId()))){
                SubOrderDTO subOrderDTO = new SubOrderDTO(orderDTO.getId(),orderDTO.getDate(),orderDTO.getCustomerName(),orderDTO.getCustomerLocation());
                KStoreIdVSubOrderDTO.put(KProductIdVStoreId.get(product.getId()), subOrderDTO);
            }

            SubOrderDTO subOrderDTO = KStoreIdVSubOrderDTO.get(KProductIdVStoreId.get(product.getId()));
            Double subOrderProductsPrices = subOrderDTO.getProductsPrice() + productsToBuyDTO.get(product).getPrice();
            Double orderProductsPrices = productsToBuyDTO.get(product).getPrice() + orderDTO.getProductsPrice();

            subOrderDTO.getKProductIdVProductsSoldInfo().put(KProductIdVStoreId.get(product.getId()),product);
            subOrderDTO.getKProductVForPriceAndAmountInfo().put(product,productsToBuyDTO.get(product));
            subOrderDTO.setProductsPrice(subOrderProductsPrices);
            orderDTO.setProductsPrice(orderProductsPrices);
        }


        Double totalDelivery = 0.0;
        for (Integer storeId : KProductIdVStoreId.keySet()){
            Store store = KStoreIdVStore.get(storeId);
            Location storeLocation = store.getStoreInfo().getLocation();
            Double deliveryCost = SubOrder.getDistance(orderDTO.getCustomerLocation(), storeLocation) * store.getStoreInfo().getDeliveryPpk();
            totalDelivery += deliveryCost;
            KStoreIdVSubOrderDTO.get(storeId).setDeliveryCost(deliveryCost);
        }

        orderDTO.setTotalDeliveryCost(totalDelivery);
        orderDTO.setKStoreVSubStore(KStoreIdVSubOrderDTO);

        return orderDTO;
    }
}
