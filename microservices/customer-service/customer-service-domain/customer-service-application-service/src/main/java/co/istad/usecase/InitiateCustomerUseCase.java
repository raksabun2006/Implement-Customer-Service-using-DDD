package co.istad.usecase;

import co.istad.dto.InitiateCustomerCommand;
import co.istad.dto.InitiateCustomerResult;
import co.istad.service.CustomerDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class InitiateCustomerUseCase {

    private final CustomerDomainService customerDomainService;

    public InitiateCustomerResult execute(InitiateCustomerCommand command) {
        log.info("initiate customer usecase: {}", command);
        // validate by load data from persistence (output port)
        // invoke domain logic (called domain service)
        //customerDomainService.initiateCustomer(customer);
        // save data into database (output port)
        return new InitiateCustomerResult(UUID.randomUUID(),command.username()
        , command.familyName(), command.givenName(), command.email(), command.phoneNumber() );
    }

}
