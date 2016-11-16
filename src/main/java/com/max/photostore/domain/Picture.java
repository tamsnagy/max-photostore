package com.max.photostore.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class Picture {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String note;

    private String location;

    private Date timestamp;

    @ManyToOne
    private AppUser owner;

    @ManyToOne
    private Album album;

    public Picture(String name, String note, String location, Date timestamp, AppUser owner, Album album) {
        this.name = name;
        this.note = note;
        this.location = location;
        this.timestamp = timestamp;
        this.owner = owner;
        this.album = album;
    }

    public Picture() {
    }
}
