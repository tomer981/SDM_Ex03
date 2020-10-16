package dto;

import xml.schema.generated.SDMItem;
import xml.schema.generated.SDMSell;

public class ProductDTO {
    Double price;
    Double amount;

    public void setPrice(Double price) {
        this.price = price;
    }
    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getPrice() {
        return price;
    }
    public Double getAmount() {
        return amount;
    }
}
