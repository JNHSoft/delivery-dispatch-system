/*<![CDATA[*/
var storeLatitude = parseFloat($('#storeLatitude').val());
var storeLongitude = parseFloat($('#storeLongitude').val());
var storeId = $('#storeId').val();
var map;
var marker = [];
var store;
var rider;
function initMap() {
    store = {lat: storeLatitude, lng: storeLongitude};
    map = new google.maps.Map(document.getElementById('map'), {
        zoom: 15,
        center: store
    });
}
$(function() {
    $(window).load(function () {
        $('.chat-item').last().focus();
        getRiderList(storeId);
        getChatList("42");
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
    });
    $('.table tr').click(function () {
        $(this).addClass('selected').siblings().removeClass('selected');
        $('.chat_wrap').addClass('active');
    })

    $('.chat_wrap .close').click(function (e) {
        e.preventDefault();
        $('.table tr').removeClass('selected');
        $('.chat_wrap').removeClass('active');
        $(window).scrollTop(0);
    })
    $('#map').css('z-index', '10000');

});

function addMarker(location, data, i) {
    if($('#myStoreChk').prop('checked') && data.riderStore.id != $('#storeId').val()) {
        return i;
    }
    if(data.latitude != null){
        console.log(i);
        marker[i] = new google.maps.Marker({
            position : location,
            riderMapId : data.id,
            label : data.name,
            map : map
        });
        marker[i].addListener('click', function () {
            console.log(this.riderMapId);
            $('tr').removeClass('selected');
            $('#riderMapId' + this.riderMapId).addClass('selected');
        });
        return i + 1;
    }
    return i;
}

function getRiderList(storeId) {
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
        console.log(data);
        for (var key in data) {
            if (data.hasOwnProperty(key)) {
                    var lattitude = parseFloat(data[key].latitude);
                    var longitude = parseFloat(data[key].longitude)
                    var location = {lat: lattitude, lng: longitude};
                if(data[key].working==1 && typeof data[key].order != "undefined"){
                    $status = '<i class="ic_txt ic_blue">'+ 'status_work' +'</i>';
                }else if (data[key].working==1 && typeof data[key].order == "undefined"){
                    $status = '<i class="ic_txt ic_green">'+ 'status_standby' +'</i>';
                }else if (data[key].working==3){
                    $status = '<i class="ic_txt ic_red">'+ 'status_rest' +'</i>';
                }else{
                    $status = '<i class="ic_txt">'+ 'status_off' +'</i>';
                }
                if($('#srchChk1').prop('checked') && data[key].working==1 && typeof data[key].order != "undefined") {
                    i = addMarker(location, data[key], i);
                    shtml += gridRiderList(data[key], $status);
                }
                if($('#srchChk2').prop('checked') && data[key].working==1 && typeof data[key].order == "undefined") {
                    i = addMarker(location, data[key], i);
                    shtml += gridRiderList(data[key], $status);
                }
                if($('#srchChk3').prop('checked') && data[key].working==3) {
                    i = addMarker(location, data[key], i);
                    shtml += gridRiderList(data[key], $status);
                }
                if(data[key].working==0){
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
    if(typeof data.orderStore != "undefined"){
        shtml += "<td>"+data.orderStore.storeName+"</td>";
    }else {
        shtml +="<td>-</td>";
    }
    if(typeof data.order != "undefined" && typeof data.orderStore != "undefined"){
        if(data.riderStore.id != data.orderStore.id && typeof data.returnTime == "undefined"){
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
    var today = new Date();
    var d = new Date(time);
    if(today == d){
        return  'today';
    }
    return $.datepicker.formatDate('yyyy-mm-dd ', d);
}
function messageTimeSet(time) {
    var d = new Date(time);
    return d.toString("hh:mm tt");
}
function getChatList(chatUserId, riderName) {
    var shtml = "";
    $.ajax({
        url: "/getChatList",
        type: 'get',
        data: {
            chatUserId : chatUserId
        },
        dataType: 'json',
        success: function (data) {
            console.log(data);
            for (var key in data) {
                if (data.hasOwnProperty(key)) {
                    shtml +='<div class="date"><span>' + daySet(data[key].createdDatetime) + '</span></div>';
                    shtml +='<div class="chat-item" tabindex="0"><p class="name">'+riderName+'</p>';
                    shtml +='<div class="bubble"><div>'+data[key].message +'</div>';
                    shtml +='<p class="time">'+messageTimeSet(data[key].createdDatetime) +'</p></div></div>';
                    shtml
                }
            }
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
            console.log(data);
            getRiderList($('#storeId').val());
        }
    });
}
$(function() {
    $('input[type="checkbox"]').on('click',function() {
        getRiderList($('#storeId').val());
    });
});
/*]]>*/
