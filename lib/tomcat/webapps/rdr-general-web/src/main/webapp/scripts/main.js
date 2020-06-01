/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var conclusionList;
var currentCase;
var attrConvertedNameList;
var usedConditionSet;
var wrongConclusion;
var potentialOperators;
var validationCaseSet;
var validatingCase;
var validatingCaseIndex;
var otherConclusionList;

var newConditionSet = new Array();

var currentStage;

//when page is loaded
$(document).ready(function(){
    console.log("initialising...");
    
    alert("main.js start");
    
    $.ajax({
        url: 'BloodTest',
        dataType: "json",
        data: {
            mode : 'check'
        },
        success: function(data) {
            console.log(data);
        }, error: function(data){
            console.log(data);
        }
    });
    
    $( "#rdr-body" ).fadeTo( "fast" , 0.3, function() {     
        $("#rdr-body").css("pointer-events", "none");

    });
 
    
    //if there is no client id
    if(caseId==null || kaMode==null){
        $("body").html("");
        alert("잘못된 접근방식입니다.");
        window.history.back();
    } else {
        if(kaMode=="edit" && conclusionId == null) {
            $("body").html("");
            alert("잘못된 접근방식입니다.");
            window.history.back();
        } else if(kaMode=="delete" && conclusionId == null) {
            $("body").html("");
            alert("잘못된 접근방식입니다.");
            window.history.back();
        } else {
            if(kaMode==="add"){
                $("#rdr-ka-mode-title").text("경험 지식 추가");
            } else if(kaMode==="edit"){
                $("#rdr-ka-mode-title").text("경험 지식 수정");
            } else if(kaMode==="delete"){
                $("#rdr-ka-mode-title").text("경험 지식 삭제");
                $(".rdr-ka-new-conclusion-label").text("삭제할 소견");
            }
            //initialise interface by requesting data
            initialiseAjax(caseId, conclusionId, kaMode);
            activateDeleteConditionButtonEvent();
            activateRuleChangeEvent();
            activateCheckAgainButtonEvent();
            activateValidationCaseSetControllerButtonsEvent();

            $('input[type=checkbox]').click(function (e) {
                e.stopPropagation();
            });
        }
    }
});