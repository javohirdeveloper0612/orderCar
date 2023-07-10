package com.example.repository;

import com.example.entity.OrderClientEntity;
import com.example.enums.Payment;
import com.example.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderClientRepository extends JpaRepository<OrderClientEntity, Long> {
    Boolean existsByOrderDateAndStatus(LocalDate orderDate, Status status);
    Optional<OrderClientEntity> findTop1ByChatIdAndStatusAndIsVisibleTrueOrderByOrderDateDesc(long chatId, Status status);
    Optional<OrderClientEntity> findTop1ByChatIdAndStatusAndIsVisibleTrueAndPhoneIsNullOrderByOrderDateDesc(long chatId, Status status);
    Optional<OrderClientEntity> findTop1ByChatIdAndStatusAndIsVisibleTrueAndPhoneIsNotNullOrderByOrderDateDesc(long chatId, Status status);
    List<OrderClientEntity> findAllByStatusAndDriverId(Status status, Long driverId);
    List<OrderClientEntity> findAllByPayment(Payment payment);

    List<OrderClientEntity> findAllByStatus(Status status);
    @Query(value = "SELECT * FROM order_client WHERE (status = 'ACTIVE' AND driver_id = :id)", nativeQuery = true)
    List<OrderClientEntity> findActiveOrders(@Param("id") Long id);
    @Query(value = "SELECT * FROM order_client WHERE (status = 'ACTIVE' AND driver_id IS NULL)", nativeQuery = true)
    List<OrderClientEntity> findOrderByStatus();


}
