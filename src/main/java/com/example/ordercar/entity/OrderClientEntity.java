package com.example.ordercar.entity;

import com.example.ordercar.enums.Payment;
import com.example.ordercar.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "order_client")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderClientEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long chatId;

    @Column
    private String fullName;

    @Column
    private String phone;

    @Column
    private Long amount;

    @Column
    private String smsCode;

    @OneToOne(cascade = CascadeType.ALL)
    private LocationClient fromWhere;

    @OneToOne(cascade = CascadeType.ALL)
    private LocationClient toWhere;
    @Column
    private double onlineMoney;

    @Column
    private LocalDate orderDate;

    @Enumerated(EnumType.STRING)
    private Payment payment;

    @Enumerated(EnumType.STRING)
    private Status status = Status.NOTACTIVE;

    @Column
    private Long driverId;

    @Column(name = "is_visible")
    private boolean isVisible = true;

}
