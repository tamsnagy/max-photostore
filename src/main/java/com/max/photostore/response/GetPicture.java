package com.max.photostore.response;

import com.max.photostore.domain.AppUser;
import com.max.photostore.domain.Picture;

import java.util.Date;

public class GetPicture {
    public Long id;
    public String name;
    public String note;
    public String location;
    public Date timestamp;
    public byte[] content;
    public AppUser owner;
    public Long albumId;

    public GetPicture(Picture picture) {
        this.id = picture.getId();
        this.name = picture.getName();
        this.note = picture.getNote();
        this.location = picture.getLocation();
        this.timestamp = picture.getTimestamp();
        this.content = picture.getContent();
        this.owner = picture.getOwner();
        this.albumId = picture.getAlbum().getId();
    }

    public GetPicture() {
    }
}
