package com.max.photostore.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.max.photostore.request.UpdatePicture;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class Picture {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "picture_id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String note;

    private String location;

    private Date timestamp;

    @Column(nullable = false)
    private byte[] content;

    @Column(nullable = false)
    private byte[] originalContent;

    @ManyToOne
    @JsonBackReference
    private AppUser owner;

    @ManyToOne
    @JoinColumn(name = "album_id", nullable = false)
    @JsonBackReference
    private Album album;

    public Picture(String name, String note, String location, Date timestamp, byte[] content, byte[] originalContent, AppUser owner, Album album) {
        this.name = name;
        this.note = note;
        this.location = location;
        this.timestamp = timestamp;
        this.content = content;
        this.originalContent = originalContent;
        this.owner = owner;
        this.album = album;
    }

    public Picture(String name, byte[] content, byte[] originalContent, AppUser owner, Album album) {
        this.name = name;
        this.content = content;
        this.originalContent = originalContent;
        this.owner = owner;
        this.album = album;
    }

    public Picture() {
    }

    public void update(UpdatePicture picture) {
        this.location = picture.location;
        this.name = picture.name;
        this.note = picture.note;
        this.timestamp = picture.timestamp;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNote() {
        return note;
    }

    public String getLocation() {
        return location;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public byte[] getContent() {
        return content;
    }

    public byte[] getOriginalContent() {
        return originalContent;
    }

    public AppUser getOwner() {
        return owner;
    }

    public Album getAlbum() {
        return album;
    }
}
