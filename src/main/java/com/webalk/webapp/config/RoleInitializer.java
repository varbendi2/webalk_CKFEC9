package com.webalk.webapp.config;

import com.webalk.webapp.entity.Role;
import com.webalk.webapp.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class RoleInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public RoleInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.count() == 0) {
            Role userRole = new Role();
            userRole.setName("ROLE_USER");

            Role adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");

            roleRepository.saveAll(Arrays.asList(userRole, adminRole));
            System.out.println("Szerepkörök inicializálva");
        } else {
            System.out.println("A szerepkörök már léteznek");
        }
    }
}
