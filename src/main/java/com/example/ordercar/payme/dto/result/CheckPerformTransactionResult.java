package com.example.ordercar.payme.dto.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CheckPerformTransactionResult {
    public boolean allow;
    private DetailResult detail;

}
