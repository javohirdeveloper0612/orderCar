package com.example.ordercar.payme.controller;

import com.example.ordercar.payme.dto.result.Error;
import com.example.ordercar.payme.dto.result.Exception;
import com.example.ordercar.payme.exp.*;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.*;

@ControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", status.value());

        List<String> errors = new LinkedList<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.add(fieldError.getDefaultMessage());
        }
        body.put("errors", errors);
        return new ResponseEntity<>(body, headers, status);
    }

    @ExceptionHandler({OrderNotFoundException.class})
    private ResponseEntity<?> handler(OrderNotFoundException e) {
        return ResponseEntity.ok(new Error(new Exception(-31050, e.getMessage(), "order")));
    }

    @ExceptionHandler({WrongAmountException.class})
    private ResponseEntity<?> handler(WrongAmountException e) {
        return ResponseEntity.ok(new Error(new Exception(-31001, e.getMessage(), "amount")));
    }

    @ExceptionHandler({UnableCompleteException.class})
    private ResponseEntity<?> handler(UnableCompleteException e) {
        return ResponseEntity.ok(new Error(new Exception(-31008, e.getMessage(), "transaction")));
    }

    @ExceptionHandler({TransactionNotFoundException.class})
    private ResponseEntity<?> handler(TransactionNotFoundException e) {
        return ResponseEntity.ok(new Error(new Exception(-31003, e.getMessage(), "transaction")));
    }

    @ExceptionHandler({TransactionInWaiting.class})
    private ResponseEntity<?> handler(TransactionInWaiting e) {
        return ResponseEntity.ok(new Error(new Exception(-31099, e.getMessage(), "transaction")));
    }

    @ExceptionHandler({OrderAlreadyPayed.class})
    private ResponseEntity<?> handler(OrderAlreadyPayed e) {
        return ResponseEntity.ok(new Error(new Exception(-31099, e.getMessage(), "order payed/canceled")));
    }


}
