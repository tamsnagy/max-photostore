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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class PictureServiceImpl implements PictureService {
    private final PictureRepository pictureRepository;
    private final AlbumRepository albumRepository;
    private final UserRepository userRepository;

    @Value("${photos.small.width}")
    private int width;

    @Value("${photos.small.height}")
    private int height;


    public PictureServiceImpl(PictureRepository pictureRepository, AlbumRepository albumRepository, UserRepository userRepository) {
        this.pictureRepository = pictureRepository;
        this.albumRepository = albumRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Long uploadPicture(byte[] bytes, String originalFilename, String owner, Long albumId) throws PhotostoreException{
        AppUser user = userRepository.findOneByUsername(owner);
        if(user == null){
            throw new ResourceMissingException("User not found");
        }
        Album parentAlbum = albumRepository.findOne(albumId);
        Picture picture = new Picture(originalFilename, scale(bytes), bytes, user, parentAlbum);
        picture = pictureRepository.save(picture);
        parentAlbum.addPicture(picture);
        return picture.getId();
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

    private byte[] scale(final byte[] fileData) throws PhotostoreException {
        ByteArrayInputStream in = new ByteArrayInputStream(fileData);
        try {
            BufferedImage img = ImageIO.read(in);
            final float ratio = img.getHeight() * 1.0f / img.getWidth();
            int small_width = width;
            int small_height = height;
            if(ratio > 1.0f) {
                small_width = (int) (small_width / ratio);
            } else {
                small_height = (int) (small_height * ratio);
            }
            Image scaledImage = img.getScaledInstance(small_width, small_height, Image.SCALE_SMOOTH);
            BufferedImage imageBuff = new BufferedImage(small_width, small_height, BufferedImage.TYPE_INT_RGB);
            imageBuff.getGraphics().drawImage(scaledImage, 0, 0, new Color(0,0,0), null);

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            ImageIO.write(imageBuff, "jpg", buffer);

            return buffer.toByteArray();
        } catch (IOException e) {
            throw new PhotostoreException("IOException in scale");
        }
    }
}
