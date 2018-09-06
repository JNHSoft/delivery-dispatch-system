var loading= $('<div id="loading"><div><p style="background-color: #838d96"/></div></div>').appendTo(document.body).hide();
$(function () {
    $('input[name="datepicker"]').change(function () {
        getStoreStatisticsByInterval();
    })
    $('input[name=datepicker]').val($.datepicker.formatDate('yy-mm-dd', new Date));
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
    var mydata = [];
    loading.show();
    $.ajax({
        url: "/getStoreStatisticsByInterval",
        type: 'get',
        data: {
            startDate: $('#day1').val(),
            endDate: $('#day2').val()
        },
        dataType: 'json',
        success: function (data) {
            var tmpChartLabels = [];
            var tmpChartData = [];

            for (var key in data.intervalMinuteCounts) {

                tmpChartData.push(data.intervalMinuteCounts[key][0]);

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
                tmpObject.intervalCount = data.intervalMinuteCounts[key][0];
                tmpObject.intervalCount1 = data.intervalMinuteCounts[key][1]+"%";
                tmpObject.intervalCount2 = data.intervalMinuteCounts[key][2]+"%";
                mydata.push(tmpObject);
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
                    {label: '時間', name: 'intervalMinute', width: 25, align: 'center'},
                    {label: 'TC數', name: 'intervalCount', width: 25, align: 'center'},
                    {label: '百分比', name: 'intervalCount1', width: 25, align: 'center'},
                    {label: '累積達成率', name: 'intervalCount2', width: 25, align: 'center'}
                ],
                width: 'auto',
                height: 700,
                autowidth: true,
                rowNum: 20,
                footerrow: true,
                pager: "#jqGridPager"
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

            var chartData = {
                labels: tmpChartLabels,
                datasets: [{
                    label: 'TC數',
                    data: tmpChartData,
                    backgroundColor: 'rgba(0, 0, 255, 0.6)',
                    borderColor: 'rgba(0, 0, 255, 1)',
                    borderWidth: 1
                }]
            };
            intervalChart(chartData);
        }
    });
}

function intervalChart(chartData) {
    var cnvs = document.getElementById('intervalCanvas');
    var ctx = cnvs.getContext('2d');

    ctx.clearRect(0, 0, cnvs.width, cnvs.height);
    ctx.beginPath();

    var stackedBar = new Chart(ctx, {
        type: 'bar',
        data: chartData
    });
}

function excelDownloadByInterval(){
    alert(befoore_function);
    /*let startDate = $('#day1').val();
    let endDate = $('#day2').val();
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
        failCallback: function(responseHtml,url){
            loading.hide();
        }
    })*/
}