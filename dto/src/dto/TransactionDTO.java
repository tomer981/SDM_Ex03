package dto;

import java.util.Date;

public class TransactionDTO {
    private String action;
    private Date date;
    private Double transactionAmount;
    private Double moneyBeforeTransaction;
    private Double moneyAfterTransaction;

    public String getAction() {
        return action;
    }
    public Date getDate() {
        return date;
    }
    public Double getTransactionAmount() {
        return transactionAmount;
    }
    public Double getMoneyBeforeTransaction() {
        return moneyBeforeTransaction;
    }
    public Double getMoneyAfterTransaction() {
        return moneyAfterTransaction;
    }

    public TransactionDTO(String action, Date date, Double transactionAmount, Double moneyBeforeTransaction, Double moneyAfterTransaction) {
        this.action = action;
        this.date = date;
        this.transactionAmount = transactionAmount;
        this.moneyBeforeTransaction = moneyBeforeTransaction;
        this.moneyAfterTransaction = moneyAfterTransaction;
    }
}
