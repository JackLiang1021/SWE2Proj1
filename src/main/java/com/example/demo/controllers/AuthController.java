package com.example.demo.controllers;

import com.example.demo.entities.Student;
import com.example.demo.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final StudentRepository studentRepo;

    public AuthController(StudentRepository studentRepo) {
        this.studentRepo = studentRepo;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest req) {
        Student s = studentRepo.findByEmail(req.email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        if (!s.getPassword().equals(req.password)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("studentId", s.getId());
        out.put("externalId", s.getExternalId());
        out.put("firstName", s.getFirstName());
        out.put("lastName", s.getLastName());
        out.put("email", s.getEmail());
        return out;
    }

    public static class LoginRequest {
        public String email;
        public String password;
    }
}
