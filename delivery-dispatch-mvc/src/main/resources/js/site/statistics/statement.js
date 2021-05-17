let loading= $('<div id="loading"><div><p style="background-color: #838d96"/></div></div>').appendTo(document.body).hide();
$(function () {
    let date = $.datepicker.formatDate('yy-mm-dd', new Date);
    $('#day1, #day2').val(date);

    $('#day1').datepicker({
        maxDate : date,
        onClose: function(selectedDate) {
            $('#day2').datepicker('option', 'minDate', selectedDate);
            getStoreStatistics();
        }
    });

    $('#day2').datepicker({
        minDate : date,
        onClose: function( selectedDate ) {
            $('#day1').datepicker('option', 'maxDate', selectedDate);
            getStoreStatistics();
        }
    });

    getStoreStatistics();
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

function totalTimeSet(time) {
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
            id: regOrderId
        },
        dataType: 'json',
        success: function (data) {
            if (data.status == 3) {
                $status = '<i class="ic_txt">' + status_completed + '</i>';
            }
            else {
                $status = '<i class="ic_txt ic_red">' + status_canceled + '</i>';
            }
            /*if(data.regOrderId){
                regOrderId = data.regOrderId;
            }else{
                regOrderId = "-";
            }*/
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

            /*if(data.payment) {
                if (data.payment.type == "0") {
                    $paid = order_payment_card;
                } else if (data.payment.type == "1") {
                    $paid = order_payment_cash;
                } else if (data.payment.type == "2") {
                    $paid = order_payment_prepayment;
                } else {
                    $paid = order_payment_service;
                }
            }else {
                $paid = "-";
            }*/
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
    let diffDate = Math.ceil((new Date($('#day2').val()).getTime() - new Date($('#day1').val()).getTime()) / (1000*3600*24));
    if (diffDate > 31){
        return;
    }


    let myData = [];
    loading.show();
    $.ajax({
        url: "/getStoreStatisticsByOrder",
        type: 'get',
        data: {
            startDate: $('#day1').val(),
            endDate: $('#day2').val()
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
                    //tmpData.orderPickup1 = minusTimeSet2(data[key].assignedDatetime, data[key].pickedUpDatetime);
                    // 21-05-13 PizzaHut만 예약시간에서 무조건 -30분한 시간으로 계산 될 수 있도록 적용 단, 배정 ~ 픽업 간의 시간 표기만 적용한다.
                    let tmpDate = new Date(data[key].reservationDatetime);
                    tmpDate.setMinutes(tmpDate.getMinutes()-30);            // 예약 시간에서 30분을 제외한다.

                    // 배정 ~ 픽업 시간이 1분 미만인 경우 파란색으로 표시
                    tmpData.orderPickup1 = diffTimeBlue(data[key].assignedDatetime, data[key].pickedUpDatetime, minusTimeSet2(tmpDate, data[key].pickedUpDatetime));           // 21-05-13 30분 제외 시간으로 변경
                    tmpData.pickupComplete1 =  minusTimeSet2(data[key].pickedUpDatetime, data[key].arrivedDatetime);

                    // 고객에게 머무른 시간 체크
                    if (data[key].arrivedDatetime){
                        tmpData.riderStayTime = diffTimeBlue(data[key].arrivedDatetime, data[key].completedDatetime, minusTimeSet2(data[key].arrivedDatetime, data[key].completedDatetime));
                        riderStayTimeSum += minusTime(data[key].arrivedDatetime, data[key].completedDatetime);
                    }else{
                        tmpData.riderStayTime = "-"
                    }

                    //tmpData.orderComplete1 = minusTimeSet2(data[key].createdDatetime, data[key].arrivedDatetime);
                    tmpData.orderComplete1 = minusTimeSet2(data[key].assignedDatetime, data[key].arrivedDatetime);

                    //orderPickupSum += minusTime(data[key].createdDatetime, data[key].pickedUpDatetime);
                    //orderPickupSum += minusTime(data[key].assignedDatetime, data[key].pickedUpDatetime);
                    orderPickupSum += minusTime(tmpDate, data[key].pickedUpDatetime);   // 예약 시간에서 30분을 제외한 값
                    pickupCompleteSum += minusTime(data[key].pickedUpDatetime, data[key].arrivedDatetime);
                    //orderCompleteSum += minusTime(data[key].createdDatetime, data[key].arrivedDatetime);
                    orderCompleteSum += minusTime(data[key].assignedDatetime, data[key].arrivedDatetime);

                    if(data[key].returnDatetime){
                        tmpData.completeReturn1 = minusTimeSet2(data[key].arrivedDatetime, data[key].returnDatetime);
                        tmpData.pickupReturn1 = minusTimeSet2(data[key].pickedUpDatetime, data[key].returnDatetime);
                        //tmpData.orderReturn1 = minusTimeSet2(data[key].createdDatetime, data[key].returnDatetime);
                        tmpData.orderReturn1 = minusTimeSet2(data[key].assignedDatetime, data[key].returnDatetime);
                        completeReturnSum += minusTime(data[key].arrivedDatetime, data[key].returnDatetime);
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
            myData.push(averageData);

            if (myData != null) {
                jQuery('#jqGrid').jqGrid('clearGridData');
                jQuery('#jqGrid').jqGrid('setGridParam', {data: myData, page: 1});
                jQuery('#jqGrid').trigger('reloadGrid');
            }
            $("#jqGrid").jqGrid({
                datatype: "local",
                data: myData,
                colModel: [
                    {label: 'No', name: 'No', width: 25, key: true, align: 'center', hidden: true},
                    {label: label_order_number, name: 'reg_order_id', width: 80, align: 'center'},
                    {label: order_id, name: 'id', width: 80, align: 'center', hidden: true},
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
            });

            /*jQuery("#grid").jqGrid('setGroupHeaders', {
                useColSpanStyle: true,
                groupHeaders:[
                    {startColumnName: 'orderPickup1', numberOfColumns: 1, titleText: '留店時間'},
                    {startColumnName: 'pickupComplete1', numberOfColumns: 1, titleText: '外送時間'},
                    {startColumnName: 'orderComplete1', numberOfColumns: 1, titleText: '外送達成時間'},
                    {startColumnName: 'completeReturn1', numberOfColumns: 1, titleText: '回店所需時間'},
                    {startColumnName: 'pickupReturn1', numberOfColumns: 1, titleText: '外出時間'},
                    {startColumnName: 'orderReturn1', numberOfColumns: 1, titleText: '完成整張外送時間'}
                ]
            });*/
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
    let startDate = $('#day1').val();
    let endDate = $('#day2').val();

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
