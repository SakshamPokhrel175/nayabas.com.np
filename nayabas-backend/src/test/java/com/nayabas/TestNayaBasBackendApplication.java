package com.nayabas;

import org.springframework.boot.SpringApplication;

public class TestNayaBasBackendApplication {

	public static void main(String[] args) {
		SpringApplication.from(NayaBasBackendApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
