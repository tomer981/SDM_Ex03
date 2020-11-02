package dto;


public class ProductDTO implements Cloneable {
    private Double price = 0.0;
    private Double amount = 0.0;
    private Double amountUseInDiscounts = 0.0;

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return (ProductDTO)super.clone();
    }

    public Double getAmountUseInDiscounts() {
        return amountUseInDiscounts;
    }

    public void setAmountUseInDiscounts(Double amountUseInDiscounts) {
        this.amountUseInDiscounts = amountUseInDiscounts;
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
