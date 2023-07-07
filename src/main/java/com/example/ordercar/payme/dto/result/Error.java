package com.example.ordercar.payme.dto.result;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class Error {
    private Exception error;
}
