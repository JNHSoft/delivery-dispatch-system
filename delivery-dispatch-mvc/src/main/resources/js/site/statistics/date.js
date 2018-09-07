let loading= $('<div id="loading"><div><p style="background-color: #838d96"/></div></div>').appendTo(document.body).hide();
$(function () {
    $('input[name="datepicker"]').change(function () {
        getStoreStatistics();
    })
    $('input[name=datepicker]').val($.datepicker.formatDate('yy-mm-dd', new Date));
    getStoreStatistics();
});

function timeSet(time) {
    if (time != null) {
        let d = new Date(time);
        return $.datepicker.formatDate('mm-dd ', d) + ('0' + d.getHours()).slice(-2) + ':' + ('0' + d.getMinutes()).slice(-2);
    } else {
        return "-";
    }
}

function timeSetDate(time) {
    if (time != null) {
        let d = new Date(time);
        return $.datepicker.formatDate('yy.mm.dd ', d);
    } else {
        return "-";
    }
}

function totalTimeSet(time) {
    if (time != null) {
        let d = new Date(time);
        return ('0' + d.getUTCHours()).slice(-2) + ':' + ('0' + d.getUTCMinutes()).slice(-2) + ':' + ('0' + d.getUTCSeconds()).slice(-2);
    } else {
        return "-";
    }
}


function minusTimeSet(time1, time2) {
    if (time2 != null) {
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

function minusTime(time1, time2) {
    let d1 = new Date(time1);
    let d2 = new Date(time2);
    let minusTime = d2.getTime() - d1.getTime();
    return minusTime;
}

function getStoreStatistics() {
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
            for (let key in data) {
                if (data.hasOwnProperty(key)) {
                    let tmpdata = new Object();
                    tmpdata.store = my_store.storeName;
                    tmpdata.day = data[key].dayToDay;
                    tmpdata.orderPickup = data[key].orderPickup;
                    tmpdata.pickupComplete = data[key].pickupComplete;
                    tmpdata.orderComplete = data[key].orderComplete;
                    tmpdata.completeReturn = data[key].completeReturn;
                    tmpdata.pickupReturn = data[key].pickupReturn;
                    tmpdata.orderReturn = data[key].orderReturn;
                    tmpdata.min30Below = totalTimeSet(data[key].min30Below);
                    tmpdata.min30To40 = totalTimeSet(data[key].min30To40);
                    tmpdata.min40To50 = totalTimeSet(data[key].min40To50);
                    tmpdata.min50To60 = totalTimeSet(data[key].min50To60);
                    tmpdata.min90Under = totalTimeSet(data[key].min90Under);
                    tmpdata.min90Over = totalTimeSet(data[key].min90Over);
                    tmpdata.totalSales = data[key].totalSales;
                    tmpdata.tplh = data[key].tplh;
                    tmpdata.spmh = data[key].spmh;
                    tmpdata.totalPickupReturn = totalTimeSet(data[key].totalPickupReturn);
                    tmpdata.avgDistance = data[key].avgDistance +'km';
                    mydata.push(tmpdata);
                }
            }

            if (mydata != null) {
                jQuery('#jqGrid').jqGrid('clearGridData')
                jQuery('#jqGrid').jqGrid('setGridParam', {data: mydata, page: 1})
                jQuery('#jqGrid').trigger('reloadGrid');
            }
            $("#jqGrid").jqGrid({
                datatype: "local",
                data: mydata,
                colModel: [
                    {label: 'STORE', name: 'store', width: 25, key: true, align: 'center', hidden: true},
                    {label: '日期', name: 'day', width: 80, align: 'center'},
                    {label: '留店時間', name: 'orderPickup', index: 'orderPickup', width: 80, align: 'center'},
                    {label: '外送時間', name: 'pickupComplete', index: 'pickupComplete', width: 80, align: 'center'},
                    {label: '外送達成時間', name: 'orderComplete', index: 'orderComplete', width: 80, align: 'center'},
                    {label: '回店所需時間', name: 'completeReturn', index: 'completeReturn', width: 80, align: 'center'},
                    {label: '外出時間', name: 'pickupReturn', index: 'pickupReturn', width: 80, align: 'center'},
                    {label: '完成整張外送時間', name: 'orderReturn', index: 'orderReturn', width: 80, align: 'center'},
                    {label: '<=30 MINS %', name: 'min30Below', index: 'min30Below', width: 80, align: 'center'},
                    {label: '<=40 MINS %', name: 'min30To40', index: 'min30To40', width: 80, align: 'center'},
                    {label: '<=50 MINS %', name: 'min40To50', index: 'min40To50', width: 80, align: 'center'},
                    {label: '<=60 MINS %', name: 'min50To60', index: 'min50To60', width: 80, align: 'center'},
                    {label: '<=90 MINS %', name: 'min90Under', index: 'min90Under', width: 80, align: 'center'},
                    {label: '>90 MINS %', name: 'min90Over', index: 'min90Over', width: 80, align: 'center'},
                    {label: 'SALES', name: 'totalSales', index: 'totalSales', width: 80, align: 'center'},
                    {label: 'TC', name: 'tc', index: 'tc', width: 80, align: 'center'},
                    {label: 'TPLH', name: 'tplh', index: 'tplh', width: 80, align: 'center'},
                    {label: 'SPMH', name: 'spmh', index: 'spmh', width: 80, align: 'center'},
                    {label: '外出總時間', name: 'totalPickupReturn', index: 'totalPickupReturn', width: 80, align: 'center'},
                    {label: '平均外送距離', name: 'avgDistance', width: 80, align: 'center'},
                ],
                width: 'auto',
                height: 700,
                autowidth: true,
                rowNum: 20,
                footerrow: true,
                pager: "#jqGridPager",
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

function excelDownloadByDate(){
    let startDate = $('#day1').val();
    let endDate = $('#day2').val();
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
            loading.hide();
        }
    })
}