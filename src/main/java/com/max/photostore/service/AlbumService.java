package com.max.photostore.service;

import com.max.photostore.domain.AppUser;
import com.max.photostore.request.CreateAlbum;

public interface AlbumService {
    void createAlbum(Long groupId, CreateAlbum request, AppUser owner);
}
