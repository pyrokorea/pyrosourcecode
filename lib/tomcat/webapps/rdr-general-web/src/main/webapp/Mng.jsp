<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page session="false"%>
<html>
<head>
<title>RDR 지식관리</title>
<script type="text/javascript" src="resources/js/jquery-1.12.0.min.js"></script>
<script type="text/javascript" src="resources/js/bootstrap.min.js"></script>
<script type="text/javascript" src="resources/js/mng.js"></script>
<script type="text/javascript" src="resources/lib/jstree/jstree.min.js"></script>
<link href="resources/css/bootstrap.min.css" rel="stylesheet">
<link href="resources/css/common.css" rel="stylesheet">
<style>
	/* div[class^=col]{
	    padding: 30px;
    	border: solid 1px;
    	border-color: gray;
	} */
	
    ::-webkit-scrollbar {
    	width: 2em;
    	height: 2em
	}
	::-webkit-scrollbar-button {
	    background: #ccc
	}
	::-webkit-scrollbar-track-piece {
	    background: #ccc
	}
	::-webkit-scrollbar-thumb {
	    background: black;
	}
    .table thead th {
    	text-align: center;
    	vertical-align: middle;
    	border-bottom: 0px;
    	table-layout: fixed;
    	white-space: nowrap;
    }
    .tb th {
    	white-space: nowrap;
    }
    .tb tr>td {
    	white-space: nowrap;
    }
    .btn-success{
    	margin-top: -7px;
    }
    .row{
    	padding: 10px;
    	margin : 0px;
    }

</style>
</head>
<body>
	<input type="hidden" id="kaMode" value=<%= request.getParameter("kaMode") %>>
	<input type="hidden" id="domain" value=<%= request.getParameter("domain") %>>
	<input type="hidden" id="lmbCode" value=<%= request.getParameter("lmbCode") %>>
	<input type="hidden" id="receiptDate" value=<%= request.getParameter("receiptDate") %>>
	<input type="hidden" id="receiptNo" value=<%= request.getParameter("receiptNo") %>>
	<input type="hidden" id="testCode" value=<%= request.getParameter("testCode") %>>
	<input type="hidden" id="specimenCode" value=<%= request.getParameter("specimenCode") %>>
	<input type="hidden" id="decisionSeq" value=<%= request.getParameter("decisionSeq") %>>
	<input type="hidden" id="userId" value=<%= request.getParameter("userId") %>>
	<input type="hidden" id="conclusionId" value=<%= request.getParameter("conclusionId") %>>
	<input type="hidden" id="timestamp" value=<%= request.getParameter("timestamp") %>>
	<div id="wrap">
		<!-- header -->
		<header id="hd" class="clearfix">
			<h1 class="logo text-hide"><a href="/"><img src="resources/images/logo.png" alt="SEEGENEMEDICAL"></a>재단법인 씨젠의료재단</h1>
			<ul class="gnb">
			</ul>
			<div class="util">
				<span class="user"><img src="resources/images/award.png" alt=""> AI연구팀 홍길동</span>
				<a href="#" class="btn btn-info">로그아웃</a>
			</div>
		</header>
		<div id="container">
			<div style="width:66.6%; height:60px; float: left">
				<div class="top-area">
					<form class="frm-top-srch">
						<strong class="tit">지식 관리</strong>
						<div class="form-inline">
							<span class="control-label">Mode : </span>
							<select class="form-control" title="모드 선택" style="width:130px" id="selectKaMode" disabled="disabled">
								<option value="add">추가</option>
								<option value="edit">수정</option>
								<option value="delete">삭제</option>
							</select>
							<span class="control-label">현재 결론</span>
							<input readonly="readonly" type="text" class="form-control" title="결론" style="width:350px" id="thisConclusion"> 
							<button type="button" id="addRuleBatch" onclick="btnAddRuleBatch()" class="btn btn-primary" style="float:right; width:100px;margin-left: 50px;">저장</button>
							<button type="button" onclick="resultSearch()" class="btn btn-primary" style="float:right; width:180px;margin-left: 50px;">추론결과조회화면</button>
						</div>
					</form>
				</div>
			</div>
			<div style="width:32.3%; height:70%; float: right; margin-left: 15px">
				<div class="panel">
					<div class="panel-hd">
						<h2 class="panel-title"><i class="icon-list"></i>사례</h2>
						<!-- <button type="button" class="btn btn-primary" style="float:right;margin-top: 10px;margin-right: 20px;" onclick="inference()">추론</button> -->
						<label style="float: right; margin-top: 12px; margin-right: 20px; color: white;">
						<input type="checkbox" id="diff" style="" onchange="showDiff()">
						불일치 항목만 보이기
						</label>
					</div>
					<div class="tb-scroll-wrp">
						<div class="tb-scroll" style="height: 92%">
							<table class="table table-sm tb" id="tableCase">
								<thead>
									<tr>
										<th scope="col">사례항목명</th>
										<th scope="col">Type</th>
										<th scope="col">기준사례</th>
										<th scope="col">충돌사례</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td>정상</td>
										<td>정상</td>
										<td>정상</td>
										<td>정상</td>
									</tr>
								</tbody>
							</table>
						</div>
					</div>
				</div>
			</div>
			
			<div style="width:20%; height:calc(70% - 60px); float: left">
				<div class="panel" style="height:50%; margin-right: 15px; margin-bottom: 15px;">
					<div class="panel-hd">
						<!-- <h2 class="panel-title"><i class="icon-list"></i>결론 선택</h2> -->
						<form class="frm-top-srch">
							<strong class="tit">결론 선택</strong>
							<div class="form-inline">
								<span class="control-label">검색</span>
								<input hidden="hidden" />
								<input type="text" class="form-control" style="width:130px" id="searchText" onkeyup="searchTextKeyDown()">
							</div>
						</form>
						<!-- <button type="submit" class="btn btn-primary" style="float:right; width:100px;margin-right: 15px;margin-top: 8px;">결론추가</button> -->
					</div>
					<div class="tb-scroll-wrp">
						<div class="tb-scroll" style="height: calc(100% - 50px);">
							<table class="table table-sm tb" style="width: 100%;" id="tableConclusion">
								<thead>
									<tr>
										<th scope="col"> </th>
										<th scope="col">결론</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td></td>
										<td></td>
									</tr>
								</tbody>
							</table>
						</div>
					</div>
				</div>
				<div class="panel-txt" style="width:95%; height:47%;background-color: white; border-radius: 15px; margin-right: 15px; margin-bottom: 15px" id="conclusionTXT"></div>
			</div>
			
			<!-- <div style="width: 33.3%; float:left; height: 30%">
				<div class="panel-txt" style="height:100%;background-color: white;" id="conclusionTXT">
				</div>
			</div> -->
			
			<div style="width:46.6%; height:calc(70% - 60px); float: left">
				<div class="panel" style="height:50%">
					<div class="panel-hd">
						<h2 class="panel-title"><i class="icon-list"></i>사용된 조건</h2>
					</div>
					<div class="tb-scroll-wrp" style="height: 80%">
						<div class="tb-scroll">
							<table class="table table-sm tb" id="tableCondition">
								<thead>
									<tr>
										<th scope="col">항목</th>
										<th scope="col">연산자</th>
										<th scope="col">Value</th>
									</tr>
								</thead>
								<tbody>
								</tbody>
							</table>
						</div>
					</div>
				</div>
				
				<div class="panel" style="height:47%; margin-top: 15px;">
					<div class="panel-hd">
						<h2 class="panel-title"><i class="icon-list"></i>추가할 조건</h2>
						<button type="button" onclick="getValidationCasesBatch()" class="btn btn-primary" style="float:right; width:100px;margin-right: 15px;margin-top: 8px;">지식 검증</button>
						<button type="button" onclick="getSuggestedConditions(true)" class="btn btn-primary" style="float:right; width:150px;margin-right: 15px;margin-top: 8px;">조건 자동 생성</button>
					</div>
					<div class="tb-scroll-wrp">
						<div class="tb-scroll" style="height: 84%">
							<table class="table table-sm tb" id="tableConditionSet">
								<colgroup>
									<col width="30%">
									<col width="30%">
									<col width="25%">
									<col width="15%">
								</colgroup>
								
								<thead>
									<tr>
										<th scope="col">항목</th>
										<th scope="col">연산자</th>
										<th scope="col">Value</th>
										<th scope="col">삭제</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td></td>
										<td></td>
										<td></td>
										<td></td>
									</tr>
								</tbody>
							</table>
						</div>
					</div>
				</div>
			</div>
			
			<div style="width: 50%; float:left; height: 30%">
				<div class="panel" style="height:95%; margin-top: 15px; margin-right: 15px;">
					<div class="panel-hd">
						<h2 class="panel-title"><i class="icon-list"></i>충돌사례</h2>
						<label style="float: right; margin-top: 12px; margin-right: 20px; color: white;">
							<input type="checkbox" id="nullColhide" style="" checked="checked" onchange="nullColhideChange()">
							값 없는 항목 숨기기
						</label>
					</div>
					<div class="tb-scroll-wrp">
						<div class="tb-scroll" style="height: 80%">
							<table class="table table-sm tb" id="tableCornerCase">
								<thead>
									<tr>
										<th scope="col">id</th>
										<th scope="col">albumin</th>
										<th scope="col">alpha1</th>
										<th scope="col">alpha2</th>
										<th scope="col">beta</th>
										<th scope="col">gamma</th>
										<th scope="col">total protein</th>
									</tr>
								</thead>
								<tbody>
								</tbody>
							</table>
						</div>
					</div>
				</div>
			</div>
			<div style="width: 20%; float:left; height: 28%">
				<div class="panel" style="height:100%;margin-top: 15px;margin-right: 15px;">
					<div class="panel-hd">
						<h2 class="panel-title"><i class="icon-list"></i>결론</h2>
					</div>
					<div class="tb-scroll-wrp">
						<div class="tb-scroll">
							<table class="table table-sm tb" id="tableCornerConclusion">
								<thead>
									<tr>
										<th scope="col">선택</th>
										<th scope="col">충돌사례 결론</th>
										<th scope="col">상태</th>
									</tr>
								</thead>
								<tbody>
								</tbody>
							</table>
						</div>
					</div>
				</div>
			</div>
			<div style="width: 30%; float:left; height: 28%;margin-top: 15px;">
				<div class="panel-txt" style="height:100%;background-color: white; border-radius: 15px;" id="cornerConclusionTXT">
				</div>
			</div>
		</div>
	</div>
</body>
</html>

