package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "ru.practicum.main",
        "ru.practicum.ewm.client.stats"})
public class MainService {
    public static void main(String[] args) {

        SpringApplication.run(MainService.class, args);
    }
}