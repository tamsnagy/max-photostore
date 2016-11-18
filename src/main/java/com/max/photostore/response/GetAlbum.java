package com.max.photostore.response;

import com.max.photostore.domain.Album;
import com.max.photostore.domain.AppUser;
import com.max.photostore.domain.Picture;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class GetAlbum {
    private Long id;
    private String name;
    private Date timestamp;
    private AppUser owner;
    private List<Picture> pictureList;
    private List<Long> albumList;

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
