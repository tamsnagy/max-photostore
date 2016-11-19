package com.max.photostore.response;

import com.max.photostore.domain.AppGroup;

import java.util.List;

public class GetGroups {
    private List<AppGroup> data;

    public GetGroups(List<AppGroup> data) {
        this.data = data;
    }

    public GetGroups() {}

    public List<AppGroup> getData() {
        return data;
    }
}
