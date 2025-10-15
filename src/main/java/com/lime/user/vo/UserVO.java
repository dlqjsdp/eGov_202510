package com.lime.user.vo;

import java.time.LocalDate;

/**
 * @Class Name  : UserVO.java
 * @Description : 회원 정보를 담는 VO 클래스
 * @Modification Information
 * 
 *     수정일                        수정자                     수정내용
 *     ----------    ---------    -------------------------------
 *     2025.10.14     노유경                   필드명 오타(usrr → user) 수정
 *     2025.10.15     노유경                   REG_DT 컬럼 타입 변경에 맞춰 LocalDate로 타입 수정
 *     
 */


public class UserVO {

	private Long userSeq; // 자동 증가 값
	private String userId; // 로그인 ID
	private String pwd; // 비밀번호
	private String userName; // 사용자 이름
	private java.sql.Date regDt; // 등록일시

	public Long getUserSeq() {
		return userSeq;
	}
	public void setUserSeq(Long userSeq) {
		this.userSeq = userSeq;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public java.sql.Date getRegDt() {
		return regDt;
	}
	public void setRegDt(java.sql.Date regDt) {
		this.regDt = regDt;
	}

}
