package com.nincore.nincoreboardapi.repository;

import com.nincore.nincoreboardapi.domain.BoardSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoardSessionRepository extends JpaRepository<BoardSession, Long> {
    Optional<BoardSession> findFirstByIpAndPasswordAndIsDeletedFalse(String ip, String password);

    Optional<BoardSession> findByIdAndIsDeletedFalse(Long id);
}
