package co.istad.payment.domain.service;

import co.istad.payment.domain.entity.CreditEntry;
import co.istad.payment.domain.entity.CreditHistory;
import co.istad.payment.domain.entity.Payment;
import co.istad.payment.domain.event.PaymentEvent;

import java.util.List;

public interface PaymentDomainService {

    PaymentEvent validateAndInitiatePayment(Payment payment,
                                           CreditEntry creditEntry,
                                           List<CreditHistory> creditHistories,
                                           List<String> failureMessages);

    PaymentEvent validateAndCancelPayment(Payment payment,
                                          CreditEntry creditEntry,
                                          List<CreditHistory> creditHistories,
                                          List<String> failureMessages);
}
