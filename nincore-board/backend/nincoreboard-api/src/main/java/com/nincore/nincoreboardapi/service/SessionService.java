package com.nincore.nincoreboardapi.service;

import com.nincore.nincoreboardapi.domain.BoardSession;
import com.nincore.nincoreboardapi.dto.LoginRequest;
import com.nincore.nincoreboardapi.dto.LoginResponse;
import com.nincore.nincoreboardapi.dto.LogoutRequest;
import com.nincore.nincoreboardapi.repository.BoardSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
public class SessionService {
    private final BoardSessionRepository boardSessionRepository;
    @Lazy
    @Autowired
    private final StateService stateService;

    public SessionService(BoardSessionRepository boardSessionRepository, StateService stateService) {
        this.boardSessionRepository = boardSessionRepository;
        this.stateService = stateService;
    }

    @Transactional
    public void logout(LogoutRequest logoutRequest) {
        Optional<BoardSession> sessionOptional = boardSessionRepository.findById(logoutRequest.getSessionId());
        if (sessionOptional.isPresent()) {
            BoardSession session = sessionOptional.get();
            session.delete();
            boardSessionRepository.save(session);
        }
    }

    @Transactional(readOnly = true)
    public Optional<BoardSession> sessionExists(String ip, String password) {
        return boardSessionRepository.findFirstByIpAndPasswordAndIsDeletedFalse(ip, password);
    }

    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        Optional<BoardSession> existSession = sessionExists(loginRequest.getIp(), loginRequest.getPassword());

        if (existSession.isPresent()) {
            log.info("기존 세션에 접속합니다.");
            BoardSession session = existSession.get();
            stateService.get(session.getId().intValue()); // 추가
            return new LoginResponse(session.getId(), false);
        } else {
            log.info("새로운 세션을 생성합니다.");
            BoardSession newSession = new BoardSession(loginRequest.getIp(), loginRequest.getPassword());
            BoardSession savedSession = boardSessionRepository.save(newSession);
            stateService.get(savedSession.getId().intValue()); // 추가
            return new LoginResponse(savedSession.getId(), true);
        }
    }

    @Transactional(readOnly = true)
    public boolean isSessionActive(Long sessionId) {
        return boardSessionRepository.findByIdAndIsDeletedFalse(sessionId).isPresent();
    }

    @Transactional(readOnly = true)
    public Optional<BoardSession> getBoardSession(Long id) {
        return boardSessionRepository.findById(id);
    }
}
