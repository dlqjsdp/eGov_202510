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
 *     2025.10.15     노유경                   최초 생성 (회원가입 및 ID 중복검사 기능 구현)
 *     2025.10.16     노유경                   메서드명 수정 (existsUserId -> isAvailableUserId)
 *     
 *     
 */

@Service("userService")
public class UserServiceImpl implements UserService{
	
	@Resource(name="userDAO") // DAO 객체 주입
	private UserDAO userDAO;

	// 아이디 중복검사 (유효성 체크)
	@Override
	public boolean isAvailableUserId(String userId) {
		System.out.println("아이디 중복검사 요청 -userId: " + userId);
		int count = userDAO.checkId(userId);
		
		// DAO에서 중복된 아이디 개수를 조회해서 0이면 사용가능
		System.out.println("DB 조회 결과 - 동일 아이디 개수: " + count);
		return count == 0; // true 리턴
	}

	// 회원가입 (등록)
	@Override
	@Transactional // 트랜잭션 적용 (DB 작업 도중 오류 발생 시 롤백)
	public void register(UserVO vo) {
		System.out.println("회원가입 요청 - ID: " + vo.getUserId() + ", 이름: " + vo.getUserName());
		userDAO.insertUser(vo); // DAO를 통해 회원정보를 DB에 저장
	}
	

}
