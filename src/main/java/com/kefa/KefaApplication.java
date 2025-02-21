package com.kefa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class KefaApplication {

    public static void main(String[] args) {
        SpringApplication.run(KefaApplication.class, args);
    }

}
