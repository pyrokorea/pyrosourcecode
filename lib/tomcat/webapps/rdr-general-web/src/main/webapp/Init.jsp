<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page session="false"%>
<html>
<head>
<title>RDR 지식관리</title>
<script type="text/javascript" src="resources/js/jquery-1.12.0.min.js"></script>
<script type="text/javascript" src="resources/js/bootstrap.min.js"></script>
<script type="text/javascript" src="resources/js/init.js"></script>
<link href="resources/css/bootstrap.min.css" rel="stylesheet">
<link href="resources/css/common.css" rel="stylesheet">
<style>
	/* div[class^=col]{
	    padding: 30px;
    	border: solid 1px;
    	border-color: gray;
	} */
     .table thead th {
    	text-align: center;
    	vertical-align: middle;
    	border-bottom: 0px;
    }
    .tableCaseCol{
    	width: 110px;
    }
    .table tbody td {
    	text-align: center;
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
			
			<div style="width:48%; height:100%; float: left; margin-right: 15px">
				<div class="panel" style="height:200px; margin-bottom: 15px;">
					<div class="panel-hd">
						<h2 class="panel-title"><i class="icon-list"></i>도메인 추가</h2>
						<!-- <button type="button" onclick="LoadDomain()" class="btn btn-primary" style="float:right; width:100px;margin-right: 15px;margin-top: 8px;">취소</button> -->
						<button type="button" onclick="addDomain()" class="btn btn-primary" style="float:right; width:100px;margin-right: 15px;margin-top: 8px;">추가</button>
					</div>
					<div class="tb-scroll-wrp">
						<hr>
						<div class="form-inline">
							<span class="control-label" style="margin-left: 20px;">Domain 명 : </span>
							<input type="text" class="form-control" title="결론" style="width:350px" id="domainName">
							<span class="control-label" style="margin-left: 20px;" >Reasoner : </span>
							<select class="form-control" title="모드 선택" style="width:130px" id="domainType">
								<option value="SCRDR">SCRDR</option>
								<option value="MCRDR">MCRDR</option>
							</select>
						</div>
						
						<hr>
						
						<div class="form-inline">
							<span class="control-label" style="margin-left: 20px;">Domain 설명 : </span>
							<input type="text" class="form-control" title="결론" style="width:600px" id="domainDesc">
						</div>
					</div>
				</div>
				<div class="panel"  style="height:calc(100% - 215px)">
					<div class="panel-hd">
						<h2 class="panel-title"><i class="icon-list"></i>도메인</h2>
						<button type="button" onclick="DeleteDomain()" class="btn btn-primary" style="float:right; width:100px;margin-right: 15px;margin-top: 8px;">삭제</button>
						<button type="button" onclick="LoadDomain()" class="btn btn-primary" style="float:right; width:100px;margin-right: 15px;margin-top: 8px;">조회</button>
						<br>
					</div>
					<div class="tb-scroll" style="height: 100%">
						<table class="table table-sm tb" id="domainTable">
							<colgroup>
								<col width="10%">
								<col width="25%">
								<col width="35%">
								<col width="15%">
								<col width="15%">
							</colgroup>
							
							<thead>
								<tr>
									<th scope="col"></th>
									<th scope="col">도메인명</th>
									<th scope="col">도메인 설명</th>
									<th scope="col">Reasoner</th>
									<th scope="col">생성일시</th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<td></td>
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
			
			<div style="width:48%; height:100%; float: right; margin-right: 0px">
				<div class="panel">
					<div class="panel-hd">
						<h2 class="panel-title"><i class="icon-list"></i>사례구조 관리</h2>
						<!-- <button type="button" onclick="struInit()" 
						class="btn btn-primary" style="float:right; width:100px;margin-right: 15px;margin-top: 8px;">취소</button> -->
						<button type="button" onclick="getDelete()" 
						class="btn btn-primary" style="float:right; width:100px;margin-right: 15px;margin-top: 8px;">삭제</button>
						<button type="button" onclick="addCategoricals()" 
						class="btn btn-primary" style="float:right; width:100px;margin-right: 15px;margin-top: 8px;">저장</button>
						<button type="button" onclick="getStru()" 
						class="btn btn-primary" style="float:right; width:100px;margin-right: 15px;margin-top: 8px;">조회</button>
					</div>
					<div class="tb-scroll-wrp" style="height: 60%">
						<div class="tb-scroll" style="height: 100%">
							<table class="table table-sm tb" id="structTable">
								<colgroup>
									<col width="5%">
									<col width="15%">
									<col width="15%">
									<col width="15%">
									<col width="15%">
									<col width="35%">
								</colgroup>
								
								<thead>
									<tr>
										<th scope="col"></th>
										<th scope="col">사례항목명</th>
										<th scope="col">Type</th>
										<th scope="col">사례항목설명</th>
										<th scope="col">CategoricalValue</th>
										<th scope="col">Categorical 추가</th>
									</tr>
								</thead>
								<tbody>
								</tbody>
							</table>
						</div>
					</div>
						
					<div class="panel-hd">
						<h2 class="panel-title"><i class="icon-list"></i>사례항목 추가</h2>
						<button type="button" onclick="addAttribute()" 
						class="btn btn-primary" style="float:right; width:200px;margin-right: 15px;margin-bottom: 8px;margin-top: 8px;">추가</button>
					</div>
					<br>
					<div style="width: 40%; float: left">
						<div class="form-inline">
							<span class="control-label"  style="width:90px;margin-left: 20px;">사례항목명 : </span>
							<input type="text" class="form-control" title="결론" style="width:200px" id="chkAttr">
						</div>
						
						<!-- <div class="form-inline">
							<span class="control-label"  style="width:90px;margin-left: 20px;">변경항목명 : </span>
							<input type="text" class="form-control" title="결론" style="width:200px" id="chkAttr2">
						</div>  -->
						
						<div class="form-inline">
							<span class="control-label" style="width:90px;margin-left: 20px;">Type : </span>
							<select class="form-control" title="모드 선택" style="width:200px" id="chkType">
							</select>
						</div>
						
						<div class="form-inline">
							<span class="control-label" style="width:90px;margin-left: 20px;">사례항목설명 : </span>
							<input type="text" class="form-control" title="결론" style="width:200px" id="chkdesc">
						</div>
					</div>
						
						
					<div style="width: 30%; float: left;">
						<div class="tb-scroll" style="width: 100%; height: 25%; float: left;padding-right: 25px">
							<table class="table table-sm tb" id="tableCate">
								<thead>
									<tr>
										<th scope="col">Categorical Value</th>
										<th scope="col">삭제</th>
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
					<div style="width: 30%; float: left">
						<div class="tb-scroll" style="width: 100%; height: 25%;float: left;padding-right: 25px">
							<table class="table table-sm tb" id="tableCateSub">
								<thead>
									<tr>
										<th scope="col">항목</th>
										<th scope="col">Type</th>
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
			</div>
		</div>
	</div>
</body>
</html>

