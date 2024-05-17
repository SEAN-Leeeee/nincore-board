package me.sean.nlnnong.Controller;

import me.sean.nlnnong.Domain.User;
import me.sean.nlnnong.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserRestController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/api/hello")
    public String hello() {
        return "hello";
    }

    @PostMapping("/api/user")
    public void userSave(@RequestBody User user) {

        userRepository.save(user);
    }
}
