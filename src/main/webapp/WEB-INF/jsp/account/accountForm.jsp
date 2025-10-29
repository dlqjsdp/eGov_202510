<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="ui" uri="http://egovframework.gov/ctl/ui"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<!-- jQuery & jQuery UI -->
<script src="/js/jquery/jquery.1.12.4.min.js"></script>
<script src="/js/jquery-ui-1.12.1/jquery-ui.min.js"></script>
<link rel="stylesheet" href="/js/jquery-ui-1.12.1/themes/blitzer/jquery-ui.css">

<script src="/js/common.js"></script>

<script>

	// 공용 JS 함수 및 초기 세팅
	// DB 저장용 (입력값 정리)
	function nvlCode(v){		
	  	return (v === undefined || v === null || v === '' || v === '0' || v === 0) ? null : v;
	}
	
	// 화면 출력용 (조회값 정리)
	function nv(v){ 
		return (v === 'null' || v == null) ? '' : v; 
	}

	$(function () {
		// 서버에서 데이터 받아오기 -> 받아온 데이터를 변수에 저장
		let MODE = '${mode}'; // insert / modify
		let DETAIL = {
		    ACCOUNT_SEQ : nv('<c:out value="${empty account.ACCOUNT_SEQ ? account.accountSeq : account.ACCOUNT_SEQ}" default=""/>'),
		    PROFIT_COST : nv('<c:out value="${empty account.PROFIT_COST ? account.profitCost : account.PROFIT_COST}" default=""/>'),
		    BIG_GROUP : nv('<c:out value="${empty account.BIG_GROUP ? account.bigGroup : account.BIG_GROUP}" default=""/>'),
		    MIDDLE_GROUP : nv('<c:out value="${empty account.MIDDLE_GROUP ? account.middleGroup : account.MIDDLE_GROUP}" default=""/>'),
		    SMALL_GROUP : nv('<c:out value="${empty account.SMALL_GROUP ? account.smallGroup : account.SMALL_GROUP}" default=""/>'),
		    DETAIL_GROUP : nv('<c:out value="${empty account.DETAIL_GROUP ? account.detailGroup : account.DETAIL_GROUP}" default=""/>'),
		    COMMENTS : nv('<c:out value="${empty account.COMMENTS ? account.comments : account.COMMENTS}" default=""/>'),
		    TRANSACTION_MONEY : nv('<c:out value="${empty account.TRANSACTION_MONEY ? account.transactionMoney : account.TRANSACTION_MONEY}" default=""/>'),
		    TRANSACTION_DATE : nv('<c:out value="${empty account.TRANSACTION_DATE ? account.transactionDate : account.TRANSACTION_DATE}" default=""/>')
		  };
		
	
	     // 하위 select 데이터 로드
		function loadChildren(parentCode, $target, firstAsNone, selectedValue, done){
		  let baseOption = firstAsNone 
		  	? '<option value="0">해당없음</option>' 
			: '<option value="">선택</option>';
		
		  // 상위 선택이 비어있을 대 하위 목록을 초기화
		  if(!parentCode){
		    $target.html(baseOption); // 하위에 기본 옵션(헤당없음/선택)만 넣음
		    if(selectedValue) $target.val(String(selectedValue)); // 수정모드처럼 미리 선택한 값이 있으면 그걸 표시
		    if(typeof done === 'function') done(); // done이 전달되면 호출해서 다음 함수 실행
		    return; // 더 이상 아래 ajax 호출하지 않고 종료
		  }
	
		$.ajax({
		  url: '/account/selectCombo.do',
		  type: 'GET',
		  dataType: 'json',
		  data: { category: parentCode },
		  success: function(res){
		    let list = (res && Array.isArray(res.list)) ? res.list : [];
		    
		    let html = baseOption; // 하위 select 박스에 들어갈 HTML(option 목록)을 만들기 시작하는 초기값 선언
		    for(let i=0; i<list.length; i++){
		      let r = list[i];
		      let code = r.code || r.CODE;
		      let name = r.comKor || r.COM_KOR;
		      html += '<option value="'+code+'">'+name+'</option>';
		    }
		    
		    $target.html(html); // 완성된 HTML을 select에 출력
		    
		    if(selectedValue) $target.val(String(selectedValue));
		    else if(firstAsNone) $target.val('0');
		    if(typeof done === 'function') done();
		  },
		  error: function(){
		    alert('코드 조회 중 오류가 발생했습니다.');
		    $target.html(baseOption); // 기본 옵션만 있는 상태로 초기화
		    if(firstAsNone) $target.val('0');
		    if(typeof done === 'function') done(); // done이 함수인 경우에만 실행
		  }
		});
	  }

	     
	  // 체인 이벤트 등록 (공통)
	  $('#profitCost').on('change', function(){
	    let code = $(this).val();
	    loadChildren(code, $('#bigGroup'), false);
	    $('#middleGroup, #smallGroup, #detailGroup').html('<option value="0">해당없음</option>');
	  });
	  $('#bigGroup').on('change', function(){
	    let code = $(this).val();
	    loadChildren(code, $('#middleGroup'), true);
	    $('#smallGroup, #detailGroup').html('<option value="0">해당없음</option>');
	  });
	  $('#middleGroup').on('change', function(){
	    let code = $(this).val();
	    loadChildren(code, $('#smallGroup'), true);
	    $('#detailGroup').html('<option value="0">해당없음</option>');
	  });
	  $('#smallGroup').on('change', function(){
	    let code = $(this).val();
	    loadChildren(code, $('#detailGroup'), true);
	  });
	
	  // 모드별 초기값 세팅 -> 받은 데이터를 화면에 표시하기
	  if(MODE === 'modify'){
	    $('#saveBtn').text('수정');
	    $('#accountSeq').val(DETAIL.ACCOUNT_SEQ);
	    
	    $('#profitCost').val(String(DETAIL.PROFIT_COST).toUpperCase());
	 	// 하위 select 자동 로드
	    loadChildren(DETAIL.PROFIT_COST, $('#bigGroup'), false, DETAIL.BIG_GROUP, function(){
	      loadChildren(DETAIL.BIG_GROUP, $('#middleGroup'), true, DETAIL.MIDDLE_GROUP, function(){
	        loadChildren(DETAIL.MIDDLE_GROUP, $('#smallGroup'), true, DETAIL.SMALL_GROUP, function(){
	          loadChildren(DETAIL.SMALL_GROUP, $('#detailGroup'), true, DETAIL.DETAIL_GROUP);
	        });
	      });
	    });
	
	    $('#comments').val(DETAIL.COMMENTS);
	    $('#transactionMoney').val(DETAIL.TRANSACTION_MONEY);
	    $('#transactionDate').val(DETAIL.TRANSACTION_DATE);
	
	  }else{
	    $('#saveBtn').text('등록');
	  }
	
	
	  // 저장(등록/수정) 버튼 클릭
	  $('#saveBtn').on('click', function(e){
	    e.preventDefault();
	
	    let payload = {
	      accountSeq: $('#accountSeq').val() || null,
	      profitCost: nvlCode($('#profitCost').val()),
	      bigGroup: nvlCode($('#bigGroup').val()),
	      middleGroup: nvlCode($('#middleGroup').val()),
	      smallGroup: nvlCode($('#smallGroup').val()),
	      detailGroup: nvlCode($('#detailGroup').val()),
	      comments: $('#comments').val(),
	      transactionMoney: $('#transactionMoney').val(),
	      transactionDate: $('#transactionDate').val()
	    };
	
	    if(!payload.profitCost){
	      alert('수익/비용 구분을 선택하세요.');
	      return;
	    }
	
	    let url = (MODE === 'modify') 
	    	? '/account/accountUpdateProc.do' 
	    	: '/account/accountInsertProc.do';
	
	    $.ajax({
	      url: url,
	      type: 'POST',
	      contentType: 'application/json; charset=UTF-8',
	      dataType: 'json',
	      data: JSON.stringify(payload),
	      success: function(res){
	        alert(res.message || (MODE === 'modify' ? '수정 성공' : '등록 성공'));
	        if(res.success){
	          if(MODE === 'insert' && res.accountSeq){
	            location.href = '/account/accountModify.do?accountSeq=' + res.accountSeq;
	          }else{
	            location.href = '/account/accountList.do';
	          }
	        }
	      },
	      error: function(){
	        alert('서버 오류가 발생했습니다.');
	      }
	    });
	  });
	
	});
</script>


<div class="container" style="margin-top: 50px">

  <input type="hidden" id="accountSeq" value=""/>

  <!-- 상위 select -->
  <div class="col-sm-12">
    <div class="col-sm-3">
      <select class="form-control" id="profitCost" name="profitCost">
        <option value="">선택</option>
        <c:forEach var="list" items="${resultMap}">
          <option value="${list.code}">${list.comKor}</option>
        </c:forEach>
      </select>
    </div>
    <div class="col-sm-3"><select class="form-control" id="bigGroup"><option value="">선택</option></select></div>
    <div class="col-sm-3"><select class="form-control" id="middleGroup"><option value="0">해당없음</option></select></div>
    <div class="col-sm-3"><select class="form-control" id="smallGroup"><option value="0">해당없음</option></select></div>
  </div>

  <div class="col-sm-12" style="margin-top:10px;">
    <div class="col-sm-3">
      <select class="form-control" id="detailGroup"><option value="0">해당없음</option></select>
    </div>
    <div class="col-sm-9">
      <input class="form-control" id="comments" type="text" placeholder="비용 상세 입력">
    </div>
  </div>

  <div class="col-sm-12" style="margin-top:10px;">
    <label class="col-sm-1 control-label">금액</label>
    <div class="col-sm-3">
      <input class="form-control" id="transactionMoney" type="number" min="1" oninput="this.value=this.value.replace(/[^0-9]/g,'')" placeholder="숫자만 입력">
    </div>
    <label class="col-sm-1 control-label">거래일자</label>
    <div class="col-sm-3">
      <input class="form-control datepicker" id="transactionDate" type="text" placeholder="YYYY-MM-DD" style="width:80%">
    </div>
  </div>

  <!-- 버튼 -->
  <div class="col-sm-12 text-center" style="margin-top:20px;">
    <button id="saveBtn" type="button" class="btn btn-primary">등록</button>
    <button type="button" class="btn btn-warning" onclick="history.back()">취소</button>
  </div>
</div>
