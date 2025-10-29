<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="ui" uri="http://egovframework.gov/ctl/ui"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<script src="https://code.jquery.com/jquery-1.12.4.min.js"></script>

<!-- 이미지 경로 미리 변수로 선언 -->
<c:url var="imgPrev10" value="/images/egovframework/cmmn/btn_page_pre10.gif"/>
<c:url var="imgPrev1"  value="/images/egovframework/cmmn/btn_page_pre1.gif"/>
<c:url var="imgNext1"  value="/images/egovframework/cmmn/btn_page_next1.gif"/>
<c:url var="imgNext10" value="/images/egovframework/cmmn/btn_page_next10.gif"/>

<script type="text/javascript">
	
  	// 유틸 함수 추가
	function d(v){ 
  		return (v === undefined || v === null) ? '' : v; 
  		}
	
	function formatDate(dateStr) {
	  if (!dateStr) return '';
	  let parts = String(dateStr).split('-');
	  if (parts.length !== 3) return dateStr;
	  return parts[0] + '년 ' + parts[1] + '월 ' + parts[2] + '일';
	}
	
	// 페이지 기본값  설정
	let pageUnit = 10; // 한 페이지 당 데이터 행 수
	let pageSize = 10; // 페이지 번호의 개수
	let currentPage = 1; // 현재 사용자가 보고 있는 페이지 번호
	let totalCount = 0; // 전체 데이터 건수
	
	// 목록 불러오기 (데이터 로드)
	function loadList(pageIndex) {
	  if (!pageIndex) pageIndex = 1; // 기본값으로 1페이지를 설정
	  currentPage = pageIndex;
	
	  $.ajax({
	    url: '/account/accountListData.do',
	    type: 'GET',
	    dataType: 'json',
	    data: {
	      pageIndex: pageIndex,
	      recordCountPerPage: pageUnit,
	      pageSize: pageSize
	    },
	    success: function (data) {
	      let list = (data && data.resultList) ? data.resultList : [];
	      totalCount = (data && data.totalCount) ? data.totalCount : 0;
	      renderRows(list); // 받아온 list 데이터를 테이블 <tbody> 영역에 표시.
	      renderPager(); // totalCount 값을 기반으로 페이지 번호 버튼을 새로 만듦
	    },
	    error: function () {
	      alert('목록을 불러오지 못했습니다.');
	    }
	  });
	}
	
	// 테이블 렌더링 (문자열 더하기)
	function renderRows(list) {
	  let tbody = document.getElementById('listBody');
	  if (!tbody) return;
	
	  if (!list || list.length === 0) { // 서버에서 받아온 목록(list)이 비어 있을 때
	    tbody.innerHTML = '<tr><td colspan="8" class="text-center">등록된 데이터가 없습니다.</td></tr>';
	    return;
	  }
	
	  let html = ''; // 빈 문자열 준비
	  for (let i = 0; i < list.length; i++) { // 서버에서 전달된 list 배열의 길이만큼 반복해서 한 행씩 만듦
	    let row = list[i]; // i번째 데이터를 row 변수에 담음
	    html += '<tr align="center">'
	         + '<td>' + d(row.profitCostNm) + '</td>'
	         + '<td>' + d(row.bigGroupNm) + '</td>'
	         + '<td>' + d(row.middleGroupNm) + '</td>'
	         + '<td>' + d(row.smallGroupNm) + '</td>'
	         + '<td>' + d(row.detailGroupNm) + '</td>'
	         + '<td>' + d(row.transactionMoney) + '</td>'
	         + '<td>' + formatDate(row.transactionDate) + '</td>'
	         + '<td>' + d(row.writerNm) + '</td>'
	         + '</tr>';
	  }
	  
	  tbody.innerHTML = html; // 화면 렌더링
	}
	
	// 페이징 버튼 렌더링
	function renderPager() {
	  let pager = document.getElementById('pager');
	  if (!pager) return;
	
	  let totalPages = Math.ceil(totalCount / pageUnit); // 페이지 버튼 계산
	  if (totalPages <= 1) {
	    pager.innerHTML = '';
	    return;
	  }
	
	  let block = Math.ceil(currentPage / pageSize); // 현재 페이지(currentPage)가 몇 번째 블록(페이지 묶음)에 속하는지를 계산
	  let start = (block - 1) * pageSize + 1; // 현재 블록의 첫 번째 페이지 번호를 계산
	  let end = Math.min(block * pageSize, totalPages); // 현재 블록의 마지막 페이지 번호를 계산
	
	  let html = ''; // 페이지 버튼들을 넣을 “빈 문자열 그릇”을 준비
	
	  // 이전 블록 (<<)
	  if (start > 1) {
	    html += '<a href="#" data-page="' + (start - 1) + '">'
	         +  '<img src="${imgPrev10}" alt="이전 10페이지">'
	         +  '</a> ';
	  }
	
	  // 이전 페이지 (<)
	  if (currentPage > 1) {
	    html += '<a href="#" data-page="' + (currentPage - 1) + '">'
	         +  '<img src="${imgPrev1}" alt="이전 페이지">'
	         +  '</a> ';
	  }
	
	  // 숫자 버튼
	  for (let p = start; p <= end; p++) {
	    if (p === currentPage) {
	      html += '<span style="font-weight:bold; color:#337ab7;">' + p + '</span> ';
	    } else {
	      html += '<a href="#" data-page="' + p + '">' + p + '</a> ';
	    }
	  }
	
	  // 다음 페이지 (>)
	  if (currentPage < totalPages) {
	    html += '<a href="#" data-page="' + (currentPage + 1) + '">'
	         +  '<img src="${imgNext1}" alt="다음 페이지">'
	         +  '</a> ';
	  }
	
	  // 다음 블록 (>>)
	  if (end < totalPages) {
	    html += '<a href="#" data-page="' + (end + 1) + '">'
	         +  '<img src="${imgNext10}" alt="다음 10페이지">'
	         +  '</a>';
	  }
	
	  pager.innerHTML = html; // 화면에 렌더링
	}
	
	// 엑셀 다운로드 함수
	function downloadExcel() {
	  let params = $.param({
	    pageIndex: currentPage,
	    recordCountPerPage: pageUnit,
	    pageSize: pageSize
	  });
	  location.href = '/account/accountListExcel.do?' + params;
	}
	
	// 초기화, 이벤트 바인딩
	$(function () {
	  // 초기 목록
	  loadList(1);
	
	  // 등록 버튼
	  $("#insertBtn").on("click", function () {
	    location.href = "/account/accountInsert.do";
	  });
	
	  // 엑셀 다운로드 버튼
	  $("#excelBtn").on("click", function () {
	    downloadExcel();
	  });
	
	  // 동적 페이징 링크 클릭 (이벤트 위임)
	  $(document).on("click", "#pager a[data-page]", function (e) {
	    e.preventDefault();
	    let p = Number($(this).data("page"));
	    if (!isNaN(p)) loadList(p);
	  });
	});
</script>


<form name="sendForm" id="sendForm" method="post" onsubmit="return false;">

<input type="hidden" id="situSeq" name="situSeq" value="">
<input type="hidden" id="mode" name="mode" value="Cre">

<div id="wrap"  class="col-md-offset-1 col-sm-10" >
		<div align="center"><h2>회계정보리스트</h2></div>
		<div class="form_box2 col-md-offset-7" align="right" >
			<div class="right" >
				<button type="button" id="insertBtn" class="btn btn-primary">등록</button>
				<button type="button" id="excelBtn" class="btn btn-primary">엑셀 다운</button>
			</div>
		</div>
	    <br/>
		<table class="table table-hover">
			    <thead>
			      <tr align="center">
			        <th style="text-align: center;" >수익/비용</th>
			        <th style="text-align: center;" >관</th>
			        <th style="text-align: center;" >항</th>
			        <th style="text-align: center;" >목</th>
			        <th style="text-align: center;" >과</th>
			        <th style="text-align: center;" >금액</th>
			        <th style="text-align: center;" >등록일</th>
			        <th style="text-align: center;" >작성자</th>
			      </tr>
			    </thead>
			    <tbody id="listBody"> <!-- id 추가함 --> </tbody>
			</table>
			
			<div id="pager" class="text-center" style="margin-top:10px;"></div> <!-- 페이저 박스 추가 -->

</div>
</form>


