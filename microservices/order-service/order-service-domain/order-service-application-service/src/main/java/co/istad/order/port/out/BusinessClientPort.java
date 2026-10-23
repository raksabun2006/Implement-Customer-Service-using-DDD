package co.istad.order.port.out;

import co.istad.order.dto.external.BusinessExternalDto;

import java.util.Optional;
import java.util.UUID;

public interface BusinessClientPort {
    Optional<BusinessExternalDto> getBusiness(UUID businessId);
    Optional<String> getRespondingInstance();
}
