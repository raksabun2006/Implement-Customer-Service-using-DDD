package co.istad.payment.controller;

import co.istad.payment.dto.ProcessPaymentRequest;
import co.istad.payment.dto.ProcessPaymentResponse;
import co.istad.payment.dto.ProcessPaymentResult;
import co.istad.payment.mapper.PaymentWebMapper;
import co.istad.payment.usecase.ProcessPaymentUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final ProcessPaymentUseCase processPaymentUseCase;
    private final PaymentWebMapper paymentWebMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ProcessPaymentResponse processPayment(@Valid @RequestBody ProcessPaymentRequest request) {
        log.info("Received payment processing request for order: {}", request.orderId());
        ProcessPaymentResult result = processPaymentUseCase.execute(paymentWebMapper.toCommand(request));
        return paymentWebMapper.toResponse(result);
    }
}
