package co.istad.controller;

import co.istad.dto.*;
import co.istad.mapper.CustomerWebMapper;
import co.istad.usecase.DeactivateCustomerUseCase;
import co.istad.usecase.InitiateCustomerUseCase;
import co.istad.usecase.UpdateCustomerUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customers")
public class CustomerController {

    private final InitiateCustomerUseCase initiateCustomerUseCase;
    private final UpdateCustomerUseCase updateCustomerUseCase;
    private final DeactivateCustomerUseCase deactivateCustomerUseCase;
    private final CustomerWebMapper customerWebMapper;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CustomerInitiateResponse initiateCustomer(
            @Valid @RequestBody CustomerInitiateRequest customerInitiateRequest
    ) {

        InitiateCustomerResult result = initiateCustomerUseCase.execute(
                customerWebMapper.toCommand(customerInitiateRequest)
        );
        return customerWebMapper.toResponse(result);
    }

    @PutMapping("/{customerId}")
    public CustomerUpdateResponse updateCustomer(
            @PathVariable UUID customerId,
            @Valid @RequestBody CustomerUpdateRequest request
    ) {
        UpdateCustomerResult result = updateCustomerUseCase.execute(
                customerWebMapper.toCommand(customerId, request)
        );

        return customerWebMapper.toResponse(result);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/{customerId}/deactivate")
    public void deactivateCustomer(
            @PathVariable UUID customerId
    ) {
        deactivateCustomerUseCase.execute(
                customerWebMapper.toCommand(customerId)
        );
    }
}
