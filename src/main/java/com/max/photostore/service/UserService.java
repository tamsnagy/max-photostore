package com.max.photostore.service;

import com.max.photostore.exception.InternalServerErrorException;
import com.max.photostore.exception.SignupException;

public interface UserService {
    void signup(String username, String email, String password) throws SignupException, InternalServerErrorException;

    boolean login(String username, String password) throws InternalServerErrorException;
}
