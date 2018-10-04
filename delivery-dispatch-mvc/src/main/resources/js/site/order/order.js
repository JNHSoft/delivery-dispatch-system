/*<![CDATA[*/
let loading = $('<div id="loading"><div><p style="background-color: #838d96"/></div></div>').appendTo(document.body).hide();
$(document).ready(function () {
    var storeId = $('#orderMyStoreChk').val();
    var statusArray = ["0", "1", "2", "3", "4", "5"];
    $('#statusArray').val(statusArray);
    getOrderList(statusArray, storeId);
    var supportsWebSockets = 'WebSocket' in window || 'MozWebSocket' in window;
    $.ajax({
        url: "/websocketHost",
        success: function (websocketHost) {
            if (supportsWebSockets) {
                var socket = io(websocketHost, {
                    path: '/socket.io', // 서버 사이드의 path 설정과 동일해야 한다
                    transports: ['websocket'], // websocket만을 사용하도록 설정
                    secure: true
                });
                socket.on('message', function (data) {
                    var objData = JSON.parse(data);
                    var subgroup_id = objData.subGroupId;
                    if (!my_store.subGroup && my_store.id == objData.storeId) {
                        orderAlarmMessage(data, statusArray, storeId);
                    } else if (my_store.subGroup) {
                        if (subgroup_id == my_store.subGroup.id) {
                            orderAlarmMessage(data, statusArray, storeId);
                        }
                    }
                });
                $(function () {
                    /*$('#orderUpdate').click(function(){
                        socket.emit('message', "push_data:{type:order_updated, storeId:"+storeId+"}");
                    });*/
                })
            } else {
                alert('websocket을 지원하지 않는 브라우저입니다.');
            }
        }
    });

    if (map_region) {
        if (map_region == "tw") {
            $('#combinedChk').attr("disabled", true);
        }
    }

    $("#orderAllChk").click(function () {
        if (this.checked) {
            $("input[name=srchChk]:checkbox").each(function () {
                $(this).prop("checked", "checked");
                $(this).attr("disabled", true);
            });
            for (var a in statusArray) {
                statusArray[a] = a;
            }
        } else {
            $("input[name=srchChk]:checkbox").each(function () {
                $(this).prop("checked", false);
                $(this).attr("disabled", false);
            });
            for (var a in statusArray) {
                statusArray[a] = null;
            }
        }
        $('#statusArray').val(statusArray);
        getOrderList(statusArray, storeId);
    });

    $("input[name=srchChk]:checkbox").click(function () {
        if (this.checked) {
            statusArray[this.value] = this.value;
            if (this.value == "0") {
                statusArray[5] = "5";
            }
        } else {
            statusArray[this.value] = null;
            if (this.value == "0") {
                statusArray[5] = null;
            }
        }
        $('#statusArray').val(statusArray);
        getOrderList(statusArray, storeId);
    });

    $("input[name=myStoreChk]:checkbox").click(function () {
        if (this.checked) {
            storeId = this.value;
            $('#statusArray').val(statusArray);
            getOrderList(statusArray, storeId);
        } else {
            storeId = "";
            $('#statusArray').val(statusArray);
            getOrderList(statusArray, storeId);
        }
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
}).ajaxStart(function () {
    loading.show();
}).ajaxStop(function () {
    loading.hide();
});

function orderAlarmMessage(data, statusArray, storeId) {
    if (data.match('order_') == 'order_') {
        getOrderList(statusArray, storeId);
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


function timeSet(time) {
    if (time) {
        var d = new Date(time);
        return $.datepicker.formatDate('mm/dd ', d) + ('0' + d.getHours()).slice(-2) + ':' + ('0' + d.getMinutes()).slice(-2);
    } else {
        return "-";
    }
}

function timeSet2(time) {
    if (time) {
        var d = new Date(time);
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

var selectedOriginOrder;
var map;
var marker;
var changeChkInputMessage = false;

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
    var regOrderId = "";
    $.ajax({
        url: "/getOrderDetail",
        type: 'get',
        data: {
            id: orderId
        },
        dataType: 'json',
        success: function (data) {
            selectedOriginOrder = data;
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
            if (data.regOrderId) {
                regOrderId = data.regOrderId;
            } else {
                regOrderId = "-";
            }
            // $('.tit').html('<h2>'+order_detail + ' - '+ data.id + '('+ regOrderId +')</h2>'+$status);
            $('.tit').html('<h2>' + order_detail + ' - ' + regOrderIdReduce(regOrderId) + '</h2>' + $status);
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
                for (var key in currentOrderList) {
                    if (currentOrderList.hasOwnProperty(key)) {
                        if (currentOrderList[key].regOrderId == data.combinedOrderId) {
                            var shtml = '<option value="' + currentOrderList[key].regOrderId + '">' + order_id + ':' + currentOrderList[key].regOrderId + '|' + order_created + ':' + timeSet(currentOrderList[key].createdDatetime) + '</option>';
                            $('#selectCombined').html(shtml);
                        }
                    }
                }
                $('input[name=combinedChk]:checkbox').prop("checked", true);
                $('#selectCombined').val(data.combinedOrderId).prop("selected", true);
                $('#selectCombined').attr("disabled", false);
            } else {
                getNewOrderList(statusNewArray);
                $('input[name=combinedChk]:checkbox').removeAttr("checked");
                $('#selectCombined').attr("disabled", true);
            }
            getMyRiderList(data);



        }/*,
        error : function (request,status,error) {
            // alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
            alert("error: "+order_detail_error);
            location.href = "/order";
        }*/
    });
}

function getMyRiderList(orderDetailData) {
    var shtml = '<option value="0">-</option>';
    var shtml2 = '';
    $.ajax({
        url: "/getMyRiderList",
        type: 'get',
        data: {},
        success: function (data) {
            for (var key in data) {
                if (data.hasOwnProperty(key)) {
                    if (data[key].working == "1") {
                        if ($("input[name=myStoreChk]:checkbox").prop("checked")) {
                            if (data[key].subGroupRiderRel) {
                                if (data[key].subGroupRiderRel.storeId == $('#orderMyStoreChk').val()) {
                                    shtml += '<option value="' + data[key].id + '">' + data[key].name + '</option>';
                                    var tmpId = data[key].id;
                                    shtml2 += '<span id="rider' + tmpId + '" class="riderPhone" style="display:none">' + data[key].phone + '</span>';
                                }
                            }
                        } else {
                            shtml += '<option value="' + data[key].id + '">' + data[key].name + '</option>';
                            var tmpId = data[key].id;
                            shtml2 += '<span id="rider' + tmpId + '" class="riderPhone" style="display:none">' + data[key].phone + '</span>';
                        }
                        $('#selectedRider').html(shtml);
                        $('#riderPhone').html(shtml2);
                    }
                }
            }
            var riderPhone = '#rider' + orderDetailData.riderId;
            if (orderDetailData.riderId != null) {
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

            getThirdPartyList(orderDetailData);
        }

    });
}

// 서드 파티 추가
function getThirdPartyList(orderDetailData) {
    var html = '';
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
                        for (var thirdParty in thirdPartyList) {
                            if (thirdPartyList[thirdParty] == data[key].id) {
                                html += '<option value="' + data[key].id + '">' + data[key].name + '</option>';
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
}


// 서드파티 업데이트
function thirdPartyUpdate() {
    if(selectedOriginOrder.status == 1 || selectedOriginOrder.status == 2 || selectedOriginOrder.status == 4){
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

    var combinedOrderId = "";
    if ($('#combinedChk').prop("checked")) {
        combinedOrderId = $('#selectCombined').val();
    }

    var regOrderId = selectedOriginOrder.regOrderId;
    var thirdPartyId = $('#selectedThirdParty').val();
    var orderStatus = selectedOriginOrder.status;

    $.ajax({
        url: "/putOrderThirdParty",
        type: 'put',
        data:JSON.stringify({
            id: regOrderId,
            status: orderStatus,
            combinedOrderId : combinedOrderId,
            thirdParty: {id:thirdPartyId}
        }),
        contentType:'application/json',
        dataType: 'json',
        success: function (data) {
            getOrderDetail(selectedOriginOrder.regOrderId);
            var statusArray = $('#statusArray').val().split(",");
            var storeId = $('#orderMyStoreChk').val();
            getOrderList(statusArray, storeId);

        }
    });
}

function getNewOrderList(statusNewArray) {
    // var shtml = "<option value=0>-</option>";
    var shtml = "";
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
                    if (data[key].regOrderId != selectedOriginOrder.regOrderId && !data[key].combinedOrderId) {
                        shtml += '<option value="' + data[key].regOrderId + '">' + order_id + ':' + data[key].regOrderId + '|' + order_created + ':' + timeSet(data[key].createdDatetime) + '</option>';
                    }
                }
            }
            $('#selectCombined').html(shtml);
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
            statusArray: statusArray.filter(n => n), //null 제거
            status: statusArray.join('')
        },
        dataType: 'json',
        success: function (data) {
            var i = 1;
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
                    else if (data[key].paid == 3) {
                        $toBePaid = order_payment_service;
                    } else {
                        $toBePaid = "-";
                    }

                    if ($('#assignmentStatus').val() == "0") {
                        if (data[key].assignedFirst == null && (data[key].status == 0 || data[key].status == 5)) {
                            $button = '<button class="button h20" onclick="javascript:putAssignedAdvanceFirst(' + '\'' + data[key].regOrderId + '\'' + ');">' + order_assigned_advance + '</button>';
                        } else {
                            $button = "";
                        }
                    } else {
                        $button = "";
                    }

                    tmpdata.No = i;
                    i++;
                    tmpdata.state = $status;
                    tmpdata.id = data[key].id;
                    tmpdata.time1 = timeSet2(data[key].createdDatetime);
                    tmpdata.address = data[key].address;
                    if (data[key].regOrderId) {
                        tmpdata.reg_order_id = regOrderIdReduce(data[key].regOrderId);
                        tmpdata.origin_reg_order_id = data[key].regOrderId;
                    } else {
                        tmpdata.reg_order_id = '-';
                        tmpdata.origin_reg_order_id = '-';
                    }

                    tmpdata.time2 = data[key].cookingTime;
                    tmpdata.pay = $toBePaid;

                    if (!data[key].message) {
                        tmpdata.message = "-";
                    } else {
                        tmpdata.message = data[key].message;
                    }

                    if (!data[key].phone) {
                        tmpdata.phone = "-";
                    } else {
                        tmpdata.phone = data[key].phone;
                    }

                    if (!data[key].assignedDatetime) {
                        tmpdata.time3 = "-";
                    } else {
                        tmpdata.time3 = timeSet2(data[key].assignedDatetime);
                    }


                    if (!data[key].pickedUpDatetime) {
                        tmpdata.time4 = "-";
                    } else {
                        tmpdata.time4 = timeSet2(data[key].pickedUpDatetime);
                    }

                    // 픽업 시간
                    // 픽업 , 도착 , 복귀
                    tmpdata.time5 = checkTime(data[key].pickedUpDatetime, data[key].completedDatetime);
                    //
                    // if(!data[key].completedDatetime){
                    //     tmpdata.time5 = "-";
                    // }else{
                    //     tmpdata.time5 = timeSet2(data[key].completedDatetime);
                    // }
                    tmpdata.time6 = "-";
                    if (data[key].pickedUpDatetime && data[key].completedDatetime && data[key].returnDatetime) {
                        if (diffTime(data[key].pickedUpDatetime, data[key].returnDatetime) == 1) {
                            tmpdata.time6 = '<span style="color: red">' + timeSet2(data[key].returnDatetime) + '</span>';
                        } else if (diffTime(data[key].completedDatetime, data[key].returnDatetime) == 1) {
                            tmpdata.time6 = '<span style="color: red">' + timeSet2(data[key].returnDatetime) + '</span>';
                        } else {
                            tmpdata.time6 = timeSet2(data[key].returnDatetime);
                        }

                    } else if (data[key].returnDatetime) {
                        tmpdata.time6 = '<span style="color: red">' + timeSet2(data[key].returnDatetime) + '</span>';
                    }

                    // if(!data[key].returnDatetime){
                    //     tmpdata.time6 = "-";
                    // }else{
                    //     tmpdata.time6 = timeSet2(data[key].returnDatetime);
                    // }

                    if (!data[key].reservationDatetime) {
                        tmpdata.time7 = "-";
                    } else if (data[key].reservationStatus == 1) {
                        tmpdata.time7 = timeSet2(data[key].reservationDatetime);
                    } else if (data[key].reservationStatus == 0) {
                        if (map_region) {
                            if (map_region == "tw") {
                                tmpdata.time7 = '<span style="color: red">' + timeSet2(data[key].reservationDatetime) + '</span>';
                            } else {
                                tmpdata.time7 = "-";
                            }
                        } else {
                            tmpdata.time7 = "-";
                        }
                    }
                    // 서드 파티 추가
                    if (data[key].thirdParty) {
                        tmpdata.rider = data[key].thirdParty.name;
                    } else if (!data[key].rider) {
                        tmpdata.rider = "-";
                    } else {
                        tmpdata.rider = data[key].rider.name;
                    }
                    tmpdata.button = $button;

                    if ($("input[name=myStoreChk]:checkbox").prop("checked")) {
                        if (data[key].storeId == storeId) {
                            mydata.push(tmpdata);
                        }
                    } else {
                        mydata.push(tmpdata);
                    }
                }
            }
            if (mydata) {
                jQuery('#jqGrid').jqGrid('clearGridData')
                jQuery('#jqGrid').jqGrid('setGridParam', {data: mydata, page: 1})
                jQuery('#jqGrid').trigger('reloadGrid');
            }

            $("#jqGrid").jqGrid({
                datatype: "local",
                data: mydata,
                colModel: [
                    {label:order_number, name:'No', width:25, key:true, align:'center'},
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
                    {label: order_arrived, name: 'time5', width: 80, align: 'center'},
                    {label: order_return, name: 'time6', width: 80, align: 'center'},
                    {label: order_reserved, name: 'time7', width: 80, align: 'center'},
                    {label: rider_name, name: 'rider', width: 80, align: 'center'},
                    {label: order_assigned_advance, name: 'button', width: 80, align: 'center'}
                ],
                height: 680,
                autowidth: true,
                rowNum: 20,
                pager: "#jqGridPager",
                ondblClickRow: function (rowid, icol, cellcontent, e) {
                    var rowData = jQuery(this).getRowData(rowid);
                    var orderId = rowData['origin_reg_order_id'];
                    changeChkInputMessage = false;
                    getOrderDetail(orderId);//상세보기 열기
                    setTimeout(function () {
                        $(window).trigger('resize');
                    }, 300)//그리드 리사이즈
                }
            });

            resizeJqGrid('#jqGrid'); //그리드 리사이즈

            $('.state_wrap .btn_close').click(function (e) {
                e.preventDefault();
                $('.state_wrap').removeClass('on'); //상세보기 닫기
                setTimeout(function () {
                    $(window).trigger('resize');
                }, 300)//그리드 리사이즈
            });
        }
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


// 시간 비교 2분차이
function diffTime(time1, time2) {
    // 픽업시간 - 완료시간
    if (time1 && time2) {
        var time1 = new Date(time1);
        var time2 = new Date(time2);

        if (time2.getTime() - time1.getTime() < 120000) {
            return 1;
        }
    }
    return 0;
}


function putAssignedAdvanceFirst(id) {
    $.ajax({
        url: '/putAssignedAdvanceFirst',
        type: 'put',
        data: {
            id: id
        },
        dataType: 'json',
        success: function (data) {
            var statusArray = $('#statusArray').val().split(",");
            var storeId = $('#orderMyStoreChk').val();
            getOrderList(statusArray, storeId);
        }
    });
}

function putOrder() {
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
    var combinedOrderId = '';
    if ($('#combinedChk').prop("checked")) {
        combinedOrderId = $('#selectCombined').val();
    }
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
        },
        dataType: 'json',
        success: function (data) {
            getOrderDetail(selectedOriginOrder.regOrderId);
            var statusArray = $('#statusArray').val().split(",");
            var storeId = $('#orderMyStoreChk').val();
            getOrderList(statusArray, storeId);
            changeChkInputMessage = false;
        }
    });
}

function putOrderAssignCancle() {
    var id = $('.tit').attr("orderId");
    var combinedOrderId = '';
    if ($('#combinedChk').prop("checked")) {
        combinedOrderId = $('#selectCombined').val();
    }

    $.ajax({
        url: '/putOrderAssignCancle',
        type: 'put',
        data: {
            id: id,
            combinedOrderId: combinedOrderId,
        },
        dataType: 'json',
        success: function (data) {
            getOrderDetail(selectedOriginOrder.regOrderId);
            var statusArray = $('#statusArray').val().split(",");
            var storeId = $('#orderMyStoreChk').val();
            getOrderList(statusArray, storeId);
        }
    });
}

function putAssignedAdvance() {
    var id = $('.tit').attr("orderId");
    var riderId = $('#selectedRider').val();
    var combinedOrderId = "";
    if ($('#combinedChk').prop("checked")) {
        combinedOrderId = $('#selectCombined').val();
    }
    // var firstTime = new Date().getTime();
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
                getOrderDetail(selectedOriginOrder.regOrderId);
                var statusArray = $('#statusArray').val().split(",");
                var storeId = $('#orderMyStoreChk').val();
                getOrderList(statusArray, storeId);
            }
            // console.log(new Date().getTime() - firstTime);
        }
    });
}

function orderConfirm() {
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
                putOrderAssignCancle();
                putOrder();
            } else {
                putOrder();
            }
        } else {
            putOrder();
        }
    } else {
        if (selectedOriginOrder.riderId) {
            if ($('#selectedRider').val() == selectedOriginOrder.riderId) {
                putOrder();//주문만수정
            } else {
                putOrderAssignCancle();
                putAssignedAdvance();
                putOrder();//기사배정변경
            }
        } else {
            putAssignedAdvance();
            putOrder();
        }
    }
}

function putOrderCancel() {
    if (selectedOriginOrder.status == "3") {
        alert(order_confirm_completed);
        return;
    }

    if (selectedOriginOrder.status == "4") {
        alert(order_confirm_canceled);
        return;
    }
    var id = $('.tit').attr("orderId");
    var combinedOrderId = "";
    if ($('#combinedChk').prop("checked")) {
        combinedOrderId = $('#selectCombined').val();
    }
    $.ajax({
        url: '/putOrderCancel',
        type: 'put',
        data: {
            id: id,
            combinedOrderId: combinedOrderId
        },
        dataType: 'json',
        success: function (data) {
            var statusArray = $('#statusArray').val().split(",");
            var storeId = $('#orderMyStoreChk').val();
            getOrderList(statusArray, storeId);
            $('.state_wrap').removeClass('on');
            setTimeout(function () {
                $(window).trigger('resize');
            }, 300)
        }
    });
}

/*]]>*/