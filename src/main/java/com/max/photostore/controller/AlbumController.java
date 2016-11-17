package com.max.photostore.controller;

import com.max.photostore.domain.Album;
import com.max.photostore.exception.PhotostoreException;
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
@RequestMapping("/api/album")
class AlbumController {
    private final AlbumService albumService;

    @Autowired
    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @RequestMapping(method = RequestMethod.GET)
    Collection<Album> listAlbums(Principal principal) {
        // get groups, where a user belongs to, and query all albums for it.
        return null;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{groupId}")
    ResponseEntity<?> createAlbum(@PathVariable Long groupId, @RequestBody CreateAlbum request, Principal principal) {
        //TODO can I create album here??
        try {
            albumService.createAlbum(groupId, request, principal.getName());
        } catch (PhotostoreException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{albumId}")
    ResponseEntity<?> getAlbum(@PathVariable Long albumId, Principal principal) {
        //TODO can I see that album??
        try {
            return ResponseEntity.ok(albumService.getAlbum(albumId));
        } catch (PhotostoreException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
