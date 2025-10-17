package com.lime.login.service;

import com.lime.user.vo.UserVO;

/**
 * @Class Name  : LoginService.java
 * @Description : 로그인 서비스 인터페이스 (사용자 인증 기능 정의)
 * @Modification Information
 * 
 *     수정일                    수정자                     수정내용
 *    ----------    ---------    -------------------------------
 *    2025.10.17    노유경                    사용자 로그인 메서드(login) 정의
 */

public interface LoginService {
	
	// 사용자 로그인
	public UserVO login(UserVO inputVO);

}
