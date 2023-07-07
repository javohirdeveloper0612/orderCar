package com.example.payme.exp;

public class WrongAmountException extends RuntimeException {
    public WrongAmountException(String message) {
        super(message);
    }
}
