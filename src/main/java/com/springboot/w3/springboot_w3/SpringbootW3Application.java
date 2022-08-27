package com.springboot.w3.springboot_w3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SpringbootW3Application {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootW3Application.class, args);
    }

}
