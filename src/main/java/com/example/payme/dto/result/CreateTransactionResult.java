package com.example.payme.dto.result;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class CreateTransactionResult {
    private long create_time;
    private String transaction;
    private Integer state;

}
