package co.istad.payment.adapter;

import co.istad.CustomerId;
import co.istad.payment.document.CreditHistoryDocument;
import co.istad.payment.domain.entity.CreditHistory;
import co.istad.payment.mapper.PaymentPersistenceMapper;
import co.istad.payment.port.out.CreditHistoryRepository;
import co.istad.payment.repository.CreditHistoryMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CreditHistoryRepositoryAdapter implements CreditHistoryRepository {

    private final CreditHistoryMongoRepository creditHistoryMongoRepository;
    private final PaymentPersistenceMapper paymentPersistenceMapper;

    @Override
    public void saveAll(List<CreditHistory> creditHistories) {
        if (creditHistories == null || creditHistories.isEmpty()) {
            return;
        }
        List<CreditHistoryDocument> documents = creditHistories.stream()
                .map(paymentPersistenceMapper::toDocument)
                .toList();
        creditHistoryMongoRepository.saveAll(documents);
    }

    @Override
    public List<CreditHistory> findByCustomerId(CustomerId customerId) {
        if (customerId == null || customerId.value() == null) {
            return List.of();
        }
        return creditHistoryMongoRepository.findByCustomerId(customerId.value()).stream()
                .map(paymentPersistenceMapper::toDomain)
                .toList();
    }
}
