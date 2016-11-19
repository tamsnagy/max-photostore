package com.max.photostore.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @JsonIgnore
    private byte[] password;

    @Column(nullable = false)
    @JsonIgnore
    private byte[] salt;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "owner")
    @JsonBackReference
    private List<AppGroup> ownedGroups;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "members")
    @JsonBackReference
    private List<AppGroup> groups;

    public AppUser(String username, String email, byte[] password, byte[] salt) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.salt = salt;
    }

    public AppUser() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    public List<AppGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<AppGroup> groups) {
        this.groups = groups;
    }

    public List<AppGroup> getOwnedGroups() {
        return ownedGroups;
    }

    public void setOwnedGroups(List<AppGroup> ownedGroups) {
        this.ownedGroups = ownedGroups;
    }
}
