package com.example.ordercar.repository;

import com.example.ordercar.entity.OrderClientEntity;
import com.example.ordercar.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderClientRepository extends JpaRepository<OrderClientEntity, Long> {
    Boolean existsByOrderDate(LocalDate orderDate);

    List<OrderClientEntity> findAllByStatus(Status status);

    Optional<OrderClientEntity> findByStatus(Status status);



}
