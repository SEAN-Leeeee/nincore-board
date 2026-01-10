package com.nincore.nincoreboardapi.service;

import com.nincore.nincoreboardapi.domain.BoardSession;
import com.nincore.nincoreboardapi.dto.LoginRequest;
import com.nincore.nincoreboardapi.dto.LoginResponse;
import com.nincore.nincoreboardapi.repository.BoardSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final BoardSessionRepository boardSessionRepository;

    @Transactional(readOnly = true)
    public Optional<BoardSession> sessionExists(String ip, String password) {
        return boardSessionRepository.findByIdAndPassword(ip, password);
    }



    public void createSession(String ip, String password) {
        BoardSession newSession = new BoardSession(ip, password);
        boardSessionRepository.save(newSession);
    }

    public LoginResponse login(LoginRequest loginRequest) {
        Optional<BoardSession> session = sessionExists(loginRequest.getIp(), loginRequest.getPassword());

        session.isPresent();
    }
}
