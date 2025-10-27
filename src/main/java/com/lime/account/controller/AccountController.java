package com.lime.account.controller;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.lime.account.service.AccountService;
import com.lime.common.service.CommonService;
import com.lime.user.vo.UserVO;
import com.lime.util.CommUtils;

import egovframework.rte.psl.dataaccess.util.EgovMap;
import egovframework.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;



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
 *     2025.10.22    	노유경       		서비스 메서드 네이밍 변경(registerAccount 적용), 세션 키명 통일("LOGIN_USER")
 *     2025.10.23      	노유경     			수정화면 진입(/account/accountModify.do) 및 단건조회 연동 추가
 *     2025.10.23      	노유경     			수정 처리(/account/accountUpdateProc.do) 추가: Service boolean 반환에 맞춘 JSON 응답
 *     2025.10.23      	노유경     			accountModify에서 resultMap(수익/비용 코드 리스트) 추가 전달
 *     2025.10.26       노유경                       페이징 처리 기능(accountList.do) 추가: PaginationInfo 적용, LIMIT/OFFSET 기반 목록 조회
 *     2025.10.26       노유경                       엑셀 다운로드 기능(accountListExcel.do) 추가: 현재 페이지 데이터 XLSX 파일로 생성 및 다운로드
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
	 * 회계 목록 출력 (AJAX)
	 * @param pageIndex           현재 페이지 번호 (기본값: 1)
	 * @param recordCountPerPage  페이지당 출력 건수 (기본값: 10)
	 * @param pageSize            페이징 블록 크기 (기본값: 10)
	 * @return 회계 목록 및 페이징 정보(JSON)
	 * @throws Exception
	 * 
	 */
	@ResponseBody
	@RequestMapping(value = "/account/accountListData.do")
	public Map<String, Object> accountListData(
		    @RequestParam(defaultValue = "1")  int pageIndex,
		    @RequestParam(defaultValue = "10") int recordCountPerPage,
		    @RequestParam(defaultValue = "10") int pageSize
		) throws Exception {

		// 1. eGov 페이징
	    PaginationInfo paginationInfo = new PaginationInfo();
	    paginationInfo.setCurrentPageNo(pageIndex);
	    paginationInfo.setRecordCountPerPage(recordCountPerPage);
	    paginationInfo.setPageSize(pageSize);

	    // Limit/offset
	    EgovMap param = new EgovMap();
	    param.put("firstIndex", paginationInfo.getFirstRecordIndex()); // OFFSET
	    param.put("recordCountPerPage", paginationInfo.getRecordCountPerPage()); // LIMIT

	    // DB 조회
	    List<EgovMap> list = accountService.getAccountList(param);
	    int totalCount = accountService.getAccountListCount(param);

	    // Json 응답 구성
	    Map<String, Object> result = new HashMap<>();
	    result.put("resultList", list);
	    result.put("totalCount", totalCount);
	    result.put("pageIndex", pageIndex);
	    result.put("recordCountPerPage", recordCountPerPage);
	    result.put("pageSize", pageSize);
	    return result;
	}
	
	
	/**
	 * 회계 목록 엑셀(XLSX) 다운로드
	 * 
	 * @param pageIndex           현재 페이지 번호 (기본값: 1)
	 * @param recordCountPerPage  페이지당 출력 건수 (기본값: 10)
	 * @param pageSize            페이징 블록 크기 (기본값: 10)
	 * @param response            파일 스트림 전송용 HTTP 응답 객체(본문 없음)
	 * @throws Exception          조회/엑셀 생성/전송 과정에서 발생 가능한 예외
	 */
	@RequestMapping(value="/account/accountListExcel.do")
	public void accountListExcel(
			@RequestParam(defaultValue = "1")  int pageIndex,
		    @RequestParam(defaultValue = "10") int recordCountPerPage,
		    @RequestParam(defaultValue = "10") int pageSize,
			HttpServletResponse response
			) throws Exception {

	    // 1. eGov 페이징 계산
	    PaginationInfo paginationInfo = new PaginationInfo();
	    paginationInfo.setCurrentPageNo(pageIndex);
	    paginationInfo.setRecordCountPerPage(recordCountPerPage);
	    paginationInfo.setPageSize(pageSize);

	    // 2. 목록 조회 파라미터(목록 AJAX와 동일하게 구성)
	    EgovMap param = new EgovMap();
	    param.put("firstIndex", paginationInfo.getFirstRecordIndex()); // OFFSET
	    param.put("recordCountPerPage", paginationInfo.getRecordCountPerPage()); // LIMIT

	    // 3. 현재 페이지 데이터 조회(기존 서비스 재사용)
	    List<EgovMap> resultList = accountService.getAccountList(param);

	    // 4. 엑셀 생성(XLSX)
	    XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("회계정보리스트");
		XSSFRow headerRow = sheet.createRow(0); 
		
	    // 헤더
	    String[] headers = {"수익/비용","관","항","목","과","금액","등록일","작성자"};
	    for (int i = 0; i < headers.length; i++) {
			headerRow.createCell(i).setCellValue(headers[i]);
		}

	    // 본문(현재 페이지 데이터만)
	    for (int i = 0; i < resultList.size(); i++) {
			EgovMap row = resultList.get(i);
			XSSFRow excelRow = sheet.createRow(i + 1);
			excelRow.createCell(0).setCellValue(row.get("profitCostNm") != null ? row.get("profitCostNm").toString() : "");
			excelRow.createCell(1).setCellValue(row.get("bigGroupNm") != null ? row.get("bigGroupNm").toString() : "");
			excelRow.createCell(2).setCellValue(row.get("middleGroupNm") != null ? row.get("middleGroupNm").toString() : "");
			excelRow.createCell(3).setCellValue(row.get("smallGroupNm") != null ? row.get("smallGroupNm").toString() : "");
			excelRow.createCell(4).setCellValue(row.get("detailGroupNm") != null ? row.get("detailGroupNm").toString() : "");
			excelRow.createCell(5).setCellValue(row.get("transactionMoney") != null ? row.get("transactionMoney").toString() : "");
			excelRow.createCell(6).setCellValue(row.get("transactionDate") != null ? row.get("transactionDate").toString() : "");
			excelRow.createCell(7).setCellValue(row.get("writerNm") != null ? row.get("writerNm").toString() : "");
		}

		// 5. 응답 헤더 및 전송
		String filename = URLEncoder.encode("회계정보_현재페이지.xlsx", "UTF-8").replaceAll("\\+", "%20");
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + filename);

		workbook.write(response.getOutputStream()); // response.getOutputStream() → 서버에서 클라이언트(브라우저)로 나가는 데이터 스트림. 엑셀 파일 내용을 이 스트림에 직접 써서 → 브라우저로 전송.
		workbook.close();

		System.out.println("엑셀다운 pageIndex: " + pageIndex + ", recordCountPerPage: " + recordCountPerPage);
	
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
	        UserVO loginUser = (UserVO) request.getSession().getAttribute("LOGIN_USER");
	        if (loginUser == null) {
	            res.put("success", false);
	            res.put("message", "로그인이 필요합니다.");
	            return res;
	        }
	        
			// 3. 파라미터 구성
			EgovMap param = new EgovMap();
	        param.putAll(body); // DAO/SQL에서 사용하는 키 그대로 매핑
	        param.put("writer", loginUser.getUserId()); // 작성자 세션에서 주입
			
			
			// 4. 등록 처리 → 생성된 PK 반환
            long accountSeq = accountService.registerAccount(param);

            res.put("success", true);
            res.put("message", "등록이 완료되었습니다.");
            res.put("accountSeq", accountSeq); // 프론트에서 곧바로 수정화면으로 이동 가능
			
		} catch (Exception e) {
			// 예외 발생 시 응답
			res.put("success", false);
			res.put("message", "등록 중 오류가 발생했습니다.");
		}
	
		return res;
	}
	
	/**
	 * 회계 수정 페이지로 이동
	 * - accountSeq로 단건 조회하여 화면에 바인딩
	 * @throws Exception 
	 * 
	 */
	@RequestMapping(value="/account/accountModify.do")
	public String accountModify(@RequestParam("accountSeq") Long accountSeq, ModelMap model) throws Exception {
		System.out.println("[AccountController] accountModify() 호출됨: accountSeq= " + accountSeq);
		
		// 1. 단건 조회
		EgovMap detail = accountService.getAccountDetail(accountSeq); // 
		model.put("account", detail);
		
		// 2. 상단 수익/비용 셀렉트 옵션
		Map<String, Object> inOutMap = new HashMap<>();

		// 최상위 카테고리 조회 (A000000)
		inOutMap.put("category", "A000000");
		List<EgovMap> resultMap = commonService.selectCombo(inOutMap); // 호출로 최상위 하위목록(=실질적 상위의 자식들) 조회

		System.out.println(resultMap);
		model.put("resultMap", resultMap); // 결과를 model.put("resultMap", resultMap)로 JSP에 전달
		
		return "/account/accountModify";
		
	}
	
	/**
	 * 회계 정보 수정 처리 (AJAX)
	 * @param body
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/account/accountUpdateProc.do", method=RequestMethod.POST)
	public Map<String, Object> accountUpdateProc(@RequestBody Map<String, Object> body, HttpServletRequest request) {
		System.out.println("[AccountController] accountUpdateProc() 호출됨");
		System.out.println("수정 요청 데이터: " + body);
		
		Map<String, Object> res = new HashMap<>();
		try {
			
			// accountSeq 존재 확인
			Object seqObj = body.get("accountSeq");
			if (seqObj == null) {
				res.put("success", false);
				res.put("message", "수정 대상이 없습니다. (accountSeq 누락됨)");
				return res;
			}
			
			// 로그인 사용자 확인
			UserVO loginUser = (UserVO) request.getSession().getAttribute("LOGIN_USER");
	        if (loginUser == null) {
	            res.put("success", false);
	            res.put("message", "로그인이 필요합니다.");
	            return res;
	        }
	        
	        EgovMap param = new EgovMap();
	        param.putAll(body);
	        
	        boolean success = accountService.modifyAccount(param);
	        res.put("success", success);
            res.put("message", success ? "정상적으로 수정되었습니다." : "수정된 데이터가 없습니다.");
	        
		}catch (Exception e) {
			res.put("success", false);
            res.put("message", "수정 중 오류가 발생했습니다.");
		}
		
		return res;
	}
	


}// end of class
