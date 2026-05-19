package com.campus.market.config;

import com.campus.market.user.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * AdminBootstrapRunner 业务组件。
 *
 * @author 阿德
 * @date 2026/05/16
 */
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
