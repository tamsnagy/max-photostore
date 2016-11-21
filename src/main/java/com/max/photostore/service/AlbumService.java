package com.max.photostore.service;

import com.max.photostore.exception.PhotostoreException;
import com.max.photostore.exception.ResourceMissingException;
import com.max.photostore.request.CreateAlbum;
import com.max.photostore.response.GetAlbum;

import java.security.Principal;
import java.util.List;

public interface AlbumService {
    void createAlbum(CreateAlbum request, String owner) throws PhotostoreException;
    GetAlbum getAlbum(Long albumId, String username) throws PhotostoreException;
    List<GetAlbum> listAlbums(String user) throws ResourceMissingException;
    List<GetAlbum> listOwnedParentlessAlbums(String user) throws ResourceMissingException;

    void deleteAlbum(Long albumId, String name) throws PhotostoreException;

    byte[] zipAlbum(Long albumId, String username) throws PhotostoreException;

    void shareAlbum(long albumId, long groupId, Principal principal) throws PhotostoreException;
}
