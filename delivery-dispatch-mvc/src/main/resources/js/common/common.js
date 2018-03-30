var my_notice_list = [];
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
    $('#logout').click(function () {
        $.ajax({
            url : "/logoutProcess",
            success : function () {
                location.href = "/login"
            }
        })
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