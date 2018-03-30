/*<![CDATA[*/
var storeLatitude = parseFloat($('#storeLatitude').val());
var storeLongitude = parseFloat($('#storeLongitude').val());
var storeId = $('#storeId').val();
var map;
var marker = [];
var store;
var rider;
var RiderChatUserId = "";
var chatUserName = "";
function initMap() {
    store = {lat: storeLatitude, lng: storeLongitude};
    map = new google.maps.Map(document.getElementById('map'), {
        zoom: 15,
        center: store
    });
}
function footerRiders() {
    if(footerRiderList[2]){
        $('#rest').text(parseInt(footerRiderList[2].workCount) + parseInt(footerRiderList[2].orderCount));//휴식
    }else {
        $('#rest').text('0');
    }
    if(footerRiderList[1]){
        $('#standby').text(parseInt(footerRiderList[1].workCount) - parseInt(footerRiderList[1].orderCount));// 대기
        $('#work').text(footerRiderList[1].orderCount);//근무
    }else {
        $('#standby').text('0');
        $('#work').text('0');
    }
}
function footerOrders() {
    var newCnt = 0;
    var assignedCnt = 0;
    var completedCnt = 0;
    var canceledCnt = 0;
    for (i =0; i <footerOrderList.length; i++){
        if(footerOrderList[i].status=="0"){
            newCnt += parseInt(footerOrderList[i].count);
        }else if(footerOrderList[i].status=="1"){
            assignedCnt += parseInt(footerOrderList[i].count);
        }else if(footerOrderList[i].status=="2"){
            assignedCnt += parseInt(footerOrderList[i].count);
        }else if(footerOrderList[i].status=="3"){
            completedCnt += parseInt(footerOrderList[i].count);
        }else if(footerOrderList[i].status=="4"){
            canceledCnt += parseInt(footerOrderList[i].count);
        }else if(footerOrderList[i].status=="5"){
            newCnt += parseInt(footerOrderList[i].count);
        }
    }
    $('#new').text(newCnt);
    $('#assigned').text(assignedCnt);
    $('#completed').text(completedCnt);
    $('#canceled').text(canceledCnt);
}
$(function() {
    footerRiders();
    footerOrders();
    var supportsWebSockets = 'WebSocket' in window || 'MozWebSocket' in window;
    if (supportsWebSockets) {
        var socket = io(websocket_localhost, {
            path: '/socket.io', // 서버 사이드의 path 설정과 동일해야 한다
            transports: ['websocket'] // websocket만을 사용하도록 설정
        });
        socket.on('message', function(data){
            if(data.match('rider_')=='rider_'){
                getRiderList();
                footerRiders();
            }
            if(data.match('chat_')=='chat_'){
                var chatUserId = data.substring(data.indexOf("recv_chat_user_id:")+18, data.lastIndexOf('}'));
                if(RiderChatUserId == chatUserId){
                    getChatList(chatUserId,chatUserName);
                }
            }
            if(data.match('notice_')=='notice_'){
                noticeAlarm();
            }
            if(data.match('order_')=='order_'){
                footerOrders()
            }
        });
        $(function() {
            $('#sendChat').click(function(){
                socket.emit('message', "push_data:{type:chat_send, recv_chat_user_id:"+RiderChatUserId+"}");//data보내는부분
            });
        })
    } else {
        alert('websocket을 지원하지 않는 브라우저입니다.');
    }
    $('.chat-item').last().focus();
    getRiderList();
    $("#orderAllChk").click(function () {
        if(this.checked){
            $("input[name=srchChk]:checkbox").each(function() {
                $(this).attr("checked", true);
                $(this).attr("disabled", true);
            });
        }else{
            $("input[name=srchChk]:checkbox").each(function() {
                $(this).attr("checked", false);
                $(this).attr("disabled", false);
            });
        }
    });
    $('.table tr').click(function () {
        $(this).addClass('selected').siblings().removeClass('selected');
        $('.chat_wrap').addClass('active');
    });

    $('.chat_wrap .close').click(function (e) {
        e.preventDefault();
        $('.table tr').removeClass('selected');
        $('.chat_wrap').removeClass('active');
        $(window).scrollTop(0);
    });

});
function addMarker(location, data, i, status) {
    if($('#myStoreChk').prop('checked') && data.riderStore.id != $('#storeId').val()) {
        return i;
    }
    if(data.latitude != null){
        marker[i] = new google.maps.Marker({
            position : location,
            riderMapId : data.id,
            riderChatUserId : data.chatUserId,
            label : data.name,
            map : map
        });
        marker[i].addListener('click', function () {
            chatUserName = this.label
            RiderChatUserId = this.riderChatUserId;
            $('tr').removeClass('selected');
            $('#riderMapId' + this.riderMapId).addClass('selected');
            getChatList(this.riderChatUserId, this.label);
            var name = this.label + rider_chat_title;
            $('#chatRider').text(name);
            $('#workingStatus').html(status);
        });
        return i + 1;
    }
    return i;
}

function getRiderList() {
    var shtml = "";
    var mydata = [];
    var i = 1;
    var $status = "";
    $.ajax({
        url: "/getRiderList",
        type: 'get',
        data: {
        },
        dataType: 'json',
        success: function (data) {
        for (var j = 1; j < marker.length; j++) {
            marker[j].setMap(null);
        }
        marker=[];
        marker[0] = new google.maps.Marker({
            position : {lat: storeLatitude, lng: storeLongitude},
            map : map
        });
        for (var key in data) {
            if (data.hasOwnProperty(key)) {
                    var lattitude = parseFloat(data[key].latitude);
                    var longitude = parseFloat(data[key].longitude)
                    var location = {lat: lattitude, lng: longitude};
                if(data[key].working==1 && typeof data[key].order != "undefined"){
                    $status = '<i class="ic_txt ic_blue">'+ status_work +'</i>';
                }else if (data[key].working==1 && typeof data[key].order == "undefined"){
                    $status = '<i class="ic_txt ic_green">'+ status_standby +'</i>';
                }else if (data[key].working==3){
                    $status = '<i class="ic_txt ic_red">'+ status_rest +'</i>';
                }else{
                    $status = '<i class="ic_txt">'+ status_off +'</i>';
                }
                if($('#srchChk1').prop('checked') && data[key].working==1 && typeof data[key].order != "undefined") {
                    i = addMarker(location, data[key], i, $status);
                    shtml += gridRiderList(data[key], $status);
                }
                if($('#srchChk2').prop('checked') && data[key].working==1 && typeof data[key].order == "undefined") {
                    i = addMarker(location, data[key], i, $status);
                    shtml += gridRiderList(data[key], $status);
                }
                if($('#srchChk3').prop('checked') && data[key].working==3) {
                    i = addMarker(location, data[key], i, $status);
                    shtml += gridRiderList(data[key], $status);
                }
                if($('#srchChk4').prop('checked') && data[key].working==0){
                    i = addMarker(location, data[key], i, $status);
                    shtml += gridRiderList(data[key], $status);
                }
            } 
        }
        $('#riderList').html(shtml);
        }
    });
}
function gridRiderList(data, $status) {
    if($('#myStoreChk').prop('checked') && data.riderStore.id != $('#storeId').val()) {
        return '';
    }
    var shtml = "";
    shtml += "<tr id='riderMapId"  + data.id + "'><td>" +data.id+"</td>";
    shtml += "<td>"+data.name+"</td>";
    shtml += "<td>"+$status+"</td>";
    shtml += "<td>"+data.riderStore.storeName+"</td>";
    if(data.orderStore){
        shtml += "<td>"+data.orderStore.storeName+"</td>";
    }else {
        shtml +="<td>-</td>";
    }
    if(typeof data.order != "undefined" && typeof data.orderStore != "undefined"){
        if(data.riderStore.id == $('#storeId').val() && data.riderStore.id != data.orderStore.id && typeof data.returnTime == "undefined"){
            shtml += "<td>" + '<button class="button h20" onclick="javascript:putRiderReturnTime('+ data.id +');">' + 'rider_rest' +'</button></td>';
        }else {
            shtml +="<td>-</td>";
        }
    }else {
        shtml +="<td>-</td>";
    }
    shtml += "</tr>"
    return shtml;
}
function daySet(time) {
    var today= $.datepicker.formatDate('yy-mm-dd ', new Date());
    var d = $.datepicker.formatDate('yy-mm-dd ', new Date(time));
    if(today == d){
        return  'Today';
    }
    return d;
}
function messageTimeSet(time) {
    return new Date(time).toLocaleTimeString();
}
function getChatList(chatUserId, riderName) {
    var shtml = "";
    var tmpDate;
    $.ajax({
        url: "/getChatList",
        type: 'get',
        data: {
            chatUserId : chatUserId
        },
        dataType: 'json',
        success: function (data) {
            for (var key in data) {
                if (data.hasOwnProperty(key)) {
                    if(tmpDate != daySet(data[key].createdDatetime)){
                        shtml +='<div class="date"><span>' + daySet(data[key].createdDatetime) + '</span></div>';
                        tmpDate = daySet(data[key].createdDatetime);
                    }
                    if(data[key].chatUserId == chatUserId){
                        shtml +='<div class="chat-item" tabindex="0"><p class="name">'+riderName+'</p>';
                        shtml +='<div class="bubble"><div>'+data[key].message +'</div>';
                        shtml +='<p class="time">'+messageTimeSet(data[key].createdDatetime) +'</p></div></div>';
                    }else{
                        shtml += '<div class="chat-item me" tabindex="0"><div class="bubble">';
                        shtml += '<div>' + data[key].message + '</div>';
                        shtml += '<p class="time">'+messageTimeSet(data[key].createdDatetime) +'</p></div></div>';
                    }
                }
            }
            $('#chatList').html(shtml);
            $('.chat-item').focus();
        }
    });
}
function postChat() {
    var chatUserId = RiderChatUserId;
    var message = $('#chatTextarea').val();
    $.ajax({
        url: "/postChat",
        type: 'post',
        data: {
            chatUserId : chatUserId,
            message : message
        },
        dataType: 'json',
        success: function (data) {
            getChatList(chatUserId, chatUserName);
            $('#chatTextarea').val("");
        }
    });
}
function putRiderReturnTime(riderId) {
    $.ajax({
        url: "/putRiderReturnTime",
        type: 'put',
        data: {
            id : riderId
        },
        dataType: 'json',
        success: function (data) {
            getRiderList();
        }
    });
}
$(function() {
    $('input[type="checkbox"]').on('click',function() {
        getRiderList();
    });
});
/*]]>*/
