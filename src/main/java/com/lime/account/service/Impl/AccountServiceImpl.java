package com.lime.account.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lime.account.service.AccountService;
import com.lime.util.CommUtils;

import egovframework.rte.psl.dataaccess.util.EgovMap;

/**
 * @Class Name  : AccountServiceImpl.java
 * @Description : 회계 정보 관리 서비스 구현 클래스
 * @Modification Information
 * 
 *     수정일                         수정자                       수정내용
 *     ----------       ---------       -------------------------------------------
 *     2025.10.21       노유경                       회계 정보 등록 기능 구현
 *     2025.10.22    	노유경       		자동 생성된 accountSeq 추출 로직 추가 및 형변환 처리(Number → long)
 *     2025.10.22    	노유경       		단건 조회(getAccountDetail), 수정(modifyAccount) 메서드 구조 추가
 * 
 */

@Service("accountService")
public class AccountServiceImpl implements AccountService {

	@Resource(name="accountDAO")
	private AccountDAO accountDAO;
	
	
	// 회계 정보 등록
	@Override
	public long registerAccount(EgovMap param) {
		System.out.println("[AccountServiceImpl] registerAccount() 호출됨");
        System.out.println("전달된 파라미터: " + param);
		
        // DAO 호출 (insert)
        accountDAO.insertAccount(param);
        
        // 자동 생성된 accountSeq 추출 (형변환하는 부분 GPT)
        Object pk = param.get("accountSeq");
        long accountSeq = 0L;
        if (pk instanceof Number) {
            accountSeq = ((Number) pk).longValue();
        }
        
        System.out.println("등록된 accountSeq: " + accountSeq);
        
		return accountSeq;
	}


	@Override
	public EgovMap getAccountDetail(Long accountSeq) {
		
		return null;
	}


	@Override
	public int modifyAccount(EgovMap param) {
		
		return 0;
	}

}
