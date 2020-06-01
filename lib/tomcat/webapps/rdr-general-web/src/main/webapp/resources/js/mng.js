var myDomain = {};
var myCase = {};
var operators = {};
var myConditons = [];
var tableConditionSet = [];
var myTableCornerCase = [];

var caseStructure = {};
var inferenceByBrokerResult = "";
var ruleList = {};
var isTimeStamp = false;

var prevCondValue = ""; //added by ucciri

function searchTextKeyDown(){
	var searchText = $("#searchText").val();
	
	var tableList = $("#tableConclusion > tbody")[0];
	
	for (var i = 0; i < $(tableList).find('tr').length; i++) {
		$($(tableList).find('tr')[i]).css("display","none");
		if($($(tableList).find('tr')[i]).find('td')[1].innerHTML.toLowerCase().indexOf(searchText.toLowerCase()) !== -1){
			$($(tableList).find('tr')[i]).css("display","");
		}else{
			$($(tableList).find('tr')[i]).css("display","none");
		}
	}
	
	
	console.log(searchText);
}

function nullColhideChange(){
	var checked = $("#nullColhide")[0].checked;
	console.log(checked);
	var thead = $($("#tableCornerCase > thead")[0]).find('th');
	var tbody = $("#tableCornerCase > tbody > tr");
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

function showDiff(){
	var flag = $("#diff")[0].checked;
	console.log(flag);
	var attrAll = $("#tableCase").find('tr');
	if(flag){
		for (var i = 2; i < attrAll.length+1; i++) {
			if($("#attr" + (i-2))[0].innerHTML != $("#coner" + (i-2))[0].innerHTML){
				$($("#attr" + (i-2))[0]).parent().css('background-color', "beige");
			}else{
				$($("#attr" + (i-2))[0]).parent().css('display', "none");
			}
		}
	}else{
		for (var i = 2; i < attrAll.length+1; i++) {
			if($("#attr" + (i-2))[0].innerHTML != $("#coner" + (i-2))[0].innerHTML){
				$($("#attr" + (i-2))[0]).parent().css('display', "");
				$($("#attr" + (i-2))[0]).parent().css('background-color', "beige");
			}else{
				$($("#attr" + (i-2))[0]).parent().css('display', "");
				$($("#attr" + (i-2))[0]).parent().css('background-color', "white");
			}
		}
	}
}

function cornerCommentChange(comment){
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
					$("#cornerConclusionTXT")[0].innerHTML = data[0].comment.replace(/(\n|\r\n)/g, '<br>');
				}, error: function(data){
					
				}
			});
	}
}


function getInferenceByJson(data){
	console.log("getInferenceByJson : ", data);
	var attrAll = $(data.parentElement.parentElement).find('td');
	var caseId = $(data.parentElement.parentElement).find('td')[1].innerHTML;
	var mycase = {}
	for (var i = 2; i < attrAll.length; i++) {
		if($($($(data.parentElement.parentElement).find('td')[i])[0]).find('input').val()){
			mycase[caseStructure[i-2].name] = $($($(data.parentElement.parentElement).find('td')[i])[0]).find('input').val(); 	
			$("#coner" + (i-2))[0].innerHTML = $($($(data.parentElement.parentElement).find('td')[i])[0]).find('input').val();
		}else{
			mycase[caseStructure[i-2].name] = $($(data.parentElement.parentElement).find('td')[i])[0].innerHTML
			$("#coner" + (i-2))[0].innerHTML = $($(data.parentElement.parentElement).find('td')[i])[0].innerHTML;
		}
		if($("#attr" + (i-2))[0].innerHTML != $("#coner" + (i-2))[0].innerHTML){
			$($("#attr" + (i-2))[0]).parent().css('background-color', "beige");
		}else{
			$($("#attr" + (i-2))[0]).parent().css('background-color', "white");
		}
	}
	console.log(caseId);
    $("#tableCornerConclusion > tbody").empty();
	for (var i = 0; i < myTableCornerCase.otherConclusions.length; i++) {
		if(myTableCornerCase.otherConclusions[i].caseId == caseId){
			for (var j = 0; j < myTableCornerCase.otherConclusions[i].conclusions.length; j++) {
				var isCheck = "";
				if(j == 0){
					cornerCommentChange(myTableCornerCase.otherConclusions[i].conclusions[0].conclusion);
					isCheck = "checked";
				}
		      	
				$("#tableCornerConclusion > tbody").append("<tr>\
						<td><input type='radio' " + isCheck + " name='tableCornerConclusion' value=" 
					+ "" + " onchange='cornerCommentChange(\"" + myTableCornerCase.otherConclusions[i].conclusions[j].conclusion + "\")'></td>" 
					+ "<td>" + myTableCornerCase.otherConclusions[i].conclusions[j].conclusion + "</td>"
					+ "<td>" + myTableCornerCase.otherConclusions[i].conclusions[j].status + "</td>\
					</tr>");
			}
		}
	}
	
	
	
	
	
//	$.ajax({
//        url: 'inference/JSON?domain=' + $("#domain").val(),
//        method: "PUT", 
//        dataType: "json",
//		accept: "application/json",
//		contentType: "application/json; charset=utf-8",
//        data: JSON.stringify(mycase),
//        async: false,
//        success: function(data) {
//            console.log(data);
//            $("#tableCornerConclusion > tbody").empty();
//		      	
//	      	if(data.validity == "valid" && data.inference.length > 0 &&
//	      			data.inference[0].conclusion.length > 0){
//	      		var isCheck = "";
//		      	if(myDomain.reasoner == "SCRDR"){
//		      		cornerCommentChange(data.inference[0].conclusion);
//		      		isCheck = "checked";
//		      	}
//		      	
//		      	for (var i = 0; i < data.inference.length; i++) {
//		      		$("#tableCornerConclusion > tbody").append("<tr>\
//							<td><input type='radio' " + isCheck + " name='tableCornerConclusion' value=" 
//						+ data.inference[i].conclusionId + " onchange='cornerCommentChange(\"" + data.inference[i].conclusion + "\")'></td>\
//							<td>" 
//						+ data.inference[i].conclusion + "</td>\
//						</tr>");
//				}
//	      	}
////
////            var addconditionSet = [];
////	      	var conditionSet = {};
////            $("#tableConditionSet > tbody").empty();
////	      	for (var i = 0; i < data.firedRules.length; i++) {
////	      		for (var j = 0; j < data.firedRules[i].conditionSet.length; j++) {
////	      			conditionSet[data.firedRules[i].conditionSet[j].attribute 
////	      				+ data.firedRules[i].conditionSet[j].operator 
////	      				+ data.firedRules[i].conditionSet[j].value] = data.firedRules[i].conditionSet[j]; 
////				}
////			}
////	      	
////	      	for ( var i in conditionSet) {
////	      		addconditionSet.push(conditionSet[i]);
////	      	}
////	      	for (var i = 0; i < addconditionSet.length; i++) {
////	      		var appendHtml = '<tr><td>' + addconditionSet[i].attribute + '</td><td>'
////      			+ addconditionSet[i].operator + '</td><td>'
////      			+ addconditionSet[i].value + '</td></tr>'
////      			$("#tableConditionSet > tbody").append(appendHtml)
////			}
//		      	
//		      	
//        }, error: function(data){
//        	
//        }
//    });
}
function resultSearch(){
	if (confirm("모든 입력내용을 취소하시겠습니까?")) {
		console.log(myCase);
		if(isTimeStamp){
			var hrefLink = 'search?domain=' + $("#domain").val() +
			'&userId=' + $("#userId").val() +
			'&timestamp=' + $("#timestamp").val();
			location.href = hrefLink;
		}else{
			var hrefLink = 'search?domain=' + $("#domain").val() +
			'&lmbCode=' + $("#lmbCode").val() +
			'&receiptNo=' + $("#receiptNo").val() +
			'&testCode=' + $("#testCode").val() +
			//'&specimenCode=' + $("#specimenCode").val() +
			'&decisionSeq=' + $("#decisionSeq").val() +
			'&userId=' + $("#userId").val() +
			'&kaMode=' + $("#kaMode").val() +
			'&conclusionId=' + $("#conclusionId").val() +
			'&receiptDate=' + $("#receiptDate").val();
			location.href = hrefLink;
			
//			$.ajax({
//		    	url: 'delivery/case?userId=' + $("#userId").val(),
//				method: "POST", 
//				accept: "application/json",
//				contentType: "application/json; charset=utf-8",
//				dataType: "json",
//				async: false,
//				data: JSON.stringify({
//					caseId : 0,
//					data : myCase,
//				}),
//		    	success: function(data) {
//		    		if(data.validity == "valid"){
//		    			var timestamp = data.timestamp;
//		    			var hrefLink = 'search?domain=' + $("#domain").val() +
//		    			'&userId=' + $("#userId").val() +
//		    			'&timestamp=' + timestamp;
//		    			location.href = hrefLink;
//		    		}else{
//		    			alert(data.msg);
//		    		}
//		    	}, error: function(data){
//		    		
//		    	}
//		    });
		}
	}else{
		
	}
}

function btnDel(e){
	console.log(e);
	e.parentElement.parentElement.remove();
}

function addCondition(obj){
	console.log(obj);
	
	var isExistCondition = false;
	
	var condition = $("#tableConditionSet > tbody");
	for (var i = 0; i < condition.find('tr').length; i++) {
		var con = {};
		con["attributeName"] = $($(condition).find('tr')[i]).find('td')[0].innerHTML;
		con["operator"] = $($($(condition).find('tr')[i]).find('td')[1]).find('select').val();
		con["value"] = $($($(condition).find('tr')[i]).find('td')[2]).find('select').val() || $($($(condition).find('tr')[i]).find('td')[2]).find('input').val();
		if(con.attributeName == $(obj).find('td')[0].innerHTML && con.operator == "==" && con.value == $(obj).find('td')[2].innerHTML){
			isExistCondition = true;
			break;
		}
	}
	
	if(isExistCondition){
		alert("중복 조건이 존재합니다.");
	}else{
		$("#tableConditionSet > tbody").append("<tr>\
				<td name='attr'>" + $(obj).find('td')[0].innerHTML + "</td>\
				<td name='oper'>" + returnOper($(obj).find('td')[0].innerHTML, "==") + "</td>\
				<td name='value'>" + returnCaseStructure($(obj).find('td')[0].innerHTML, $(obj).find('td')[2].innerHTML) + "</td>\
				<td><a href='#' onclick='btnDel(this)' class='btn btn-info'>삭제</a></td>\
			</tr>");
	}

}

function getCaseStructure(name){
	for (var i = 0; i < caseStructure.length; i++) {
		if(caseStructure[i].name == name){
			return caseStructure[i];
		}
	}
}
function returnOper(name, value){
	var returnHtml = "";
	var oper = {};
	var myCaseStru = {};
	
	for (var i = 0; i < caseStructure.length; i++) {
		if(caseStructure[i].name == name){
			oper = operators[caseStructure[i].type];
			break;
		}
	}
	
	returnHtml = '<select class="form-control" class="oper" style="">';
	for (var i = 0; i < oper.length; i++) {
		if(value == oper[i]){
			returnHtml += '<option value="' + oper[i] + '" selected>' + oper[i] + '</option>'; 	
		}else{
			returnHtml += '<option value="' + oper[i] + '">' + oper[i] + '</option>'; 	
		}
	}
	returnHtml += '</select>';
	return returnHtml;
}

//added by ucciri
function changeValue(obj)
{
    //alert(obj.value);
    if (obj.value == "직접입력")
    {
	    var trow = obj.parentElement.parentElement
	    var rowIndex = trow.sectionRowIndex;
	    
	    var condition = $("#tableConditionSet > tbody");
	    //var condValue = $($($(condition).find('tr')[rowIndex]).find('td')[2]).find('select').val();
	    //alert(condValue);
	    $($(condition).find('tr')[rowIndex]).find('td')[2].innerHTML = "<input type='text' style='width:100%' value=" + prevCondValue + ">";
    }
}

//added by ucciri
function focusValue(obj)
{
	prevCondValue = obj.value;
	//alert(prev);
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
		//modified by ucciri
		returnHtml = '<select class="form-control" style="width:100%" class="name"' + stru.id + ' style="" onChange="changeValue(this)" onFocus="focusValue(this)">';
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
		returnHtml += '<option value="직접입력"> 직접입력 </option>';
		returnHtml += '</select>';
	}

	else if(stru.type == "TEXT"){
		returnHtml = "<input type='text' style='width:100%' value=" + value + ">";
	}
	
	else {
		returnHtml = "<input type='text' style='width:100%' value=" + value + ">";
	}
	
	return returnHtml;
}

function getValidationCasesBatch(){
	$("#tableCornerConclusion > tbody").empty();
	$("#cornerConclusionTXT")[0].innerHTML = "";
	var condition = $("#tableConditionSet > tbody");
	myConditons = [];
	for (var i = 0; i < $(condition[0]).find('tr').length; i++) {
		var con = {};
		con["attributeName"] = $($(condition[0]).find('tr')[i]).find('td')[0].innerHTML;
		con["operator"] = $($($(condition[0]).find('tr')[i]).find('td')[1]).find('select').val();
		con["value"] = $($($(condition[0]).find('tr')[i]).find('td')[2]).find('select').val() || $($($(condition[0]).find('tr')[i]).find('td')[2]).find('input').val();
		myConditons.push(con);
	}
	
	var myValidationData = {};
	myValidationData["case"] = myCase;
	if(myDomain.reasoner == "SCRDR"){
		// SCRDR은 setting하지 않음
		myValidationData["conclusionId"] = null;		
	}else{
		if($("#kaMode").val() == "add"){
			//add는 setting하지 않아도 됨
			myValidationData["conclusionId"] = null;		
		}else{
			//delete, edit, alter mode는 추론결과조회 화면에서 선택한 결론의 id (url 에 포함)
			myValidationData["conclusionId"] = $("#conclusionId").val();
		}
	}
	myValidationData["kaMode"] = $("#kaMode").val();
	myValidationData["selectedConclusion"] = $(":input:radio[name=tableConclusion]:checked").val();
	myValidationData["condition"] = myConditons;
	
	
	$.ajax({
		url: 'kax/validation?domain=' + $("#domain").val(),
		method: "POST", 
		accept: "application/json",
		contentType: "application/json; charset=utf-8",
		dataType: "json",
		async: false,
		data: JSON.stringify(myValidationData),
		success: function(data) {
			console.log("getValidationCasesBatch : ", data);
			myTableCornerCase = data;
			$("#tableCornerCase > tbody").empty();
			if(data.validity == "valid"){
				if(data.count > 0){
					for (var i = 0; i < data.validatingCases.length; i++) {
						var appendHtml = "<tr><td><input type='radio' name='radioCase' onclick='getInferenceByJson(this)' value='" + data.validatingCases[i].caseId + "'></td>";
						appendHtml += "<td>" + data.validatingCases[i].caseId + "</td>";
						for (var j = 0; j < caseStructure.length; j++) {
							if(data.validatingCases[i].data[caseStructure[j].name]){
								appendHtml += "<td>" + data.validatingCases[i].data[caseStructure[j].name] + "</td>";
							}else{
								appendHtml += "<td class='hideCol'></td>";
							}
						}
						appendHtml += "</tr>";
						$("#tableCornerCase > tbody").append(appendHtml);
					}
					$('input[name="radioCase"]')[0].click();
		            
		            
					alert(data.count + "개의 충돌사례가 존재합니다.");
			      	nullColhideChange();
				}else{
					if(isTimeStamp){
						$.ajax({
					        url: 'delivery/case?timestamp=' + $("#timestamp").val() + 
					        	'&userId=' + $("#userId").val(),
					        method: "GET", 
					        dataType: "json",
					        async: false,
					        success: function(result) {
					            console.log(result);
					
					            $("#tableCase > tbody").empty();
					            var appendHtml = "";
					            for (var i = 0; i < caseStructure.length; i++) {
					            	appendHtml += "<tr ondblclick='addCondition(this)';>" + 
									"<td>" + caseStructure[i].name + "</td>\
									<td>" + caseStructure[i].type + "</td>";
					
					            	var isContain = false;
					            	for (var key in result.data) {
					            		if( caseStructure[i].name == key ){
					            			appendHtml += "<td id='attr" + i + "'>" + result.data[key] + "</td>";
					            			isContain = true;
					            			break;
					            		}
									}
					            	if(!isContain){
					            		appendHtml += "<td id='attr" + i + "'></td>";
					            	}
					            	appendHtml += "<td id=coner" + i + "></td></tr>";
								}
					            $("#tableCase > tbody").append(appendHtml);
					            myCase = result.data;
						      	console.log(myCase);
					        }, error: function(data){
					        	
					        }
					    });
					}else{
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

			      	            $("#tableCase > tbody").empty();
			      	            var appendHtml = "";
			      	            for (var i = 0; i < caseStructure.length; i++) {
			      	            	appendHtml += "<tr ondblclick='addCondition(this)';>" + 
			      					"<td>" + caseStructure[i].name + "</td>\
			      					<td>" + caseStructure[i].type + "</td>";

			      	            	var isContain = false;
			      	            	for (var key in result.data) {
			      	            		if( caseStructure[i].name == key ){
			      	            			appendHtml += "<td id='attr" + i + "'>" + result.data[key] + "</td>";
			      	            			isContain = true;
			      	            			break;
			      	            		}
			      					}
			      	            	if(!isContain){
			      	            		appendHtml += "<td id='attr" + i + "'></td>";
			      	            	}
			      	            	appendHtml += "<td id=coner" + i + "></td></tr>";
			      				}
			      	            $("#tableCase > tbody").append(appendHtml);
			      	            
			      	            myCase = result.data;
			      		      	console.log(myCase);
			      	        }, error: function(data){
			      	        	
			      	        }
			      	    });
					}
					
					
					
					alert("충돌 사례가 없습니다.");
					
//					$.ajax({
//				        url: 'smf/case?domain=' + $("#domain").val() +
//				        	'&lmbCode=' + $("#lmbCode").val() + 
//				        	'&receiptDate=' + $("#receiptDate").val() + 
//				        	'&receiptNo=' + $("#receiptNo").val() +
//				        	'&testCode=' + $("#testCode").val() +
//				        	'&specimenCode=' + $("#specimenCode").val() +
//				        	'&userId=' + $("#userId").val(),
//				        method: "GET", 
//				        dataType: "json",
//				        async: false,
//				        success: function(result) {
//				            console.log(result);
//
//				            $("#tableCase > tbody").empty();
//				            var appendHtml = "";
//				            for (var i = 0; i < caseStructure.length; i++) {
//				            	appendHtml += "<tr ondblclick='addCondition(this)';>" + 
//								"<td>" + caseStructure[i].name + "</td>\
//								<td>" + caseStructure[i].type + "</td>";
//
//				            	var isContain = false;
//				            	for (var key in result.data) {
//				            		if( caseStructure[i].name == key ){
//				            			appendHtml += "<td id='attr" + i + "'>" + result.data[key] + "</td>";
//				            			isContain = true;
//				            			break;
//				            		}
//								}
//				            	if(!isContain){
//				            		appendHtml += "<td id='attr" + i + "'></td>";
//				            	}
//				            	appendHtml += "<td id=coner" + i + "></td></tr>";
////				            	$("#tableCase > tbody").append(
////										<td></td>\
////										<td></td>\
////									</tr>");
//							}
//				            $("#tableCase > tbody").append(appendHtml);
//				            
////				            for (var key in result.data) {
////				                $("#tableCase > tbody").append("<tr onclick='addCondition(\"" + key + '","' + result.data[key] + "\")';>" + 
////										"<td>" + key + "</td>\
////										<td>" + getCaseStructure(key).type +"</td>\
////										<td>" + result.data[key] + "</td>\
////										<td></td>\
////									</tr>");
////				            }
//				            
//				            myCase = result.data;
//					      	console.log(myCase);
//					      	
//				        }, error: function(data){
//				        	
//				        }
//				    });
					
				}
			}else{
//				$("#addRuleBatch").attr("disabled",true);
				alert("지식학습시 에러가 발생했습니다.[" + data.msg + "]");
			}
		}, error: function(data){
			
		}
	});
	
}

function btnAddRuleBatch(){
	var condition = $("#tableConditionSet > tbody");
	myConditons = [];
	for (var i = 0; i < $(condition[0]).find('tr').length; i++) {
		var con = {};
		con["attributeName"] = $($(condition[0]).find('tr')[i]).find('td')[0].innerHTML;
		con["operator"] = $($($(condition[0]).find('tr')[i]).find('td')[1]).find('select').val();
		con["value"] = $($($(condition[0]).find('tr')[i]).find('td')[2]).find('select').val() || $($($(condition[0]).find('tr')[i]).find('td')[2]).find('input').val();
		myConditons.push(con);
	}
	
	var myValidationData = {};
	myValidationData["case"] = myCase;
	if(myDomain.reasoner == "SCRDR"){
		// SCRDR은 setting하지 않음
		myValidationData["conclusionId"] = null;		
	}else{
		if($("#kaMode").val() == "add"){
			//add는 setting하지 않아도 됨
			myValidationData["conclusionId"] = null;		
		}else{
			//delete, edit, alter mode는 추론결과조회 화면에서 선택한 결론의 id (url 에 포함)
			myValidationData["conclusionId"] = $("#conclusionId").val();
		}
	}
	myValidationData["kaMode"] = $("#kaMode").val();
	myValidationData["selectedConclusion"] = $(":input:radio[name=tableConclusion]:checked").val();
	myValidationData["condition"] = myConditons;
	
	
	$.ajax({
		url: 'kax/validation?domain=' + $("#domain").val(),
		method: "POST", 
		accept: "application/json",
		contentType: "application/json; charset=utf-8",
		dataType: "json",
		async: false,
		data: JSON.stringify(myValidationData),
		success: function(data) {
			console.log("addRuleBatch : ", data);
			if(data.validity == "valid"){
				if(data.count > 0){
					if (confirm(data.count + "개의 충돌사례가 존재합니다. 충돌을 무시하고 저장하시겠습니까?")) {
						$.ajax({
							url: 'kax/rule?domain=' + $("#domain").val(),
							method: "POST", 
							accept: "application/json",
							contentType: "application/json; charset=utf-8",
							dataType: "json",
							async: false,
							data: JSON.stringify(myValidationData),
							success: function(data) {
								console.log("addRuleBatch : ", data);
								if(data.validity == "valid"){
									if (confirm("저장하시겠습니까?")) {
										alert("정상 등록되었습니다.");
										window.location.href=window.location.href;
									} else {
										
									}
								}else{
									alert("지식학습시 에러가 발생했습니다.[" + data.msg + "]");
								}
							}, error: function(data){
								
							}
						});
					} else {
						
					}
				}else{
					if (confirm("저장하시겠습니까?")) {
						$.ajax({
							url: 'kax/rule?domain=' + $("#domain").val(),
							method: "POST", 
							accept: "application/json",
							contentType: "application/json; charset=utf-8",
							dataType: "json",
							async: false,
							data: JSON.stringify(myValidationData),
							success: function(data) {
								console.log("addRuleBatch : ", data);
								if(data.validity == "valid"){
									alert("정상 등록되었습니다.");
									window.location.href=window.location.href;
								}else{
									alert("지식학습시 에러가 발생했습니다.[" + data.msg + "]");
								}
							}, error: function(data){
								
							}
						});
					} else {
						
					}
				}
			}else{
				alert("지식학습시 에러가 발생했습니다.[" + data.msg + "]");
			}
		}, error: function(data){
			
		}
	});
	
	

}

function commentChange(comment){
	 $.ajax({
		url: 'smf/comment?commentCode=' + comment,
		method: "GET", 
		dataType: "json",
		async: false,
		data: {
		},
		success: function(data) {
			console.log(data);
			$("#conclusionTXT")[0].innerHTML = "";
			$("#conclusionTXT")[0].innerHTML = data[0].comment.replace(/(\n|\r\n)/g, '<br>');;
//			$($(":input:radio[name=tableConclusion]:checked").parent().parent()[0]).css("background-color","red");
			var tableConclusionName = document.getElementsByName("tableConclusion");
			for (var i = 0; i < tableConclusionName.length; i++) {
				$(tableConclusionName[i]).parent().parent().removeClass("selected");
			}
			$($("#conclusion" + comment).parent().parent()[0]).addClass("selected");
			if($("#conclusion" + comment)[0]){
				$("#conclusion" + comment)[0].checked = true;
			}
		}, error: function(data){
			
		}
	});
}

function getSuggestedConditions(bConfirm){
	if (bConfirm == false || (bConfirm && confirm("조건을 자동생성 하시겠습니까?"))) {

	    if($("#domain").val() != "null" && $("#timestamp").val() != "null"){
	    	$.ajax({
	    		url: 'smf/suggestedConditions?domain=' + $("#domain").val() +
	    		'&kaMode=' + $("#kaMode").val() +
	    		'&conclusionId=' + $("#conclusionId").val() +
		    	'&userId=' + $("#userId").val(),
	            method: "PUT", 
	            dataType: "json",
	    		accept: "application/json",
	    		contentType: "application/json; charset=utf-8",
	            data: JSON.stringify(myCase),
	            async: false,
	            success: function(data) {
	                console.log(data);
		            $("#tableConditionSet > tbody").empty();
		            for (var i = 0; i <  data.length; i++) {
		            	$("#tableConditionSet > tbody").append("<tr>\
								<td name='attr'>" + data[i].attribute + "</td>\
								<td name='oper'>" + returnOper(data[i].attribute, data[i].operator) + "</td>\
								<td name='value'>" + returnCaseStructure(data[i].attribute, data[i].value) + "</td>\
								<td><a href='#' onclick='btnDel(this)' class='btn btn-info'>삭제</a></td>\
							</tr>");
					}
		            
			  }, error: function(data){
				  
			  }
		  });
	    	
	    }else{
	    	$.ajax({
				  url: 'smf/suggestedConditions?domain=' + $("#domain").val() +
				      	'&lmbCode=' + $("#lmbCode").val() + 
				    	'&receiptDate=' + $("#receiptDate").val() + 
				    	'&receiptNo=' + $("#receiptNo").val() +
				    	'&testCode=' + $("#testCode").val() +
				    	//'&specimenCode=' + $("#specimenCode").val() +
				    	'&decisionSeq=' + $("#decisionSeq").val() +
				    	'&kaMode=' + $("#kaMode").val() +
			    		'&conclusionId=' + $("#conclusionId").val() +
				    	'&userId=' + $("#userId").val(),
				  method: "GET", 
				  dataType: "json",
				  async: false,
				  data: {
				  },
				  success: function(data) {
					  	console.log(data);
			            $("#tableConditionSet > tbody").empty();
			            for (var i = 0; i <  data.length; i++) {
			            	$("#tableConditionSet > tbody").append("<tr>\
									<td name='attr'>" + data[i].attribute + "</td>\
									<td name='oper'>" + returnOper(data[i].attribute, data[i].operator) + "</td>\
									<td name='value'>" + returnCaseStructure(data[i].attribute, data[i].value) + "</td>\
									<td><a href='#' onclick='btnDel(this)' class='btn btn-info'>삭제</a></td>\
								</tr>");
						}
			            
				  }, error: function(data){
					  
				  }
			  });
	    }
	}else{
		
	}
}

$(document).ready(function(){
    console.log("initialising...");
    
    $("#selectKaMode").val($("#kaMode").val());
    if($("#timestamp").val() != "null"){
    	isTimeStamp = true;
    }
    
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
					myDomain = data[i];
				}
			}
    	}, error: function(data){
    		
    	}
    });
    
    $.ajax({
    	url: 'kb/operator?domain=' + $("#domain").val(),
    	method: "GET", 
    	dataType: "json",
    	async: false,
    	data: {
    	},
    	success: function(data) {
    		console.log("도메인정보 : ", data);
    		operators = data ;
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
    		console.log("caseStructure : ", data);
    		caseStructure = data; 
    		$("#tableCornerCase > thead").empty();
            appendHtml = "<tr><th scope='col'>선택</th>";
            appendHtml += "<th scope='col'>id</th>";
            for (var i = 0; i < caseStructure.length; i++) {
            	appendHtml += "<th scope='col'>" + caseStructure[i].name + "</th>";
			}
            appendHtml += "</tr>";
            console.log(appendHtml);
            $("#tableCornerCase > thead").append(appendHtml);
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
//    			obj.state = {'opened' : true};
    			myList.push(obj);
    			ruleList[data[i].id] = data[i];
			}
    		console.log(myList);
    		$('#rdrJstree').jstree({ 'core' : {
		        'data' : myList
		    } });
    	}, error: function(data){
    		
    	}
    });
    
   
    
   
    if(isTimeStamp){
    	// 5번 호출시
		 $.ajax({
	        url: 'delivery/case?timestamp=' + $("#timestamp").val() + 
	        	'&userId=' + $("#userId").val(),
	        method: "GET", 
	        dataType: "json",
	        async: false,
	        success: function(result) {
	            console.log(result);
	
	            $("#tableCase > tbody").empty();
	            var appendHtml = "";
	            for (var i = 0; i < caseStructure.length; i++) {
	            	appendHtml += "<tr ondblclick='addCondition(this)';>" + 
					"<td>" + caseStructure[i].name + "</td>\
					<td>" + caseStructure[i].type + "</td>";
	
	            	var isContain = false;
	            	for (var key in result.data) {
	            		if( caseStructure[i].name == key ){
	            			appendHtml += "<td id='attr" + i + "'>" + result.data[key] + "</td>";
	            			isContain = true;
	            			break;
	            		}
					}
	            	if(!isContain){
	            		appendHtml += "<td id='attr" + i + "'></td>";
	            	}
	            	appendHtml += "<td id=coner" + i + "></td></tr>";
				}
	            $("#tableCase > tbody").append(appendHtml);
	            myCase = result.data;
		      	console.log(myCase);
	        }, error: function(data){
	        	
	        }
	    });
		 
		if ($("#kaMode").val() == "add")
		{
			$("#tableCondition > tbody").empty();
		}
		else
		{
			$.ajax({
		       url: 'inference/JSON?domain=' + $("#domain").val(),
		       method: "PUT", 
		       dataType: "json",
				accept: "application/json",
				contentType: "application/json; charset=utf-8",
		       data: JSON.stringify(myCase),
		       async: false,
		       success: function(data) {
		           console.log(data);
	//	           $("#tableCornerConclusion > tbody").empty();
	//			      	
	//		      	if(data.validity == "valid" && data.inference.length > 0 &&
	//		      			data.inference[0].conclusion.length > 0){
	//		      		var isCheck = "";
	//			      	if(myDomain.reasoner == "SCRDR"){
	//			      		cornerCommentChange(data.inference[0].conclusion);
	//			      		isCheck = "checked";
	//			      	}
	//			      	
	//			      	for (var i = 0; i < data.inference.length; i++) {
	//			      		$("#tableCornerConclusion > tbody").append("<tr>\
	//								<td><input type='radio' " + isCheck + " name='tableCornerConclusion' value=" 
	//							+ data.inference[i].conclusionId + " onchange='cornerCommentChange(\"" + data.inference[i].conclusion + "\")'></td>\
	//								<td>" 
	//							+ data.inference[i].conclusion + "</td>\
	//							</tr>");
	//					}
	//		      	}
		           
		           //added by ucciri
		           var ruleId = -1;
		           for (var i = 0; i < data.inference.length; i++) {
		        	   if (i == 0) ruleId = data.inference[i].ruleId;
		        	   if (data.inference[i].conclusionId == $("#conclusionId").val() )
		        		   ruleId = data.inference[i].ruleId;
				   }
		           
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
		  			        $("#tableCondition > tbody").empty();
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
		  			      			$("#tableCondition > tbody").append(appendHtml)
		  					}	
		  	
		  						
		  					}, error: function(data){
		  						$("#tableCondition > tbody").empty();
		  					}
		  				});
		  		 }
	
		           //blocked by ucciri
	//	           var addconditionSet = [];
	//		      	var conditionSet = {};
	//	           $("#tableCondition > tbody").empty();
	//		      	for (var i = 0; i < data.firedRules.length; i++) {
	//		      		for (var j = 0; j < data.firedRules[i].conditionSet.length; j++) {
	//		      			conditionSet[data.firedRules[i].conditionSet[j].attribute 
	//		      				+ data.firedRules[i].conditionSet[j].operator 
	//		      				+ data.firedRules[i].conditionSet[j].value] = data.firedRules[i].conditionSet[j]; 
	//					}
	//				}
	//		      	
	//		      	for ( var i in conditionSet) {
	//		      		addconditionSet.push(conditionSet[i]);
	//		      	}
	//		      	for (var i = 0; i < addconditionSet.length; i++) {
	//		      		var appendHtml = '<tr><td>' + addconditionSet[i].attribute + '</td><td>'
	//	     			+ addconditionSet[i].operator + '</td><td>'
	//	     			+ addconditionSet[i].value + '</td></tr>'
	//	     			$("#tableCondition > tbody").append(appendHtml)
	//				}
				      	
				      	
		       }, error: function(data){
		       	
		       }
			});
		}
		
		getSuggestedConditions(false);
//		$.ajax({
//   		url: 'smf/suggestedConditions?domain=' + $("#domain").val() +
//	    	'&userId=' + $("#userId").val(),
//           method: "PUT", 
//           dataType: "json",
//   		accept: "application/json",
//   		contentType: "application/json; charset=utf-8",
//           data: JSON.stringify(myCase),
//           async: false,
//           success: function(data) {
//               console.log(data);
//	            $("#tableConditionSet > tbody").empty();
//	            for (var i = 0; i <  data.length; i++) {
//	            	$("#tableConditionSet > tbody").append("<tr>\
//							<td name='attr'>" + data[i].attribute + "</td>\
//							<td name='oper'>" + returnOper(data[i].attribute, data[i].operator) + "</td>\
//							<td name='value'>" + returnCaseStructure(data[i].attribute, data[i].value) + "</td>\
//							<td><a href='#' onclick='btnDel(this)' class='btn btn-info'>삭제</a></td>\
//						</tr>");
//				}
//	            
//		  }, error: function(data){
//			  
//		  }
//	  });
    }else{
    	$.ajax({
     		  url: 'smf/inference?domain=' + $("#domain").val() +
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
     		  data: {
     		  },
     		  success: function(data) {
     		      	console.log("추론결과 : ", data);
     		      	inferenceByBrokerResult = data;
     	            $("#thisConclusion").val(inferenceByBrokerResult.inference[0].conclusion);
     		      	
     	       //added by ucciri
     	      var ruleId = -1;
     	      for (var i = 0; i < data.inference.length; i++) {
     	          if (i == 0) ruleId = data.inference[i].ruleId;
     	          if (data.inference[i].conclusionId == $("#conclusionId").val() )
     	        	  ruleId = data.inference[i].ruleId;
     		  }
     	           
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
     	  			        $("#tableCondition > tbody").empty();
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
     	  			      			$("#tableCondition > tbody").append(appendHtml)
     	  					}	
     	  	
     	  						
     	  					}, error: function(data){
     	  						$("#tableCondition > tbody").empty();
     	  					}
     	  				});
     	  		}
     	            
     	            
     	            //blocked by ucciri
//     	            var addconditionSet = [];
//     		      	var conditionSet = {};
//     		      	$("#tableCondition > tbody").empty();
//     		      	for (var i = 0; i < data.firedRules.length; i++) {
//     		      		for (var j = 0; j < data.firedRules[i].conditionSet.length; j++) {
//     		      			conditionSet[data.firedRules[i].conditionSet[j].attribute 
//     		      				+ data.firedRules[i].conditionSet[j].operator 
//     		      				+ data.firedRules[i].conditionSet[j].value] = data.firedRules[i].conditionSet[j]; 
//     					}
//     				}
//     		      	
//     		      	for ( var i in conditionSet) {
//     		      		addconditionSet.push(conditionSet[i]);
//     		      	}
//     		      	for (var i = 0; i < addconditionSet.length; i++) {
//     		      		var appendHtml = '<tr><td>' + addconditionSet[i].attribute + '</td><td>'
//     	      			+ addconditionSet[i].operator + '</td><td>'
//     	      			+ addconditionSet[i].value + '</td></tr>'
//     	      			$("#tableCondition > tbody").append(appendHtml)
//     				}
     		      	
     		  }, error: function(data){
     		  }
     	  });
      	 
    	getSuggestedConditions(false);
//      	 $.ajax({
//     		  url: 'smf/suggestedConditions?domain=' + $("#domain").val() +
//     		      	'&lmbCode=' + $("#lmbCode").val() + 
//     		    	'&receiptDate=' + $("#receiptDate").val() + 
//     		    	'&receiptNo=' + $("#receiptNo").val() +
//     		    	'&testCode=' + $("#testCode").val() +
//     		    	//'&specimenCode=' + $("#specimenCode").val() +
//     		    	'&decisionSeq=' + $("#decisionSeq").val() +
//     		    	'&userId=' + $("#userId").val(),
//     		  method: "GET", 
//     		  dataType: "json",
//     		  async: false,
//     		  data: {
//     		  },
//     		  success: function(data) {
//     			  	console.log(data);
//     	            $("#tableConditionSet > tbody").empty();
//     	            for (var i = 0; i <  data.length; i++) {
//     	            	$("#tableConditionSet > tbody").append("<tr>\
//     							<td name='attr'>" + data[i].attribute + "</td>\
//     							<td name='oper'>" + returnOper(data[i].attribute, data[i].operator) + "</td>\
//     							<td name='value'>" + returnCaseStructure(data[i].attribute, data[i].value) + "</td>\
//     							<td><a href='#' onclick='btnDel(this)' class='btn btn-info'>삭제</a></td>\
//     						</tr>");
//     				}
//     	            
//     		  }, error: function(data){
//     			  
//     		  }
//     	  });
      	 
      	 
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

      	            $("#tableCase > tbody").empty();
      	            var appendHtml = "";
      	            for (var i = 0; i < caseStructure.length; i++) {
      	            	appendHtml += "<tr ondblclick='addCondition(this)';>" + 
      					"<td>" + caseStructure[i].name + "</td>\
      					<td>" + caseStructure[i].type + "</td>";

      	            	var isContain = false;
      	            	for (var key in result.data) {
      	            		if( caseStructure[i].name == key ){
      	            			appendHtml += "<td id='attr" + i + "'>" + result.data[key] + "</td>";
      	            			isContain = true;
      	            			break;
      	            		}
      					}
      	            	if(!isContain){
      	            		appendHtml += "<td id='attr" + i + "'></td>";
      	            	}
      	            	appendHtml += "<td id=coner" + i + "></td></tr>";
      				}
      	            $("#tableCase > tbody").append(appendHtml);
      	            
      	            myCase = result.data;
      		      	console.log(myCase);
      	        }, error: function(data){
      	        	
      	        }
      	    });
    }
    
    
    var myCommentCode = "";
    /// 공통
    if($("#kaMode").val() != "add" && $("#conclusionId").val()){
    	$.ajax({
        	url: 'kb/conclusion/' + $("#conclusionId").val() + "?domain=" + $("#domain").val(),
        	method: "GET", 
        	dataType: "json",
        	async: false,
        	data: {
        	},
        	success: function(data) {
        		console.log(data);
        		myCommentCode = data;
        		$("#thisConclusion").val(myCommentCode.name);
        	}, error: function(data){
        	}
        });
    }
    
    
    $.ajax({
 		  url: 'smf/comment',
 		  method: "GET", 
 		  dataType: "json",
 		  async: false,
 		  data: {
 		  },
 		  success: function(data) {
 		      	console.log(data);
 		      	$("#tableConclusion > tbody").empty();
 		      	var checkedCode = "";
 		      	for (var i = 0; i < data.length; i++) {
 		      		var isChecked = "";
// 		      		if(data[i].commentCode == myCommentCode.name){
//// 		      				|| $("#conclusionId").val() == ){
// 		      			isChecked = "checked";
// 		      			checkedCode = data[i].commentCode;
// 		      		}else{
// 		      			isChecked = "";
// 		      		}
 		      		$("#tableConclusion > tbody").append("<tr onclick='commentChange(\"" + data[i].commentCode + "\")'>\
 							<td><input type='radio'" + isChecked + " name='tableConclusion' id='conclusion" + data[i].commentCode + "' value=" 
     						+ data[i].commentCode + " onchange='commentChange(\"" + data[i].commentCode + "\")'></td>\
 							<td>" 
     						+ data[i].commentCode + "</td>\
 						</tr>");
 				}
// 		      	commentChange(checkedCode);
 		  }, error: function(data){
 		  }
 	  });

});