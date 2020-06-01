var caseStructure = {};
var operators = {};
var selectDomain = {};

function addAttribute(){
	var attrlist = $("#tableCate > tbody").find('td');
	var mylist = [];
	console.log(attrlist);
	for (var i = 0; i < attrlist.length; i++) {
		if(attrlist[i].innerText != '삭제'){
			mylist.push(attrlist[i].innerText);
		}
	}
	$.ajax({
    	url: 'kb/attribute?domain=' + selectDomain,
		method: "POST", 
		accept: "application/json",
		contentType: "application/json; charset=utf-8",
		dataType: "json",
		async: false,
		data: JSON.stringify({
			"name" : $("#chkAttr").val(),
			"type" : $("#chkType").val(),
			"desc" : $("#chkdesc").val(),
			"list" : mylist
		}),
    	success: function(data) {
    		if(data.validity == "valid"){
    			getStru();
    			alert("추가되었습니다.");
    		}else{
    			alert(data.msg);
    		}
    	}, error: function(data){
    		
    	}
    });
}

function btnDel(e){
	console.log(e);
	e.parentElement.parentElement.remove();
}

function addCataValue(name){
//	var appendHtml = "";
//	appendHtml += "<tr class='addClass' style='background-color: beige;'><td>" + name + "</td>"
//	 + "<td><a href='#' onclick='btnDel(this)' class='btn btn-info'>삭제</a></td></tr>";
//	$("#tableCate > tbody").append(appendHtml);
	$("#chkAttr").val(name);
//	$("#chkAttr2").val(name);
}

function addCategoricals(){
	if($(":input:radio[name=attr]:checked").length == 0){
		alert("속성을 선택해주세요");
	}else{
		var chkAttr = $($(":input:radio[name=attr]:checked")[0]).parent().parent().find('td')[1].innerHTML;
		var chkType = $($(":input:radio[name=attr]:checked")[0]).parent().parent().find('td')[2].innerHTML;
		var chkdesc = $($(":input:radio[name=attr]:checked")[0]).parent().parent().find('td')[3].innerHTML;
		//editAttributeDesc
		$.ajax({
	        url: 'kb/attributeDesc?domain=' + selectDomain,
	        method: "PUT", 
	        dataType: "json",
			accept: "application/json",
			contentType: "application/json; charset=utf-8",
	        data: JSON.stringify({
	        	"name" : chkAttr,
	        	"desc" : $("#chkdesc").val(),
	        }),
	        async: false,
	        success: function(data) {
	            console.log(data);
	            //editAttributeName
	    		$.ajax({
	    	        url: 'kb/attribute?domain=' + selectDomain,
	    	        method: "PUT", 
	    	        dataType: "json",
	    			accept: "application/json",
	    			contentType: "application/json; charset=utf-8",
	    	        data: JSON.stringify({
	    	        	"name" : chkAttr,
	    	        	"newName" : $("#chkAttr").val(),
	    	        }),
	    	        async: false,
	    	        success: function(data) {
	    	            console.log(data);
	    	        }, error: function(data){
	    	        	
	    	        }
	    	    });
	        }, error: function(data){
	        	
	        }
	    });
		
		var chkList = $("#add_" + chkAttr).val().split(',');
		var addChkList = [];
		
		
//	var chkAttr = $("#chkAttr").val();
//	var chkType = $("#chkType").val();
//	var chkdesc = $("#chkdesc").val();
		
		if(chkType == "CATEGORICAL"){
			for (var i = 0; i < chkList.length; i++) {
				if(chkList[i].length != 0){
					addChkList.push(chkList[i]);
				}
			}
//			for (var i = 0; i < $("#tableCate > tbody > tr.addClass").length; i++) {
//				chkList.push($($("#tableCate > tbody > tr.addClass")[i]).find('td')[0].innerHTML);
//			}
		}
		console.log(addChkList);
		
		for (var i = 0; i < addChkList.length; i++) {
			$.ajax({
		    	url: 'kb/categorical?domain=' + selectDomain,
				method: "POST", 
				accept: "application/json",
				contentType: "application/json; charset=utf-8",
				dataType: "json",
				async: false,
				data: JSON.stringify({
					name : $("#chkAttr").val(),
					value : addChkList[i],
				}),
		    	success: function(data) {
		    		if(data.validity == "valid"){
		    			
		    		}else{
		    			alert(data.msg);
		    		}
		    	}, error: function(data){
		    		
		    	}
		    });
		}
		alert("저장되었습니다.");
		$("#add_" + chkAttr).val("");
		getStru();
		
	}
}
function clickDomain(domain){
	console.log(domain);
	selectDomain = domain;
	$.ajax({
    	url: 'kb/caseStructure?domain=' + domain,
    	method: "GET", 
    	dataType: "json",
    	async: false,
    	data: {
    	},
    	success: function(data) {
    		console.log("caseStructure : ", data);
    		caseStructure = data;
    		var appendHtml = "";
    		$("#structTable > tbody").empty();
            for (var i = 0; i < data.length; i++) {
				appendHtml += "<tr onclick='clickAttr(\"" + data[i].name + "\")'><td><input type='radio' name='attr' id='attr_'" + data[i].name + " onchange='clickAttr(\"" + data[i].name + "\")'></td>"; 
				appendHtml += "<td>" + data[i].name + "</td>"; 
				appendHtml += "<td>" + data[i].type + "</td>"; 
				appendHtml += "<td>" + data[i].desc + "</td>";
				if(data[i].list){
					appendHtml += "<td>" + data[i].list + "</td>"; 
				}else{
					appendHtml += "<td></td>"; 
				}
				
				if(data[i].type == "CATEGORICAL"){
					appendHtml += "<td><input type='text' style='width:100%;' id='add_" + data[i].name + "' value=''></td></tr>"; 
				}else{
					appendHtml += "<td></td></tr>"; 
				}
			}
            console.log(appendHtml);
            $("#structTable > tbody").append(appendHtml);
    	}, error: function(data){
    		
    	}
    });
	
}

function clickAttr(attr){
	
	for (var i = 0; i < $(":input:radio[name=attr]").parent().parent().parent().find('tr').length; i++) {
		if($($(":input:radio[name=attr]")[i]).parent().parent().find('td')[1].innerHTML == attr){
			$($($(":input:radio[name=attr]")[i]).parent().parent().find('td')[0]).find('input')[0].checked = true;
			break;
		}
	}
	
	var chkAttr = $($(":input:radio[name=attr]:checked")[0]).parent().parent().find('td')[1].innerHTML;
	var chkType = $($(":input:radio[name=attr]:checked")[0]).parent().parent().find('td')[2].innerHTML;
	var chkdesc = $($(":input:radio[name=attr]:checked")[0]).parent().parent().find('td')[3].innerHTML;
	var myStru = {};
	
	$.ajax({
    	url: 'kb/caseStructure?domain=' + selectDomain,
    	method: "GET", 
    	dataType: "json",
    	async: false,
    	data: {
    	},
    	success: function(data) {
    		console.log("caseStructure : ", data);
    		caseStructure = data;
    	}, error: function(data){
    		
    	}
    });
	
	for (var i = 0; i < caseStructure.length; i++) {
		if(caseStructure[i].name == attr){
			myStru = caseStructure[i];
			break;
		}
	}
	$("#tableCate > tbody").empty();
	$("#tableCateSub > tbody").empty();
	
	$("#chkAttr").val(chkAttr);
	$("#chkType").val(chkType);
	if(chkType == "CATEGORICAL"){
		$("#tableCateSub").css("display","");
		$("#tableCate").css("display","");
		
		var appendHtml = "";
		for (var i = 0; i < myStru.list.length; i++) {
			appendHtml += "<tr><td>" + myStru.list[i] + "</td><td><a href='#' onclick='btnDel(this)' class='btn btn-info'>삭제</a></td></tr>";
		}
		$("#tableCate > tbody").append(appendHtml);
		
		
		
		$.ajax({
	    	url: 'smf/getCandidateAttributes?dbName=smf_ep',
	    	method: "GET", 
	    	dataType: "json",
	    	async: false,
	    	data: {
	    	},
	    	success: function(data) {
	    		console.log("getCandidateAttributes : ", data);
	    		
	    		appendHtml = "";
	    		for (var i = 0; i < data.length; i++) {
	    			appendHtml += "<tr onclick='addCataValue(\"" + data[i].name + "\")'><td>" + data[i].name + "</td>";
	    			appendHtml += "<td>" + data[i].type + "</td></tr>";
	    		}
	    		$("#tableCateSub > tbody").append(appendHtml);
	    	}, error: function(data){
	    		
	    	}
	    });
		
	}else{
		$("#tableCateSub").css("display","none");
		$("#tableCate").css("display","none");
	}
	$("#chkdesc").val(chkdesc);
	
	
	
	
	
	
	
	
}

function getDelete(){
	var chkDomain = $($(":input:radio[name=domain]:checked")[0]).parent().parent().find('td')[1].innerHTML;
	var chkAttr = $($(":input:radio[name=attr]:checked")[0]).parent().parent().find('td')[1].innerHTML;
	
	console.log(chkDomain, chkAttr);
	
	$.ajax({
    	url: 'kb/attribute?domain=' + chkDomain + "&name=" + chkAttr,
		method: "DELETE", 
		accept: "application/json",
		contentType: "application/json; charset=utf-8",
		dataType: "json",
		async: false,
		data: JSON.stringify(),
    	success: function(data) {
    		if(data.validity == "valid"){
    			getStru();
    			alert("삭제되었습니다.");
    		}else{
    			alert(data.msg);
    		}
    	}, error: function(data){
    		
    	}
    });
	
}
function getStru(){
	var chkDomain = $($(":input:radio[name=domain]:checked")[0]).parent().parent().find('td')[1].innerHTML;
	$.ajax({
    	url: 'kb/caseStructure?domain=' + chkDomain,
    	method: "GET", 
    	dataType: "json",
    	async: false,
    	data: {
    	},
    	success: function(data) {
    		console.log("caseStructure : ", data);
    		var appendHtml = "";
    		$("#structTable > tbody").empty();
            for (var i = 0; i < data.length; i++) {
				appendHtml += "<tr onclick='clickAttr(\"" + data[i].name + "\")'><td><input type='radio' name='attr' id='attr_'" + data[i].name + " onchange='clickAttr(\"" + data[i].name + "\")'></td>"; 
				appendHtml += "<td>" + data[i].name + "</td>"; 
				appendHtml += "<td>" + data[i].type + "</td>"; 
				appendHtml += "<td>" + data[i].desc + "</td>"; 
				if(data[i].list){
					appendHtml += "<td>" + data[i].list + "</td>"; 
				}else{
					appendHtml += "<td></td>"; 
				}
				if(data[i].type == "CATEGORICAL"){
					appendHtml += "<td><input type='text' style='width:100%;' id='add_" + data[i].name + "' value=''></td></tr>"; 
				}else{
					appendHtml += "<td></td></tr>"; 
				} 
			}
            console.log(appendHtml);
            $("#structTable > tbody").append(appendHtml);
    	}, error: function(data){
    		
    	}
    });
}
function struInit(){
	$("#structTable > tbody").empty();
}
function DeleteDomain(){
	var chkDomain = $($(":input:radio[name=domain]:checked")[0]).parent().parent().find('td')[1].innerHTML;
	console.log(chkDomain);
	if (confirm(chkDomain + "도메인의 모든 지식정보가 삭제됩니다. 계속하시겠습니까?")) {
		$.ajax({
	    	url: 'kb/domain?domain=' + chkDomain,
			method: "DELETE", 
			accept: "application/json",
			contentType: "application/json; charset=utf-8",
			dataType: "json",
			async: false,
			data: JSON.stringify({
				domain : chkDomain,
			}),
	    	success: function(data) {
	    		if(data.validity == "valid"){
	    			LoadDomain();
	    		}else{
	    			alert(data.msg);
	    		}
	    	}, error: function(data){
	    		
	    	}
	    });
	}else{
		
	}
}

function addDomain(){
	if (confirm("새로운 도메인 정보를 추가하시겠습니까?")) {
		$.ajax({
	    	url: 'kb/domain?domain=' + $("#domainName").val() + '&desc=' + $("#domainDesc").val() + '&reasoner=' + $("#domainType").val(),
			method: "POST", 
			accept: "application/json",
			contentType: "application/json; charset=utf-8",
			dataType: "json",
			async: false,
//			data: JSON.stringify({
//				"domain" : $("#domainName").val(),
//				"desc" : $("#domainDesc").val(),
//				"reasoner" : $("#domainType").val(),
//			}),
	    	success: function(data) {
	    		if(data.validity == "valid"){
	    			LoadDomain();
	    		}else{
	    			alert(data.msg);
	    		}
	    	}, error: function(data){
	    		
	    	}
	    });
	}else{
		
	}
}

function LoadDomain(){
	$.ajax({
    	url: 'kb/domain',
    	method: "GET", 
    	dataType: "json",
    	async: false,
    	data: {
    	},
    	success: function(data) {
    		console.log("연산자 정보 : ", data);
    		for (var i = 0; i < data.length; i++) {
				if(data[i].domain == $("#domain").val()){
					selectDomain = data[i];
				}
			}
    		if($("#domain").val()){
    			selectDomain = data[0].domain;
    		}
    		
    		$("#domainTable > tbody").empty();
    		var appendHtml = "";
    		for (var i = 0; i < data.length; i++) {
				appendHtml += "<tr><td><input type='radio' name='domain' onchange='clickDomain(\"" + data[i].domain + "\")'></td>"; 
				appendHtml += "<td>" + data[i].domain + "</td>"; 
				appendHtml += "<td>" + data[i].desc + "</td>"; 
				appendHtml += "<td>" + data[i].reasoner + "</td>"; 
				appendHtml += "<td></td></tr>"; 
			}
    		$("#domainTable > tbody").append(appendHtml);
    		
    	}, error: function(data){
    		
    	}
    });
}

$(document).ready(function(){
    console.log("initialising...");
    
    
    
    $("#selectKaMode").val($("#kaMode").val());
    
    LoadDomain();
    
    $.ajax({
    	url: 'kb/operator?domain=' + selectDomain,
    	method: "GET", 
    	dataType: "json",
    	async: false,
    	data: {
    	},
    	success: function(data) {
    		console.log("도메인정보 : ", data);
    		operators = data ;
    		
    		$("#chkType").empty();
    		
    		for (var i = 0; i < Object.keys(operators).length; i++) {
    			$("#chkType").append("<option value='" + Object.keys(operators)[i] + "'>" + Object.keys(operators)[i] + "</option>");
			}
    		
    		
    	}, error: function(data){
    		
    	}
    });
    
   
});