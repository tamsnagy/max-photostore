package com.max.photostore.response;

import java.util.Set;

public class GetGroups {
    private Set<GroupMetaData> data;

    public GetGroups(Set<GroupMetaData> data) {
        this.data = data;
    }

    public GetGroups() {}

    public Set<GroupMetaData> getData() {
        return data;
    }
}
