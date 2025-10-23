<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="ui" uri="http://egovframework.gov/ctl/ui"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!-- 

@Class Name  : userInsert.jsp
@Description : 회원가입 페이지 (ID 중복체크 및 회원가입 처리)
@Modification Information

      수정일                   수정자        수정내용
   2025.10.16    노유경        AJAX 기반 ID 중복체크 및 회원가입 처리 추가
   2025.10.16    노유경        비밀번호 유료성 검사 함수 추가
   2025.10.16    노유경        버튼 이벤트 및 취소 버튼 이동 기능 구현
   
-->

<script type="text/javascript">
	$(document).ready(function() {

		// 1. 유효성 검사 관련 함수
		// 비밀번호 정책: 6~12자 & 영문/숫자/특수문자 포함
		const reg = /^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[#?!@$%^&*-]).{6,12}$/;

		function validatePassword(pwd) {
			console.log("비밀번호 입력 길이:", pwd.length);
			if (!reg.test(pwd)) {
				alert("비밀번호는 6~12자이며, 영문, 숫자, 특수문자를 모두 포함해야 합니다.");
				return false;
			}
			return true;
		}

		// 2. 이벤트 등록
		// 2-1) ID 중복 체크
		$("#idcked").on("click", function() {
			const userId = $("#userId").val().trim();
			console.log("중복체크 클릭 - userId:", userId);
			
			if (!userId || userId.length < 6) {
			      alert("ID는 6자 이상 입력하세요.");
			      return;
			    }

			$.get("<c:url value='/user/checkId.do'/>", {
				userId : userId
			}).done(function(res) {
				console.log("중복체크 결과", res);
				alert(res.message);
			}).fail(function() {
				alert("중복 체크 중 오류가 발생했습니다.");
			});
		});

		// 2-2) 저장(회원가입)
		$("#saveBtn").on("click", function() {
			const userId = $("#userId").val().trim();
			const pwd = $("#pwd").val();
			const pwdck = $("#pwdck").val();
			const userName = $("#userName").val().trim();
			
			console.log("회원가입 클릭", { userId:userId, userName:userName });

			// 기본 검증
			if (!userId || userId.length < 6) {
				alert("ID는 6자 이상 입력하세요.");
				return;
			}
			if (!validatePassword(pwd)) {
				return;
			}
			if (pwd !== pwdck) {
				alert("비밀번호가 일치하지 않습니다.");
				return;
			}
			if (!userName) {
				alert("이름을 입력하세요.");
				return;
			}

			// AJAX로 컨트롤러에 JSON 전송
			$.ajax({
				url : "<c:url value='/user/insertUser.do'/>",
				method : "POST",
				contentType : "application/json; charset=UTF-8",
				data : JSON.stringify({
					userId : userId,
					pwd : pwd,
					userName : userName
				})
			}).done(function(res) {
				console.log("회원가입 성공", res);
				alert(res.message);
				if (res.success) {
					window.location.href = "<c:url value='/login/login.do'/>";
				}
			}).fail(function() {
				alert("저장 중 오류가 발생했습니다.");
			});
		});
		// 2-3) 취소 버튼: 로그인 화면으로 이동
		$("#cancelBtn").on("click", function() {
			console.log("취소버튼 클릭 - 로그인 화면으로 이동");
			window.location.href = "<c:url value='/login/login.do'/>";
		});

	});
</script>


<div class="container" style="margin-top: 50px">
	<form class="form-horizontal" id="sendForm">
		<div class="form-group">
			<label class="col-sm-2 control-label">ID</label>
			<div class="col-sm-4">
				<input class="form-control" id="userId" name="userId" type="text"
					value="" title="ID">
			</div>

			<div class="container">
				<button type="button" id="idcked" class="btn btn-default"
					style="display: block;">ID 중복 체크</button>
			</div>

		</div>

		<div class="form-group">
			<label for="disabledInput " class="col-sm-2 control-label">패스워드</label>
			<div class="col-sm-4">
				<input class="form-control" id="pwd" name="pwd" type="password"
					title="패스워드">
			</div>
			<label for="disabledInput " class="col-sm-2 control-label">패스워드
				확인</label>
			<div class="col-sm-4">
				<input class="form-control" id="pwdck" name="pwdck" type="password"
					title="패스워드 확인">
			</div>
		</div>

		<div class="form-group">
			<label for="disabledInput" class="col-sm-2 control-label">이름</label>
			<div class="col-sm-4">
				<input class="form-control" id="userName" name="userName"
					type="text" value="" title="이름">
			</div>
		</div>


		<div class="col-md-offset-4">
			<button type="button" id="saveBtn" class="btn btn-primary">저장</button>
			<button type="button" id="cancelBtn" class="btn btn-danger">취소</button>
		</div>
	</form>
</div>


