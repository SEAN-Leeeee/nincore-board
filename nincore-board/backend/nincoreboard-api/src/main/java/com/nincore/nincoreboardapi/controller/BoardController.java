package com.nincore.nincoreboardapi.controller;


import com.nincore.nincoreboardapi.dto.LoginRequest;
import com.nincore.nincoreboardapi.dto.LoginResponse;
import com.nincore.nincoreboardapi.dto.LogoutRequest;
import com.nincore.nincoreboardapi.service.SessionService;
import com.nincore.nincoreboardapi.service.StateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BoardController {
    private final SessionService sessionService;
    private final StateService stateService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {

        return ResponseEntity.ok().body(sessionService.login(loginRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequest logoutRequest) {
        sessionService.logout(logoutRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/shutdown")
    public ResponseEntity<Void> shutdown(@RequestBody LogoutRequest logoutRequest) {
        stateService.removeSessionState(logoutRequest.getSessionId().intValue());
        sessionService.logout(logoutRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/session/{sessionId}/status")
    public ResponseEntity<Void> getSessionStatus(@PathVariable Long sessionId) {
        if (sessionService.isSessionActive((sessionId))) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(401).build();
        }
    }
}
