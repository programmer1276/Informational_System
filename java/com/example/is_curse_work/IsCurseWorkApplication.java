package com.example.is_curse_work;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class IsCurseWorkApplication {
    public static void main(String[] args) {
        SpringApplication.run(IsCurseWorkApplication.class, args);
    }
}
