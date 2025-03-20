package com.sean.nlnnong.repository;

import com.sean.nlnnong.domain.TeamUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<TeamUser, Long> {
}
