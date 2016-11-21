package com.max.photostore.controller;

import com.max.photostore.domain.Album;
import com.max.photostore.exception.PhotostoreException;
import com.max.photostore.exception.ResourceMissingException;
import com.max.photostore.request.CreateAlbum;
import com.max.photostore.request.ShareRequest;
import com.max.photostore.response.GetAlbum;
import com.max.photostore.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;

@Controller
@RequestMapping("/api/album")
class AlbumController {
    private final AlbumService albumService;

    @Autowired
    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @RequestMapping(method = RequestMethod.GET)
    ResponseEntity<?> listAlbums(Principal principal) {
        try {
            return ResponseEntity.ok(albumService.listAlbums(principal.getName()));
        } catch (ResourceMissingException e) {
            return e.buildResponse();
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/ownedroots")
    ResponseEntity<?> listOwnedAlbums(Principal principal) {
        try {
            return ResponseEntity.ok(albumService.listOwnedParentlessAlbums(principal.getName()));
        } catch (ResourceMissingException e) {
            return e.buildResponse();
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<?> createAlbum(@RequestBody CreateAlbum request, Principal principal) {
        //TODO can I create album here??
        try {
            albumService.createAlbum(request, principal.getName());
        } catch (PhotostoreException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{albumId}")
    ResponseEntity<?> getAlbum(@PathVariable Long albumId, Principal principal) {
        //TODO can I see that album??
        try {
            return ResponseEntity.ok(albumService.getAlbum(albumId, principal.getName()));
        } catch (PhotostoreException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{albumId}")
    ResponseEntity<?> deleteAlbum(@PathVariable Long albumId, Principal principal){
        try {
            albumService.deleteAlbum(albumId, principal.getName());
            return ResponseEntity.ok().build();
        } catch (PhotostoreException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{albumId}/download")
    void downloadAlbum(HttpServletResponse response, @PathVariable Long albumId, Principal principal) throws IOException, PhotostoreException {
        //TODO can I see that album??
        GetAlbum album = albumService.getAlbum(albumId, principal.getName());
        byte[] content = albumService.zipAlbum(albumId, principal.getName());
        response.setContentLength(content.length);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + album.name + ".zip\"");
        response.setContentType("application/zip");
        response.getOutputStream().write(content);
        response.flushBuffer();
    }

    @RequestMapping(method = RequestMethod.POST, value="/{albumId}/share")
    ResponseEntity<?> shareAlbum(@PathVariable long albumId, @RequestBody ShareRequest request, Principal principal) {
        try {
            albumService.shareAlbum(albumId, request.groupId, principal);
        } catch (PhotostoreException e) {
            return e.buildResponse();
        }
        return ResponseEntity.ok().build();
    }
}
