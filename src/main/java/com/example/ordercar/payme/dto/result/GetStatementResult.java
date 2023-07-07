package com.example.ordercar.payme.dto.result;

import com.example.ordercar.payme.dto.request.Account;
import lombok.*;


@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetStatementResult {
    private String id;
    private long time;
    private long amount;
    private Account account;
    private long create_time;
    private long perform_time;

    private long cancel_time;
    private String transaction;
    private Integer state;

    private Integer reason;

}
