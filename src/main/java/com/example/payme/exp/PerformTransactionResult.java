package com.example.payme.exp;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PerformTransactionResult {
    private String transaction;
    private Long perform_time;
    private Integer state;
}
