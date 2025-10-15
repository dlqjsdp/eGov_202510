package com.lime.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	    "classpath:egovframework/spring/context-datasource.xml",
	    "classpath:egovframework/spring/context-mapper.xml",
	    "classpath:egovframework/spring/context-transaction.xml",
	    "classpath:egovframework/spring/context-common.xml"
	})
@WebAppConfiguration("src/main/webapp")
@Transactional
public class UserServiceTest {

	// 필드 주입
	@Resource(name = "userService")
    private UserService userService;
	
	
	@Test
	public void duplicateId_returnsFalse(){
		// Given
		String userId = "admin1";
		
		// When
		boolean available = userService.existsUserId(userId);
		
		// Then
		assertThat(available).isFalse();
	}
	

}
