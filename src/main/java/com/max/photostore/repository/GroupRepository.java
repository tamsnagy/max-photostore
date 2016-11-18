package com.max.photostore.repository;

import com.max.photostore.domain.AppGroup;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends CrudRepository<AppGroup, Long> {
    AppGroup findOneByName(String name);
}
