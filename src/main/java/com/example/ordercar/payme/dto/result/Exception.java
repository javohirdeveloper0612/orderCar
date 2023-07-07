package com.example.ordercar.payme.dto.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Exception {
    private Integer code;
    private String message;
    private String data;
}
