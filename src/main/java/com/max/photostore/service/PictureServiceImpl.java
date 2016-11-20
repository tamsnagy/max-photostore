package com.max.photostore.service;

import com.max.photostore.domain.Album;
import com.max.photostore.domain.AppUser;
import com.max.photostore.domain.Picture;
import com.max.photostore.exception.AccessDeniedException;
import com.max.photostore.exception.InternalServerErrorException;
import com.max.photostore.exception.PhotostoreException;
import com.max.photostore.exception.ResourceMissingException;
import com.max.photostore.repository.AlbumRepository;
import com.max.photostore.repository.PictureRepository;
import com.max.photostore.repository.UserRepository;
import com.max.photostore.request.UpdatePicture;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class PictureServiceImpl implements PictureService {
    private final PictureRepository pictureRepository;
    private final AlbumRepository albumRepository;
    private final UserRepository userRepository;
    private final ScaleService scaleService;

    @Autowired
    public PictureServiceImpl(PictureRepository pictureRepository, AlbumRepository albumRepository,
                              UserRepository userRepository, ScaleService scaleService) {
        this.pictureRepository = pictureRepository;
        this.albumRepository = albumRepository;
        this.userRepository = userRepository;
        this.scaleService = scaleService;
    }

    @Override
    public Long uploadPicture(byte[] bytes, String originalFilename, String owner, Long albumId) throws PhotostoreException{
        AppUser user = userRepository.findOneByUsername(owner);
        if(user == null){
            throw new ResourceMissingException("User not found");
        }
        Album parentAlbum = albumRepository.findOne(albumId);
        if(parentAlbum == null) {
            throw new ResourceMissingException("Album not found with id " + albumId);
        }
        Picture picture = new Picture(originalFilename, scaleService.scale(bytes), bytes, user, parentAlbum);
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

    @Override
    @Transactional
    public void uploadZip(byte[] bytes, String fileName, String username, Long albumId) throws PhotostoreException {
        AppUser user = userRepository.findOneByUsername(username);
        if(user == null){
            throw new ResourceMissingException("User not found");
        }
        Album parentAlbum = albumRepository.findOne(albumId);
        if(parentAlbum == null) {
            throw new ResourceMissingException("Album not found with id " + albumId);
        }
        try(ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(bytes))){
            ZipEntry entry = null;
            while ((entry = zipStream.getNextEntry()) != null) {
                if(entry.isDirectory()) {
                    // TODO Do nothing, or upload as album ?
                } else {
                    final String entryFullName = entry.getName();
                    if(! entryFullName.startsWith("__MACOS")) {
                        final String entryName = entryFullName.contains("/") ?
                                entryFullName.substring(entryFullName.lastIndexOf('/')) :
                                entryFullName;
                        final String[] parts = entryName.split("\\.");
                        final String extension = parts[parts.length - 1];
                        if (Arrays.asList("jpeg", "jpg", "png").contains(extension)) {
                            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                                copyStream(zipStream, byteArrayOutputStream);
                                byte[] originalSizedImage = byteArrayOutputStream.toByteArray();
                                byte[] smallSizedImage = scaleService.scale(originalSizedImage);
                                Picture picture = new Picture(entryName, smallSizedImage, originalSizedImage, user, parentAlbum);
                                picture = pictureRepository.save(picture);
                                parentAlbum.addPicture(picture);
                            }
                        }
                    }
                }
                zipStream.closeEntry();
            }
        } catch (IOException e) {
            throw new InternalServerErrorException(e);
        }
    }

    private static void copyStream(InputStream zipIn, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024 * 4];
        int read = 0;
        while ((read = zipIn.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }
}
