package com.lime.account.service;

import java.util.List;

import egovframework.rte.psl.dataaccess.util.EgovMap;

/**
 * @Class Name  : AccountService.java
 * @Description : 회계 정보 관리 서비스 인터페이스
 * @Modification Information
 * 
 *     수정일                         수정자                       수정내용
 *     ----------       ---------       -------------------------------
 *     2025.10.21       노유경                       회계 정보 등록 기능 정의
 *     2025.10.22    	노유경    			단건 조회/수정 기능 정의, insert 반환값을 PK로 변경
 *     2025.10.22    	노유경     			단건 조회(getAccountDetail), 수정(modifyAccount), 등록(registerAccount) 메서드명 변경
 * 
 */

public interface AccountService {
	
	// 회계 정보 등록 (등록화면 -> 수정화면 바로 전환)
	public long registerAccount (EgovMap param);
	
	// 회계 정보 단건 조회
	public EgovMap getAccountDetail(Long accountSeq);
	
	// 회계 정보 수정
	public int modifyAccount(EgovMap param);
	
}
