package com.may.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MayBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(MayBackendApplication.class, args);
    }
}
