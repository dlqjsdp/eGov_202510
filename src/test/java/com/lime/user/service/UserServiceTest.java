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

import com.lime.user.vo.UserVO;

/**
 * @Class Name  : UserServiceTest.java
 * @Description : 회원 서비스 단위 테스트 클래스
 * @Modification Information
 * 
 *     수정일                       수정자                    수정내용
 *     ----------    ---------    -------------------------------
 *     2025.10.16    노유경                     아이디 중복검사(checkUserId) 테스트 메서드 작성
 *     2025.10.16    노유경                     회원가입(register) 테스트 메서드 작성 및 시나리오별 검증 추가
 *     2025.10.16    노유경                     메서드명 수정 (existsUserId -> isAvailableUserId)
 * 
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	    "classpath:egovframework/spring/context-datasource.xml",
	    "classpath:egovframework/spring/context-mapper.xml",
	    "classpath:egovframework/spring/context-transaction.xml",
	    "classpath:egovframework/spring/context-common.xml"
	})
@Transactional
public class UserServiceTest {

	// 필드 주입
	@Resource(name = "userService")
    private UserService userService;
	
	
	/*
	 * 아이디 중복 검사 테스트
	 * 1. DB에 존재하는 admin1 를 사용해 false값 기대
	 */
	@Test
	public void checkUserId(){
		// Given: 테스트용 기존 아이디 사용
		String userId = "admin1";
		
		// When: 서비스 메서드 호출
		boolean available = userService.isAvailableUserId(userId);
		
		// Then: 결과 검증 (false값 기대)
		assertThat(available).isFalse();
	}
	
	/*
	 * 회원가입 성공 테스트
	 * 1. 존재하지 않는 아이디는 사용 가능하기 때문에 True값 기대
	 * 2. 등록 후에는 중복이여서 false값 기대
	 * 3. insert 되었을 떄 UserSeq 값은 자동 증가 이기 때문에 값이 채워졌는지 확인
	 * 
	 */
	@Test
	public void register(){
		// Given: 테스트용 더미 데이터 입력
		String userid = "test1234";
		
		UserVO vo = new UserVO();
		vo.setUserId(userid);
		vo.setPwd("Test1234!");
		vo.setUserName("테스트");
		
		// 1. 존재하지 않는 아이디는 사용 가능하기 때문에 True값 기대
		assertThat(userService.isAvailableUserId(userid)).isTrue();
		
		// When: 회원 등록 수행
		userService.register(vo);
		
		// Then: 결과 검증
		// 2. 등록 후에는 중복이여서 false값 기대
		assertThat(userService.isAvailableUserId(userid)).isFalse();
		// 3. DB에 insert시 UserSeq값이 자동으로 증가되었는지 검증
		assertThat(vo.getUserSeq()).isNotNull(); 
	}

}
