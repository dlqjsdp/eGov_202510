package com.lime.login.service.impl;

import javax.annotation.Resource;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.lime.user.vo.UserVO;

/**
 * @Class Name  : LoginDAO.java
 * @Description : 로그인 관련 데이터베이스 접근 클래스
 * @Modification Information
 * 
 *    수정일                       수정자                    수정내용
 *    ----------    ---------    -------------------------------
 *    2025.10.17    노유경                     사용자 아이디 기반 회원 정보 조회 메서드(findByUserId) 추가
 * 
 */

@Repository("loginDAO")
public class LoginDAO {
	
	@Resource(name="sqlSessionTemplate")
	private SqlSessionTemplate sqlSession;
	
	// 사용자 아이디로 회원 정보 조회
	public UserVO findByUserId(String userId) {
		UserVO uservo = sqlSession.selectOne("Login.findById", userId);
		
		System.out.println("[LoginDAO] findByUserId 실행 - 조회 아이디: " + userId);
		return uservo;
		
	}

}
