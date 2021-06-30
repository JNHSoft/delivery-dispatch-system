let loading= $('<div id="loading"><div><p style="background-color: #838d96"/></div></div>').appendTo(document.body).hide();

var selectId =$("#statisticsStoreList");
var selectIdOption = $("#statisticsStoreList option:selected");

$(function () {
    /// 데이터 정보 조회
    let date = $.datepicker.formatDate('yy-mm-dd', new Date);
    $('#startDate, #endDate').val(date);
    getStoreStatistics();           // 정보 조회
    getGroupList();                 // 그룹 정보 조회

    /// 이벤트 정보 등록 시작

    $('#startDate').datepicker({
        maxDate : date,
        onClose: function(selectedDate) {
            $('#endDate').datepicker('option', 'minDate', selectedDate);
            getStoreStatistics();
        }
    });     // 시작일 이벤트

    $('#endDate').datepicker({
        minDate : date,
        onClose: function( selectedDate ) {
            $('#startDate').datepicker('option', 'maxDate', selectedDate);
            getStoreStatistics();
        }
    });     // 종료일 이벤트

    $(".select").change(function(){
        selectId = $(this);
        selectIdOption = $('option:selected', this);
        searchList(selectId, selectIdOption);
        getStoreStatistics();
    });     //select box의 change 이벤트

    $('#searchButton').click(function () {
        getStoreStatistics();
        searchList(selectId, selectIdOption);
    });     // 조건 검색 버튼
});

function timeSet(time) {
    if (time) {
        let d = new Date(time);
        return $.datepicker.formatDate('mm-dd ', d) + ('0' + d.getHours()).slice(-2) + ':' + ('0' + d.getMinutes()).slice(-2);
    } else {
        return "-";
    }
}

function timeSetDate(time) {
    if (time) {
        let d = new Date(time);
        return $.datepicker.formatDate('yy.mm.dd ', d);
    } else {
        return "-";
    }
}

function    totalTimeSet(time) {
    if (time) {
        let d = new Date(time);
        return ('0' + d.getUTCHours()).slice(-2) + ':' + ('0' + d.getUTCMinutes()).slice(-2) + ':' + ('0' + d.getUTCSeconds()).slice(-2);
    } else {
        return "-";
    }
}

function averageTimeSet(time,i) {
    if (time && i) {
        let d = new Date(time/i);
        return ('0' + d.getUTCHours()).slice(-2) + ':' + ('0' + d.getUTCMinutes()).slice(-2) + ':' + ('0' + d.getUTCSeconds()).slice(-2);
    } else {
        return "-";
    }
}


function minusTimeSet(time1, time2) {
    if (time2) {
        let d1 = new Date(time1);
        let d2 = new Date(time2);
        let minusTime = new Date(d2.getTime() - d1.getTime());
        return ('0' + minusTime.getUTCHours()).slice(-2) + ':' + ('0' + minusTime.getUTCMinutes()).slice(-2);
    } else {
        return "-";
    }
}

function minusTimeSet2(time1, time2) {
    let d1 = new Date(time1);
    let d2 = new Date(time2);
    if(d2.getTime() - d1.getTime() >=0){
        let minusTime = new Date(d2.getTime() - d1.getTime());
        return ('0' + minusTime.getUTCHours()).slice(-2) + ':' + ('0' + minusTime.getUTCMinutes()).slice(-2) + ':' + ('0' + minusTime.getUTCSeconds()).slice(-2);
    }else{
        let minusTime = new Date(Math.abs(d2.getTime() - d1.getTime()));
        return "-"+('0' + minusTime.getUTCHours()).slice(-2) + ':' + ('0' + minusTime.getUTCMinutes()).slice(-2) + ':' + ('0' + minusTime.getUTCSeconds()).slice(-2);
    }
}

let map;
let marker;

function initMap() {
    let uluru = {lat: 37.5806376, lng: 126.9058433};
    map = new google.maps.Map(document.getElementById('map'), {
        zoom: 15,
        center: uluru
    });
    marker = new google.maps.Marker({
        position: uluru,
        map: map
    });
}

function minusTime(time1, time2) {
    let d1 = new Date(time1);
    let d2 = new Date(time2);
    let minusTime = d2.getTime() - d1.getTime();
    return minusTime;
}

/*function timepickerConfirm(time1 , time2, createdTime) {
    let d1 = new Date(time1);
    let d2 = new Date(time2);
    d1.setHours(0,0,0);
    d2.setHours(0,0,0);
    d2.setDate(d2.getDate()+1);
    let cT = new Date(createdTime);
    if(minusTime(cT,d2)>=0 && minusTime(d1,cT)>=0){
        return true;
    }else{
        return false;
    }
}*/
function getStatisticsInfo(regOrderId) {
    $.ajax({
        url: "/getStatisticsInfo",
        type: 'get',
        data: {
            regOrderId: regOrderId
        },
        dataType: 'json',
        success: function (data) {
            if (data.status == 3) {
                $status = '<i class="ic_txt">' + status_completed + '</i>';
            }
            else {
                $status = '<i class="ic_txt ic_red">' + status_canceled + '</i>';
            }

            $('.tit').html('<h2>' + order_detail + ' - ' + regOrderIdReduce(regOrderId) + '</h2>' + $status);
            $('.tit').attr("orderId", data.regOrderId);

            $('#createdDatetime').html(timeSet(data.createdDatetime));
            $('#reservationDatetime').html(timeSet(data.reservationDatetime));
            $('#assignedDatetime').html(timeSet(data.assignedDatetime));
            $('#pickedUpDatetime').html(timeSet(data.pickedUpDatetime));
            $('#completedDatetime').html(timeSet(data.completedDatetime));
            //$('#passtime').html(minusTimeSet(data.createdDatetime, data.completedDatetime));
            $('#passtime').html(minusTimeSet(data.assignedDatetime, data.completedDatetime));
            $('#menuName').html(data.menuName);
            $('#cookingTime').html(data.cookingTime);
            $('#menuPrice').html(data.menuPrice);
            $('#deliveryPrice').html(data.deliveryPrice);
            $('#totalPrice').html(data.totalPrice);

            if (data.paid == 0) {
                $paid = order_payment_cash;
            }
            else if (data.paid == 1) {
                $paid = order_payment_card;
            }
            else if (data.paid == 2) {
                $paid = order_payment_prepayment;
            }
            else if (data.paid == 3) {
                $paid = order_payment_service;
            } else {
                $paid = "-";
            }

            $('#paid').html($paid);

            if (data.combinedOrderId) {
                $('#combinedOrder').html(data.combinedOrderId);
            }

            if (data.riderId) {
                $('#riderName').html(data.rider.name);
                $('#riderPhone').html(data.rider.phone);
            } else {
                $('#riderName').html("-");
                $('#riderPhone').html("-");
            }
            if (data.memo != null) {
                $('#memo').html(data.message);
            }
            $('#userPhone').html(data.phone);
            $('#userAddress').html(data.address);
            $('#distance').html(data.distance);
            map.setCenter({lat: parseFloat(data.latitude), lng: parseFloat(data.longitude)});
            marker.setPosition({lat: parseFloat(data.latitude), lng: parseFloat(data.longitude)});
        }
    });
}

function getStoreStatistics() {
    let diffDate = Math.ceil((new Date($('#endDate').val()).getTime() - new Date($('#startDate').val()).getTime()) / (1000*3600*24));
    if (diffDate > 31){
        return;
    }

    let myData = [];
    loading.show();
    $.ajax({
        url: "/getStoreStatisticsByOrder",
        type: 'get',
        data: {
            startDate: $('#startDate').val(),
            endDate: $('#endDate').val(),
            groupID: $("#statisticsGroupList").val(),
            subGroupName: $("#statisticsSubGroupList").val(),
            storeID: $("#statisticsStoreList").val(),
        },
        dataType: 'json',
        success: function (data) {
            let chkReturnTimeCnt = 0;
            let chkDistanceCnt = 0;
            let rowNum = 0;
            let orderPickupSum = 0;
            let pickupCompleteSum = 0;
            let riderStayTimeSum = 0;
            let orderCompleteSum = 0;
            let completeReturnSum = 0;
            let pickupReturnSum = 0;
            let orderReturnSum = 0;
            let distanceSum = 0;

            for (let key in data) {
                if (data.hasOwnProperty(key)) {
                    let tmpData = new Object();
                    tmpData.No = ++rowNum;
                    tmpData.reg_order_id = regOrderIdReduce(data[key].regOrderId);
                    tmpData.id = data[key].id;
                    tmpData.origin_reg_order_id = data[key].regOrderId;
                    tmpData.orderDate = timeSetDate(data[key].createdDatetime);
                    //tmpData.orderPickup1 = minusTimeSet2(data[key].createdDatetime, data[key].pickedUpDatetime);
                    tmpData.orderPickup1 = minusTimeSet2(data[key].assignedDatetime, data[key].pickedUpDatetime);

                    // 배정 ~ 픽업 시간이 1분 미만인 경우 파란색으로 표시
                    tmpData.orderPickup1 = diffTimeBlue(data[key].assignedDatetime, data[key].pickedUpDatetime, minusTimeSet2(data[key].assignedDatetime, data[key].pickedUpDatetime));

                    tmpData.pickupComplete1 =  minusTimeSet2(data[key].pickedUpDatetime, data[key].arrivedDatetime);

                    if (data[key].arrivedDatetime){
                        // 도착 ~ 완료 시간이 1분 미만인 경우 파란색으로 표시
                        tmpData.riderStayTime = diffTimeBlue(data[key].arrivedDatetime, data[key].completedDatetime, minusTimeSet2(data[key].arrivedDatetime, data[key].completedDatetime));
                        riderStayTimeSum += minusTime(data[key].arrivedDatetime, data[key].completedDatetime);
                    }else{
                        tmpData.riderStayTime = "-";
                    }

                    //tmpData.orderComplete1 = minusTimeSet2(data[key].createdDatetime, data[key].arrivedDatetime);
                    tmpData.orderComplete1 = minusTimeSet2(data[key].assignedDatetime, data[key].arrivedDatetime);
                    
                    //orderPickupSum += minusTime(data[key].createdDatetime, data[key].pickedUpDatetime);
                    //orderPickupSum += minusTime(data[key].assignedDatetime, data[key].pickedUpDatetime);
                    orderPickupSum += minusTime(data[key].assignedDatetime, data[key].pickedUpDatetime);   // 예약 시간에서 30분을 제외한 값
                    pickupCompleteSum += minusTime(data[key].pickedUpDatetime, data[key].arrivedDatetime);
                    //orderCompleteSum += minusTime(data[key].createdDatetime, data[key].arrivedDatetime);
                    orderCompleteSum += minusTime(data[key].assignedDatetime, data[key].arrivedDatetime);

                    // 검색 조건을 위한 데이터 입력
                    tmpData.store_name = data[key].store.storeName;
                    tmpData.rider_name = data[key].rider.name;
                    if (data[key].group){
                        tmpData.group_name = data[key].group.name;
                    }else{
                        tmpData.group_name = group_none;
                    }

                    if (data[key].subGroup){
                        tmpData.subGroup_name = data[key].subGroup.name;
                    }else{
                        tmpData.subGroup_name = group_none;
                    }


                    if(data[key].returnDatetime){
                        //tmpData.completeReturn1 = minusTimeSet2(data[key].arrivedDatetime, data[key].returnDatetime);
                        tmpData.completeReturn1 = minusTimeSet2(data[key].completedDatetime, data[key].returnDatetime);           // 21.05.17 완료 시간 ~ 복귀 시간으로 변경
                        tmpData.pickupReturn1 = minusTimeSet2(data[key].pickedUpDatetime, data[key].returnDatetime);
                        //tmpData.orderReturn1 = minusTimeSet2(data[key].createdDatetime, data[key].returnDatetime);
                        tmpData.orderReturn1 = minusTimeSet2(data[key].assignedDatetime, data[key].returnDatetime);
                        //completeReturnSum += minusTime(data[key].arrivedDatetime, data[key].returnDatetime);
                        completeReturnSum += minusTime(data[key].completedDatetime, data[key].returnDatetime);              // 21.05.17 완료 시간 ~ 복귀 시간으로 변경
                        pickupReturnSum += minusTime(data[key].pickedUpDatetime, data[key].returnDatetime);
                        //orderReturnSum += minusTime(data[key].createdDatetime, data[key].returnDatetime);
                        orderReturnSum += minusTime(data[key].assignedDatetime, data[key].returnDatetime);
                    }else{
                        chkReturnTimeCnt++;
                    }

                    if(data[key].distance){
                        tmpData.distance = parseFloat(data[key].distance).toFixed(2) + 'km';
                        distanceSum += parseFloat(data[key].distance);
                    }else{
                        chkDistanceCnt++;
                    }

                    myData.push(tmpData);
                }
            }
            let totalData = new Object();
            totalData.No = rowNum+1;
            totalData.reg_order_id = "TOTAL";
            totalData.id = "";
            totalData.origin_reg_order_id = "";
            totalData.orderDate = "";
            totalData.orderPickup1 = totalTimeSet(orderPickupSum);
            totalData.pickupComplete1 = totalTimeSet(pickupCompleteSum);
            totalData.orderComplete1 = totalTimeSet(orderCompleteSum);
            totalData.riderStayTime = totalTimeSet(riderStayTimeSum);
            totalData.completeReturn1 = totalTimeSet(completeReturnSum);
            totalData.pickupReturn1 = totalTimeSet(pickupReturnSum);
            totalData.orderReturn1 = totalTimeSet(orderReturnSum);
            totalData.distance = (distanceSum ==0?0:parseFloat(distanceSum).toFixed(2)) + 'km';

            // 검색 조건을 위한 데이터 입력
            totalData.store_name = "";
            totalData.rider_name = "";
            totalData.group_name = "";
            totalData.subGroup_name = "";

            myData.push(totalData);

            let averageData = new Object();
            averageData.No = rowNum+2;
            averageData.reg_order_id = "AVERAGE";
            averageData.id = "";
            averageData.origin_reg_order_id = "";
            averageData.orderDate = "";
            averageData.orderPickup1 = averageTimeSet(orderPickupSum,rowNum);
            averageData.pickupComplete1 = averageTimeSet(pickupCompleteSum,rowNum);
            averageData.orderComplete1 = averageTimeSet(orderCompleteSum,rowNum);
            averageData.riderStayTime = averageTimeSet(riderStayTimeSum,rowNum);
            averageData.completeReturn1 = averageTimeSet(completeReturnSum,rowNum - chkReturnTimeCnt);
            averageData.pickupReturn1 = averageTimeSet(pickupReturnSum,rowNum - chkReturnTimeCnt);
            averageData.orderReturn1 = averageTimeSet(orderReturnSum,rowNum - chkReturnTimeCnt);
            averageData.distance = (distanceSum == 0?0:parseFloat(distanceSum/(rowNum - chkDistanceCnt)).toFixed(2)) + 'km';

            // 검색 조건을 위한 데이터 입력
            averageData.store_name = "";
            averageData.rider_name = "";
            averageData.group_name = "";
            averageData.subGroup_name = "";

            myData.push(averageData);

            if (myData != null) {
                jQuery('#jqGrid').jqGrid('clearGridData');
                jQuery('#jqGrid').jqGrid('setGridParam', {data: myData, page: 1});
                jQuery('#jqGrid').trigger('reloadGrid');
            }
            $("#jqGrid").trigger("reloadGrid");

            $("#jqGrid").jqGrid({
                datatype: "local",
                data: myData,
                colModel: [
                    {label: 'No', name: 'No', width: 25, key: true, align: 'center', hidden: true},
                    {label: label_order_number, name: 'reg_order_id', width: 80, align: 'center'},
                    {label: order_id, name: 'id', width: 80, align: 'center', hidden: true},
                    {label: store_name, name: 'store_name', width: 80, align: 'center'},
                    {label: order_reg_order_id, name: 'origin_reg_order_id', width: 80, align: 'center', hidden: true},
                    {label: label_order_date, name: 'orderDate', width: 80, align: 'center'},
                    {label: label_order_in_store_time, name: 'orderPickup1', index: 'orderPickup1', width: 80, align: 'center'},
                    {label: label_order_delivery_time, name: 'pickupComplete1', index: 'pickupComplete1', width: 80, align: 'center'},
                    {label: label_order_completed_time, name: 'orderComplete1', index: 'orderComplete1', width: 80, align: 'center'},
                    {label: label_order_stay_time, name: 'riderStayTime', index: 'riderStayTime', width: 80, align: 'center'},
                    {label: label_order_return_time, name: 'completeReturn1', index: 'completeReturn1', width: 80, align: 'center'},
                    {label: label_order_out_time, name: 'pickupReturn1', index: 'pickupReturn1', width: 80, align: 'center'},
                    {label: label_order_total_time, name: 'orderReturn1', index: 'orderReturn1', width: 80, align: 'center'},
                    {label: label_order_distance, name: 'distance', width: 80, align: 'center'},

                    {label: group_name, name: 'group_name', width: 80, align: 'center', hidden: true},
                    {label: subGroup_name, name: 'subGroup_name', width: 80, align: 'center', hidden: true},
                    {label: rider_name, name: 'rider_name', width: 80, align: 'center', hidden: true}
                ],
                // minHeight: 400,
                height: 680,
                autowidth: true,
                rowNum: 20,
                // footerrow: true,
                pager: "#jqGridPager",
                ondblClickRow: function (rowid, icol, cellcontent, e) {
                    let rowData = jQuery(this).getRowData(rowid);
                    let No = rowData['No'];
                    let regOrderId = rowData['origin_reg_order_id'];
                    if(No != 10000000){
                        getStatisticsInfo(regOrderId);
                        $('.state_wrap').addClass('on'); //상세보기 열기
                        setTimeout(function () {
                            $(window).trigger('resize');
                        }, 300)//그리드 리사이즈
                    }
                }
                // ,loadComplete: function (data) {
                //     let ids = $("#jqGrid").getDataIDs();
                //
                //     $.each(ids, function (idx, rowId) {
                //         let objRowData = $("#jqGrid").getRowData(rowId);
                //
                //         if (objRowData.approvalStatus == "2" || objRowData.approvalStatus == "3"){
                //             $("#jqGrid").setRowData(rowId, false, {background: '#FFAA55'});
                //         }else if (objRowData.approvalStatus == "4"){
                //             $("#jqGrid").setRowData(rowId, false, {background: '#f5c717'});
                //         }
                //     });
                // }
            });

            resizeJqGrid('#jqGrid'); //그리드 리사이즈
            loading.hide();

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

function excelDownloadByOrder(){
    let startDate = $('#startDate').val();
    let endDate = $('#endDate').val();

    let diffDate = Math.ceil((new Date(endDate).getTime() - new Date(startDate).getTime()) / (1000*3600*24));
    if (diffDate > 31){
        return;
    }

    loading.show();
    $.fileDownload("/excelDownloadByOrder",{
        httpMethod:"GET",
        data : {
            startDate : startDate,
            endDate : endDate
        },
        successCallback: function(url){
            loading.hide();
        },
        failCallback: function(responseHtml,url){
            loading.hide();
        }
    })
}

// 21.05.13
function diffTimeBlue(time1, time2, time3){
    var result = time3;
    // timer2 - timer1의 시간이 1분 미만인 경우 timer3의 시간을 blue 색으로 보이게 한다.
    if (time1 && time2){
        let t1 = new Date(time1);
        let t2 = new Date(time2);

        if (t2.getTime() - t1.getTime() < 60000){
            result = '<span style="color: blue">' + time3 + '</span>'
        }
    }
    return result;
}

//// 20.04.24 이벤트 추가제
/**
 * 검색 조건 관련 함수
 * */
function searchList(selectId, selectIdOption) {

    if(selectId.attr('id')=="statisticsGroupList"){
        $("#statisticsStoreList").html("<option value='reset'>" + list_search_all_store + "</option>");
        if(selectIdOption.val() == "reset"){
            $("#statisticsSubGroupList").html("<option value='reset'>" + list_search_all_subgroup + "</option>");
        }else{
            $("#statisticsSubGroupList").val("reset").prop("selected", true);
        }
    }else if(selectId.attr('id')=="statisticsSubGroupList"){
        if(selectIdOption.val() == "reset") {
            $("#statisticsStoreList").html("<option value='reset'>" + list_search_all_store + "</option>");
        }else{
            $("#statisticsStoreList").val("reset").prop("selected", true);
        }
    }

    var searchText = $("#searchText").val();


    var filter2= {
        groupOp: "OR",
        rules: []
    };

    var select = $("#searchSelect option:selected").val();

    if(select == 'reg_order_id'){
        filter2.rules.push({
            field : 'reg_order_id',
            op : "eq",
            data : searchText
        });
    }else if(select == 'all'){
        filter2.rules.push({
            field : 'reg_order_id',
            op : "eq",
            data : searchText
        });
        filter2.rules.push({
            field : 'rider_name',
            op : "cn",
            data : searchText
        });
    }else if (select == 'rider'){
        filter2.rules.push({
            field : 'rider_name',
            op : "cn",
            data : searchText
        });
    }

    var filter3 = {
        groupOp: "OR",
        rules: [],
        groups : [filter2]
    };

    filter3.rules.push({
        field : 'reg_order_id',
        op : "eq",
        data : 'TOTAL'
    });

    filter3.rules.push({
        field: "reg_order_id",
        op : "eq",
        data : "AVERAGE"
    });

    var grid = jQuery('#jqGrid');
    if(filter2.rules.length > 0 || filter3.rules.length > 0){
        grid[0].p.search = true;
    }
    $.extend(grid[0].p.postData, { filters: filter3 });
    grid.trigger("reloadGrid", [{ page: 1 }]);
}

/**
 * 그룹 List 불러오기
 */
function getGroupList() {
    $.ajax({
        url : "/getStatisticsGroupList",
        type : 'get',
        data : {

        },
        async : false,
        dataType : 'json',
        success : function(data) {
            if (data) {

                var statisticsGroupListHtml = "<option value='reset'>" + list_search_all_group + "</option>";
                // var statisticsGroupListHtml = "";
                for (var i in data) {
                    statisticsGroupListHtml += "<option value='" + data[i].id + "'>" + data[i].name + "</option>";
                }
                statisticsGroupListHtml += "<option value='none'>" + group_none + "</option>";
                $("#statisticsGroupList").html(statisticsGroupListHtml);

                $("#statisticsGroupList").on("change", function () {
                    getStatisticsSubGroupList($("#statisticsGroupList option:selected").val());
                });
            }
        }
    });
}

/**
 * 서브 그룹 List 불러오기
 */
function getStatisticsSubGroupList(gId, subGroup) {
    var selectGroupId = null;

    if (gId == null) {
        selectGroupId = '1';
    } else {
        selectGroupId = gId
    }

    $.ajax({
        url : "/getStatisticsSubGroupList",
        type : 'get',
        data : {
            groupId : selectGroupId
        },
        async : false,
        dataType : 'json',
        success : function(data){
            if(data) {
                var pstatisticsSubGroupListHtml = "<option value='reset'>" + list_search_all_subgroup + "</option>";
                for (var i in data){
                    pstatisticsSubGroupListHtml += "<option value='" + data[i].name  + "'>" + data[i].name + "</option>";
                }
                $("#statisticsSubGroupList").html(pstatisticsSubGroupListHtml);

                $("#statisticsSubGroupList").on("change", function () {
                    getStatisticsStoreList($("#statisticsSubGroupList option:selected").val(),$("#statisticsGroupList option:selected").val());
                });

            }
        }
    });
}

/**
 * 상점 List 불러오기
 */
function getStatisticsStoreList(subId, gId) {
    var selectGroupId = gId;
    var selecSubGroupId = subId;
    // debugger;
    $.ajax({
        url : "/getStatisticsStoreList",
        type : 'get',
        data : {
            groupId : selectGroupId,
            subGroupName : selecSubGroupId
        },
        async : false,
        dataType : 'json',
        success : function(data){
            if(data) {
                var statisticsStoreListHtml = "<option value='reset'>" + list_search_all_store + "</option>";
                for (var i in data){
                    statisticsStoreListHtml += "<option value='" + data[i].storeId  + "'>" + data[i].storeName + "</option>";
                }
                $("#statisticsStoreList").html(statisticsStoreListHtml);

            }
        }
    });
}
