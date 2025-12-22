package com.nincore.nincoreboardapi.domain;

import lombok.Data;

@Data
public class Command {
    private String type;
    private String payload;
}
