package com.example.ordercar.repository;

import com.example.ordercar.entity.OrderClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface OderClientRepository extends JpaRepository<OrderClientEntity, Long> {
    Boolean existsByOrderDate(LocalDate orderDate);


}
