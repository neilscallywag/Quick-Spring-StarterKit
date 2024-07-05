package com.starterkit.demo.repository;

import com.starterkit.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

@RepositoryRestResource
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.name LIKE %:name% AND u.email LIKE %:email%")
    Page<User> findByNameContainingAndEmailContaining(@Param("name") String name, @Param("email") String email, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.name LIKE %:name%")
    Page<User> findByNameContaining(@Param("name") String name, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.email LIKE %:email%")
    Page<User> findByEmailContaining(@Param("email") String email, Pageable pageable);
}
