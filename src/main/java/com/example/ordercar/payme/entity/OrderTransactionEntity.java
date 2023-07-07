package com.example.ordercar.payme.entity;

import com.example.ordercar.payme.enums.OrderCancelReason;
import com.example.ordercar.payme.enums.TransactionState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_transaction")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderTransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String paycomId;
    @Column
    private long paycomTime;
    @Column
    private long createTime;
    @Column
    private long performTime;
    @Column
    private long cancelTime;
    @Column
    private OrderCancelReason reason;
    @Column(nullable = false)
    private TransactionState state;

    @OneToOne
    private CustomOrder order;
}
