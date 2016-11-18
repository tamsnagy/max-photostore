package com.max.photostore.service;

import com.max.photostore.exception.PhotostoreException;
import com.max.photostore.request.CreateAlbum;
import com.max.photostore.response.GetAlbum;

public interface AlbumService {
    void createAlbum(Long groupId, CreateAlbum request, String owner) throws PhotostoreException;
    GetAlbum getAlbum(Long albumId) throws PhotostoreException;
}
