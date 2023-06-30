package com.example.ordercar.service;

import com.example.ordercar.entity.OrderClientEntity;
import com.example.ordercar.entity.ProfileEntity;
import com.example.ordercar.enums.Status;
import com.example.ordercar.repository.ProfileRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private final ProfileRepository repository;

    public AuthService(ProfileRepository authRepository) {
        this.repository = authRepository;
    }

    public boolean isExists(Long userId) {
        return repository.existsByChatId(userId);
    }

    public void createProfile(ProfileEntity profileEntity) {
        Optional<ProfileEntity> optional = repository.findByPhone(profileEntity.getPhone());

        if (optional.isPresent()) {
            ProfileEntity profile  = optional.get();
            profile.setStatus(profileEntity.getStatus());
            profile.setSmsCode(profileEntity.getSmsCode());
            repository.save(profile);
        }
    }

    public boolean isExists(String phone) {
        return repository.existsByPhone(phone);
    }


    public void saveUserId(String phone,Long chatId) {
        Optional<ProfileEntity> optional = repository.findByPhone(phone);

        if (optional.isPresent()) {
            ProfileEntity profile  = optional.get();
            profile.setStatus(Status.ACTIVE);
            profile.setChatId(chatId);
            repository.save(profile);
        }
    }
}
