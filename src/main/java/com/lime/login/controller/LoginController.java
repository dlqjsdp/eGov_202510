package com.lime.login.controller;

import java.util.HashMap;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.lime.common.service.CommonService;
import com.lime.login.service.LoginService;
import com.lime.user.vo.UserVO;
import com.lime.util.CommUtils;

/**
 * @Class Name  : LoginController.java
 * @Description : 로그인 페이지 진입 및 로그인 처리 컨트롤러
 * @Modification Information
 * 
 *   수정일                      수정자                     수정내용
 *   ----------    ---------    -------------------------------
 *   2025.10.17    노유경                    로그인 페이지 매핑 및 로그인 처리 기능 구현
 *   2025.10.17    노유경                    @RequestBody 기반 JSON 요청 처리 및 세션 저장 로직 추가
 *   2025.10.17    노유경                    로그인 실패 시 메시지 반환 및 성공 시 /account/accountList.do 이동 설정
 *   
 */

@Controller
public class LoginController {


	@Resource(name = "jsonView")
	private MappingJackson2JsonView jsonView;

	@Resource(name="commonService")
	private CommonService commonService;
	
	@Resource(name="loginService")
	private LoginService loginService;
	

	// 로그인 페이지 이동
	@RequestMapping(value="/login/login.do")
	public String loginview(HttpServletRequest request ) {

		return "/login/login";
	}

	// 아이디 중복확인 (?)
	@RequestMapping(value="/login/idCkedAjax.do")
	public ModelAndView idCkedAjax(HttpServletRequest request ) throws Exception {
		Map<String, Object> inOutMap  = CommUtils.getFormParam(request);

		return new ModelAndView(jsonView, inOutMap);
	}
	
	// 로그인 처리
	@ResponseBody
	@RequestMapping(value="/login/loginProc.do", method=RequestMethod.POST)
	public Map<String, Object> loginProc(@RequestBody UserVO inputVO, HttpSession session) {
		Map<String, Object> result = new HashMap<>();
		System.out.println("[LoginController] loginProc() 호출됨");
    	System.out.println("userId : " + inputVO.getUserId());
        System.out.println("pwd : " + inputVO.getPwd());
		
		// 1. 입력 정보 유효성 검사
		if(inputVO.getUserId() == null || inputVO.getUserId().trim().isEmpty() ||
				inputVO.getPwd() == null || inputVO.getPwd().isEmpty()) {
			System.out.println("[LoginController] 아이디 또는 비밀번호 누락");
			result.put("success", false);
			result.put("message", "아이디와 비밀번호를 입력해주세요.");
			return result;
		}
        
		// 2. 서비스 호출 (아이디/비밀번호 검증)
		UserVO loginUser = loginService.login(inputVO);
		
		// 3. 로그인 실패 처리
		if(loginUser == null) {
			System.out.println("[LoginController] 로그인 실패 - 정보 불일치");
			result.put("success", false);
			result.put("message", "아이디 또는 비밀번호가 올바르지 않습니다.");
			return result;
		}
		
		// 4. 성공 시 세션 저장 (비밀번호는 제외)
		loginUser.setPwd(null); // 비밀번호는 저장 안함
		session.setAttribute("LOGIN_USER", loginUser);
		System.out.println("[LoginController] 로그인 성공 - 세션 저장 완료");
		// 세션 정보 확인
		System.out.println("=== 세션 확인 ===");
		System.out.println("세션 ID : " + session.getId());
		System.out.println("세션 저장된 사용자 : " + ((UserVO)session.getAttribute("LOGIN_USER")).getUserId());
		System.out.println("=================");
		
		// 5. 성공 응답
		result.put("success", true);
		result.put("redirect", "/account/accountList.do");
		System.out.println("[LoginController] JSON 응답 반환 완료");
		return result;
	}


}// end of class
