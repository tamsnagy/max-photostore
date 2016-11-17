package com.max.photostore.repository;

import com.max.photostore.domain.AppUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<AppUser, Long> {
    List<AppUser> findByUsername(String username);
    List<AppUser> findByEmail(String email);
}
