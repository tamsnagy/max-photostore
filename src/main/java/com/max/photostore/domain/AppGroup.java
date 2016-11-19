package com.max.photostore.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class AppGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne
    @JsonManagedReference
    private AppUser owner;

    @ManyToMany(fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<AppUser> members;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Album> albums;

    public AppGroup(String name, AppUser owner) {
        this.name = name;
        this.owner = owner;
    }

    public AppGroup() {}


    public void addMember(AppUser user) {
        if(! owner.equals(user)) {
            if(this.members == null) {
                this.members = new ArrayList<>();
            }
            this.members.add(user);
        }
    }

    public void addAlbum(Album album) {
        if(this.albums == null) {
            this.albums = new ArrayList<>();
        }
        this.albums.add(album);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AppUser getOwner() {
        return owner;
    }

    public void setOwner(AppUser owner) {
        this.owner = owner;
    }

    public List<AppUser> getMembers() {
        return members;
    }

    public void setMembers(List<AppUser> members) {
        this.members = members;
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppGroup group = (AppGroup) o;

        return id == group.id;

    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
