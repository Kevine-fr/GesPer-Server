package com.gesper.server.securite;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootVersion;
import org.springframework.core.SpringVersion;

/**
 * Spring Boot 3.3.5 (Spring Framework 6.1, Tomcat 10.1.31) n'était plus
 * maintenu : les failles de Tomcat, Spring Framework et Spring Security
 * publiées depuis n'y sont pas corrigées. La ligne 3.5 l'est. Ce test refuse
 * un retour en arrière du parent Maven, qui tirerait toutes ces versions.
 */
class VersionsCorrigeesTest {

    static int[] version(String texte) {
        String[] parties = texte.split("[.-]");
        return new int[] {Integer.parseInt(parties[0]), Integer.parseInt(parties[1]), Integer.parseInt(parties[2])};
    }

    static boolean auMoins(String version, String minimum) {
        int[] v = version(version), m = version(minimum);
        for (int i = 0; i < 3; i++) {
            if (v[i] != m[i]) {
                return v[i] > m[i];
            }
        }
        return true;
    }

    @Test
    void comparaisonDesVersions() {
        assertThat(auMoins("3.3.5", "3.5.16")).isFalse();
        assertThat(auMoins("3.5.15", "3.5.16")).isFalse();
        assertThat(auMoins("3.5.16", "3.5.16")).isTrue();
        assertThat(auMoins("4.0.0", "3.5.16")).isTrue();
    }

    @Test
    void springBootEtSpringFrameworkSontCorriges() {
        assertThat(auMoins(SpringBootVersion.getVersion(), "3.5.16"))
                .as("Spring Boot %s", SpringBootVersion.getVersion()).isTrue();
        assertThat(auMoins(SpringVersion.getVersion(), "6.2.19"))
                .as("Spring Framework %s", SpringVersion.getVersion()).isTrue();
    }
}
