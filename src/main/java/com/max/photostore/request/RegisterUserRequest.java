package com.max.photostore.request;

public class RegisterUserRequest {
    public String username;
    public String email;
    public String password;

    public RegisterUserRequest() {}

    public RegisterUserRequest(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
