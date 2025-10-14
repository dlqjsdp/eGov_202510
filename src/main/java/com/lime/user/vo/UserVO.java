package com.lime.user.vo;

/**
 * @Class Name  : UserVO.java
 * @Description : 회원 정보를 담는 VO 클래스
 * @Modification Information
 * 
 *     수정일                        수정자                     수정내용
 *     ----------    ---------    -------------------------------
 *     2025.10.14     노유경                   필드명 오타(usrr → user) 수정
 *     
 */


public class UserVO {

	private String userSeq   ;
	private String userId  ;
	private String pwd  ;
	private String userName  ;
	private String regDt  ;

	public String getUserSeq() {
		return userSeq;
	}
	public void setUserSeq(String userSeq) {
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
	public String getRegDt() {
		return regDt;
	}
	public void setRegDt(String regDt) {
		this.regDt = regDt;
	}






}
