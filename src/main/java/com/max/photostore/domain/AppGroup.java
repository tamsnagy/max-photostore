package com.max.photostore.domain;

import javax.persistence.*;
import java.util.Set;

@Entity
public class AppGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne
    private AppUser owner;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<AppUser> members;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Album> albums;

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

    public Set<AppUser> getMembers() {
        return members;
    }

    public void setMembers(Set<AppUser> members) {
        this.members = members;
    }

    public Set<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(Set<Album> albums) {
        this.albums = albums;
    }
}
