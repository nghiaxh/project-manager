package com.pm.project_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ProjectManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjectManagerApplication.class, args);
	}

}
