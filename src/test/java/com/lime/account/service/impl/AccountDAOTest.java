package com.lime.account.service.impl;

import static org.junit.Assert.*;

import java.sql.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import egovframework.rte.psl.dataaccess.util.EgovMap;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	    "classpath:egovframework/spring/context-datasource.xml",
	    "classpath:egovframework/spring/context-mapper.xml",
	    "classpath:egovframework/spring/context-transaction.xml",
	    "classpath:egovframework/spring/context-common.xml"
	})
@Transactional
public class AccountDAOTest {
	
	@Resource(name="accountDAO")
	private AccountDAO accountDAO;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	
	@Test
	public void insertAccountTest(){
		// given
		EgovMap param = new EgovMap();
		param.put("profitCost", "A000000"); // 수익/비용 구분 (예: A000000 = 비용)
		param.put("bigGroup", "AB00000"); // 대분류 (예: 경상운영비)
		param.put("middleGroup", "ABB0000"); // 중분류 (예: 미지급금)
		param.put("smallGroup", "ABBAA00"); // 소분류 (예: 차량관리비)
		param.put("detailGroup", "ABBAA04"); // 세부항목 (예: 차량유지비)
		param.put("comments", "차량 관리비 테스트 등록");
		param.put("transactionMoney", 20000);
		param.put("transactionDate", Date.valueOf("2025-10-20"));
		param.put("writer", "admin");
		
		// when
		accountDAO.insertAccount(param);
		
		// then
		assertTrue(true);
	}
	

	/*
	 * DB에서 단건 조회하기
	 * (DB에 있는 내용 가지고 조회)
	 */
	@Test
	public void selectAccountDetailTest(){
		// given
		long accountSeq = 17L; // DB에 이미 존재하는 pk
		
		// when
		EgovMap row = accountDAO.selectAccountDetail(accountSeq);
		
		// then
		assertNotNull("조회 결과가 있어야 함", row);
		System.out.println("조회 결과: "+ row);
		
		// 값 검증
		assertEquals("AB00000", row.get("profitCost"));
		assertEquals("ABA0000", row.get("bigGroup"));
		assertEquals("ABAC000", row.get("middleGroup"));
		assertEquals("ABACB00", row.get("smallGroup"));
		assertEquals("ABACB01", row.get("detailGroup"));
		assertEquals("코멘트2", row.get("comments"));
		assertEquals(1798798, ((Number) row.get("transactionMoney")).intValue());
		assertEquals(Date.valueOf("2025-10-15"), row.get("transactionDate"));
		assertEquals("admin4", row.get("writer"));
	}

}
