package com.max.photostore.response;

import com.max.photostore.domain.Picture;

public class GetFullPicture extends GetPicture {
    public byte[] originalContent;

    public GetFullPicture(Picture picture) {
        super(picture);
        this.originalContent =picture.getOriginalContent();
    }

    public GetFullPicture() {
    }
}
