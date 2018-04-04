var my_notice_list = [];
var websocketHost = "";
$(document).ready(function() {
    $.ajax({
        url : "/commonNotice",
        async : false,
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
    var cancelAlarm_alarm = $.cookie("cancelAlarm_alarm");
    if(my_store){
        if(data.match('order_')=='order_'){
            if(data.match('update')=='update'){
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
                    var audio = new Audio('/alarmFiles/alarm/'+cancelAlarm_alarm);
                    audio.play();
                }
            }
        }
    }

}