package com.example.payme.repository;

import com.example.payme.entity.OrderTransactionEntity;
import com.example.payme.enums.TransactionState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<OrderTransactionEntity, Long> {
    Optional<OrderTransactionEntity> findByPaycomId(String id);

    Optional<List<OrderTransactionEntity>> findByPaycomTimeBetweenAndState(Long from, Long to, TransactionState state);

    Optional<OrderTransactionEntity> findByOrder_Id(long id);
}
