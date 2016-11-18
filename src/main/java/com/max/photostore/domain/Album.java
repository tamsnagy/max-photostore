package com.max.photostore.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

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

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "albums")
    private Set<AppGroup> groups;

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

    public Set<AppGroup> getGroups() {
        return groups;
    }
}
