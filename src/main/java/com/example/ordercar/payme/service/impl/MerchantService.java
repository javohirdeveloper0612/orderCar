package com.example.ordercar.payme.service.impl;

import com.example.ordercar.controller.TransportUslugaController;
import com.example.ordercar.entity.OrderClientEntity;
import com.example.ordercar.enums.Status;
import com.example.ordercar.payme.dto.request.*;
import com.example.ordercar.payme.dto.result.*;
import com.example.ordercar.payme.entity.OrderTransactionEntity;

import com.example.ordercar.payme.enums.Method;
import com.example.ordercar.payme.enums.TransactionState;
import com.example.ordercar.payme.exp.*;
import com.example.ordercar.payme.repository.TransactionRepository;
import com.example.ordercar.payme.service.IMerchantService;
import com.example.ordercar.repository.OrderClientRepository;
import com.example.ordercar.service.TransportUslugaService;
import com.google.gson.Gson;
import com.sun.jdi.connect.spi.TransportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class MerchantService implements IMerchantService {
    private static final Long time_expired = 43_200_000L;
    private final OrderClientRepository orderRepository;
    private final TransactionRepository transactionRepository;
    private final TransportUslugaController transportUslugaController;

    @Override
    public CheckPerformTransactionResult checkPerformTransaction(CheckPerformTransaction checkPerformTransaction) {
        Optional<OrderClientEntity> optional = orderRepository.findById(Long.valueOf(checkPerformTransaction.getAccount().getOrder_id()));
        if (optional.isEmpty()) {
            throw new OrderNotFoundException("Order not found");
        }
        OrderClientEntity orderClient = optional.get();
        if (!checkPerformTransaction.getAmount().equals(orderClient.getAmount())) {
            throw new WrongAmountException("Wrong amount");
        }

        if (orderClient.getStatus().equals(Status.ACTIVE)) {
            throw new OrderAlreadyPayed("invoice already paid/cancelled");
        }
        return new CheckPerformTransactionResult(true, new DetailResult(0, List.of(new Item("10107002001000000",
                "Услуги по перевозке грузов автомобильным транспортом",
                orderClient.getAmount(), 1, "1209885", 0))));
    }

    @Override
    public CreateTransactionResult createTransaction(CreateTransaction createTransaction) {
        Optional<OrderTransactionEntity> optional = transactionRepository.findByPaycomId(createTransaction.getId());
        if (optional.isEmpty()) {
            if (checkPerformTransaction(new CheckPerformTransaction(createTransaction.getAmount(),
                    createTransaction.getAccount())).isAllow()) {
                Optional<OrderTransactionEntity> optional1 = transactionRepository.findByOrder_Id(Long.parseLong(createTransaction.getAccount().getOrder_id()));

                if (optional1.isPresent()) {
                    throw new TransactionInWaiting("transaction");
                }

                OrderTransactionEntity newTransaction = new OrderTransactionEntity();
                newTransaction.setPaycomId(createTransaction.getId());
                newTransaction.setPaycomTime(createTransaction.getTime());
                newTransaction.setCreateTime(new Date().getTime());
                newTransaction.setState(TransactionState.STATE_IN_PROGRESS);
                newTransaction.setOrder(orderRepository.findById(Long.parseLong(createTransaction.getAccount().getOrder_id())).get());
                transactionRepository.save(newTransaction);
                return new CreateTransactionResult((newTransaction.getCreateTime()), newTransaction.getId().toString(), newTransaction.getState().getCode());
            }
        } else {
            OrderTransactionEntity transaction = optional.get();
            if (transaction.getState().equals(TransactionState.STATE_IN_PROGRESS)) {
                if (System.currentTimeMillis() - transaction.getPaycomTime() > time_expired) {
                    throw new UnableCompleteException("Unable to complete operation");
                } else {
                    return new CreateTransactionResult((transaction.getCreateTime()), transaction.getId().toString(), transaction.getState().getCode());
                }
            } else {
                throw new UnableCompleteException("Unable to complete operation");
            }
        }
        throw new

                UnableCompleteException("Unable to complete operation");

    }

    @Override
    public PerformTransactionResult performTransaction(PerformTransaction performTransaction) {
        Optional<OrderTransactionEntity> optional = transactionRepository.findByPaycomId(performTransaction.getId());
        if (optional.isEmpty()) {
            throw new TransactionNotFoundException("Transaction not found");
        }
        OrderTransactionEntity transaction = optional.get();
        if (transaction.getState().equals(TransactionState.STATE_IN_PROGRESS)) {
            if (System.currentTimeMillis() - transaction.getPaycomTime() > time_expired) {
                transaction.setState(TransactionState.STATE_CANCELED);
                transactionRepository.save(transaction);
                throw new UnableCompleteException("Unable to complete operation");
            } else {
                transaction.setState(TransactionState.STATE_DONE);
                transaction.setPerformTime(new Date().getTime());
                OrderClientEntity order = transaction.getOrder();
                order.setStatus(Status.ACTIVE);
                orderRepository.save(order);
                transportUslugaController.acceptOrder(order.getChatId(), order.getId());

                transactionRepository.save(transaction);
                return new PerformTransactionResult(transaction.getId().toString(), transaction.getPerformTime(), transaction.getState().getCode());
            }
        } else if (transaction.getState().equals(TransactionState.STATE_DONE)) {
            return new PerformTransactionResult(transaction.getId().toString(), transaction.getPerformTime(), transaction.getState().getCode());
        } else {
            throw new UnableCompleteException("Unable to complete operation");
        }
    }

    @Override
    public CancelTransactionResult cancelTransaction(CancelTransaction cancelTransaction) {
        Optional<OrderTransactionEntity> optional = transactionRepository.findByPaycomId(cancelTransaction.getId());

        if (optional.isEmpty()) {
            throw new TransactionNotFoundException("Transaction not found");
        }
        OrderTransactionEntity transaction = optional.get();
        if (transaction.getState().equals(TransactionState.STATE_IN_PROGRESS)) {
            transaction.setState(TransactionState.STATE_CANCELED);
        } else if (transaction.getState().equals(TransactionState.STATE_DONE)) {
            if (transaction.getOrder().getStatus().equals(Status.FINISHED) || transaction.getOrder().getStatus().equals(Status.ACTIVE)) {
                throw new UnableCancelTransaction("Unable cancel transaction");
            } else {
                transaction.setState(TransactionState.STATE_POST_CANCELED);
            }
        } else {
            transaction.setState(TransactionState.STATE_CANCELED);
        }

        transaction.setCancelTime(new Date().getTime());
        transaction.setReason(cancelTransaction.getReason());
        transactionRepository.save(transaction);
        return new CancelTransactionResult(transaction.getId().toString(), transaction.getCancelTime(), transaction.getState().getCode());
    }

    @Override
    public Transactions getStatement(GetStatement getStatement) {
        Optional<List<OrderTransactionEntity>> optional = transactionRepository.findByPaycomTimeBetweenAndState(getStatement.getFrom(), getStatement.getTo(), TransactionState.STATE_DONE);
        if (optional.isEmpty()) {
            return new Transactions(new ArrayList<>());
        }
        List<OrderTransactionEntity> transactionEntityList = optional.get();
        Function<OrderTransactionEntity, GetStatementResult> mapper = (entity) -> {
            GetStatementResult getStatementResult = GetStatementResult.builder()
                    .id(entity.getPaycomId())
                    .time(entity.getPaycomTime())
                    .amount(entity.getOrder().getAmount())
                    .account(new Account(entity.getOrder().getId().toString()))
                    .create_time(entity.getCreateTime())
                    .perform_time(entity.getPerformTime())
                    .cancel_time(entity.getCancelTime())
                    .transaction(entity.getId().toString()).build();
            if (entity.getState() != null) {
                getStatementResult.setState(entity.getState().getCode());
            }
            if (entity.getReason() != null) {
                getStatementResult.setReason(entity.getReason().getCode());
            }
            return getStatementResult;
        };

        List<GetStatementResult> collect = transactionEntityList.stream().map(mapper).collect(Collectors.toList());
        return new Transactions(collect);
    }


    @Override
    public CheckTransactionResult checkTransaction(CheckTransaction checkTransaction) {
        Optional<OrderTransactionEntity> optional = transactionRepository.findByPaycomId(checkTransaction.getId());
        if (optional.isEmpty()) {
            throw new TransactionNotFoundException("Transaction not found");
        }
        OrderTransactionEntity transaction = optional.get();
        CheckTransactionResult result = CheckTransactionResult.builder()
                .create_time(transaction.getCreateTime())
                .perform_time(transaction.getPerformTime())
                .cancel_time(transaction.getCancelTime())
                .transaction(transaction.getId().toString()).build();

        if (transaction.getState() != null) {
            result.setState(transaction.getState().getCode());
        }
        if (transaction.getReason() != null) {
            result.setReason(transaction.getReason().getCode());
        }
        return result;
    }

    @Override
    public Result handle(String method, String params) {
        Result result = new Result();
        Gson gson = new Gson();

        switch (Method.valueOf(method)) {
            case CheckPerformTransaction ->
                    result.setResult(checkPerformTransaction(gson.fromJson(params, CheckPerformTransaction.class)));
            case CreateTransaction ->
                    result.setResult(createTransaction(gson.fromJson(params, CreateTransaction.class)));
            case PerformTransaction ->
                    result.setResult(performTransaction(gson.fromJson(params, PerformTransaction.class)));
            case CheckTransaction -> result.setResult(checkTransaction(gson.fromJson(params, CheckTransaction.class)));
            case CancelTransaction ->
                    result.setResult(cancelTransaction(gson.fromJson(params, CancelTransaction.class)));
            case GetStatement -> result.setResult(getStatement(gson.fromJson(params, GetStatement.class)));
        }
        return result;
    }
}
