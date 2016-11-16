package com.max.photostore.service;

import com.max.photostore.domain.Album;
import com.max.photostore.domain.AppUser;
import com.max.photostore.repository.AlbumRepository;
import com.max.photostore.request.CreateAlbum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;

@Service
public class AlbumServiceImpl implements AlbumService {
    private final AlbumRepository albumRepository;

    @Autowired
    public AlbumServiceImpl(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }

    @Override
    public void createAlbum(Long groupId, CreateAlbum request, AppUser owner) {
        if(request.parentAlbum == null) {
            final Album album = new Album(request.name, new Date(), owner, Collections.emptyList(), Collections.emptyList());
            albumRepository.save(album);
        } else {
            //TODO query for parent, and add it there.
        }
    }
}
