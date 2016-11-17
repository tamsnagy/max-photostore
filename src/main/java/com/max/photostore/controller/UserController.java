package com.max.photostore.controller;

import com.max.photostore.exception.InternalServerErrorException;
import com.max.photostore.exception.SignupException;
import com.max.photostore.request.LoginRequest;
import com.max.photostore.request.RegisterUserRequest;
import com.max.photostore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping(
        headers = {"content-type=application/json"},
        value = "/api")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> signup(@RequestBody RegisterUserRequest request) {
        try {
            userService.signup(request.username, request.email, request.password);
        } catch (SignupException | InternalServerErrorException e) {
            return e.buildResponse();
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            return userService.login(request.username, request.password)
                ? ResponseEntity.ok().build()
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (InternalServerErrorException e) {
            return e.buildResponse();
        }
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> logout(HttpSession session) {
        userService.logout(session);
        return ResponseEntity.ok().build();
    }
}
