package com.max.photostore.controller;

import com.max.photostore.exception.GroupException;
import com.max.photostore.request.MemberRequest;
import com.max.photostore.request.CreateGroupRequest;
import com.max.photostore.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping(
        headers = {"content-type=application/json"},
        value = "/api/group")
public class GroupController {
    private final GroupService groupService;

    @Autowired
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @RequestMapping(value = "/", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<?> createGroup(@RequestBody CreateGroupRequest request, Principal principal) {
        try {
            groupService.createGroup(request.name, principal);
        } catch (GroupException e) {
            return e.buildResponse();
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getGroups(Principal principal) {
        return ResponseEntity.ok(groupService.getGroups(principal));
    }

    @RequestMapping(value = "/{groupId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getGroup(@PathVariable long groupId, Principal principal) {
        try {
            return ResponseEntity.ok(groupService.getGroup(groupId, principal));
        } catch (GroupException e) {
            return e.buildResponse();
        }
    }

    @RequestMapping(value = "/{groupId}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<?> deleteGroup(@PathVariable long groupId, Principal principal) {
        try {
            groupService.deleteGroup(groupId, principal);
        } catch (GroupException e) {
            return e.buildResponse();
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/{groupId}/addmember", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> addMember(@PathVariable long groupId, @RequestBody MemberRequest request, Principal principal) {
        try {
            groupService.addMember(groupId, request.id, principal);
        } catch (GroupException e) {
            return e.buildResponse();
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/{groupId}/removemember", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> removeMember(@PathVariable long groupId, @RequestBody MemberRequest request, Principal principal) {
        try {
            groupService.removeMember(groupId, request.id, principal);
        } catch (GroupException e) {
            return e.buildResponse();
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/{groupId}/albums", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getAlbums(@PathVariable long groupId, Principal principal) {
        try {
            return ResponseEntity.ok(groupService.getAlbums(groupId, principal));
        } catch (GroupException e) {
            return e.buildResponse();
        }
    }
}
