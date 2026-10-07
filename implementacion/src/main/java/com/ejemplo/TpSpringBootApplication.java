package com.ejemplo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.ejemplo.entities")
@EnableJpaRepositories(basePackages = "com.ejemplo.repository")
public class TpSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(TpSpringBootApplication.class, args);
    }
}
