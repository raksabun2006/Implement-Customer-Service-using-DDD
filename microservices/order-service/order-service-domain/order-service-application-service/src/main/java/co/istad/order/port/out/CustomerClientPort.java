package co.istad.order.port.out;

import co.istad.order.dto.external.CustomerExternalDto;

import java.util.Optional;
import java.util.UUID;

public interface CustomerClientPort {
    Optional<CustomerExternalDto> getCustomer(UUID customerId);
}
