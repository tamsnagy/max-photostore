package com.max.photostore.request;

public class RegisterUser {
    public String username;
    public String email;
    public String password;

    public RegisterUser() {}

    public RegisterUser(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
