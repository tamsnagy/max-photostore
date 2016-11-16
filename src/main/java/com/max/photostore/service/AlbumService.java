package com.max.photostore.service;

import com.max.photostore.domain.AppUser;
import com.max.photostore.exception.PhotostoreException;
import com.max.photostore.request.CreateAlbum;
import com.max.photostore.response.GetAlbum;

public interface AlbumService {
    void createAlbum(Long groupId, CreateAlbum request, AppUser owner) throws PhotostoreException;
    GetAlbum getAlbum(Long albumId) throws PhotostoreException;
}
