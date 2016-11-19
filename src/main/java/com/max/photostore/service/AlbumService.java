package com.max.photostore.service;

import com.max.photostore.exception.PhotostoreException;
import com.max.photostore.exception.ResourceMissingException;
import com.max.photostore.request.CreateAlbum;
import com.max.photostore.response.GetAlbum;

import java.util.List;

public interface AlbumService {
    void createAlbum(CreateAlbum request, String owner) throws PhotostoreException;
    GetAlbum getAlbum(Long albumId) throws PhotostoreException;
    List<GetAlbum> listAlbums(String user) throws ResourceMissingException;

}
