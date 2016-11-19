package com.max.photostore.repository;

import com.max.photostore.domain.Album;
import com.max.photostore.domain.AppUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumRepository extends CrudRepository<Album, Long> {
    List<Album> findByOwner(AppUser owner);
}
