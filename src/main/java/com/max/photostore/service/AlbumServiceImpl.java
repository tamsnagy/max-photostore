package com.max.photostore.service;

import com.max.photostore.domain.Album;
import com.max.photostore.domain.AppUser;
import com.max.photostore.exception.PhotostoreException;
import com.max.photostore.exception.ResourceMissingException;
import com.max.photostore.repository.AlbumRepository;
import com.max.photostore.repository.UserRepository;
import com.max.photostore.request.CreateAlbum;
import com.max.photostore.response.GetAlbum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

@Service
public class AlbumServiceImpl implements AlbumService {
    private final AlbumRepository albumRepository;
    private final UserRepository userRepository;

    @Autowired
    public AlbumServiceImpl(AlbumRepository albumRepository, UserRepository userRepository) {
        this.albumRepository = albumRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void createAlbum(Long groupId, CreateAlbum request, String owner) throws PhotostoreException{
        final AppUser user = userRepository.findOneByUsername(owner);
        final Album album = new Album(request.name, new Date(), user, Collections.emptyList(), Collections.emptyList());
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

    @Override
    public GetAlbum getAlbum(Long albumId) throws PhotostoreException {
        Album album = albumRepository.findOne(albumId);
        if (album == null) {
            throw new ResourceMissingException("Parent album does not exist");
        }
        return new GetAlbum(album);
    }
}
