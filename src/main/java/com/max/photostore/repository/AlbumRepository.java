package com.max.photostore.repository;

import com.max.photostore.domain.Album;
import com.max.photostore.domain.AppGroup;
import com.max.photostore.domain.AppUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface AlbumRepository extends CrudRepository<Album, Long> {
    Set<Album> findByOwnerAndParentIsNull(AppUser owner);
    Set<Album> findByGroupsIn(List<AppGroup> groupList);
}
