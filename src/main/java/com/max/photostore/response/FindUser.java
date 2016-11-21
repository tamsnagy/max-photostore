package com.max.photostore.response;

import com.max.photostore.domain.AppUser;

import java.util.List;

public class FindUser {
    public List<AppUser> users;

    public FindUser(List<AppUser> users) {
        this.users = users;
    }

    public FindUser() {}
}
