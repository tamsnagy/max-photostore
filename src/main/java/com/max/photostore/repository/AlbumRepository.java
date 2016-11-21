package com.max.photostore.repository;

import com.max.photostore.domain.Album;
import com.max.photostore.domain.AppGroup;
import com.max.photostore.domain.AppUser;
import com.max.photostore.domain.Picture;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface AlbumRepository extends CrudRepository<Album, Long> {
    Set<Album> findByOwnerAndParentIsNull(AppUser owner);
    Set<Album> findByGroupsInAndParentIsNull(List<AppGroup> groupList);
    Album findOneByPictureListIn(List<Picture> pictureList);
}
