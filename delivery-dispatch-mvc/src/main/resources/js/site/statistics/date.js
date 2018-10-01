let loading= $('<div id="loading"><div><p style="background-color: #838d96"/></div></div>').appendTo(document.body).hide();
$(function () {
    $('input[name="datepicker"]').change(function () {
        getStoreStatisticsByDate();
    })
    $('input[name=datepicker]').val($.datepicker.formatDate('yy-mm-dd', new Date));
    getStoreStatisticsByDate();
});


function totalTimeSet(time) {
    if (time) {
        if(time>=0){
            let d = new Date(time);
            return ('0' + d.getUTCHours()).slice(-2) + ':' + ('0' + d.getUTCMinutes()).slice(-2) + ':' + ('0' + d.getUTCSeconds()).slice(-2);
        }else{
            time = Math.abs(time);
            let d = new Date(time);
            return "-"+('0' + d.getUTCHours()).slice(-2) + ':' + ('0' + d.getUTCMinutes()).slice(-2) + ':' + ('0' + d.getUTCSeconds()).slice(-2);
        }

    } else {
        return "-";
    }
}

function getStoreStatisticsByDate() {
    let mydata = [];
    loading.show();
    $.ajax({
        url: "/getStoreStatisticsByDate",
        type: 'get',
        data: {
            startDate: $('#day1').val(),
            endDate: $('#day2').val()
        },
        dataType: 'json',
        success: function (data) {
            // 평균 값
            let orderPickupSum = 0;
            let pickupCompleteSum = 0;
            let orderCompleteSum = 0;
            let completeReturnSum = 0;
            let pickupReturnSum = 0;
            let orderReturnSum = 0;
            let min30BelowSum = 0;
            let min30To40Sum = 0;
            let min40To50Sum = 0;
            let min50To60Sum = 0;
            let min60To90Sum = 0;
            let min90UnderSum = 0;
            let totalSalesSum = 0;
            let tcSum = 0;
            let tplhSum = 0;
            let spmhSum = 0;
            let totalPickupReturnSum = 0;
            let avgDistanceSum = 0;
            // row 갯수
            let rowCnt = 0;
            // 빈값이 들어간 row 갯수
            let rowReduceCnt = 0;
            // 거리값 제외 count
            let distanceCnt = 0;
            // tpsp 제외 count
            let tpSpCnt = 0;

            for (let key in data) {
                if (data.hasOwnProperty(key)) {
                    // 빈값 여부를 위해 chkcount
                    let chkCnt = 0;
                    let chkDistanceCnt = 0;
                    let chkTpSpCnt = 0;

                    let tmpdata = new Object();
                    rowCnt++;
                    rowReduceCnt++;
                    distanceCnt++;
                    tpSpCnt++;
                    tmpdata.store = my_store.storeName;
                    tmpdata.day = data[key].dayToDay;
                    tmpdata.orderPickup = totalTimeSet(data[key].orderPickup*1000);
                    tmpdata.pickupComplete = totalTimeSet(data[key].pickupComplete*1000);
                    tmpdata.orderComplete = totalTimeSet(data[key].orderComplete*1000);
                    tmpdata.completeReturn = totalTimeSet(data[key].completeReturn*1000);
                    tmpdata.pickupReturn = totalTimeSet(data[key].pickupReturn*1000);
                    tmpdata.orderReturn = totalTimeSet(data[key].orderReturn*1000);
                    tmpdata.min30Below = parseInt(data[key].min30Below) + "%";
                    tmpdata.min30To40 = parseInt(data[key].min30To40) + "%";
                    tmpdata.min40To50 = parseInt(data[key].min40To50) + "%";
                    tmpdata.min50To60 = parseInt(data[key].min50To60) + "%";
                    tmpdata.min60To90 = parseInt(data[key].min60To90) + "%";
                    tmpdata.min90Under = parseInt(data[key].min90Under) + "%";
                    tmpdata.totalSales = data[key].totalSales;
                    tmpdata.tc = data[key].tc;

                    if(data[key].tplh){
                        tmpdata.tplh = parseFloat(data[key].tplh).toFixed(2);
                        tplhSum += parseFloat(data[key].tplh);
                    } else{
                        tmpdata.tplh = "-";
                        chkTpSpCnt++;
                    }

                    if(data[key].spmh){
                        tmpdata.spmh = parseFloat(data[key].spmh).toFixed(2);
                        spmhSum += parseFloat(data[key].spmh);
                    } else{
                        tmpdata.spmh = "-";
                        chkTpSpCnt++;
                    }
                    tmpdata.totalPickupReturn = totalTimeSet(data[key].totalPickupReturn*1000);
                    tmpdata.avgDistance = (data[key].avgDistance?parseFloat(data[key].avgDistance).toFixed(2):0) +'km';

                    // 평균 값
                    orderPickupSum += parseFloat(data[key].orderPickup);
                    pickupCompleteSum += parseFloat(data[key].pickupComplete);
                    orderCompleteSum += parseFloat(data[key].orderComplete);

                    if(tmpdata.completeReturn != "-"){
                        completeReturnSum += parseFloat(data[key].completeReturn);
                    } else{
                        chkCnt++;
                    }

                    if(tmpdata.pickupReturn != "-"){
                        pickupReturnSum += parseFloat(data[key].pickupReturn);
                    } else{
                        chkCnt++;
                    }

                    if(tmpdata.orderReturn != "-"){
                        orderReturnSum += parseFloat(data[key].orderReturn);
                    } else{
                        chkCnt++;
                    }

                    if(tmpdata.totalPickupReturn != "-"){
                        totalPickupReturnSum += parseFloat(data[key].totalPickupReturn);
                    } else{
                        chkCnt++;
                    }

                    if(tmpdata.avgDistance != "-"){
                        avgDistanceSum += parseFloat(data[key].avgDistance?data[key].avgDistance:0);
                    } else{
                        chkDistanceCnt++;
                    }
                    min30BelowSum += parseInt(data[key].min30Below);
                    min30To40Sum += parseInt(data[key].min30To40);
                    min40To50Sum += parseInt(data[key].min40To50);
                    min50To60Sum += parseInt(data[key].min50To60);
                    min60To90Sum += parseInt(data[key].min60To90);
                    min90UnderSum += parseInt(data[key].min90Under);
                    totalSalesSum += parseInt(data[key].totalSales);
                    tcSum += parseInt(data[key].tc);
                    mydata.push(tmpdata);
                    if(chkCnt !=0){
                        rowReduceCnt--;
                    }
                    if(chkDistanceCnt !=0){
                        distanceCnt--;
                    }
                    if(chkTpSpCnt !=0){
                        tpSpCnt--;
                    }

                }
            }
            // 평균 값
            let avgData = new Object();
            avgData.store = "Average" ;
            avgData.day = "";
            avgData.orderPickup = totalTimeSet((orderPickupSum*1000)/rowCnt);
            avgData.pickupComplete = totalTimeSet((pickupCompleteSum*1000)/rowCnt);
            avgData.orderComplete  = totalTimeSet((orderCompleteSum*1000)/rowCnt);
            avgData.completeReturn = totalTimeSet((completeReturnSum*1000)/rowReduceCnt);
            avgData.pickupReturn =  totalTimeSet((pickupReturnSum*1000)/rowReduceCnt);
            avgData.orderReturn =   totalTimeSet((orderReturnSum*1000)/rowReduceCnt);
            avgData.min30Below = (min30BelowSum/rowCnt).toFixed(2) +"%";
            avgData.min30To40 = (min30To40Sum/rowCnt).toFixed(2) +"%";
            avgData.min40To50 = (min40To50Sum/rowCnt).toFixed(2) +"%";
            avgData.min50To60 = (min50To60Sum/rowCnt).toFixed(2) +"%";
            avgData.min60To90 = (min60To90Sum/rowCnt).toFixed(2) +"%";
            avgData.min90Under = (min90UnderSum/rowCnt).toFixed(2) +"%";
            avgData.totalSales = (totalSalesSum/rowCnt).toFixed(2);
            avgData.tc = (tcSum/rowCnt).toFixed(2);
            avgData.tplh = (tplhSum/tpSpCnt).toFixed(2);
            avgData.spmh = (spmhSum/tpSpCnt).toFixed(2);
            avgData.totalPickupReturn = totalTimeSet((totalPickupReturnSum*1000)/rowReduceCnt);
            avgData.avgDistance = (avgDistanceSum/distanceCnt).toFixed(2) +'km';

            // 날짜 조회시 avg 노출
            if(rowCnt!=0){
                mydata.push(avgData);
            }

            if (mydata != null) {
                jQuery('#jqGrid').jqGrid('clearGridData');
                jQuery('#jqGrid').jqGrid('setGridParam', {data: mydata, page: 1});
                jQuery('#jqGrid').trigger('reloadGrid');
            }
            $("#jqGrid").jqGrid({
                datatype: "local",
                data: mydata,
                colModel: [
                    {label: label_store, name: 'store', width: 80, align: 'center'},
                    {label: label_date, name: 'day', width: 80, align: 'center'},
                    {label: label_in_store_time, name: 'orderPickup', index: 'orderPickup', width: 80, align: 'center'},
                    {label: label_delivery_time, name: 'pickupComplete', index: 'pickupComplete', width: 80, align: 'center'},
                    {label: label_completed_time, name: 'orderComplete', index: 'orderComplete', width: 80, align: 'center'},
                    {label: label_return_time, name: 'completeReturn', index: 'completeReturn', width: 80, align: 'center'},
                    {label: label_out_time, name: 'pickupReturn', index: 'pickupReturn', width: 80, align: 'center'},
                    {label: label_total_delivery_time, name: 'orderReturn', index: 'orderReturn', width: 80, align: 'center'},
                    {label: '<=30 MINS %', name: 'min30Below', index: 'min30Below', width: 80, align: 'center'},
                    {label: '<=40 MINS %', name: 'min30To40', index: 'min30To40', width: 80, align: 'center' , hidden: regionLocale.country == "TW"?true:false},
                    {label: '<=50 MINS %', name: 'min40To50', index: 'min40To50', width: 80, align: 'center' , hidden: regionLocale.country == "TW"?true:false},
                    {label: '<=60 MINS %', name: 'min50To60', index: 'min50To60', width: 80, align: 'center' , hidden: regionLocale.country == "TW"?true:false},
                    {label: '<=90 MINS %', name: 'min60To90', index: 'min60To90', width: 80, align: 'center' , hidden: regionLocale.country == "TW"?true:false},
                    {label: '>90 MINS %', name: 'min90Under', index: 'min90Under', width: 80, align: 'center' , hidden: regionLocale.country == "TW"?true:false},
                    {label: label_sales, name: 'totalSales', index: 'totalSales', width: 80, align: 'center' , hidden: regionLocale.country == "TW"?true:false},
                    {label: label_tc, name: 'tc', index: 'tc', width: 50, align: 'center'},
                    {label: label_tplh, name: 'tplh', index: 'tplh', width: 80, align: 'center'},
                    {label: label_spmh, name: 'spmh', index: 'spmh', width: 80, align: 'center' , hidden: regionLocale.country == "TW"?true:false},
                    {label: label_total_time, name: 'totalPickupReturn', index: 'totalPickupReturn', width: 80, align: 'center'},
                    {label: label_average_distance, name: 'avgDistance', width: 80, align: 'center'},
                ],
                width: '100%',
                height: 660,
                autowidth: true,
                rowNum: 20,
                // footerrow: true,
                pager: "#jqGridPager",
            });

            $("#jqGrid").jqGrid('destroyGroupHeader');
            $("#jqGrid").jqGrid('setGroupHeaders', {
                useColSpanStyle: true,
                groupHeaders:[
                    {startColumnName: 'orderPickup', numberOfColumns: 6, titleText: label_average_time},
                    {startColumnName: 'min30Below', numberOfColumns: 6, titleText: label_percent_completed},
                    {startColumnName: 'totalSales', numberOfColumns: 6, titleText: label_productivity}
                ]
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

function excelDownloadByDate(){
    let startDate = $('#day1').val();
    let endDate = $('#day2').val();

    // let storeName = $('#myStoreName').text();
    //
    //
    // console.log("!!!!!!!!!!!!");
    // console.log(storeName);

    loading.show();
    $.fileDownload("/excelDownloadByDate",{
        httpMethod:"GET",
        data : {
            startDate : startDate,
            endDate : endDate
        },
        successCallback: function(url){
            loading.hide();
        },
        failCallback: function(responseHtml,url){
            // console.log(responseHtml);
            loading.hide();
        }
    })
}