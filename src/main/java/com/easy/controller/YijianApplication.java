package com.easy.controller;

import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.DelegatingFilterProxy;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@SpringBootApplication
@ComponentScan(basePackages = {"com.easy"})
@MapperScan(value = "com.easy.dao")
//public class YijianApplication extends SpringBootServletInitializer {
//	@Override
//	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
//		return builder.sources(YijianApplication.class);
//	}

public class YijianApplication{
	public static void main(String[] args) {

		SpringApplication.run(YijianApplication.class, args);
	}
}
