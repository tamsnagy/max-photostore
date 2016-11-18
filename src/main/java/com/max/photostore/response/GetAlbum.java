package com.max.photostore.response;

import com.max.photostore.domain.Album;
import com.max.photostore.domain.AppUser;
import com.max.photostore.domain.Picture;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class GetAlbum {
    public Long id;
    public String name;
    public Date timestamp;
    public AppUser owner;
    public List<Picture> pictureList;
    public List<Long> albumList;

    public GetAlbum(Album album) {
        this.id = album.getId();
        this.name = album.getName();
        this.timestamp = album.getTimestamp();
        this.owner = album.getOwner();
        this.pictureList = album.getPictureList();
        this.albumList = album.getAlbumList().stream().map(Album::getId).collect(Collectors.toList());
    }

    public GetAlbum() {
    }
}
