package xmlBuild;

import xmlBuild.schema.generated.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class SchemaBaseJaxbObjects {
    private static final String JAXB_XML_SDM_PACKAGE_NAME = "xmlBuild.schema.generated";

    private ValidateSchema validate;

    private SuperDuperMarketDescriptor xmlZoneMarket;
    private List<SDMItem> xmlProducts;
    private List<SDMStore> xmlStores;

    public SchemaBaseJaxbObjects(File file) throws IllegalStateException {
        validate = new ValidateSchema(this);
        validate.isXmlFile(file);
        JAXBContext jaxbContext = null;
        Unmarshaller SDM = null;

        try {
            jaxbContext = JAXBContext.newInstance(JAXB_XML_SDM_PACKAGE_NAME);
            SDM = jaxbContext.createUnmarshaller();
            xmlZoneMarket = (SuperDuperMarketDescriptor) SDM.unmarshal(file);
        } catch (JAXBException e) {
            throw new IllegalStateException("Couldn't parse XML file", e);
        }

        initializeSchema();
    }

    public SuperDuperMarketDescriptor getXmlZoneMarket() {
        return xmlZoneMarket;
    }

    private void initializeSchema() throws IllegalStateException {
        try {
            validate = new ValidateSchema(this);
            initXmlProducts();
            initXmlStores();
            initXmlLocation();
            initXmlDiscounts();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public void initXmlProducts() throws IllegalArgumentException {
        xmlProducts = xmlZoneMarket.getSDMItems().getSDMItem();
        validate.noIdenticalProductsIds(xmlProducts);
    }

    public void initXmlStores() throws IllegalArgumentException {
        if (xmlZoneMarket.getSDMStores() != null) {
            xmlStores = xmlZoneMarket.getSDMStores().getSDMStore();
            Set<Integer> productsSoldInStores = new HashSet<>();
            validate.noIdenticalStoresIds(xmlStores);
            for (SDMStore xmlStore : xmlStores) {
                List<Integer> xmlProductsInStore = xmlStore.getSDMPrices().getSDMSell().stream().map(SDMSell::getItemId).collect(Collectors.toList());
                productsSoldInStores.addAll(xmlProductsInStore);
                validate.noIdenticalStoreProductsIds(xmlProductsInStore);
                validate.StoreProductInMarketProduct(xmlProductsInStore, xmlProducts);
            }
            validate.eachProductSoldByAtLeastOneStore(productsSoldInStores, xmlProducts);
        }
    }
    public void initXmlDiscounts() throws IllegalArgumentException {
        if (xmlStores != null) {
            for (SDMStore xmlStore : xmlStores) {
                if (xmlStore.getSDMDiscounts() != null) {
                    List<Integer> xmlProductsInStore = xmlStore.getSDMPrices().getSDMSell().stream().map(SDMSell::getItemId).collect(Collectors.toList());
                    List<IfYouBuy> conditions = xmlStore.getSDMDiscounts().getSDMDiscount().stream().map(SDMDiscount::getIfYouBuy).collect(Collectors.toList());
                    List<Integer> xmlDiscountProductsId = conditions.stream().map(IfYouBuy::getItemId).collect(Collectors.toList());
                    List<ThenYouGet> discountsOffers = xmlStore.getSDMDiscounts().getSDMDiscount().stream().map(SDMDiscount::getThenYouGet).collect(Collectors.toList());
                    for (ThenYouGet discountOffer : discountsOffers) {
                        xmlDiscountProductsId.addAll(discountOffer.getSDMOffer().stream().map(SDMOffer::getItemId).collect(Collectors.toList()));
                    }
                    validate.IsDiscountProductSoldInStore(xmlProductsInStore, xmlDiscountProductsId);
                }
            }
        }
    }
    public void initXmlLocation() {
        List<Location> xmlLocation = new ArrayList<>();
        if (xmlStores != null) {
            xmlLocation.addAll(xmlStores.stream().map(SDMStore::getLocation).collect(Collectors.toList()));
        }
        validate.noIdenticalLocation(xmlLocation);
        validate.InRangeStoreLocation(xmlLocation);
    }
}
