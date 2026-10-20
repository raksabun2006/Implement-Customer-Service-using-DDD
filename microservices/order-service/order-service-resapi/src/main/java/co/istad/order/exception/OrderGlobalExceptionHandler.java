package co.istad.order.exception;

import co.istad.order.domain.exception.OrderDomainException;
import co.istad.order.domain.exception.OrderNotFoundException;
import co.istad.order.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class OrderGlobalExceptionHandler {

    @ExceptionHandler(OrderDomainException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleOrderDomainException(OrderDomainException exception) {
        log.error("OrderDomainException occurred: {}", exception.getMessage(), exception);
        return new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                List.of(exception.getMessage()),
                ZonedDateTime.now(ZoneId.of("UTC"))
        );
    }

    @ExceptionHandler(OrderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleOrderNotFoundException(OrderNotFoundException exception) {
        log.error("OrderNotFoundException occurred: {}", exception.getMessage(), exception);
        return new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                exception.getMessage(),
                List.of(exception.getMessage()),
                ZonedDateTime.now(ZoneId.of("UTC"))
        );
    }

    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(WebExchangeBindException exception) {
        log.error("Validation error in WebFlux request: {}", exception.getMessage());
        List<String> errors = exception.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
        return new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                errors,
                ZonedDateTime.now(ZoneId.of("UTC"))
        );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException exception) {
        log.warn("Response status exception: {}", exception.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                exception.getStatusCode().value(),
                exception.getReason() != null ? exception.getReason() : exception.getMessage(),
                List.of(exception.getMessage()),
                ZonedDateTime.now(ZoneId.of("UTC"))
        );
        return ResponseEntity.status(exception.getStatusCode()).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception exception) {
        log.error("Unexpected error occurred in Order Service: {}", exception.getMessage(), exception);
        return new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred",
                List.of(exception.getMessage() != null ? exception.getMessage() : "Internal Server Error"),
                ZonedDateTime.now(ZoneId.of("UTC"))
        );
    }
}
