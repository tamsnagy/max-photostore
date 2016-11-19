package com.max.photostore.controller;

import com.max.photostore.exception.PhotostoreException;
import com.max.photostore.exception.ResourceMissingException;
import com.max.photostore.request.UpdatePicture;
import com.max.photostore.service.PictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@Controller
@RequestMapping("/api/picture")
class PictureController {
    private final PictureService pictureService;

    @Autowired
    public PictureController(PictureService pictureService) {
        this.pictureService = pictureService;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{albumId}")
    ResponseEntity<?> uploadPicture(@PathVariable Long albumId, @RequestParam("uploadfile") MultipartFile uploadfile, Principal principal) {
        //TODO validate upload
        try {
            return ResponseEntity.ok(pictureService.uploadPicture(uploadfile.getBytes(), uploadfile.getOriginalFilename(), principal.getName(), albumId));
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        } catch (PhotostoreException e) {
            return e.buildResponse();
        }
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{pictureId}")
    ResponseEntity<?> updatePicture(@RequestBody UpdatePicture updatePicture, @PathVariable Long pictureId, Principal principal) {
        //TODO validate update
        pictureService.updatePicture(pictureId, updatePicture);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{pictureId}")
    ResponseEntity<?> getPicture(@PathVariable Long pictureId, Principal principal) {
        try {
            return ResponseEntity.ok(pictureService.getPicture(pictureId));
        } catch (ResourceMissingException e) {
            return e.buildResponse();
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{pictureId}")
    ResponseEntity<?> deletePicture(@PathVariable Long pictureId, Principal principal) {
        try {
            pictureService.deletePicture(pictureId, principal.getName());
            return ResponseEntity.ok().build();
        } catch (PhotostoreException e) {
            return e.buildResponse();
        }
    }
}
