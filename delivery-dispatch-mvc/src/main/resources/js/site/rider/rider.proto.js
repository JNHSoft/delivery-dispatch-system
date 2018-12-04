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
    store = parseLocation(storeLatitude, storeLongitude);
    map = new google.maps.Map(document.getElementById('map'), { zoom: 15, center: store });
    setMyStore();
}
function setMyStore(){
    if(store != null) marker[0] = new google.maps.Marker({ position : store, map : map });
}

function parseLocation(lat, lng){
    try {
        return new google.maps.LatLng(lat,lng);
    } catch(err) {
        return null;
    }
}

function updateMarker(data) {
    var loc = parseLocation(data.latitude,  data.longitude);
    if(loc != null) {
        marker[data.id].setPosition(loc);
    }
}

if(typeof DDELib === 'undefined'){
    DDELib = {};
}
DDELib.Riders = function(el){
    this.log("create:"+el);
    this.$el = $(el);
    this.init();

};
DDELib.Riders.prototype = {
    log: function (logstr) {
        console.log(logstr);
    },
    init: function () {
        this.log("init");
        this.initVar();
        this.bindEvent();
        this.makeWebSocket();
    },
    initVar: function () {
        this.log("initVar");

        this.htLayer = {
            list: this.$el.find(".riderList:first"),
            checkbox : $("#myStoreChk"),
            button : this.$el.find("button")
        };
        this.checkBoxs = {
            all : this.$el.find("input:checkbox[name=orderAllChk]"),
            srchChk : this.$el.find("input:checkbox[name=srchChk]")
        }

        this.tpl = {
            ridertr : "<tr id='riderMapId{=RIDER}'>"
            +"<td>{=IDX}</td>"
            +"<td>{=NAME}</td>"
            +"<td>{=STATUS}</td>"
            +"<td>{=STORE}</td>"
            +"<td>{=ORDER}</td>"
            +"<td>{=ETC}</td>"
            +"</tr>",
            riderbutton : "<button class='button h20 {=CLASSMODE}' data-id='{=ID}'>{=REST}</button>"

        };

    },
    bindEvent: function () {
        this.log("bindEvent");
        this.htLayer.checkbox.bind('click', $.proxy(this.getRiderList, this));
        this.htLayer.button.bind('click', $.proxy(this.onButtonClick, this));
        this.checkBoxs.all.bind('change', $.proxy(this.onCheckBoxClick, this));
        this.checkBoxs.srchChk.bind('change', $.proxy(this.onCheckBoxClick, this));

        this.getRiderList();

        $('.chat-item').last().focus();

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


    },
    makeWebSocket : function() {
        var supportsWebSockets = 'WebSocket' in window || 'MozWebSocket' in window;
        var self = this;
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
                        self.riderAlarmMessage(data);
                    });
                } else {
                    alert('It is a browser that does not support Websocket');
                }
            }
        });
    },
    getRiderList : function()  {
        this.log("getRiderList");
        var self = this;
        this.htLayer.list.html(null);
        $.ajax({
            url: "/getRiderList",
            type: 'get',
            data: {
            },
            dataType: 'json',
            success: function (data) {
                self.paintRiderList(data);
            }
        });
    },
    paintRiderList : function(data){
        this.log("paintRiderList");
        var riderCount = 0;
        for (var key in data) {
            if (data.hasOwnProperty(key)) {
                riderCount++;
                var ev = data[key];
                var trdata = this.makeRiderRow(riderCount, ev);
                var addon = false;
                if($('#srchChk1').is(':checked') && data[key].working==1 && typeof data[key].order != "undefined") {
                    addon = true;
                }
                if($('#srchChk2').is(':checked') && data[key].working==1 && typeof data[key].order == "undefined") {
                    addon = true;
                }
                if($('#srchChk3').is(':checked') && data[key].working==3) {
                    addon = true;
                }
                if($('#srchChk4').is(':checked') && data[key].working==0){
                    addon = true;
                }
                if(trdata == '') {
                    this.removeMarker(ev);
                } else {
                    if(addon) {
                        this.removeMarker(ev);
                        this.setMarker(ev);
                        this.htLayer.list.append(trdata);
                    } else {
                        this.removeMarker(ev);
                    }
                }

            }
        }
    },
    setMarker:function(data) {
        this.log("setMarker");
        this.log(data);
        var loc = parseLocation(data.latitude, data.longitude);
        var self = this;
        marker[data.id] = new google.maps.Marker({
            position : loc,
            riderMapId : data.id,
            riderChatUserId : data.chatUserId,
            label : data.name,
            map : map
        });
        marker[data.id].addListener('click', function () {
            chatUserName = this.label;
            RiderChatUserId = this.riderChatUserId;
            $('tr').removeClass('selected');
            $('#riderMapId' + this.riderMapId).addClass('selected');
            self.getChatList(this.riderChatUserId, this.label);
            var name = this.label + rider_chat_title;
            $('#chatRider').text(name);
            $('#workingStatus').html(status);
        });
    },
    removeMarker : function(ev) {
        this.log("removeMarker");
        try{
            marker[ev.id].setMap(null);
        } catch (e) {

        }
    },
    makeRiderRow:function(i, data) {
        if($('#myStoreChk').is(':checked') && data.riderStore) {
            if (data.riderStore.id != $('#storeId').val()){
                return '';
            }
        }else if($('#myStoreChk').is(':checked') && !data.riderStore){
            return '';
        }
        var shtml = this.tpl.ridertr
            .replace(/{=IDX}/g,i)
            .replace(/{=RIDER}/g,data.id)
            .replace(/{=NAME}/g,data.name)
            .replace(/{=STATUS}/g,this.getStatusInfo(data))
            .replace(/{=STORE}/g,(data.riderStore==null?"-":data.riderStore.storeName))
            .replace(/{=ORDER}/g,(data.orderCount==null?"-":data.orderCount))
            .replace(/{=ETC}/g,this.getPutRiderReturnTime(data))
        ;
        return shtml;
    },
    getStatusInfo:function(rowdata) {
        var status = '';
        if (rowdata.working == 1 && typeof rowdata.order != "undefined") {
            status = '<i class="ic_txt ic_blue">' + status_work + '</i>';
        } else if (rowdata.working == 1 && typeof rowdata.order == "undefined") {
            status = '<i class="ic_txt ic_green">' + status_standby + '</i>';
        } else if (rowdata.working == 3) {
            status = '<i class="ic_txt ic_red">' + status_rest + '</i>';
        } else {
            status = '<i class="ic_txt">' + status_off + '</i>';
        }
        return status;
    },
    getPutRiderReturnTime :function(data){
        if(typeof data.order != "undefined" && typeof data.orderStore != "undefined"){
            if(data.riderStore.id == $('#storeId').val() && data.riderStore.id != data.orderStore.id && typeof data.returnTime == "undefined"){
                return this.tpl.riderbutton.replace(/{=CLASSMODE}/g,"rider_return").replace(/{=ID}/g,data.id).replace(/{=REST}/g,"rider_rest");
            }
        }
        return "-";
    },
    putRiderReturnTime : function(riderId) {
        var self = this;
        $.ajax({
            url: "/putRiderReturnTime",
            type: 'put',
            data: {
                id: riderId
            },
            dataType: 'json',
            success: function (data) {
                self.getRiderList();
            }
        });
    },
    onButtonClick : function (e) {
        this.log("onButtonClick:"+e.which);
        var el = $(e.target);
        if(el.hasClass('rider_return')){
            this.log("onButtonClick:rider_return:"+el.attr('data-id'));
            this.putRiderReturnTime(el.attr('data-id'));
        } else if(el.attr("id") == "sendChat") {
            this.postChat();
        }

    },
    onCheckBoxClick : function (e) {
        this.log("onCheckBoxChange:"+e.which);
        var el = $(e.target);
        if(el.is(this.checkBoxs.all)){
            this.log("ALL checkbox change:"+el.is(":checked"));
            this.checkBoxs.srchChk.each(function() {
               $(this).prop("checked", el.is(":checked"));
               $(this).attr("disabled", el.is(":checked"));
            });
            this.getRiderList();
        } else if( el.is(this.checkBoxs.srchChk) ){
            this.log("Other checkbox change:"+el.attr("id"));
            this.getRiderList();
        }
    },
    riderAlarmMessage:function(data) {
        this.log("riderAlarmMessage:"+data);
        // 라이더 push
        var objData = JSON.parse(data);
        var subgroup_id = objData.subGroupId;
        var type = objData.type;
        var store_id = objData.storeId;
        var chatUserId = objData.recvChatUserId;

        var isProcMessage = false;

        if ( !my_store.subGroup && my_store.id == store_id ){
            isProcMessage = true;
        }else if(my_store.subGroup){
            if(subgroup_id == my_store.subGroup.id ){
                isProcMessage = true;
            } else if (my_store.id == store_id){
                isProcMessage = true;
            }
        }
        if(isProcMessage) {
            if(data.match('rider_')=='rider_' || type == 'order_picked_up' || type == 'order_completed'){
                this.getRiderList();
                if(map_region){
                    if(map_region!="tw"){
                        footerRiders();
                    }
                }
            }
            if(data.match('order_')=='order_'){
                footerOrders();
            }
            if(data.match('notice_')=='notice_'){
                noticeAlarm();
            }
            if(data.match('chat_')=='chat_'){
                if(RiderChatUserId == chatUserId){
                    this.getChatList(chatUserId, chatUserName);
                }
            }
            alarmSound(data);
        }
    },
    getChatList :function(chatUserId, riderName) {
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
                        var ev = data[key];
                        if(tmpDate != daySet(ev.createdDatetime)){
                            shtml +='<div class="date"><span>' + daySet(ev.createdDatetime) + '</span></div>';
                            tmpDate = daySet(ev.createdDatetime);
                        }
                        if(ev.chatUserId == chatUserId){
                            shtml +='<div class="chat-item" tabindex="0"><p class="name">'+riderName+'</p>';
                            shtml +='<div class="bubble"><div>'+ev.message +'</div>';
                            shtml +='<p class="time">'+messageTimeSet(ev.createdDatetime) +'</p></div></div>';
                        }else{
                            shtml += '<div class="chat-item me" tabindex="0"><div class="bubble">';
                            shtml += '<div>' + ev.message + '</div>';
                            shtml += '<p class="time">'+messageTimeSet(ev.createdDatetime) +'</p></div></div>';
                        }
                    }
                }
                $('#chatList').html(shtml);
                $('.chat-item').focus();
            }
        });
    },
     postChat: function() {
        var chatUserId = RiderChatUserId;
        var message = $('#chatTextarea').val();
        if(message.trim().length > 0) {
            var self = this;
            $.ajax({
                url: "/postChat",
                type: 'post',
                data: {
                    chatUserId : chatUserId,
                    message : message
                },
                dataType: 'json',
                success: function (data) {
                    self.getChatList(chatUserId, chatUserName);
                    $('#chatTextarea').val("");
                }
            });
        }
    }
};
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
var cntrider = new DDELib.Riders("#container");