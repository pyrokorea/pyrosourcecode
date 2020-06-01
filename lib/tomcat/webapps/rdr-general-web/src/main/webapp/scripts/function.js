/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


function isEmptyValue(value) {
    if(value === "NULL"){
        return true;
    }
    return false;
}

function isMissingValue(checkBoxId, value) {
    if($("#"+checkBoxId).prop("checked") == true){
        if(value === "NULL" || value === "na" || value === "NA"){
            return true;
        }
        return false;
    } else {
        return false;
    }
}

(function($) {
    'use strict';
    $.fn.tooltipOnOverflow = function() {
        $(this).on("mouseenter", function() {
            if (this.offsetWidth < this.scrollWidth) {
                $(this).attr('title', $(this).text());
            } else {
                $(this).removeAttr("title");
            }
        });
    };
})(jQuery);
