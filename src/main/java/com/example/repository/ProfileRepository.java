package com.example.repository;

import com.example.entity.ProfileEntity;
import com.example.enums.ProfileRole;
import com.example.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<ProfileEntity, Long> {

    List<ProfileEntity> findAllByRole(ProfileRole role);

    Optional<ProfileEntity> findByPhone(String phone);
    boolean existsByChatIdAndRole(Long userId, ProfileRole role);

    @Modifying
    @Transactional
    @Query("update ProfileEntity p set p.status=?2 where p.chatId=?1")
    void  changeVisibleByUserid(Long chatId, Status status);

    boolean existsByPhone(String phone);

    boolean existsByChatId(Long chatId);
}
