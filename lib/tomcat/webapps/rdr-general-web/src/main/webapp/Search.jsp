<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page session="false"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=1900">
<title>RDR 추론결과 조회화면</title>
<script type="text/javascript" src="resources/js/jquery-1.12.0.min.js"></script>
<script type="text/javascript" src="resources/js/bootstrap.min.js"></script>
<script type="text/javascript" src="resources/js/search.js"></script>
<script type="text/javascript" src="resources/lib/jstree/jstree.min.js"></script>
<link href="resources/css/bootstrap.min.css" rel="stylesheet">
<link href="resources/lib/jstree/themes/default/style.min.css" rel="stylesheet">
<link href="resources/css/common.css" rel="stylesheet">
<link href="resources/css/all.css" rel="stylesheet">
<style>
	#page-loading {
	    width: 100%;
	    height: 100%;
	    top: 0px;
	    left: 0px;
	    display: table;
	    background-color: #fff;
	    z-index: 9999;
	    text-align: center;
	}
	 
	#page-loading i{
	    vertical-align: middle;
	    text-align: center;
	    display: table-cell;
	    font-size: 60px;
	}
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
	}​
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
    #tableCase > input {
    	text-align: center;
    }
	.modal-dialog-scrollable .modal-content {
	    max-height: calc(85vh - 3.5rem);
	}
	#exampleModal .tb-scroll{
		overflow: inherit;
	}
    /* .tableCaseCol{
    	width: 110px;
    } */
    .panel-cnt-hd>*{
    	display: initial;
    }
    #rdrJstree a {  height: auto; padding:1px 2px;color: #393e42; } 
	#rdrJstree li > ins { vertical-align:top; }
	#rdrJstree .jstree-hovered, #demo4 .jstree-clicked { border:0; }
	#rdrJstree { overflow: auto; }
    #rdrJstreePath a {  height: auto; padding:1px 2px;color: #393e42; } 
	#rdrJstreePath li > ins { vertical-align:top; }
	#rdrJstreePath .jstree-hovered, #demo4 .jstree-clicked { border:0; }
	#rdrJstreePath { overflow: auto; }
	/* .modal-body > .tb-scroll-wrp {
		width:512px;
	    position: relative;
	    padding-top: 40px;
	    border: 1px solid #d4d4d4;
	}
	.modal-body > .tb-scroll-wrp div {
		width:1912px;
	    max-height: 800px;
	    overflow: auto;
	    overflow-x: hidden;
	}
	.modal-body > .tb-scroll-wrp table {
	    width: 510px;
	    table-layout: fixed;
	    border-spacing: 0;
	    border-collapse: collapse;
	}
	.modal-body > .tb-scroll-wrp table thead tr {
	    position: absolute;
	    top: 0;
	}
	.modal-body > .tb-scroll-wrp table thead tr th {
	    font-weight: normal;
	    width: 120px;
	    height: 40px;
	    background:#eee;
	}
	.modal-body > .tb-scroll-wrp table thead tr th:first-child {
	    width: 238px;
	    font-weight: bold;
	    padding-left: 15px;
	    padding-right: 15px;
	    text-align: left;
	} */
	/* .modal-body > .tb-scroll-wrp table td {
	    text-align: center;
	    width: 120px;
	    color: #2a4383;
	    height: 40px;
    } */
	.scroll-table {
	    width:512px;
	    margin-top: 0px;
	    position: relative;
	    padding-top: 40px;
	    /* border: 1px solid #d4d4d4; */
	}
	.scroll-table div {
	    max-height: 600px;
	    overflow: auto;
	    overflow-x: hidden;
	}
	.scroll-table table {
	    width: 510px;
	    table-layout: fixed;
	    border-spacing: 0;
	    border-collapse: collapse;
	}
	.scroll-table table thead tr {
	    position: absolute;
	    top: 0;
	}
	.scroll-table table thead tr th {
	    font-weight: normal;
	    width: 100px;
	    height: 40px;
	    background:#eee;
	    text-align: center;
	    border: 1px solid #393e42;
	}
	/* .scroll-table table thead tr th{
	    font-weight: bold;
	    padding-left: 5px;
	    padding-right: 5px;
	    text-align: center;
	    border: 1px solid #d4d4d4;
	} */
	.scroll-table table td {
	    text-align: center;
	    width: 100px;
	    height: 30px;
	    border: 1px solid #d4d4d4;
	}
	.scroll-table table td:first-child {
	    font-weight: bold;
	    padding-left: 5px;
	    padding-right: 5px;
	    text-align: center;
	    border: 1px solid #d4d4d4;
	}
	.scroll-table table tr {
	    display: inline-table;
	    width: 508px;
	    table-layout: fixed;
	    border: 1px solid #393e42;
	}
	/* .scroll-table table tr td {
	    border-top: 1px solid #d4d4d4;
	} */
</style>
</head>
<body style="overflow: hidden;">
	<input type="hidden" id="domain" value=<%= request.getParameter("domain") %>>
	<input type="hidden" id="lmbCode" value=<%= request.getParameter("lmbCode") %>>
	<input type="hidden" id="receiptDate" value=<%= request.getParameter("receiptDate") %>>
	<input type="hidden" id="receiptNo" value=<%= request.getParameter("receiptNo") %>>
	<input type="hidden" id="testCode" value=<%= request.getParameter("testCode") %>>
	<input type="hidden" id="specimenCode" value=<%= request.getParameter("specimenCode") %>>
	<input type="hidden" id="decisionSeq" value=<%= request.getParameter("decisionSeq") %>>
	<input type="hidden" id="userId" value=<%= request.getParameter("userId") %>>
	<input type="hidden" id="timestamp" value=<%= request.getParameter("timestamp") %>>
<!-- wrap -->
<div id="wrap">
	<!-- header -->
	<header id="hd" class="clearfix">
		<h1 class="logo text-hide"><a href="/"><img src="resources/images/logo.png" alt="SEEGENEMEDICAL"></a>재단법인 씨젠의료재단</h1>
		<ul class="gnb">
			<!-- <li class="active"><a href="EP_UI_01_검사결과.html">검사결과</a></li>
			<li><a href="EP_UI_02_검사코드관리.html">기준정보관리</a></li>
			<li><a href="EP_UI_03_이력관리.html">이력관리</a></li> -->
		</ul>
		<div class="util">
			<span class="user"><img src="resources/images/award.png" alt=""> AI연구팀 홍길동</span>
			<a href="#" class="btn btn-info">로그아웃</a>
		</div>
	</header>
	<div id="container">
		<div class="row" style="margin-bottom: 20px;">
			<!-- <button type="button" class="btn btn-success" style="float:left" disabled="disabled">도메인 관리</button>
			<button type="button" class="btn btn-success" style="float:left; margin-left: 20px" disabled="disabled">사례구조 관리</button>-->
			<button type="button" class="btn btn-success" style="float:left; margin-left: 20px" onclick="initHref()">설정화면</button>
			<button type="button" class="btn btn-success" style="float:left; margin-left: 20px" onclick="exportRule()">Export Rules</button>
			<button type="button" data-toggle="modal" data-target="#exampleModal" class="btn btn-success" style="float:left; margin-left: 20px" onclick="getCornerCase()">학습된 사례 조회</button> 
		</div>
		<!-- Modal -->
		<div class="modal fade bd-example-modal-xl" id="exampleModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
		  <div class="modal-dialog modal-xl modal-dialog-scrollable" role="document">
		    <div class="modal-content" style="height: 80%">
		      <div class="modal-header">
		        <h5 class="modal-title" id="exampleModalLabel">코너스톤케이스 조회</h5>
		        <div class="form-inline" style="margin-left: 440px">
		        	<input type="checkbox" onchange="allCheckBox()" id="allCheck"><span class="control-label">전체선택</span>
					<span class="control-label" style="margin-left: 45px">검사코드 : </span>
					<input type="text" class="form-control" style="width:130px" id="searchText" onkeyup="searchTextKeyDown()">
				</div>
		        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
		          <span aria-hidden="true">&times;</span>
		        </button>
		      </div>
		      
		     



		      <div class="modal-body" style="padding:0px;overflow-y: hidden;height:800px;">
		      	<div id="page-loading" style="display:none">
    				<i class="fa fa-spinner fa-pulse fa-3x fa-fw margin-bottom"></i>   
			  	</div>
		      	
				<!-- <div class="tb-scroll-wrp" style="height: calc(100% - 45px);width:100%">
					<div>
						<table class="table table-sm tb" id="tableAllCase" style="margin-top: 0px; width:100%">
							<thead style="margin-top: -30px;background-color: #dee2e6;">
								<tr style="height:30px">
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
				</div> -->
				<div class="scroll-table">
				    <div style="width:3525px;height:100%">
				        <table id="tableAllCase">
				            <thead>
				                <tr>
				                </tr>
				            </thead>
				            <tbody>
				            </tbody>
				        </table>
				    </div>
				</div>
		      </div>
		      <div class="modal-footer">
		        <button type="button" class="btn btn-secondary" onclick="getCase()">조회</button>
		        <button type="button" class="btn btn-secondary" data-dismiss="modal" >닫기</button>
		      </div>
		    </div>
		  </div>
		</div>
		
		<div class="row" style="height:100%;">
			<div style="width: calc(30% - 20px); height: calc(100% - 30px); margin-right: 20px">
				<div class="panel">
					<div class="panel-hd">
						<h2 class="panel-title"><i class="icon-list"></i>Rule Tree</h2>
					</div>
					<div class="panel-cnt">
						<div class="panel-cnt-hd">
						<ul class="tab list-inline">
							<li class="active"><a href="javascript:tabClick('ALL')">ALL</a></li>
							<li><a href="javascript:tabClick('PATH')">Rule Path</a></li>
						</ul>
						
						
							<!-- <ul class="nav nav-tabs tab list-inline" id="myTab" role="tablist">
							  <li class="nav-item">
							    <a class="nav-link active" id="home-tab" data-toggle="tab" href="#home" role="tab" aria-controls="home" aria-selected="true">ALL</a>
							  </li>
							  <li class="nav-item">
							    <a class="nav-link" id="profile-tab" data-toggle="tab" href="#profile" role="tab" aria-controls="profile" aria-selected="false">Rule Path</a>
							  </li>
							</ul> -->
							<div class="tab-content" id="myTabContent">
							  <div class="tab-pane fade show active" id="all" role="tabpanel" aria-labelledby="home-tab">
								<div id="rdrJstree" style="height: 600px; width: 100%; margin-top: 25px;"></div>  
							  </div>
							  <div class="tab-pane fade" role="tabpanel" aria-labelledby="profile-tab" id="path">
							  	<div id="rdrJstreePath" style="height: 600px; width: 100%; margin-top: 25px; overflow-x: hidden;"></div>
							  </div>
							</div>
							<!-- <span class="control-label">I D</span>
							<input type="text" class="form-control" title="접수번호 입력" style="width:100%"> -->
							<form style="width: 100%">
							  <div class="form-group row" style="margin: 0">
							    <label for="ruleId" class="col-sm-2 col-form-label" style="color: #393e42;">I D</label>
							    <div class="col-sm-10">
							      <input type="text" readonly class="form-control" id="ruleId" value="" style="width: 97%">
							    </div>
							  </div>
							  <div class="form-group row" style="margin: 0">
							    <label for="ruleCondition" class="col-sm-2 col-form-label" style="color: #393e42;">조건</label>
							    <div class="col-sm-10">
							      <input type="text" readonly class="form-control" id="ruleCondition" placeholder="" style="width: 97%">
							    </div>
							  </div>
							  <div class="form-group row" style="margin: 0">
							    <label for="ruleConclusion" class="col-sm-2 col-form-label" style="color: #393e42;">결론</label>
							    <div class="col-sm-10">
							      <input type="text" readonly class="form-control" id="ruleConclusion" placeholder="" style="width: 97%">
							    </div>
							  </div>
							  <div class="form-group row" style="margin: 0">
							    <label for="ruleConclusion" class="col-sm-2 col-form-label" style="color: #393e42;">학습된 사례</label>
							    <div class="col-sm-10">
							      <input type="text" readonly class="form-control" id="stutyCase" placeholder="" style="width: 80%;float: left">
							      <button type="button" class="btn btn-primary" style="float:right;margin-top: 0px;margin-right: 20px;" onclick="getStudyCase()">조회</button>
							    </div>
							  </div>
							</form>
							<!-- <div class="col-4">
						    	<span class="input-group-text">I D</span>
							</div>
							<div class="col-7">
								<input type="text" class="form-control" id="ruleId">
							</div>
							<div class="input-group mb-3">
							  <div class="input-group-prepend">
							    <span class="input-group-text">I D</span>
							  </div>
							  <input type="text" class="form-control" id="ruleId">
							</div>
							<div class="input-group mb-3">
							  <div class="input-group-prepend">
							    <span class="input-group-text">조건</span>
							  </div>
							  <input type="text" class="form-control" id="ruleCondition">
							</div>
							<div class="input-group mb-3">
							  <div class="input-group-prepend">
							    <span class="input-group-text">결론</span>
							  </div>
							  <input type="text" class="form-control" id="ruleConclusion">
							</div>	 -->
						</div>
					</div>
				</div>
			</div>
			
			
			<div style="width: 70%; height: calc(100% - 70px);">
				<div class="panel" style="height: 65%; margin-bottom: 15px;">
					<div class="panel-hd">
						<h2 class="panel-title"><i class="icon-list"></i>Case</h2>
						<label style="float: right; margin-top: 12px; margin-right: 20px; color: white;">
							<input type="checkbox" id="nullColhide" style="" checked="checked" onchange="nullColhideChange()">
							값 없는 항목 숨기기
						</label>
						<button type="button" class="btn btn-primary" style="float:right;margin-top: 10px;margin-right: 20px;" onclick="copyCase()">사례복제</button>
						<button type="button" class="btn btn-primary" style="float:right;margin-top: 10px;margin-right: 20px;" onclick="insertCase(null,null,true)">사례추가</button>
					</div>
					<div class="tb-scroll-wrp" style="height: calc(100% - 45px)">
						<div class="tb-scroll">
							<table class="table table-sm tb" id="tableCase">
								<thead>
								</thead>
								<tbody>
								</tbody>
							</table>
						</div>
					</div>
				</div>
				<div class="panel" style="width:38%; height:38%; float:left; margin-right: 15px;">
					<div class="panel-hd">
						<h2 class="panel-title"><i class="icon-list"></i>추론에 사용된 조건</h2>
					</div>
					<div class="tb-scroll-wrp" style="height:calc(100% - 30px)">
						<div class="tb-scroll">
							<table class="table table-sm tb" id="tableConditionSet">
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
				
				<div class="panel" style="width:29%; height:38%; float:left; margin-right: 15px;">
					<div class="panel-hd">
						<h2 class="panel-title"><i class="icon-list"></i>결론</h2>
					</div>
					<div class="tb-scroll-wrp">
						<div class="tb-scroll">
							<table class="table table-sm tb" style="width: 100%;" id="tableConclusion">
								<thead>
									<tr>
										<th scope="col">선택</th>
										<th scope="col">결론</th>
									</tr>
								</thead>
								<tbody>
								</tbody>
							</table>
						</div>
					</div>
				</div>
				<div style="width: 30%; height:5%; float:left;padding-top: 7px;">
					<button type="button" class="btn btn-success" style="float:right;" onclick="btnRule('delete')" id="btnDeleteRule">지식삭제</button>
					<button type="button" class="btn btn-success" style="float:right; margin-right: 20px" onclick="btnRule('add')" id="btnAddRule">지식추가</button>
					<button type="button" class="btn btn-success" style="float:right; margin-right: 20px" onclick="btnRule('edit')" id="btnEditRule">지식수정</button>
				</div>
				<div class="panel-txt" style="width: 30%; height:33%; float:left;background-color: white; border-radius: 15px;" id="conclusionTXT">

				</div>
			</div>
		
		</div>
	</div>
</div>
</body>
</html>

