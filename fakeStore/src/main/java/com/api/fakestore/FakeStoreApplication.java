package com.api.fakestore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class FakeStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(FakeStoreApplication.class, args);
    }

    @GetMapping("/")
    public String greeting() {
        return "Welcome to fakeStore API!";
    }


}
