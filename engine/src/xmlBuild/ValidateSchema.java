package xmlBuild;

import xmlBuild.schema.generated.*;


import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ValidateSchema {
    SchemaBaseJaxbObjects schema;

    private boolean noIdenticalIds(List<Integer> listIds) {
        return listIds.stream().collect(Collectors.groupingBy(Integer::intValue)).values().
                stream().noneMatch(idGroupValue -> idGroupValue.size() > 1);
    }

    public void noIdenticalProductsIds(List<SDMItem> xmlProducts) throws IllegalArgumentException {
        List<Integer> xmlProductsId = xmlProducts.stream().map(SDMItem::getId).collect(Collectors.toList());
        if (!noIdenticalIds(xmlProductsId)) {
            throw new IllegalArgumentException("Same product Id define twice in market");
        }
    }

    public void noIdenticalStoresIds(List<SDMStore> xmlStores) throws IllegalArgumentException {
        List<Integer> xmlStoresId = xmlStores.stream().map(SDMStore::getId).collect(Collectors.toList());
        if(!noIdenticalIds(xmlStoresId)){
            throw new IllegalArgumentException("More then one store with the same id");
        }
    }

    public void noIdenticalLocation(List<Location> Locations) throws IllegalArgumentException {
        for (Location location1 : Locations) {
            for (Location location2 : Locations) {
                if (location1 != location2 && location1.getX() == location2.getX() && location1.getY() == location2.getY()) {
                    throw new IllegalArgumentException("The same Location for 2 objects");
                }
            }
        }
    }

    public void InRangeStoreLocation(List<Location> xmlLocation)throws IllegalArgumentException {
        if (!xmlLocation.stream().allMatch(storeLocation ->
                storeLocation.getX() > 0 && storeLocation.getX() < 51 &&
                        storeLocation.getY() > 0 && storeLocation.getY() < 51)) {
            throw new IllegalArgumentException("Out of range location");
        }
    }

    public void noIdenticalStoreProductsIds(List<Integer> xmlProductsInStore) {
        if(!noIdenticalIds(xmlProductsInStore)){
            throw new IllegalArgumentException("More then one store product with the same id");
        }
    }

    public void StoreProductInMarketProduct( List<Integer> xmlProductsInStore, List<SDMItem> xmlProducts) throws IllegalArgumentException {
        List<Integer> xmlProductsId = xmlProducts.stream().map(SDMItem::getId).collect(Collectors.toList());
        if (!xmlProductsId.containsAll(xmlProductsInStore)){
            throw new IllegalArgumentException("Product in Store Not Found in Market");
        }
    }

    public void IsDiscountProductSoldInStore(List<Integer> xmlProductsInStore, List<Integer> xmlDiscountProductsId) {
        if (!xmlProductsInStore.containsAll(xmlDiscountProductsId)){
            throw new IllegalArgumentException("Discont Product not Sold in Store");
        }
    }

    public void eachProductSoldByAtLeastOneStore(Set<Integer> productsSoldInStores, List<SDMItem> xmlMarketProduct) throws IllegalArgumentException {
        List<Integer> MarketProductsId = xmlMarketProduct.stream().map(SDMItem::getId).collect(Collectors.toList());
        if (!MarketProductsId.containsAll(productsSoldInStores))
            throw new IllegalArgumentException("Product in market but not in any store");
    }

    public ValidateSchema(SchemaBaseJaxbObjects schema){
        this.schema = schema;
    }

    public void isXmlFile(File file) throws IllegalArgumentException{
        if (!file.getPath().endsWith(".xml")){
            throw new IllegalArgumentException("the file is not .xml file");
        }
    }
}
