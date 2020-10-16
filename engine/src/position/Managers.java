package position;

import dto.OrderDTO;
import dto.SubOrderDTO;
import dto.ProductDTO;
import order.Order;
import order.SubOrder;
import store.Store;
import xml.schema.generated.SDMItem;
import xml.schema.generated.SDMOffer;

import java.util.*;
import java.util.stream.Collectors;

public class Managers {
    private String zoneName;
    private Map <String, Manager> KManagerIdVManger = new HashMap<>();
    private Map<Integer, Store> KStoreIdVStore = new HashMap<>();

    public Managers(String zoneName) {
        this.zoneName = zoneName;
    }

    public void addManager(Manager manager){
        KManagerIdVManger.put(manager.getName(), manager);
        this.zoneName = zoneName;
        List<Store> storesZone = manager.getKZoneNameVStores().get(zoneName);
        for (Store store : storesZone){
            KStoreIdVStore.put(store.getStoreInfo().getId(), store);
        }
    }

    public Order addOrder(OrderDTO orderDTO) {
        Order order = new Order(orderDTO);
        Map<Integer, SubOrder> KStoreIdVSubOrder = order.getKStoreIdVSubOrder();
        for (Integer storeId : KStoreIdVSubOrder.keySet()){
            KStoreIdVStore.get(storeId).addSubOrder(KStoreIdVSubOrder.get(storeId));
        }

        return order;
    }


    private Map<Integer,Integer> getProductIdToChipsetStore(Map<SDMItem, ProductDTO> productsToBuy){
        Map<Integer,Integer> KProductIdVStoreId = new HashMap<>();
        for (SDMItem product : productsToBuy.keySet()){
            Double minProductPrice = 0.0;
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
            subOrderDTO.getKProductIdVProductsSoldInfo().put(KProductIdVStoreId.get(product.getId()),product);
            subOrderDTO.getKProductVForPriceAndAmountInfo().put(product,productsToBuyDTO.get(product));
        }

        orderDTO.setKStoreVSubStore(KStoreIdVSubOrderDTO);
        return orderDTO;
    }
}
