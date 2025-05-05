package com.example.groovyeditor.controller;

import com.example.groovyeditor.service.GroovyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class GroovyController {

    @Autowired
    private GroovyService groovyService;

    @PostMapping("/api/execute")
    public ResponseEntity<Map<String, String>> executeGroovy(@RequestBody String code) {
        try {
            String output = groovyService.executeGroovyCode(code);
            return ResponseEntity.ok(Map.of("output", output));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("error", e.getMessage()));
        }
    }
}