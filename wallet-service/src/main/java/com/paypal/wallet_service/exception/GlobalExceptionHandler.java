package com.paypal.wallet_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    //handle specific exception - InsufficientFundsException
    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorDetails> handleInsufficientFundsException(InsufficientFundsException exception,
                                                                         WebRequest webRequest) {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(),
                exception.getMessage(),
                webRequest.getDescription(false),
                "INSUFFICIENT_BALANCE");
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    //handle specific exception - NotFoundException
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorDetails> handleNotFoundException(NotFoundException exception,
                                                                WebRequest webRequest) {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(),
                exception.getMessage(),
                webRequest.getDescription(false),
                "ACCOUNT_NOT_FOUND");
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    //Handle Generic Exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handlerGenericException(Exception exception,
                                                                WebRequest webRequest) {
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                exception.getMessage(),
                webRequest.getDescription(false),
                "INTERNAL_SERVER_ERROR"
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
