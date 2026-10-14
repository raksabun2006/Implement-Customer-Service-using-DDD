package co.istad.payment.mapper;

import co.istad.payment.dto.ProcessPaymentCommand;
import co.istad.payment.dto.ProcessPaymentRequest;
import co.istad.payment.dto.ProcessPaymentResponse;
import co.istad.payment.dto.ProcessPaymentResult;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentWebMapper {

    ProcessPaymentCommand toCommand(ProcessPaymentRequest request);

    ProcessPaymentResponse toResponse(ProcessPaymentResult result);
}
