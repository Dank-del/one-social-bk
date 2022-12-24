package com.sayan.onesocialbk.controllers;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.sayan.onesocialbk.models.Person;
import com.sayan.onesocialbk.repositories.PersonRepository;
import com.sayan.onesocialbk.utils.JwtUtils;

import io.jsonwebtoken.JwtException;

@RestController
public class UserController {
    @Autowired
    private PersonRepository personRepository;

    @RestControllerAdvice
    public class JwtExceptionHandler {

        @ExceptionHandler(JwtException.class)
        public ResponseEntity<String> handleJwtException(JwtException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        }
    }

    @ModelAttribute
    public void setUser(Model model) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        if (request.getCookies() == null) {
            return;
        }
        String jwtToken = request.getCookies()[0].getValue();
        if (jwtToken != null) {
            String userId = JwtUtils.parseUserIdFromJwtToken(jwtToken);
            Optional<Person> person = personRepository.findById(userId);
            if (person.isPresent()) {
                model.addAttribute("person", person.get());
                System.out.println("Model: " + model);
            }
        }
    }

    // @GetMapping("/users")
    // public List<Person> getAllUsers(@ModelAttribute Person user) {
    // // Perform authorization check here
    // if (user == null || !user.hasRole(Role.ADMIN)) {
    // // Return unauthorized response if the user is not an admin
    // throw new UnauthorizedException();
    // }
    // return personRepository.findAll();
    // }

    @GetMapping("/users/me")
    public ResponseEntity<Person> getCurrentUser(@ModelAttribute("person") Person person) {
        return ResponseEntity.ok().body(person);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<Person> getUserById(@PathVariable(value = "id") String userId)
            throws ResourceNotFoundException {
        Person user = personRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for this id :: " + userId));
        return ResponseEntity.ok().body(user);
    }

    @PostMapping("/users/signup")
    public ResponseEntity<Person> createUser(@Valid @RequestBody Person user, HttpServletResponse response) {
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
        Person newUser = personRepository.save(user);
        setJwtCookie(response, newUser);
        return ResponseEntity.ok().body(newUser);
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

    private String setJwtCookie(HttpServletResponse response, Person user) {
        String jwtToken = JwtUtils.generateJwtToken(user);
        Cookie jwtCookie = new Cookie("jwt", jwtToken);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setMaxAge((int) TimeUnit.DAYS.toSeconds(1));
        response.addCookie(jwtCookie);
        return jwtToken;
    }

    @PostMapping("/users/login")
    public ResponseEntity<Person> loginUser(@Valid @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {
        Optional<Person> existingUser = personRepository.findByUsername(loginRequest.getUsername());
        // System.out.println(existingUser);
        if (existingUser.isPresent() && existingUser.get().getPassword().equals(loginRequest.getPassword())) {
            setJwtCookie(response, existingUser.get());
            return ResponseEntity.ok().body(existingUser.get());
        }
        return ResponseEntity.badRequest().body(null);
    }
}