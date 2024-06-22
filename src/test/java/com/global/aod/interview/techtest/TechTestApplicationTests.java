package com.global.aod.interview.techtest;

import com.global.aod.interview.techtest.service.impl.StationServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TechTestApplicationTests {

	@Autowired
	private ApplicationContext context;

	@Test
	void contextLoads() {
		var service = context.getBean(StationServiceImpl.class);

		assertThat(service).isNotNull();
	}
}
