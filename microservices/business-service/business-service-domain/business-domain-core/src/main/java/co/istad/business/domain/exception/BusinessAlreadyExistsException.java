package co.istad.business.domain.exception;

public class BusinessAlreadyExistsException extends BusinessDomainException {

    public BusinessAlreadyExistsException(String message) {
        super(message);
    }

    public BusinessAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
