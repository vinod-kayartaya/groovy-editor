package com.example.groovyeditor.controller;

import com.example.groovyeditor.service.GroovyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@RestController
public class GroovyController {
    private static final Logger logger = LoggerFactory.getLogger(GroovyController.class);

    @Autowired
    private GroovyService groovyService;

    @PostMapping("/api/execute")
    public ResponseEntity<Map<String, String>> executeGroovy(@RequestBody String code) {
        try {
            logger.debug("Received code execution request");
            String output = groovyService.executeGroovyCode(code);
            return ResponseEntity.ok(Map.of("output", output));
        } catch (Exception e) {
            logger.error("Error processing code execution request", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getClass().getSimpleName() + ": " + e.getMessage()));
        }
    }
}