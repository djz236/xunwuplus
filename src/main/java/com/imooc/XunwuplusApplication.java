package com.imooc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication

public class XunwuplusApplication  extends SpringBootServletInitializer {
    @Override  
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {  
        return application.sources(XunwuplusApplication.class);  
        
    }

	public static void main(String[] args) {
		System.out.println("1");
		System.out.println("2");
		SpringApplication.run(XunwuplusApplication.class, args);
	}

}
