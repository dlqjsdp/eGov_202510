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
	
	// GPT 참고
	@Resource(name="sqlSessionTemplate") 
	private SqlSessionTemplate sqlSession;
	
	// 아이디 중복체크 (User_SQL.xml의  <select id="checkId">과 연결)
	public int checkId(String userId){
		System.out.println("DAO checkId 호출 - userid: " + userId);
		int count = sqlSession.selectOne("User.checkId", userId);
		
		System.out.println("DAO checkId() 결과 - 동일 ID 개수: " + count);
		return count;
	}
	
	// 회원가입 (User_SQL.xml의  <insert id="insertUser">과 연결)
	public void insertUser(UserVO vo) {
		System.out.println("DAO insertUser 호출: - ID" + vo.getUserId());
		sqlSession.insert("User.insertUser", vo); 
		
		System.out.println("DAO insertUser 완료");
	}
}
