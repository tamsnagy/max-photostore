package com.max.photostore.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "album_id", nullable = false)
    private Long id;

    private String name;

    private Date timestamp;

    @ManyToOne
    private AppUser owner;

    @ManyToOne
    @JoinColumn(name = "parent_album_id")
    @JsonBackReference
    private Album parent;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY, mappedBy = "album")
    @JsonManagedReference
    private List<Picture> pictureList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "parent")
    @JsonManagedReference
    private List<Album> albumList = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "albums")
    @JsonManagedReference
    private List<AppGroup> groups = new ArrayList<>();

    public Album(String name, Date timestamp, AppUser owner, Album parent, List<Picture> pictureList, List<Album> albumList) {
        this.name = name;
        this.timestamp = timestamp;
        this.owner = owner;
        this.pictureList = pictureList;
        this.albumList = albumList;
        this.parent = parent;
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

    public Album getParent() {
        return parent;
    }

    public List<AppGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<AppGroup> groups) {
        this.groups = groups;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Album album = (Album) o;

        return id.equals(album.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
