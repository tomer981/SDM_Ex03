package order;

import dto.FeedbackDTO;
import dto.SubOrderDTO;
import xmlBuild.schema.generated.*;


public class SubOrder {
    private SubOrderDTO subOrderDTO;
//    private Integer id = -1;
//    private Date date;
//    private String customerName;
//    private Location customerLocation;
//
//    private Map<Integer,SDMItem> KProductIdVProductsSoldInfo;
//    private Map<SDMItem, ProductDTO> KProductVForPriceAndAmountInfo;
//    private Map<SDMDiscount, Integer> KDiscountVTimeUse;

    public SubOrderDTO getSubOrderDTO() {
        return subOrderDTO;
    }

    public SubOrder(SubOrderDTO subOrderDTO) {
        this.subOrderDTO = subOrderDTO;
    }


    public static Double getDistance(Location location1, Location location2){
        return getDistance(location1.getX(),location1.getY(),location2.getX(),location2.getY());
    }
    public static Double getDistance(Integer x1,Integer y1,Integer x2,Integer y2){
        Double X =  Math.pow(x1 - x2,2);
        Double Y = Math.pow(y1 - y2,2);
        return Math.pow(X+Y,0.5);
    }

    public boolean isProductSold(Integer productId){
        return subOrderDTO.getKProductIdVProductsSoldInfo().containsKey(productId);

    }
    public boolean isProductSold(SDMItem product){
        return isProductSold(product.getId());
    }

    public Double getAmountSold(Integer productId){
        Double amountSold = 0.0;
        SDMItem SDMProduct = subOrderDTO.getKProductIdVProductsSoldInfo().get(productId);
        if (subOrderDTO.getKProductVForPriceAndAmountInfo().containsKey(SDMProduct)){
            amountSold = subOrderDTO.getKProductVForPriceAndAmountInfo().get(SDMProduct).getAmount();
        }

        for (SDMDiscount discount : subOrderDTO.getKDiscountVTimeUse().keySet()){
            for (SDMOffer offer : discount.getThenYouGet().getSDMOffer()){
                amountSold += offer.getQuantity() * subOrderDTO.getKDiscountVTimeUse().get(discount);
            }
        }

        return amountSold;
    }
    public Double getAmountSold(SDMItem product) {
        return getAmountSold(product.getId());
    }
}
