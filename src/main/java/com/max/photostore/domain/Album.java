package com.max.photostore.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    private String name;

    private Date timestamp;

    @ManyToOne
    private AppUser owner;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Picture> pictureList;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Album> albumList;

    //TODO: Add groups


    public Album(String name, Date timestamp, AppUser owner, List<Picture> pictureList, List<Album> albumList) {
        this.name = name;
        this.timestamp = timestamp;
        this.owner = owner;
        this.pictureList = pictureList;
        this.albumList = albumList;
    }

    public Album() {
    }

    public void addAlbum(Album album) {
        if(albumList == null) {
            albumList = new ArrayList<>();
        }
        albumList.add(album);
    }

    public void addPicture(Picture picture) {
        if(pictureList == null) {
            pictureList = new ArrayList<>();
        }
        pictureList.add(picture);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public AppUser getOwner() {
        return owner;
    }

    public List<Picture> getPictureList() {
        return pictureList;
    }

    public List<Album> getAlbumList() {
        return albumList;
    }
}
