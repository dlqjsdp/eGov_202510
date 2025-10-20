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
 * 
 */

public interface AccountService {
	
	// 회계 정보 등록
	public void insertAccount (EgovMap param);

}
