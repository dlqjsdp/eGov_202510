package com.lime.account.service.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import egovframework.rte.fdl.cmmn.exception.EgovBizException;
import egovframework.rte.psl.dataaccess.EgovAbstractMapper;
import egovframework.rte.psl.dataaccess.util.EgovMap;

/**
 * @Class Name  : AccountDAO.java
 * @Description : 회계(Account) 관련 데이터베이스 연동 DAO 클래스
 * @Modification Information
 * 
 *     수정일                       수정자                  수정내용
 *     ----------    --------    --------------------------------------------
 *     2025.10.20    노유경                   ACCOUNT_TB 데이터 등록(insertAccount) 기능 추가
 * 
 * @since 2025.10.20
 * @version 1.1
 */

@Repository("accountDAO")
public class AccountDAO extends EgovAbstractMapper{

	
	// 회계 정보 등록 (ACCOUNT_TB)
	public void insertAccount(EgovMap param){
		System.out.println("[AccountDAO] insertAccount() 호출됨");
		System.out.println("전달된 파라미터: " + param);
		insert("Account.insertAccount", param);
	}

}
