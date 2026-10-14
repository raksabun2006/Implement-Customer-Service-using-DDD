package co.istad.payment.adapter;

import co.istad.CustomerId;
import co.istad.payment.document.CreditEntryDocument;
import co.istad.payment.domain.entity.CreditEntry;
import co.istad.payment.mapper.PaymentPersistenceMapper;
import co.istad.payment.port.out.CreditEntryRepository;
import co.istad.payment.repository.CreditEntryMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CreditEntryRepositoryAdapter implements CreditEntryRepository {

    private final CreditEntryMongoRepository creditEntryMongoRepository;
    private final PaymentPersistenceMapper paymentPersistenceMapper;

    @Override
    public CreditEntry save(CreditEntry creditEntry) {
        CreditEntryDocument document = paymentPersistenceMapper.toDocument(creditEntry);
        CreditEntryDocument saved = creditEntryMongoRepository.save(document);
        return paymentPersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<CreditEntry> findByCustomerId(CustomerId customerId) {
        if (customerId == null || customerId.value() == null) {
            return Optional.empty();
        }
        return creditEntryMongoRepository.findByCustomerId(customerId.value())
                .map(paymentPersistenceMapper::toDomain);
    }
}
