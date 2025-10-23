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
 *     2025.10.22        노유경        	      회계 정보 단건 조회(selectAccountDetail), 수정(updateAccount) 기능 추가
 *     2025.10.23        노유경        	   updateAccount() 반환타입을 void → int 로 변경하여 영향 행 수 반환하도록 수정
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
	
	// 회계 정보 단건 조회
	public EgovMap selectAccountDetail(Long accountSeq) {
        System.out.println("[AccountDAO] selectAccountDetail() 호출됨");
        System.out.println("조회할 accountSeq: " + accountSeq);
        return selectOne("Account.selectAccountDetail", accountSeq);
    }
	
	// 회계 정보 수정
	public int updateAccount(EgovMap param) {
        System.out.println("[AccountDAO] updateAccount() 호출됨");
        System.out.println("수정할 파라미터: " + param);
        return update("Account.updateAccount", param); // 영향을 받은 행의 수 반환
    }
	
}
