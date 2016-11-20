package com.max.photostore.repository;

import com.max.photostore.domain.AppGroup;
import com.max.photostore.domain.AppUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends CrudRepository<AppGroup, Long> {
    AppGroup findOneByName(String name);
    List<AppGroup> findByMembersInOrOwner(List<AppUser> users, AppUser owner);
}
