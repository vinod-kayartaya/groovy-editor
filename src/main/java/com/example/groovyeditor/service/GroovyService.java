package com.example.groovyeditor.service;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

@Service
public class GroovyService {

    public String executeGroovyCode(String code) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        PrintStream oldOut = System.out;

        try {
            // Redirect System.out to our stream
            System.setOut(printStream);

            // Create Groovy shell with binding
            Binding binding = new Binding();
            GroovyShell shell = new GroovyShell(binding);

            // Execute the code
            Object result = shell.evaluate(code);

            // Get the output
            String output = outputStream.toString();

            // If there's a result and no output, return the result
            if (output.isEmpty() && result != null) {
                return result.toString();
            }

            return output;
        } finally {
            // Restore the original System.out
            System.setOut(oldOut);
        }
    }
}