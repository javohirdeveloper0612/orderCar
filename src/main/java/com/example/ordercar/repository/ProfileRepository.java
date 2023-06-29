package com.example.ordercar.repository;

import com.example.ordercar.entity.ProfileEntity;
import com.example.ordercar.enums.ProfileRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<ProfileEntity, Long> {

    List<ProfileEntity> findAllByRole(ProfileRole role);

    Optional<ProfileEntity> findByPhone(String phone);
}
