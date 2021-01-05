var loading= $('<div id="loading"><div><p style="background-color: #838d96"/></div></div>').appendTo(document.body).hide();
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
            $('#day1').datetimepicker('option', 'maxDate', $('#day2').datetimepicker('getDate'));
            $('#day2').datetimepicker('option', 'minDate', $('#day1').datetimepicker('getDate'));
            getStoreStatisticsByInterval();
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
        minDate : $('#day2').val(),
        onClose: function( selectedDate ) {
            $('#day1').datetimepicker('option', 'maxDate', $('#day2').datetimepicker('getDate'));
            $('#day2').datetimepicker('option', 'minDate', $('#day1').datetimepicker('getDate'));
            getStoreStatisticsByInterval();
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
        }
    }else{
        $('#startTime').css('display', 'none');
        $('#endTime').css('display', 'none');

        if (!peakTime){
            getStoreStatisticsByInterval();
        }
    }
}

// 21.01.05 피크(Peak) 타임 조회
function showPeakTime(){
    let peakTime = $('#chkPeakTime').is(":checked");

    if (peakTime){
        $('#chkTime').prop("checked", false);
        showTimePicker();
    }

    getStoreStatisticsByInterval();
}

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

function minusTime(time1, time2) {
    var d1 = new Date(time1);
    var d2 = new Date(time2);
    var minusTime = d2.getTime() - d1.getTime();
    return minusTime;
}

function getStoreStatisticsByInterval() {

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

    var tcData = [];

    loading.show();

    let sDate = $('#day1').val();
    let eDate = $('#day2').val();

    if (chkTime && !peakTime){
        sDate = sDate + " " + $('#startTime').val();
        eDate = eDate + " " + $('#endTime').val();
    }

    $.ajax({
        url: "/getStoreStatisticsByIntervalAtTWKFC",
        type: 'get',
        data: {
            startDate: sDate,
            endDate: eDate,
            timeCheck: chkTime,
            peakCheck: peakTime
        },
        dataType: 'json',
        success: function (data) {
            var tmpChartLabels = [];
            var tmpChartData = [];

            var intervalData = data.intervalData.intervalMinuteCounts;

            for (var key in intervalData) {

                tmpChartData.push(intervalData[key][0]);
                var interval_key;

                // index 번호 0, 1은 규칙이 없으므로 예외처리
                switch (Number(key)) {
                    case 0:                 // 0~7분 데이터
                        interval_key = "~6:59";
                        break;
                    case 1:                 // 7~10분 데이터
                        interval_key = "~9:59";
                        break;
                    default:
                        interval_key = Number(key) + 8;
                        if (interval_key == 60){
                            interval_key = "1:00:00";
                        }else if (interval_key > 60){
                            interval_key = "1:01:00~";
                        }else {
                            interval_key = interval_key + ":00";
                        }
                        break;
                }

                tmpChartLabels.push(interval_key);

                var tmpObject = new Object();
                tmpObject.intervalMinute = interval_key;
                tmpObject.intervalCount = intervalData[key][0];
                tmpObject.intervalCount1 = intervalData[key][1];
                tmpObject.intervalCount2 = intervalData[key][2];
                tmpObject.d7intervalCount = intervalData[key][3];
                tmpObject.d7intervalCount1 = intervalData[key][4];
                tmpObject.d7intervalCount2 = intervalData[key][5];
                tcData.push(tmpObject);
            }
            loading.hide();
            intervalGraph(tcData, data.intervalMin30Below);
        }
    });
}

function chart30minute(data){
    //30미만 평균 퍼센트 구하기
    $('#avg_30minute').html('');
    $('#avg_07minute').html('');

    var sum = 0;
    var avgLength = 0;
    var avg = 0;

    let d7avg = 0;
    let d7sum = 0;
    let d7avgLength = 0;
    data.forEach(function(d){
        if(d.min_30below != undefined){
            sum+=d.min_30below;
            avgLength++;
        }

        if (d.min_07below != undefined){
            d7sum+=d.min_07below;
            d7avgLength++;
        }
    })

    avg = (avgLength == 0)? avg : sum/avgLength;
    d7avg = (d7avgLength == 0)? d7avg : d7sum/d7avgLength;

    $('#avg_30minute').html(`D30 MINS : ${parseFloat(avg.toFixed(1))}%`);
    $('#avg_07minute').html(`D07 MINS : ${parseFloat(d7avg.toFixed(1))}%`);

    //30미만 그래프 구하기
    var chart = am4core.create("chart_30minute", am4charts.XYChart);
    chart.scrollbarX = new am4core.Scrollbar();

    chart.data = data;

    var categoryAxis = chart.xAxes.push(new am4charts.CategoryAxis());
    categoryAxis.dataFields.category = "day_to_day";
    categoryAxis.autoGridCount = false;
    categoryAxis.gridCount = 10;
    categoryAxis.renderer.grid.template.location = 0;
    categoryAxis.renderer.minGridDistance = 20;
    categoryAxis.renderer.labels.template.fontSize = 13;
    categoryAxis.renderer.labels.template.horizontalCenter = "right";
    categoryAxis.renderer.labels.template.verticalCenter = "middle";
    categoryAxis.renderer.labels.template.rotation = 270;
    categoryAxis.tooltip.disabled = true;
    categoryAxis.renderer.minHeight = 50;
    //categoryAxis.renderer.grid.template.disabled = true;

    var valueAxis = chart.yAxes.push(new am4charts.ValueAxis());
    valueAxis.renderer.minWidth = 30;
    valueAxis.min = 0;
    valueAxis.max = 100;
    //valueAxis.renderer.grid.template.disabled = true;

    // min 30에 대한 그래프 정보
    var series = chart.series.push(new am4charts.ColumnSeries());
    series.dataFields.valueY = "min_30below";
    series.dataFields.categoryX = "day_to_day";
    series.columns.template.tooltipText = "{categoryX}: [bold]{valueY}%[/]";
    series.columns.template.tooltipY = 0;
    series.columns.template.strokeWidth = 0;
    series.columns.template.fill = am4core.color("#85a9e3");
    series.tooltip.pointerOrientation = "vertical";

    series.columns.template.width = am4core.percent(100);

    // D7에 대한 그래픽 정보
    var series1 = chart.series.push(new am4charts.ColumnSeries());
    series1.dataFields.valueY = "min_07below";
    series1.dataFields.categoryX = "day_to_day";
    series1.columns.template.tooltipText = "{categoryX}: [bold]{valueY}%[/]";
    series1.columns.template.tooltipY = 0;
    series1.columns.template.strokeWidth = 0;
    series1.columns.template.fill = am4core.color("#ffa9f1");
    series1.tooltip.pointerOrientation = "vertical";

    series1.columns.template.width = am4core.percent(100);

    /*var labelBullet = new am4charts.LabelBullet();
    series.bullets.push(labelBullet);
    labelBullet.strokeOpacity = 0;
    labelBullet.stroke = am4core.color("#dadada");
    labelBullet.dy = -20;
    labelBullet.minWidth = 20;
    labelBullet.label.text = "{valueY}";
    labelBullet.label.fontSize = 10;
    labelBullet.label.adapter.add("text", function(value, target) {
        if(target.dataItem.valueY == 0) return '';
        return value;
    })*/
}

let testData;

function chartPercentage(data){
    $('#chart_percentage').html('');

    //파이 그래프 데이터 작업
    var count = [];
    var d7count = [];
    var x = [];
    var y = [];
    var sliceStart = 2;
    var result = [];
    data.forEach((d)=> x.push(d.intervalCount));
    data.forEach((d)=> y.push(d.d7intervalCount));

    testData = y;

    count.push(x.slice(0,1)[0]);//0~7분 추가
    count.push(x.slice(1,2)[0]);//7~10분 추가
    d7count.push(y.slice(0, 1)[0]); // d7 0-7분 추가
    d7count.push(y.slice(1, 2)[0]); // d7 7-10분 추가

    //10분부터 60분이상 까지 5분 간격으로 표시
    for(var i=0; i<11; i++){
        count.push(x.slice(sliceStart,sliceStart+5).reduce((a,b)=>a+b));
        d7count.push(y.slice(sliceStart,sliceStart+5).reduce((a,b)=>a+b));
        sliceStart+= 5;
    }

    //전부 값이 0이면 그래프 표출 안하기 위해서
    var value = count.reduce((a, b) => a + b);
    var d7value = count.reduce((a, b) => a + b);
    if(value != 0 || d7value != 0){
        result.push({'intervalMinute':'00~07', 'intervalCount':Number(count[0]) + Number(d7count[0])});
        result.push({'intervalMinute':'07~10', 'intervalCount':Number(count[1]) + Number(d7count[1])});
        result.push({'intervalMinute':'10~15', 'intervalCount':Number(count[2]) + Number(d7count[2])});
        result.push({'intervalMinute':'15~20', 'intervalCount':Number(count[3]) + Number(d7count[3])});
        result.push({'intervalMinute':'20~25', 'intervalCount':Number(count[4]) + Number(d7count[4])});
        result.push({'intervalMinute':'25~30', 'intervalCount':Number(count[5]) + Number(d7count[5])});
        result.push({'intervalMinute':'30~35', 'intervalCount':Number(count[6]) + Number(d7count[6])});
        result.push({'intervalMinute':'35~40', 'intervalCount':Number(count[7]) + Number(d7count[7])});
        result.push({'intervalMinute':'40~45', 'intervalCount':Number(count[8]) + Number(d7count[8])});
        result.push({'intervalMinute':'45~50', 'intervalCount':Number(count[9]) + Number(d7count[9])});
        result.push({'intervalMinute':'50~55', 'intervalCount':Number(count[10]) + Number(d7count[10])});
        result.push({'intervalMinute':'55~60', 'intervalCount':Number(count[11]) + Number(d7count[11])});
        result.push({'intervalMinute':'60~  ', 'intervalCount':Number(count[12]) + Number(d7count[12])});

        var chart = am4core.create("chart_percentage", am4charts.PieChart);

        chart.innerRadius = am4core.percent(30);

        var pieSeries = chart.series.push(new am4charts.PieSeries());
        pieSeries.ticks.template.disabled = true;
        pieSeries.alignLabels = false;
        pieSeries.isActive = false;
        pieSeries.dataFields.value = "intervalCount";
        pieSeries.dataFields.category = "intervalMinute";

        pieSeries.slices.template.stroke = am4core.color("#fff");
        pieSeries.slices.template.strokeWidth = 1;
        pieSeries.slices.template.strokeOpacity = 1;
        pieSeries.slices.template.cursorOverStyle = [{"property": "cursor","value": "pointer"}];


        //pieSeries.tooltip.getFillFromObject = false;
        //pieSeries.tooltip.label.fill = am4core.color('#fff');

        pieSeries.labels.template.text = "{value.percent.formatNumber('#.0')}%";
        pieSeries.labels.template.radius = am4core.percent(-30);

        pieSeries.labels.template.adapter.add("radius", function(radius, target) {
            if (target.dataItem && (target.dataItem.values.value.percent < 5)) return 0;
            return radius;
        });

        pieSeries.labels.template.adapter.add("text", function(value, target) {
            if (target.dataItem.values.value.percent == 0) return '';
            return value;
        });

        chart.data = result;

        chart.legend = new am4charts.Legend();
        chart.legend.position = "right";
        chart.legend.width = 110;
        //chart.legend.markers.template.disabled = true;
        chart.legend.labels.template.text = "[bold {color}]{name}";
        chart.legend.valueLabels.template.text = "{value.value}";
        //chart.legend.itemContainers.template.tooltipText = "";
        chart.legend.itemContainers.template.hoverable = false;
    } else {
        $('#chart_percentage').html(`<div class="no_chart_wrap">${label_count}<br>${result_none}</div>`);
    }

}

function chartTc(data){
    var chart = am4core.create("chart_tc", am4charts.XYChart);
    //chart.scrollbarX = new am4core.Scrollbar();

    chart.data = data;

    var categoryAxis = chart.xAxes.push(new am4charts.CategoryAxis());
    categoryAxis.dataFields.category = "intervalMinute";
    categoryAxis.renderer.grid.template.location = 0;
    categoryAxis.renderer.minGridDistance = 10;
    categoryAxis.renderer.labels.template.fontSize = 13;
    categoryAxis.renderer.labels.template.horizontalCenter = "right";
    categoryAxis.renderer.labels.template.verticalCenter = "middle";
    categoryAxis.renderer.labels.template.rotation = 270;
    categoryAxis.tooltip.disabled = true;
    categoryAxis.renderer.minHeight = 50;
    //categoryAxis.renderer.grid.template.disabled = true;

    var valueAxis = chart.yAxes.push(new am4charts.ValueAxis());
    valueAxis.renderer.minWidth = 30;
    valueAxis.min = 0
    //valueAxis.renderer.grid.template.disabled = true;

    var series = chart.series.push(new am4charts.ColumnSeries());
    series.dataFields.valueY = "intervalCount";
    series.dataFields.categoryX = "intervalMinute";
    series.columns.template.tooltipText = "{categoryX}: [bold]{valueY}[/]";
    series.columns.template.tooltipY = 0;
    series.columns.template.strokeWidth = 0;
    series.columns.template.fill = am4core.color("#f2d32a");
    series.tooltip.pointerOrientation = "vertical";

    var series1 = chart.series.push(new am4charts.ColumnSeries());
    series1.dataFields.valueY = "d7intervalCount";
    series1.dataFields.categoryX = "intervalMinute";
    series1.columns.template.tooltipText = "{categoryX}: [bold]{valueY}[/]";
    series1.columns.template.tooltipY = 0;
    series1.columns.template.strokeWidth = 0;
    series1.columns.template.fill = am4core.color("#a1d3f5");
    series1.tooltip.pointerOrientation = "vertical";

    /*series.columns.template.column.cornerRadiusTopLeft = 5;
    series.columns.template.column.cornerRadiusTopRight = 5;
    series.columns.template.column.fillOpacity = 0.8;*/
    series.columns.template.width = am4core.percent(85);
    series1.columns.template.width = am4core.percent(85);

    var labelBullet = new am4charts.LabelBullet();
    series.bullets.push(labelBullet);
    series1.bullets.push(labelBullet);
    labelBullet.label.text = "{valueY}";
    labelBullet.strokeOpacity = 0;
    labelBullet.stroke = am4core.color("#dadada");
    labelBullet.dy = -20;
    labelBullet.minWidth = 20;
    labelBullet.label.fontSize = 10;
    labelBullet.label.adapter.add("text", function(value, target) {
        if(target.dataItem.valueY == 0) return '';
        return value;
    })

}

function intervalGraph(tcData, less30MinuteData) {
    am4core.useTheme(am4themes_animated);

    chart30minute(less30MinuteData)
    chartPercentage(tcData)
    chartTc(tcData)
}

function excelDownloadByInterval(){
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

    $.fileDownload("/excelDownloadByIntervalAtTWKFC",{
        httpMethod:"GET",
        data : {
            startDate : startDate,
            endDate : endDate,
            timeCheck: chkTime,
            peakCheck: peakTime
        },
        successCallback: function(url){
            loading.hide();
        },
        failCallback: function(responseHtml,url){
            loading.hide();
        }
    });
}