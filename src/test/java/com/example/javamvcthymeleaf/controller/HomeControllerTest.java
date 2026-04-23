package com.example.javamvcthymeleaf.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HomeControllerTest {

    private final HomeController controller = new HomeController();

    @Test
    void deveRetornarViewHome() {
        String view = controller.home();

        assertEquals("home", view);
    }
}
