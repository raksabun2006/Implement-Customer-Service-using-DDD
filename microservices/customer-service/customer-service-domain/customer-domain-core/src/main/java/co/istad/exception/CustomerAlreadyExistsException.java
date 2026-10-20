package co.istad.exception;

public class CustomerAlreadyExistsException extends CustomerDomainException {

    public CustomerAlreadyExistsException(String message) {
        super(message);
    }

    public CustomerAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
