package com.frontstep.deal_analyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class DealAnalyzerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DealAnalyzerApplication.class, args);
	}

}
