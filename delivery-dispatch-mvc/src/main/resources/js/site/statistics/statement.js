function footerRiders() {
    if(footerRiderList[2]){
        $('#rest').text(parseInt(footerRiderList[2].workCount) + parseInt(footerRiderList[2].orderCount));//휴식
    }else {
        $('#rest').text('0');
    }
    if(footerRiderList[1]){
        $('#standby').text(parseInt(footerRiderList[1].workCount) - parseInt(footerRiderList[1].orderCount));// 대기
        $('#work').text(footerRiderList[1].orderCount);//근무
    }else {
        $('#standby').text('0');
        $('#work').text('0');
    }
}
function footerOrders() {
    if(footerOrderList[0]) {
        if (footerOrderList[5]) {
            $('#new').text(parseInt(footerOrderList[0].count)+parseInt(footerOrderList[5].count));
        } else {
            $('#new').text(parseInt(footerOrderList[0].count));
        }
    } else if(footerOrderList[5]){
        $('#new').text(parseInt(footerOrderList[5].count));
    } else {
        $('#new').text('0');
    }

    if(footerOrderList[1]){
        $('#assigned').text(parseInt(footerOrderList[1].count));
    }else {
        $('#assigned').text('0');
    }

    if(footerOrderList[3]){
        $('#completed').text(parseInt(footerOrderList[3].count));
    }else {
        $('#completed').text('0');
    }

    if(footerOrderList[4]){
        $('#canceled').text(parseInt(footerOrderList[4].count));
    }else{
        $('#canceled').text('0')
    }
}
$(function() {
    footerRiders();
    footerOrders();
    $('input[type="checkbox"]').on('click',function() {
        getStoreStatistics();
    });
    $('input[name="datepicker"]').change(function () {
        getStoreStatistics();
    })
    $("#allChk").click(function () {
        if(this.checked){
            $("input[name=srchChk]:checkbox").each(function() {
                $(this).attr("checked", true);
                $(this).attr("disabled", true);
            });
        }else{
            $("input[name=srchChk]:checkbox").each(function() {
                $(this).attr("checked", false);
                $(this).attr("disabled", false);
            });
        }
    });
    $('input[name=datepicker]').val($.datepicker.formatDate('yy-mm-dd',new Date));
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
                field : 'text',
                op : "cn",
                data : searchText
            });
            filter.rules.push({
                field : 'pay',
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
    getStoreStatistics();
});

function timeSet(time) {
    if(time != null){
        var d = new Date(time);
        return $.datepicker.formatDate('mm-dd ', d) + ('0' + d.getHours()).slice(-2) + ':' + ('0' + d.getMinutes()).slice(-2);
    }else{
        return "-";
    }
}

function timeSet2(time) {
    if(time != null){
        var d = new Date(time);
        return $.datepicker.formatDate('mm/dd ', d) + ('0' + d.getHours()).slice(-2) + ':' + ('0' + d.getMinutes()).slice(-2);
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
    var minusTime = d2.getTime()-d1.getTime();
    return minusTime;
}
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
function getStatisticsInfo(orderId) {
    var regOrderId
    $.ajax({
        url : "/getStatisticsInfo",
        type : 'get',
        data : {
            id : orderId
        },
        dataType : 'json',
        success : function (data) {
            console.log(data);
            if (data.status == 3) {
                $status = '<i class="ic_txt">' + status_completed + '</i>';
            }
            else {
                $status = '<i class="ic_txt ic_red">' + status_canceled + '</i>';
            }
            if(data.regOrderId){
                regOrderId = data.regOrderId;
            }else{
                regOrderId = "-";
            }
            $('.tit').html('<h2>'+order_detail + ' - '+ data.id + '('+ regOrderId +')</h2>'+$status);
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
            }


            $('#paid').html($paid);

            if(data.combinedOrderId != null){
                $('#combinedOrder').html(data.combinedOrderId);
            }

            if(data.riderId){
                $('#riderName').html(data.rider.name);
                $('#riderPhone').html(data.rider.phone);
            }else {
                $('#riderName').html("-");
                $('#riderPhone').html("-");
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

function getStoreStatistics() {
    var mydata = [];

    $.ajax({
        url: "/getStoreStatistics",
        type: 'get',
        data: {
        },
        dataType: 'json',
        success: function (data) {
            var i = 1;
            console.log(data);
            for (var key in data) {
                if (data.hasOwnProperty(key)) {
                    if (timepickerConfirm($('#day1').val(), $('#day2').val(), data[key].createdDatetime)) {
                        var tmpdata = new Object();
                        if (data[key].status == 3) {
                            if (!$('#completeChk').prop('checked')) {
                                continue;
                            }
                            $status = '<i class="ic_txt">' + status_completed + '</i>';
                        }
                        else {
                            if (!$('#canceledChk').prop('checked')) {
                                canceledChk
                                continue;
                            }
                            $status = '<i class="ic_txt ic_red">' + status_canceled + '</i>';

                        }


                        if (data[key].payment) {
                            if (data[key].payment.type == "0") {
                                $toBePaid = order_payment_card;
                            } else if (data[key].payment.type == "1") {
                                $toBePaid = order_payment_cash;
                            } else {
                                $toBePaid =  "-";
                            }
                        } else{
                            $toBePaid =  "-";
                        }
                        tmpdata.pay = $toBePaid;

                        tmpdata.No = i;
                        i++;
                        tmpdata.state = $status;
                        tmpdata.id = data[key].id;
                        if (data[key].createdDatetime) {
                            tmpdata.time1 = timeSet(data[key].createdDatetime);
                        }
                        tmpdata.address = data[key].address;

                        tmpdata.reg_order_id = data[key].regOrderId;
                        if (data[key].cookingTime) {
                            tmpdata.time2 = data[key].cookingTime;
                        }


                        if (!data[key].assignedDatetime) {
                            tmpdata.time3 = "-";
                        } else {
                            tmpdata.time3 = timeSet(data[key].assignedDatetime);
                        }
                        if (!data[key].pickedUpDatetime) {
                            tmpdata.time4 = "-";
                        } else {
                            tmpdata.time4 = timeSet(data[key].pickedUpDatetime);
                        }
                        if (!data[key].reservationDatetime) {
                            tmpdata.time5 = "-";
                        } else {
                            tmpdata.time5 = timeSet(data[key].reservationDatetime);
                        }
                        if (!data[key].rider) {
                            tmpdata.rider = "-";
                        } else {
                            tmpdata.rider = data[key].rider.name;
                        }
                        if ($("input[name=myStoreChk]:checkbox").prop("checked")) {
                            if (data[key].storeId == storeId) {
                                mydata.push(tmpdata);
                            }
                        } else {
                            mydata.push(tmpdata);
                        }
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
                colModel:[
                    {label:'No', name:'No', width:25, key:true, align:'center'},
                    {label:order_status, name:'state', width:80, align:'center'},
                    {label:order_id, name:'id', width:80, align:'center'},
                    {label:order_created, name:'time1', width:80, align:'center'},
                    {label:order_address, name:'address', width:200},
                    {label:order_reg_order_id, name:'reg_order_id', width:150},
                    {label:order_cooking, name:'time2', width:80, align:'center'},
                    {label:order_payment, name:'pay', width:80, align:'center'},
                    {label:order_assigned, name:'time3', width:80, align:'center'},
                    {label:order_pickedup, name:'time4', width:80, align:'center'},
                    {label:order_reserved, name:'time5', width:80, align:'center'},
                    {label:rider_name, name:'rider', width:80, align:'center'}
                ],
                width:'auto',
                height:700,
                autowidth:true,
                rowNum:20,
                pager:"#jqGridPager",
                ondblClickRow: function(rowid,icol,cellcontent,e){
                    var rowData = jQuery(this).getRowData(rowid);
                    var orderId = rowData['id'];
                    getStatisticsInfo(orderId);
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