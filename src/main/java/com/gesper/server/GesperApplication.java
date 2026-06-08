package com.gesper.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Point d'entrée de l'API GesPer — gestion financière personnelle.
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
@EnableAsync
public class GesperApplication {

    public static void main(String[] args) {
        SpringApplication.run(GesperApplication.class, args);
    }
}
