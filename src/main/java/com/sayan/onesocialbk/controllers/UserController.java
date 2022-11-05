package com.sayan.onesocialbk.controllers;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sayan.onesocialbk.models.Person;
import com.sayan.onesocialbk.repositories.PersonRepository;

@RestController
public class UserController {
    @Autowired
    private PersonRepository personRepository;

    @GetMapping("/users")
    public List<Person> getAllUsers() {
        return personRepository.findAll();
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<Person> getUserById(@PathVariable(value = "id") String userId)
            throws ResourceNotFoundException {
        Person user = personRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for this id :: " + userId));
        return ResponseEntity.ok().body(user);
    }

    @PostMapping("/users/signup")
    public ResponseEntity<Person> createUser(@Valid @RequestBody Person user) {
        Optional<Person> existingUser = personRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body(null);
        }
        existingUser = personRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body(null);
        }
        existingUser = personRepository.findByPhone(user.getPhone());
        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok().body(personRepository.save(user));
    }

    static class LoginRequest {

        @NotBlank(message = "Username is mandatory")
        private String username;

        @NotBlank(message = "Password is mandatory")
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    @PostMapping("/users/login")
    public ResponseEntity<Person> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        Optional<Person> existingUser = personRepository.findByUsername(loginRequest.getUsername());
        if (existingUser.isPresent() && existingUser.get().getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.ok().body(existingUser.get());
        }
        return ResponseEntity.badRequest().body(null);
    }
}