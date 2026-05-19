package com.campus.market;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * CampusMarketApplication 业务组件。
 *
 * @author 阿德
 * @date 2026/05/06
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class CampusMarketApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusMarketApplication.class, args);
    }
}
