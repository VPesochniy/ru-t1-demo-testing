package ru.t1.demo;

import org.springframework.boot.SpringApplication;

public class TestTestingApplication {

	public static void main(String[] args) {
		SpringApplication.from(TestingApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
