package dto;

import xml.schema.generated.SDMItem;
import xml.schema.generated.SDMSell;

public class ProductDTO implements Cloneable {
    private Double price = 0.0;
    private Double amount = 0.0;
    private Double amountUsInDiscounts = 0.0;

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return (ProductDTO)super.clone();
    }

    public Double getAmountUsInDiscounts() {
        return amountUsInDiscounts;
    }

    public void setAmountUsInDiscounts(Double amountUsInDiscounts) {
        this.amountUsInDiscounts = amountUsInDiscounts;
    }

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
