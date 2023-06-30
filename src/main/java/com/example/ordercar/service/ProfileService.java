package com.example.ordercar.service;

import com.example.ordercar.enums.ProfileRole;
import com.example.ordercar.repository.ProfileRepository;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {
    private final ProfileRepository repository;

    public ProfileService(ProfileRepository repository) {
        this.repository = repository;
    }


    public boolean isDriver(Long userId) {
        return repository.existsByChatIdAndRole(userId, ProfileRole.DRIVER);
    }
}
