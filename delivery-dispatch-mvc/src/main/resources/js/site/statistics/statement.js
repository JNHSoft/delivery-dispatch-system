var loading= $('<div id="loading"><div><p style="background-color: #838d96"/></div></div>').appendTo(document.body).hide();
$(function () {
    $('input[name="datepicker"]').change(function () {
        getStoreStatistics();
    })
    $('input[name=datepicker]').val($.datepicker.formatDate('yy-mm-dd', new Date));
    getStoreStatistics();
});

function timeSet(time) {
    if (time != null) {
        var d = new Date(time);
        return $.datepicker.formatDate('mm-dd ', d) + ('0' + d.getHours()).slice(-2) + ':' + ('0' + d.getMinutes()).slice(-2);
    } else {
        return "-";
    }
}

function timeSetDate(time) {
    if (time != null) {
        var d = new Date(time);
        return $.datepicker.formatDate('yy.mm.dd ', d);
    } else {
        return "-";
    }
}

function totalTimeSet(time) {
    if (time != null) {
        var d = new Date(time);
        return ('0' + d.getUTCHours()).slice(-2) + ':' + ('0' + d.getUTCMinutes()).slice(-2) + ':' + ('0' + d.getUTCSeconds()).slice(-2);
    } else {
        return "-";
    }
}


function minusTimeSet(time1, time2) {
    if (time2 != null) {
        var d1 = new Date(time1);
        var d2 = new Date(time2);
        var minusTime = new Date(d2.getTime() - d1.getTime());
        return ('0' + minusTime.getUTCHours()).slice(-2) + ':' + ('0' + minusTime.getUTCMinutes()).slice(-2);
    } else {
        return "-";
    }
}

function minusTimeSet2(time1, time2) {
    var d1 = new Date(time1);
    var d2 = new Date(time2);
    var minusTime = new Date(d2.getTime() - d1.getTime());
    return ('0' + minusTime.getUTCHours()).slice(-2) + ':' + ('0' + minusTime.getUTCMinutes()).slice(-2) + ':' + ('0' + minusTime.getUTCSeconds()).slice(-2);
}

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

function minusTime(time1, time2) {
    var d1 = new Date(time1);
    var d2 = new Date(time2);
    var minusTime = d2.getTime() - d1.getTime();
    return minusTime;
}

/*function timepickerConfirm(time1 , time2, createdTime) {
    var d1 = new Date(time1);
    var d2 = new Date(time2);
    d1.setHours(0,0,0);
    d2.setHours(0,0,0);
    d2.setDate(d2.getDate()+1);
    var cT = new Date(createdTime);
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
            $('#passtime').html(minusTimeSet(data.createdDatetime, data.completedDatetime));
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
    var mydata = [];
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
            var i = 1;
            var orderPickupSum = 0;
            var pickupCompleteSum = 0;
            var orderCompleteSum = 0;
            var completeReturnSum = 0;
            var pickupReturnSum = 0;
            var orderReturnSum = 0;
            var distanceSum = 0;
            for (var key in data) {
                if (data.hasOwnProperty(key)) {
                    // if (timepickerConfirm($('#day1').val(), $('#day2').val(), data[key].createdDatetime)) {
                    var tmpdata = new Object();
                    tmpdata.No = i++;
                    tmpdata.reg_order_id = regOrderIdReduce(data[key].regOrderId);
                    tmpdata.id = data[key].id;
                    tmpdata.origin_reg_order_id = data[key].regOrderId;
                    tmpdata.orderDate = timeSetDate(data[key].createdDatetime);
                    tmpdata.orderPickup1 = minusTimeSet2(data[key].createdDatetime, data[key].pickedUpDatetime);
                    tmpdata.pickupComplete1 =  minusTimeSet2(data[key].pickedUpDatetime, data[key].completedDatetime);
                    tmpdata.orderComplete1 = minusTimeSet2(data[key].createdDatetime, data[key].completedDatetime);
                    tmpdata.completeReturn1 = minusTimeSet2(data[key].completedDatetime, data[key].returnDatetime);
                    tmpdata.pickupReturn1 = minusTimeSet2(data[key].pickedUpDatetime, data[key].returnDatetime);
                    tmpdata.orderReturn1 = minusTimeSet2(data[key].createdDatetime, data[key].returnDatetime);
                    tmpdata.distance = data[key].distance + 'km';
                    orderPickupSum += minusTime(data[key].createdDatetime, data[key].pickedUpDatetime);
                    pickupCompleteSum += minusTime(data[key].pickedUpDatetime, data[key].completedDatetime);
                    orderCompleteSum += minusTime(data[key].createdDatetime, data[key].completedDatetime);
                    completeReturnSum += minusTime(data[key].completedDatetime, data[key].returnDatetime);
                    pickupReturnSum += minusTime(data[key].pickedUpDatetime, data[key].returnDatetime);
                    orderReturnSum += minusTime(data[key].createdDatetime, data[key].returnDatetime);
                    distanceSum += parseFloat(data[key].distance);
                    mydata.push(tmpdata);
                }
            }
            var tmpdata = new Object();
            tmpdata.No = 10000000;
            tmpdata.reg_order_id = "TOTAL";
            tmpdata.id = "";
            tmpdata.origin_reg_order_id = "";
            tmpdata.orderDate = "";
            tmpdata.orderPickup1 = totalTimeSet(orderPickupSum);
            tmpdata.pickupComplete1 = totalTimeSet(pickupCompleteSum);
            tmpdata.orderComplete1 = totalTimeSet(orderCompleteSum);
            tmpdata.completeReturn1 = totalTimeSet(completeReturnSum);
            tmpdata.pickupReturn1 = totalTimeSet(pickupReturnSum);
            tmpdata.orderReturn1 = totalTimeSet(orderReturnSum);
            tmpdata.distance = distanceSum + 'km';
            mydata.push(tmpdata);
            if (mydata != null) {
                jQuery('#jqGrid').jqGrid('clearGridData')
                jQuery('#jqGrid').jqGrid('setGridParam', {data: mydata, page: 1})
                jQuery('#jqGrid').trigger('reloadGrid');
            }
            $("#jqGrid").jqGrid({
                datatype: "local",
                data: mydata,
                colModel: [
                    {label: 'No', name: 'No', width: 25, key: true, align: 'center', hidden: true},
                    {label: 'Order 號碼', name: 'reg_order_id', width: 80, align: 'center'},
                    {label: order_id, name: 'id', width: 80, align: 'center', hidden: true},
                    {label: order_reg_order_id, name: 'origin_reg_order_id', width: 80, align: 'center', hidden: true},
                    {label: '訂單日期', name: 'orderDate', width: 80, align: 'center'},
                    {label: '留店時間', name: 'orderPickup1', index: 'orderPickup1', width: 80, align: 'center'},
                    {label: '外送時間', name: 'pickupComplete1', index: 'pickupComplete1', width: 80, align: 'center'},
                    {label: '外送達成時間', name: 'orderComplete1', index: 'orderComplete1', width: 80, align: 'center'},
                    {label: '回店所需時間', name: 'completeReturn1', index: 'completeReturn1', width: 80, align: 'center'},
                    {label: '外出時間', name: 'pickupReturn1', index: 'pickupReturn1', width: 80, align: 'center'},
                    {label: '完成整張外送時間', name: 'orderReturn1', index: 'orderReturn1', width: 80, align: 'center'},
                    {label: '距離', name: 'distance', width: 80, align: 'center'},
                ],
                width: 'auto',
                height: 700,
                autowidth: true,
                rowNum: 20,
                footerrow: true,
                pager: "#jqGridPager",
                ondblClickRow: function (rowid, icol, cellcontent, e) {
                    var rowData = jQuery(this).getRowData(rowid);
                    var No = rowData['No'];
                    var regOrderId = rowData['origin_reg_order_id'];
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