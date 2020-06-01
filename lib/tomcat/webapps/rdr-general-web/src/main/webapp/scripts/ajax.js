
        
/**
 * 
 * @returns {undefined}
 */ 
function initialiseAjax(caseId, conclusionId, kaMode) {
    $.ajax({
        url: 'BloodTest',
        dataType: "json",
        data: {
            mode : 'initialise',
            caseId: caseId,
            conclusionId: conclusionId,
            kaMode: kaMode
        },
        success: function(data) {
            console.log(data);
            var filter = "";
            conclusionList = data.conclusionList;
            wrongConclusion = data.wrongConclusion;
            currentCase = data.currentCase;
            attrConvertedNameList = data.attrConvertedNameList;
            usedConditionSet = data.usedConditionSet;
            potentialOperators = data.potentialOperators;
            putWrongConclusion(wrongConclusion);
//            conditionArray = data[1];
            drawConclusionListTable(conclusionList, filter);
//            drawNewConditionTable(conditionArray);
            drawUsedConditionSetTable(usedConditionSet);

            activateOpenAddConclcusionDiv();
            activateTabSelectionEvent();
            activateTabNextButtonEvent();
            activateConclusionTableSelectionEvent();
            activateCurrentCaseTableSelectionEvent();
            activateConclusionSearchKeyUpEvent(conclusionList);
            activateAttributeSearchKeyUpEvent(currentCase);
            activateAddConclusionEvent();
            activateAddRuleConditionEvent();
            
            
            if(kaMode === "delete") {
                currentStage=1;
                putNewConclusion(wrongConclusion);
                changeTab("rdr-tab-1");
            } else {
                currentStage = 0;
                $("#rdr-conclusion-table-loading").hide();
                $("#rdr-conclusion-table").show();

            }
            
            $( "#rdr-body" ).fadeTo( "fast" , 1.0, function() {  
                $("#rdr-body").css("pointer-events", "auto");
                $("#rdr-initial-loading").hide();
            });
            console.log("initialised");
            
        }, error: function(){
            console.log("failure");
        }
    });
}

/**
 * addConclusionAjax
 * 
 * @param {type} newConclusionText
 * @returns {undefined}
 */
function addConclusionAjax(newConclusionStr){
    console.log("adding conclusion");
    
    $.ajax({
        url: 'BloodTest',
        dataType: "json",
        data: {
            mode : 'addConclusion',
            newConclusionStr : newConclusionStr
        },
        success: function(data) {
            console.log(data);
            if(data.validity === "error"){
                alert(data.string);
            } else {
                conclusionList.push(data.string);
                
                var conclusionText = newConclusionStr;
                drawConclusionListTable(conclusionList, "");
                selectLastTr("#rdr-conclusion-table");
                putNewConclusion(conclusionText);

                $("#rdr-ka-add-new-conclusion-section").slideUp(function(){
                    $("#rdr-ka-main-controller-section").show();
                });
                newConclusion = conclusionText;
                putNewConclusion(conclusionText);

                // set the current stage, which makes available to move next tab.
                currentStage = 1;
            }

        }
    });
}

/**
 * selectConclusionAjax
 * 
 * @param {type} selectedConclusionStr
 * @returns {undefined}
 */
function selectConclusionAjax(selectedConclusionStr){
    console.log("selecting conclusion for new rule = " + selectedConclusionStr);
    
    $.ajax({
        url: 'BloodTest',
        dataType: "json",
        data: {
            mode : 'selectConclusion',
            selectedConclusionStr : selectedConclusionStr
        },
        success: function(data) {
            if(data.validity === "error"){
                alert(data.string);
            } else {
            }

        }
    });
}


/**
 * addConditionAjax
 * 
 * @param {type} newConAttrStr
 * @param {type} newConOperStr
 * @param {type} newConValStr
 * @returns {undefined}
 */
function addConditionAjax(newConAttrStr, newConOperStr, newConValStr){
    console.log("adding condition");
    console.log(newConAttrStr + " " +newConOperStr + " " + newConValStr);
    
    $.ajax({
        url: 'BloodTest',
        dataType: "json",
        data: {
            mode : 'addCondition',
            newConAttrStr : newConAttrStr,
            newConOperStr : newConOperStr,
            newConValStr : newConValStr
        },
        success: function(data) {
            console.log(data);
            if(data.validity === "error"){
                alert(data.string);
            } else {
                var newCondition = new Object();
                newCondition.attribute = newConAttrStr;
                newCondition.operator = newConOperStr;
                newCondition.value = newConValStr;
                
                newConditionSet.push(newCondition);
                
                drawNewConditionSetTable(newConditionSet);
                currentStage = 2;
            }

        }
    });
}


/**
 * deleteConditionAjax
 * 
 * @param {type} newConAttrStr
 * @param {type} newConOperStr
 * @param {type} newConValStr
 * @returns {undefined}
 */
function deleteConditionAjax(newConAttrStr, newConOperStr, newConValStr){
    console.log("deleting condition");
    
    $.ajax({
        url: 'BloodTest',
        dataType: "json",
        data: {
            mode : 'deleteCondition',
            newConAttrStr : newConAttrStr,
            newConOperStr : newConOperStr,
            newConValStr : newConValStr
        },
        success: function(data) {
            console.log(data);
            if(data.validity === "error"){
                alert(data.string);
            } else {
                                
                newConditionSet = data.conditionSet;
                drawNewConditionSetTable(newConditionSet);
                if(newConditionSet.length > 0) {
                    currentStage = 2;
                } else {
                    currentStage = 1;
                }
            }

        }
    });
}


/**
 * validateRuleAjax
 * 
 * @returns {undefined}
 */
function validateRuleAjax(){
    console.log("validating new rule");
    
    $.ajax({
        url: 'BloodTest',
        dataType: "json",
        data: {
            mode : 'validateRule'
        },
        success: function(data) {
            console.log(data);
            if(data.validity === "confirm"){
                var r = confirm(data.string);
                if (r === true) {
                    addRuleAjax();
                } else {
                    changeTab("rdr-tab-1");
                }
            } else {
                //getting validation caseset
                getValidationCasesAjax();
            }

        }
    });
}


/**
 * getValidationCasesAjax
 * 
 * @returns {undefined}
 */
function getValidationCasesAjax(){
    console.log("getting validation case list");
    
    $.ajax({
        url: 'BloodTest',
        dataType: "json",
        data: {
            mode : 'getValidationCases'
        },
        success: function(data) {
            validationCaseSet = data;
            var filterText = $("#rdr-ka-validate-case-list-search-input").val();
            drawValidationCaseSetTable(validationCaseSet, filterText);
            activateValidateCaseAttributeSearchKeyUpEvent(validationCaseSet);
        }
    });
}



/**
 * getOtherConclusionsAjax
 * 
 * @returns {undefined}
 */
function getOtherConclusionsAjax(cornerstoneCaseId){
    console.log("getting other conclusions of the validation case");
    
    $.ajax({
        url: 'BloodTest',
        dataType: "json",
        data: {
            mode : 'getOtherConclusions',
            cornerstoneCaseId : cornerstoneCaseId
        },
        success: function(data) {
            //display other conclusions
            validatingCase = data.validatingCase;
            otherConclusionList = data.otherConclusions;
            
            var conclusionFilterText = $("#rdr-ka-search-other-conclusion-input-box").val();
            drawOtherConclusionListTable(otherConclusionList, conclusionFilterText);
            activateOtherConclusionSearchKeyUpEvent(otherConclusionList);
            activateCompareCaseAttributeSearchKeyUpEvent();
        }
    });
}


function addRuleAjax(){
    console.log("Adding rule");
    $.ajax({
        url: 'BloodTest',
        dataType: "json",
        data: {
            mode : 'addRule'
        },
        success: function(data) {            
            console.log(data);
            if ( data.validity === "done") {
                console.log(data);
                $( "#rdr-body" ).fadeTo( "fast" , 0.3, function() {     
                    $("#rdr-body").css("pointer-events", "none");
                    $("#rdr-rule-adding-done").show();

                });
            }
        }
    });
}