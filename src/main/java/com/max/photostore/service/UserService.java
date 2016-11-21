package com.max.photostore.service;

import com.max.photostore.exception.InternalServerErrorException;
import com.max.photostore.exception.PhotostoreException;
import com.max.photostore.exception.SignupException;
import com.max.photostore.response.FindUser;
import com.max.photostore.response.Login;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.Principal;

public interface UserService {
    void signup(String username, String email, String password) throws SignupException, InternalServerErrorException;

    Login login(String username, String password, HttpServletResponse response) throws PhotostoreException;

    void logout(HttpSession session, HttpServletResponse response);

    FindUser findUser(String query);
}
