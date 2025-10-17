package com.lime.login.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lime.login.service.LoginService;
import com.lime.user.vo.UserVO;

/**
 * @Class Name  : LoginServiceImpl.java
 * @Description : 로그인 서비스 구현 클래스 (사용자 인증 처리 및 DAO 연동)
 * @Modification Information
 * 
 *     수정일                     수정자                    수정내용
 *    ----------    ---------    -------------------------------
 *    2025.10.17     노유경                   로그인 로직 구현 (아이디/비밀번호 검증 및 DB 조회)
 *    
 */

@Service("loginService")
public class LoginServiceImpl implements LoginService{
	
	@Resource(name="loginDAO")
	private LoginDAO loginDAO;
	

	@Override
	public UserVO login(UserVO inputVO) {
		UserVO dbUser = loginDAO.findByUserId(inputVO.getUserId());
		
		// 아이디 비교
		if(dbUser == null) {
			System.out.println("[LoginServiceImpl] 아이디 없음");
			return null;
		}
		
		// 비밀번호 비교
		if(!dbUser.getPwd().equals(inputVO.getPwd())){
			System.out.println("[LoginServiceImpl] 비밀번호 불일치");
			return null;
		}
		
		System.out.println("[LoginServiceImpl] 로그인 성공: " + dbUser.getUserId());

		return dbUser; // 로그인 성공 시 사용자 정보 반환
	}

}
