package co.istad.order.port.out;

import co.istad.order.dto.external.PaymentExternalRequest;
import co.istad.order.dto.external.PaymentExternalResponse;

public interface PaymentClientPort {
    PaymentExternalResponse processPayment(PaymentExternalRequest request);
}
