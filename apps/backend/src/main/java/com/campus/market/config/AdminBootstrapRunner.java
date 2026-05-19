package com.campus.market.config;

import com.campus.market.user.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties(AdminBootstrapProperties.class)
public class AdminBootstrapRunner implements CommandLineRunner {

    private final AdminBootstrapProperties properties;
    private final UserService userService;

    public AdminBootstrapRunner(AdminBootstrapProperties properties, UserService userService) {
        this.properties = properties;
        this.userService = userService;
    }

    @Override
    public void run(String... args) {
        userService.ensureAdminUser(properties.username(), properties.password(), properties.nickname());
    }
}
