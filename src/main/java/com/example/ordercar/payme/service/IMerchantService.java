package com.example.ordercar.payme.service;


import com.example.ordercar.payme.dto.request.*;
import com.example.ordercar.payme.dto.result.*;
import com.example.ordercar.payme.exp.PerformTransaction;
import com.example.ordercar.payme.exp.PerformTransactionResult;

;

public interface IMerchantService {

    CheckPerformTransactionResult checkPerformTransaction(CheckPerformTransaction checkPerformTransaction);

    CreateTransactionResult createTransaction(CreateTransaction createTransaction);

    PerformTransactionResult performTransaction(PerformTransaction performTransaction);

    CancelTransactionResult cancelTransaction(CancelTransaction fromJson);

    CheckTransactionResult checkTransaction(CheckTransaction checkTransaction);

    Transactions getStatement(GetStatement getStatement);

    Result handle(String method, String params);
}
