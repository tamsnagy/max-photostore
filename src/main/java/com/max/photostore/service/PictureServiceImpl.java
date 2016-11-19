package com.max.photostore.service;

import com.max.photostore.domain.Album;
import com.max.photostore.domain.AppUser;
import com.max.photostore.domain.Picture;
import com.max.photostore.exception.AccessDeniedException;
import com.max.photostore.exception.PhotostoreException;
import com.max.photostore.exception.ResourceMissingException;
import com.max.photostore.repository.AlbumRepository;
import com.max.photostore.repository.PictureRepository;
import com.max.photostore.repository.UserRepository;
import com.max.photostore.request.UpdatePicture;
import org.springframework.stereotype.Service;

@Service
public class PictureServiceImpl implements PictureService {
    private final PictureRepository pictureRepository;
    private final AlbumRepository albumRepository;
    private final UserRepository userRepository;

    public PictureServiceImpl(PictureRepository pictureRepository, AlbumRepository albumRepository, UserRepository userRepository) {
        this.pictureRepository = pictureRepository;
        this.albumRepository = albumRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Long uploadPicture(byte[] bytes, String originalFilename, String owner, Long albumId) throws PhotostoreException{
        //TODO save with two sizes: orig, small
        AppUser user = userRepository.findOneByUsername(owner);
        if(user == null){
            throw new ResourceMissingException("User not found");
        }
        Album parentAlbum = albumRepository.findOne(albumId);
        Picture picture = new Picture(originalFilename, bytes, user, parentAlbum);
        picture = pictureRepository.save(picture);
        parentAlbum.addPicture(picture);
        //albumRepository.save(parentAlbum);
        return picture.getId(); //TODO maybe return picture.
    }

    @Override
    public void updatePicture(Long id, UpdatePicture update) {
        //TODO secure
        Picture picture = pictureRepository.findOne(id);
        picture.update(update);
        pictureRepository.save(picture);
    }

    @Override
    public Picture getPicture(Long pictureId) throws ResourceMissingException {
        //TODO secure
        Picture picture = pictureRepository.findOne(pictureId);
        if(picture == null) {
            throw new ResourceMissingException("Picture with id " + pictureId + " not found");
        }
        return picture;
    }

    @Override
    public void deletePicture(Long pictureId, String username) throws PhotostoreException {
        AppUser user = userRepository.findOneByUsername(username);
        if(user == null) {
            throw new ResourceMissingException("User not found");
        }
        Picture picture = pictureRepository.findOne(pictureId);
        if(picture == null) {
            throw new ResourceMissingException("Picture with id " + pictureId + " not found");
        }
        if(! picture.getOwner().equals(user)) {
            throw new AccessDeniedException("User cannot delete picture with id " + pictureId);
        }

        Album parentAlbum = picture.getAlbum();
        parentAlbum.getPictureList().remove(picture);
        albumRepository.save(parentAlbum);
        pictureRepository.delete(pictureId);
    }
}
