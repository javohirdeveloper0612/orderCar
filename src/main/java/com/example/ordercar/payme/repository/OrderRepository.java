package com.example.ordercar.payme.repository;

import com.example.ordercar.payme.entity.CustomOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<CustomOrder, Long> {
}
