package co.istad.payment.domain.entity;

import co.istad.CustomerId;
import co.istad.Money;
import co.istad.entity.BaseEntity;
import co.istad.payment.domain.valueobject.CreditHistoryId;
import co.istad.payment.domain.valueobject.TransactionType;

public class CreditHistory extends BaseEntity<CreditHistoryId> {
    private final CustomerId customerId;
    private final Money amount;
    private final TransactionType transactionType;

    public CreditHistory(CreditHistoryId creditHistoryId, CustomerId customerId, Money amount, TransactionType transactionType) {
        super.setId(creditHistoryId);
        this.customerId = customerId;
        this.amount = amount;
        this.transactionType = transactionType;
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public Money getAmount() {
        return amount;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }
}
