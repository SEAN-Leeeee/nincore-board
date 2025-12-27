package com.nincore.nincoreboardapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class NincoreboardApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(NincoreboardApiApplication.class, args);
    }

}
