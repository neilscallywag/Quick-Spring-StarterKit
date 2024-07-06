package com.starterkit.demo.config;

import com.starterkit.demo.model.EnumRole;
import com.starterkit.demo.model.Role;
import com.starterkit.demo.model.User;
import com.starterkit.demo.repository.RoleRepository;
import com.starterkit.demo.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;

@Component
@Slf4j
public class DataInitializer implements CommandLineRunner {


    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        createRolesIfNotExist();
        // createUsers();
    }

    private void createRolesIfNotExist() {
        try {
            if (roleRepository.findByName(EnumRole.ROLE_USER).isEmpty()) {
                roleRepository.save(new Role(EnumRole.ROLE_USER));
            }
            if (roleRepository.findByName(EnumRole.ROLE_OFFICER).isEmpty()) {
                roleRepository.save(new Role(EnumRole.ROLE_OFFICER));
            }
            if (roleRepository.findByName(EnumRole.ROLE_MANAGER).isEmpty()) {
                roleRepository.save(new Role(EnumRole.ROLE_MANAGER));
            }
        } catch (Exception e) {
            log.error("Error creating roles", e);
        }
    }

    private void createUsers() {
        Set<Role> rolesUser = new HashSet<>();
        Set<Role> rolesOfficer = new HashSet<>();
        Set<Role> rolesManager = new HashSet<>();
        try {
            rolesUser.add(roleRepository.findByName(EnumRole.ROLE_USER).orElseThrow());
            rolesOfficer.add(roleRepository.findByName(EnumRole.ROLE_OFFICER).orElseThrow());
            rolesManager.add(roleRepository.findByName(EnumRole.ROLE_MANAGER).orElseThrow());
        } catch (Exception e) {
            log.error("Error fetching roles", e);
            return;
        }

        String password = passwordEncoder.encode("password");

        IntStream.range(0, 10000000).forEach(i -> {
            try {
                User user = new User();
                String uniqueID = UUID.randomUUID().toString();
                user.setUsername("user" + uniqueID);
                user.setPassword(password);
                user.setEmail("user" + uniqueID + "@example.com");
                user.setName("User " + i);
                user.setRoles((i % 3 == 0) ? rolesManager : (i % 3 == 1) ? rolesOfficer : rolesUser);
                userRepository.save(user);

                if (i % 1000 == 0) {
                    log.info("Created " + i + " users");
                }
            } catch (Exception e) {
                log.error("Error creating user at index " + i, e);
            }
        });
    }
}
