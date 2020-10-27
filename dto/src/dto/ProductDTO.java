package dto;

import xml.schema.generated.SDMItem;
import xml.schema.generated.SDMSell;

public class ProductDTO {
    private Double price = 0.0;
    private  Double amount = 0.0;

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
