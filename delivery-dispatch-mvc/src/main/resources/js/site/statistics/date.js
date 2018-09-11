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
            for (let key in data) {
                if (data.hasOwnProperty(key)) {
                    let tmpdata = new Object();
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
                        tmpdata.tplh = data[key].tplh;
                    }else{
                        tmpdata.tplh = "-";
                    }
                    if(data[key].spmh){
                        tmpdata.spmh = data[key].spmh;
                    }else{
                        tmpdata.spmh = "-";
                    }

                    tmpdata.totalPickupReturn = totalTimeSet(data[key].totalPickupReturn*1000);
                    tmpdata.avgDistance = (data[key].avgDistance?data[key].avgDistance:0) +'km';
                    mydata.push(tmpdata);
                }
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
                    {label: 'STORE', name: 'store', width: 80, align: 'center'},
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
                    {label: '<=90 MINS %', name: 'min60To90', index: 'min60To90', width: 80, align: 'center'},
                    {label: '>90 MINS %', name: 'min90Under', index: 'min90Under', width: 80, align: 'center'},
                    {label: 'SALES', name: 'totalSales', index: 'totalSales', width: 80, align: 'center'},
                    {label: 'TC', name: 'tc', index: 'tc', width: 50, align: 'center'},
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

            $("#jqGrid").jqGrid('destroyGroupHeader');
            $("#jqGrid").jqGrid('setGroupHeaders', {
                useColSpanStyle: true,
                groupHeaders:[
                    {startColumnName: 'orderPickup', numberOfColumns: 6, titleText: '平均時間'},
                    {startColumnName: 'min30Below', numberOfColumns: 6, titleText: 'DELIVERY 達成率'},
                    {startColumnName: 'totalSales', numberOfColumns: 6, titleText: '業績及生產力'}
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
            console.log('good');
            loading.hide();
        },
        failCallback: function(responseHtml,url){
            console.log(responseHtml);
            console.log('err');
            loading.hide();
        }
    })
}