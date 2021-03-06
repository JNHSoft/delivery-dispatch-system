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

            $('.tit').html('<h2>' + order_detail + ' - ' + regOrderIdReduce(regOrderId) + '</h2>' + $status);
            $('.tit').attr("orderId", data.regOrderId);

            $('#createdDatetime').html(timeSet(data.createdDatetime));
            $('#reservationDatetime').html(timeSet(data.reservationDatetime));
            $('#assignedDatetime').html(timeSet(data.assignedDatetime));
            $('#pickedUpDatetime').html(timeSet(data.pickedUpDatetime));
            $('#completedDatetime').html(timeSet(data.completedDatetime));
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
    let diffDate = Math.ceil((new Date($('#day2').val()).getTime() - new Date($('#day1').val()).getTime()) / (1000*3600*24));
    if (diffDate > 31){
        return;
    }


    let myData = [];
    loading.show();
    $.ajax({
        url: "/getStoreStatisticsByOrderAtTWKFC",
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
            let orderCompleteSum = 0;
            let completeReturnSum = 0;
            let pickupReturnSum = 0;
            let orderReturnSum = 0;
            let distanceSum = 0;

            let QTTimerSum=0;

            for (let key in data) {
                if (data.hasOwnProperty(key)) {
                    let tmpData = new Object();
                    tmpData.No = ++rowNum;
                    tmpData.reg_order_id = regOrderIdReduce(data[key].regOrderId);
                    tmpData.id = data[key].id;
                    tmpData.origin_reg_order_id = data[key].regOrderId;
                    tmpData.orderDate = timeSetDate(data[key].createdDatetime);

                    tmpData.assignedDate = timeSet(data[key].assignedDatetime);
                    tmpData.qtTimes = data[key].cookingTime;

                    tmpData.orderPickup1 = minusTimeSet2(data[key].assignedDatetime, data[key].pickedUpDatetime);

                    tmpData.pickupComplete1 =  minusTimeSet2(data[key].pickedUpDatetime, data[key].arrivedDatetime);
                    tmpData.orderComplete1 = minusTimeSet2(data[key].assignedDatetime, data[key].arrivedDatetime);

                    orderPickupSum += minusTime(data[key].assignedDatetime, data[key].pickedUpDatetime);
                    pickupCompleteSum += minusTime(data[key].pickedUpDatetime, data[key].arrivedDatetime);
                    orderCompleteSum += minusTime(data[key].assignedDatetime, data[key].arrivedDatetime);
                    QTTimerSum += Number(data[key].cookingTime);

                    if(data[key].returnDatetime){
                        tmpData.completeReturn1 = minusTimeSet2(data[key].arrivedDatetime, data[key].returnDatetime);
                        tmpData.pickupReturn1 = minusTimeSet2(data[key].pickedUpDatetime, data[key].returnDatetime);
                        tmpData.orderReturn1 = minusTimeSet2(data[key].assignedDatetime, data[key].returnDatetime);
                        completeReturnSum += minusTime(data[key].arrivedDatetime, data[key].returnDatetime);
                        pickupReturnSum += minusTime(data[key].pickedUpDatetime, data[key].returnDatetime);
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
            totalData.completeReturn1 = totalTimeSet(completeReturnSum);
            totalData.pickupReturn1 = totalTimeSet(pickupReturnSum);
            totalData.orderReturn1 = totalTimeSet(orderReturnSum);
            totalData.distance = (distanceSum ==0?0:parseFloat(distanceSum).toFixed(2)) + 'km';

            totalData.qtTimes = QTTimerSum;

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
            averageData.completeReturn1 = averageTimeSet(completeReturnSum,rowNum - chkReturnTimeCnt);
            averageData.pickupReturn1 = averageTimeSet(pickupReturnSum,rowNum - chkReturnTimeCnt);
            averageData.orderReturn1 = averageTimeSet(orderReturnSum,rowNum - chkReturnTimeCnt);
            averageData.distance = (distanceSum == 0?0:parseFloat(distanceSum/(rowNum - chkDistanceCnt)).toFixed(2)) + 'km';

            averageData.qtTimes = QTTimerSum / rowNum;

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
                    {label: label_order_assign, name: 'assignedDate', width: 80, align: 'center'},
                    {label: label_order_qt, name: 'qtTimes', width: 80, align: 'center'},
                    {label: label_order_in_store_time , name: 'orderPickup1', index: 'orderPickup1', width: 80, align: 'center'},
                    {label: label_order_delivery_time, name: 'pickupComplete1', index: 'pickupComplete1', width: 80, align: 'center'},
                    {label: label_order_completed_time, name: 'orderComplete1', index: 'orderComplete1', width: 80, align: 'center'},
                    {label: label_order_return_time, name: 'completeReturn1', index: 'completeReturn1', width: 80, align: 'center'},
                    {label: label_order_out_time, name: 'pickupReturn1', index: 'pickupReturn1', width: 80, align: 'center'},
                    {label: label_order_total_time, name: 'orderReturn1', index: 'orderReturn1', width: 80, align: 'center'},
                    {label: label_order_distance, name: 'distance', width: 80, align: 'center'},
                ],

                height: 680,
                autowidth: true,
                rowNum: 20,

                pager: "#jqGridPager",
                ondblClickRow: function (rowid, icol, cellcontent, e) {
                    let rowData = jQuery(this).getRowData(rowid);
                    let No = rowData['No'];
                    let regOrderId = rowData['origin_reg_order_id'];
                    if(No != 10000000){
                        getStatisticsInfo(regOrderId);
                        $('.state_wrap').addClass('on'); //???????????? ??????
                        setTimeout(function () {
                            $(window).trigger('resize');
                        }, 300)//????????? ????????????
                    }
                }
            });

            resizeJqGrid('#jqGrid'); //????????? ????????????
            loading.hide();
            $('.state_wrap .btn_close').click(function (e) {
                e.preventDefault();
                $('.state_wrap').removeClass('on'); //???????????? ??????
                setTimeout(function () {
                    $(window).trigger('resize');
                }, 300)//????????? ????????????
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
    $.fileDownload("/excelDownloadByOrderAtTWKFC",{
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