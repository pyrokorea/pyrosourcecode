/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


//event activation
function activateOpenAddConclcusionDiv(){
    $("#rdr-ka-add-new-conclusion-button").click(function(){
        if( !$("#rdr-ka-add-new-conclusion-section").is(":visible") ){
            $("#rdr-ka-add-new-conclusion-section").slideDown();
            $("#rdr-ka-main-controller-section").hide();
            
        } else {
            $("#rdr-ka-add-new-conclusion-section").slideUp(function(){
                $("#rdr-ka-main-controller-section").show();
            });
        }
    });
}

function activateAddConclusionEvent(){
    $("#rdr-ka-new-conclusion-add-button").click(function(){
        var valid = true;
        var newConclusionText = $("#rdr-ka-new-conclusion-input-textarea").val();
        for(var i=0; i<conclusionList.length; i++){
            if(conclusionList[i] === newConclusionText){
                alert("'" + newConclusionText + "' 이미 존재하는 소견입니다.");
                valid = false;
                break;
            }
        }
        if(valid){
            addConclusionAjax(newConclusionText);
        }
    });
}

function activateConclusionTableSelectionEvent(){
    $("#rdr-conclusion-table").find("tr").click(function(){
        if(! $(this).children().is("th")){
            $("#rdr-conclusion-table").find("tr").removeClass("tr-selected");
            $("#rdr-conclusion-table").find("tr").removeClass("tr-not-selected");
            $(this).removeClass("tr-not-selected");
            $(this).addClass("tr-selected");
            
            // set the wrong conclusion
            newConclusion = $(this).find("td").text();
            putNewConclusion(newConclusion);
            
            // set the current stage, which makes available to move next tab.
            currentStage = 1;
        }
    });
}

function activateCurrentCaseTableSelectionEvent(){
    $("#rdr-ka-current-case-viewer-table").find("tr").click(function(){
        if(! $(this).children().is("th")){
            $("#rdr-ka-current-case-viewer-table").find("tr").removeClass("tr-selected");
            $("#rdr-ka-current-case-viewer-table").find("tr").removeClass("tr-not-selected");            
            $(this).removeClass("tr-not-selected");
            $(this).addClass("tr-selected");
            
            // set rule contoller
            var selectedAttrName = $(this).find("td:first").text();
            var selectedTypeName = $(this).find("td:first").next().next().text();
            var selectedValueName = $(this).find("td:first").next().next().next().text();
            setRuleController(selectedAttrName, selectedTypeName, selectedValueName);
            
        }
    });
}

function activateRuleConditionTableSelectionEvent(){
    $(".rdr-ka-new-condition-table").find("tr").click(function(){
        if(! $(this).children().is("th")){
            $(".rdr-ka-new-condition-table").find("tr").removeClass("tr-selected");
            $(".rdr-ka-new-condition-table").find("tr").removeClass("tr-not-selected");            
            $(this).removeClass("tr-not-selected");
            $(this).addClass("tr-selected");
        }
    });
}

function activateValidateCaseSetTableSelectionEvent(){
    $("#rdr-ka-validate-case-list-table").find("tr").click(function(){
        if(! $(this).children().is("th")){
            $("#rdr-ka-validate-case-list-table").find("tr").removeClass("tr-selected");
            $("#rdr-ka-validate-case-list-table").find("tr").removeClass("tr-not-selected");            
            $(this).removeClass("tr-not-selected");
            $(this).addClass("tr-selected");
            var cornerstoneCaseId = $(this).find("td").first().text();
            // take out 1 because of header
            validatingCaseIndex = $(this).index()-1;
            
            console.log("cornerstoneCaseId " + cornerstoneCaseId);        
            // retrieve other conclusions (ajax)
            getOtherConclusionsAjax(cornerstoneCaseId);
            currentStage = 3;
        }
    });
}

function activateCompareCurrentCaseTableSelectionEvent(){
    $("#rdr-ka-compare-current-case-viewer-table").find("tr").click(function(){
        if(! $(this).children().is("th")){
            var trIndex = $(this).index();
            var trPosition = $(this).parent().offset().top;
            selectGivenTr("#rdr-ka-compare-current-case-viewer-table", trIndex, false, "", trPosition);
            selectGivenTr("#rdr-ka-compare-validate-case-viewer-table", trIndex, true, ".rdr-ka-compare-validate-case-viewer-table-container", trPosition);
            
            // set rule contoller
            var selectedAttrName = $(this).find("td:first").text();
            var selectedTypeName = $(this).find("td:first").next().next().text();
            var selectedValueName = $(this).find("td:first").next().next().next().text();
            setRuleController(selectedAttrName, selectedTypeName, selectedValueName);
        }
    });
}

function activateCompareValidateCaseTableSelectionEvent(){
    $("#rdr-ka-compare-validate-case-viewer-table").find("tr").click(function(){
        if(! $(this).children().is("th")){
            var trIndex = $(this).index();
            var trPosition = $(this).parent().offset().top;
            selectGivenTr("#rdr-ka-compare-validate-case-viewer-table", trIndex, false, "", trPosition);
            selectGivenTr("#rdr-ka-compare-current-case-viewer-table", trIndex, true, ".rdr-ka-compare-current-case-viewer-table-container", trPosition);
            
            // set rule contoller
            var selectedAttrName = $(this).find("td:first").text();
            var selectedTypeName = $(this).find("td:first").next().next().text();
            var newTrIndex = trIndex + 1;
            var currentCaseTr = $("#rdr-ka-compare-current-case-viewer-table").find("tr:nth-child(" + newTrIndex + ")");
            
            var selectedValueName = currentCaseTr.find("td:first").next().next().next().text();
            
            setRuleController(selectedAttrName, selectedTypeName, selectedValueName);
        }
    });
}

var previousConclusionText = "";
function activateConclusionSearchKeyUpEvent(conclusionList){
    $("#rdr-ka-search-conclusion-input-box").keyup(function(){
        var delay = (function(){
            var timer = 0;
            return function(callback, ms){
              clearTimeout (timer);
              timer = setTimeout(callback, ms);
            };
        })();
        // filter only if there is change in the search field.
        if( $("#rdr-ka-search-conclusion-input-box").val() !== previousConclusionText){
            $("#rdr-conclusion-table-loading").show();
            $("#rdr-conclusion-table").hide();

            delay(function(){
                var conclusionText = $("#rdr-ka-search-conclusion-input-box").val();
                drawConclusionListTable(conclusionList, conclusionText);

                $("#rdr-conclusion-table-loading").hide();
                $("#rdr-conclusion-table").show();
                previousConclusionText = conclusionText;
            }, 200 );
        }
    });
}

var previousOtherConclusionText = "";
function activateOtherConclusionSearchKeyUpEvent(otherConclusionList){
    $("#rdr-ka-search-other-conclusion-input-box").keyup(function(){
        var delay = (function(){
            var timer = 0;
            return function(callback, ms){
              clearTimeout (timer);
              timer = setTimeout(callback, ms);
            };
        })();
        // filter only if there is change in the search field.
        if( $("#rdr-ka-search-other-conclusion-input-box").val() !== previousOtherConclusionText){

            delay(function(){
                var conclusionText = $("#rdr-ka-search-other-conclusion-input-box").val();
                drawConclusionListTable(otherConclusionList, conclusionText);

                previousOtherConclusionText = conclusionText;
            }, 200 );
        }
    });
}

var previousAttributeText = "";
function activateAttributeSearchKeyUpEvent(caseValues){
    $("#rdr-ka-current-case-viewer-search-input").keyup(function(){
        var delay = (function(){
            var timer = 0;
            return function(callback, ms){
              clearTimeout (timer);
              timer = setTimeout(callback, ms);
            };
        })();
        // filter only if there is change in the search field.
        if( $("#rdr-ka-current-case-viewer-search-input").val() !== previousAttributeText){

            delay(function(){
                $("#rdr-ka-current-case-viewer-table").hide();
                $("#rdr-ka-current-case-viewer-table-loading").show();
                var attributeText = $("#rdr-ka-current-case-viewer-search-input").val();
                drawCurrentCaseViewerTable(caseValues, attributeText);

                previousAttributeText = attributeText;
            }, 200 );
        }
    });
}

var previousValidateCaseAttributeText = "";
function activateValidateCaseAttributeSearchKeyUpEvent(){
    $("#rdr-ka-validate-case-list-search-input").keyup(function(){
        var delay = (function(){
            var timer = 0;
            return function(callback, ms){
              clearTimeout (timer);
              timer = setTimeout(callback, ms);
            };
        })();
        var attributeText = $("#rdr-ka-validate-case-list-search-input").val();
        // filter only if there is change in the search field.
        if( attributeText !== previousValidateCaseAttributeText){

            delay(function(){
                drawValidationCaseSetTable(validationCaseSet, attributeText);
                previousValidateCaseAttributeText = attributeText;
            }, 200 );
        }
    });
}

var previousCompareCaseAttributeText = "";
function activateCompareCaseAttributeSearchKeyUpEvent(){
    $("#rdr-ka-compare-current-case-viewer-search-input").keyup(function(){
        var delay = (function(){
            var timer = 0;
            return function(callback, ms){
              clearTimeout (timer);
              timer = setTimeout(callback, ms);
            };
        })();
        var attributeText = $("#rdr-ka-compare-current-case-viewer-search-input").val();
        // filter only if there is change in the search field.
        if( attributeText !== previousValidateCaseAttributeText){

            delay(function(){
                drawCompareCaseViewerTable(currentCase, validatingCase, attributeText);
                previousCompareCaseAttributeText = attributeText;
            }, 200 );
        }
    });
}

function activateRuleChangeEvent(){
    $(".rdr-ka-new-condition-controller-attr").change(function(){
        $(".rdr-ka-new-condition-controller-attr").val($(this).val());
    });
    
    $(".rdr-ka-new-condition-controller-oper").change(function(){
        $(".rdr-ka-new-condition-controller-oper").val($(this).val());
    });
    
    $(".rdr-ka-new-condition-controller-value").change(function(){
        $(".rdr-ka-new-condition-controller-value").val($(this).val());
    });
}

function activateMissingAttributeEvent(){
    $(".rdr-ka-current-case-viewer-missing-checkbox-container").click(function(){
        if($("#rdr-ka-current-case-viewer-missing-checkbox").prop("checked") === true) {
            $("#rdr-ka-current-case-viewer-missing-checkbox").prop("checked", false);
        } else {
            $("#rdr-ka-current-case-viewer-missing-checkbox").prop("checked", true);
        }
        var attributeText = $("#rdr-ka-current-case-viewer-search-input").val();
        drawCurrentCaseViewerTable(currentCase, attributeText);
    });
    $("#rdr-ka-current-case-viewer-missing-checkbox").change(function(){
        var attributeText = $("#rdr-ka-current-case-viewer-search-input").val();
        drawCurrentCaseViewerTable(currentCase, attributeText);
    });
}

function activateCompareMissingAttributeEvent(){
    $(".rdr-ka-compare-case-viewer-missing-checkbox-container").click(function(){
        if($("#rdr-ka-compare-case-viewer-missing-checkbox").prop("checked") === true) {
            $("#rdr-ka-compare-case-viewer-missing-checkbox").prop("checked", false);
        } else {
            $("#rdr-ka-compare-case-viewer-missing-checkbox").prop("checked", true);
        }
        var attributeText = $("#rdr-ka-current-case-viewer-search-input").val();
        drawCurrentCaseViewerTable(currentCase, validatingCase,attributeText);
    });
    $("#rdr-ka-compare-case-viewer-missing-checkbox").change(function(){
        var attributeText = $("#rdr-ka-current-case-viewer-search-input").val();
        drawCurrentCaseViewerTable(currentCase, validatingCase,attributeText);
    });
}

function activateAttributeChangeEvent(caseValues, potentialOperators){
    $(".rdr-ka-new-condition-controller-attr").change(function(){
        var selectedAttrName = $(".rdr-ka-new-condition-controller-attr").val();
        setOperatorOfNewRule(false, selectedAttrName, caseValues, potentialOperators);
        
    });
}

function activateAddRuleConditionEvent(){
    $(".rdr-ka-new-condition-controller-add-button").click(function(){
        var newConAttrStr = $(".rdr-ka-new-condition-controller-attr").val();
        var newConOperStr = $(".rdr-ka-new-condition-controller-oper").val();
        var newConValStr = $(".rdr-ka-new-condition-controller-value").val();
        
        addConditionAjax(newConAttrStr, newConOperStr, newConValStr);
        
    });
}

function activateTabSelectionEvent(){
    $(".rdr-ka-main-tab-label").click(function(){
        var id = $(this).attr("id"); 
        changeTab(id);
    });
}

function activateTabNextButtonEvent(){
    $("#rdr-ka-tab-1-next-button").click(function(){
        var selectedConclusionStr = $(".rdr-ka-new-conclusion-textarea").val();
        selectConclusionAjax(selectedConclusionStr);
        changeTab("rdr-tab-1");
    });
    
    $("#rdr-ka-tab-2-next-button").click(function(){
        if(newConditionSet.length > 0) {
            //enable to go next tab
            currentStage = 2;
        } else {
            //disable to go to next tab
            currentStage = 1;
        }
        changeTab("rdr-tab-2");
    });
}

function activateDeleteConditionButtonEvent(){
    $(".rdr-ka-new-condition-controller-delete-button").click(function(){
        var newConAttrStr = $(".rdr-ka-new-condition-table").find(".tr-selected").find("td:first").text();
        var newConOperStr = $(".rdr-ka-new-condition-table").find(".tr-selected").find("td:first").next().next().text();
        var newConValStr = $(".rdr-ka-new-condition-table").find(".tr-selected").find("td:first").next().next().next().text();
        if(newConAttrStr === ""){
            alert("소견지식 조건을 선택해주세요.");
        } else {
            deleteConditionAjax(newConAttrStr, newConOperStr, newConValStr);
        }
    });
}

function activateCheckAgainButtonEvent(){
    $("#rdr-ka-validate-rule-controller-check-again-button").click(function(){
        changeTab("rdr-tab-2");
    });
}

// validation case list controller buttons
function activateValidationCaseSetControllerButtonsEvent(){
    $("#rdr-ka-validate-case-list-controller-button-except").click(function(){
        //go to tab 4
        changeTab("rdr-tab-3");
    });
    
    $("#rdr-ka-validate-case-list-controller-button-accept").click(function(){
        //remove selected case
        var newArray = new Array();
        for(var i=0; i<validationCaseSet.length; i++){
            if(i !== validatingCaseIndex){
                newArray.push(validationCaseSet[i]);
            }
        }
        
        if(newArray.length === 0){
            var r = confirm("모든 사례들을 수락하셨습니다. 새로운 지식을 생성하시겠습니까? ");
            if (r == true) {
                //add rule
                addRuleAjax();
            } else {

            }
        } else {
            validationCaseSet = newArray;

            var attributeText = $("#rdr-ka-validate-case-list-search-input").val();
            drawValidationCaseSetTable(validationCaseSet, attributeText);

            //select the case on top of the list
            $("#rdr-ka-validate-case-list-table").find("tr:first").next().addClass("tr-selected");
            var cornerstoneCaseId =$("#rdr-ka-validate-case-list-table").find("tr:first").next().find("td").first().text();
            
            getOtherConclusionsAjax(cornerstoneCaseId);
            validatingCaseIndex = 0;
        }
    });
    
    $("#rdr-ka-validate-case-list-controller-button-accept-all").click(function(){
        var r = confirm("모든 사례들을 수락하시고 새로운 지식을 생성하시겠습니까? ");
        if (r == true) {
            //add rule
            addRuleAjax();
        } else {
            
        }
    });
}