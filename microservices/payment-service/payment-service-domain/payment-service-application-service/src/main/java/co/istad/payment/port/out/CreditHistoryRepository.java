package co.istad.payment.port.out;

import co.istad.CustomerId;
import co.istad.payment.domain.entity.CreditHistory;

import java.util.List;

public interface CreditHistoryRepository {

    void saveAll(List<CreditHistory> creditHistories);

    List<CreditHistory> findByCustomerId(CustomerId customerId);
}
