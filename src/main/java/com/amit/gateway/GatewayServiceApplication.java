package com.amit.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

/**
 * This is a type of api gateway service but with the difference, this doesn't discovers the other service in the
 * deployment but rather assigns them jobs over RabbitMQ
 */
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class GatewayServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }
}
