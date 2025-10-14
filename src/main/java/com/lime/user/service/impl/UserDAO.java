package com.lime.user.service.impl;

import javax.annotation.Resource;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.lime.user.vo.UserVO;

/**
 * @Class Name  : UserDAO.java
 * @Description : 회원 관련 데이터베이스 연동을 담당하는 DAO 클래스
 * @Modification Information
 * 
 *     수정일                      수정자                     수정내용
 *     ----------    ---------    -------------------------------
 *     2025.10.14    노유경                     최초 생성 (회원가입 및 ID 중복검사 기능 구현)
 * 
 */

@Repository("userDAO")
public class UserDAO {
	
	@Resource(name="sqlSessionTemplate") 
	private SqlSessionTemplate sqlSession;
	
	// 아이디 중복체크
	public int countByUserId(String userId){
		return sqlSession.selectOne("User.countByUserId", userId);
	}
	
	// 회원가입
	public void insertUser(UserVO vo) {
		sqlSession.insert("User.insertUser", vo);
	}

}
