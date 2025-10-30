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
 *     2025.10.23		노유경 			modifyAccount() 반환타입을 boolean으로 변경 및 로직 완성 (성공/실패 여부 반환)
 *     2025.10.26       노유경                       목록 조회(getAccountList), 총건수(getAccountListCount) 구현 및 기본 페이징 파라미터 보정
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


	// 회계 정보 단건 조회
	@Override
	public EgovMap getAccountDetail(Long accountSeq) {
		System.out.println("[AccountServiceImpl] getAccountDetail() 호출됨" + accountSeq);
		return accountDAO.selectAccountDetail(accountSeq);
	}


	// 회계 정보 수정
	@Override
	public boolean modifyAccount(EgovMap param) {
		System.out.println("[AccountServiceImpl] modifyAccount() 호출됨");
		System.out.println("수정할 파라미터: " + param);
		
		int updatedRows = accountDAO.updateAccount(param);
		boolean success = updatedRows > 0;
		
		if(success) {
			System.out.println("수정 성공 ( " + updatedRows + "건 반영됨)" );
		} else {
			System.out.println("수정 실패 (DB 반영 0건)");
		}
		
		return success;
	}


    // 회계 정보 목록 조회 (페이징)
    @Override
    public List<EgovMap> getAccountList(EgovMap param) {
        System.out.println("[AccountServiceImpl] getAccountList() 호출됨");
        // 기본 페이징 보정 (null 방지)
        if (param.get("firstIndex") == null) { // 현재 페이지 번호
            param.put("firstIndex", 0);
        }
        if (param.get("recordCountPerPage") == null) { // 페이지당 보여줄 데이터 개수
            param.put("recordCountPerPage", 10);
        }
        System.out.println("목록 조회 파라미터: firstIndex=" + param.get("firstIndex")
                + ", recordCountPerPage=" + param.get("recordCountPerPage"));
        return accountDAO.selectAccountList(param);
    }

    // 회계 정보 총 건수 (페이징용)
    @Override
    public int getAccountListCount(EgovMap param) {
        System.out.println("[AccountServiceImpl] getAccountListCount() 호출됨");
        return accountDAO.selectAccountListCount(param);
    }
}
