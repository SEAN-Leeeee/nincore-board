package com.sean.nlnnong.domain;

import com.sean.nlnnong.repository.TeamUserRepository;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class TeamUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User user;
    @ManyToOne
    private Team team;
    private boolean isLeader;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        WAITING, ACTIVE, WITHDRAWN
    }

}
