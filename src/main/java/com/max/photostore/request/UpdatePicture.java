package com.max.photostore.request;

import java.util.Date;

public class UpdatePicture {
    public String name;
    public String note;
    public String location;
    public Date timestamp;

    public UpdatePicture(String name, String note, String location, Date timestamp) {
        this.name = name;
        this.note = note;
        this.location = location;
        this.timestamp = timestamp;
    }

    public UpdatePicture() {
    }
}
