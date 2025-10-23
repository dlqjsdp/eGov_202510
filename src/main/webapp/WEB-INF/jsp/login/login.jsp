<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- 

@Class Name  : login.jsp
@Description : 로그인 화면 (AJAX 기반 로그인 처리)

@Modification Information
     수정일                  수정자          수정내용
  2025.10.17    노유경          AJAX 기반 로그인 처리 및 유효성 검사 추가
  2025.10.17    노유경          로그인 실패 및 서버 오류 처리 로직 추가
  2025.10.17    노유경          window.location.href를 통한 성공 시 페이지 이동 적용
  2025.10.17    노유경          $(document).on() 사용하여 동적 바인딩 안정화
  2025.10.17    노유경          로그인 폼 입력값 유효성 검증 및 콘솔 디버깅 로그 추가

-->

<script type="text/javascript">
	// DOM 준비 후 실행
	$(function () {
	  // 동적 변경에도 안전한 위임 바인딩
	  $(document).on('click', '#loginBtn', function (e) {
	    e.preventDefault(); // <button>의 기본 form 제출 동작을 막고, AJAX로만 전송
	
	    // 입력값 가져오기
	    const memId = $.trim($('#memId').val());
	    const memPassword = $('#memPassword').val();
	
	    // 유효성 검사
	    if (!memId || !memPassword) {
	      alert('아이디와 비밀번호를 입력해주세요.');
	      return;
	    }
	
	    // AJAX 요청 (로그인 처리)
	    $.ajax({
	      url: "<c:url value='/login/loginProc.do'/>",
	      method: 'POST',
	      contentType: 'application/json; charset=UTF-8',
	      dataType: 'json',
	      data: JSON.stringify({ userId: memId, pwd: memPassword })
	    })
	    .done(function (res) { // 요청 성공시 처리
	    	if(res.success) {
		      console.log('로그인 성공', res);
		      alert(res.message || '로그인 성공');
		      window.location.href = res.redirect || "<c:url value='/account/accountList.do'/>";
		    } else {
		        console.warn('로그인 실패', res);
		        alert(res.message || '아이디 또는 비밀번호가 올바르지 않습니다.');
		      }
		    })
	    .fail(function (xhr, status, err) {
	      console.error('로그인 오류:', status, err, xhr.responseText);
	      alert('서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
	    });
	  });
	});

</script>


<form id="sendForm">

	<input type="hidden" id="platform" name="platform" value="">
	<div class="container col-md-offset-2 col-sm-6" style="margin-top: 100px;">
			<div class="input-group">
				<span class="input-group-addon"><i class="glyphicon glyphicon-user"></i></span>
				<input id="memId" type="text" class="form-control valiChk" name="memId" placeholder="id" title="ID">
			</div>
			<div class="input-group">
				<span class="input-group-addon"><i class="glyphicon glyphicon-lock"></i></span>
				<input id="memPassword" type="password" class="form-control valiChk" name="memPassword" placeholder="Password" title="Password">
			</div>
			<br />
		<br>
		<div class="col-md-offset-4">
			<button type="button" id="loginBtn" class="btn btn-primary">로그인</button>
			<button type="button" id="#" class="btn btn-warning" onclick="location.href='/login/login.do'">취소</button>
			<button type="button" id="#" class="btn btn-info" onclick="location.href='/user/userInsert.do'">회원가입</button>
		</div>
	</div>
</form>

