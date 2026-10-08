package co.istad.usecase;

import co.istad.dto.UpdateCustomerCommand;
import co.istad.dto.UpdateCustomerResult;

public interface UpdateCustomerUseCase {

    UpdateCustomerResult execute(UpdateCustomerCommand command);

}
