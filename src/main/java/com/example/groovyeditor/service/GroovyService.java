package com.example.groovyeditor.service;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.security.Permission;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class GroovyService {
    private static final Logger logger = LoggerFactory.getLogger(GroovyService.class);

    private static final Set<String> ALLOWED_PACKAGES = new HashSet<>(Arrays.asList(
            "java.lang",
            "java.util",
            "groovy.lang",
            "groovy.util"));

    public String executeGroovyCode(String code) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        PrintStream oldOut = System.out;
        SecurityManager oldSecurityManager = System.getSecurityManager();

        try {
            logger.debug("Executing Groovy code: {}", code);

            // Set up security manager
            System.setSecurityManager(new SecurityManager() {
                @Override
                public void checkPermission(Permission perm) {
                    logger.debug("Checking permission: {}", perm);

                    // Allow basic JVM operations
                    if (perm instanceof RuntimePermission) {
                        String name = perm.getName();
                        if (name.equals("createClassLoader") ||
                                name.equals("getClassLoader") ||
                                name.equals("setSecurityManager") ||
                                name.equals("createSecurityManager") ||
                                name.equals("getenv.*") ||
                                name.equals("exitVM") ||
                                name.equals("setIO")) {
                            return;
                        }
                    }

                    // Block file operations
                    if (perm instanceof java.io.FilePermission) {
                        String actions = perm.getActions();
                        if (actions.contains("write") || actions.contains("delete") || actions.contains("execute")) {
                            throw new SecurityException("File write/delete/execute operations are not allowed");
                        }
                    }

                    // Block network operations
                    if (perm instanceof java.net.SocketPermission) {
                        throw new SecurityException("Network operations are not allowed");
                    }

                    // Block property access
                    if (perm instanceof java.util.PropertyPermission) {
                        String actions = perm.getActions();
                        if (actions.contains("write")) {
                            throw new SecurityException("Property write operations are not allowed");
                        }
                    }

                    // Block process operations
                    if (perm instanceof java.lang.RuntimePermission) {
                        String name = perm.getName();
                        if (name.equals("exec") ||
                                name.equals("forkAndExec") ||
                                name.equals("loadLibrary") ||
                                name.equals("load")) {
                            throw new SecurityException("Process operations are not allowed");
                        }
                    }
                }
            });

            // Redirect System.out to our stream
            System.setOut(printStream);

            // Create Groovy shell with binding and custom class loader
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
        } catch (SecurityException e) {
            logger.warn("Security exception while executing Groovy code: {}", e.getMessage());
            return "Error: " + e.getMessage();
        } catch (Exception e) {
            logger.error("Error executing Groovy code", e);
            return "Error: " + e.getClass().getSimpleName() + ": " + e.getMessage();
        } finally {
            // Restore the original System.out and security manager
            System.setOut(oldOut);
            System.setSecurityManager(oldSecurityManager);
        }
    }
}