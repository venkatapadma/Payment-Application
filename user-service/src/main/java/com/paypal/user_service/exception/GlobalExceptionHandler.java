package com.paypal.user_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    //handle specific exception - UserAlreadyExistsException
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorDetails> handleUserAlreadyExistsException(UserAlreadyExistsException exception,
                                                                         WebRequest webRequest) {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(),
                exception.getMessage(),
                webRequest.getDescription(false),
                "USER_ALREADY_EXISTS");
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorDetails> handleInvalidCredentialsException(InvalidCredentialsException exception, WebRequest webRequest) {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(),
                exception.getMessage(),
                webRequest.getDescription(false),
                "INVALID_CREDENTIALS");
        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    //handle specific exception - NotFoundException
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorDetails> handleNotFoundException(NotFoundException exception,
                                                                WebRequest webRequest) {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(),
                exception.getMessage(),
                webRequest.getDescription(false),
                "USER_NOT_FOUND");
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
