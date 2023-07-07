package com.example.ordercar.payme.exp;

public class TransactionInWaiting extends RuntimeException {
    public TransactionInWaiting(String message) {
        super(message);
    }
}
