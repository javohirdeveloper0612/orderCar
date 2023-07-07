package com.example.payme.exp;

public class TransactionInWaiting extends RuntimeException {
    public TransactionInWaiting(String message) {
        super(message);
    }
}
