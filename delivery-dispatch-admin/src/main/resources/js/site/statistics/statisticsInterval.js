var loading= $('<div id="loading"><div><p style="background-color: #838d96"/></div></div>').appendTo(document.body).hide();
$(function () {
    let date = $.datepicker.formatDate('yy-mm-dd', new Date);
    $('#startDate, #endDate').val(date);

    $('#startDate').datepicker({
        maxDate : date,
        onClose: function(selectedDate) {
            $('#endDate').datepicker('option', 'minDate', selectedDate);
            getStoreStatisticsByInterval();
        }
    });

    $('#endDate').datepicker({
        minDate : date,
        onClose: function( selectedDate ) {
            $('#startDate').datepicker('option', 'maxDate', selectedDate);
            getStoreStatisticsByInterval();
        }
    });

    getStoreStatisticsByInterval();
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

function minusTime(time1, time2) {
    var d1 = new Date(time1);
    var d2 = new Date(time2);
    var minusTime = d2.getTime() - d1.getTime();
    return minusTime;
}

function getStoreStatisticsByInterval() {

    let diffDate = Math.ceil((new Date($('#endDate').val()).getTime() - new Date($('#startDate').val()).getTime()) / (1000*3600*24));
    if (diffDate > 31){
        return;
    }

    var tcData = [];

    loading.show();

    $.ajax({
        url: "/getStoreStatisticsByInterval",
        type: 'get',
        data: {
            startDate: $('#startDate').val(),
            endDate: $('#endDate').val()
        },
        dataType: 'json',
        success: function (data) {
            var tmpChartLabels = [];
            var tmpChartData = [];

            var intervalData = data.intervalData.intervalMinuteCounts;

            for (var key in intervalData) {

                tmpChartData.push(intervalData[key][0]);
                var interval_key;

                var interval_key = Number(key) + 9;
                if (interval_key == 9) {
                    interval_key = "~" + interval_key + ":59";
                } else if (interval_key == 60) {
                    interval_key = "1:00:00";
                } else if (interval_key > 60) {
                    interval_key = "1:01:00~";
                } else {
                    interval_key = interval_key + ":00"
                }

                tmpChartLabels.push(interval_key);

                var tmpObject = new Object();
                tmpObject.intervalMinute = interval_key;
                tmpObject.intervalCount = intervalData[key][0];
                tmpObject.intervalCount1 = intervalData[key][1];
                tmpObject.intervalCount2 = intervalData[key][2];
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

    var sum = 0;
    var avgLength = 0;
    var avg = 0;

    data.forEach(function(d){
        if(d.min_30below != undefined){
            sum+=d.min_30below;
            avgLength++;
        }
    })

    avg = (avgLength == 0)? avg : sum/avgLength;

    $('#avg_30minute').html(`D30 MINS : ${parseFloat(avg.toFixed(1))}%`);

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
}

function chartPercentage(data){
    $('#chart_percentage').html('');

    //파이 그래프 데이터 작업
    var count = [];
    var x = [];
    var y = [];
    var sliceStart = 1;
    var result = [];
    data.forEach((d)=> x.push(d.intervalCount));

    testData = y;

    count.push(x.slice(0,1)[0]);//10분 미만은 먼저 추가

    //10분부터 60분이상 까지 5분 간격으로 표시
    for(var i=0; i<11; i++){
        count.push(x.slice(sliceStart,sliceStart+5).reduce((a,b)=>a+b));
        sliceStart+= 5
    }

    //전부 값이 0이면 그래프 표출 안하기 위해서
    var value = count.reduce((a, b) => a + b);
    if(value != 0){
        result.push({'intervalMinute':'00~10', 'intervalCount':count[0]});
        result.push({'intervalMinute':'10~15', 'intervalCount':count[1]});
        result.push({'intervalMinute':'15~20', 'intervalCount':count[2]});
        result.push({'intervalMinute':'20~25', 'intervalCount':count[3]});
        result.push({'intervalMinute':'25~30', 'intervalCount':count[4]});
        result.push({'intervalMinute':'30~35', 'intervalCount':count[5]});
        result.push({'intervalMinute':'35~40', 'intervalCount':count[6]});
        result.push({'intervalMinute':'40~45', 'intervalCount':count[7]});
        result.push({'intervalMinute':'45~50', 'intervalCount':count[8]});
        result.push({'intervalMinute':'50~55', 'intervalCount':count[9]});
        result.push({'intervalMinute':'55~60', 'intervalCount':count[10]});
        result.push({'intervalMinute':'60~  ', 'intervalCount':count[11]});

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
        pieSeries.marginLeft = 20;

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
        chart.legend.labels.template.text = "[bold {color}]{name}";
        chart.legend.valueLabels.template.text = "{value.value}";
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
    series.columns.template.fill = am4core.color("#85a9e3");
    series.tooltip.pointerOrientation = "vertical";

    series.columns.template.width = am4core.percent(85);

    var labelBullet = new am4charts.LabelBullet();
    series.bullets.push(labelBullet);
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
    let startDate = $('#startDate').val();
    let endDate = $('#endDate').val();

    let diffDate = Math.ceil((new Date(endDate).getTime() - new Date(startDate).getTime()) / (1000*3600*24));
    if (diffDate > 31){
        return;
    }

    loading.show();

    $.fileDownload("/excelDownloadByInterval",{
        httpMethod:"GET",
        data : {
            startDate : startDate,
            endDate : endDate
        },
        successCallback: function(url){
            loading.hide();
        },
        failCallback: function(responseHtml,url,err){
            loading.hide();
        }
    });
}