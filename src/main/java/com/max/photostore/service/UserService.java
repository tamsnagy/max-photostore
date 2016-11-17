package com.max.photostore.service;

import com.max.photostore.exception.InternalServerErrorException;
import com.max.photostore.exception.SignupException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.Principal;

public interface UserService {
    void signup(String username, String email, String password) throws SignupException, InternalServerErrorException;

    boolean login(String username, String password) throws InternalServerErrorException;

    void logout(HttpSession session);
}
