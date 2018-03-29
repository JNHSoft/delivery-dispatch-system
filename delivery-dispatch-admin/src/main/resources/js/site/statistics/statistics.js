/*<![CDATA[*/
$(document).ready(function () {

    // 날짜 변경될때마다
    $('input[name="datepicker"]').change(function () {
        getStatisticsList();
    })

    // 오늘 날짜로 초기화
    $('input[name=datepicker]').val($.datepicker.formatDate('yy-mm-dd',new Date));

    getStatisticsList();
    getGroupList();

    $(".select").change(function () {
       var searchText1= $("#statisticsGroupList").text();
       var searchText2= $("#statisticsSubGroupList").text();
       var searchText3= $("#statisticsStoreList").text();



       console.log("change valalalalvlavlalvl");
       console.log(searchText1);
       console.log(searchText2);
       console.log(searchText3);

        var filter = {
            groupOp: "OR",
            rules: []
        };
        if(searchText1 != ""){
            filter.rules.push({
                field : 'th1',
                op : "eq",
                data : searchText1
            });
            if(searchText2 != ""){
                filter.rules.push({
                    field : 'th2',
                    op : "eq",
                    data : searchText2
                });
                if(searchText3 != ""){
                    filter.rules.push({
                        field : 'th3',
                        op : "eq",
                        data : searchText3
                    });
                }
            }
        }
        var grid = jQuery('#jqGrid');
        grid[0].p.search = filter.rules.length > 0;
        $.extend(grid[0].p.postData, { filters: JSON.stringify(filter) });
        grid.trigger("reloadGrid", [{ page: 1 }]);
    });

});

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

                var statisticsGroupListHtml = "<option value=''>" + list_search_all_group + "</option>";
                // var statisticsGroupListHtml = "";
                for (var i in data) {
                    statisticsGroupListHtml += "<option value='" + data[i].id + "'>" + data[i].name + "</option>";
                }
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
                var pstatisticsSubGroupListHtml = "";
                for (var i in data){
                    pstatisticsSubGroupListHtml += "<option value='" + data[i].id  + "'>" + data[i].name + "</option>";
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
    var selectGroupId = null;


        selectGroupId = gId;

    var selecSubGroupId = null;

        selecSubGroupId = subId;
    console.log(gId);
    console.log(subId);
    // debugger;
    $.ajax({
        url : "/getStatisticsStoreList",
        type : 'get',
        data : {
            groupId : selectGroupId,
            subGroupId : selecSubGroupId
        },
        async : false,
        dataType : 'json',
        success : function(data){
            if(data) {
                var statisticsStoreListHtml = "";
                for (var i in data){
                    statisticsStoreListHtml += "<option value='" + data[i].id  + "'>" + data[i].storeName + "</option>";
                }
                $("#statisticsStoreList").html(statisticsStoreListHtml);


                $("#statisticsStoreList").on("change", function () {
                    getStatisticsList();
                });

            }
        }
    });
}

/**
 * 시간변환
 */
function timeSet(time) {
    if(time != null){
        var d = new Date(time);
        return $.datepicker.formatDate('mm-dd ', d) + ('0' + d.getHours()).slice(-2) + ':' + ('0' + d.getMinutes()).slice(-2);
    }else{
        return "-";
    }
}


/**
 * 경과시간 적용
 */
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

/**
 * 시간계산하고 변환해주는 함수
 */
function minusTime(time1, time2) {
    var d1 = new Date(time1);
    var d2 = new Date(time2);
    var minusTime = d2.getTime()-d1.getTime();
    return minusTime;
}


// function minusTime(time1, time2) {
//     time1 += " 00:00:00";
//     time2 += " 00:00:00";
//     var d1 = new Date(time1);
//     var d2 = new Date(time2);
//     var minusTime = d2.getTime()-d1.getTime();
//     return minusTime;
// }


/**
 * 날짜 계산 
 * 날짜 사이에 주문등록 날짜가 있는지
 */
function timepickerConfirm(time1 , time2, createdTime) {
    var d1 = new Date(time1);
    var d2 = new Date(time2);
    var cT = new Date(createdTime);
    if(minusTime(cT,d2)>=0 && minusTime(d1,cT)>=0){
        return true;
    }else{
        return false;
    }
}


/**
 * map 호출 
 */
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

/**
 * 통계 목록 List
 */
function getStatisticsList() {
    var $mydata = [];
    $.ajax({
        url: "/getStatisticsList",
        type: 'get',
        data: {
            startDate: $('#startDate').val(),
            endDate: $('#endDate').val()
        },
        dataType: 'json',
        success: function (data) {

            var i = 1;
            for (var key in data) {
                var $tmpData = new Object();
                var tmpStart = new Date($('#startDate').val());
                tmpStart.setHours('00');
                tmpStart.setMinutes('00');
                tmpStart.setSeconds('00');

                var tmpEnd = new Date($('#endDate').val());
                tmpEnd.setHours('23');
                tmpEnd.setMinutes('59');
                tmpEnd.setSeconds('59');
                tmpEnd.setDate(tmpEnd.getDate()+1);

                if (timepickerConfirm(tmpStart, tmpEnd, data[key].createdDatetime)) {
                console.log(data);
                if (data.hasOwnProperty(key)) {

                    $tmpData.th0 = i++
                    $tmpData.th1 = data[key].group.name
                    $tmpData.th2 = data[key].subGroup.name
                    $tmpData.th3 = data[key].store.storeName
                    if (data[key].status == "3") {
                        $tmpData.th4 = status_completed
                    } else if (data[key].status == "4") {
                        $tmpData.th4 = status_canceled
                    }
                    $tmpData.th5 = data[key].id
                    $tmpData.th6 = timeSet(data[key].createdDatetime);
                    $tmpData.th7 = data[key].address
                    $tmpData.th8 = data[key].menuName
                    $tmpData.th9 = data[key].cookingTime

                    if (data[key].paid != null) {
                        if (data[key].paid == "0") {
                            $tmpData.th10 = order_payment_card;
                        } else if (data[key].paid == "1") {
                            $tmpData.th10 = order_payment_cash;
                        } else if (data[key].paid == "2") {
                            $tmpData.th10 = order_payment_prepayment;
                        } else if (data[key].paid == "3"){
                            $tmpData.th10 = order_payment_service;
                        }
                    }
                    if (!data[key].assignedDatetime) {
                        $tmpData.th11 = "-";
                    } else {
                        $tmpData.th11 = timeSet(data[key].assignedDatetime);
                    }

                    if (!data[key].pickedUpDatetime) {
                        $tmpData.th12 = "-";
                    } else {
                        $tmpData.th12 = timeSet(data[key].pickedUpDatetime);
                    }

                    if (!data[key].reservationDatetime) {
                        $tmpData.th13 = "-";
                    } else {
                        $tmpData.th13 = timeSet(data[key].reservationDatetime);
                    }

                    $tmpData.th14 = data[key].rider.name


                    if(data[key].group != null){
                        $tmpData.th15 = data[key].group.id

                    }
                    if(data[key].subGroup != null) {
                        $tmpData.th16 = data[key].subGroup.id
                    }

                    if(data[key].storeId != null) {
                        $tmpData.th17 = data[key].storeId
                    }


                    $mydata.push($tmpData);

                }
            }
        }
            if ($mydata != null) {
                jQuery('#jqGrid').jqGrid('clearGridData')
                jQuery('#jqGrid').jqGrid('setGridParam', {data: $mydata, page: 1})
                jQuery('#jqGrid').trigger('reloadGrid');
            }
            $("#jqGrid").trigger("reloadGrid");

            $("#jqGrid").jqGrid({
                datatype: "local",
                data: $mydata,
                colModel: [
                    {label: 'No', name: 'th0', width: 25, key: true, align: 'center'},
                    {label: group_name, name: 'th1', width: 80, align: 'center'},
                    {label: subGroup_name, name: 'th2', width: 80, align: 'center'},
                    {label: store_name, name: 'th3', width: 120, align: 'center'},
                    {label: order_status, name: 'th4', width: 80, align: 'center'},
                    {label: order_id, name: 'th5', width: 80, align: 'center'},
                    {label: order_created, name: 'th6', width: 80, align: 'center'},
                    {label: order_address, name: 'th7', width: 200},
                    {label: order_summary, name: 'th8', width: 80},
                    {label: order_cooking, name: 'th9', width: 80, align: 'center'},
                    {label: order_payment, name: 'th10', width: 80, align: 'center'},
                    {label: order_assigned, name: 'th11', width: 80, align: 'center'},
                    {label: order_pickedup, name: 'th12', width: 80, align: 'center'},
                    {label: order_reserved, name: 'th13', width: 80, align: 'center'},
                    {label: rider_name, name: 'th14', width: 80, align: 'center'},
                    {label:'그룹ID', name:'th15', width:60, hidden:'hidden'},
                    {label:'서브그룹ID', name:'th16', width:60, hidden:'hidden'},
                    {label:'매장ID', name:'th17', width:60, hidden:'hidden'}
                ],
                width: 'auto',
                height: 520,
                autowidth: true,
                rowNum: 20,
                pager: "#jqGridPager",
                ondblClickRow: function(rowid,icol,cellcontent,e){
                    var rowData = jQuery(this).getRowData(rowid);
                    var orderId = rowData['th5'];
                    console.log(orderId);
                    getStatisticsInfo(orderId);
                    $('.state_wrap').addClass('on'); //상세보기 열기
                    setTimeout(function(){
                        $(window).trigger('resize');
                    },300)//그리드 리사이즈
                }
            });

            resizeJqGrid('#jqGrid'); //그리드 리사이즈

            $('.state_wrap .btn_close').click(function (e) {
                e.preventDefault();
                $('.state_wrap').removeClass('on') //상세보기 닫기
                setTimeout(function () {
                    $(window).trigger('resize');
                }, 300)//그리드 리사이즈
            })
        }
    }
    )};


/**
 * 통계 상세보기
 */
function getStatisticsInfo(orderId) {

    var $paid = "";
    var $status = "";

    $.ajax({
        url : "/getStatisticsInfo",
        type : 'get',
        data : {
            orderId : orderId
        },
        dataType : 'json',
        success : function (data) {
            console.log(data);

            if (data.status == "3") {
                $status = '<i class="ic_txt">' + status_completed + '</i>';
            }
            else {
                $status = '<i class="ic_txt ic_red">' + status_canceled + '</i>';
            }
            $('.tit').html('<h2>' +order_detail + ' - '+ data.id + '</h2>'+$status);

            $('.tit').attr("orderId", data.id);

            $('#createdDatetime').html(timeSet(data.createdDatetime));
            $('#reservationDatetime').html(timeSet(data.reservationDatetime));
            $('#assignedDatetime').html(timeSet(data.assignedDatetime));
            $('#pickedUpDatetime').html(timeSet(data.pickedUpDatetime));
            $('#completedDatetime').html(timeSet(data.completedDatetime));
            $('#passtime').html(minusTimeSet(data.createdDatetime, data.completedDatetime));
            $('#menuName').html(data.menuName);
            $('#cookingTime').html(data.cookingTime);
            $('#menuPrice').html(data.menuPrice);
            $('#deliveryPrice').html(data.deliveryPrice);
            $('#totalPrice').html(data.totalPrice);


            if(data.payment) {
                if (data.payment.type == "0") {
                    $paid = "CARD";
                } else if (data.payment.type == "1") {
                    $paid = "CASH";
                } else if (data.payment.type == "2") {
                    $paid = "PREPAYMENT";
                } else {
                    $paid = "SERVICE";
                }
                $('#paid').html($paid);
            }

            if(data.combinedOrderId != null){
                $('#combinedOrder').html(data.combinedOrderId);
            }

            if(data.rider){
                $('#riderName').html(data.rider.name);
                $('#riderPhone').html(data.rider.phone);
            }else {
                $('#riderName').val("-");
                $('#riderPhone').val("-");
            }
            if(data.memo != null){
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
/*]]>*/


