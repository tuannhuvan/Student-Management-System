package org.example.demostudentmanagementsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DemoStudentManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoStudentManagementSystemApplication.class, args);
    }

}
