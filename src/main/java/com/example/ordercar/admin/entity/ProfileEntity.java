package com.example.ordercar.admin.entity;
import com.example.ordercar.admin.enums.ProfileRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "profile")
public class ProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String sms_code;

    @Column
    private String full_name;

    @Column(unique = true)
    private String phone;

    @Enumerated(EnumType.STRING)
    private ProfileRole profileRole;

}
