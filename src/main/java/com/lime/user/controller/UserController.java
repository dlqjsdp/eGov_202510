package com.lime.user.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lime.user.service.UserService;
import com.lime.user.vo.UserVO;

/**
 * @Class Name  : UserController.java
 * @Description : 회원가입 및 ID 중복검사 컨트롤러
 * @Modification Information
 * 
 *     수정일                       수정자                    수정내용
 *     ----------    ---------    -------------------------------
 *     2025.10.16    노유경                     회원가입 폼, ID 중복검사, 회원가입 처리
 *     2025.10.16    노유경                     메서드명 수정 (existsUserId -> isAvailableUserId)
 *     2025.10.16    노유경                     회원가입 시 아이디 null/공백 유효성 검증 추가
 */

@Controller
@RequestMapping("/user")
public class UserController {

    @Resource(name = "userService")
    private UserService userService;

    // 회원가입 폼 페이지 진입
    @RequestMapping(value="/userInsert.do", method=RequestMethod.GET)
    public String userInsertForm() {
        return "/user/userInsert"; // JSP: /WEB-INF/jsp/user/userInsert.jsp
    }

    // ID 중복체크 (AJAX)
    @ResponseBody
    @RequestMapping(value="/checkId.do", method=RequestMethod.GET)
    public Map<String, Object> checkId(@RequestParam("userId") String userId) {
        boolean available = userService.isAvailableUserId(userId);

        Map<String, Object> result = new HashMap<>();
        System.out.println("전달 받은 ID: " + userId);
        
        result.put("available", available);
        result.put("message", available ? "사용 가능한 ID입니다." : "사용 불가능한 ID입니다.");
        return result;
    }

    // 회원가입 처리
    @ResponseBody
    @RequestMapping(value="/insertUser.do", method=RequestMethod.POST)
    public Map<String, Object> insertUser(@RequestBody UserVO vo) {
    	Map<String, Object> result = new HashMap<>();
    	
    	System.out.println("===============================");
    	System.out.println("userId : " + vo.getUserId());
        System.out.println("pwd : " + vo.getPwd());
        System.out.println("userName : " + vo.getUserName());
        System.out.println("===============================");
    	
    	// 아이디 누락 체크
        if (vo == null || vo.getUserId() == null || vo.getUserId().trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "아이디를 입력해 주세요.");
            return result; // JSON 응답 반환
        }
        
    	try{ // 회원가입 서비스 호출
    		userService.register(vo);
    		result.put("success", true);
    		result.put("message", "회원가입이 완료되었습니다.");
    	}catch(Exception e) {
    		result.put("success", false);
    		result.put("message", "회원가입 중 오류가 발생했습니다.");
    	}
        
    	return result; // 결과 반환
    }
}
