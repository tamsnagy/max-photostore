package com.max.photostore.controller;

import com.max.photostore.domain.Picture;
import com.max.photostore.exception.PhotostoreException;
import com.max.photostore.request.UpdatePicture;
import com.max.photostore.response.GetFullPicture;
import com.max.photostore.service.PictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
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
        try {
            final String fileName = uploadfile.getOriginalFilename();
            final String extension = Picture.extension(fileName);
            if("zip".equals(extension)) {
                pictureService.uploadZip(uploadfile.getBytes(), fileName, principal.getName(), albumId);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.ok(pictureService.uploadPicture(uploadfile.getBytes(), fileName, principal.getName(), albumId));
            }
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        } catch (PhotostoreException e) {
            return e.buildResponse();
        }
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{pictureId}")
    ResponseEntity<?> updatePicture(@RequestBody UpdatePicture updatePicture, @PathVariable Long pictureId, Principal principal) {
        try {
            pictureService.updatePicture(pictureId, updatePicture, principal.getName());
            return ResponseEntity.ok().build();
        } catch (PhotostoreException e) {
            return e.buildResponse();
        }

    }

    @RequestMapping(method = RequestMethod.GET, value = "/{pictureId}")
    ResponseEntity<?> getPicture(@PathVariable Long pictureId, Principal principal) {
        try {
            return ResponseEntity.ok(pictureService.getFullPicture(pictureId, principal.getName()));
        } catch (PhotostoreException e) {
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

    @RequestMapping(method = RequestMethod.GET, value = "/{pictureId}/download")
    void downloadPicture(HttpServletResponse response, @PathVariable Long pictureId, Principal principal) throws PhotostoreException, IOException {
        Picture picture = pictureService.getPicture(pictureId, principal.getName());
        if("png".equals(picture.getContentType())){
            response.setContentType(MediaType.IMAGE_PNG.toString());
        } else {
            response.setContentType(MediaType.IMAGE_JPEG.toString());
        }
        byte[] content = picture.getOriginalContent();
        response.setContentLength(content.length);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + picture.getName() + "\"");
        response.getOutputStream().write(content);
        response.flushBuffer();
    }
}
