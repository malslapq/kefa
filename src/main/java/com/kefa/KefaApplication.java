package com.kefa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableRetry
@EnableAsync
@EnableJpaAuditing
@EnableCaching
@SpringBootApplication
public class KefaApplication {

    public static void main(String[] args) {
        SpringApplication.run(KefaApplication.class, args);
    }

}
