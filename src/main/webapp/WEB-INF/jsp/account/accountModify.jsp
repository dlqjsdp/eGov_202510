<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="ui" uri="http://egovframework.gov/ctl/ui"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<script src="/js/common.js"></script>

<script>
$(document).ready(function(){
	
	function nvlCode(v){
		return (v === undefined || v === null || v === '' || v === '0' || v === 0) ? null : v;
		}
	
	
	 // 0) 서버에서 바인딩된 단건 데이터 (대문자 키 주의)
	 function nv(v){ return (v === 'null' || v == null) ? '' : v; }
	 var detail = {
			 ACCOUNT_SEQ: nv('<c:out value="${account.get("accountSeq")}" default=""/>'),
			 PROFIT_COST: nv('<c:out value="${account.get("profitCost")}" default=""/>'),
			 BIG_GROUP: nv('<c:out value="${account.get("bigGroup")}" default=""/>'),
			 MIDDLE_GROUP: nv('<c:out value="${account.get("middleGroup")}" default=""/>'),
			 SMALL_GROUP: nv('<c:out value="${account.get("smallGroup")}" default=""/>'),
			 DETAIL_GROUP: nv('<c:out value="${account.get("detailGroup")}" default=""/>'),
			 COMMENTS: nv('<c:out value="${account.get("comments")}" default=""/>'),
			 TRANSACTION_MONEY: nv('<c:out value="${account.get("transactionMoney")}" default=""/>'),
			 TRANSACTION_DATE: nv('<c:out value="${account.get("transactionDate")}" default=""/>')
	 };
	 console.log('detail =', detail);

	// 1. 하위 select 채우기
	function loadChildren(parentCode, $target, firstAsNone, selectedValue, done) {
		
		// 기본 옵션 구성
		var baseOption = firstAsNone
		  ? '<option value="0">해당없음</option>'
		  : '<option value="">선택</option>';
		
		// 상위 코드가 없을 경우 기본 옵션만 표시하고 종료
		if (!parentCode) {
		  $target.html(baseOption);
		  if (selectedValue != null && selectedValue !== '') $target.val(String(selectedValue));
		  if (typeof done === 'function') done();
		  return;
		}
		
		// 하위 코드 목록을 서버에서 가져옴 (AJAX)
		$.ajax({
			url: '/account/selectCombo.do',
			type: 'GET',
			dataType: 'json',
			data: {category: parentCode}, // 서버로 보낼 파라미터: category=상위코드
			success: function(res) {
				console.log('selectCombo res =', res);
				
				// 1) 응답 리스트 추출 (배열이 아닐 경우 빈 배열)
				var list = (res && Array.isArray(res.list)) ? res.list : [];
				
			    // 2) for문으로 <option> 생성: 기본옵션 + 데이터 옵션
			    var html = baseOption;
				for (var i = 0; i < list.length; i++) {
					var row = list[i];
					var code = row.code || row.CODE; // 대/소문자 동시 대응
			        var name = row.comKor || row.COM_KOR; // 대/소문자 동시 대응

			        html += '<option value="' + code + '">' + name + '</option>';
				}
				
				// 3) 하위 select 태그에 넣기
				$target.html(html);
				
				if (selectedValue != null && selectedValue !== '') {
					$target.val(String(selectedValue));
				} else if (firstAsNone) {
					$target.val('0');
				}
				
				if (typeof done === 'function') done();
			},
			
			// 4) 오류 처리
			error: function(){
				alert('코드 조회 중 오류가 발생했습니다.');
				$target.html(baseOption); // 실패 시에도 기본옵션 유지
				if (firstAsNone) $target.val('0');
			    if (typeof done === 'function') done();
			}
		});
	}
	
	// 폼 초기값 세팅 (상위 -> 하위 순서대로 값 세팅 보장)
	$('#profitCost').val(String(detail.PROFIT_COST).toUpperCase());
	
	loadChildren(detail.PROFIT_COST, $('#bigGroup'), false, detail.BIG_GROUP, function() {
		loadChildren(detail.BIG_GROUP, $('#middleGroup'), true, detail.MIDDLE_GROUP, function(){
			loadChildren(detail.MIDDLE_GROUP, $('#smallGroup'), true, detail.SMALL_GROUP, function(){
				loadChildren(detail.SMALL_GROUP, $('#detailGroup'), true, detail.DETAIL_GROUP, function(){
					
					// 인풋류
					$('#comments').val(detail.COMMENTS);
					$('#transactionMoney').val(detail.TRANSACTION_MONEY);
					$('#transactionDate').val(detail.TRANSACTION_DATE);
				});
			});
		});
	});

	
	// 2. 체인 이벤트: 상위 선택 시 하위 초기화 및 로드
	$('#profitCost').on('change', function(){
		var code = $(this).val();
		console.log('[profitCost change] code=', code);
		loadChildren(code, $('#bigGroup'), false);
		$('#middleGroup').html('<option value="0">해당없음</option>');
		$('#smallGroup').html('<option value="0">해당없음</option>');
		$('#detailGroup').html('<option value="0">해당없음</option>');
	});
	
	$('#bigGroup').on('change', function(){
		var code = $(this).val();
		loadChildren(code, $('#middleGroup'), true);
		$('#smallGroup').html('<option value="0">해당없음</option>');
		$('#detailGroup').html('<option value="0">해당없음</option>');
	});
	
	$('#middleGroup').on('change', function(){
		var code = $(this).val();
		loadChildren(code, $('#smallGroup'), true);
		$('#detailGroup').html('<option value="0">해당없음</option>');
	});
	
	$('#smallGroup').on('change', function(){
		var code = $(this).val();
		loadChildren(code, $('#detailGroup'), true);
	});
	
	// 3. 수정(저장): 폼 값 수집 -> JSON POST -> 성공 시 이동
	$('#saveBtn').on('click', function(e) {
		e.preventDefault(); // 기본 폼 submit 방지(페이지 리로드 방지)
		
		var accountSeq = $('#accountSeq').val() || detail.ACCOUNT_SEQ;
		
		if(!accountSeq) {
			alert('수정 대상이 없습니다.');
			return;
		}
		
		// payload에 넣어서 전송
		var payload = {
			accountSeq: accountSeq,
			profitCost: nvlCode($('#profitCost').val()),
			bigGroup: nvlCode($('#bigGroup').val()),
			middleGroup: nvlCode($('#middleGroup').val()),
			smallGroup: nvlCode($('#smallGroup').val()),
			detailGroup: nvlCode($('#detailGroup').val()),
			comments: $('#comments').val(),
			transactionMoney: $('#transactionMoney').val(),
			transactionDate: $('#transactionDate').val(),
			// writer는 서버가 세션에서 주입
		};
		
		// select null 값 방지
		if(!payload.profitCost) {
			alert('수익/비용 구분을 선택하세요.');
			return;
		}
		
		// 수정 API 호출(JSON POST)
		$.ajax({
			url: '/account/accountUpdateProc.do',
			type: 'POST',
			contentType: 'application/json; charset=UTF-8', // 본문이 JSON
			dataType: 'json', // 응답도 JSON 기대
			data: JSON.stringify(payload), // 객체 → JSON 문자열
			success: function(res) { // 서버 메시지 우선 표시, 없으면 성공/실패 기본 문구
				alert(res.message || (res.success ? '수정 성공' : '수정 실패'));
				if(res.success) {
					// 목록으로
					location.href = '/account/accountList.do';
					
				}
			},
			error: function(){
				alert('서버 오류가 발생했습니다.')
			}
			
		});
	});

});


</script>

<!-- 비용 START -->
<div class="container" style="margin-top: 50px">
	<div class="col-sm-12"><label for="disabledInput" class="col-sm-12 control-label"></label></div>
	<div class="col-sm-12"><label for="disabledInput" class="col-sm-12 control-label"></label></div>
	<div class="col-sm-12"><label for="disabledInput" class="col-sm-12 control-label"></label></div>
	<div class="col-sm-12"><label for="disabledInput" class="col-sm-12 control-label"></label></div>



	<div class="col-sm-11" id="costDiv">
		<div>
			<div class="col-sm-11">
			
					<input type="hidden" id="accountSeq" value="<c:out value='${account.get("ACCOUNT_SEQ")}' default=''/>" >
					
			 		<div class="col-sm-12">
				      <div class="col-sm-3">
						<select class="form-control" id="profitCost" name="profitCost" title="비용">
				        	<option value="">선택</option>
				        	<c:forEach var="list" items="${resultMap}" varStatus="cnt">
					        	<option value="${list.code}">${list.comKor}</option>
				        	</c:forEach>
				        </select>
				      </div>

				      <div class="col-sm-3">
						<select class="form-control" id="bigGroup" name="bigGroup" title="관">
				        	<option value="">선택</option>
				        </select>
				      </div>

				      <div class="col-sm-3">
						<select class="form-control" id="middleGroup" name="middleGroup" title="항">
					        	<option value="0">해당없음</option>
				        </select>
				      </div>

				      <div class="col-sm-3">
						<select class="form-control" id="smallGroup" name="smallGroup" title="목">
					        	<option value="0">해당없음</option>
				        </select>
				      </div>
			 		</div>

			 		<div class="col-sm-12">  <label for="disabledInput" class="col-sm-12 control-label"> </label></div>
			 		<div class="col-sm-12">
			 			  <div class="col-sm-3">
								<select class="form-control" id="detailGroup" name="detailGroup" title="과">
							        	<option value="0">해당없음</option>
						        </select>
					      </div>
				      <div class="col-sm-9">
				      		<input class="form-control"  id="comments" name="comments" type="text" value="" placeholder="비용 상세 입력" title="비용 상세">
				      </div>
			 		</div>

				<div class="col-sm-12">  <label for="disabledInput" class="col-sm-12 control-label"> </label></div>
			 		<div class="col-sm-12">
			 		  <label for="disabledInput" class="col-sm-1 control-label"><font size="1px">금액</font></label>
				      <div class="col-sm-3">
				        	<input class="form-control"
							       id="transactionMoney"
							       name="transactionMoney"
							       type="number"
							       min="1"
							       oninput="this.value = this.value.replace(/[^0-9]/g, '')"
							       placeholder="숫자만 입력"
							       title="금액">
				      </div>
			 		  <label for="disabledInput" class="col-sm-1 control-label"><font size="1px">거래일자</font></label>
				      <div class="col-sm-3">
				      	 <!-- datepicker 클래스 추가 -->
				       	 <input class="form-control datepicker" id="transactionDate" name="transactionDate" type="text" value="" style="width: 80%" title="거래일자" placeholder="YYYY-MM-DD">
				      </div>
			 		</div>

					<div class="col-sm-12"><label for="disabledInput" class="col-sm-12 control-label"></label></div>
					<div class="col-sm-12"><label for="disabledInput" class="col-sm-12 control-label"></label></div>
					
					<!-- 등록/취소 버튼 추가 -->
					<div class="col-sm-12 text-center" style="margin-top:16px; text-align: center;">
						<button id="saveBtn" type="button" class="btn btn-primary">수정</button>
						<button id="cancelBtn" type="button" class="btn btn-warning" onclick="history.back()">취소</button>
					</div>
			 </div>
		</div>
	</div>
</div>

<!-- 비용 END -->