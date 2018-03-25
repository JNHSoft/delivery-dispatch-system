$(document).ready(function() {
    var supportsWebSockets = 'WebSocket' in window || 'MozWebSocket' in window;
    if (supportsWebSockets) {
        var socket = io('13.125.18.185:3000', {
            path: '/socket.io', // 서버 사이드의 path 설정과 동일해야 한다
            transports: ['websocket'] // websocket만을 사용하도록 설정
        });
        socket.on('message', function(data){
//                alert(data);//data 받는부분
        });
        $(function() {
            $('#test').click(function(){
                alert("!!!");
                socket.emit('message', "websocketTest");//data보내는부분
            });
        })
    } else {
        alert('websocket을 지원하지 않는 브라우저입니다.');
    }

    var storeId = $('#orderMyStoreChk').val();
    console.log("!!!!!!"+storeId);
    var statusArray = ["0","1","2","3","4","5"];
    $('#statusArray').val(statusArray);
    console.log($('#statusArray').val());
    getOrderList(statusArray, storeId);

    $("#orderAllChk").click(function () {
        if(this.checked){
            $("input[name=srchChk]:checkbox").each(function() {
                $(this).attr("checked", true);
                $(this).attr("disabled", true);
            });
            for(a in statusArray){
                statusArray[a] = a;
            }
            $('#statusArray').val(statusArray);
            console.log(statusArray);
            getOrderList(statusArray, storeId);
        }else{
            $("input[name=srchChk]:checkbox").each(function() {
                $(this).attr("checked", false);
                $(this).attr("disabled", false);
            });
            for(a in statusArray){
                statusArray[a] = null;
            }
            console.log(statusArray);
        }
    });

    $("input[name=srchChk]:checkbox").click(function () {
        if(this.checked){
            statusArray[this.value] = this.value;
            if(this.value == "0"){
                statusArray[5] = "5";
            }
        }else{
            statusArray[this.value] = null;
            if(this.value == "0"){
                statusArray[5] = null;
            }
        }
        console.log(statusArray);
        $('#statusArray').val(statusArray);
        getOrderList(statusArray, storeId);
    });

    $("input[name=myStoreChk]:checkbox").click(function () {
        if(this.checked){
            storeId = this.value;
            $('#statusArray').val(statusArray);
            getOrderList(statusArray, storeId);
        }else{
            storeId = "";
            $('#statusArray').val(statusArray);
            getOrderList(statusArray, storeId);
        }
    });

    $('input[name=combinedChk]:checkbox').click(function () {
        if(this.checked){
            $('#selectCombined').attr("disabled", false);
        }else{
            $('#selectCombined').attr("disabled", true);
        }
    });

    $('#selectedRider').on('change', function () {
        $('.riderPhone').css('display', 'none');
        $('#rider'+$(this).val()).css('display', 'block');

    });

    $("#searchButton").click(function () {
        var searchText = $("#searchText").val();
        console.log(searchText);
        var filter = {
            groupOp: "OR",
            rules: []
        };
        var select = $("#searchSelect option:selected").val();
        console.log(select);

        if(select == 'id'){
            filter.rules.push({
                field : select,
                op : "eq",
                data : searchText
            });
        }else if(select == 'all'){
            filter.rules.push({
                field : 'id',
                op : "eq",
                data : searchText
            });
            filter.rules.push({
                field : 'address',
                op : "cn",
                data : searchText
            });
            filter.rules.push({
                field : 'text',
                op : "cn",
                data : searchText
            });
            filter.rules.push({
                field : 'rider',
                op : "cn",
                data : searchText
            });
        }else{
            filter.rules.push({
                field : select,
                op : "cn",
                data : searchText
            });
        }
        var grid = jQuery('#jqGrid');
        grid[0].p.search = filter.rules.length > 0;
        $.extend(grid[0].p.postData, { filters: JSON.stringify(filter) });
        grid.trigger("reloadGrid", [{ page: 1 }]);
    });
});

function timeSet(time) {
    if(time != null){
        var d = new Date(time);
        return $.datepicker.formatDate('mm/dd ', d) + ('0' + d.getHours()).slice(-2) + ':' + ('0' + d.getMinutes()).slice(-2);
    }else{
        return "-";
    }
}

function timeSet2(time) {
    if(time != null){
        var d = new Date(time);
        return ('0' + d.getHours()).slice(-2) + ':' + ('0' + d.getMinutes()).slice(-2);
    }else{
        return "-";
    }
}

function minusTimeSet(time1 , time2) {
    if(time2 != null){
        var d1 = new Date(time1);
        var d2 = new Date(time2);
        var minusTime = new Date(d2.getTime()-d1.getTime());
        return ('0' + minusTime.getHours()).slice(-2) + ':' + ('0' + minusTime.getMinutes()).slice(-2)
    }else{
        return "-";
    }
}

var selectedOriginOrder;
var map;
var marker;
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

function getOrderDetail(orderId) {
    console.log("orderId: " + orderId);
    $.ajax({
        url : "/getOrderDetail",
        type : 'get',
        data : {
            id : orderId
        },
        async : false, //비동기 -> 동기
        dataType : 'json',
        success : function (data) {
            selectedOriginOrder = data;
            console.log(selectedOriginOrder);
            if (data.status == 0 || data.status == 5) {
                $status = '<i class="ic_txt ic_green">' + status_new + '</i>';
            }
            else if (data.status == 1) {
                $status = '<i class="ic_txt ic_blue">' + status_assigned + '</i>';
            }
            else if (data.status == 2) {
                $status = '<i class="ic_txt ic_blue">' + status_pickedup + '</i>';
            }
            else if (data.status == 3) {
                $status = '<i class="ic_txt">' + status_completed + '</i>';
            }
            else {
                $status = '<i class="ic_txt ic_red">' + status_canceled + '</i>';
            }
            $('.tit').html('<h2>'+order_detail + ' - '+ data.id + '</h2>'+$status);
            $('.tit').attr("orderId", data.id);

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

            var statusNewArray = ["0","5"];

            if(data.combinedOrderId != null){
                for (var key in currentOrderList) {
                    if (currentOrderList.hasOwnProperty(key)) {
                        if(currentOrderList[key].id == data.combinedOrderId){
                            var shtml = '<option value="'+ currentOrderList[key].id+'">'+'주문 ID'+':'+ currentOrderList[key].id + '|'+ '등록시간'+':'+ timeSet(currentOrderList[key].createdDatetime) + '</option>';
                            $('#selectCombined').html(shtml);
                        }

                    }
                }
                $('input[name=combinedChk]:checkbox').attr("checked", true);
                $('#selectCombined').val(data.combinedOrderId).prop("selected", true);
                $('#selectCombined').attr("disabled", false);
            }else{
                getNewOrderList(statusNewArray);
                $('input[name=combinedChk]:checkbox').attr("checked", false);
                $('#selectCombined').attr("disabled", true);
            }

            getMyRiderList();
            var riderPhone = '#rider'+data.riderId;
            if(data.riderId != null){
                $('#selectedRider').val(data.riderId).prop("selected", true);
                $(riderPhone).css('display', 'block');
            }else {
                $('#selectedRider').val("0").prop("selected", true);
                $(riderPhone).css('display', 'none');
            }
            $('#memo').html(data.message);
            $('#userPhone').html(data.phone);
            $('#userAddress').html(data.address);
            $('#distance').html(data.distance);
            map.setCenter({lat: parseFloat(data.latitude), lng: parseFloat(data.longitude)});
            marker.setPosition({lat: parseFloat(data.latitude), lng: parseFloat(data.longitude)});
        }
    });
}
function getMyRiderList() {
    var shtml = '<option value="0">정보없음</option>';
    var shtml2 = '';
    $.ajax({
        url:"/getMyRiderList",
        type: 'get',
        data: {
        },
        async : false,
        success: function (data) {
            console.log(data);
            for (var key in data) {
                if (data.hasOwnProperty(key)){
                    if(data[key].working=="1"){
                        shtml += '<option value="'+data[key].id+'">'+data[key].name+'</option>';
                        var tmpId = data[key].id;
                        shtml2 += '<span id="rider'+tmpId+'" class="riderPhone" style="display:none">'+data[key].phone+'</span>';
                        $('#selectedRider').html(shtml);
                        $('#riderPhone').html(shtml2);
                    }
                }
            }
        }
    });
}

function getNewOrderList(statusNewArray) {
    var shtml = "";
    jQuery.ajaxSettings.traditional = true;// ajax 배열 던지려면 필요함
    $.ajax({
        url:"/getOrderList",
        type: 'get',
        data: {
            statusArray : statusNewArray, //null 제거
            status : statusNewArray.join('')
        },
        datatype: 'json',
        success: function (data) {
            $('#selectCombined').html("");
            for (var key in data) {
                if (data.hasOwnProperty(key)){
                    if(data[key].id != selectedOriginOrder.id){
                        shtml += '<option value="'+data[key].id+'">'+'주문 ID'+':'+ data[key].id + '|'+ '등록시간'+':'+ timeSet(data[key].createdDatetime) + '</option>';
                        $('#selectCombined').html(shtml);
                    }
                }
            }
        }
    });
}

var currentOrderList;

function getOrderList(statusArray, storeId) {

    var mydata = [];
    jQuery.ajaxSettings.traditional = true;// ajax 배열 던지려면 필요함
    $.ajax({
        url: "/getOrderList",
        type: 'get',
        data: {
            statusArray : statusArray.filter(n => n), //null 제거
        status : statusArray.join('')
        },
        dataType: 'json',
        success: function (data) {
        var i = 1;
        console.log(data);
        currentOrderList = data;
        for (var key in data) {
            if (data.hasOwnProperty(key)) {
                var tmpdata = new Object();
                if (data[key].status == 0 || data[key].status == 5) {
                    $status = '<i class="ic_txt ic_green">' + status_new + '</i>';
                }
                else if (data[key].status == 1) {
                    $status = '<i class="ic_txt ic_blue">' + status_assigned + '</i>';
                }
                else if (data[key].status == 2) {
                    $status = '<i class="ic_txt ic_blue">' + status_pickedup + '</i>';
                }
                else if (data[key].status == 3) {
                    $status = '<i class="ic_txt">' + status_completed + '</i>';
                }
                else {
                    $status = '<i class="ic_txt ic_red">' + status_canceled + '</i>';
                }

                if (data[key].paid == 0) {
                    $toBePaid = order_payment_cash;
                }
                else if (data[key].paid == 1) {
                    $toBePaid = order_payment_card;
                }
                else if (data[key].paid == 2) {
                    $toBePaid = order_payment_prepayment;
                }
                else if (data[key].paid == 3){
                    $toBePaid = order_payment_service;
                }else {
                    $toBePaid = "-";
                }

                if($('#assignmentStatus').val() == "0"){
                    if(data[key].assignedFirst == null && (data[key].status ==0||data[key].status ==5)){
                        $button = '<button class="button h20" onclick="javascript:putAssignedAdvanceFirst('+ data[key].id +');">'+order_assigned_advance+'</button>';
                    }else{
                        $button = "";
                    }
                }else{
                    $button = "";
                }

                tmpdata.No = i;
                i++;
                tmpdata.state = $status;
                tmpdata.id = data[key].id;
                tmpdata.time1 = data[key].createdDatetime;
                tmpdata.address = data[key].address;
                tmpdata.text = data[key].menuName;
                tmpdata.time2 = data[key].cookingTime;
                tmpdata.pay = $toBePaid;

                if(data[key].assignedDatetime == null){
                    tmpdata.time3 = "-";
                }else{
                    tmpdata.time3 = timeSet2(data[key].assignedDatetime);
                }

                if(data[key].pickedUpDatetime == null){
                    tmpdata.time4 = "-";
                }else{
                    tmpdata.time4 = timeSet2(data[key].pickedUpDatetime);
                }

                if(data[key].reservationDatetime == null){
                    tmpdata.time5 = "-";
                }else{
                    tmpdata.time5 = timeSet2(data[key].reservationDatetime);
                }

                if(data[key].rider == null){
                    tmpdata.rider = "-";
                }else{
                    tmpdata.rider = data[key].rider.name;
                }
                tmpdata.button = $button;

                if($("input[name=myStoreChk]:checkbox").prop("checked")){
                    if(data[key].storeId == storeId){
                        mydata.push(tmpdata);
                    }
                }else{
                    mydata.push(tmpdata);
                }
            }
        }
        console.log("mydata");
        console.log(mydata);
        if(mydata != null) {
            jQuery('#jqGrid').jqGrid('clearGridData')
            jQuery('#jqGrid').jqGrid('setGridParam', {data: mydata, page: 1})
            jQuery('#jqGrid').trigger('reloadGrid');
        }

        $("#jqGrid").jqGrid({
            datatype:"local",
            data:mydata,
            width:'auto',
            autowidth:true,
            colModel:[
                {label:'No', name:'No', width:25, key:true, align:'center'},
                {label:order_status, name:'state', width:80, align:'center'},
                {label:order_id, name:'id', width:80, align:'center'},
                {label:order_created, name:'time1', width:80, align:'center'},
                {label:order_address, name:'address', width:200},
                {label:order_summary, name:'text', width:150},
                {label:order_cooking, name:'time2', width:80, align:'center'},
                {label:order_payment, name:'pay', width:80, align:'center'},
                {label:order_assigned, name:'time3', width:80, align:'center'},
                {label:order_pickedup, name:'time4', width:80, align:'center'},
                {label:order_reserved, name:'time5', width:80, align:'center'},
                {label:rider_name, name:'rider', width:80, align:'center'},
                {label:order_assigned_advance, name:'button', width:80, align:'center'}
            ],
            height:700,
            rowNum:20,
            pager:"#jqGridPager",
            ondblClickRow: function(rowid,icol,cellcontent,e){
                var rowData = jQuery(this).getRowData(rowid);
                var orderId = rowData['id'];
                getOrderDetail(orderId);
                $('.state_wrap').addClass('on'); //상세보기 열기
                setTimeout(function(){
                    $(window).trigger('resize');
                },300)//그리드 리사이즈
            }
        });

        resizeJqGrid('#jqGrid'); //그리드 리사이즈

        $('.state_wrap .btn_close').click(function(e){
            e.preventDefault();
            $('.state_wrap').removeClass('on'); //상세보기 닫기
            setTimeout(function(){
                $(window).trigger('resize');
            },300)//그리드 리사이즈
        });
    }
});
}

function putAssignedAdvanceFirst(id) {
    $.ajax({
        url: '/putAssignedAdvanceFirst',
        type: 'put',
        data : {
            id : id
        },
        dataType : 'json',
        success : function (data) {
            console.log(data);
            var statusArray = $('#statusArray').val().split(",");
            var storeId = $('#orderMyStoreChk').val();
            getOrderList(statusArray, storeId);
        }
    });
}

function putOrder() {
    var id = $('.tit').attr("orderId");

    var menuName = $('#menuName').val();
    var cookingTime = $('#cookingTime').val();
    var menuPrice = $('#menuPrice').val();
    var deliveryPrice = $('#deliveryPrice').val();
    var paid = $('#selectPaid').val();
    var combinedOrderId = '';
    if($('#combinedChk').prop("checked")){
        combinedOrderId = $('#selectCombined').val();
    }
    $.ajax({
        url: '/putOrder',
        type: 'put',
        data: {
            id : id,
            menuName : menuName,
            cookingTime : cookingTime,
            menuPrice : menuPrice,
            deliveryPrice : deliveryPrice,
            paid : paid,
            combinedOrderId : combinedOrderId,
        },
        dataType : 'json',
        async : false,
        success : function (data) {
            getOrderDetail(selectedOriginOrder.id);
            var statusArray = $('#statusArray').val().split(",");
            var storeId = $('#orderMyStoreChk').val();
            getOrderList(statusArray, storeId);
            console.log("test");
            console.log(data);
        }
    });
}

function putOrderAssignCancle() {
    var id = $('.tit').attr("orderId");
    var combinedOrderId = '';
    if($('#combinedChk').prop("checked")){
        combinedOrderId = $('#selectCombined').val();
    }

    $.ajax({
        url: '/putOrderAssignCancle',
        type: 'put',
        data: {
            id : id,
            combinedOrderId : combinedOrderId,
        },
        async : false,
        dataType : 'json',
        success : function (data) {
            console.log(data)
        }
    });
}

function putAssignedAdvance() {
    var id = $('.tit').attr("orderId");
    var riderId = $('#selectedRider').val();
    var combinedOrderId ="";
    if($('#combinedChk').prop("checked")){
        combinedOrderId = $('#selectCombined').val();
    }
    $.ajax({
        url: '/putAssignedAdvance',
        type: 'put',
        data: {
            id : id,
            riderId : riderId,
            combinedOrderId : combinedOrderId
        },
        async : false,
        dataType : 'json',
        success : function (data) {
            console.log(data)
        }
    });
}

function orderConfirm() {
    console.log("status: " + selectedOriginOrder.status);
    if(selectedOriginOrder.status == "3" ){
        alert("완료된 주문은 수정이 불가능합니다.");
        return;
    }

    if(selectedOriginOrder.status == "4"){
        alert("취소된 주문은 수정이 불가능합니다.");
        return;
    }

    if($('#selectedRider').val()=='0'){
        if(selectedOriginOrder.riderId != null) {
            var result = confirm("배정을 하지않거나 취소 하시겠습니까..?");
            if (result) {
                putOrderAssignCancle();
                putOrder();
            } else {
                putOrder();
            }
        }else {
            putOrder();
        }
    }else{
        if(selectedOriginOrder.riderId != null) {
            if ($('#selectedRider').val() == selectedOriginOrder.riderId) {
                putOrder();//주문만수정
            } else {
                putOrderAssignCancle();
                putAssignedAdvance();
                putOrder();//기사배정변경
            }
        }else {
            putAssignedAdvance();
            putOrder();
        }
    }
}
function putOrderCancle() {
    if(selectedOriginOrder.status == "3" ){
        alert("완료된 주문은 수정이 불가능합니다.");
        return;
    }

    if(selectedOriginOrder.status == "4"){
        alert("취소된 주문은 수정이 불가능합니다.");
        return;
    }
    var id = $('.tit').attr("orderId");
    $.ajax({
        url: '/putOrderCancle',
        type: 'put',
        data: {
            id : id,
        },
        dataType : 'json',
        success : function (data) {
            var statusArray = $('#statusArray').val().split(",");
            var storeId = $('#orderMyStoreChk').val();
            getOrderList(statusArray, storeId);
            $('.state_wrap').removeClass('on');
        }
    });
}
