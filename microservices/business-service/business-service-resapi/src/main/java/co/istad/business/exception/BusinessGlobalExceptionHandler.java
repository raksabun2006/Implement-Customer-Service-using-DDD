package co.istad.business.exception;

import co.istad.business.domain.exception.BusinessAlreadyExistsException;
import co.istad.business.domain.exception.BusinessDomainException;
import co.istad.business.domain.exception.BusinessNotFoundException;
import co.istad.business.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class BusinessGlobalExceptionHandler {

    @ExceptionHandler(BusinessAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleBusinessAlreadyExistsException(BusinessAlreadyExistsException exception) {
        log.error("BusinessAlreadyExistsException occurred: {}", exception.getMessage(), exception);
        return new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                exception.getMessage(),
                List.of(exception.getMessage()),
                ZonedDateTime.now(ZoneId.of("UTC"))
        );
    }

    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicateKeyException(DuplicateKeyException exception) {
        log.error("DuplicateKeyException occurred: {}", exception.getMessage(), exception);
        return new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Duplicate business record already exists",
                List.of(exception.getMessage() != null ? exception.getMessage() : "Duplicate key error"),
                ZonedDateTime.now(ZoneId.of("UTC"))
        );
    }

    @ExceptionHandler(BusinessDomainException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBusinessDomainException(BusinessDomainException exception) {
        log.error("BusinessDomainException occurred: {}", exception.getMessage(), exception);
        return new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                List.of(exception.getMessage()),
                ZonedDateTime.now(ZoneId.of("UTC"))
        );
    }

    @ExceptionHandler(BusinessNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleBusinessNotFoundException(BusinessNotFoundException exception) {
        log.error("BusinessNotFoundException occurred: {}", exception.getMessage(), exception);
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

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception exception) {
        log.error("Unexpected error occurred in Business Service: {}", exception.getMessage(), exception);
        return new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred",
                List.of(exception.getMessage() != null ? exception.getMessage() : "Internal Server Error"),
                ZonedDateTime.now(ZoneId.of("UTC"))
        );
    }
}
