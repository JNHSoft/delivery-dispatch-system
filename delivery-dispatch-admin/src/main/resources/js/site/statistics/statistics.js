/*<![CDATA[*/
var loading= $('<div id="loading"><div><p style="background-color: #838d96"/></div></div>').appendTo(document.body).hide();
$(function () {
    let date = $.datepicker.formatDate('yy-mm-dd', new Date);
    $('#startDate, #endDate').val(date);

    getStatisticsList();
    getGroupList();

    $('#startDate').datepicker({
        maxDate : date,
        onClose: function(selectedDate) {
            $('#endDate').datepicker('option', 'minDate', selectedDate);
            getStatisticsList();
        }
    });

    $('#endDate').datepicker({
        minDate : date,
        onClose: function( selectedDate ) {
            $('#startDate').datepicker('option', 'maxDate', selectedDate);
            getStatisticsList();
        }
    });

    $(".select").change(function () {
        selectId = $(this);
        selectIdOption = $('option:selected',this);
        getStatisticsList();
        searchList(selectId, selectIdOption);
    });

    $("#searchButton").click(function () {
        getStatisticsList();
        searchList(selectId, selectIdOption);
    });
});

var selectId =$("#statisticsStoreList");
var selectIdOption = $("#statisticsStoreList option:selected");
function searchList(selectId, selectIdOption) {
    console.log("serchList");

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
            field : 'th5',
            op : "eq",
            data : searchText
        });
    }else if(select == 'all'){
        filter2.rules.push({
            field : 'th5',
            op : "eq",
            data : searchText
        });
        filter2.rules.push({
            field : 'th10',
            op : "cn",
            data : searchText
        });
        filter2.rules.push({
            field : 'th14',
            op : "cn",
            data : searchText
        });
    }else if (select == 'pay'){
        filter2.rules.push({
            field : 'th10',
            op : "cn",
            data : searchText
        });
    }else if (select == 'rider'){
        filter2.rules.push({
            field : 'th14',
            op : "cn",
            data : searchText
        });
    }

    var searchText1= $("#statisticsGroupList option:selected").text();
    var searchTextVal1= $("#statisticsGroupList option:selected").val();
    var searchText2= $("#statisticsSubGroupList option:selected").text();
    var searchTextVal2= $("#statisticsSubGroupList option:selected").val();
    var searchText3= $("#statisticsStoreList option:selected").text();
    var searchTextVal3= $("#statisticsStoreList option:selected").val();

    var filter = {
        groupOp: "AND",
        rules: [],
        groups : [filter2]
    };
    if(searchTextVal1 != "reset"){
        filter.rules.push({
            field : 'th1',
            op : "eq",
            data : searchText1
        });
        if(searchTextVal2 != "reset"){
            filter.rules.push({
                field : 'th2',
                op : "eq",
                data : searchText2
            });
            if(searchTextVal3 != "reset"){
                filter.rules.push({
                    field : 'th3',
                    op : "eq",
                    data : searchText3
                });
            }
        }
    }
    var grid = jQuery('#jqGrid');
    if(filter.rules.length > 0 || filter2.rules.length >0){
        grid[0].p.search = true;
    }
    $.extend(grid[0].p.postData, { filters: filter });
    grid.trigger("reloadGrid", [{ page: 1 }]);
}

/**
 * ?????? List ????????????
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
 * ?????? ?????? List ????????????
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
 * ?????? List ????????????
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
            subGroupId : selecSubGroupId
        },
        async : false,
        dataType : 'json',
        success : function(data){
            if(data) {
                var statisticsStoreListHtml = "<option value='reset'>" + list_search_all_store + "</option>";
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
 * ????????????
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
 * ???????????? ??????
 */
function minusTimeSet(time1 , time2) {
    if(time2 != null){
        var d1 = new Date(time1);
        var d2 = new Date(time2);
        var minusTime = new Date(d2.getTime()-d1.getTime());
        return ('0' + minusTime.getUTCHours()).slice(-2) + ':' + ('0' + minusTime.getUTCMinutes()).slice(-2)
    }else{
        return "-";
    }
}

/**
 * ?????????????????? ??????????????? ??????
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
 * ?????? ?????? 
 * ?????? ????????? ???????????? ????????? ?????????
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
 * map ?????? 
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
 * ?????? ?????? List
 */
function getStatisticsList() {

    let diffDate = Math.ceil((new Date($('#endDate').val()).getTime() - new Date($('#startDate').val()).getTime()) / (1000*3600*24));

    if (diffDate > 31){
        return;
    }

    var $mydata = [];
    loading.show();
    $.ajax({
        url: "/getStatisticsList",
        type: 'get',
        data: {
            startDate: $('#startDate').val(),
            endDate: $('#endDate').val()
        },
        dataType: 'json',
        success: function (data) {
            console.log("getStatisticsList");
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
                    if (data.hasOwnProperty(key)) {

                        $tmpData.th0 = i++;
                        if(data[key].group){
                            $tmpData.th1 = data[key].group.name
                        }else {
                            $tmpData.th1 = group_none;
                        }
                        if(data[key].subGroup){
                            $tmpData.th2 = data[key].subGroup.name
                        }else{
                            $tmpData.th2 = group_none;
                        }
                        $tmpData.th3 = data[key].store.storeName
                        if (data[key].status == "3") {
                            $tmpData.th4 = status_completed
                        } else if (data[key].status == "4") {
                            $tmpData.th4 = status_canceled
                        }
                        $tmpData.th5 = regOrderIdReduce(data[key].regOrderId);
                        $tmpData.origin_reg_order_id = data[key].regOrderId;
                        $tmpData.th6 = timeSet(data[key].createdDatetime);
                        $tmpData.th7 = data[key].address
                        // $tmpData.th8 = data[key].menuName
                        $tmpData.th9 = data[key].cookingTime

                        if (data[key].paid != null) {
                            if (data[key].paid == "0") {
                                $tmpData.th10 = order_payment_cash;
                            } else if (data[key].paid == "1") {
                                $tmpData.th10 = order_payment_card;
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

                        // 20.07.15 arrived datetime ??????
                        if (!data[key].arrivedDatetime){
                            $tmpData.th18 = "-";
                        }else{
                            $tmpData.th18 = timeSet(data[key].arrivedDatetime);
                        }

                        if(!data[key].reservationDatetime){
                            $tmpData.th13 = "-";
                        }else if(data[key].reservationStatus==1){
                            $tmpData.th13 = timeSet(data[key].reservationDatetime);
                        }else if(data[key].reservationStatus==0){
                            if(map_region){
                                if(map_region=="tw"){
                                    $tmpData.th13 = '<span style="color: red">' + timeSet(data[key].reservationDatetime) + '</span>';
                                }else{
                                    $tmpData.th13 = "-";
                                }
                            }else{
                                $tmpData.th13 = "-";
                            }
                        }

                        $tmpData.th14 = data[key].rider.name

                        /*if(data[key].group != null){
                            $tmpData.th15 = data[key].group.id

                        }
                        if(data[key].subGroup != null) {
                            $tmpData.th16 = data[key].subGroup.id
                        }

                        if(data[key].storeId != null) {
                            $tmpData.th17 = data[key].storeId
                        }*/
                        if($('#selectStatus option:selected').val()=="all"){
                            $mydata.push($tmpData);
                        }else if ($('#selectStatus option:selected').val()=="complete" && data[key].status =="3"){
                            $mydata.push($tmpData);
                        }else if ($('#selectStatus option:selected').val()=="cancel" && data[key].status =="4"){
                            $mydata.push($tmpData);
                        }
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
                    {label: order_reg_order_id, name: 'th5', width: 80, align: 'center'},
                    {label: order_reg_order_id, name: 'origin_reg_order_id', width: 80, align: 'center', hidden:true},
                    {label: group_name, name: 'th1', width: 80, align: 'center'},
                    {label: subGroup_name, name: 'th2', width: 80, align: 'center'},
                    {label: store_name, name: 'th3', width: 120, align: 'center'},
                    {label: order_status, name: 'th4', width: 80, align: 'center'},
                    {label: order_created, name: 'th6', width: 80, align: 'center'},
                    {label: order_address, name: 'th7', width: 200},
                    /*{label: order_summary, name: 'th8', width: 80},*/
                    {label: order_cooking, name: 'th9', width: 80, align: 'center'},
                    {label: order_payment, name: 'th10', width: 80, align: 'center'},
                    {label: order_assigned, name: 'th11', width: 80, align: 'center'},
                    {label: order_pickedup, name: 'th12', width: 80, align: 'center'},
                    {label: order_arrived, name: 'th18', width: 80, align: 'center'},
                    {label: order_reserved, name: 'th13', width: 80, align: 'center'},
                    {label: rider_name, name: 'th14', width: 80, align: 'center'}/*,
                    {label:'??????ID', name:'th15', width:60, hidden:'hidden'},
                    {label:'????????????ID', name:'th16', width:60, hidden:'hidden'},
                    {label:'??????ID', name:'th17', width:60, hidden:'hidden'}*/
                ],
                height: 680,
                autowidth: true,
                rowNum: 20,
                pager: "#jqGridPager",
                ondblClickRow: function(rowid,icol,cellcontent,e){
                    var rowData = jQuery(this).getRowData(rowid);
                    var orderId = rowData['origin_reg_order_id'];
                    getStatisticsInfo(orderId);
                    $('.state_wrap').addClass('on'); //???????????? ??????
                    setTimeout(function(){
                        $(window).trigger('resize');
                    },300)//????????? ????????????
                }
            });

            resizeJqGrid('#jqGrid'); //????????? ????????????
            loading.hide();

            $('.state_wrap .btn_close').click(function (e) {
                e.preventDefault();
                $('.state_wrap').removeClass('on') //???????????? ??????
                setTimeout(function () {
                    $(window).trigger('resize');
                }, 300)//????????? ????????????
            })
        }
    }
)};


/**
 * ?????? ????????????
 */
function getStatisticsInfo(orderId) {

    var $paid = "";
    var $status = "";

    $.ajax({
        url : "/getStatisticsInfo",
        type : 'get',
        data : {
            regOrderId : orderId
        },
        dataType : 'json',
        success : function (data) {
            if (data.status == "3") {
                $status = '<i class="ic_txt">' + status_completed + '</i>';
            }
            else {
                $status = '<i class="ic_txt ic_red">' + status_canceled + '</i>';
            }
            $('.tit').html('<h2>' +order_detail + ' - '+ regOrderIdReduce(data.regOrderId) + '</h2>'+$status);

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

            if (data.paid) {
                if (data.paid == "0") {
                    $paid = order_payment_cash;
                } else if (data.paid == "1") {
                    $paid = order_payment_card;
                } else if (data.paid == "2") {
                    $paid = order_payment_prepayment;
                } else if (data.paid == "3"){
                    $paid = order_payment_service;
                }
                $('#paid').html($paid);
            }
            /*if(data.payment) {
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
            }*/

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



function excelDownload(){
    // console.log("@@@@");
    // $('input[name=startDate]').val($('#startDate').val());
    // $('input[name=endDate]').val($('#endDate').val());

    // document.searchForm.action="/excelDownload";
    // document.searchForm.method="GET";
    // document.searchForm.submit();

    let startDate = $('#startDate').val();
    let endDate = $('#endDate').val();

    let diffDate = Math.ceil((new Date(endDate).getTime() - new Date(startDate).getTime()) / (1000*3600*24));

    if (diffDate > 31){
        return;
    }


    loading.show();

    /*jQuery('<form action="'+ "/excelDownload" +'" method="'+ 'get' +'">'
        + '<input type="hidden" name="'+ 'startDate' +'" value="'+ startDate +'" />'
        + '<input type="hidden" name="'+ 'endDate' +'" value="'+ endDate +'" />'
        + '</form>').appendTo('body').submit().remove();

    FILEDOWNLOAD_INTERVAL = setInterval(function() {
            if ($.cookie("fileDownloadToken")) {
                $.removeCookie('fileDownloadToken');
                clearInterval(FILEDOWNLOAD_INTERVAL);
                loading.hide();
                console.log('hide');
            }
        }, 500);*/

    $.fileDownload("/excelDownload",{
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

    /*jQuery.download = function(url, data, method){
        // url??? data??? ????????????
        if( url && data ){
            // data ???  string ?????? array/object ??? ??????????????? ?????????.
            data = typeof data == 'string' ? data : jQuery.param(data);
            // ??????????????? form???  input?????? ?????????.
            var inputs = '';
            jQuery.each(data.split('&'), function(){
                var pair = this.split('=');
                inputs+='<input type="hidden" name="'+ pair[0] +'" value="'+ pair[1] +'" />';
            });
            // request??? ?????????.
            jQuery('<form action="'+ url +'" method="'+ (method||'post') +'">'+inputs+'</form>')
                .appendTo('body').submit().remove();
        };
    };

    let url = '/excelDownload';
    let params = {startDate: startDate, endDate: endDate};

    $.download(url, params, 'get');*/

}

/*]]>*/


