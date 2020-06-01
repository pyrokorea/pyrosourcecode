/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

//page rendering

/**TABLE**/

function drawConclusionListTable(data, filter) {
    filter = filter.toLowerCase();
    $("#rdr-conclusion-table").html("<tr><th>소견 리스트</th></tr>");
    for (var i = 0; i < data.length; i++) {     
        var compareData = data[i].toLowerCase();
        if(compareData.indexOf(filter)){
            drawTableRow("#rdr-conclusion-table", data[i]);
        }
    }
    activateConclusionTableSelectionEvent();
}

function drawOtherConclusionListTable(otherConclusionList, filter) {
    filter = filter.toLowerCase();
    $("#rdr-ka-validate-case-other-conclusion-list-table").html("<tr><th>소견 리스트</th></tr>");
    for (var i = 0; i < otherConclusionList.length; i++) {    
        var compareData = otherConclusionList[i].toLowerCase(); 
        if(compareData.indexOf(filter)){
            drawTableRow("#rdr-ka-validate-case-other-conclusion-list-table", otherConclusionList[i]);
        }
    }
    activateConclusionTableSelectionEvent();
}

function drawCurrentCaseViewerTable(data, filter) {
    filter = filter.toLowerCase();
    $("#rdr-ka-current-case-viewer-table").html("<tr><th width='100px'>검사코드 ID</th><th width='200px'>검사코드 이름</th><th width='80px'>수치 종류</th><th>수치 값</th></tr>");
//    for(var usedCondition in usedConditionSet) {
//            if(data[i].hasOwnProperty(attrName)) {
//        usedConditionSet[i];
//    }

    var usedConditionIds = new Array();
    
    //put used conditions on the top
    for(var i=0; i<usedConditionSet.length; i++){
        usedConditionIds.push(usedConditionSet[i].attribute);        
    }
    for(var j=0; j<2; j++){
        var isInUsedCondition;
        
        for(var i=0; i<data.length; i++) {
            //if id exists in array
            if(j===0){
                if($.inArray(data[i].attribute, usedConditionIds) >-1) {
                    isInUsedCondition = true;
                } else {
                    isInUsedCondition = false;
                }
            } else {
                if($.inArray(data[i].attribute, usedConditionIds) >-1) {
                    isInUsedCondition = false;
                } else {
                    isInUsedCondition = true;
                }
            }
            var compareData = data[i].attribute.toLowerCase();
            var compareData2 = attrConvertedNameList[data[i].attribute].toLowerCase();
            var isFiltered = false;
            if(compareData.indexOf(filter) || compareData2.indexOf(filter)){
                isFiltered = true;
            }
            if(isFiltered && !isMissingValue("rdr-ka-current-case-viewer-missing-checkbox", data[i].value) && isInUsedCondition ){
                var row = $("<tr />");
                row.append($("<td>" + data[i].attribute + "</td>"));
                row.append($("<td>" + data[i].attrConvertedName + "</td>"));
                row.append($("<td>" + data[i].type + "</td>"));
                row.append($("<td>" + data[i].value + "</td>"));

                $("#rdr-ka-current-case-viewer-table").append(row); 

            }
        }
    }
    
    $("#rdr-ka-current-case-viewer-table").show();
    $("#rdr-ka-current-case-viewer-table-loading").hide();
    activateCurrentCaseTableSelectionEvent();
}

function drawCompareCurrentCaseViewerTable(data, filter) {
    filter = filter.toLowerCase();
    $("#rdr-ka-compare-current-case-viewer-table").html("<tr><th width='110px'>Attribute</th><th>Converted Name</th><th>Type</th><th>Value</th></tr>");
    for (var i=0; i<data.length; i++) {
        var compareData = data[i].attribute.toLowerCase();
        var compareData2 = attrConvertedNameList[data[i].attribute].toLowerCase();
        var isFiltered = false;
        if(compareData.indexOf(filter) || compareData2.indexOf(filter)){
            isFiltered = true;
        }
        if(isFiltered && !isMissingValue("rdr-ka-compare-case-viewer-missing-checkbox", data[i].value)){
            var row = $("<tr />");
            row.append($("<td>" + data[i].attribute + "</td>"));
            row.append($("<td>" + attrConvertedNameList[data[i].attribute] + "</td>"));
            row.append($("<td>" + data[i].type + "</td>"));
            row.append($("<td>" + data[i].value + "</td>"));
            
            $("#rdr-ka-compare-current-case-viewer-table").append(row); 
            
        }
    }
    $("#rdr-ka-compare-current-case-viewer-table").show();
    $("#rdr-ka-compare-current-case-viewer-table-loading").hide();
    activateCompareCurrentCaseTableSelectionEvent();
}

function drawCompareCaseViewerTable(currentData, validateData, filter) {
    filter = filter.toLowerCase();
    $("#rdr-ka-compare-current-case-viewer-table").html("<tr><th width='110px'>Attribute</th><th>Converted Name</th><th>Type</th><th>Value</th></tr>");
    $("#rdr-ka-compare-validate-case-viewer-table").html("<tr><th width='110px'>Attribute</th><th>Converted Name</th><th>Type</th><th>Value</th></tr>");
    var validRow = 0;
    for (var i=0; i<currentData.length; i++) {
        var compareData = currentData[i].attribute.toLowerCase();
        var compareData2 = attrConvertedNameList[currentData[i].attribute].toLowerCase();
        var isFiltered = false;
        if(compareData.indexOf(filter) || compareData2.indexOf(filter)){
            isFiltered = true;
        }
        
        if(!isMissingValue("rdr-ka-compare-case-viewer-missing-checkbox", currentData[i].value) 
                && currentData[i].value !== validateData[i].value )
        {
            if(isFiltered) {
            
                var currentRow = $("<tr />");
                currentRow.append($("<td>" + currentData[i].attribute + "</td>"));
                currentRow.append($("<td>" + attrConvertedNameList[currentData[i].attribute] + "</td>"));
                currentRow.append($("<td>" + currentData[i].type + "</td>"));
                currentRow.append($("<td>" + currentData[i].value + "</td>"));            
                $("#rdr-ka-compare-current-case-viewer-table").append(currentRow); 

                var validateRow = $("<tr />");
                validateRow.append($("<td>" + validateData[i].attribute + "</td>"));
                validateRow.append($("<td>" + attrConvertedNameList[validateData[i].attribute] + "</td>"));
                validateRow.append($("<td>" + validateData[i].type + "</td>"));
                validateRow.append($("<td>" + validateData[i].value + "</td>"));            
                $("#rdr-ka-compare-validate-case-viewer-table").append(validateRow); 
                
            }
            validRow++;
        }
    }    
    $("#rdr-ka-compare-current-case-viewer-table").show();
    $("#rdr-ka-compare-current-case-viewer-table-loading").hide();
    $("#rdr-ka-compare-validate-case-viewer-table").show();
    $("#rdr-ka-compare-validate-case-viewer-table-loading").hide();
    activateCompareCurrentCaseTableSelectionEvent();
    activateCompareValidateCaseTableSelectionEvent();
    
    if(validRow === 0 ){    
        currentStage = 2;
        changeTab("rdr-tab-2");
        alert("There is no different attribute.");
    } 
}

function drawCompareValidateCaseViewerTable(data, filter) {
    filter = filter.toLowerCase();
    $("#rdr-ka-compare-validate-case-viewer-table").html("<tr><th width='110px'>Attribute</th><th>Converted Name</th><th>Type</th><th>Value</th></tr>");
    for (var i=0; i<data.length; i++) {
        var compareData = data[i].attribute.toLowerCase();
        var compareData2 = attrConvertedNameList[data[i].attribute].toLowerCase();
        var isFiltered = false;
        if(compareData.indexOf(filter) || compareData2.indexOf(filter)){
            isFiltered = true;
        }
        if(isFiltered && !isMissingValue("rdr-ka-compare-case-viewer-missing-checkbox", data[i].value)){
            var row = $("<tr />");
            row.append($("<td>" + data[i].attribute + "</td>"));
            row.append($("<td>" + attrConvertedNameList[data[i].attribute] + "</td>"));
            row.append($("<td>" + data[i].type + "</td>"));
            row.append($("<td>" + data[i].value + "</td>"));
            
            $("#rdr-ka-compare-validate-case-viewer-table").append(row); 
            
        }
    }
    $("#rdr-ka-compare-validate-case-viewer-table").show();
    $("#rdr-ka-compare-validate-case-viewer-table-loading").hide();
    activateCompareValidateCaseTableSelectionEvent();
}

function drawUsedConditionSetTable(data) {
    $("#rdr-ka-used-condition-list-table").html("<tr><th>Attribute</th><th>Converted Name</th><th>Operator</th><th>Value</th></tr>");
    for (var i=0; i<data.length; i++) {
        var row = $("<tr />");
        row.append($("<td>" + data[i].attribute + "</td>"));
        row.append($("<td>" + attrConvertedNameList[data[i].attribute] + "</td>"));
        row.append($("<td>" + data[i].operator + "</td>"));
        row.append($("<td>" + data[i].value + "</td>"));
        
        $("#rdr-ka-used-condition-list-table").append(row); 
    }
    
}

function drawNewConditionSetTable(data) {
    $(".rdr-ka-new-condition-table").html("<tr><th>Attribute</th><th>Converted Name</th><th>Operator</th><th>Value</th></tr>");
    for (var i=0; i<data.length; i++) {
        var row = $("<tr />");
        row.append($("<td>" + data[i].attribute + "</td>"));
        row.append($("<td>" + attrConvertedNameList[data[i].attribute] + "</td>"));
        row.append($("<td>" + data[i].operator + "</td>"));
        row.append($("<td>" + data[i].value + "</td>"));
        
        $(".rdr-ka-new-condition-table").append(row); 
    }
    activateRuleConditionTableSelectionEvent();
}

function drawValidationCaseSetTable(data, filter) {
    filter = filter.toLowerCase();
    //draw header (header varies to case structure)
    $("#rdr-ka-validate-case-list-table").html("");
    var firstData = data[0];
    var headerRow = $("<tr />");
    headerRow.append($("<th>Case ID</th>"));
    for(var attrName in firstData) {
        if(firstData.hasOwnProperty(attrName) && attrName !== "caseId") {
            var compareData = attrName.toLowerCase();
            console.log(attrName);
            console.log(attrConvertedNameList[attrName]);
            var compareData2 = attrConvertedNameList[attrName].toLowerCase();
            var isFiltered = false;
            if(compareData.indexOf(filter) || compareData2.indexOf(filter)){
                isFiltered = true;
            }
            if(isFiltered && compareData !== "caseId" ){
                headerRow.append($("<th>" + attrName + "<br/>(" + attrConvertedNameList[attrName] + ")</th>"));
            }
        }
    }
    $("#rdr-ka-validate-case-list-table").append(headerRow);
    
    //draw body (each row represents each case)
    for (var i=0; i<data.length; i++) {
        var row = $("<tr />");
        row.append($("<td>" + data[i]["caseId"] + "</td>"));
        
        for(var attrName in data[i]) {
            if(data[i].hasOwnProperty(attrName) && attrName !== "caseId") {
                var compareData = attrName.toLowerCase();
                var compareData2 = attrConvertedNameList[attrName].toLowerCase();
                var isFiltered = false;
                if(compareData.indexOf(filter) || compareData2.indexOf(filter)){
                    isFiltered = true;
                }
                if(isFiltered && compareData !== "caseId" ){
                    var puttingData;
                    if(data[i][attrName].length > 30) {
                        puttingData = "<div class='rdr-tooltip' data-toggle='tooltip' title='" + data[i][attrName] +"'> " + data[i][attrName].substr(0,30) + "...</a>";
                    } else {
                        puttingData = data[i][attrName];
                    }
                    row.append($("<td>" + puttingData + "</td>"));
                }
            }
        }
        $("#rdr-ka-validate-case-list-table").append(row);
    }
    
    $("#rdr-ka-validate-case-list-table-loading").hide();
    $("#rdr-ka-validate-case-list-table").show();
    
    activateValidateCaseSetTableSelectionEvent();
}


function drawTableRow(tableId, rowData) {
    var row = $("<tr />");
    $(tableId).append(row); 
    row.append($("<td>" + rowData + "</td>"));
}

function selectLastTr(tableId){
    $(tableId).find("tr").removeClass("tr-selected");
    $(tableId).find("tr").removeClass("tr-not-selected");
    $(tableId + " tr").last().removeClass("tr-not-selected");
    $(tableId + " tr").last().addClass("tr-selected");
    
    var height =  $(tableId).height();
    
    $(tableId).parent().animate({
        scrollTop: height
    });
    
}

function selectGivenTr(tableId, trIndex, isScrollTop, container, position){
    var selectedTr = $(tableId).find("tr:eq("+ trIndex + ")");
    
    $(tableId).find("tr").removeClass("tr-selected");
    $(tableId).find("tr").removeClass("tr-not-selected");            
    selectedTr.removeClass("tr-not-selected");
    selectedTr.addClass("tr-selected");
    
    if(isScrollTop){
        var tdHeight = $(tableId).find("tr").find("td").height();
        var rowCount = $(tableId + " tr").length;
        var newPosition;
        if(trIndex < 10) {
            newPosition = ( (28) * trIndex ) - 100;
        } else if(trIndex > rowCount - 10){
            newPosition = ( (28) * trIndex ) + 300;
        } else {
            newPosition = ( (28) * trIndex ) - 100 ;
        }
        console.log(rowCount);
        $(container).animate({
            scrollTop: newPosition
        });
        
    }
}


/**SELECT OPTIONS**/
function addAttributesToOption(data, selection){
    $(".rdr-ka-new-condition-controller-attr").html("");
    for (var i=0; i<data.length; i++) {
        var option;
        if(selection == data[i].attribute){
            option = $("<option selected>" + data[i].attribute + "</option>");
        } else {
            option = $("<option>" + data[i].attribute + "</option>");
        }
        $(".rdr-ka-new-condition-controller-attr").append(option); 
    }
    activateAttributeChangeEvent(data, potentialOperators);
    var selectedAttrName = $(".rdr-ka-new-condition-controller-attr").val();
    setOperatorOfNewRule(false, selectedAttrName, data, potentialOperators);
}

function setOperatorOfNewRule(isEmpty, selectedAttrName, data, potentialOperators){
    if(isEmpty){
        var operatorArray = ["MISSING"];
        addOperatorsToOption(operatorArray, "MISSING");
        $(".rdr-ka-new-condition-controller-value").val("");
        $(".rdr-ka-new-condition-controller-value").prop('disabled', true);
    } else {
        $(".rdr-ka-new-condition-controller-value").prop('disabled', false);
        for (var i=0; i<data.length; i++) {
            var attrName = data[i].attribute;
            if(attrName === selectedAttrName) {
                var type = data[i].type;
                for(var key in potentialOperators) {
                    if(potentialOperators.hasOwnProperty(key)) {
                        if(type === key){
                            addOperatorsToOption(potentialOperators[key], "==");
                            break;
                        }
                    }
                }
            }
        }
        setValueOfNewRule(selectedAttrName, data);
    }
}

function addOperatorsToOption(operatorArray, selection){
    $(".rdr-ka-new-condition-controller-oper").html("");
    var selectedOper;
    for (var i=0; i<operatorArray.length; i++) {
        var option;
        if(selection == operatorArray[i]){
            option = $("<option >" + operatorArray[i] + "</option>");
        } else {
            option = $("<option>" + operatorArray[i] + "</option>");
        }
        $(".rdr-ka-new-condition-controller-oper").append(option); 
    }
}

function setValueOfNewRule(selectedAttrName, data){
    for (var i=0; i<data.length; i++) {
        var attrName = data[i].attribute;
        if(attrName === selectedAttrName) {
            if(isEmptyValue(data[i].value)){
                setOperatorOfNewRule(true, selectedAttrName, data, potentialOperators);
            } else {
                $(".rdr-ka-new-condition-controller-value").val(data[i].value);
            }
        }
    }
}

function setRuleController(attrName, typeName, valueName) {
    $(".rdr-ka-new-condition-controller-attr").val(attrName);
    if(isEmptyValue(valueName)){
        var operatorArray = ["MISSING"];
        addOperatorsToOption(operatorArray, "MISSING");
        $(".rdr-ka-new-condition-controller-value").val("");
        $(".rdr-ka-new-condition-controller-value").prop('disabled', true);
    } else {
        $(".rdr-ka-new-condition-controller-value").prop('disabled', false);
        addOperatorsToOption(potentialOperators[typeName], "==");
        $(".rdr-ka-new-condition-controller-value").val(valueName);
    }
}

/**TEXTAREA**/
function putWrongConclusion(wrongConclusion) {
    $("#rdr-ka-wrong-conclusion-textarea").text(wrongConclusion);    
}

function putNewConclusion(wrongConclusion) {
    $(".rdr-ka-new-conclusion-textarea").text(wrongConclusion);    
}


/** DIV or TAB **/
function changeTab(id){
    var stage =  parseInt(id.substr(8, 1));
    
    if (currentStage < stage){
        if(currentStage == 0){
            alert("새로운 소견을 선택해 주세요.");
        } else if(currentStage == 1){
            alert("소견지식 조건을 추가해 주세요.");
        } else if(currentStage == 2){
            alert("사례를 선택해 주세요.");
        }
    } else {
        $(".rdr-ka-main-tab-contents-container").hide();
        $(".rdr-ka-main-tab-label").removeClass("tab-active");
        $(".rdr-ka-main-tab-label").addClass("tab-inactive");
        $("#"+id).removeClass("tab-inactive");
        $("#"+id).addClass("tab-active");

        if(id === "rdr-tab-0"){
            console.log("opening tab 0 ");
            $("#rdr-ka-main-tab-contents-0").show();
        } else if(id === "rdr-tab-1"){
            console.log("opening tab 1 ");
            //get case values
            $("#rdr-ka-main-tab-contents-1").show();
            
            drawCurrentCaseViewerTable(currentCase, "");
            activateMissingAttributeEvent();
            addAttributesToOption(currentCase, "");
            
        } else if(id === "rdr-tab-2"){
            console.log("opening tab 2 ");
            currentStage = 2;
            //validate rule and get validation case set
            validateRuleAjax();
            $("#rdr-ka-main-tab-contents-2").show();
        } else if(id === "rdr-tab-3"){
            console.log("opening tab 3 ");
            $("#rdr-ka-main-tab-contents-3").show();    
//            drawCompareCurrentCaseViewerTable(currentCase,"");
//            drawCompareValidateCaseViewerTable(validatingCase,"");
            drawCompareCaseViewerTable(currentCase, validatingCase, "");
            activateCompareMissingAttributeEvent();
            addAttributesToOption(currentCase, "");
        } 
        
    }
}