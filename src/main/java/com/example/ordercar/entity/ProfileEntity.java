package com.example.ordercar.entity;

import com.example.ordercar.enums.ProfileRole;
import com.example.ordercar.enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    private String phone;
    private Long chatId;
    @Enumerated(EnumType.STRING)
    private ProfileRole role;
    @Enumerated(EnumType.STRING)
    private Status status;


}
