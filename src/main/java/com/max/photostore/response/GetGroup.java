package com.max.photostore.response;

public class GetGroup {
    private GroupMetaData data;

    public GetGroup(GroupMetaData data) {
        this.data = data;
    }

    public GetGroup() {}

    public GroupMetaData getData() {
        return data;
    }
}
