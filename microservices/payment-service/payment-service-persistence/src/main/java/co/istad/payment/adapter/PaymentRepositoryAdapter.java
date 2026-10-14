package co.istad.payment.adapter;

import co.istad.payment.document.PaymentDocument;
import co.istad.payment.domain.entity.Payment;
import co.istad.payment.mapper.PaymentPersistenceMapper;
import co.istad.payment.port.out.PaymentRepository;
import co.istad.payment.repository.PaymentMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentRepositoryAdapter implements PaymentRepository {

    private final PaymentMongoRepository paymentMongoRepository;
    private final PaymentPersistenceMapper paymentPersistenceMapper;

    @Override
    public Payment save(Payment payment) {
        PaymentDocument document = paymentPersistenceMapper.toDocument(payment);
        PaymentDocument saved = paymentMongoRepository.save(document);
        return paymentPersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<Payment> findById(UUID id) {
        if (id == null) {
            return Optional.empty();
        }
        return paymentMongoRepository.findById(id)
                .map(paymentPersistenceMapper::toDomain);
    }

    @Override
    public Optional<Payment> findByOrderId(UUID orderId) {
        if (orderId == null) {
            return Optional.empty();
        }
        return paymentMongoRepository.findByOrderId(orderId)
                .map(paymentPersistenceMapper::toDomain);
    }
}
