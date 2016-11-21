package com.max.photostore.repository;

import com.max.photostore.domain.AppUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<AppUser, Long> {
    AppUser findOneByUsername(String username);
    AppUser findOneByEmail(String email);
    List<AppUser> findByUsernameContainingOrEmailContaining(String username, String email);
}
