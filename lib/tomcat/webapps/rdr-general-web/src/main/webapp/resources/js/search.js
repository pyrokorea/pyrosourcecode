var myDomain = {};

var inferenceByBrokerResult = "";
var ruleList = {};
var caseStructure = {};
var isTimeStamp = false;

function getContextPath() {
	var hostIndex = location.href.indexOf(location.host) + location.host.length;
	return location.href.substring(hostIndex, location.href.indexOf('/', hostIndex + 1));
}

function initHref(){
	//location.href = '${pageContext.request.contextPath}' + "/init";
	//location.href = "/rdr-pbs/init";
	location.href = getContextPath() + "/init";
}

function allCheckBox(){
	var allCheck = $("#allCheck")[0].checked;
	console.log(allCheck);
	
	var tableList = $("#tableAllCase > tbody")[0];
	if(allCheck){
		for (var i = 0; i < $(tableList).find('tr').length; i++) {
			if($($(tableList).find('tr')[i]).css("display") != "none"){
				$($($(tableList).find('tr')[i]).find('td')[0]).find('input')[0].checked = true;
			}
		}
	}else{
		for (var i = 0; i < $(tableList).find('tr').length; i++) {
			if($($(tableList).find('tr')[i]).css("display") != "none"){
				$($($(tableList).find('tr')[i]).find('td')[0]).find('input')[0].checked = false;
			}
		}
	}
	
}
function searchTextKeyDown(){
	var searchText = $("#searchText").val();
	
	var tableList = $("#tableAllCase > tbody")[0];
	
	for (var i = 0; i < $(tableList).find('tr').length; i++) {
		$($(tableList).find('tr')[i]).css("display","none");
		if($($(tableList).find('tr')[i]).find('td')[3].innerHTML.toLowerCase().indexOf(searchText.toLowerCase()) !== -1){
			$($(tableList).find('tr')[i]).css("display","");
		}else{
			$($(tableList).find('tr')[i]).css("display","none");
		}
	}
	
	
	console.log(searchText);
}

function exportRule(){
//	$.ajax({
//		url: 'smf/report?domain=special_immunology&userId=testUser',
//		method: "GET", 
//		dataType: "json",
//		async: false,
//		data: {
//		},
//		success: function(result) {
//			alert("download 폴더에 다운로드 되었습니다.");
//		}, error: function(data){
//			
//		}
//	});
	
	//window.open('smf/report?domain=special_immunology&userId=testUser');
	$(window.open('smf/report?domain=' + $("#domain").val() + '&userId=' + $("#userId").val()));

}

function getStudyCase(){
	var stutyCase = $("#stutyCase").val().split(',');
	console.log(stutyCase);
	for (var i = 0; i < stutyCase.length; i++) {
		$.ajax({
			//url: '/rdr-ep/kb/cornerstone/' + stutyCase[i] + '?domain=' + $("#domain").val(),
			url: 'kb/cornerstone/' + stutyCase[i] + '?domain=' + $("#domain").val(),
			method: "GET", 
			dataType: "json",
			async: false,
			data: {
			},
			success: function(result) {
				console.log("학습된 사례 조회 : ", result);
				insertCase(null, result.data,false);
			}, error: function(data){
				
			}
		});
	}
	
}

function returnCaseStructure(name, value){
	var returnHtml = "";
	var stru = {};
	
	
	
	
	for (var i = 0; i < caseStructure.length; i++) {
		if(caseStructure[i].name == name){
			stru = caseStructure[i];
		}
	}
	
	if(stru.type == "CATEGORICAL"){
		returnHtml = '<select class="form-control" class="name"' + stru.id + ' style="width:80px;text-align:center">';
		var selected = "";
		if(value == ""){
			selected = "selected";
		}
		returnHtml += '<option value="" ' + selected + '></option>';
		for (var i = 0; i < stru.list.length; i++) {
			if(value == stru.list[i]){
				returnHtml += '<option value="' + stru.list[i] + '" selected>' + stru.list[i] + '</option>'; 	
			}else{
				returnHtml += '<option value="' + stru.list[i] + '">' + stru.list[i] + '</option>'; 	
			}
		}
		returnHtml += '</select>';
	}

	else if(stru.type == "TEXT"){
		returnHtml = "<input type='text' style='width:80px;text-align:center' value=" + value + " >";
	}
	
	return returnHtml;
}
function copyCase(){
	var allCase = $("#tableCase").find('tbody > tr');
	var myCopyCase = null;
	for (var i = 0; i < allCase.length; i++) {
		if($(allCase[i]).find('input[type=radio]')[0].checked){
			myCopyCase = allCase[i]; 
			break;
		}
			
	}
	if(myCopyCase){
		console.log(myCopyCase);
		insertCase(myCopyCase, null,true);
	}else{
		alert("Case를 선택해주세요");
	}
	
//	$($(copyCase)[0]).find('td')[0].innerHTML = '<input type="radio" name="radioCase" onclick="getInferenceByJson(this)" value="M">M';
//    $("#tableCase > tbody").append($(copyCase)[0].outerHTML);
}

function insertCase(obj, data, isEditable){
	$("#nullColhide")[0].checked = false;
	nullColhideChange();
	var appendHtml = "";
	if(obj == null && data == null){
		appendHtml += "<tr style='background-color: beige;'><td><input type='radio' name='radioCase' onclick='getInferenceByJson(this)' value='M'></td>";
		appendHtml += "<td>M</td>";
		for (var i = 0; i < caseStructure.length; i++) {
			appendHtml += "<td name='case'>" + returnCaseStructure(caseStructure[i].name, "") + "</td>";
		}
		appendHtml += "</tr>";
	}else if(data && isEditable){
		appendHtml += "<tr style='background-color: beige;'><td><input type='radio' name='radioCase' onclick='getInferenceByJson(this)' value='M'></td>";
		appendHtml += "<td>M</td>";
		for (var i = 0; i < caseStructure.length; i++) {
			if(data !== undefined && data[caseStructure[i].name]){
				appendHtml += "<td name='case'>" + returnCaseStructure(caseStructure[i].name, data[caseStructure[i].name]) + "</td>";
			}else{
				appendHtml += "<td name='case'>" + returnCaseStructure(caseStructure[i].name, "") + "</td>";
			}
		}
		appendHtml += "</tr>";
	}else if(data && !isEditable){
		appendHtml += "<tr style='background-color: beige;'><td><input type='radio' name='radioCase' onclick='getInferenceByJson(this)' value='M'></td>";
		appendHtml += "<td>M</td>";
		for (var i = 0; i < caseStructure.length; i++) {
			if(data !== undefined && data[caseStructure[i].name]){
				appendHtml += "<td name='case'>" + data[caseStructure[i].name] + "</td>";
			}else{
				appendHtml += "<td name='case'>" + "" + "</td>";
			}
		}
		appendHtml += "</tr>";
	}else if(isEditable){
		var myTd = $(obj).find('td');
		appendHtml += "<tr style='background-color: beige;'><td><input type='radio' name='radioCase' onclick='getInferenceByJson(this)' value='M'></td>";
		appendHtml += "<td>M</td>";
		for (var i = 2; i < myTd.length; i++) {
			var tmpData = "";
			if(myTd[1].innerHTML == "M"){
				tmpData = $(myTd[i]).find('select').val();
				if(typeof tmpData == "undefined"){
					tmpData = $(myTd[i]).find('input').val();
				}
			}else{
				tmpData = myTd[i].innerHTML;
			}
			appendHtml += "<td name='case'>" + returnCaseStructure(caseStructure[i-2].name, tmpData) + "</td>"
		}
		appendHtml += "</tr>";
	}else{
		var myTd = $(obj).find('td');
		appendHtml += "<tr style='background-color: beige;'><td><input type='radio' name='radioCase' onclick='getInferenceByJson(this)' value='M'></td>";
		appendHtml += "<td>M</td>";
		for (var i = 2; i < myTd.length; i++) {
			var tmpData = "";
			if(myTd[1].innerHTML == "M"){
				tmpData = $(myTd[i]).find('select').val();
				if(typeof tmpData == "undefined"){
					tmpData = $(myTd[i]).find('input').val();
				}
			}else{
				tmpData = myTd[i].innerHTML;
			}
			appendHtml += "<td name='case'>" + tmpData + "</td>"
		}
		appendHtml += "</tr>";
	}
    
	$("#tableCase > tbody").append(appendHtml);
}

function getInferenceByJson(data){
	console.log("getInferenceByJson : ", data);
	var attrAll = $(data.parentElement.parentElement).find('td');
	console.log($($($(data.parentElement.parentElement).find('td')[1])[0])[0].innerHTML);
//	if($($($(data.parentElement.parentElement).find('td')[1])[0])[0].innerHTML == 0){
//		$("#btnAddRule").attr("disabled",false);
//		$("#btnEditRule").attr("disabled",false);
//		$("#btnDeleteRule").attr("disabled",false);
//	}else{
//		$("#btnAddRule").attr("disabled",true);
//		$("#btnEditRule").attr("disabled",true);
//		$("#btnDeleteRule").attr("disabled",true);
//	}
	if(myDomain.reasoner == "SCRDR"){
		$("#btnAddRule").attr("disabled",true);
	}else{
		$("#btnAddRule").attr("disabled",false);
	}
	var mycase = {}
	for (var i = 2; i < attrAll.length; i++) {
		if($($($(data.parentElement.parentElement).find('td')[i])[0]).find('input').val() || 
				$($($(data.parentElement.parentElement).find('td')[i])[0]).find('input').val() == ""){
			if($($($(data.parentElement.parentElement).find('td')[i])[0]).find('input').val() != ""){
				mycase[caseStructure[i-2].name] = $($($(data.parentElement.parentElement).find('td')[i])[0]).find('input').val(); 	
			}
		}else if($($($(data.parentElement.parentElement).find('td')[i])[0]).find('select').val() ||
				$($($(data.parentElement.parentElement).find('td')[i])[0]).find('select').val() == ""){
			if($($($(data.parentElement.parentElement).find('td')[i])[0]).find('select').val() != ""){
				mycase[caseStructure[i-2].name] = $($($(data.parentElement.parentElement).find('td')[i])[0]).find('select').val();
			}
		}else{
			if($($(data.parentElement.parentElement).find('td')[i])[0].innerHTML != ""){
				mycase[caseStructure[i-2].name] = $($(data.parentElement.parentElement).find('td')[i])[0].innerHTML;
			}
			
		}
//		console.log(i + " = ", $($(data.parentElement.parentElement).find('td')[i])[0].innerHTML);
	}
	console.log(mycase);
	$.ajax({
        url: 'inference/JSON?domain=' + $("#domain").val(),
        method: "PUT", 
        dataType: "json",
		accept: "application/json",
		contentType: "application/json; charset=utf-8",
        data: JSON.stringify(mycase),
        async: false,
        success: function(data) {
            console.log(data);
            $("#tableConclusion > tbody").empty();
		      	
	      	if(data.validity == "valid"){
	      		$("#conclusionTXT")[0].innerHTML = "";
	      		var isCheck = "";
		      	if(myDomain.reasoner == "SCRDR"){
		      		commentChange(data.inference[0].conclusion, -1); //modified by ucciri
		      		isCheck = "checked";
		      	}
		      	
//		      	if(data.inference[0].conclusionId == 0 && data.inference.length == 1){
//		      		
//		      	}else{
		      		for (var i = 0; i < data.inference.length; i++) {
		      			$("#tableConclusion > tbody").append("<tr>\
		      					<td><input type='radio' " + isCheck + " name='tableConclusion' value=" 
		      					//+ data.inference[i].conclusionId + " onchange='commentChange(\"" + data.inference[i].conclusion + "\")'></td>\
		      					//modified by ucciri
		      					+ data.inference[i].conclusionId + " onchange='commentChange(\"" + data.inference[i].conclusion + "\", " + data.inference[i].ruleId + ")'></td>\
		      					<td>" 
		      					+ data.inference[i].conclusion + "</td>\
		      			</tr>");
		      			
		      		}
//		      	}
	      	}

            var addconditionSet = [];
	      	var conditionSet = {};
            $("#tableConditionSet > tbody").empty();
	      	for (var i = 0; i < data.firedRules.length; i++) {
	      		for (var j = 0; j < data.firedRules[i].conditionSet.length; j++) {
	      			conditionSet[data.firedRules[i].conditionSet[j].attribute 
	      				+ data.firedRules[i].conditionSet[j].operator 
	      				+ data.firedRules[i].conditionSet[j].value] = data.firedRules[i].conditionSet[j]; 
				}
			}
	      	
	      	for ( var i in conditionSet) {
	      		addconditionSet.push(conditionSet[i]);
	      	}
	      	for (var i = 0; i < addconditionSet.length; i++) {
	      		var appendHtml = '<tr><td>' + addconditionSet[i].attribute + '</td><td>'
      			+ addconditionSet[i].operator + '</td><td>'
      			+ addconditionSet[i].value + '</td></tr>'
      			$("#tableConditionSet > tbody").append(appendHtml)
			}
		      	
	      	var myList = [];
			for (var i = 0; i < data.firedRules.length; i++) {
				var obj = {};
				obj.id = data.firedRules[i].id;
				if(data.firedRules[i].parentId == null){
					obj.parent = '#';
					obj.state = {'opened' : true};
				}else if(data.firedRules[i].parentId == 0){
					obj.parent = '0';
				}
				else{
					obj.parent = data.firedRules[i].parentId;
				}
				obj.text = data.firedRules[i].ruleStatement;
//				obj.state = {'opened' : true};
				myList.push(obj);
				ruleList[data.firedRules[i].id] = data.firedRules[i];
			}
			console.log(myList);
			$('#rdrJstreePath').jstree({ 'core' : {
		        'data' : myList
		    } });
			$('#rdrJstreePath').jstree(true).settings.core.data = myList;
			$('#rdrJstreePath').jstree(true).refresh();
        }, error: function(data){
        	
        }
    });
}

function nullColhideChange(){
	var checked = $("#nullColhide")[0].checked;
	console.log(checked);
	var thead = $($("#tableCase > thead")[0]).find('th');
	var tbody = $("#tableCase > tbody > tr");
	var colHide = [];
	
	for (var j = 0; j < thead.length; j++) {
		colHide.push(true);
	}
	if(checked){
			for (var i = 0; i < tbody.length; i++) {
				for (var j = 0; j < thead.length; j++) {
				if(colHide[j]){
					if($(tbody[i].cells[j]).find('input').val() !== ""
						&&$(tbody[i].cells[j]).find('input').val() !== undefined){
						if($(tbody[i].cells[j]).find('input').val().length > 0){
							colHide[j] = false;
						}
					}else if($(tbody[i].cells[j]).find('select').val() !== ""
						&& $(tbody[i].cells[j]).find('select').val() !== undefined){
						if($(tbody[i].cells[j]).find('select').val().length > 0){
							colHide[j] = false;
						}
							
					}else if(tbody[i].cells[j].innerText !== ""
						&& tbody[i].cells[j].innerText !== undefined){
						if($(tbody[i].cells[j].innerHTML).val() !== ""){
							colHide[j] = false;
						}
					}
				}
				
			}
		}
		console.log(colHide);
		for (var i = 0; i < colHide.length; i++) {
			if(!colHide[i]){
				$(thead[i]).css("display","");
			}else{
				$(thead[i]).css("display","none");
			}
		}
		for (var j = 0; j < tbody.length; j++) {
			var tbodytr = $(tbody[j]).find("td");
			for (var i = 0; i < colHide.length; i++) {
				if(!colHide[i]){
					$(tbodytr[i]).css("display","");
				}else{
					$(tbodytr[i]).css("display","none");
				}
			}
		}
		
				
				
//				var tbodytr = $(tbody[i]).find("td");
//				if(tbodytr[j].innerHTML.length == 0){
//					$(tbodytr[j]).css("display","none");
//					$(theadtr[j]).css("display","none");
//				}else{
//					$(tbodytr[j]).css("display","");
//					$(theadtr[j]).css("display","");
//				}
	}else{
		for (var i = 2; i < colHide.length; i++) {
			$(thead[i]).css("display","");
		}
		for (var j = 0; j < tbody.length; j++) {
			var tbodytr = $(tbody[j]).find("td");
			for (var i = 0; i < colHide.length; i++) {
				$(tbodytr[i]).css("display","");
			}
		}
	}
}

function getCase(){
	var chkds = $("input[name='tableAllCaseCheck']:checked");
	
	if(chkds.length > 0){
		setTimeout(function() {
			$("#page-loading").css("display", "");
		}, 0);
		
		
		console.log(chkds);
		setTimeout(function() {
			for (var i = 0; i < chkds.length; i++) {
				$.ajax({
					url: 'kb/cornerstone/' + chkds[i].value + '?domain=' + $("#domain").val(),
					method: "GET", 
					dataType: "json",
					async: false,
					data: {
					},
					success: function(result) {
						console.log("학습된 사례 조회 : ", result);
						var rowData = [];
						var appendHtml = "";
						
						appendHtml = "";
						appendHtml += "<tr><td><input type='radio' name='radioCase' onclick='getInferenceByJson(this)' value='" + result.caseId + "'></td>";
						appendHtml += "<td>" + result.caseId + "</td>";
						for (var i = 0; i < caseStructure.length; i++) {
							if(result.data[caseStructure[i].name]){
								appendHtml += "<td>" + result.data[caseStructure[i].name] + "</td>";
							}else{
								appendHtml += "<td class='hideCol'></td>";
							}
						}
						appendHtml += "</tr>";
						$("#tableCase > tbody").append(appendHtml);
						
						nullColhideChange();
						
					}, error: function(data){
						
					},beforeSend:function(){
						
					}
					,complete:function(){
						if(i == chkds.length-1){
							console.log(i);
							$("#page-loading").css("display", "none");
							$('#exampleModal').modal('hide');
						}
					}	
				});
			}
		}, 500);
	}else{
		alert("선택된 사례가 없습니다.");
//		$('#exampleModal').modal('hide');
	}
	
}


function getCornerCase(){
	$("#nullColhide")[0].checked = false;
	$.ajax({
		url: 'kb/cornerstone?domain=' + $("#domain").val(),
		method: "GET", 
		dataType: "json",
		async: false,
		data: {
		},
		success: function(result) {
			console.log("학습된 사례 조회 : ", result);
			
            var rowData = [];
            

            var appendHtml = "";
//            for (var i = 0; i < caseStructure.length; i++) {
//            	appendHtml += '<col class="tableCaseCol">';
//            }
//            $("#tableAllCase").append(appendHtml);
            
            $("#tableAllCase > thead").empty();
            appendHtml = "<tr style='height:30px'><th>선택</th><th>id</th>";
            for (var i = 0; i < caseStructure.length; i++) {
            	appendHtml += "<th scope='col'>" + caseStructure[i].name + "</th>";
			}
            appendHtml += "</tr>";
            console.log(appendHtml);
            $("#tableAllCase > thead").append(appendHtml);
            
            appendHtml = "";
            $("#tableAllCase > tbody").empty();

            for (var j = 0; j < result.length; j++) {
	            appendHtml += "<tr>";
	            appendHtml += "<td><input type='checkbox' name='tableAllCaseCheck' value='" + result[j].caseId + "'></td><td>" + result[j].caseId + "</td>";
	            for (var i = 0; i < caseStructure.length; i++) {
	            	if(result[j].data[caseStructure[i].name]){
	            		appendHtml += "<td>" + result[j].data[caseStructure[i].name] + "</td>";
	            	}else{
	            		appendHtml += "<td class='hideCol'></td>";
	            	}
				}
	            appendHtml += "</tr>";
            }
            $("#tableAllCase > tbody").append(appendHtml);
            
		}, error: function(data){
			
		}
	});
}



function tabClick(tabName){
	var myVar = $("#container").find('li');
	var all = $("#all");
	var path = $("#path");
	$(myVar[0]).removeClass("active");
	$(myVar[1]).removeClass("active");
	$(all).removeClass("show");
	$(all).removeClass("active");
	$(path).removeClass("show");
	$(path).removeClass("active");
	if(tabName == 'ALL'){
		$(myVar[0]).addClass("active");
		$(all).addClass("show");
		$(all).addClass("active");
	}else{
		$(myVar[1]).addClass("active");
		$(path).addClass("show");
		$(path).addClass("active");
		console.log(inferenceByBrokerResult);
		
		var myList = [];
		for (var i = 0; i < inferenceByBrokerResult.firedRules.length; i++) {
			var obj = {};
			obj.id = inferenceByBrokerResult.firedRules[i].id;
			if(inferenceByBrokerResult.firedRules[i].parentId == null){
				obj.parent = '#';
				obj.state = {'opened' : true};
			}else if(inferenceByBrokerResult.firedRules[i].parentId == 0){
				obj.parent = '0';
			}
			else{
				obj.parent = inferenceByBrokerResult.firedRules[i].parentId;
			}
			obj.text = inferenceByBrokerResult.firedRules[i].ruleStatement;
//			obj.state = {'opened' : true};
			myList.push(obj);
			ruleList[inferenceByBrokerResult.firedRules[i].id] = inferenceByBrokerResult.firedRules[i];
		}
		console.log(myList);
		$('#rdrJstreePath').jstree({ 'core' : {
	        'data' : myList
	    } });
	}
}

function btnRule(ruleType){
	
	var checkedConclusion =$(":input:radio[name=tableConclusion]:checked").val();
	
	if (ruleType != "add" && typeof checkedConclusion == "undefined")
	{
		alert("결론을 선택하세요");
		return;
	}
	
	var chkds = $("input[name='radioCase']:checked")[0].value;
	if(true){//chkds == "M" || chkds == "0"){
		var attrAll = $("input[name='radioCase']:checked").parent().parent().find('td');
		var mycase = {}
		for (var i = 2; i < attrAll.length; i++) {
			if($($($("input[name='radioCase']:checked").parent().parent().find('td')[i])[0]).find('input').val() || 
					$($($("input[name='radioCase']:checked").parent().parent().find('td')[i])[0]).find('input').val() == ""){
				if($($($("input[name='radioCase']:checked").parent().parent().find('td')[i])[0]).find('input').val() != ""){
					mycase[caseStructure[i-2].name] = $($($("input[name='radioCase']:checked").parent().parent().find('td')[i])[0]).find('input').val(); 	
				}
			}else if($($($("input[name='radioCase']:checked").parent().parent().find('td')[i])[0]).find('select').val() ||
					$($($("input[name='radioCase']:checked").parent().parent().find('td')[i])[0]).find('select').val() == ""){
				if($($($("input[name='radioCase']:checked").parent().parent().find('td')[i])[0]).find('select').val() != ""){
					mycase[caseStructure[i-2].name] = $($($("input[name='radioCase']:checked").parent().parent().find('td')[i])[0]).find('select').val();
				}
			}else{
				if($($("input[name='radioCase']:checked").parent().parent()).find('td')[i].innerHTML.length > 0){
					mycase[caseStructure[i-2].name] = $($("input[name='radioCase']:checked").parent().parent()).find('td')[i].innerHTML;
				}
			}
//			console.log(i + " = ", $($(data.parentElement.parentElement).find('td')[i])[0].innerHTML);
		}
		// 5번 실행
		console.log(mycase);
		$.ajax({
	    	url: 'delivery/case?userId=' + $("#userId").val(),
			method: "POST", 
			accept: "application/json",
			contentType: "application/json; charset=utf-8",
			dataType: "json",
			async: false,
			data: JSON.stringify({
				caseId : 0,
				data : mycase,
			}),
	    	success: function(data) {
	    		if(data.validity == "valid"){
	    			var timestamp = data.timestamp;
	    			var hrefLink = 'mng?domain=' + $("#domain").val() +
	    			'&userId=' + $("#userId").val() +
	    			'&timestamp=' + timestamp +
	    			'&kaMode=' + ruleType +
	    			'&conclusionId=' + $(":input:radio[name=tableConclusion]:checked").val();
	    			location.href = hrefLink;
	    		}else{
	    			alert(data.msg);
	    		}
	    	}, error: function(data){
	    		
	    	}
	    });
		
		
//		var hrefLink = 'mng?domain=' + $("#domain").val() +
//	  	'&lmbCode=' + $("#lmbCode").val() + 
//		'&receiptDate=' + $("#receiptDate").val() + 
//		'&receiptNo=' + $("#receiptNo").val() +
//		'&testCode=' + $("#testCode").val() +
//		'&specimenCode=' + $("#specimenCode").val() +
//		'&userId=' + $("#userId").val() +
//		'&kaMode=' + ruleType ;
//		
//		if($(":input:radio[name=tableConclusion]:checked").val()){
//			hrefLink += '&conclusionId=' + $(":input:radio[name=tableConclusion]:checked").val();
//		}
//		
//		location.href = hrefLink;
	}else{

		var hrefLink = 'mng?domain=' + $("#domain").val() +
	  	'&lmbCode=' + $("#lmbCode").val() + 
		'&receiptDate=' + $("#receiptDate").val() + 
		'&receiptNo=' + $("#receiptNo").val() +
		'&testCode=' + $("#testCode").val() +
		//'&specimenCode=' + $("#specimenCode").val() +
		'&decisionSeq=' + $("#decisionSeq").val() +
		'&userId=' + $("#userId").val() +
		'&kaMode=' + ruleType ;
		
		if($(":input:radio[name=tableConclusion]:checked").val()){
			hrefLink += '&conclusionId=' + $(":input:radio[name=tableConclusion]:checked").val();
		}
		
		location.href = hrefLink;
		
//		$.ajax({
//	    	url: 'kb/rule?domain=' + $("#domain").val(),
//	    	method: "GET", 
//	    	dataType: "json",
//	    	async: false,
//	    	data: {
//	    	},
//	    	success: function(data) {
//	    		console.log(data);
//	    		var myList = [];
//	    		for (var i = 0; i < data.length; i++) {
//	    			var obj = {};
//	    			obj.id = data[i].id;
//	    			if(data[i].parentId == null){
//	    				obj.parent = '#';
//	    				obj.state = {'opened' : true};
//	    			}else if(data[i].parentId == 0){
//	    				obj.parent = '0';
//	    			}
//	    			else{
//	    				obj.parent = data[i].parentId;
//	    			}
//	    			obj.text = data[i].ruleStatement;
//	    			myList.push(obj);
//	    			ruleList[data[i].id] = data[i];
//				}
//	    		console.log("rdrJstree : ", myList);
//	    		$('#rdrJstree').jstree({ 'core' : {
//			        'data' : myList
//			    } });
//	    	}, error: function(data){
//	    		
//	    	}
//	    });
	}
	
	
}

function commentChange(comment, ruleId){

	if(comment.trim().length > 0){
		 $.ajax({
				url: 'smf/comment?commentCode=' + comment,
				method: "GET", 
				dataType: "json",
				async: false,
				data: {
				},
				success: function(data) {
					console.log(data);
					if(data.length != 0){
						$("#conclusionTXT")[0].innerHTML = data[0].comment.replace(/(\n|\r\n)/g, '<br>');
					}
				}, error: function(data){
					
				}
			});
		
		 //added by ucciri
		 if (ruleId > -1){
			 $.ajax({
					url: 'kb/pathRules?domain=' + $("#domain").val() +
					     '&id=' + ruleId,
					method: "GET", 
					dataType: "json",
					async: false,
					data: {
					},
					success: function(data) {
						console.log(data);
						
					var addconditionSet = [];
				    var conditionSet = {};
			        $("#tableConditionSet > tbody").empty();
				    for (var i = 0; i < data.length; i++) {
				    	for (var j = 0; j < data[i].conditionSet.length; j++) {
				    		conditionSet[data[i].conditionSet[j].attribute 
    					    		   + data[i].conditionSet[j].operator 
					    			    + data[i].conditionSet[j].value] = data[i].conditionSet[j]; 
						}
					}
				      	
				    for ( var i in conditionSet) {
				    	addconditionSet.push(conditionSet[i]);
				    }
				    for (var i = 0; i < addconditionSet.length; i++) {
				    	var appendHtml = '<tr><td>' + addconditionSet[i].attribute + '</td><td>'
			      			+ addconditionSet[i].operator + '</td><td>'
			      			+ addconditionSet[i].value + '</td></tr>'
			      			$("#tableConditionSet > tbody").append(appendHtml)
					}	
	
						
					}, error: function(data){
						$("#tableConditionSet > tbody").empty();
					}
				});
		 }
	}
}
$(document).ready(function(){
    console.log("initialising...");
    console.log($("#domain").val());

    if($("#timestamp").val() != "null"){
    	isTimeStamp = true;
    }
    
    $('#rdrJstree').on("changed.jstree", function (e, data) {
  	  console.log(ruleList[data.selected]);
  	  $("#ruleId").val(data.selected);
  	  $("#ruleCondition").val(ruleList[data.selected].condition);
  	  $("#ruleConclusion").val(ruleList[data.selected].conclusion);
  	  var cornerstoneCaseIds = "";
  	  for (var i = 0; i < ruleList[data.selected].cornerstoneCaseId.length; i++) {
		if(i != ruleList[data.selected].cornerstoneCaseId.length-1){
			cornerstoneCaseIds += ruleList[data.selected].cornerstoneCaseId[i] + ",";
		}else{
			cornerstoneCaseIds += ruleList[data.selected].cornerstoneCaseId[i];
		}
  	  }
  	  $("#stutyCase").val(cornerstoneCaseIds);
  	
  	});
    
    $('#rdrJstreePath').on("changed.jstree", function (e, data) {
  	  console.log(ruleList[data.selected]);
  	  if(ruleList[data.selected]){
  		  $("#ruleId").val(data.selected);
    	  $("#ruleCondition").val(ruleList[data.selected].condition);
    	  $("#ruleConclusion").val(ruleList[data.selected].conclusion);
    	  var cornerstoneCaseIds = "";
	   	  for (var i = 0; i < ruleList[data.selected].cornerstoneCaseId.length; i++) {
	  		if(i != ruleList[data.selected].cornerstoneCaseId.length-1){
	  			cornerstoneCaseIds += ruleList[data.selected].cornerstoneCaseId[i] + ",";
	  		}else{
	  			cornerstoneCaseIds += ruleList[data.selected].cornerstoneCaseId[i];
	  		}
	   	  }
	   	  $("#stutyCase").val(cornerstoneCaseIds);
  	  }
  	  
  	});
    $.ajax({
    	url: 'kb/domain',
    	method: "GET", 
    	dataType: "json",
    	async: false,
    	data: {
    	},
    	success: function(data) {
    		console.log("도메인정보 : ", data);
    		for (var i = 0; i < data.length; i++) {
				if(data[i].domain == $("#domain").val()){
					myDomain = data[i];
				}
			}
    		
    		if(myDomain.reasoner == "SCRDR"){
//    			$("#btnAddRule").css("display", "none");
    			$("#btnAddRule").attr("disabled",true);
    			
    		}else{
//    			$("#btnAddRule").css("display", "block");
    			$("#btnAddRule").attr("disabled",false);
    		}
    		
    		
    	}, error: function(data){
    		
    	}
    });
    
    $.ajax({
    	url: 'kb/rule?domain=' + $("#domain").val(),
    	method: "GET", 
    	dataType: "json",
    	async: false,
    	data: {
    	},
    	success: function(data) {
    		console.log(data);
    		var myList = [];
    		for (var i = 0; i < data.length; i++) {
    			var obj = {};
    			obj.id = data[i].id;
    			if(data[i].parentId == null){
    				obj.parent = '#';
    				obj.state = {'opened' : true};
    			}else if(data[i].parentId == 0){
    				obj.parent = '0';
    			}
    			else{
    				obj.parent = data[i].parentId;
    			}
    			obj.text = data[i].ruleStatement;
    			myList.push(obj);
    			ruleList[data[i].id] = data[i];
			}
    		console.log("rdrJstree : ", myList);
    		$('#rdrJstree').jstree({ 'core' : {
		        'data' : myList
		    } });
    	}, error: function(data){
    		
    	}
    });
    
	 $.ajax({
	    	url: 'kb/caseStructure?domain=' + $("#domain").val(),
	    	method: "GET", 
	    	dataType: "json",
	    	async: false,
	    	data: {
	    	},
	    	success: function(data) {
	    		console.log(data);
	    		caseStructure = data;
	    	}, error: function(data){
	    		
	    	}
	    });
	 
	 var appendHtml = "";
	 $("#tableCase > thead").empty();
     appendHtml = "<tr><th scope='col'>선택</th>";
     appendHtml += "<th scope='col'>id</th>";
     for (var i = 0; i < caseStructure.length; i++) {
     	appendHtml += "<th scope='col'>" + caseStructure[i].name + "</th>";
		}
     appendHtml += "</tr>";
     console.log(appendHtml);
     $("#tableCase > thead").append(appendHtml);
     
     if($("#lmbCode").val() != "null" 
 		&& $("#receiptDate").val() != "null"
 		&& $("#receiptNo").val() != "null"
 		&& $("#testCode").val() != "null"
 		//&& $("#specimenCode").val() != "null"
 		&& $("#decisionSeq").val() != "null"
 		&& $("#userId").val() != "null"){
    $.ajax({
        url: 'smf/case?domain=' + $("#domain").val() +
        	'&lmbCode=' + $("#lmbCode").val() + 
        	'&receiptDate=' + $("#receiptDate").val() + 
        	'&receiptNo=' + $("#receiptNo").val() +
        	'&testCode=' + $("#testCode").val() +
        	//'&specimenCode=' + $("#specimenCode").val() +
        	'&decisionSeq=' + $("#decisionSeq").val() +
        	'&userId=' + $("#userId").val(),
        method: "GET", 
        dataType: "json",
        async: false,
        success: function(result) {
            console.log(result);
            
            var rowData = [];
            

//            for (var i = 0; i < caseStructure.length; i++) {
//            	appendHtml += '<col class="tableCaseCol">';
//            }
//            $("#tableCase").append(appendHtml);
            
           
            
            appendHtml = "";
            $("#tableCase > tbody").empty();
            appendHtml = "<tr><td><input type='radio' name='radioCase' onclick='getInferenceByJson(this)' value='" + result.caseId + "'></td>";
            appendHtml += "<td>" + result.caseId + "</td>";
            for (var i = 0; i < caseStructure.length; i++) {
            	if(result.data[caseStructure[i].name]){
            		appendHtml += "<td>" + result.data[caseStructure[i].name] + "</td>";
            	}else{
            		appendHtml += "<td class='hideCol'></td>";
            	}
			}
            appendHtml += "</tr>";
            console.log(appendHtml);
            $("#tableCase > tbody").append(appendHtml);

            $($("input[name='radioCase']")[0]).trigger('click');
            nullColhideChange();
        }, error: function(data){
        	
        }
    });
    
    
    	
//    	$.ajax({
//  		  url: 'smf/inference?domain=' + $("#domain").val() +
//  		      	'&lmbCode=' + $("#lmbCode").val() + 
//  		    	'&receiptDate=' + $("#receiptDate").val() + 
//  		    	'&receiptNo=' + $("#receiptNo").val() +
//  		    	'&testCode=' + $("#testCode").val() +
//  		    	'&specimenCode=' + $("#specimenCode").val() +
//  		    	'&userId=' + $("#userId").val(),
//  		  method: "GET", 
//  		  dataType: "json",
//  		  async: false,
//  		  data: {
//  		  },
//  		  success: function(data) {
//  		      	console.log(data);
//  		      	inferenceByBrokerResult = data;
//  		      	$("#tableConclusion > tbody").empty();
//  		      	
//  		      	if(data.validity == "valid"){
//  		      		$("#conclusionTXT")[0].innerHTML = "";
//  		      		var isCheck = "";
//  			      	if(myDomain.reasoner == "SCRDR"){
//  			      		commentChange(inferenceByBrokerResult.inference[0].conclusion);
//  			      		isCheck = "checked";
//  			      	}
//  			      	
//  			      	
//  			      	$("#tableConclusion > tbody").append("<tr>\
//   						<td><input type='radio' " + isCheck + " name='tableConclusion' value=" 
//       					+ inferenceByBrokerResult.inference[0].conclusionId + " onchange='commentChange(\"" + inferenceByBrokerResult.inference[0].conclusion + "\")'></td>\
//   						<td>" 
//       					+ inferenceByBrokerResult.inference[0].conclusion + "</td>\
//   					</tr>");
//  		      	}
//  		      	
//  		      	
//  		      	
//  	           
//  	            
//  	            
//  	            var addconditionSet = [];
//  		      	var conditionSet = {};
//  		      	$("#tableConditionSet > tbody").empty();
//  		      	for (var i = 0; i < data.firedRules.length; i++) {
//  		      		for (var j = 0; j < data.firedRules[i].conditionSet.length; j++) {
//  		      			conditionSet[data.firedRules[i].conditionSet[j].attribute 
//  		      				+ data.firedRules[i].conditionSet[j].operator 
//  		      				+ data.firedRules[i].conditionSet[j].value] = data.firedRules[i].conditionSet[j]; 
//  					}
//  				}
//  		      	
//  		      	for ( var i in conditionSet) {
//  		      		addconditionSet.push(conditionSet[i]);
//  		      	}
//  		      	for (var i = 0; i < addconditionSet.length; i++) {
//  		      		var appendHtml = '<tr><td>' + addconditionSet[i].attribute + '</td><td>'
//  	      			+ addconditionSet[i].operator + '</td><td>'
//  	      			+ addconditionSet[i].value + '</td></tr>'
//  	      			$("#tableConditionSet > tbody").append(appendHtml)
//  				}
//  	            
//  	            
//  		  }, error: function(data){
//  		  }
//  	  });
    }else{
    	$.ajax({
	        url: 'delivery/case?timestamp=' + $("#timestamp").val() + 
	        	'&userId=' + $("#userId").val(),
	        method: "GET", 
	        dataType: "json",
	        async: false,
	        success: function(result) {
	            console.log(result);
	
	            $("#tableCase > tbody").empty();
	            var rowData = [];
				var appendHtml = "";
				
				appendHtml = "";
				appendHtml += "<tr><td><input type='radio' name='radioCase' onclick='getInferenceByJson(this)' value='" + result.caseId + "'></td>";
				appendHtml += "<td>" + result.caseId + "</td>";
				for (var i = 0; i < caseStructure.length; i++) {
					if(result.data[caseStructure[i].name]){
						appendHtml += "<td>" + result.data[caseStructure[i].name] + "</td>";
					}else{
						appendHtml += "<td class='hideCol'></td>";
					}
				}
				appendHtml += "</tr>";
				console.log(appendHtml);
				$("#tableCase > tbody").append(appendHtml);
				$($("input[name='radioCase']")[0]).trigger('click');
	        }, error: function(data){
	        	
	        }
	    });
    }
    
	  
    
});