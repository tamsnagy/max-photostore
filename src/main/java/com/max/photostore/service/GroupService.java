package com.max.photostore.service;

import com.max.photostore.exception.GroupException;
import com.max.photostore.response.GetGroup;
import com.max.photostore.response.GetGroups;

import java.security.Principal;

public interface GroupService {
    void createGroup(String name, Principal principal) throws GroupException;
    GetGroup getGroup(long groupId, Principal principal) throws GroupException;
    GetGroups getGroups(Principal principal);
    void deleteGroup(long groupId, Principal principal) throws GroupException;
    void addMember(long groupId, long userId, Principal principal) throws GroupException;
    void removeMember(long groupId, long userId, Principal principal) throws GroupException;
}
