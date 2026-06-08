package com.gesper.server;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class GesperApplicationTests {

    @Test
    void contextLoads() {
        // Vérifie simplement que le contexte Spring démarre correctement.
    }
}
