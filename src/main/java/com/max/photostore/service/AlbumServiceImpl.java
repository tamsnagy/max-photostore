package com.max.photostore.service;

import com.max.photostore.domain.Album;
import com.max.photostore.domain.AppGroup;
import com.max.photostore.domain.AppUser;
import com.max.photostore.domain.Picture;
import com.max.photostore.exception.AccessDeniedException;
import com.max.photostore.exception.InternalServerErrorException;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
    public GetAlbum getAlbum(Long albumId, String username) throws PhotostoreException {
        Album album = albumRepository.findOne(albumId);
        if (album == null) {
            throw new ResourceMissingException("Album does not exist");
        }
        AppUser user = userRepository.findOneByUsername(username);
        if(user == null) {
            throw new ResourceMissingException("User not found with username " + user);
        }
        validateReadAccess(album, user);
        return new GetAlbum(album);
    }

    private void validateReadAccess(Album album, AppUser user) throws AccessDeniedException{
        if(user.equals(album.getOwner())) {
            return;
        }
        List<AppGroup> groupsAllowingToRead = groupRepository.findByMembersInAndAlbumsIn(
                Collections.singletonList(user),
                Collections.singletonList(album));
        if(groupsAllowingToRead.isEmpty()) {
            throw new AccessDeniedException("User does not have read access to this album");
        }
    }

    @Override
    public List<GetAlbum> listAlbums(String user) throws ResourceMissingException {
        AppUser owner = userRepository.findOneByUsername(user);
        if(owner == null) {
            throw new ResourceMissingException("User not found with username " + user);
        }
        Set<Album> albumSet = albumRepository.findByOwnerAndParentIsNull(owner);
        List<AppGroup> groupList = groupRepository.findByMembersInOrOwner(Collections.singletonList(owner), owner);
        groupList.forEach(group -> albumSet.addAll(albumRepository.findByGroupsInAndParentIsNull(Collections.singletonList(group))));
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
        albumRepository.delete(album);
    }

    @Override
    public byte[] zipAlbum(Long albumId, String username) throws PhotostoreException {
        Album album = albumRepository.findOne(albumId);
        if (album == null) {
            throw new ResourceMissingException("Album does not exist");
        }
        AppUser user = userRepository.findOneByUsername(username);
        if(user == null) {
            throw new ResourceMissingException("User not found with username " + user);
        }
        validateReadAccess(album, user);

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {

            zipAlbum(zipOutputStream, album, album.getName(), new HashSet<>());
            zipOutputStream.finish();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new InternalServerErrorException(e);
        }
    }

    @Override
    @Transactional
    public void shareAlbum(long albumId, long groupId, Principal principal) throws PhotostoreException {
        Album album = albumRepository.findOne(albumId);
        if (album == null)
            throw new PhotostoreException("Album not found");
        if (!album.getOwner().getUsername().equals(principal.getName()))
            throw new PhotostoreException("Only the album's owner can share it");
        AppGroup group = groupRepository.findOne(groupId);
        if (group == null)
            throw new PhotostoreException("Group not found");
        if (group.getAlbums() == null)
            group.setAlbums(new ArrayList<>());
        if (group.getAlbums().contains(album))
            throw new PhotostoreException("This album is already shared with the group");
        group.getAlbums().addAll(getAllChilds(album));
        groupRepository.save(group);
    }

    private List<Album> getAllChilds(Album album){
        final List<Album> albums = new ArrayList<>();
        album.getAlbumList().forEach(childAlbum -> albums.addAll(getAllChilds(childAlbum)));
        albums.add(album);
        return albums;
    }

    private void zipPicture(ZipOutputStream zipOutputStream, final Picture picture, final String pathPrefix, final HashSet<String> usedEntryNames) throws IOException {
        String entryName = makeEntryNameUnique(pathPrefix + "/" + picture.getName(), usedEntryNames);
        zipOutputStream.putNextEntry(new ZipEntry(entryName));
        zipOutputStream.write(picture.getOriginalContent());
        zipOutputStream.closeEntry();
    }

    private void zipAlbum(ZipOutputStream zipOutputStream, final Album album, final String pathPrefix, final HashSet<String> usedEntryNames) throws IOException {
        for(Picture picture: album.getPictureList()) {
            zipPicture(zipOutputStream, picture, pathPrefix, usedEntryNames);
        }
        for(Album childAlbum: album.getAlbumList()) {
            zipAlbum(zipOutputStream, childAlbum, pathPrefix + "/" + childAlbum.getName(), usedEntryNames);
        }
    }

    private String makeEntryNameUnique(final String name, final HashSet<String> usedEntryNames) {
        if( ! usedEntryNames.contains(name)) {
            usedEntryNames.add(name);
            return name;
        }
        final String extension = name.substring(name.lastIndexOf('.'));
        final String nameWithoutExtension = name.substring(0, name.lastIndexOf('.'));
        String uniqueName;
        int counter = 1;
        do {
            uniqueName = nameWithoutExtension + "(" + counter + ")" + extension;
        } while(usedEntryNames.contains(uniqueName));
        usedEntryNames.add(uniqueName);
        return uniqueName;
    }
}
