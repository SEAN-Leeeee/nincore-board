package com.nincore.nincoreboardapi.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
@Slf4j
public class LogController {

    @GetMapping("/")
    public void log() {
        log.trace("trace.message");
        log.debug("debug.message");
        log.info("info.message");
        log.warn("warn.message");
        log.error("error.message");
    }
}
