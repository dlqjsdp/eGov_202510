<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="ui" uri="http://egovframework.gov/ctl/ui"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<script src="https://code.jquery.com/jquery-1.12.4.min.js"></script>

<script type="text/javascript">
	
	// 상단 유틸 함수 추가
	function d(v){ return (v === undefined || v === null) ? '' : v; }

	function formatDate(dateStr) {
	  if (!dateStr) return ''; // null, undefined, 빈값이면 빈칸
	  var parts = dateStr.split('-');
	  if (parts.length !== 3) return dateStr; // 포맷이 다르면 그대로 반환
	  return parts[0] + '년 ' + parts[1] + '월 ' + parts[2] + '일';
	}
	
	// 페이지 기본값 (var 사용)
	var pageUnit = 10; // 한 페이지 당 행 수
	var pageSize = 10; // 페이지 블록 크기
	var currentPage = 1;
	var totalCount = 0;
	
	// 목록 불러오기 (jQuery.ajax 사용)
	function loadList(pageIndex) {
	  if (!pageIndex) pageIndex = 1;
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
	      var list = data && data.resultList ? data.resultList : [];
	      totalCount = data && data.totalCount ? data.totalCount : 0;
	      renderRows(list);
	      renderPager();
	    },
	    error: function () {
	      alert('목록을 불러오지 못했습니다.');
	    }
	  });
	}
	
	// 테이블 렌더링 (문자열 더하기)
	function renderRows(list) {
	  var tbody = document.getElementById('listBody');
	  if (!tbody) return;
	
	  if (!list || list.length === 0) {
	    tbody.innerHTML = '<tr><td colspan="8" class="text-center">등록된 데이터가 없습니다.</td></tr>';
	    return;
	  }
	
	  var html = '';
	  for (var i = 0; i < list.length; i++) {
	    var row = list[i];
	
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
	  tbody.innerHTML = html;
	}
	
	// 페이징 버튼 렌더링
	function renderPager() {
	  var pager = document.getElementById('pager');
	  if (!pager) return;
	  var totalPages = Math.ceil(totalCount / pageUnit);
	
	  if (totalPages <= 1) {
	    pager.innerHTML = '';
	    return;
	  }
	
	  var block = Math.ceil(currentPage / pageSize);
	  var start = (block - 1) * pageSize + 1;
	  var end = Math.min(block * pageSize, totalPages);
	
	  var html = '';
	  if (start > 1) {
	    html += '<button class="btn btn-default btn-sm" onclick="loadList(' + (start - 1) + ')">&laquo;</button> ';
	  }
	  for (var p = start; p <= end; p++) {
	    if (p === currentPage) {
	      html += '<button class="btn btn-primary btn-sm" disabled>' + p + '</button> ';
	    } else {
	      html += '<button class="btn btn-default btn-sm" onclick="loadList(' + p + ')">' + p + '</button> ';
	    }
	  }
	  if (end < totalPages) {
	    html += '<button class="btn btn-default btn-sm" onclick="loadList(' + (end + 1) + ')">&raquo;</button>';
	  }
	  pager.innerHTML = html;
	}
	
	// 엑셀 다운로드 함수 
	function downloadExcel() {
	  var params = $.param({
	    pageIndex: currentPage,
	    recordCountPerPage: pageUnit,
	    pageSize: pageSize
	  });
	  window.location.href = '/account/accountListExcel.do?' + params;
	}
	
	// 초기 로딩 (jQuery ready)
	$(function () {
	  loadList(1);
	});
</script>


<form name="sendForm" id="sendForm" method="post" onsubmit="return false;">

<input type="hidden" id="situSeq" name="situSeq" value="">
<input type="hidden" id="mode" name="mode" value="Cre">

<div id="wrap"  class="col-md-offset-1 col-sm-10" >
		<div align="center"><h2>회계정보리스트</h2></div>
		<div class="form_box2 col-md-offset-7" align="right" >
			<div class="right" >
				<button type="button" class="btn btn-primary" onclick="location.href='/account/accountInsert.do'" >등록</button>
				<button type="button" class="btn btn-primary" onclick="downloadExcel()">엑셀 다운</button>
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


