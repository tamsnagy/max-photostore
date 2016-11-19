package com.max.photostore.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
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
}
