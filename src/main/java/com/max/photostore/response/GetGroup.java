package com.max.photostore.response;

import com.max.photostore.domain.AppGroup;

public class GetGroup {
    private AppGroup data;

    public GetGroup(AppGroup data) {
        this.data = data;
    }

    public GetGroup() {}

    public AppGroup getData() {
        return data;
    }
}
