package com.nincore.nincoreboardapi.domain;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class Action {
    private String type;
    private JsonNode payload;
}
