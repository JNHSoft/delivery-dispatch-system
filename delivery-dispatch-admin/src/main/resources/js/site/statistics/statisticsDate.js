let loading= $('<div id="loading"><div><p style="background-color: #838d96"/></div></div>').appendTo(document.body).hide();
var selectId =$("#statisticsStoreList");
var selectIdOption = $("#statisticsStoreList option:selected");


$(function () {
    let date = $.datepicker.formatDate('yy-mm-dd', new Date);
    $('#startDate, #endDate').val(date);
    getGroupList();                 // 그룹 정보 조회
    getStoreStatisticsByDate();

    $('#startDate').datepicker({
        maxDate : date,
        onClose: function(selectedDate) {
            $('#endDate').datepicker('option', 'minDate', selectedDate);
            getStoreStatisticsByDate();
        }
    });

    $('#endDate').datepicker({
        minDate : date,
        onClose: function( selectedDate ) {
            $('#startDate').datepicker('option', 'maxDate', selectedDate);
            getStoreStatisticsByDate();
        }
    });

    $(".select").change(function(){
        selectId = $(this);
        selectIdOption = $('option:selected', this);
        getStoreStatisticsByDate();
        searchList(selectId, selectIdOption);
    });     //select box의 change 이벤트
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

    let diffDate = Math.ceil((new Date($('#endDate').val()).getTime() - new Date($('#startDate').val()).getTime()) / (1000*3600*24));
    if (diffDate > 31){
        return;
    }


    let mydata = [];
    loading.show();
    $.ajax({
        url: "/getStoreStatisticsByDate",
        type: 'get',
        data: {
            startDate: $('#startDate').val(),
            endDate: $('#endDate').val()
        },
        dataType: 'json',
        success: function (data) {

            // 평균 값
            let orderPickupSum = 0;
            let pickupCompleteSum = 0;
            let riderStayTimeSum = 0;       // 20.07.15 Stay Time 추가
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

            let errtcSum = 0;
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

                    tmpdata.store = data[key].storeName;
                    tmpdata.day = data[key].dayToDay;

                    if (data[key].groupName == ''){
                        tmpdata.group_name = group_none;
                    }else{
                        tmpdata.group_name = data[key].groupName;
                    }

                    if (data[key].subGroup_name == ''){
                        tmpdata.subGroup_name = group_none;
                    }else{
                        tmpdata.subGroup_name = data[key].subGroupName;
                    }

                    tmpdata.orderPickup = totalTimeSet(data[key].orderPickup*1000);
                    tmpdata.pickupComplete = totalTimeSet(data[key].pickupComplete*1000);
                    tmpdata.orderComplete = totalTimeSet(data[key].orderComplete*1000);
                    // 20.07.15 Stay Time
                    tmpdata.riderStayTime = totalTimeSet(data[key].stayTime*1000);
                    tmpdata.completeReturn = totalTimeSet(data[key].completeReturn*1000);
                    tmpdata.pickupReturn = totalTimeSet(data[key].pickupReturn*1000);
                    tmpdata.orderReturn = totalTimeSet(data[key].orderReturn*1000);
                    tmpdata.min30Below = formatFloat(data[key].min30Below, 1) + "%";
                    tmpdata.min30To40 = formatInt(data[key].min30To40, 1) + "%";
                    tmpdata.min40To50 = formatInt(data[key].min40To50, 1) + "%";
                    tmpdata.min50To60 = formatInt(data[key].min50To60, 1) + "%";
                    tmpdata.min60To90 = formatInt(data[key].min60To90, 1) + "%";
                    tmpdata.min90Under = formatInt(data[key].min90Under, 1) + "%";
                    tmpdata.totalSales = formatInt(data[key].totalSales, 1);

                    if (data[key].errtc){
                        tmpdata.errtc = data[key].errtc;
                        //20.07.15 err tc 추가
                        errtcSum += formatFloat(data[key].errtc, 1);
                    }else{
                        tmpdata.errtc = "-";
                    }

                    tmpdata.tc = formatInt(data[key].tc, 1);

                    if(data[key].tplh){
                        tmpdata.tplh = formatFloat(data[key].tplh, 1);
                        tplhSum += formatFloat(data[key].tplh, 1);
                    } else{
                        tmpdata.tplh = "-";
                        chkTpSpCnt++;
                    }

                    if(data[key].spmh){
                        tmpdata.spmh = formatFloat(data[key].spmh, 1);
                        spmhSum += formatFloat(data[key].spmh, 1);
                    } else{
                        tmpdata.spmh = "-";
                        chkTpSpCnt++;
                    }
                    tmpdata.totalPickupReturn = totalTimeSet(data[key].totalPickupReturn*1000);
                    tmpdata.avgDistance = (data[key].avgDistance?formatFloat(data[key].avgDistance, 1):0) +'km';

                    // 평균 값
                    orderPickupSum += formatFloat(data[key].orderPickup, 1);
                    riderStayTimeSum += parseFloat(data[key].stayTime);     // 20.07.15 Stay Time
                    pickupCompleteSum += formatFloat(data[key].pickupComplete, 1);
                    orderCompleteSum += formatFloat(data[key].orderComplete, 1);

                    if(tmpdata.completeReturn != "-"){
                        completeReturnSum += formatFloat(data[key].completeReturn, 1);
                    } else{
                        chkCnt++;
                    }

                    if(tmpdata.pickupReturn != "-"){
                        pickupReturnSum += formatFloat(data[key].pickupReturn, 1);
                    } else{
                        chkCnt++;
                    }

                    if(tmpdata.orderReturn != "-"){
                        orderReturnSum += formatFloat(data[key].orderReturn, 1);
                    } else{
                        chkCnt++;
                    }

                    if(tmpdata.totalPickupReturn != "-"){
                        totalPickupReturnSum += formatFloat(data[key].totalPickupReturn, 1);
                    } else{
                        chkCnt++;
                    }

                    if(tmpdata.avgDistance != "-"){
                        avgDistanceSum += formatFloat(data[key].avgDistance?data[key].avgDistance:0, 1);
                    } else{
                        chkDistanceCnt++;
                    }

                    min30BelowSum += formatFloat(data[key].min30Below, 1);
                    min30To40Sum += formatFloat(data[key].min30To40, 1);
                    min40To50Sum += formatFloat(data[key].min40To50, 1);
                    min50To60Sum += formatFloat(data[key].min50To60, 1);
                    min60To90Sum += formatFloat(data[key].min60To90, 1);
                    min90UnderSum += formatFloat(data[key].min90Under, 1);
                    totalSalesSum += formatFloat(data[key].totalSales, 1);

                    tcSum += formatFloat(data[key].tc, 1);

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

            if (mydata.length > 0) {
                jQuery('#jqGrid').jqGrid('clearGridData');
                jQuery('#jqGrid').jqGrid('setGridParam', {data: mydata, page: 1});
                jQuery('#jqGrid').trigger('reloadGrid');
            } else {
                jQuery('#jqGrid').jqGrid('clearGridData');
                jQuery('#jqGrid').trigger('reloadGrid');
            }

            $("#jqGrid").trigger("reloadGrid");

            $("#jqGrid").jqGrid({
                datatype: "local",
                data: mydata,
                colModel: [
                    {label: group_name, name: 'group_name', width: 80, align: 'center', hidden: true},
                    {label: subGroup_name, name: 'subGroup_name', width: 80, align: 'center', hidden: true},

                    {label: label_store, name: 'store', width: 80, align: 'center'},
                    {label: label_date, name: 'day', width: 80, align: 'center'},

                    {label: label_in_store_time, name: 'orderPickup', index: 'orderPickup', width: 80, align: 'center'},
                    {label: label_delivery_time, name: 'pickupComplete', index: 'pickupComplete', width: 80, align: 'center'},
                    {label: label_completed_time, name: 'orderComplete', index: 'orderComplete', width: 80, align: 'center'},
                    {label: label_stay_time, name: 'riderStayTime', index: 'riderStayTime', width: 80, align: 'center'},
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
                    {label: label_errtc, name: 'errtc', index: 'errtc', width: 50, align: 'center'},
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
                loadComplete: function (data){
                    let lastRowID = $("#jqGrid").getGridParam("records");

                    let ids = $("#jqGrid").getDataIDs();

                    let averageRowID = 0;
                    let rowCount = 0;
                    let tcRowCnt = 0;
                    let returnTimeNan = 0;
                    let distanceNan = 0;
                    let tpSpNan = 0;

                    let orderPickupTotal = 0;
                    let pickupCompleteTotal = 0;
                    let orderCompleteTotal = 0;
                    let riderStayTimeTotal = 0;
                    let completeReturnTotal = 0;
                    let pickupReturnTotal = 0;
                    let orderReturnTotal = 0;
                    let min30BelowTotal = 0;
                    let min30To40Total = 0;
                    let min40To50Total = 0;
                    let min50To60Total = 0;
                    let min60To90Total = 0;
                    let min90UnderTotal = 0;
                    let totalSalesTotal = 0;
                    let errTcTotal = 0;
                    let tcTotal = 0;
                    let tplhTotal = 0;
                    let spmhTotal = 0;
                    let totalPickupReturnTotal = 0;
                    let avgDistanceTotal = 0;

                    $.each(ids, function (idx, rowId){
                        var rowData = $("#jqGrid").getRowData(rowId);
                        if (rowData.store == "Average"){
                            averageRowID = rowId;
                        }else{
                            rowCount++;

                            orderPickupTotal = sumTimeValue(orderPickupTotal, rowData.orderPickup);
                            pickupCompleteTotal = sumTimeValue(pickupCompleteTotal, rowData.pickupComplete);
                            orderCompleteTotal = sumTimeValue(orderCompleteTotal, rowData.orderComplete);
                            riderStayTimeTotal = sumTimeValue(riderStayTimeTotal, rowData.riderStayTime);
                            completeReturnTotal = sumTimeValue(completeReturnTotal, rowData.completeReturn);
                            pickupReturnTotal = sumTimeValue(pickupReturnTotal, rowData.pickupReturn);
                            orderReturnTotal = sumTimeValue(orderReturnTotal, rowData.orderReturn);
                            min30BelowTotal = Number(min30BelowTotal) + Number(rowData.min30Below.replace('%', ''));
                            min30To40Total = Number(min30To40Total) + Number(rowData.min30To40.replace('%', ''));
                            min40To50Total = Number(min40To50Total) + Number(rowData.min40To50.replace('%', ''));
                            min50To60Total = Number(min50To60Total) + Number(rowData.min50To60.replace('%', ''));
                            min60To90Total = Number(min60To90Total) + Number(rowData.min60To90.replace('%', ''));
                            min90UnderTotal = Number(min90UnderTotal) + Number(rowData.min90Under.replace('%', ''));
                            totalSalesTotal += Number(rowData.totalSales);
                            errTcTotal += Number(rowData.errtc);
                            tcTotal += Number(rowData.tc);
                            tplhTotal += Number(rowData.tplh);
                            spmhTotal += Number(rowData.spmh);
                            totalPickupReturnTotal = sumTimeValue(totalPickupReturnTotal, rowData.totalPickupReturn);
                            avgDistanceTotal = Number(avgDistanceTotal) + Number(rowData.avgDistance.replace('km', ''));

                            if (Number(rowData.tc) > 0){
                                tcRowCnt++;
                            }

                            if (rowData.completeReturn == "-" || rowData.pickupReturn == "-" || rowData.orderReturn == "-" || rowData.totalPickupReturn == "-"){
                                completeReturn++;
                            }

                            if (rowData.distance == "-"){
                                distanceNan++;
                            }

                            if (rowData.tplh == "-" || rowData.spmh == "-"){
                                tpSpNan++;
                            }

                        }
                    });

                    if (averageRowID != 0){
                        $("#jqGrid").delRowData(averageRowID);
                    }

                    lastRowID = $("#jqGrid").getGridParam("records");

                    // 평균 값
                    let avgData = new Object();
                    avgData.store = "Average" ;
                    avgData.day = "";

                    avgData.group_name = "";
                    avgData.subGroup_name = "";

                    avgData.orderPickup = divisionTimeValue(orderPickupTotal, rowCount);
                    avgData.pickupComplete = divisionTimeValue(pickupCompleteTotal, rowCount);
                    avgData.orderComplete  = divisionTimeValue(orderCompleteTotal, rowCount);
                    avgData.riderStayTime = divisionTimeValue(riderStayTimeTotal, rowCount); // 20.07.15 stay time
                    avgData.completeReturn = divisionTimeValue(completeReturnTotal, (rowCount - returnTimeNan));
                    avgData.pickupReturn =  divisionTimeValue(pickupReturnTotal, (rowCount - returnTimeNan));
                    avgData.orderReturn =   divisionTimeValue(orderReturnTotal, (rowCount - returnTimeNan));
                    avgData.min30Below = formatInt((min30BelowTotal/rowCount), 1) +"%";
                    avgData.min30To40 =formatInt((min30To40Total/rowCount), 1) +"%";
                    avgData.min40To50 = formatInt((min40To50Total/rowCount), 1) +"%";
                    avgData.min50To60 = formatInt((min50To60Total/rowCount), 1) +"%";
                    avgData.min60To90 = formatInt((min60To90Total/rowCount), 1) +"%";
                    avgData.min90Under = formatInt((min90UnderTotal/rowCount), 1) +"%";
                    avgData.totalSales = formatInt((totalSalesTotal/rowCount), 1);

                    if (errTcTotal != 0){
                        avgData.errtc = formatInt((errTcTotal/rowCount), 1);
                    } else {
                        avgData.errtc = "-";
                    }
                    avgData.tc = formatInt((tcTotal/rowCount), 1);

                    if(rowCount - tpSpNan != 0){
                        avgData.tplh = formatInt((tplhTotal/(rowCount - tpSpNan)), 1);
                        avgData.spmh = formatInt((spmhTotal/(rowCount - tpSpNan)), 1);
                    }else{
                        avgData.tplh = "-";
                        avgData.spmh = "-";
                    }

                    avgData.totalPickupReturn = divisionTimeValue(totalPickupReturnTotal, (rowCount - returnTimeNan));
                    avgData.avgDistance = formatFloat((avgDistanceTotal/rowCount), 1) +'km';

                    dateGraph(lastRowID == 0 ? null : avgData);
                    dateInfo(lastRowID == 0 ? null : avgData);

                    // 날짜 조회시 avg 노출
                    if(lastRowID!=0){
                        $("#jqGrid").addRowData(lastRowID + 1, avgData);
                    }


                }
            });

            $("#jqGrid").jqGrid('destroyGroupHeader');
            $("#jqGrid").jqGrid('setGroupHeaders', {
                useColSpanStyle: true,
                groupHeaders:[
                    {startColumnName: 'orderPickup', numberOfColumns: 6, titleText: label_average_time},
                    {startColumnName: 'totalSales', numberOfColumns: 7, titleText: label_productivity}
                ]
            });

            resizeJqGrid('#jqGrid'); //그리드 리사이즈
            loading.hide();

            // $('.state_wrap .btn_close').click(function (e) {
            //     e.preventDefault();
            //     $('.state_wrap').removeClass('on'); //상세보기 닫기
            //     setTimeout(function () {
            //         $(window).trigger('resize');
            //     }, 300)//그리드 리사이즈
            // });
        }
    });
}

function dateGraph(avgData){
    $('#chart_content').html('');

    if(avgData == null || avgData == 'undefined'){
        $('#chart_content').html(`<div class="no_chart_wrap" style="line-height: 150px;">${result_none}</div>`);
    } else {
        am4core.ready(function() {
            am4core.useTheme(am4themes_animated);

            let chart = am4core.create("chart_content", am4charts.XYChart);

            chart.data = [{
                "category" : "",
                "orderPickup": convertToMinute(avgData.orderPickup),
                "pickupComplete": convertToMinute(avgData.pickupComplete),
                "riderStayTime": convertToMinute(avgData.riderStayTime),
                "completeReturn": convertToMinute(avgData.completeReturn)
            }];

            chart.legend = new am4charts.Legend();
            chart.legend.position = "right";
            chart.legend.width = 200;
            chart.legend.markers.template.disabled = true;
            chart.legend.labels.template.text = "[bold {color}]{name} : [/]";

            let categoryAxis = chart.yAxes.push(new am4charts.CategoryAxis());
            categoryAxis.dataFields.category = "category";
            categoryAxis.renderer.grid.template.location = 0;
            categoryAxis.renderer.labels.template.disabled = true;

            let valueAxis = chart.xAxes.push(new am4charts.ValueAxis());
            valueAxis.min = 0;
            valueAxis.renderer.grid.template.opacity = 0;
            valueAxis.renderer.ticks.template.strokeOpacity = 0.5;
            valueAxis.renderer.ticks.template.stroke = am4core.color("#495C43");
            valueAxis.renderer.ticks.template.length = 10;
            valueAxis.renderer.line.strokeOpacity = 0.5;
            valueAxis.renderer.baseGrid.disabled = true;
            valueAxis.renderer.minGridDistance = 40;

            valueAxis.calculateTotals = true;
            valueAxis.min = 0;
            //valueAxis.max = 60;
            valueAxis.strictMinMax = true;

            function createSeries(field, name) {
                let series = chart.series.push(new am4charts.ColumnSeries());
                series.name = name;
                series.dataFields.valueX = field;
                series.dataFields.categoryY = "category";
                series.sequencedInterpolation = true;
                series.stacked = true;
                series.columns.template.width = am4core.percent(95);
                series.columns.template.tooltipText = "[bold]{name}[/]\n[font-size:14px]{valueX}";

                series.legendSettings.labelText = "[bold {color}]{name} : [/]";
                series.legendSettings.valueText = "{valueX.close}";
                series.legendSettings.itemValueText = "[bold]{valueX}[/bold]";

                let labelBullet = series.bullets.push(new am4charts.LabelBullet());
                labelBullet.label.text = "{valueX}";
                labelBullet.locationX = 0.5;

                return series;
            }

            createSeries('orderPickup', label_in_store_time);
            createSeries('pickupComplete', label_delivery_time);
            createSeries('riderStayTime', label_stay_time);
            createSeries('completeReturn', label_return_time);
        });
    }
}

function convertToMinute(time){
    return (time == '-' || time == '0')? null : (time.split(':').reduce((acc,time) => (60 * acc) + +time) / 60).toFixed(2)
}

function convertToHms(time){
    let arr = time.toString().split(':');

    if(arr.length == 3){
        let result = '';
        if(arr[0] != '00') result += `${parseInt(arr[0])}h `;
        if(arr[1] != '00') result += `${arr[1]}m `;
        if(arr[2] != '00') result += `${arr[2]}s`;

        return (time == '00:00:00')? '00s':result
    }else {
        return '-';
    }
}

function dateInfo(avgData){
    $('.date_graph_content .box').html('');

    var colName = $("<div></div>").addClass('col_name');
    var colVal = $("<div></div>").addClass('col_val');

    let orderPickup = avgData == null || avgData == undefined ? "-" : avgData.orderPickup;
    let pickupComplete = avgData == null || avgData == undefined ? "-" : avgData.pickupComplete;
    let orderComplete = avgData == null || avgData == undefined ? "-" : avgData.orderComplete;
    let stayTime = avgData == null || avgData == undefined ? "-" : avgData.riderStayTime;
    let completeReturn = avgData == null || avgData == undefined ? "-" : avgData.completeReturn;
    let pickupReturn = avgData == null || avgData == undefined ? "-" : avgData.pickupReturn;
    let orderReturn = avgData == null || avgData == undefined ? "-" : avgData.orderReturn;

    $('#orderPickup.box').append(colName.clone().html(label_in_store_time)).append(colVal.clone().html(convertToHms(orderPickup)));
    $('#pickupComplete.box').append(colName.clone().html(label_delivery_time)).append(colVal.clone().html(convertToHms(pickupComplete)));
    $('#orderComplete.box').append(colName.clone().html(label_completed_time)).append(colVal.clone().html(convertToHms(orderComplete)));
    $('#stayTime.box').append(colName.clone().html(label_stay_time)).append(colVal.clone().html(convertToHms(stayTime)));
    $('#completeReturn.box').append(colName.clone().html(label_return_time)).append(colVal.clone().html(convertToHms(completeReturn)));
    $('#pickupReturn.box').append(colName.clone().html(label_out_time)).append(colVal.clone().html(convertToHms(pickupReturn)));
    $('#orderReturn.box').append(colName.clone().html(label_total_delivery_time)).append(colVal.clone().html(convertToHms(orderReturn)));
}

function excelDownloadByDate(){
    let startDate = $('#startDate').val();
    let endDate = $('#endDate').val();

    let diffDate = Math.ceil((new Date(endDate).getTime() - new Date(startDate).getTime()) / (1000*3600*24));
    if (diffDate > 31){
        return;
    }

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

function formatInt(sender, pointer) {
    if (sender == null || isNaN(sender)){
        return 0;
    }
    return parseFloat(parseInt(sender).toFixed(pointer));
}

function formatFloat(sender, pointer) {
    if (sender == null || isNaN(sender)){
        return 0;
    }

    return parseFloat(parseFloat(sender).toFixed(pointer));
}


/**
 * 2020.04.29 통계 검색 조건 관련 함수들 추가
 * */

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
                    console.log("Group Change");
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

    console.log("getStatisticsSubGroupList");
    console.log(gId);

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
                    getStoreStatisticsByDate();
                });

            }
        }
    });
}

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

    var searchText1= $("#statisticsGroupList option:selected").text();
    var searchTextVal1= $("#statisticsGroupList option:selected").val();
    var searchText2= $("#statisticsSubGroupList option:selected").text();
    var searchTextVal2= $("#statisticsSubGroupList option:selected").val();
    var searchText3= $("#statisticsStoreList option:selected").text();
    var searchTextVal3= $("#statisticsStoreList option:selected").val();

    var filter = {
        groupOp: "AND",
        rules: []
    };

    if(searchTextVal1 != "reset"){
        filter.rules.push({
            field : 'group_name',
            op : "eq",
            data : searchText1
        });
        if(searchTextVal2 != "reset"){
            filter.rules.push({
                field : 'subGroup_name',
                op : "eq",
                data : searchText2
            });
            if(searchTextVal3 != "reset"){
                filter.rules.push({
                    field : 'store',
                    op : "eq",
                    data : searchText3
                });
            }
        }
    }

    var grid = jQuery('#jqGrid');

    if(filter.rules.length > 0 ){
        grid[0].p.search = true;
    }

    $.extend(grid[0].p.postData, { filters: filter });
    grid.trigger("reloadGrid", [{ page: 1 }]);
    console.log("grid trigger");
}

/**
 * jqGrid Time Sum
 * */
function sumTimeValue(sumTime, addTime){
    if (sumTime == undefined || addTime == undefined || addTime == "-"){
        return sumTime == undefined ? 0 : sumTime;
    }

    let sumTimeSecond = sumTime.toString().split(':').reduce((sum, time) =>  (60 * Number(sum)) + Number(time));
    let addTimeSecond = addTime.toString().split(':').reduce((sum, time) =>  (60 * Number(sum)) + Number(time));

    sumTimeSecond = Number(sumTimeSecond) + Number(addTimeSecond);
    var modSecond = sumTimeSecond % 60;
    var modMinute = parseInt(sumTimeSecond / 60) % 60;
    var modHourse = parseInt(sumTimeSecond / (60*60)) % 60;

    return ("00" + modHourse).slice(-2) + ":" + ("00" + modMinute).slice(-2) + ":" + ("00" + modSecond).slice(-2);
}

/**
 * Time Division
 * */
function divisionTimeValue(totalTime, divistionValue){
    let totalTimeSecond = totalTime.toString().split(':').reduce((sum, time) =>  (60 * Number(sum)) + Number(time));

    if (isNaN(Number(totalTimeSecond)) || isNaN(Number(divistionValue))){
        return 0;
    }else if (totalTimeSecond == 0 || divistionValue == 0){
        return 0;
    }

    var returnSecond = parseInt(totalTimeSecond / divistionValue);

    var modSecond = returnSecond % 60;
    var modMinute = parseInt(returnSecond / 60) % 60;
    var modHourse = parseInt(returnSecond / (60*60)) % 60;

    return ("00" + modHourse).slice(-2) + ":" + ("00" + modMinute).slice(-2) + ":" + ("00" + modSecond).slice(-2);
}

/**
 * 시 분 초의 값을 초로 변경
 * */
function changeTimeStringToSecond(time){
    if (time == undefined || time == "-"){
        return 0;
    }

    return time.toString().split(':').reduce((sum, time) =>  (60 * Number(sum)) + Number(time));
}