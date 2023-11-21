package com.konkuk.daila;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class GpApplication {

    public static void main(String[] args) {
        SpringApplication.run(GpApplication.class, args);
    }

}
