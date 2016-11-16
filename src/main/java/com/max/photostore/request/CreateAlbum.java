package com.max.photostore.request;


public class CreateAlbum {
    public String name;
    public Long parentAlbum;

    public CreateAlbum(String name, Long parentAlbum) {
        this.name = name;
        this.parentAlbum = parentAlbum;
    }

    public CreateAlbum(String name) {
        this.name = name;
    }

    public CreateAlbum() {
    }
}
