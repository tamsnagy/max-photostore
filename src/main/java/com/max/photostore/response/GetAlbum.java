package com.max.photostore.response;

import com.max.photostore.domain.Album;
import com.max.photostore.domain.AppUser;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


public class GetAlbum {
    public Long id;
    public String name;
    public Date timestamp;
    public AppUser owner;
    public Long parent;
    public List<GetPicture> pictureList;
    public List<GetAlbum> albumList;

    public GetAlbum(Album album) {
        this.id = album.getId();
        this.name = album.getName();
        this.timestamp = album.getTimestamp();
        this.parent = album.getParent() == null ? null : album.getParent().getId();
        this.owner = album.getOwner();
        this.pictureList = album.getPictureList().stream().map(GetPicture::new).collect(Collectors.toList());
        this.albumList = album.getAlbumList().stream().map(GetAlbum::cutAlbumLine).collect(Collectors.toList());
    }

    private static GetAlbum cutAlbumLine(Album album) {
        GetAlbum shorted = new GetAlbum();
        shorted.id = album.getId();
        shorted.name = album.getName();
        shorted.timestamp = album.getTimestamp();
        shorted.parent = album.getParent() == null ? null : album.getParent().getId();
        shorted.owner = album.getOwner();
        shorted.pictureList = Collections.emptyList();
        shorted.albumList = Collections.emptyList();
        return shorted;
    }

    public GetAlbum() {
    }
}
