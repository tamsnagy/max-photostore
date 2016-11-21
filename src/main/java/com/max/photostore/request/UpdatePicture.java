package com.max.photostore.request;

import java.util.Date;

public class UpdatePicture {
    public String note;
    public String location;
    public Date timestamp;

    public UpdatePicture(String note, String location, Date timestamp) {
        this.note = note;
        this.location = location;
        this.timestamp = timestamp;
    }

    public UpdatePicture() {
    }
}
