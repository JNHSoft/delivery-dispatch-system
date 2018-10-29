$(document).ready(function() {
    $.ajaxSetup({
        cache : false,
        timeout : 30000,
        error : function (request,status,error) {
            // alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
            console.log("error: "+order_detail_error+"\n"+(request?"code: "+request.status:"errMessage : "+error));
            // location.href = "/order";
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
    noticeAlarm();
    if($(".f_left")){
        if(map_region){
            if(map_region!="tw"){
                footerRiders();
            }else{
                $("#footer_left").hide();
            }
        }
    }
    $('#selectLang').change(function () {
        changeAlarmByLang();
    });

});

function noticeAlarm() {
    $.ajax({
        url : "/commonNotice",
        success : function(data) {
            if(data && my_store){
                $('#myStoreName').text(my_store.storeName);
            }
            $("#notice_alarm").removeClass('new');
            $("#notice_alarm_mobile").removeClass('new');
            for (var i = 0; i < data.length; i++) {
                if (!data[i].confirmedDatetime) {
                    $("#notice_alarm").addClass('new');
                    $("#notice_alarm_mobile").addClass('new');
                }
            }
        }
    });
}

function alarmSound(data) {
    var new_alarm = $.cookie("new_alarm");
    var assign_alarm = $.cookie("assign_alarm");
    var assignCancel_alarm = $.cookie("assignCancel_alarm");
    var complete_alarm = $.cookie("complete_alarm");
    var cancel_alarm = $.cookie("cancel_alarm");
    if(my_store.alarm){
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
// footer 부분
function footerRiders() {
    $.ajax({
        url : '/footerRiderList',
        success : function(data) {
            if(typeof data == 'object') {
                var restCnt = 0;
                var workCnt = 0;
                var standbyCnt = 0;

                var myRestCnt = 0;
                var myWorkCnt = 0;
                var myStandbyCnt = 0;

                for (i =0; i <data.length; i++){
                    if(data[i].working == "1"){
                        standbyCnt = parseInt(data[i].workCount) - parseInt(data[i].orderCount);
                        workCnt = data[i].orderCount;

                        myStandbyCnt = parseInt(data[i].myWorkCount) - parseInt(data[i].orderCount);
                        myWorkCnt = data[i].orderCount;
                    }

                    if(data[i].working == "3"){
                        restCnt = parseInt(data[i].workCount);
                        myRestCnt = parseInt(data[i].myWorkCount);
                    }
                }
                $('#work').text(workCnt);
                $('#standby').text(standbyCnt);
                $('#rest').text(restCnt);

                $('#myWork').text(myWorkCnt);
                $('#myStandby').text(myStandbyCnt);
                $('#myRest').text(myRestCnt);
            }
        }
    });
}


function showKeyCode(event) {
    event = event || window.event;
    var keyID = (event.which) ? event.which : event.keyCode;
    if( ( keyID >=48 && keyID <= 57 ) || ( keyID >=96 && keyID <= 105 ) || ( keyID >=37 && keyID <= 40 ) || keyID === 8) {
        return;
    }
    else {
        return false;
    }
}

function regOrderIdReduce(regOrderId) {
    if(regOrderId.indexOf('-') != -1){
        var reduceId = regOrderId.split('-');
        if(reduceId.length >= 1){
            return reduceId[reduceId.length-2]+'-'+ reduceId[reduceId.length-1];
        }
    }else {
        return regOrderId;
    }
}

function changeAlarmByLang() {
    var lang = $("#selectLang option:selected").attr("id");
    $.ajax({
        url : "/loginSuccessSetAlarm",
        type : 'get',
        data : {
            lang : lang
        },
        success : function(data) {
            for (var key in data) {
                if (data.hasOwnProperty(key)) {
                    if(data[key].alarmType=='0'){
                        $.cookie("new_alarm", data[key].fileName, {"expires" : 365});
                    }else if(data[key].alarmType=='1'){
                        $.cookie("assign_alarm", data[key].fileName, {"expires" : 365});
                    }else if(data[key].alarmType=='2'){
                        $.cookie("assignCancel_alarm", data[key].fileName, {"expires" : 365});
                    }else if(data[key].alarmType=='3'){
                        $.cookie("complete_alarm", data[key].fileName, {"expires" : 365});
                    }else if(data[key].alarmType=='4'){
                        $.cookie("cancel_alarm", data[key].fileName, {"expires" : 365});
                    }
                }
            }
            location.href=location.pathname+"?lang="+lang;
        }
    });
}