
package com.clinic.navjivan.controller;

import java.util.Collections;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Allow CORS from both local development and tunnel URLs
// Supports: localhost:3000, devtunnel frontend, and any other origins
@CrossOrigin(origins = {
        "http://localhost:3000",
        "https://dq13wjxp-3000.inc1.devtunnels.ms"
}, maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        // Placeholder for authentication logic
        if ("Ajit".equals(loginRequest.getUsername()) && "ajubhai".equals(loginRequest.getPassword())) {
            return ResponseEntity.ok(Collections.singletonMap("message", "Login successful!"));
        } else {
            return ResponseEntity.status(401).body(Collections.singletonMap("message", "Invalid credentials"));
        }
    }
}
