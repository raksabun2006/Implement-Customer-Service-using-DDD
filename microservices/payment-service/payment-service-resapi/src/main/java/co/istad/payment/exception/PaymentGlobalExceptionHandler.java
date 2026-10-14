package co.istad.payment.exception;

import co.istad.payment.domain.exception.PaymentDomainException;
import co.istad.payment.domain.exception.PaymentNotFoundException;
import co.istad.payment.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class PaymentGlobalExceptionHandler {

    @ExceptionHandler(PaymentDomainException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlePaymentDomainException(PaymentDomainException exception) {
        log.error("PaymentDomainException occurred: {}", exception.getMessage(), exception);
        return new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                List.of(exception.getMessage()),
                ZonedDateTime.now(ZoneId.of("UTC"))
        );
    }

    @ExceptionHandler(PaymentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlePaymentNotFoundException(PaymentNotFoundException exception) {
        log.error("PaymentNotFoundException occurred: {}", exception.getMessage(), exception);
        return new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                exception.getMessage(),
                List.of(exception.getMessage()),
                ZonedDateTime.now(ZoneId.of("UTC"))
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException exception) {
        log.error("Validation error: {}", exception.getMessage());
        List<String> errors = exception.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .toList();
        return new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                errors,
                ZonedDateTime.now(ZoneId.of("UTC"))
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception exception) {
        log.error("Unexpected error occurred in Payment Service: {}", exception.getMessage(), exception);
        return new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred",
                List.of(exception.getMessage() != null ? exception.getMessage() : "Internal Server Error"),
                ZonedDateTime.now(ZoneId.of("UTC"))
        );
    }
}
