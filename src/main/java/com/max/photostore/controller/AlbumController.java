package com.max.photostore.controller;

import com.max.photostore.domain.Album;
import com.max.photostore.domain.AppUser;
import com.max.photostore.repository.UserRepository;
import com.max.photostore.request.CreateAlbum;
import com.max.photostore.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;
import java.util.Collection;

@Controller
@RequestMapping("/album")
class AlbumController {
    private final AlbumService albumService;
    private final UserRepository userRepository;

    @Autowired
    public AlbumController(AlbumService albumService, UserRepository userRepository) {
        this.albumService = albumService;
        this.userRepository = userRepository;
    }

    @RequestMapping(method = RequestMethod.GET)
    Collection<Album> listAlbums(Principal principal) {
        final String userId = principal.getName();
        System.out.println("new userid: " + userId);
        // get groups, where a user belongs to, and query all albums for it.
        return null;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{groupId}")
    ResponseEntity<?> createAlbum(@PathVariable Long groupId, @RequestBody CreateAlbum request, Principal principal) {
        final String userId = principal.getName();
        final AppUser owner = userRepository.findOne(Long.valueOf(userId));
        System.out.println("new userid: " + userId);
        albumService.createAlbum(groupId, request, owner);
        return ResponseEntity.ok().build();
    }

}
