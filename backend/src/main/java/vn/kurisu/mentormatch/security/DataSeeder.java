package vn.kurisu.mentormatch.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import vn.kurisu.mentormatch.entity.Role;
import vn.kurisu.mentormatch.entity.User;
import vn.kurisu.mentormatch.repository.RoleRepository;
import vn.kurisu.mentormatch.repository.UserRepository;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        createRoleIfNotFound("ROLE_ADMIN");
        createRoleIfNotFound("ROLE_MENTOR");
        createRoleIfNotFound("ROLE_MENTEE");

        if (!userRepository.existsByUserName("admin")) {
            log.info("Default admin user not found, initializing...");
            
            Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseThrow();
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);

            User adminUser = User.builder()
                    .userName("admin")
                    .email("admin@mentormatch.vn")
                    .password(passwordEncoder.encode("admin"))
                    .fullName("System Administrator")
                    .isActive(true)
                    .roles(roles)
                    .build();

            userRepository.save(adminUser);
            log.info("Default admin user initialized successfully.");
        }
    }

    private void createRoleIfNotFound(String roleName) {
        if (roleRepository.findByName(roleName).isEmpty()) {
            roleRepository.save(Role.builder().name(roleName).build());
            log.info("Created missing role: {}", roleName);
        }
    }
}
