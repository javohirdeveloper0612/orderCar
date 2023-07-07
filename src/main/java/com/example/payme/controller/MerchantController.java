package com.example.payme.controller;

import com.example.payme.dto.result.Error;
import com.example.payme.dto.result.Exception;
import com.example.payme.service.IMerchantService;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MerchantController {
    private final IMerchantService merchantService;
    @Value("${paycom.authorization}")
    private String paycomAuthorization;

    @PostMapping
    public ResponseEntity<?> handle(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.equals(paycomAuthorization)) {
            return ResponseEntity.ok(new Error(new Exception(-32504, "authorization failed", "authorization")));
        }
        try {
            String json = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            ObjectMapper mapper = new ObjectMapper();
            Map map = mapper.readValue(json, Map.class);
            String method = map.get("method").toString();
            String params = map.get("params").toString();
            if (method == null || method.isBlank() || params == null || params.isBlank()) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(merchantService.handle(method, params));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
