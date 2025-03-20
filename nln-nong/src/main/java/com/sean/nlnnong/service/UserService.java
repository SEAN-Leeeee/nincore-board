package com.sean.nlnnong.service;

import com.sean.nlnnong.domain.TeamUser;
import com.sean.nlnnong.repository.TeamUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final TeamUserRepository teamUserRepository;

    @Autowired
    public UserService(TeamUserRepository teamUserRepository) {
        this.teamUserRepository = teamUserRepository;
    }
    public List<TeamUser> getMyTeams(Long userId) {
        return teamUserRepository.findByUserIdAndStatus(userId, TeamUser.Status.ACTIVE);
    }
}
