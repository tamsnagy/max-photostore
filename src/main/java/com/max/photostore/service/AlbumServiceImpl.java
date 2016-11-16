package com.max.photostore.service;

import com.max.photostore.domain.Album;
import com.max.photostore.domain.AppUser;
import com.max.photostore.exception.PhotostoreException;
import com.max.photostore.exception.ResourceMissingException;
import com.max.photostore.repository.AlbumRepository;
import com.max.photostore.request.CreateAlbum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
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
    public void createAlbum(Long groupId, CreateAlbum request, AppUser owner) throws PhotostoreException{
        final Album album = new Album(request.name, new Date(), owner, Collections.emptyList(), Collections.emptyList());
        if(request.parentAlbum == null) {
            albumRepository.save(album);
        } else {
            Album parentAlbum = albumRepository.findOne(request.parentAlbum);
            if (parentAlbum == null) {
                throw new ResourceMissingException("Parent album does not exist");
            }
            parentAlbum.addAlbum(album);
            albumRepository.save(Arrays.asList(album, parentAlbum));
        }
    }
}
