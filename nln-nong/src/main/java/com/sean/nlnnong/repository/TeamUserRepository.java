package com.sean.nlnnong.repository;

import com.sean.nlnnong.domain.TeamUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamUserRepository extends JpaRepository<TeamUser, Long> {

    List<TeamUser> findByUserIdAndStatus(Long userId,TeamUser.Status status);
}
