package com.starterkit.demo.config;

import com.starterkit.demo.model.EnumRole;
import com.starterkit.demo.model.Role;
import com.starterkit.demo.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.findByName(EnumRole.ROLE_USER).isEmpty()) {
            roleRepository.save(new Role(null, EnumRole.ROLE_USER));
        }

        if (roleRepository.findByName(EnumRole.ROLE_OFFICER).isEmpty()) {
            roleRepository.save(new Role(null, EnumRole.ROLE_OFFICER));
        }

        if (roleRepository.findByName(EnumRole.ROLE_MANAGER).isEmpty()) {
            roleRepository.save(new Role(null, EnumRole.ROLE_MANAGER));
        }
    }
}
