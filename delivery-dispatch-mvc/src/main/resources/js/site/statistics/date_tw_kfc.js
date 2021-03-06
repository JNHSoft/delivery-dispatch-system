let loading= $('<div id="loading"><div><p style="background-color: #838d96"/></div></div>').appendTo(document.body).hide();
let beforePeakType = 0;

$(function () {
    let date = $.datepicker.formatDate('yy-mm-dd', new Date);
    $('#day1, #day2').val(date);

    /**
     * 20.12.24 DateTime Picker
     * */
    $('#day1').datetimepicker({
        altField: '#startTime',
        controlType: 'select',
        oneLine: true,
        timeInput: true,
        timeText: set_time,
        closeText: btn_confirm,
        stepMinute: 10,
        maxDate : $('#day1').val(),
        onClose: function(selectedDate) {
            console.log("onClose1");
            $('#day1').datetimepicker('option', 'maxDate', $('#day2').datetimepicker('getDate'));
            $('#day2').datetimepicker('option', 'minDate', $('#day1').datetimepicker('getDate'));
            getStoreStatisticsByDate();
        }
    });

    $('#day2').datetimepicker({
        altField: "#endTime",
        controlType: 'select',
        oneLine: true,
        timeInput: true,
        timeText: set_time,
        closeText: btn_confirm,
        stepMinute: 10,
        minDate : $('#endDate').val(),
        onClose: function( selectedDate ) {
            console.log("onClose2");
            $('#day1').datetimepicker('option', 'maxDate', $('#day2').datetimepicker('getDate'));
            $('#day2').datetimepicker('option', 'minDate', $('#day1').datetimepicker('getDate'));
            getStoreStatisticsByDate();
        }
    });

    showTimePicker();
});

// 20.12.24 시간을 선택할 수 있는 항목 노출
function showTimePicker(){
    let chkTime = $('#chkTime').is(":checked");
    let peakTime = $('#chkPeakTime').is(":checked");

    if (chkTime){
        $('#startTime').show();
        $('#endTime').show();
        if (peakTime){
            $('#chkPeakTime').prop("checked", false);
            $('#sel_peak_time').css('display', 'none');
        }
    }else{
        $('#startTime').css('display', 'none');
        $('#endTime').css('display', 'none');

        if (!peakTime) {
            getStoreStatisticsByDate();
        }
    }
}

// 21.01.05 피크(Peak) 타임 조회
function showPeakTime(){
    let peakTime = $('#chkPeakTime').is(":checked");

    if (peakTime){
        $('#chkTime').prop("checked", false);
        $('#sel_peak_time').show();
        showTimePicker();
    }else{
        $('#sel_peak_time').css('display', 'none');
    }

    getStoreStatisticsByDate();
}

// 21.01.06 피크 Type 종류 변경 시, 데이터 리플레쉬
function changePeakType(){
    // 변경 전 값과 다른 경우에 한하여 데이터 리플레쉬를 한다.
    if (beforePeakType.toString() != $('#sel_peak_time').val().toString()){
        beforePeakType = $('#sel_peak_time').val();
        getStoreStatisticsByDate();
    }
}

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

    let diffDate = Math.ceil((new Date($('#day2').val()).getTime() - new Date($('#day1').val()).getTime()) / (1000*3600*24));
    if (diffDate > 31){
        return;
    }

    // 20.12.24 시간 범위도 포함 유무 체크 후 값 보내기
    let chkTime = $('#chkTime').is(":checked");
    let peakTime = $('#chkPeakTime').is(":checked");

    if (chkTime && !peakTime){       // 체크가 되어 있다면 날짜 범위 체크
        let startDT = $('#day1').datetimepicker('getDate');
        let endDT = $('#day2').datetimepicker('getDate');

        if ($('#startTime').val() == undefined || $('#startTime').val() == "" || $('#endTime').val() == undefined || $('#endTime').val() == ""){
            return;
        }

        if (startDT.getTime() > endDT.getTime()){
            return;
        }
    }

    let sDate = $('#day1').val();
    let eDate = $('#day2').val();

    if (chkTime && !peakTime){
        sDate = sDate + " " + $('#startTime').val();
        eDate = eDate + " " + $('#endTime').val();
    }

    let mydata = [];
    loading.show();
    $.ajax({
        url: "/getStoreStatisticsByDateAtTWKFC",
        type: 'get',
        data: {
            startDate: sDate,
            endDate: eDate,
            timeCheck: chkTime,
            peakCheck: peakTime,
            peakType: $('#sel_peak_time').val()
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
            let minD7BelowSum = 0;
            let min30BelowSum = 0;
            let min30To40Sum = 0;
            let min40To50Sum = 0;
            let min50To60Sum = 0;
            let min60To90Sum = 0;
            let min90UnderSum = 0;
            let totalSalesSum = 0;
            let totalD7Success = 0;

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
            // 정상 TC가 있는 row 갯수
            let tcRowCnt = 0;
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

                    // 정상 Row 체크
                    if(formatInt(data[key].tc, 1) > 0){
                        tcRowCnt++;
                    }

                    tmpdata.store = my_store.storeName;
                    tmpdata.orderPickup = totalTimeSet(data[key].orderPickup*1000);
                    tmpdata.pickupComplete = totalTimeSet(data[key].pickupComplete*1000);
                    tmpdata.orderComplete = totalTimeSet(data[key].orderComplete*1000);
                    tmpdata.completeReturn = totalTimeSet(data[key].completeReturn*1000);
                    tmpdata.pickupReturn = totalTimeSet(data[key].pickupReturn*1000);
                    tmpdata.orderReturn = totalTimeSet(data[key].orderReturn*1000);
                    tmpdata.minD7Below = formatFloat(data[key].minD7Below, 1) + "%";
                    tmpdata.min30Below = formatFloat(data[key].min30Below, 1) + "%";
                    tmpdata.min30To40 = formatFloat(data[key].min30To40, 1) + "%";
                    tmpdata.min40To50 = formatFloat(data[key].min40To50, 1) + "%";
                    tmpdata.min50To60 = formatFloat(data[key].min50To60, 1) + "%";
                    tmpdata.min60To90 = formatInt(data[key].min60To90, 1) + "%";
                    tmpdata.min90Under = formatFloat(data[key].min90Under, 1) + "%";
                    tmpdata.totalSales = formatInt(data[key].totalSales, 1);
                    tmpdata.errtc = formatInt(data[key].errtc);
                    tmpdata.tc = formatInt(data[key].tc);

                    if(data[key].tplh){
                        tmpdata.tplh = formatFloat(data[key].tplh, 2);
                        tplhSum += formatFloat(data[key].tplh, 2);
                    } else{
                        tmpdata.tplh = "-";
                        chkTpSpCnt++;
                    }

                    if(data[key].spmh){
                        tmpdata.spmh = formatFloat(data[key].spmh, 2);
                        spmhSum += formatFloat(data[key].spmh, 2);
                    } else{
                        tmpdata.spmh = "-";
                        chkTpSpCnt++;
                    }
                    tmpdata.totalPickupReturn = totalTimeSet(data[key].totalPickupReturn*1000);
                    tmpdata.avgDistance = (data[key].avgDistance?formatFloat(data[key].avgDistance, 1):0) +'km';

                    // 평균 값
                    orderPickupSum += formatFloat(data[key].orderPickup, 1);
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

                    minD7BelowSum += formatFloat(data[key].minD7Below, 1);
                    min30BelowSum += formatFloat(data[key].min30Below, 1);
                    min30To40Sum += formatFloat(data[key].min30To40, 1);
                    min40To50Sum += formatFloat(data[key].min40To50, 1);
                    min50To60Sum += formatFloat(data[key].min50To60, 1);
                    min60To90Sum += formatFloat(data[key].min60To90, 1);
                    min90UnderSum += formatFloat(data[key].min90Under, 1);
                    totalSalesSum += formatFloat(data[key].totalSales, 1);
                    // D7 성공 개수
                    totalD7Success += formatInt(data[key].d7Success, 1);

                    errtcSum += formatInt(data[key].errtc, 1);
                    tcSum += formatInt(data[key].tc, 1);

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
            avgData.orderPickup = totalTimeSet((orderPickupSum*1000)/tcRowCnt);
            avgData.pickupComplete = totalTimeSet((pickupCompleteSum*1000)/tcRowCnt);
            avgData.orderComplete  = totalTimeSet((orderCompleteSum*1000)/tcRowCnt);
            avgData.completeReturn = totalTimeSet((completeReturnSum*1000)/rowReduceCnt);
            avgData.pickupReturn =  totalTimeSet((pickupReturnSum*1000)/rowReduceCnt);
            avgData.orderReturn =   totalTimeSet((orderReturnSum*1000)/rowReduceCnt);
            avgData.minD7Below = formatFloat((minD7BelowSum/tcRowCnt), 1) + "%";
            avgData.min30Below = formatFloat((min30BelowSum/tcRowCnt), 1) +"%";
            avgData.min30To40 =formatFloat((min30To40Sum/tcRowCnt), 1) +"%";
            avgData.min40To50 = formatFloat((min40To50Sum/tcRowCnt), 1) +"%";
            avgData.min50To60 = formatFloat((min50To60Sum/tcRowCnt), 1) +"%";
            avgData.min60To90 = formatFloat((min60To90Sum/tcRowCnt), 1) +"%";
            avgData.min90Under = formatFloat((min90UnderSum/tcRowCnt), 1) +"%";
            avgData.totalSales = formatInt((totalSalesSum/tcRowCnt), 1);
            avgData.errtc = formatInt((errtcSum/rowCnt), 1);
            avgData.tc = formatInt((tcSum/tcRowCnt), 1);

            if(tpSpCnt!=0){
                avgData.tplh = formatFloat((tplhSum/tpSpCnt), 2);
                avgData.spmh = formatFloat((spmhSum/tpSpCnt), 2);
            }else{
                avgData.tplh = "-";
                avgData.spmh = "-";
            }

            avgData.totalPickupReturn = totalTimeSet((totalPickupReturnSum*1000)/rowReduceCnt);
            avgData.avgDistance = formatFloat((avgDistanceSum/distanceCnt), 1) +'km';

            // 날짜 조회시 avg 노출
            if(rowCnt!=0){
                mydata.push(avgData);
            }

            if (mydata.length > 0) {
                dateGraph(avgData);
                dateInfo(avgData);
                jQuery('#jqGrid').jqGrid('clearGridData');
                jQuery('#jqGrid').jqGrid('setGridParam', {data: mydata, page: 1});
                jQuery('#jqGrid').trigger('reloadGrid');
            } else {
                dateGraph();
                dateInfo(avgData);
                jQuery('#jqGrid').jqGrid('clearGridData');
            }
            $("#jqGrid").jqGrid({
                datatype: "local",
                data: mydata,
                colModel: [
                    {label: label_store, name: 'store', width: 80, align: 'center'},
                    {label: label_in_store_time, name: 'orderPickup', index: 'orderPickup', width: 80, align: 'center'},
                    {label: label_delivery_time, name: 'pickupComplete', index: 'pickupComplete', width: 80, align: 'center'},
                    {label: label_completed_time, name: 'orderComplete', index: 'orderComplete', width: 80, align: 'center'},
                    {label: label_return_time, name: 'completeReturn', index: 'completeReturn', width: 80, align: 'center'},
                    {label: label_out_time, name: 'pickupReturn', index: 'pickupReturn', width: 80, align: 'center'},
                    {label: label_total_delivery_time, name: 'orderReturn', index: 'orderReturn', width: 80, align: 'center'},
                    {label: '< D7 MINS %', name: 'minD7Below', index: 'minD7Below', width: 80, align: 'center'},
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
                pager: "#jqGridPager",
            });

            $("#jqGrid").jqGrid('destroyGroupHeader');
            $("#jqGrid").jqGrid('setGroupHeaders', {
                useColSpanStyle: true,
                groupHeaders:[
                    {startColumnName: 'orderPickup', numberOfColumns: 6, titleText: label_average_time},
                    {startColumnName: 'minD7Below', numberOfColumns: 7, titleText: label_percent_completed},
                    {startColumnName: 'totalSales', numberOfColumns: 7, titleText: label_productivity}
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
            createSeries('completeReturn', label_return_time);
        });
    }
}
function convertToMinute(time){
    return (time == '-')? null : (time.split(':').reduce((acc,time) => (60 * acc) + +time) / 60).toFixed(2)
}
function convertToHms(time){
    let arr = time.split(':');

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

    let orderPickup = avgData.orderPickup;
    let pickupComplete = avgData.pickupComplete;
    let orderComplete = avgData.orderComplete;
    let completeReturn = avgData.completeReturn;
    let pickupReturn = avgData.pickupReturn;
    let orderReturn = avgData.orderReturn;
    let d7SuccessRate = avgData.minD7Below;

    $('#d7Timer.box').append(colName.clone().html(label_in_d7_completed)).append(colVal.clone().html(d7SuccessRate));
    $('#orderPickup.box').append(colName.clone().html(label_in_store_time)).append(colVal.clone().html(convertToHms(orderPickup)));
    $('#pickupComplete.box').append(colName.clone().html(label_delivery_time)).append(colVal.clone().html(convertToHms(pickupComplete)));
    $('#orderComplete.box').append(colName.clone().html(label_completed_time)).append(colVal.clone().html(convertToHms(orderComplete)));
    $('#completeReturn.box').append(colName.clone().html(label_return_time)).append(colVal.clone().html(convertToHms(completeReturn)));
    $('#pickupReturn.box').append(colName.clone().html(label_out_time)).append(colVal.clone().html(convertToHms(pickupReturn)));
    $('#orderReturn.box').append(colName.clone().html(label_total_delivery_time)).append(colVal.clone().html(convertToHms(orderReturn)));
}

function excelDownloadByDate(){
    let startDate = $('#day1').val();
    let endDate = $('#day2').val();

    let diffDate = Math.ceil((new Date(endDate).getTime() - new Date(startDate).getTime()) / (1000*3600*24));
    if (diffDate > 31){
        return;
    }

    // 20.12.24 시간 범위도 포함 유무 체크 후 값 보내기
    let chkTime = $('#chkTime').is(":checked");
    let peakTime = $('#chkPeakTime').is(":checked");

    if (chkTime && !peakTime){       // 체크가 되어 있다면 날짜 범위 체크
        let startDT = $('#day1').datetimepicker('getDate');
        let endDT = $('#day2').datetimepicker('getDate');

        if ($('#startTime').val() == undefined || $('#startTime').val() == "" || $('#endTime').val() == undefined || $('#endTime').val() == ""){
            return;
        }

        if (startDT.getTime() > endDT.getTime()){
            return;
        }
    }

    loading.show();

    if (chkTime && !peakTime){
        startDate = startDate + " " + $('#startTime').val();
        endDate = endDate + " " + $('#endTime').val();
    }

    $.fileDownload("/excelDownloadByDateAtTWKFC",{
        httpMethod:"GET",
        data : {
            startDate : startDate,
            endDate : endDate,
            timeCheck: chkTime,
            peakCheck: peakTime,
            peakType: $('#sel_peak_time').val()
        },
        successCallback: function(url){
            loading.hide();
        },
        failCallback: function(responseHtml,url){
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