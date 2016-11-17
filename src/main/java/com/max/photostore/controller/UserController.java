package com.max.photostore.controller;

import com.max.photostore.exception.InternalServerErrorException;
import com.max.photostore.exception.SignupException;
import com.max.photostore.request.RegisterUser;
import com.max.photostore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(
            headers = {"content-type=application/json"},
            value = "/signup", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> signup(@RequestBody RegisterUser request) {
        try {
            userService.signup(request.username, request.email, request.password);
        } catch (SignupException | InternalServerErrorException e) {
            return e.buildResponse();
        }
        return ResponseEntity.ok().build();
    }
}
