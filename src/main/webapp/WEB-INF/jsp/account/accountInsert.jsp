<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="ui" uri="http://egovframework.gov/ctl/ui"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<script>
$(document).ready(function(){
	
	// 공통: 하위 코드 로딩
	  function loadCodes(parentCode, $target){
	    if(!parentCode){ $target.html('<option value="">선택</option>'); return; }
	    $.ajax({
	      url: "<c:url value='/account/selectCombo.do'/>",
	      type: "GET",
	      dataType: "json",
	      data: { category: parentCode },   // 컨트롤러에서 CommUtils로 받는 키: category
	      success: function(res){
	        // res.list 형태로 온다는 가정 (만약 res 자체가 리스트면 아래 한 줄 수정)
	        var list = res.list || res;
	        $target.empty().append('<option value="">선택</option>');
	        $.each(list, function(_, row){
	          // EgovMap은 보통 대문자 키(CODE, COM_KOR)
	          var code = row.CODE || row.code;
	          var name = row.COM_KOR || row.comKor;
	          $target.append('<option value="'+ code +'">'+ name +'</option>');
	        });
	      },
	      error: function(){ alert("코드 조회 중 오류가 발생했습니다."); }
	    });
	  }

	  // 1단계: profitCost → bigGroup
	  $('#profitCost').on('change', function(){
	    var v = $(this).val();
	    $('#bigGroup').html('<option value="">선택</option>');
	    $('#middleGroup').html('<option value="0">해당없음</option>');
	    $('#smallGroup').html('<option value="0">해당없음</option>');
	    $('#detailGroup').html('<option value="0">해당없음</option>');
	    if(v) loadCodes(v, $('#bigGroup'));
	  });

	  // 2단계: bigGroup → middleGroup
	  $('#bigGroup').on('change', function(){
	    var v = $(this).val();
	    $('#middleGroup').html('<option value="">선택</option>');
	    $('#smallGroup').html('<option value="0">해당없음</option>');
	    $('#detailGroup').html('<option value="0">해당없음</option>');
	    if(v) loadCodes(v, $('#middleGroup'));
	  });

	  // 3단계: middleGroup → smallGroup
	  $('#middleGroup').on('change', function(){
	    var v = $(this).val();
	    $('#smallGroup').html('<option value="">선택</option>');
	    $('#detailGroup').html('<option value="0">해당없음</option>');
	    if(v) loadCodes(v, $('#smallGroup'));
	  });

	  // 4단계: smallGroup → detailGroup
	  $('#smallGroup').on('change', function(){
	    var v = $(this).val();
	    $('#detailGroup').html('<option value="">선택</option>');
	    if(v) loadCodes(v, $('#detailGroup'));
	  });

	  // 유효성 검사
	  function validate(){
	    if(!$('#profitCost').val()) { alert('대분류를 선택하세요.'); return false; }
	    if(!$('input[name=transactionMoney]').val() || !/^\d+$/.test($('input[name=transactionMoney]').val())){
	      alert('금액은 숫자만 입력하세요.'); return false;
	    }
	    if(!$('input[name=transactionDate]').val()){ alert('거래일자를 입력하세요.'); return false; }
	    return true;
	  }

	  // 저장
	  $('#saveBtn').on('click', function(){
	    if(!validate()) return;

	    const payload = {
	      profitCost:   $('#profitCost').val(),
	      bigGroup:     $('#bigGroup').val(),
	      middleGroup:  $('#middleGroup').val(),
	      smallGroup:   $('#smallGroup').val(),
	      detailGroup:  $('#detailGroup').val(),
	      comments:     $('input[name=comment]').val(),
	      transactionMoney: $('input[name=transactionMoney]').val(),
	      transactionDate:  $('input[name=transactionDate]').val(),
	      // 서버에서 세션으로 채우면 제외 가능
	      writer: '${LOGIN_USER.userId}'.trim() || 'admin'
	    };

	    $.ajax({
	      url: "<c:url value='/account/accountInsertProc.do'/>",
	      method: "POST",
	      contentType: "application/json; charset=UTF-8",
	      dataType: "json",
	      data: JSON.stringify(payload)
	    }).done(function(res){
	      if(res && res.success){
	        alert(res.message || '등록이 완료되었습니다.');
	        location.href = "<c:url value='/account/accountList.do'/>";
	      }else{
	        alert((res && res.message) || '등록에 실패했습니다.');
	      }
	    }).fail(function(){
	      alert('서버 오류가 발생했습니다.');
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
						<select class="form-control" id="bigGroup"  name="bigGroup" title="관">
				        	<option value="">선택</option>
				        </select>
				      </div>

				      <div class="col-sm-3">
						<select class="form-control" id="middleGroup" name="middleGroup"  title="항">
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
				      		<input class="form-control" id="comment" name="comment" type="text" value="" placeholder="비용 상세 입력" title="비용 상세">
				      </div>
			 		</div>

				<div class="col-sm-12">  <label for="disabledInput" class="col-sm-12 control-label"> </label></div>
			 		<div class="col-sm-12">
			 		  <label for="disabledInput" class="col-sm-1 control-label"><font size="1px">금액</font></label>
				      <div class="col-sm-3">
				        	<input class="form-control"  name="transactionMoney" type="text" value="" title="금액">
				      </div>
			 		  <label for="disabledInput" class="col-sm-1 control-label"><font size="1px">거래일자</font></label>
				      <div class="col-sm-3">
				       	 <input class="form-contro col-sm-2"  name="transactionDate" type="text" value="" style="width: 80%" title="거래일자">
				      </div>
			 		</div>

					<div class="col-sm-12"><label for="disabledInput" class="col-sm-12 control-label"></label></div>
					<div class="col-sm-12"><label for="disabledInput" class="col-sm-12 control-label"></label></div>
					
					<!-- 등록/취소 버튼 추가 -->
					<div class="col-sm-12 text-center" style="margin-top:16px; text-align: center;">
						<button id="saveBtn" type="button" class="btn btn-primary">등록</button>
						<button id="cancleBtn" type="button" class="btn btn-warning" onclick="history.back()">취소</button>
					</div>
			 </div>
		</div>
	</div>
</div>

<!-- 비용 END -->