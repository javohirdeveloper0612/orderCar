package com.example.ordercar.payme.exp;

public class UnableCancelTransaction extends RuntimeException {
    public UnableCancelTransaction(String message) {
        super(message);
    }
}
