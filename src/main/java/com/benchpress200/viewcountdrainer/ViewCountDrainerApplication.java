package com.benchpress200.viewcountdrainer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ViewCountDrainerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ViewCountDrainerApplication.class, args);
    }

}
