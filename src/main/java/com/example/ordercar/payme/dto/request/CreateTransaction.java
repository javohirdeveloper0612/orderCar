package com.example.ordercar.payme.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateTransaction {
    private String id;
    private long time;
    private Long amount;
    private Account account;
}
