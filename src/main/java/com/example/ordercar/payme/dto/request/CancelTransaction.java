package com.example.ordercar.payme.dto.request;

import com.example.ordercar.payme.enums.OrderCancelReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CancelTransaction {
    private String id;
    private OrderCancelReason reason;
}
