package com.sean.nlnnong.controller;

import com.sean.nlnnong.domain.TeamUser;
import com.sean.nlnnong.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserRestController {
    private final UserService userService;
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<TeamUser>> getMyTeams(@PathVariable long id) {
        return ResponseEntity
                .ok()
                .body(userService.getMyTeams(id)); // 클라이언트에 JSON으로 {"hello": "Hello, World!"} 반환
    }
}
