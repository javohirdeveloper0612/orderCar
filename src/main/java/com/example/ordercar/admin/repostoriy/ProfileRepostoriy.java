package com.example.ordercar.admin.repostoriy;
import com.example.ordercar.admin.entity.ProfileEntity;
import com.example.ordercar.admin.enums.ProfileRole;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ProfileRepostoriy extends CrudRepository<ProfileEntity,Long> {

    List<ProfileEntity> findByProfileRole(ProfileRole role);

    Optional<ProfileEntity> findByIdAndProfileRole(Long aLong,ProfileRole role);
}
