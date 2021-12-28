var storeLatitude = parseFloat($('#storeLatitude').val());
var storeLongitude = parseFloat($('#storeLongitude').val());
var storeId = $('#storeId').val();
var map;
var marker = new Map();
var store;
var rider;
var RiderChatUserId = "";
var chatUserName = "";

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

        // 2021-12-23 KFC에서만 MyRider 체크 해지를 디폴트로 하기
        var bMyRiderChk = true;

        if (my_store.brandCode === "1"){
            bMyRiderChk = false;

            if (localStorage.getItem('myRiderChk') === 'true'){
                bMyRiderChk = true;
            }
        }

        $("#myStoreChk").prop("checked", bMyRiderChk);

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
        this.htChat = {
            textBox : $('#chatTextarea'),
        }

        this.tpl = {
            ridertr : "<tr id='{=RIDER}'>"
            +"<td>{=IDX}</td>"
            +"<td>{=NAME}</td>"
            +"<td>{=STATUS}</td>"
            +"<td>{=STORE}</td>"
            +"<td>{=ORDER}</td>"
            +"<td>{=ETC}</td>"
            +"</tr>",
            riderbutton : "<button class='button h20 {=CLASSMODE}' data-id='{=ID}'>{=REST}</button>"
        };

        this.riderlist = new Map();
        this.isprocchat = false;
        this.isPressCtrl = false;

        // 체크박스 체크 효과 넣기
        // 디폴트 > 15 (전체)
        // 1, 2, 4, 8 (화면 순서대로 1, 2, 4, 8)
        // 비트 플래그 이용
        let chkStatus = Number(window.localStorage.getItem("riderStatusFlag"));

        if (chkStatus <= 0){
            chkStatus = 15;
        }

        // 전체 체크
        if (chkStatus !== 15){

            this.checkBoxs.all.prop("checked", false);

            this.checkBoxs.srchChk.each(function() {
                $(this).prop("checked", false);
                $(this).attr("disabled", false);
                console.log("체크 해제 완료");
            });

            for (let i = 0; i < 4; i++){
                if (Number(chkStatus & Math.pow(2, i)) === Number(Math.pow(2, i))){
                    this.checkBoxs.srchChk[i].checked = true;
                }
            }
        }
    },
    bindEvent: function () {
        this.log("bindEvent");
        this.htLayer.checkbox.bind('click', $.proxy(this.getRiderList, this));
        this.htLayer.button.bind('click', $.proxy(this.onButtonClick, this));
        this.htLayer.list.bind('click', $.proxy(this.onButtonClick, this));
        this.checkBoxs.all.bind('change', $.proxy(this.onCheckBoxClick, this));
        this.checkBoxs.srchChk.bind('change', $.proxy(this.onCheckBoxClick, this));
        this.htChat.textBox.bind('keyup',$.proxy(this.onKeyUp, this));
        this.htChat.textBox.bind('keydown',$.proxy(this.onKeyDown, this))
        this.getRiderList();

        $('.chat-item').last().focus();

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

        // 내매장 소속 라이더만 보기의 옵션 저장
        localStorage.setItem('myRiderChk', $("#myStoreChk").is(":checked"));

        $.ajax({
            url: "/getRiderList",
            type: 'get',
            data: {
            },
            dataType: 'json',
            success: function (data) {

                for (var key in data) {
                    if (data.hasOwnProperty(key)) {
                        var ev = data[key];
                        if( self.riderlist.has(ev.id.toString()) ) {
                            self.riderlist.delete(ev.id.toString());
                        }
                        self.riderlist.set(ev.id.toString(),ev);
                    }
                }

                self.paintRiderList();
            }
        });
    },
    paintRiderList : function(){
        this.log("paintRiderList");
        var riderCount = 0;
        for (let [key, value] of this.riderlist) {
            this.log(key);
            this.log(value);
            if (value != null && typeof value !== 'undefined' ) {
                var ev = value;
                riderCount++;
                var trdata = this.makeRiderRow(riderCount, ev);

                if(trdata == '') {
                    this.removeMarker(ev);
                } else {
                    this.removeMarker(ev);

                    // 활동 중인 라이더만 표기
                    if (ev.working == "1"){
                        this.setMarker(ev);
                    }

                    // this.makeSelectChat(ev);
                    this.htLayer.list.append(trdata);
                }

            }
        }
    },
    setMarker:function(data) {
        this.log("setMarker");
        this.log(data)
        var loc = parseLocation(data.latitude, data.longitude);
        var self = this;
        var mk = new google.maps.Marker({
            position : loc,
            rider : data,
            label : data.name,
            map : map
        });
        mk.addListener('click', function () {
            self.makeSelectChat(this.rider);
        });
        if(marker.has(data.id.toString())) {
            marker.delete(data.id.toString())
        }
        marker.set(data.id.toString(), mk);
    },
    removeMarker : function(ev) {
        this.log("removeMarker");
        try{
            var mk = marker.get(ev.id.toString());
            mk.setMap(null);
            marker.delete(ev.id.toString());
        } catch (e) {

        }
    },
    makeRiderRow:function(i, ev) {
        addon = false;
        if($('#srchChk1').is(':checked') && ev.working==1 && typeof ev.order != "undefined") {
            addon = true;
        }
        if($('#srchChk2').is(':checked') && ev.working==1 && typeof ev.order == "undefined") {
            addon = true;
        }
        if($('#srchChk3').is(':checked') && ev.working==3) {
            addon = true;
        }
        if($('#srchChk4').is(':checked') && ev.working==0){
            addon = true;
        }
        if(!addon) return '';
        if($('#myStoreChk').is(':checked') && ev.riderStore) {
            if (ev.riderStore.id != $('#storeId').val()){
                return '';
            }
        }else if($('#myStoreChk').is(':checked') && !ev.riderStore){
            return '';
        }
        return this.tpl.ridertr
            .replace(/{=IDX}/g,i)
            .replace(/{=RIDER}/g,ev.id)
            .replace(/{=NAME}/g,ev.name)
            .replace(/{=STATUS}/g,this.getStatusInfo(ev))
            .replace(/{=STORE}/g,(ev.riderStore==null?"-":ev.riderStore.storeName))
            .replace(/{=ORDER}/g,(ev.orderCount==null?"-":ev.orderCount))
            .replace(/{=ETC}/g,this.getPutRiderReturnTime(ev))
            ;
    },
    getStatusInfo:function(rowdata) {
        var status = '';
        if (rowdata.working == 1 && typeof rowdata.order != "undefined") {

            // 주문 상태에 따른 변화
            switch (rowdata.order.status) {
                case '1':
                    status = '<i class="ic_txt ic_blue">' + status_assigned + '</i>';
                    break;
                case '2':
                    status = '<i class="ic_txt ic_blue">' + status_pickedup + '</i>';
                    break;
                default:
                    status = '<i class="ic_txt ic_blue">' + status_work + '</i>';
                    break;
            }

            //status = '<i class="ic_txt ic_blue">' + status_work + '</i>';
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
        } else if (e.target.tagName == "TD"){
            this.makeSelectChat(this.riderlist.get(el.parent("tr").attr("id")) );
            // this.makeSelectChat();
        }

    },
    makeSelectChat : function (ev) {
        $('tr').removeClass('selected');
        $('#' + ev.id).addClass('selected');
        RiderChatUserId = ev.chatUserId;
        this.getChatList(ev.chatUserId, ev.name);
        var name =  ev.name + rider_chat_title;
        $('#chatRider').text(name);
        $('#workingStatus').html(this.getStatusInfo(ev));
        if (ev.working == "1"){
            moveMap(ev);
        }
    },
    onCheckBoxClick : function (e) {
        this.log("onCheckBoxChange:"+e.which);
        var el = $(e.target);

        // 21.04.08 라이더 상태 값 저장
        let tmpCheck = Number(localStorage.getItem('riderStatusFlag'));

        if(el.is(this.checkBoxs.all)){
            this.log("ALL checkbox change:"+el.is(":checked"));
            this.checkBoxs.srchChk.each(function() {
                $(this).prop("checked", el.is(":checked"));
                $(this).attr("disabled", el.is(":checked"));
            });

            // 전체 선택 완료에 따른 값 변경
            if (el.is(":checked")){
                tmpCheck = 15;
            }else{
                tmpCheck = 0;
            }

            // 21.04.08 라이더 상태값 저장
            localStorage.setItem('riderStatusFlag', tmpCheck);

            this.getRiderList();
        } else if( el.is(this.checkBoxs.srchChk) ){
            this.log("Other checkbox change:"+el.attr("id"));

            // 부분 선택 시, 선택된 값에 따른 값 변경
            if (tmpCheck === 0){
                tmpCheck = Math.pow(2, Number(this.checkBoxs.srchChk.index(el)));
            }else{
                if (el.is(":checked")){
                    tmpCheck = tmpCheck | Math.pow(2, Number(this.checkBoxs.srchChk.index(el)));
                }else{
                    tmpCheck = tmpCheck & ~Math.pow(2, Number(this.checkBoxs.srchChk.index(el)));
                }
            }

            // 21.04.08 라이더 상태값 저장
            localStorage.setItem('riderStatusFlag', tmpCheck);
            this.getRiderList();
        }

    },
    onKeyUp : function(e){
        this.log("onKeyUp:"+e.which);
        var el = $(e.target);
        // 쉬프트 엔터는 무시하게함
        if(e.which == 17) this.isPressCtrl=false;

    },
    onKeyDown : function(e){
        this.log("onKeyDown:"+e.which);
        var el = $(e.target);
        // Ctrl + Enter 입력시 발송되게함
        if(e.which == 17) this.isPressCtrl=true;


        if(e.which == 13 && this.isPressCtrl == true) {
            if(el.is(this.htChat.textBox)){
                this.postChat();
            }
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

        // 공유된 라이더의 경우 매칭되지 못하므로
        if (!isProcMessage && RiderChatUserId == chatUserId){
            isProcMessage = true;
        }



        if(isProcMessage) {
            if(data.match('rider_')=='rider_' || type == 'order_picked_up' || type == 'order_completed'){
                this.getRiderList();
                if(map_region){
                    if(map_region!="tw"){
                        footerRiders();
                    }
                }
            } else if (type=='rider_location_updated'){
                // 우선 마커만 움직임
                //
                updateMarker(objData);
            }
            // if(data.match('order_')=='order_'){
            //     footerOrders();
            // }
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
        this.log("getChatList:"+chatUserId);
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
        this.log("postChat");
        var chatUserId = RiderChatUserId;
        var message = this.htChat.textBox.val();
        if(!this.isprocchat) {
            this.isprocchat = true;
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
                        self.isprocchat = false;
                        self.getChatList(chatUserId, chatUserName);
                        self.htChat.textBox.val("");

                    },
                    error : function () {
                        self.isprocchat = false;
                    }
                });
            }
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
// 사파리 오픈시 invalid date 오류 제거를 위한 정규식 변경 적용
function messageTimeSet(time) {
    if(time){
        var t = time.split(/[- :]/);
        var d = new Date(t[0], t[1]-1, t[2], t[3], t[4], t[5]);
        return $.datepicker.formatDate('mm/dd ', d) + ('0' + d.getHours()).slice(-2) + ':' + ('0' + d.getMinutes()).slice(-2);
    } else {
        "-"
    }
    // return new Date(time).toLocaleTimeString();
}

var cntrider = null;

function initMap() {
    store = parseLocation(storeLatitude, storeLongitude);
    map = new google.maps.Map(document.getElementById('map'), { zoom: 15, center: store });
    setMyStore();
    cntrider = new DDELib.Riders("#container");

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
function moveMap(data) {
    var loc = parseLocation(data.latitude,  data.longitude);
    map.setCenter(loc);
}

function updateMarker(data) {
    var loc = parseLocation(data.latitude,  data.longitude);
    if(loc != null) {
        var mk = marker.get(data.id);
        mk.setPosition(loc);
    }
}
