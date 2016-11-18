package com.max.photostore.response;

public class GroupMetaData {
    private long id;
    private String name;
    private long ownerId;

    public GroupMetaData(long id, String name, long ownerId) {
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
    }

    public GroupMetaData() {}

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getOwnerId() {
        return ownerId;
    }
}
