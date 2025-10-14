package com.lime.user.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lime.user.service.UserService;
import com.lime.user.vo.UserVO;

/**
 * @Class Name  : UserServiceImpl.java
 * @Description : 회원 관련 비즈니스 로직을 처리하기 위한 서비스 구현 클래스
 * @Modification Information
 * 
 *     수정일                        수정자                     수정내용
 *     ----------    ---------    -------------------------------
 *     2025.10.14     노유경                   최초 생성 (회원가입 및 ID 중복검사 기능 구현)
 *     
 */

@Service("userService")
public class UserServiceImpl implements UserService{
	
	@Resource(name="userDAO")
	private UserDAO userDAO;

	// 아이디 중복검사 (유효성 체크)
	@Override
	public boolean isAvailableUserId(String userId) {
		// DAO에서 중복된 아이디 개수를 조회해서 0이면 사용가능
		return userDAO.countByUserId(userId) == 0;
	}

	// 회원가입 (등록)
	@Transactional
	@Override
	public void register(UserVO vo) {
		userDAO.insertUser(vo);
		
	}
	

}
