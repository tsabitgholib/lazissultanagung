package com.lazis.lazissultanagung;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


// @SpringBootApplication
// public class LazissultanagungApplication {

// 	public static void main(String[] args) {
// 		SpringApplication.run(LazissultanagungApplication.class, args);
// 	}

// }

@SpringBootApplication
public class LazissultanagungApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Jakarta"));

		SpringApplication.run(LazissultanagungApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(LazissultanagungApplication.class);
	}
}
