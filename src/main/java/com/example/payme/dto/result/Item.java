package com.example.payme.dto.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Item {
    private String code;
    private String title;
    private long price;
    private int count;
    private String package_code;
    private int vat_percent;
}
