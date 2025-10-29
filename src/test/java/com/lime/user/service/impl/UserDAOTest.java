package com.lime.user.service.impl;

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

import com.lime.user.vo.UserVO;

import egovframework.rte.psl.dataaccess.util.EgovMap;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	    "classpath:egovframework/spring/context-datasource.xml",
	    "classpath:egovframework/spring/context-mapper.xml",
	    "classpath:egovframework/spring/context-transaction.xml",
	    "classpath:egovframework/spring/context-common.xml"
	})
@Transactional
public class UserDAOTest {
	
	@Resource(name="userDAO")
	private UserDAO userDAO;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	// DB에 존재하지 않는 아이디 중복 체크
    @Test
    public void checkId_notExistsUserId() {
        // given
    	String notExistsUserId = "no_such_user"; // 존재하지 않는 ID로 고정

    	// when
        int count = userDAO.checkId(notExistsUserId);
        System.out.println("조회 결과 count = " + count);

        // then
        assertEquals("존재하지 않는 ID는 0이어야 함", 0, count);
    }
    
    // DB에 존재하는 아이디 중복 체크
    @Test
    public void checkId_existingUserId() {
        // given
        String existingUserId = "admin4"; // DB에 존재하는 값 (USER_ID 컬럼 참조)

        // when
        int count = userDAO.checkId(existingUserId);
        System.out.println("조회 결과 count = " + count);

        // then
        assertTrue("DB에 존재하는 ID는 1 이상이어야 함", count >= 1);
    }
	

    @Test
    public void insertUser() {
        // given
        String newUserId = "insert_test";
        UserVO vo = new UserVO();
        vo.setUserId(newUserId);
        vo.setPwd("Abc123!");
        vo.setUserName("홍길동");

        int before = userDAO.checkId(newUserId);
        assertEquals(0, before);

        // when
        userDAO.insertUser(vo);

        // then
        int after = userDAO.checkId(newUserId);
        assertEquals(1, after);
    }

}
