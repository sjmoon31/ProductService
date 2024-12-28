package com.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "com.shop")
@EnableFeignClients
public class ProductProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductProjectApplication.class, args);
	}

}
