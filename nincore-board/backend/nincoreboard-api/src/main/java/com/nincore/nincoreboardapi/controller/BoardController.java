package com.nincore.nincoreboardapi.controller;


import com.nincore.nincoreboardapi.dto.LoginRequest;
import com.nincore.nincoreboardapi.dto.LoginResponse;
import com.nincore.nincoreboardapi.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BoardController {
    private final SessionService sessionService;

    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {


        return ResponseEntity.ok().body(sessionService.login(loginRequest));
    }

}
