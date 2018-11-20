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
$(function() {
    var supportsWebSockets = 'WebSocket' in window || 'MozWebSocket' in window;
    $.ajax({
        url : "/websocketHost",
        success : function (websocketHost) {
            if (supportsWebSockets) {
                var socket = io(websocketHost, {
                    path: '/socket.io', // 서버 사이드의 path 설정과 동일해야 한다
                    transports: ['websocket'] // websocket만을 사용하도록 설정
                });
                socket.on('message', function(data){
                    //data.match를 type 으로 바꿔야 합니다
                    var objData = JSON.parse(data);
                    var type = objData.type;
                    var subgroup_id = objData.subGroupId;
                    var store_id = objData.storeId;
                    var chatUserId = objData.recvChatUserId;
                    if(!my_store.subGroup && my_store.id == store_id){
                        riderAlarmMessage(data);
                    }else if(my_store.subGroup){
                        if(subgroup_id == my_store.subGroup.id){
                            riderAlarmMessage(data);
                        } else if (my_store.id == store_id){
                            riderAlarmMessage(data);
                        }
                    }
                    //이부분도 riderAlarmMessage에 넣는게 좋을 것 같습니다
                    if(data.match('chat_')=='chat_'){
                        if(RiderChatUserId == chatUserId){
                            getChatList(chatUserId, chatUserName);
                        }
                    }

                });
                $(function() {
                    /*$('#sendChat').click(function(){
                        socket.emit('message', "push_data:{type:chat_send, recv_chat_user_id:"+RiderChatUserId+"}");//data보내는부분
                    });*/
                })
            } else {
                alert('websocket을 지원하지 않는 브라우저입니다.');
            }
        }
    });

    $('.chat-item').last().focus();
    getRiderList();
    $("#orderAllChk").click(function () {
        if(this.checked){
            $("input[name=srchChk]:checkbox").each(function() {
                $(this).prop("checked", "checked");
                $(this).attr("disabled", true);
            });
        }else{
            $("input[name=srchChk]:checkbox").each(function() {
                $(this).removeAttr("checked");
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
// 라이더 push
function riderAlarmMessage(data) {
    var objData = JSON.parse(data);
    var type = objData.type;

    if(type=='rider_update' || type == 'order_completed'){
        getRiderList();
        if(map_region){
            if(map_region!="tw"){
                footerRiders();
            }
        }
        // 라이더 위치정보 업데이트 받았을때
    } else if (type=='rider_location_updated'){
        var latitude = parseFloat(objData.latitude);
        var longitude = parseFloat(objData.longitude);
        var location = {lat: latitude, lng: longitude};
        var riderId = objData.id;
        locationMarker(location,riderId);
    }


    if(data.match('order_')=='order_'){
        footerOrders();
    }
    if(data.match('notice_')=='notice_'){
        noticeAlarm();
    }
    alarmSound(data);
}
// 라이더 위치정보 변경된걸로 마커 찍기
function locationMarker(location,riderId) {
    marker[riderId].setPosition(location);
}



function addMarker(location, data, i, status) {
    if($('#myStoreChk').prop('checked') && data.riderStore) {
        if (data.riderStore.id != $('#storeId').val()){
            return i;
        }
    }else if($('#myStoreChk').prop('checked') && !data.riderStore){
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
            chatUserName = this.label;
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
            console.log(data);
            for (var j = 0; j < marker.length; j++) {
                console.log(marker[j]);
                if (marker[j] !== undefined) {
                    marker[j].setMap(null);
                }
            }
            marker=[];
            // 마커 배열 0번째는 스토어의 위치다
            marker[0] = new google.maps.Marker({
                position : {lat: storeLatitude, lng: storeLongitude},
                map : map
            });
            var riderCount = 0;
            for (var key in data) {
                if (data.hasOwnProperty(key)) {
                    riderCount++;
                    var latitude = parseFloat(data[key].latitude);
                    var longitude = parseFloat(data[key].longitude)
                    var location = {lat: latitude, lng: longitude};
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
                        i = addMarker(location, data[key], data[key].id, $status);
                        shtml += gridRiderList(data[key], $status, riderCount);
                    }
                    if($('#srchChk2').prop('checked') && data[key].working==1 && typeof data[key].order == "undefined") {
                        i = addMarker(location, data[key], data[key].id, $status);
                        shtml += gridRiderList(data[key], $status, riderCount);
                    }
                    if($('#srchChk3').prop('checked') && data[key].working==3) {
                        i = addMarker(location, data[key], data[key].id, $status);
                        shtml += gridRiderList(data[key], $status, riderCount);
                    }
                    if($('#srchChk4').prop('checked') && data[key].working==0){
                        i = addMarker(location, data[key], data[key].id, $status);
                        shtml += gridRiderList(data[key], $status, riderCount);
                    }
                }
            }
            $('#riderList').html(shtml);
        }
    });
}
function gridRiderList(data, $status, riderCount) {
    if($('#myStoreChk').prop('checked') && data.riderStore) {
        if (data.riderStore.id != $('#storeId').val()){
            return '';
        }
    }else if($('#myStoreChk').prop('checked') && !data.riderStore){
        return '';
    }
    var shtml = "";
    shtml += "<tr id='riderMapId"  + data.id + "'><td>" +riderCount+"</td>";
    shtml += "<td>"+data.name+"</td>";
    shtml += "<td>"+$status+"</td>";
    if(data.riderStore){
        shtml += "<td>"+data.riderStore.storeName+"</td>";
    }else {
        shtml +="<td>-</td>";
    }
    // 상태가 배정,픽업이고 배정받은지 가장 오래된 주문의 상점 이름
    /*if(data.orderStore){
        shtml += "<td>"+data.orderStore.storeName+"</td>";
    }else {
        shtml +="<td>-</td>";
    }*/

    // 주문상점 이름 -> 라이더가 들고있는 주문 갯수로 변경
    if(data.orderCount){
        shtml += "<td>"+data.orderCount+"</td>";
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
