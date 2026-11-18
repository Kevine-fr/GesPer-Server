package com.gesper.server.supervision;

import com.gesper.server.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Ce que le VPS attend de l'API : la route lue par Prometheus
 * (/api/v1/actuator/prometheus en production, sans le préfixe ici) et la santé
 * lue par la sonde, accessibles sans jeton ; tout le reste fermé.
 */
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class SupervisionEtSecuriteTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JwtService jwtService;

    @Test
    void sante_publique() throws Exception {
        mvc.perform(get("/actuator/health")).andExpect(status().isOk());
    }

    @Test
    void metriques_prometheus_publiques_avec_les_requetes_http() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk());
        mvc.perform(get("/actuator/prometheus"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("http_server_requests_seconds")))
                .andExpect(content().string(containsString("jvm_memory_used_bytes")))
                .andExpect(content().string(containsString("hikaricp_connections")));
    }

    @Test
    void les_autres_routes_actuator_restent_fermees() throws Exception {
        mvc.perform(get("/actuator/env")).andExpect(status().is4xxClientError());
    }

    @Test
    void route_protegee_sans_jeton_401() throws Exception {
        mvc.perform(get("/users/me")).andExpect(status().isUnauthorized());
    }

    @Test
    void jeton_falsifie_refuse() throws Exception {
        mvc.perform(get("/users/me").header("Authorization", "Bearer pas.un.jeton"))
                .andExpect(status().isUnauthorized());
        assertThat(jwtService.isValid("pas.un.jeton")).isFalse();
    }

    @Test
    void connexion_mauvais_identifiants_401() throws Exception {
        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"inconnu@exemple.fr\",\"password\":\"MotDePasse123!\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void connexion_corps_invalide_400() throws Exception {
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }
}
