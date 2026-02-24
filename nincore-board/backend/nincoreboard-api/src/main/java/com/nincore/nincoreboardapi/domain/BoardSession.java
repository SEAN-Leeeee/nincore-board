package com.nincore.nincoreboardapi.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.function.Consumer;

@Entity
@Getter
@NoArgsConstructor
@Table(indexes = {
        @Index(name = "idx_board_session_ip_password", columnList = "ip, password, isDeleted")
})public class BoardSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ip;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Boolean isDeleted;

    public BoardSession(String ip, String password) {
        this.ip = ip;
        this.password = password;
        this.isDeleted = false;
    }


    public void delete() {
        this.isDeleted = true;
    }
}
