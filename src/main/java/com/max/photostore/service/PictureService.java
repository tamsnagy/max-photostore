package com.max.photostore.service;

import com.max.photostore.domain.Picture;
import com.max.photostore.exception.PhotostoreException;
import com.max.photostore.exception.ResourceMissingException;
import com.max.photostore.request.UpdatePicture;

public interface PictureService {
    Long uploadPicture(byte[] bytes, String originalFilename, String owner, Long albumId) throws PhotostoreException;
    void updatePicture(Long id, UpdatePicture update, String username) throws PhotostoreException;
    Picture getPicture(Long pictureId, String username) throws PhotostoreException;
    void deletePicture(Long pictureId, String username) throws PhotostoreException;
    void uploadZip(byte[] bytes, String fileName, String username, Long albumId) throws PhotostoreException;
}
