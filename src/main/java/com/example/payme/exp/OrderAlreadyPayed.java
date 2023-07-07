package com.example.payme.exp;

public class OrderAlreadyPayed extends RuntimeException {
    public OrderAlreadyPayed(String message) {
        super(message);
    }
}
