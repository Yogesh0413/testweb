package com.yogi.testwebsite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TestwebsiteApplication {

	public static void main(String[] args) {

		SpringApplication.run(TestwebsiteApplication.class, args);
	}

}
