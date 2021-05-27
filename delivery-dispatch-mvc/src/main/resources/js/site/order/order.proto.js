/*<![CDATA[*/
let loading = $('<div id="loading"><div><p style="background-color: #838d96"/></div></div>').appendTo(document.body).hide();
let orderArray = [];

if(typeof DDELib === 'undefined'){
    DDELib = {};
}
DDELib.Orders = function(el){
    this.log("create:"+el);
    this.$el = $(el);
    this.init();
};
DDELib.Orders.prototype = {
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
            statusArray: this.$el.find("#statusArray"),
            grid: this.$el.find("#jqGrid"),
            gridpager: this.$el.find("#jqGridPager"),
            detail : this.$el.find(".state_detail:first"),
            checkbox : $("#myStoreChk"),
            button : this.$el.find("button")
        };
        this.checkBoxs = {
            myStoreChk : this.$el.find("input:checkbox[name=myStoreChk]"),
            all : this.$el.find("input:checkbox[name=srchAllChk]"),
            srchChk : this.$el.find("input:checkbox[name=srchChk]")
        }
        this.tpl = {
            button : "<button class='button h20 {=CLASSMODE}' data-id='{=ID}'>{=TEXT}</button>",
            option : "<option value='{=VALUE}'>{=TEXT}</option>"
        };
        this.storeId = this.checkBoxs.myStoreChk.val();
        //this.statusArray = ["0", "1", "2", "3", "4", "5"];
        this.htLayer.statusArray.val(statusArray);
        if (map_region) {
            if (map_region == "tw") {
                $('#combinedChk').attr("disabled", true);
            }
        }
        this.mydata = [];
        this.lastModifyDatetime = null;

        // 21-04-22 상태에 따른 값 체크
        let orderAllStatus = Number(localStorage.getItem('orderAll'));

        if (orderAllStatus != 1){
            let chkCount = 0;
            let totalCheck = this.checkBoxs.srchChk.length;

            this.checkBoxs.all.prop("checked", false);
            this.checkBoxs.srchChk.each(function() {
                $(this).prop("checked", false);
                $(this).attr("disabled", false);
            });

            this.checkBoxs.srchChk.each(function (index, obj){
                if (localStorage.getItem(obj.id.toString()) != null){
                    obj.checked = true;
                    chkCount++;
                }
            });

            if (chkCount == totalCheck || chkCount == 0){
                this.checkBoxs.all.prop("checked", true);
                this.checkBoxs.srchChk.each(function() {
                    if (chkCount == 0){
                        $(this).prop("checked", true);
                    }
                    $(this).attr("disabled", true);
                });
                localStorage.setItem('orderAll', 1);
            }
        }
    },
    bindEvent: function () {
        this.log("bindEvent");
        this.htLayer.checkbox.bind('click', $.proxy(this.getRiderList, this));
        this.htLayer.grid.bind('click', $.proxy(this.onButtonClick, this));
        this.checkBoxs.all.bind('change', $.proxy(this.onCheckBoxClick, this));
        this.checkBoxs.srchChk.bind('change', $.proxy(this.onCheckBoxClick, this));
        this.checkBoxs.myStoreChk.bind('change', $.proxy(this.onCheckBoxClick, this));
        $('#orderUpdate').bind('click', $.proxy(this.orderConfirm, this));
        $('#thirdPartyUpdate').bind('click', $.proxy(this.thirdPartyUpdate, this));
        $('#putOrderCancel').bind('click', $.proxy(this.putOrderCancel, this));

        var self = this;
        $('.state_wrap .btn_close').click(function (e) {
            e.preventDefault();
            self.onCloseDetail();
        });
        $('input[name=combinedChk]:checkbox').click(function () {
            if (this.checked) {
                $('#selectCombined').attr("disabled", false);
            } else {
                $('#selectCombined').attr("disabled", true);
            }
        });
        $('#selectedRider').on('change', function () {
            $('.riderPhone').css('display', 'none');
            $('#rider' + $(this).val()).css('display', 'block');

        });
        $('.changeChkInputMessage').find('.input, #combinedChk, #selectCombined, #selectPaid').change(function () {
            changeChkInputMessage = true;
        });
        $("#searchButton").click(function () {
            self.log("click searchButton");

            var searchText = $("#searchText").val();
            var filter = {
                groupOp: "OR",
                rules: []
            };
            var select = $("#searchSelect option:selected").val();

            if (select == 'reg_order_id') {
                filter.rules.push({
                    field: select,
                    op: "eq",
                    data: searchText
                });
            } else if (select == 'all') {
                /*filter.rules.push({
                    field : 'id',
                    op : "eq",
                    data : searchText
                });*/
                filter.rules.push({
                    field: 'address',
                    op: "cn",
                    data: searchText
                });
                filter.rules.push({
                    field: 'reg_order_id',
                    op: "eq",
                    data: searchText
                });
                filter.rules.push({
                    field: 'rider',
                    op: "cn",
                    data: searchText
                });
            } else {
                filter.rules.push({
                    field: select,
                    op: "cn",
                    data: searchText
                });
            }
            var grid = jQuery('#jqGrid');
            grid[0].p.search = filter.rules.length > 0;
            $.extend(grid[0].p.postData, {filters: JSON.stringify(filter)});
            grid.trigger("reloadGrid", [{page: 1}]);
        });
        this.setGrid();
        this.getOrderList();
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
                        transports: ['websocket'], // websocket만을 사용하도록 설정
                        secure: true
                    });
                    socket.on('message', function(data){
                        //data.match를 type 으로 바꿔야 합니다
                        self.orderAlarmMessage(data);
                    });
                } else {
                    alert('It is a browser that does not support Websocket');
                }
            }
        });
    },
    onButtonClick : function (e) {
        this.log("onButtonClick:"+e.which);
        var el = $(e.target);
        if(el.hasClass('putAssignedAdvanceFirst')){
            this.log("onButtonClick:putAssignedAdvanceFirst:"+el.attr('data-id'));
            this.putAssignedAdvanceFirst(el.attr('data-id'));
        } else if(el.attr("id") == "sendChat") {
            //this.postChat();
        }

    },
    onCheckBoxClick : function (e) {
        this.log("onCheckBoxChange:"+e.which);
        var el = $(e.target);

        // 21.04.22 체크 값을 가져온다.

        if(el.is(this.checkBoxs.all)){
            this.log("ALL checkbox change:"+el.is(":checked"));
            this.checkBoxs.srchChk.each(function() {
                $(this).prop("checked", el.is(":checked"));
                $(this).attr("disabled", el.is(":checked"));
            });

            // 21.04.22 전체 선택에 따른 값 변경
            localStorage.setItem('orderAll', Number(el.is(":checked")));

            // value 값 없애기
            this.checkBoxs.srchChk.each(function (index, obj){
                localStorage.removeItem(obj.id.toString())
            });

            this.getOrderList();
        } else if( el.is(this.checkBoxs.srchChk) ){
            this.log("Other checkbox change:"+el.attr("id"));
            this.getOrderList();

            console.log("ID를 잘못 갖고 왔니?");
            console.log(el.attr("id"));

            if (el.is(":checked")){
                localStorage.setItem(el.attr("id"), "true");
            }else{
                localStorage.removeItem(el.attr("id"));
            }

            this.log("Other checkValus:"+ this.checkStatusValus());
        } else if( el.is(this.checkBoxs.myStoreChk) ){
            this.log("MyStoreChk checkbox change:"+el.attr("id"));
            this.getOrderList();
        }
        this.onCloseDetail();
    },
    checkStatusValus:function(){
        this.log("checkStatusValus:");
        var arr = [];
        /*
        if(this.checkBoxs.all.is(":checked")) {
           return  this.statusArray;
        }
        */
        this.checkBoxs.srchChk.each(function() {
            if($(this).is(":checked")) {
                arr.push($(this).val());
                if($(this).val() == "0") {
                    arr.push("5");
                }
                
                // KFC 브랜드이면서 도착을 선택 시 완료 값이 보이게
                if (my_store.brandCode == "1" && $(this).val() == "6"){
                    arr.push("3");
                }
            }
        });
        return arr;
    },
    getOrderList : function(order_id) {
        this.log("getOrderList:"+order_id);
        var statusArray = this.checkStatusValus();
        this.log("checkStatusValus:"+statusArray.join());
        this.log("modifiedDatetime:"+this.lastModifyDatetime);
        var self = this;
        /*
        data: {
                statusArray: statusArray.filter(n => n), //null 제거
                status: statusArray.join(''),
                modifiedDatetime :  this.modifiedDatetime
             },
         */
        var send_data;
        if(typeof order_id !== "undefined") {
            send_data = {
                id : order_id
            }
        } else {
            send_data = {
                modifiedDatetime :  this.lastModifyDatetime
            }
        }

        $.ajaxSettings.traditional = true;// ajax 배열 던지려면 필요함
        $.ajax({
            url: "/getOrderList",
            type: 'get',
            data: send_data,
            dataType: 'json',
            success: function(data) {
                self.log("getOrderList result");
                self.log(data);
                orderArray = [];

                if(self.lastModifyDatetime == null) {
                    self.log("firstdata");
                    self.lastOrderCount = 0;
                    currentOrderList.clear();
                }
                for (var key in data) {
                    self.log("checkdata");
                    if (data.hasOwnProperty(key)) {
                        var ev = data[key];
                        if(currentOrderList.has(ev.id.toString())) {
                            currentOrderList.delete(ev.id.toString());
                        }
                        currentOrderList.set(ev.id.toString(), ev);
                    }
                }
                self.paintOrderList();

            }
        });
    },
    paintOrderList:function() {
        this.log("paintOrderList");
        var statusArray = this.checkStatusValus();
        this.mydata = [];
       // data = currentOrderList;

        //this.log("statusArray:"+statusArray.join());
        this.log("currentOrderList:"+currentOrderList.size);

        for (let [key, value] of currentOrderList) {
            console.log(key);
            console.log(value);
            //if (currentOrderList.hasOwnProperty(key)) {
                var ev = value;
                var tmpdata = this.makeRowOrder(ev);
                //this.log("ev:"+ev.status);
                if( $.inArray(ev.status, statusArray) > -1 ) {

                    if (this.checkBoxs.myStoreChk.is(":checked")) {
                        if (ev.storeId ==  this.storeId) {
                            this.mydata.push(tmpdata);
                        }
                    } else {
                        this.mydata.push(tmpdata);
                    }
                } else {
                   // this.log("not have");
                }
                this.log("modifiedDatetime:"+ ev.modifiedDatetime);
                if(typeof ev.modifiedDatetime !== "undefined" && ev.modifiedDatetime != null  ){
                    this.log("modifiedDatetime:"+ this.lastModifyDatetime);

                    if(this.lastModifyDatetime == null) {
                        this.log("modifiedDatetime set:"+ ev.modifiedDatetime);
                        this.lastModifyDatetime = ev.modifiedDatetime;
                    } else if(this.lastModifyDatetime < ev.modifiedDatetime) {
                        this.log("modifiedDatetime change:"+ ev.modifiedDatetime);
                        this.lastModifyDatetime = ev.modifiedDatetime;
                    }
                }
            //}
        }
        //if (this.mydata) {
            this.reloadGrid();
        //}

    },
    setGrid : function(){
        var self = this;
        this.htLayer.grid.jqGrid({
            datatype: "local",
            data: this.mydata,
            page:1,
            colModel: [
                {label: order_reg_order_id, name: 'reg_order_id', width: 80, align: 'center'},
                {label: order_reg_order_id, name: 'origin_reg_order_id', width: 80, align: 'center', hidden: true},
                {label: order_status, name: 'state', width: 80, align: 'center'},
                {label: order_id, name: 'id', width: 80, align: 'center', hidden: true},
                {label: order_created, name: 'time1', width: 80, align: 'center'},
                {label: order_address, name: 'address', width: 200},
                {label: order_message, name: 'message', width: 80, align: 'center'},
                {label: order_customer_phone, name: 'phone', width: 80, align: 'center'},
                {label: order_cooking, name: 'time2', width: 80, align: 'center'},
                {label: order_payment, name: 'pay', width: 80, align: 'center', hidden:regionLocale == "zh_HK"?true:false},
                {label: order_assigned, name: 'time3', width: 80, align: 'center'},
                {label: order_pickedup, name: 'time4', width: 80, align: 'center'},
                {label: order_arrived, name: 'time8', width: 80, align: 'center'},
                {label: order_completed, name: 'time5', width: 80, align: 'center', hidden:my_store.brandCode == "1"?true:false},
                {label: order_return, name: 'time6', width: 80, align: 'center'},
                {label: order_reserved, name: 'time7', width: 80, align: 'center'},
                {label: rider_name, name: 'rider', width: 80, align: 'center'},
                {label: order_assigned_advance, name: 'button', width: 80, align: 'center'},
                {label: store_code, name: 'storeCode', width: 80, align: 'center'},
                {label: "", name: 'orderbystatus', width: 80, align: 'center'},
                {label: "", name: 'assignedFirst', width: 80, align: 'center'}
            ],
            height: 680,
            autowidth: true,
            viewrecords: true,
            rowNum: 20,
            rownumbers: true, // show row numbers
            rownumWidth: 25, // the width of the row numbers columns
            pager: "#jqGridPager",
            ondblClickRow: function (rowid, icol, cellcontent, e) {
                var rowData = jQuery(this).getRowData(rowid);
                var orderId = rowData['origin_reg_order_id'];
                changeChkInputMessage = false;
                self.getOrderDetail(orderId);//상세보기 열기
                setTimeout(function () {
                    $(window).trigger('resize');
                }, 300)//그리드 리사이즈
            },
            loadComplete: function (data) {
                // let ids = $("#jqGrid").getDataIDs();

                // $.each(ids, function (idx, rowId) {
                //     if (orderArray.findIndex((value) => value == rowId) > -1){
                //         $("#jqGrid").setRowData(rowId, false, {background: '#FFAA55'});
                //     }
                // });
            }
        });
        resizeJqGrid('#jqGrid'); //그리드 리사이즈
    },
    reloadGrid : function(){
        this.log("reloadGrid");
        this.htLayer.grid.clearGridData();
        this.htLayer.grid.jqGrid('hideCol',["orderbystatus","assignedFirst"])
        this.htLayer.grid.jqGrid('setGridParam', {data:this.mydata , multiSort:true})
            .jqGrid('sortGrid', 'orderbystatus', true, 'asc')
            .jqGrid('sortGrid', 'assignedFirst', true, 'desc')
            .jqGrid('sortGrid', 'time7', true, 'desc')
            .jqGrid('sortGrid', 'time1', true, 'desc')
            .jqGrid('sortGrid', 'id', true, 'desc');
        this.htLayer.grid.trigger("reloadGrid");
        setTimeout(function () {
            $(window).trigger('resize');
        }, 300)//그리드 리사이즈

    },
    makeRowOrder : function(ev,) {
        //this.log("makeRowOrder");
        var tmpdata = new Object();
        // let directionsService = new google.maps.DirectionsService;

        //tmpdata.No = i;
        tmpdata.state = this.getStatusInfo(ev.status);
        tmpdata.id = ev.id;
        tmpdata.time1 = timeSet2(ev.createdDatetime);
        tmpdata.address = ev.address;
        tmpdata.reg_order_id = (ev.regOrderId)?regOrderIdReduce(ev.regOrderId):'-';
        tmpdata.origin_reg_order_id = (ev.regOrderId)?ev.regOrderId:'-';
        tmpdata.time2 = ev.cookingTime;
        tmpdata.pay = this.getPayInfo(ev.paid);
        tmpdata.message = (!ev.message)?"-":ev.message;
        tmpdata.phone = (!ev.phone)?"-":ev.phone;
        tmpdata.time3 = (!ev.assignedDatetime )?"-":timeSet2(ev.assignedDatetime);
        tmpdata.time4 = (!ev.pickedUpDatetime )?"-":timeSet2(ev.pickedUpDatetime);

        // Order Completed
        tmpdata.time5 = timeSet2(ev.completedDatetime) ;//checkTime(ev.pickedUpDatetime, ev.completedDatetime);

        // Order Return
        tmpdata.time6 = this.getPickupTime(ev);

        // 21-04-14 예약 유무에 따른 빨간색 표기 삭제 # Resrvation
        tmpdata.time7 = timeSet2(ev.reservationDatetime);
        // 2020.05.12 고객 집 앞 도착 시간 # Arrived
        //tmpdata.time8 = (!ev.arrivedDatetime )?"-":timeSet2(ev.arrivedDatetime);
        tmpdata.time8 =  checkTime(ev.pickedUpDatetime, ev.arrivedDatetime); //timeSet2(ev.arrivedDatetime);

        // 서드 파티 추가
        tmpdata.rider = this.getRiderOrThirdTypeName(ev);
        tmpdata.button = this.makeButtonOrderData(ev,'putAssignedAdvanceFirst');

        // 스토어 코드 추가
        tmpdata.storeCode = ev.store.code;

        // 20.05.12 상태 값추가로 인하여 정렬 흐트림 방지
        // tmpdata.orderbystatus = (ev.status == 5)? 0 : ev.status ;
        switch (ev.status) {
            case '0':
            case '5':
                tmpdata.orderbystatus = 0;
                break;
            case '1':
                tmpdata.orderbystatus = 1;
                break;
            case '2':
                tmpdata.orderbystatus = 2;
                break;
            case '3':
                tmpdata.orderbystatus = 4;
                break;
            case '4':
                tmpdata.orderbystatus = 5;
                break;
            case '6':
                tmpdata.orderbystatus = 3;
                break;
            default:
                tmpdata.orderbystatus = 7;
        }

        tmpdata.assignedFirst =  ev.assignedFirst;
        //this.arrivedTimeCheck(ev, directionsService);

        /// 2020.05.14 Google Map API 작업
        switch (ev.status) {
            case '0':
            case '1':
            case '2':
            case '5':
                // // 단순 시간 비교 시에도 예상시간이 지난 경우에도 표기
                // if (convertDateTime(ev.reservationDatetime) < new Date()){
                //     orderArray.push(ev.id);
                // } else if (ev.status == '2'){
                //     this.arrivedTimeCheck(ev, directionsService);
                // }
                // break;
        }

        return tmpdata;
    },
    makeButtonOrderData(ev, btype){
        var button = '';
        if(btype == "putAssignedAdvanceFirst") {
            if ($('#assignmentStatus').val() == "0") {
                if (ev.assignedFirst == null && (ev.status == 0 || ev.status == 5)) {
                    button = this.tpl.button.replace(/{=CLASSMODE}/g,btype).replace(/{=ID}/g,ev.regOrderId).replace(/{=TEXT}/g,order_assigned_advance );
                } else {
                    button = "";
                }
            } else {
                button = "";
            }
        }
        return button;
    },
    getRiderOrThirdTypeName:function(ev) {
        var str = '';
        if (ev.thirdParty) {
            str = ev.thirdParty.name;
        } else if (!ev.rider) {
            str = "-";
        } else {
            str = ev.rider.name;
        }
        return str;
    },
    getStatusInfo:function(status) {
        var str = '';
        if (status == 0 || status == 5) {
            str = '<i class="ic_txt ic_green">' + status_new + '</i>';
        }
        else if (status == 1) {
            str = '<i class="ic_txt ic_blue">' + status_assigned + '</i>';
        }
        else if (status == 2) {
            str = '<i class="ic_txt ic_blue">' + status_pickedup + '</i>';
        }
        // 2020.05.08 Arrived Status 추가
        else if (status == 6) {
            str = '<i class="ic_txt ic_blue">' + status_arrived + '</i>';
        }
        else if (status == 3) {
            str = '<i class="ic_txt">' + status_completed + '</i>';
        }
        else {
            str = '<i class="ic_txt ic_red">' + status_canceled + '</i>';
        }
        return str;
    },
    getPayInfo:function(paid) {
        var str = '';
        if (paid == 0) {
            str = order_payment_cash;
        }
        else if (paid == 1) {
            str = order_payment_card;
        }
        else if (paid == 2) {
            str = order_payment_prepayment;
        }
        else if (paid == 3) {
            str = order_payment_service;
        } else {
            str = "-";
        }
        return str;
    },
    getPickupTime:function(ev){
        var str = '';
        if (ev.pickedUpDatetime && ev.completedDatetime && ev.returnDatetime) {
            if (diffTime(ev.pickedUpDatetime, ev.returnDatetime) == 1) {
                str = '<span style="color: red">' + timeSet2(ev.returnDatetime) + '</span>';
            } else if (diffTime(ev.completedDatetime, ev.returnDatetime) == 1) {
                str = '<span style="color: red">' + timeSet2(ev.returnDatetime) + '</span>';
            } else {
                str = timeSet2(ev.returnDatetime);
            }
        } else if (ev.returnDatetime) {
            str = '<span style="color: red">' + timeSet2(ev.returnDatetime) + '</span>';
        } else {
            str = "-";
        }

        // if(!ev.returnDatetime){
        //     str = "-";
        // }else{
        //     str = timeSet2(ev.returnDatetime);
        // }

        return str;
    },
    getReserveTime:function(ev){
        var str = '';
        if (!ev.reservationDatetime) {
            str = "-";
        } else if (ev.reservationStatus == 1) {
            str = timeSet2(ev.reservationDatetime);
        } else if (ev.reservationStatus == 0) {
            if (map_region) {
                if (map_region == "tw") {
                    str = '<span style="color: red">' + timeSet2(ev.reservationDatetime) + '</span>';
                } else {
                    str = "-";
                }
            } else {
                str = "-";
            }
        }
        return str;
    },
    getOrderDetail:function(orderId) {
        var regOrderId = "";
        var self = this;
        $.ajax({
            url: "/getOrderDetail",
            type: 'get',
            data: {
                id: orderId
            },
            dataType: 'json',
            success: function (data) {
                selectedOriginOrder = data;

                var status = self.getStatusInfo(data.status);
                regOrderId = (data.regOrderId)?data.regOrderId:"-";
                // $('.tit').html('<h2>'+order_detail + ' - '+ data.id + '('+ regOrderId +')</h2>'+$status);
                $('.tit').html('<h2>' + order_detail + ' - ' + regOrderIdReduce(regOrderId) + '</h2>' + status);
                $('.tit').attr("orderId", regOrderId);

                $('#createdDatetime').html(timeSet(data.createdDatetime));
                $('#reservationDatetime').html(timeSet(data.reservationDatetime));
                $('#assignedDatetime').html(timeSet(data.assignedDatetime));
                $('#pickedUpDatetime').html(timeSet(data.pickedUpDatetime));
                $('#completedDatetime').html(timeSet(data.completedDatetime));
                $('#passtime').html(minusTimeSet(data.createdDatetime, data.completedDatetime));
                $('#menuName').html(data.menuName);
                $('#cookingTime').val(data.cookingTime);
                $('#menuPrice').val(data.menuPrice);
                $('#deliveryPrice').val(data.deliveryPrice);
                $('#totalPrice').val(data.totalPrice);
                $('#selectPaid').val(data.paid).prop("selected", true);

                var statusNewArray = ["0", "5"];

                if (data.combinedOrderId) {
                    /*for (var key in currentOrderList) {
                        if (currentOrderList.hasOwnProperty(key)) {
                            if (currentOrderList[key].regOrderId == data.combinedOrderId) {
                                var shtml = '<option value="' + currentOrderList[key].regOrderId + '">' + order_id + ':' + currentOrderList[key].regOrderId + '|' + order_created + ':' + timeSet(currentOrderList[key].createdDatetime) + '</option>';
                                $('#selectCombined').html(shtml);
                            }
                        }
                    }*/
                    for (let [key, value] of currentOrderList) {
                        if (value.regOrderId == data.combinedOrderId) {
                            var shtml = '<option value="' + value.regOrderId + '">' + order_id + ':' + value.regOrderId + '|' + order_created + ':' + timeSet(value.createdDatetime) + '</option>';
                            $('#selectCombined').html(shtml);
                        }
                    }
                    $('input[name=combinedChk]:checkbox').prop("checked", true);
                    $('#selectCombined').val(data.combinedOrderId).prop("selected", true);
                    if (data.status == "2" || data.status == "3" || data.status == "4"){
                        $('#combinedChk').attr("disabled", true);
                        $('#selectCombined').attr("disabled", true);
                    } else {
                        $('#combinedChk').attr("disabled", false);
                        $('#selectCombined').attr("disabled", false);
                    }
                } else {
                    self.getNewOrderList(statusNewArray);
                    $('input[name=combinedChk]:checkbox').removeAttr("checked");
                    $('#combinedChk').attr("disabled", false);
                    $('#selectCombined').attr("disabled", true);
                }
                self.getMyRiderList(data);



            }/*,
            error : function (request,status,error) {
                // alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
                alert("error: "+order_detail_error);
                location.href = "/order";
            }*/
        });
    },
    putAssignedAdvanceFirst:function(id) {
        this.log("putAssignedAdvanceFirst:"+id);
        var self = this;
        $.ajax({
            url: '/putAssignedAdvanceFirst',
            type: 'put',
            data: {
                id: id
            },
            dataType: 'json',
            success: function (data) {
                self.log("putAssignedAdvanceFirst result:"+data);
                self.getOrderList();
            }
        });
    },
    getNewOrderList:function (statusNewArray) {
        orderArray = [];
        var self = this;
        jQuery.ajaxSettings.traditional = true;// ajax 배열 던지려면 필요함
        $.ajax({
            url: "/getOrderList",
            type: 'get',
            data: {
                statusArray: statusNewArray, //null 제거
                status: statusNewArray.join('')
            },
            datatype: 'json',
            success: function (data) {
                $('#selectCombined').html("");
                for (var key in data) {
                    if (data.hasOwnProperty(key)) {
                        var ev = data[key];
                        if (ev.regOrderId != selectedOriginOrder.regOrderId && !ev.combinedOrderId) {
                            var strvalue = ev.regOrderId;
                            var strtext = order_id + ':' + ev.regOrderId + '|' + order_created + ':' + timeSet(ev.createdDatetime);
                            $('#selectCombined').append(self.tpl.option.replace(/{=VALUE}/g,strvalue).replace(/{=TEXT}/g,strtext));
                        }
                    }
                }

            }
        });
    },
    getMyRiderList: function (orderDetailData) {
        var shtml = '<option value="0">-</option>';
        var shtml2 = '';
        var self = this;
        $.ajax({
            url: "/getMyRiderList",
            type: 'get',
            data: {},
            success: function (data) {
                for (var key in data) {
                    if (data.hasOwnProperty(key)) {
                        var ev = data[key];
                        if (ev.working == "1") {
                            if ($("input[name=myStoreChk]:checkbox").prop("checked")) {
                                if (ev.subGroupRiderRel) {
                                    if ((ev.subGroupRiderRel.storeId === $('#orderMyStoreChk').val())
                                        || (ev.sharedStore !== undefined && ev.sharedStore === "Y" && ev.sharedStoreId === $('#orderMyStoreChk').val())) {
                                        shtml +=  self.tpl.option.replace(/{=VALUE}/,ev.id).replace(/{=TEXT}/g,ev.name);
                                        // '<option value="' + data[key].id + '">' + data[key].name + '</option>';
                                        var tmpId = ev.id;
                                        shtml2 += '<span id="rider' + tmpId + '" class="riderPhone" style="display:none">' + ev.phone + '</span>';
                                    }
                                }
                            } else {
                                if ((ev.subGroupRiderRel.storeId === $('#orderMyStoreChk').val())
                                    || (ev.subGroupRiderRel.storeId !== $('#orderMyStoreChk').val() && ev.returnTime == null)
                                    || (ev.sharedStore !== undefined && ev.sharedStore === "Y" && ev.sharedStoreId === $('#orderMyStoreChk').val())) {
                                    shtml += self.tpl.option.replace(/{=VALUE}/,ev.id).replace(/{=TEXT}/g,ev.name);
                                    var tmpId = ev.id;
                                    shtml2 += '<span id="rider' + tmpId + '" class="riderPhone" style="display:none">' + ev.phone + '</span>';
                                }
                            }
                            $('#selectedRider').html(shtml);
                            $('#riderPhone').html(shtml2);
                        }
                    }
                }
                var riderPhone = '#rider' + orderDetailData.riderId;
                if (orderDetailData.riderId != null && $('#selectedRider option[value='+ orderDetailData.riderId +']').length > 0) {
                    $('#selectedRider').val(orderDetailData.riderId).prop("selected", true);
                    // $(riderPhone).css('display', 'block');
                    $(riderPhone).css('display', 'none');
                } else {
                    $('#selectedRider').val("0").prop("selected", true);
                    $(riderPhone).css('display', 'none');
                }
                $('#memo').html(orderDetailData.message);
                $('#userPhone').html(orderDetailData.phone);
                if (orderDetailData.detailAddress != null) {
                    // $('#userAddress').html(data.address + ', ' + data.detailAddress);
                    $('#userAddress').html(orderDetailData.detailAddress);
                }
                /* else {
                    $('#userAddress').html(data.address);
                }*/
                $('#distance').html(orderDetailData.distance);
                map.setCenter({lat: parseFloat(orderDetailData.latitude), lng: parseFloat(orderDetailData.longitude)});
                marker.setPosition({lat: parseFloat(orderDetailData.latitude), lng: parseFloat(orderDetailData.longitude)});
                $('.state_wrap').addClass('on');

                self.getThirdPartyList(orderDetailData);
            }

        });
    },
    getThirdPartyList:function(orderDetailData) {
        // 서드 파티 추가
        var html = '';
        var self = this;
        $.ajax({
            url: "/getThirdPartyList",
            type: 'get',
            data: {},
            success: function (data) {
                if(!my_store.thirdParty){
                    $('#thirdArea').css('display', 'none');
                    $('#thirdAreaBtn').css('display', 'none');
                }else{
                    var thirdPartyList = my_store.thirdParty.split('|');
                    for (var key in data) {
                        if (data.hasOwnProperty(key)) {
                            var ev = data[key];
                            for (var thirdParty in thirdPartyList) {
                                if (thirdPartyList[thirdParty] == ev.id) {
                                    html += self.tpl.option.replace(/{=VALUE}/,ev.id).replace(/{=TEXT}/g,ev.name);
                                }
                            }
                        }
                    }
                }
                $('#selectedThirdParty').html(html);
                if(orderDetailData.thirdParty){
                    $('#selectedThirdParty').val(orderDetailData.thirdParty.id).prop("selected", true);
                }
            }
        });
    },
    thirdPartyUpdate:function() {
        // 서드파티 업데이트
        // 신규 배정 일때 서드파티로 적용 가능 Nick
        if(selectedOriginOrder.status == 2 || selectedOriginOrder.status == 4){
            alert(order_confirm_thirdparty_status);
            return;
        }else if(selectedOriginOrder.status == 3){
            alert(order_confirm_completed);
            return;
        }
        var result = confirm('['+ $('#selectedThirdParty option:selected').text()+'] ' + order_confirm_thirdparty);
        if(!result){
            return;
        }

        var combinedOrderId = $('#selectCombined').val();
        var isCombined = "";
        if ($('#combinedChk').prop("checked")) {
            isCombined = true;
        } else {
            isCombined = false;
        }

        var regOrderId = selectedOriginOrder.regOrderId;
        var thirdPartyId = $('#selectedThirdParty').val();
        var orderStatus = selectedOriginOrder.status;
        var self = this;

        $.ajax({
            url: "/putOrderThirdParty",
            type: 'put',
            data:JSON.stringify({
                id: regOrderId,
                status: orderStatus,
                combinedOrderId : combinedOrderId,
                isCombined: isCombined,
                thirdParty: {id:thirdPartyId}
            }),
            contentType:'application/json',
            dataType: 'json',
            success: function (data) {
                self.getOrderDetail(selectedOriginOrder.regOrderId);
                self.getOrderList();

            }
        });
    },
    putOrder:function() {
        if (changeChkInputMessage == false) {
            // console.log('안바뀜!!!');
            return;
        }
        // console.log('바뀜!!!');
        var id = $('.tit').attr("orderId");
        var menuName = $('#menuName').val();
        var cookingTime = $('#cookingTime').val();
        var menuPrice = $('#menuPrice').val() ? $('#menuPrice').val() : 0;
        var deliveryPrice = $('#deliveryPrice').val() ? $('#deliveryPrice').val() : 0;
        var paid = $('#selectPaid').val();
        var combinedOrderId = combinedOrderId = $('#selectCombined').val();
        var isCombined = "";
        if ($('#combinedChk').prop("checked")) {
            isCombined = true;
        } else {
            isCombined = false;
        }
        var self = this;
        $.ajax({
            url: '/putOrder',
            type: 'put',
            data: {
                id: id,
                menuName: menuName,
                cookingTime: cookingTime,
                menuPrice: menuPrice,
                deliveryPrice: deliveryPrice,
                paid: paid,
                combinedOrderId: combinedOrderId,
                isCombined: isCombined
            },
            dataType: 'json',
            success: function (data) {
                // self.getOrderDetail(selectedOriginOrder.regOrderId);
                self.getOrderList();
                changeChkInputMessage = false;
                // 기사배정 후 팝업창 닫기 적용 Nick
                self.onCloseDetail();
            }
        });
    },
    putOrderAssignCancel:function () {
        var id = $('.tit').attr("orderId");
        var combinedOrderId = '';
        if ($('#combinedChk').prop("checked")) {
            combinedOrderId = $('#selectCombined').val();
        }
        var self = this;
        $.ajax({
            url: '/putOrderAssignCancle',
            type: 'put',
            data: {
                id: id,
                combinedOrderId: combinedOrderId,
            },
            dataType: 'json',
            success: function (data) {
                self.getOrderDetail(selectedOriginOrder.regOrderId);
                self.getOrderList();
            }
        });
    },
    putAssignedAdvance:function() {
        var id = $('.tit').attr("orderId");
        var riderId = $('#selectedRider').val();
        var combinedOrderId = "";
        if ($('#combinedChk').prop("checked")) {
            combinedOrderId = $('#selectCombined').val();
        }
        // var firstTime = new Date().getTime();
        var self = this;
        $.ajax({
            url: '/putAssignedAdvance',
            type: 'put',
            data: {
                id: id,
                riderId: riderId,
                combinedOrderId: combinedOrderId
            },
            dataType: 'json',
            success: function (data) {
                if (data == false) {
                    alert(alert_order_assign_max);
                } else {
                    self.getOrderList();
                    self.onCloseDetail();
                }
            }
        });
    },
    orderConfirm:function() {
        if (selectedOriginOrder.status == "3") {
            alert(order_confirm_completed);
            return;
        }

        if (selectedOriginOrder.status == "4") {
            alert(order_confirm_canceled);
            return;
        }

        if ($('#selectedRider').val() == '0') {
            if (selectedOriginOrder.riderId) {
                var result = confirm(order_confirm_assigned);
                if (result) {
                    this.putOrderAssignCancel();
                    this.putOrder();
                } else {
                    this.putOrder();
                }
            } else {
                this.putOrder();
            }
        } else {
            if (selectedOriginOrder.riderId) {
                if ($('#selectedRider').val() == selectedOriginOrder.riderId) {
                    this.putOrder();//주문만수정
                } else {
                    this.putOrderAssignCancel();
                    this.putAssignedAdvance();
                    this.putOrder();//기사배정변경
                }
            } else {
                this.putAssignedAdvance();
                this.putOrder();
            }
        }
    },
    putOrderCancel:function () {
        if (selectedOriginOrder.status == "3") {
            alert(order_confirm_completed);
            return;
        }

        if (selectedOriginOrder.status == "4") {
            alert(order_confirm_canceled);
            return;
        }
        var id = $('.tit').attr("orderId");
        var combinedOrderId = $('#selectCombined').val();
        var isCombined = "";
        if ($('#combinedChk').prop("checked")) {
            isCombined = true;
        } else {
            isCombined = false;
        }
        var self = this;
        $.ajax({
            url: '/putOrderCancel',
            type: 'put',
            data: {
                id: id,
                combinedOrderId: combinedOrderId,
                isCombined: isCombined
            },
            dataType: 'json',
            success: function (data) {
                self.getOrderList();
                self.onCloseDetail();
            }
        });
    },
    onCloseDetail:function(){
        $('.state_wrap').removeClass('on');
        setTimeout(function () {
            $(window).trigger('resize');
        }, 300);
    },
    orderAlarmMessage:function(data) {
        this.log("orderAlarmMessage:"+data);
        var objData = JSON.parse(data);
        var subgroup_id = objData.subGroupId;
        var isAlarm = false;

        if (!my_store.subGroup && my_store.id == objData.storeId) {
            isAlarm = true;
        } else if (my_store.subGroup) {
            if (subgroup_id == my_store.subGroup.id) {
                isAlarm = true;
            }
        }
        if( isAlarm ) {
            if (data.match('order_') == 'order_') {
                if(objData.type == 'order_new') {
                    this.getOrderList(objData.orderId);
                } else {
                    if ($('.state_wrap').hasClass('on') && objData.orderId === selectedOriginOrder.regOrderId) {
                        this.getOrderDetail(selectedOriginOrder.regOrderId);
                    }
                    this.getOrderList();
                }
                footerOrders();
            }
            if (data.match('rider_') == 'rider_') {
                if (map_region) {
                    if (map_region == "hk") {
                        footerRiders();
                    }
                }
            }
            if (data.match('notice_') == 'notice_') {
                noticeAlarm();
            }
            alarmSound(data);
        }
    },
    arrivedTimeCheck:function(data, directionsService){
        //var directionsService = new google.maps.DirectionsService;
        let mode = 'DRIVING';

        // directionsService.route({
        //      origin: new google.maps.LatLng(data.rider.latitude, data.rider.longitude)                   /// 출발지
        //     //origin: new google.maps.LatLng(24.987451, 121.552371)
        //     ,destination: new google.maps.LatLng(data.latitude, data.longitude)               /// 도착지
        //     ,travelMode: google.maps.TravelMode[mode]              /// 조회 방법
        //     ,drivingOptions:{
        //          departureTime: new Date() // 현재 시간
        //         ,trafficModel: 'bestguess'  // 조회 방식
        //     }
        //     ,optimizeWaypoints: true
        // },function (response, status) {
        //     if (status === 'OK'){
        //         console.log("Call API = [" + data.id + "]");
        //
        //         var durationInTraffic = response.routes[0].legs.reduce(function (sum, leg) {
        //             return sum + leg.duration.value
        //         },0)/60;
        //
        //         var arriveTrafficTime = new Date((new Date).getTime()+durationInTraffic*60*1000);
        //
        //         if (convertDateTime(data.reservationDatetime) < arriveTrafficTime){
        //             $("#jqGrid").setRowData(data.id, false, {background: '#FFAA55'});
        //         }
        //
        //     }
        // }
        // );
    }
}

var ddeorder = new DDELib.Orders("#container");
var selectedOriginOrder;
var map;
var marker;
var changeChkInputMessage = false;
//var currentOrderList;
let currentOrderList = new Map();

function initMap() {
    var uluru = {lat: 37.5806376, lng: 126.9058433};
    map = new google.maps.Map(document.getElementById('map'), {
        zoom: 15,
        center: uluru
    });
    marker = new google.maps.Marker({
        position: uluru,
        map: map
    });
}
// 시간 비교 함수
function checkTime(time1, time2) {
    var result = "-";
    // 픽업 시간 + 완료 시간이 있다.
    if (time1 && time2) {
        if (diffTime(time1, time2) == 1) {
            result = '<span style="color: red">' + timeSet2(time2) + '</span>';
        } else {
            result = timeSet2(time2);
        }
    } else if (!time1 && time2) {
        result = '<span style="color: red">' + timeSet2(time2) + '</span>';
    }
    return result;
}
// 시간 비교 1분차이
function diffTime(time1, time2) {
    // 픽업시간 - 완료시간
    if (time1 && time2) {
        var time1 = new Date(time1);
        var time2 = new Date(time2);

        if (time2.getTime() - time1.getTime() < 60000) {
            return 1;
        }
    }
    return 0;
}
function timeSet(time) {
    if (time) {
        var t = time.split(/[- :]/);
        var d = new Date(t[0], t[1]-1, t[2], t[3], t[4], t[5]);
        return $.datepicker.formatDate('mm/dd ', d) + ('0' + d.getHours()).slice(-2) + ':' + ('0' + d.getMinutes()).slice(-2);
    } else {
        return "-";
    }
}
function timeSet2(time) {
    if (time) {
        var t = time.split(/[- :]/);
        var d = new Date(t[0], t[1]-1, t[2], t[3], t[4], t[5]);

        return ('0' + d.getHours()).slice(-2) + ':' + ('0' + d.getMinutes()).slice(-2);
    } else {
        return "-";
    }
}
function minusTimeSet(time1, time2) {
    if (time2) {
        var d1 = new Date(time1);
        var d2 = new Date(time2);
        var minusTime = new Date(d2.getTime() - d1.getTime());
        return ('0' + minusTime.getUTCHours()).slice(-2) + ':' + ('0' + minusTime.getUTCMinutes()).slice(-2)
    } else {
        return "-";
    }
}
function convertDateTime(time){
    time = time.replace(' ', 'T') + 'Z';

    var changeDate = new Date(time);

    var yyyy = changeDate.getUTCFullYear().toString();
    var mm = (changeDate.getUTCMonth()).toString();
    var dd = changeDate.getUTCDate().toString();

    var hh = changeDate.getUTCHours().toString();
    var nn = changeDate.getUTCMinutes().toString();
    var ss = changeDate.getUTCSeconds().toString();

    return new Date(yyyy, mm, dd, hh, nn, ss);
}

$(document).ready(function () {

}).ajaxStart(function () {
   // loading.show();
}).ajaxStop(function () {
  //  loading.hide();
});
/*]]>*/