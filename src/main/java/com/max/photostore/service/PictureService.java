package com.max.photostore.service;

import com.max.photostore.domain.Picture;
import com.max.photostore.exception.ResourceMissingException;
import com.max.photostore.request.UpdatePicture;

public interface PictureService {
    Long uploadPicture(byte[] bytes, String originalFilename, String owner, Long albumId);
    void updatePicture(Long id, UpdatePicture update);
    Picture getPicture(Long pictureId) throws ResourceMissingException;
}
