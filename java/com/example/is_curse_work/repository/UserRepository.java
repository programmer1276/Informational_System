package com.example.is_curse_work.repository;

import com.example.is_curse_work.model.User;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("select u from User u left join fetch u.roles where u.email = :email")
    Optional<User> findByEmailFetchRoles(@Param("email") String email);

    @Query("select distinct u from User u left join fetch u.roles order by u.userId")
    java.util.List<User> findAllWithRoles();
}
