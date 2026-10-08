package co.istad.mapper;

import co.istad.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CustomerWebMapper {

    InitiateCustomerCommand toCommand(CustomerInitiateRequest request);

    CustomerInitiateResponse toResponse(InitiateCustomerResult result);

    @Mapping(target = "customerId", source = "customerId")
    UpdateCustomerCommand toCommand(UUID customerId, CustomerUpdateRequest request);

    CustomerUpdateResponse toResponse(UpdateCustomerResult result);

    default DeactivateCustomerCommand toCommand(UUID customerId) {
        return new DeactivateCustomerCommand(customerId);
    }

}
