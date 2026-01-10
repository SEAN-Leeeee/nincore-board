package com.nincore.nincoreboardapi.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    public String ip;
    public String password;
}
