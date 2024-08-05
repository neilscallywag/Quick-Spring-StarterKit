package com.starterkit.demo.repository;

import com.starterkit.demo.model.Role;
import com.starterkit.demo.model.EnumRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
	Optional<Role> findByName(EnumRole name);
}
