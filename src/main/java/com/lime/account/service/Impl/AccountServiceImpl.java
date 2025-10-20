package com.lime.account.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lime.account.service.AccountService;

import egovframework.rte.psl.dataaccess.util.EgovMap;

/**
 * @Class Name  : AccountServiceImpl.java
 * @Description : 회계 정보 관리 서비스 구현 클래스
 * @Modification Information
 * 
 *     수정일                         수정자                       수정내용
 *     ----------       ---------       -------------------------------------------
 *     2025.10.21       노유경                       회계 정보 등록 기능 구현
 * 
 */

@Service("accountService")
public class AccountServiceImpl implements AccountService {

	@Resource(name="accountDAO")
	private AccountDAO accountDAO;
	

	// 회계 정보 등록
	@Override
	public void insertAccount(EgovMap param) {
		accountDAO.insertAccount(param);
		
	}






}
