package com.max.photostore.service;

import com.max.photostore.domain.AppGroup;
import com.max.photostore.domain.AppUser;
import com.max.photostore.exception.GroupException;
import com.max.photostore.repository.GroupRepository;
import com.max.photostore.repository.UserRepository;
import com.max.photostore.response.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GroupServiceImpl implements GroupService {
    private static final int MIN_GROUPNAME_LENGTH = 4;
    private static final int MAX_GROUPNAME_LENGTH = 20;

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    @Autowired
    public GroupServiceImpl(UserRepository userRepository, GroupRepository groupRepository) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }

    @Override
    public void createGroup(String name, Principal principal) throws GroupException {
        if (!validateGroupName(name))
            throw new GroupException("Invalid group name");

        if (groupRepository.findOneByName(name) != null)
            throw new GroupException("Group name already in use");

        AppUser owner = userRepository.findOneByUsername(principal.getName());

        AppGroup group = new AppGroup(name, owner);
        if (group.getMembers() == null)
            group.setMembers(new ArrayList<>());
        group.getMembers().add(owner);
        groupRepository.save(group);
    }

    @Override
    public GetGroup getGroup(long groupId, Principal principal) throws GroupException {
        AppGroup group = groupRepository.findOne(groupId);
        if (group == null)
            throw new GroupException("Not existing group");
        AppUser user = userRepository.findOneByUsername(principal.getName());
        if (!user.getGroups().contains(group))
            throw new GroupException("User is not member of this group");
        return new GetGroup(group);
    }

    @Override
    public GetGroups getGroups(Principal principal) {
        AppUser user = userRepository.findOneByUsername(principal.getName());
        List<AppGroup> groups = user.getGroups();
        return new GetGroups(groups != null ? groups : new ArrayList<>());
    }

    @Override
    public void deleteGroup(long groupId, Principal principal) throws GroupException {
        AppGroup group = groupRepository.findOne(groupId);
        if (!group.getOwner().getUsername().equals(principal.getName()))
            throw new GroupException("User is not owner of this group");
        groupRepository.delete(group);
    }

    @Override
    public void addMember(long groupId, long userId, Principal principal) throws GroupException {
        AppGroup group = groupRepository.findOne(groupId);
        if (group == null)
            throw new GroupException("Group does not exist");
        if (!group.getOwner().getUsername().equals(principal.getName()))
            throw new GroupException("User is not owner of this group");
        AppUser newMember = userRepository.findOne(userId);
        if (newMember == null)
            throw new GroupException("User does not exist");
        if (group.getMembers() == null)
            group.setMembers(new ArrayList<>());
        if (group.getMembers().contains(newMember))
            throw new GroupException("User already member of this group");
        group.getMembers().add(newMember);
        groupRepository.save(group);
    }

    @Override
    public void removeMember(long groupId, long userId, Principal principal) throws GroupException {
        AppGroup group = groupRepository.findOne(groupId);
        if (group == null)
            throw new GroupException("Group does not exist");
        if (!group.getOwner().getUsername().equals(principal.getName()))
            throw new GroupException("User is not owner of this group");
        AppUser member = userRepository.findOne(userId);
        if (member == null)
            throw new GroupException("User does not exist");
        if (group.getMembers() == null)
            group.setMembers(new ArrayList<>());
        if (!group.getMembers().contains(member))
            throw new GroupException("User is not member of this group");
        if (group.getOwner().equals(member))
            throw new GroupException("Owner of the group can not be removed from the group");
        group.getMembers().remove(member);
        groupRepository.save(group);
    }

    private boolean validateGroupName(final String name) {
        if (name == null)
            return false;
        int length = name.length();
        return !(length < MIN_GROUPNAME_LENGTH || length > MAX_GROUPNAME_LENGTH);
    }
}
