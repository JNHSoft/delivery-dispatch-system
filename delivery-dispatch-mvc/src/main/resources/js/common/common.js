var my_notice_list = [];
var websocketHost = "";
$(document).ready(function() {
    $.ajaxSetup({ cache: false });
    $.ajax({
        url : "/commonNotice",
        async : false,
        cache : false,
        success : function(data) {
        	if(typeof data == 'object') {
                my_notice_list = data;
                noticeAlarm();
			}
        }
    });
    $.ajax({
        url : "/websocketHost",
        async : false,
        success : function (data) {
            websocketHost = data;
        }
    });
    $('#logout').click(function () {
        $.ajax({
            url : "/logoutProcess",
            success : function () {
                location.href = "/login";
            }
        });
    })
    footerOrders();
    footerRiders();
});

function noticeAlarm() {
    if(my_notice_list && my_store){
    	$('#myStoreName').text(my_store.storeName);
    }
    $("#notice_alarm").removeClass('new');
    for (var i = 0; i < my_notice_list.length; i++) {
        if (!my_notice_list[i].confirmedDatetime) {
            $("#notice_alarm").addClass('new');
        }
    }
}

function alarmSound(data) {
    var new_alarm = $.cookie("new_alarm");
    var assign_alarm = $.cookie("assign_alarm");
    var assignCancel_alarm = $.cookie("assignCancel_alarm");
    var complete_alarm = $.cookie("complete_alarm");
    var cancel_alarm = $.cookie("cancel_alarm");
    if(my_store){
        if(data.match('order_')=='order_'){
            if(data.match('new')=='new'){
                if ((my_store.alarm).match('0')=='0'){
                    var audio = new Audio('/alarmFiles/alarm/'+new_alarm);
                    audio.play();
                }
            }
            if(data.match('assigned')=='assigned'){
                if ((my_store.alarm).match('1')=='1'){
                    var audio = new Audio('/alarmFiles/alarm/'+assign_alarm);
                    audio.play();
                }
            }
            if(data.match('assign_canceled')=='assign_canceled'){
                if ((my_store.alarm).match('2')=='2'){
                    var audio = new Audio('/alarmFiles/alarm/'+assignCancel_alarm);
                    audio.play();
                }
            }
            if(data.match('completed')=='completed'){
                if ((my_store.alarm).match('3')=='3'){
                    var audio = new Audio('/alarmFiles/alarm/'+complete_alarm);
                    audio.play();
                }
            }
            if(data.match('order_canceled')=='order_canceled'){
                if ((my_store.alarm).match('4')=='4'){
                    var audio = new Audio('/alarmFiles/alarm/'+cancel_alarm);
                    audio.play();
                }
            }
        }
    }
}

function footerOrders() {
    $.ajax({
        url : "/footerOrderList",
        type : "get",
        cache : false,
        dataType : 'json',
        success : function(data) {
            if(typeof data == 'object') {
                var newCnt = 0;
                var assignedCnt = 0;
                var completedCnt = 0;
                var canceledCnt = 0;
                for (i =0; i <data.length; i++){
                    if(data[i].status=="0"){
                        newCnt += parseInt(data[i].count);
                    }else if(data[i].status=="1"){
                        assignedCnt += parseInt(data[i].count);
                    }else if(data[i].status=="2"){
                        assignedCnt += parseInt(data[i].count);
                    }else if(data[i].status=="3"){
                        completedCnt += parseInt(data[i].count);
                    }else if(data[i].status=="4"){
                        canceledCnt += parseInt(data[i].count);
                    }else if(data[i].status=="5"){
                        newCnt += parseInt(data[i].count);
                    }
                }
                $('#new').text(newCnt);
                $('#assigned').text(assignedCnt);
                $('#completed').text(completedCnt);
                $('#canceled').text(canceledCnt);
            }
        }
    });
}
function footerRiders() {
    $.ajax({
        url : "/footerRiderList",
        cache : false,
        success : function(data) {
            if(typeof data == 'object') {
                var restCnt = 0;
                var workCnt = 0;
                var standbyCnt = 0;
                for (i =0; i <data.length; i++){
                    if(data[i].working == "1"){
                        standbyCnt = parseInt(data[i].workCount) - parseInt(data[i].orderCount);
                        workCnt = data[i].orderCount;
                    }
                    if(data[i].working == "3"){
                        restCnt = parseInt(data[i].workCount) + parseInt(data[i].orderCount);
                    }
                }
                $('#work').text(workCnt);
                $('#standby').text(standbyCnt);
                $('#rest').text(restCnt);
            }
        }
    });
}
