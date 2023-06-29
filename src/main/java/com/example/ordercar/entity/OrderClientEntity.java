package com.example.ordercar.entity;

import com.example.ordercar.enums.Payment;
import com.example.ordercar.enums.ProfileStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderClientEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long chatId;

    @Column
    private String fullName;

    @Column
    private String phone;

    @Column
    private String smsCode;

    @OneToOne(cascade = CascadeType.ALL)
    private LocationClient fromWhere;

    @OneToOne(cascade = CascadeType.ALL)
    private LocationClient toWhere;


    private LocalDate orderDate;

    @Enumerated(EnumType.STRING)
    private Payment cashOrOnline;

    @Enumerated(EnumType.STRING)
    private ProfileStatus status;


}
