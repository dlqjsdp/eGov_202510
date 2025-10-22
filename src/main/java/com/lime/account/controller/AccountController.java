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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.lime.account.service.AccountService;
import com.lime.common.service.CommonService;
import com.lime.user.vo.UserVO;
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
 *     2025.10.21       노유경                       AJAX 응답 구조 개선 (ModelAndView → Map 반환), 금액 검증 및 세션 기반 작성자 정보 추가
 *     2025.10.21       노유경                       하위 코드 조회(selectCombo) JSON 반환 구조 수정 (list 키 추가)
 *     2025.10.21       노유경                       insertAccount() 서비스 호출 후 accountSeq 반환 로직 추가
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
		List<EgovMap> resultMap= commonService.selectCombo(inOutMap); // 호출로 최상위 하위목록(=실질적 상위의 자식들) 조회

		System.out.println(resultMap);
		model.put("resultMap", resultMap); // 결과를 model.put("resultMap", resultMap)로 JSP에 전달

		return "/account/accountInsert";
	}


	/**
	 * AJAX로 하위 코드 목록 조회 - 수정함
	 * 
	 * @param request
	 * @return JSON 형태 코드 리스트
	 * @throws Exception
	 */
	@RequestMapping(value="/account/selectCombo.do")
	public ModelAndView ajaxtest(HttpServletRequest request) throws Exception{
		System.out.println("/account/selectCombo.do");

		Map<String, Object> in = CommUtils.getFormParam(request); // 요청 파라미터 추출(예: category=0101)

		List<EgovMap> list = commonService.selectCombo(in); // 해당 카테고리의 하위 코드 목록 조회

	    Map<String, Object> out = new HashMap<>();
	    out.put("list", list); // 리스트 형태로 JSON 응답 구성

	    return new ModelAndView(jsonView, out); // JSON으로 직렬화되어 응답
	}
	
	/**
	 * 회계 정보 등록 처리 (AJAX)
	 * 
	 * @param body - 클라이언트에서 전달된 JSON 데이터
	 * @return 등록 결과(JSON)
	 */
	@ResponseBody
	@RequestMapping(value="/account/accountInsertProc.do", method=RequestMethod.POST)
	public Map<String, Object> insertAccountProc(@RequestBody Map<String,Object> body, HttpServletRequest request) {
		
		System.out.println("[AccountController] insertAccountProc() 호출됨");
		System.out.println("전달받은 데이터: " + body);
		
		// 결과 객체
		Map<String, Object> res = new HashMap<>();
		
		try {
			// 1. 사용자 입력값 검증
			if(CommUtils.isEmpty((String) body.get("profitCost"))) {
				res.put("success", false);
				res.put("message", "수익/비용 구분은 필수입니다.");
				return res;
			}
	        
	        // 2. 세션에 작성자 정보 추가
	        UserVO loginUser = (UserVO) request.getSession().getAttribute("LoginUser");
	        if (loginUser == null) {
	            res.put("success", false);
	            res.put("message", "로그인이 필요합니다.");
	            return res;
	        }
	        body.put("writer", loginUser.getUserId());
	        
			// DB 저장 처리
			EgovMap param = new EgovMap();
	        param.putAll(body); // DAO/SQL에서 사용하는 키 그대로 매핑
			accountService.insertAccount(param);
			
			System.out.println("생성된 accountSeq = " + param.get("accountSeq"));
			
			// 생성된 PK (새로 만들어진 게시글 번호를 응답으로 보내주려는 목적)
			Object accountSeq = param.get("accountSeq");
			if (accountSeq != null) {
			    res.put("accountSeq", accountSeq);
			}
			
			// 성공 시 응답
			res.put("success", true);
			res.put("message", "등록이 완료되었습니다.");
			
		} catch (Exception e) {
			// 예외 발생 시 응답
			res.put("success", false);
			res.put("message", "등록 중 오류가 발생했습니다.");
		}
	
		return res;
	}



}// end of calss
