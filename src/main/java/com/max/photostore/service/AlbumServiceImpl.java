package com.max.photostore.service;

import com.max.photostore.domain.Album;
import com.max.photostore.domain.AppGroup;
import com.max.photostore.domain.AppUser;
import com.max.photostore.domain.Picture;
import com.max.photostore.exception.AccessDeniedException;
import com.max.photostore.exception.PhotostoreException;
import com.max.photostore.exception.ResourceMissingException;
import com.max.photostore.repository.AlbumRepository;
import com.max.photostore.repository.GroupRepository;
import com.max.photostore.repository.PictureRepository;
import com.max.photostore.repository.UserRepository;
import com.max.photostore.request.CreateAlbum;
import com.max.photostore.response.GetAlbum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AlbumServiceImpl implements AlbumService {
    private final AlbumRepository albumRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final PictureRepository pictureRepository;

    @Autowired
    public AlbumServiceImpl(AlbumRepository albumRepository, UserRepository userRepository, GroupRepository groupRepository, PictureRepository pictureRepository) {
        this.albumRepository = albumRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.pictureRepository = pictureRepository;
    }

    @Override
    @Transactional
    public void createAlbum(CreateAlbum request, String owner) throws PhotostoreException{
        final AppUser user = userRepository.findOneByUsername(owner);
        if(user == null) {
            throw new ResourceMissingException("User with username: " + owner + " not found");
        }
        if(request.parentAlbum == null) {
            final Album album = new Album(request.name, new Date(), user, null, Collections.emptyList(), Collections.emptyList());
            albumRepository.save(album);
        } else {
            Album parentAlbum = albumRepository.findOne(request.parentAlbum);
            if (parentAlbum == null) {
                throw new ResourceMissingException("Parent album does not exist");
            }
            if (! parentAlbum.getOwner().equals(user)) {
                throw new AccessDeniedException("User is not owner of this album");
            }
            final Album album = new Album(request.name, new Date(), user, parentAlbum, Collections.emptyList(), Collections.emptyList());
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

    @Override
    public List<GetAlbum> listAlbums(String user) throws ResourceMissingException {
        AppUser owner = userRepository.findOneByUsername(user);
        if(owner == null) {
            throw new ResourceMissingException("User not found with username " + user);
        }
        Set<Album> albumSet = albumRepository.findByOwnerAndParentIsNull(owner);
        List<AppGroup> groupList = groupRepository.findByMembersInOrOwner(Collections.singletonList(owner), owner);
        groupList.forEach(group -> albumSet.addAll(albumRepository.findByGroupsIn(Collections.singletonList(group))));
        return albumSet.stream().map(GetAlbum::new).collect(Collectors.toList());
    }

    @Override
    public List<GetAlbum> listOwnedParentlessAlbums(String user) throws ResourceMissingException {
        AppUser owner = userRepository.findOneByUsername(user);
        if(owner == null) {
            throw new ResourceMissingException("User not found with username " + user);
        }
        return albumRepository.findByOwnerAndParentIsNull(owner).stream().map(GetAlbum::new).collect(Collectors.toList());
    }

    @Override
    public void deleteAlbum(Long albumId, String name) throws PhotostoreException {
        AppUser owner = userRepository.findOneByUsername(name);
        if(owner == null) {
            throw new ResourceMissingException("User not found with username " + name);
        }
        Album album = albumRepository.findOne(albumId);
        if(album == null) {
            throw new ResourceMissingException("Album not found with id " + albumId);
        }
        if(! album.getOwner().equals(owner)) {
            throw new AccessDeniedException("User " + name + " does not have right to delete album " + albumId);
        }
        Album parentAlbum = album.getParent();
        deleteAlbumsAndPhotos(album);
        if(parentAlbum != null) {
            parentAlbum.getAlbumList().remove(album);
            albumRepository.save(parentAlbum);
        }
        albumRepository.delete(album);
    }

    private void deleteAlbumsAndPhotos(Album album){
        List<Picture> pictureList = album.getPictureList();
        if(pictureList != null){
            pictureRepository.delete(pictureList);
        }
        List<Album> albumList = album.getAlbumList();
        if(albumList != null){
            albumList.forEach(this::deleteAlbumsAndPhotos);
        }
        albumRepository.delete(albumList);
    }
}
