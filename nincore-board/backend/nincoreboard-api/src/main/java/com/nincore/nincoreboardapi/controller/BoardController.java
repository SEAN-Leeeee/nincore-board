package com.nincore.nincoreboardapi.controller;


import com.nincore.nincoreboardapi.dto.LoginRequest;
import com.nincore.nincoreboardapi.dto.LoginResponse;
import com.nincore.nincoreboardapi.dto.LogoutRequest;
import com.nincore.nincoreboardapi.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BoardController {
    private final SessionService sessionService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {

        return ResponseEntity.ok().body(sessionService.login(loginRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequest logoutRequest) {
        sessionService.logout(logoutRequest);
        return ResponseEntity.ok().build();
    }

}
