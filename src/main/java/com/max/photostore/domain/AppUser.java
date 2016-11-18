package com.max.photostore.domain;

import javax.persistence.*;
import java.util.Set;

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
    private byte[] password;

    @Column(nullable = false)
    private byte[] salt;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "owner")
    private Set<AppGroup> ownedGroups;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "members")
    private Set<AppGroup> groups;

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

    public Set<AppGroup> getGroups() {
        return groups;
    }

    public void setGroups(Set<AppGroup> groups) {
        this.groups = groups;
    }

    public Set<AppGroup> getOwnedGroups() {
        return ownedGroups;
    }

    public void setOwnedGroups(Set<AppGroup> ownedGroups) {
        this.ownedGroups = ownedGroups;
    }
}
