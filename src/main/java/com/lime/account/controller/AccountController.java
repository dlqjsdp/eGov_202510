package com.lime.account.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.lime.account.service.AccountService;
import com.lime.common.service.CommonService;
import com.lime.util.CommUtils;

import egovframework.rte.psl.dataaccess.util.EgovMap;



/**
 * @Class Name  : AccountController.java
 * @Description : 회계 정보 관리 컨트롤러
 * @Modification Information
 * 
 *     수정일                         수정자                       수정내용
 *     ----------       ---------       -------------------------------------------------------
 *     2025.10.20       노유경                       /account/accountInsertProc.do 등록 처리 추가
 *     2025.10.20       노유경                       selectCombo.do AJAX 코드 조회 기능 추가
 * 
 */

@Controller
public class AccountController {


	@Resource(name = "jsonView")
	private MappingJackson2JsonView jsonView;

	@Resource(name="accountService")
	private AccountService accountService;

	@Resource(name="commonService")
	private CommonService commonService;

	
	/**
	 * 회계 목록 페이지 이동
	 * 
	 * @param request
	 * @param model
	 * @return accountList.jsp
	 * @throws Exception
	 */
	@RequestMapping(value = "/account/accountList.do")
	public String selectSampleList(HttpServletRequest request, ModelMap model) throws Exception {

		Map<String, Object> inOutMap  = CommUtils.getFormParam(request);

		model.put("inOutMap", inOutMap);
		return "/account/accountList";
	}



	/**
	 * 회계 등록 페이지 이동 (공통 코드 조회 포함)
	 * 
	 * @param request
	 * @param model
	 * @return accountInsert.jsp
	 * @throws Exception
	 */
	@RequestMapping(value="/account/accountInsert.do")
	public String accountInsert(HttpServletRequest request, ModelMap model) throws Exception{

		Map<String, Object> inOutMap = new HashMap<>();

		// 최상위 카테고리 조회 (A000000)
		inOutMap.put("category", "A000000");
		List<EgovMap> resultMap= commonService.selectCombo(inOutMap);

		System.out.println(resultMap);
		model.put("resultMap", resultMap);

		return "/account/accountInsert";
	}


	/**
	 * AJAX로 하위 코드 목록 조회
	 * 
	 * @param request
	 * @return JSON 형태 코드 리스트
	 * @throws Exception
	 */
	@RequestMapping(value="/account/selectCombo.do")
	public ModelAndView ajaxtest(HttpServletRequest request) throws Exception{
		System.out.println("/account/selectCombo.do");
		
		Map<String, Object> in = CommUtils.getFormParam(request);

		// 실제 목록 받아오기
		List<EgovMap> list = commonService.selectCombo(in);
		
		// 리스트를 넣어서 반환
		Map<String, Object> out = new HashMap<>();
		
		out.put("list", list);

		return new ModelAndView(jsonView, out);
	}

	
	/**
	 * 회계 등록 처리 (AJAX)
	 * 
	 * @param body - 클라이언트에서 전달된 JSON 데이터
	 * @return 등록 결과(JSON)
	 */
	@RequestMapping(value="/account/accountInsertProc.do", method=RequestMethod.POST)
	public ModelAndView insertAccountProc(@RequestBody Map<String,Object> body) {
		
		System.out.println("[AccountController] insertAccountProc() 호출됨");
		System.out.println("전달받은 데이터: " + body);
		
		// 결과 객체
		Map<String, Object> res = new HashMap<>();
		
		try {
			// DB 저장 처리
			EgovMap param = new EgovMap();
	        param.putAll(body); // DAO/SQL에서 사용하는 키 그대로 매핑
			accountService.insertAccount(param);
			
			// 성공 시 응답
			res.put("success", true);
			res.put("message", "등록이 완료되었습니다.");
			
		} catch (Exception e) {
			// 예외 발생 시 응답
			res.put("success", false);
			res.put("message", "등록 중 오류가 발생했습니다.");
			e.printStackTrace();
		}
	
		
		return new ModelAndView(jsonView, res);
	}



}// end of calss
