package order;

import dto.TransactionDTO;

import java.util.Date;

public enum Action {
    DEPOSIT,
    TRANSFER,
    ACCEPT;

    public static TransactionDTO invokeAction(Action operation, Double moneyHave, Double transactionAmount, Date date) {
        switch (operation) {
            case TRANSFER:
                moneyHave -= transactionAmount;
                return new TransactionDTO(operation.name(), date,transactionAmount,moneyHave + transactionAmount, moneyHave);
            default:
                moneyHave += transactionAmount;
                return new TransactionDTO(operation.name(), date,transactionAmount,moneyHave - transactionAmount, moneyHave);
        }
    }
}
