package com.example.payme.exp;

public class UnableCancelTransaction extends RuntimeException {
    public UnableCancelTransaction(String message) {
        super(message);
    }
}
