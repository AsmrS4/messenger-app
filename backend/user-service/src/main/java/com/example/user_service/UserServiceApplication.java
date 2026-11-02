package com.example.user_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class UserServiceApplication {


	public static void main(String[] args) {
        System.setProperty("log4j2.loggerContextFactory", "org.apache.logging.log4j.simple.SimpleLoggerContextFactory");
        System.setProperty("log4j.ignoreTCL", "true");
        System.setProperty("log4j2.simplelogLevel", "INFO");
        System.setProperty("log4j2.simplelogShowdatetime", "true");
        System.setProperty("log4j2.simplelogShowlogname", "true");
        System.setProperty("log4j2.simplelogLogFile", "System.out");

        SpringApplication.run(UserServiceApplication.class, args);
	}

}
