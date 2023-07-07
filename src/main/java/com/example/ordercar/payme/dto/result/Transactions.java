package com.example.ordercar.payme.dto.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class Transactions {
    private List<GetStatementResult> transactions;
}
