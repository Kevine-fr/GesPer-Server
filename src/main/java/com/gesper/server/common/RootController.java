package com.gesper.server.common;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "Root", description = "Endpoints publics")
@RestController
@RequestMapping("/")
public class RootController {

    @GetMapping
    public Map<String, Object> hello() {
        return Map.of(
                "name", "GesPer Server",
                "version", "1.0.0",
                "docs", "/api/v1/swagger-ui.html"
        );
    }
}
