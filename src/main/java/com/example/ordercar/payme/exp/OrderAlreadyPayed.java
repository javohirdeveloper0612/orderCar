package com.example.ordercar.payme.exp;

public class OrderAlreadyPayed extends RuntimeException {
    public OrderAlreadyPayed(String message) {
        super(message);
    }
}
