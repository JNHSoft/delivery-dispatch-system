let loading= $('<div id="loading"><div><p style="background-color: #838d96"/></div></div>').appendTo(document.body).hide();
var selectId =$("#statisticsStoreList");
var selectIdOption = $("#statisticsStoreList option:selected");
let beforePeakType = 0;

$(function () {
    let date = $.datepicker.formatDate('yy-mm-dd', new Date);
    $('#startDate, #endDate').val(date);
    getGroupList();                 // 그룹 정보 조회

    /**
     * 20.12.24 DateTime Picker
     * */
    $('#startDate').datetimepicker({
        altField: '#startTime',
        controlType: 'select',
        oneLine: true,
        timeInput: true,
        timeText: set_time,
        closeText: btn_confirm,
        stepMinute: 10,
        maxDate : $('#startDate').val(),
        onClose: function(selectedDate) {
            $('#startDate').datetimepicker('option', 'maxDate', $('#endDate').datetimepicker('getDate'));
            $('#endDate').datetimepicker('option', 'minDate', $('#startDate').datetimepicker('getDate'));
            getStoreStatisticsByDate();
        }
    });

    $('#endDate').datetimepicker({
        altField: "#endTime",
        controlType: 'select',
        oneLine: true,
        timeInput: true,
        timeText: set_time,
        closeText: btn_confirm,
        stepMinute: 10,
        minDate : $('#endDate').val(),
        onClose: function( selectedDate ) {
            $('#startDate').datetimepicker('option', 'maxDate', $('#endDate').datetimepicker('getDate'));
            $('#endDate').datetimepicker('option', 'minDate', $('#startDate').datetimepicker('getDate'));
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

// 21.01.04 피크(Peak) 타임 조회
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

function getStoreStatisticsByDate() {

    let diffDate = Math.ceil((new Date($('#endDate').val()).getTime() - new Date($('#startDate').val()).getTime()) / (1000*3600*24));
    if (diffDate > 31){
        return;
    }

    // 20.12.24 시간 범위도 포함 유무 체크 후 값 보내기
    let chkTime = $('#chkTime').is(":checked");
    let peakTime = $('#chkPeakTime').is(":checked");

    if (chkTime && !peakTime){       // 체크가 되어 있다면 날짜 범위 체크
        let startDT = $('#startDate').datetimepicker('getDate');
        let endDT = $('#endDate').datetimepicker('getDate');

        if ($('#startTime').val() == undefined || $('#startTime').val() == "" || $('#endTime').val() == undefined || $('#endTime').val() == ""){
            return;
        }

        if (startDT.getTime() > endDT.getTime()){
            return;
        }
    }

    let sDate = $('#startDate').val();
    let eDate = $('#endDate').val();

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
            sDate : sDate,
            eDate : eDate,
            chkTime : chkTime,
            chkPeakTime : peakTime,
            peakType : $('#sel_peak_time').val(),
            groupId : $("#statisticsGroupList").val(),
            subgroupId : $("#statisticsSubGroupList").val(),
            storeId : $("#statisticsStoreList").val(),
        },
        dataType: 'json',
        success: function (data) {
            let rowNum = 0;

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

            let errtcSum = 0;
            let thirdtcSum = 0;
            let tcSum = 0;
            let tplhSum = 0;
            let spmhSum = 0;
            let totalPickupReturnSum = 0;
            let avgDistanceSum = 0;
            // 빈값이 들어간 row 갯수
            let rowReduceCnt = 0;
            // 거리값 제외 count
            let distanceCnt = 0;
            // tpsp 제외 count
            let tpSpCnt = 0;
            // Error 개수
            let onlyErrCnt = 0;
            // Third party 개수
            let onlyThirdCnt = 0;
            let tcRowCnt = 0;

            for (let key in data) {
                if (data.hasOwnProperty(key)) {

                    let currentRow = formatInt(data[key].tc);

                    // 빈값 여부를 위해 chkcount

                    let chkCnt = 0;
                    let chkDistanceCnt = 0;
                    let chkTpSpCnt = 0;
                    let chkErrCnt = 0;
                    let chkThirdCnt = 0;

                    let tmpdata = new Object();
                    rowReduceCnt++;
                    distanceCnt++;
                    tpSpCnt++;

                    // 정상 Row 체크
                    if(formatInt(data[key].tc, 1) > 0){
                        tcRowCnt++;
                    }

                    tmpdata.No = ++rowNum;
                    tmpdata.store = data[key].storeName;

                    if (!data[key].groupName || data[key].groupName == ''){
                        tmpdata.group_name = group_none;
                    }else{
                        tmpdata.group_name = data[key].groupName;
                    }

                    if (!data[key].subGroupName || data[key].subGroupName == ''){
                        tmpdata.subGroup_name = group_none;
                    }else{
                        tmpdata.subGroup_name = data[key].subGroupName;
                    }

                    tmpdata.orderPickup = millisecondToTime((data[key].orderPickup / currentRow) *1000);
                    tmpdata.orderPickups = formatInt(data[key].orderPickup);

                    tmpdata.pickupComplete = millisecondToTime((data[key].pickupComplete / currentRow) * 1000);
                    tmpdata.pickupCompletes = formatInt(data[key].pickupComplete);

                    tmpdata.orderComplete = millisecondToTime((data[key].orderComplete / currentRow) * 1000);
                    tmpdata.orderCompletes = formatInt(data[key].orderComplete)

                    tmpdata.completeReturn = millisecondToTime((data[key].completeReturn / currentRow) * 1000);
                    tmpdata.completeReturns = formatInt(data[key].completeReturn);

                    tmpdata.pickupReturn = millisecondToTime((data[key].pickupReturn / currentRow) * 1000);
                    tmpdata.pickupReturns = formatInt(data[key].pickupReturn);

                    tmpdata.orderReturn = millisecondToTime((data[key].orderReturn / currentRow) * 1000);
                    tmpdata.orderReturns = formatInt(data[key].orderReturn);

                    tmpdata.minD7Below = formatFloat(data[key].d7Success / currentRow * 100, 1) + "%";
                    tmpdata.minD7Belows = formatInt(data[key].d7Success);

                    tmpdata.min30Below = formatFloat(data[key].min30Below / currentRow * 100, 1) + "%";
                    tmpdata.min30Belows = formatInt(data[key].min30Below);

                    tmpdata.min30To40 = formatFloat(data[key].min30To40 / currentRow * 100, 2) + "%";
                    tmpdata.min30To40s = formatInt(data[key].min30To40);

                    tmpdata.min40To50 = formatFloat(data[key].min40To50 / currentRow * 100, 1) + "%";
                    tmpdata.min40To50s = formatInt(data[key].min40To50);

                    tmpdata.min50To60 = formatFloat(data[key].min50To60 / currentRow * 100, 1) + "%";
                    tmpdata.min50To60s = formatInt(data[key].min50To60);

                    tmpdata.min60To90 = formatFloat(data[key].min60To90 / currentRow * 100, 1) + "%";
                    tmpdata.min60To90s = formatInt(data[key].min60To90);

                    tmpdata.min90Under = formatFloat(data[key].min90Under / currentRow * 100, 1) + "%";
                    tmpdata.min90Unders = formatInt(data[key].min90Under);

                    tmpdata.totalSales = formatFloat(data[key].totalSales / currentRow, 1);
                    tmpdata.totalSaless = formatInt(data[key].totalSales);

                    if (formatInt(data[key].errtc, 0) > 0){
                        tmpdata.errtc = formatInt(data[key].errtc);
                        chkErrCnt++;
                    }else{
                        tmpdata.errtc = "-"
                    }

                    if (formatInt(data[key].thirdtc, 0) > 0){
                        tmpdata.thirdtc = formatInt(data[key].thirdtc);
                        chkThirdCnt++;
                    }else{
                        tmpdata.thirdtc = "-"
                    }

                    tmpdata.tc = formatInt(data[key].tc, 1);

                    if(data[key].hours){
                        tmpdata.tplh = formatFloat( currentRow / data[key].hours, 2);
                        tmpdata.tplhs = formatFloat(data[key].hours, 2);
                        tplhSum += formatFloat(data[key].hours, 2);

                        tmpdata.spmh = formatFloat(formatInt(data[key].totalSales) / data[key].hours, 2);
                        tmpdata.spmhs = formatFloat(data[key].hours, 2);
                        spmhSum += formatFloat(data[key].hours, 2);
                    } else{
                        tmpdata.tplh = "-";
                        tmpdata.spmh = "-";
                        chkTpSpCnt++;
                    }

                    tmpdata.totalPickupReturn = millisecondToTime(data[key].totalPickupReturn * 1000);
                    tmpdata.totalPickupReturns = formatInt(data[key].totalPickupReturn);

                    tmpdata.avgDistance = formatFloat(data[key].avgDistance / currentRow, 1) +'km';
                    tmpdata.avgDistances = formatFloat(data[key].avgDistance, 1);

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
                        avgDistanceSum += formatFloat(data[key].avgDistance, 1);
                    } else{
                        chkDistanceCnt++;
                    }
                    minD7BelowSum += formatFloat(data[key].d7Success, 1);
                    min30BelowSum += formatFloat(data[key].min30Below, 1);
                    min30To40Sum += formatFloat(data[key].min30To40, 1);
                    min40To50Sum += formatFloat(data[key].min40To50, 1);
                    min50To60Sum += formatFloat(data[key].min50To60, 1);
                    min60To90Sum += formatFloat(data[key].min60To90, 1);
                    min90UnderSum += formatFloat(data[key].min90Under, 1);
                    totalSalesSum += formatFloat(data[key].totalSales, 1);

                    errtcSum += formatInt(data[key].errtc);
                    thirdtcSum += formatInt(data[key].thirdtc);
                    tcSum += formatInt(data[key].tc);

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

                    // 정상 개수가 없는 경우 에러와 제 3자를 제외
                    if (chkErrCnt != 0){
                        onlyErrCnt++;
                    }

                    if (chkThirdCnt != 0){
                        onlyThirdCnt++;
                    }
                }
            }

            // 평균과 관련한 값을 생성하기 위함
            fillteringData(mydata);

            // 평균 값
            let avgData = new Object();
            avgData.No = ++rowNum;
            avgData.store = "Average" ;

            avgData.group_name = "";
            avgData.subGroup_name = "";

            avgData.orderPickup = millisecondToTime(orderPickupSum / tcSum * 1000);
            avgData.pickupComplete = millisecondToTime(pickupCompleteSum / tcSum * 1000);
            avgData.orderComplete  = millisecondToTime(orderCompleteSum / tcSum * 1000);
            avgData.completeReturn = millisecondToTime(completeReturnSum / tcSum *1000);
            avgData.pickupReturn =  millisecondToTime(pickupReturnSum / tcSum * 1000);
            avgData.orderReturn =   millisecondToTime(orderReturnSum / tcSum * 1000);
            avgData.minD7Below = formatFloat((minD7BelowSum / tcSum) * 100, 1) + "%";
            avgData.min30Below = formatFloat((min30BelowSum / tcSum) * 100, 1) +"%";
            avgData.min30To40 =formatFloat((min30To40Sum / tcSum) * 100, 1) +"%";
            avgData.min40To50 = formatFloat((min40To50Sum / tcSum) * 100, 1) +"%";
            avgData.min50To60 = formatFloat((min50To60Sum / tcSum) * 100, 1) +"%";
            avgData.min60To90 = formatFloat((min60To90Sum / tcSum) * 100, 1) +"%";
            avgData.min90Under = formatFloat((min90UnderSum / tcSum) * 100, 1) +"%";
            avgData.totalSales = formatFloat((totalSalesSum / tcSum), 1);
            avgData.errtc = formatInt(errtcSum / onlyErrCnt);
            avgData.thirdtc = formatInt(thirdtcSum / onlyThirdCnt);
            avgData.tc = formatInt(tcSum / tcRowCnt);

            if(tpSpCnt!=0){
                avgData.tplh = formatFloat(tcSum / tplhSum, 2);
                avgData.spmh = formatFloat(totalSalesSum /spmhSum, 2);
            }else{
                avgData.tplh = "-";
                avgData.spmh = "-";
            }

            avgData.totalPickupReturn = secondsToTime(totalPickupReturnSum / tcRowCnt);
            avgData.avgDistance = formatFloat(avgDistanceSum / tcSum, 1) +'km';

            mydata.push(avgData);

            // RC나 AC에 따른 마지막 데이터 추가
            if ($("#statisticsGroupList option:selected").val() === "reset"){
                for (const g of rcGroup) {
                    let gData = new Object();
                    gData.No = ++rowNum;
                    gData.store = g[0] + " AVERAGE";

                    //console.log('g data => ', g, 'dddd', g[0]);
                    mydata.push(gData);
                }
            }else if ($("#statisticsGroupList option:selected").val() !== "reset" && $("#statisticsSubGroupList option:selected").val() === "reset"){
                for (const ag of acGroup) {
                    let agData = new Object();
                    agData.No = ++rowNum;
                    agData.store = ag[0] + " AVERAGE";

                    mydata.push(agData);
                }
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
                jQuery('#jqGrid').trigger('reloadGrid');
            }

            $("#jqGrid").trigger("reloadGrid");

            $("#jqGrid").jqGrid({
                datatype: "local",
                data: mydata,
                colModel: [
                    {label: 'No', name: 'No', width: 25, key: true, align: 'center', hidden: true},
                    {label: label_store, name: 'store', width: 80, align: 'center'},
                    {label: group_name, name: 'group_name', width: 80, align: 'center'},
                    {label: subGroup_name, name: 'subGroup_name', width: 80, align: 'center'},

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
                    {label: label_third_party, name: 'thirdtc', index: 'thirdtc', width: 50, align: 'center'},
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
                loadComplete: function (){
                    let ids = $("#jqGrid").getDataIDs();

                    $.each(ids, function (idx, rowId){
                        let objRowData = $("#jqGrid").getRowData(rowId);

                        if ($("#statisticsGroupList option:selected").val() === "reset"){
                            for (const rc of rcGroup) {
                                if (objRowData.store === rc[0] + " AVERAGE"){
                                    let rcData = rcGroup.get(rc[0]);

                                    if (rcData.length > 0){
                                        let orderCount = rcData.reduce((sum, obj) => formatInt(sum) + formatInt(obj.tc), 0);
                                        let rowCount = formatInt(rcData.reduce((sum, obj) => formatInt(sum) + (formatInt(obj.tc) > 0 ? 1 : 0), 0));
                                        let errCount = rcData.reduce((sum, obj) => formatInt(sum) + (formatInt(obj.errtc) > 0 ? 1 : 0), 0);
                                        let thirdCount = rcData.reduce((sum, obj) => formatInt(sum) + (formatInt(obj.thirdtc) > 0 ? 1 : 0), 0);



                                        objRowData.orderPickup = secondsToTime(formatInt(rcData.reduce((sum, obj) => formatInt(sum) + formatInt(obj.orderPickups), 0) / orderCount));
                                        objRowData.pickupComplete = secondsToTime(formatInt(rcData.reduce((sum, obj) => formatInt(sum) + formatInt(obj.pickupCompletes), 0) / orderCount));
                                        objRowData.orderComplete  = secondsToTime(formatInt(rcData.reduce((sum, obj) => formatInt(sum) + formatInt(obj.orderCompletes), 0) / orderCount));
                                        objRowData.completeReturn = secondsToTime(formatInt(rcData.reduce((sum, obj) => formatInt(sum) + formatInt(obj.completeReturns), 0) / orderCount));
                                        objRowData.pickupReturn =  secondsToTime(formatInt(rcData.reduce((sum, obj) => formatInt(sum) + formatInt(obj.pickupReturns), 0) / orderCount));
                                        objRowData.orderReturn =   secondsToTime(formatInt(rcData.reduce((sum, obj) => formatInt(sum) + formatInt(obj.orderReturns), 0) / orderCount));
                                        objRowData.minD7Below = formatFloat(rcData.reduce((sum, obj) => formatInt(sum) + formatInt(obj.minD7Belows), 0) / orderCount * 100, 1) + "%";
                                        objRowData.min30Below = formatFloat(rcData.reduce((sum, obj) => formatInt(sum) + formatInt(obj.min30Belows), 0) / orderCount * 100, 1) +"%";
                                        objRowData.min30To40 =formatFloat(rcData.reduce((sum, obj) => formatInt(sum) + formatInt(obj.min30To40s), 0) / orderCount * 100, 1) +"%";
                                        objRowData.min40To50 = formatFloat(rcData.reduce((sum, obj) => formatInt(sum) + formatInt(obj.min40To50s), 0) / orderCount * 100, 1) +"%";
                                        objRowData.min50To60 = formatFloat(rcData.reduce((sum, obj) => formatInt(sum) + formatInt(obj.min50To60s), 0) / orderCount * 100, 1) +"%";
                                        objRowData.min60To90 = formatFloat(rcData.reduce((sum, obj) => formatInt(sum) + formatInt(obj.min60To90s), 0) / orderCount * 100, 1) +"%";
                                        objRowData.min90Under = formatFloat(rcData.reduce((sum, obj) => formatInt(sum) + formatInt(obj.min90Unders), 0) / orderCount * 100, 1) +"%";
                                        objRowData.totalSales = formatFloat(rcData.reduce((sum, obj) => formatInt(sum) + formatInt(obj.totalSaless), 0) / orderCount, 1);
                                        objRowData.errtc = formatInt(rcData.reduce((sum, obj) => formatInt(sum) + formatInt(obj.errtc), 0) / errCount);
                                        objRowData.thirdtc = formatInt(rcData.reduce((sum, obj) => formatInt(sum) + formatInt(obj.thirdtc), 0) / thirdCount);
                                        objRowData.tc = formatInt(orderCount / rowCount);
                                        objRowData.tplh = formatFloat(orderCount / formatFloat(rcData.reduce((sum, obj) => formatFloat(sum, 2) + formatFloat(obj.tplhs, 2), 0), 2), 2);
                                        objRowData.spmh = formatFloat(rcData.reduce((sum, obj) => formatInt(sum) + formatInt(obj.totalSaless), 0) /formatFloat(rcData.reduce((sum, obj) => formatFloat(sum, 2) + formatFloat(obj.spmhs, 2), 0), 2), 2);
                                        objRowData.totalPickupReturn = secondsToTime(rcData.reduce((sum, obj) => formatInt(sum) + formatInt(obj.totalPickupReturns), 0) / rowCount);
                                        objRowData.avgDistance = formatFloat(rcData.reduce((sum, obj) => formatFloat(sum, 2) + formatFloat(obj.avgDistances, 2), 0) / orderCount, 1) +'km';

                                        $("#jqGrid").setRowData(rowId, objRowData);
                                    }else{
                                        $("#jqGrid").delRowData(rowId);
                                    }
                                }
                            }
                        }else if ($("#statisticsGroupList option:selected").val() !== "reset" && $("#statisticsSubGroupList option:selected").val() === "reset"){
                            for (const ac of acGroup) {
                                if (objRowData.store === ac[0] + " AVERAGE"){
                                    let acData = acGroup.get(ac[0]);

                                    if (acData.length > 0){
                                        let orderCount = acData.reduce((sum, obj) => formatInt(sum) + formatInt(obj.tc), 0);
                                        let rowCount = formatInt(acData.reduce((sum, obj) => formatInt(sum) + (formatInt(obj.tc) > 0 ? 1 : 0), 0));
                                        let errCount = acData.reduce((sum, obj) => formatInt(sum) + (formatInt(obj.errtc) > 0 ? 1 : 0), 0);
                                        let thirdCount = acData.reduce((sum, obj) => formatInt(sum) + (formatInt(obj.thirdtc) > 0 ? 1 : 0), 0);



                                        objRowData.orderPickup = secondsToTime(formatInt(acData.reduce((sum, obj) => formatInt(sum) + formatInt(obj.orderPickups), 0) / orderCount));
                                        objRowData.pickupComplete = secondsToTime(formatInt(acData.reduce((sum, obj) => formatInt(sum) + formatInt(obj.pickupCompletes), 0) / orderCount));
                                        objRowData.orderComplete  = secondsToTime(formatInt(acData.reduce((sum, obj) => formatInt(sum) + formatInt(obj.orderCompletes), 0) / orderCount));
                                        objRowData.completeReturn = secondsToTime(formatInt(acData.reduce((sum, obj) => formatInt(sum) + formatInt(obj.completeReturns), 0) / orderCount));
                                        objRowData.pickupReturn =  secondsToTime(formatInt(acData.reduce((sum, obj) => formatInt(sum) + formatInt(obj.pickupReturns), 0) / orderCount));
                                        objRowData.orderReturn =   secondsToTime(formatInt(acData.reduce((sum, obj) => formatInt(sum) + formatInt(obj.orderReturns), 0) / orderCount));
                                        objRowData.minD7Below = formatFloat(acData.reduce((sum, obj) => formatInt(sum) + formatInt(obj.minD7Belows), 0) / orderCount * 100, 1) + "%";
                                        objRowData.min30Below = formatFloat(acData.reduce((sum, obj) => formatInt(sum) + formatInt(obj.min30Belows), 0) / orderCount * 100, 1) +"%";
                                        objRowData.min30To40 =formatFloat(acData.reduce((sum, obj) => formatInt(sum) + formatInt(obj.min30To40s), 0) / orderCount * 100, 1) +"%";
                                        objRowData.min40To50 = formatFloat(acData.reduce((sum, obj) => formatInt(sum) + formatInt(obj.min40To50s), 0) / orderCount * 100, 1) +"%";
                                        objRowData.min50To60 = formatFloat(acData.reduce((sum, obj) => formatInt(sum) + formatInt(obj.min50To60s), 0) / orderCount * 100, 1) +"%";
                                        objRowData.min60To90 = formatFloat(acData.reduce((sum, obj) => formatInt(sum) + formatInt(obj.min60To90s), 0) / orderCount * 100, 1) +"%";
                                        objRowData.min90Under = formatFloat(acData.reduce((sum, obj) => formatInt(sum) + formatInt(obj.min90Unders), 0) / orderCount * 100, 1) +"%";
                                        objRowData.totalSales = formatFloat(acData.reduce((sum, obj) => formatInt(sum) + formatInt(obj.totalSaless), 0) / orderCount, 1);
                                        objRowData.errtc = formatInt(acData.reduce((sum, obj) => formatInt(sum) + formatInt(obj.errtc), 0) / errCount);
                                        objRowData.thirdtc = formatInt(acData.reduce((sum, obj) => formatInt(sum) + formatInt(obj.thirdtc), 0) / thirdCount);
                                        objRowData.tc = formatInt(orderCount / rowCount);
                                        objRowData.tplh = formatFloat(orderCount / formatFloat(acData.reduce((sum, obj) => formatFloat(sum, 2) + formatFloat(obj.tplhs, 2), 0), 2), 2);
                                        objRowData.spmh = formatFloat(acData.reduce((sum, obj) => formatInt(sum) + formatInt(obj.totalSaless), 0) /formatFloat(acData.reduce((sum, obj) => formatFloat(sum, 2) + formatFloat(obj.spmhs, 2), 0), 2), 2);
                                        objRowData.totalPickupReturn = secondsToTime(acData.reduce((sum, obj) => formatInt(sum) + formatInt(obj.totalPickupReturns), 0) / rowCount);
                                        objRowData.avgDistance = formatFloat(acData.reduce((sum, obj) => formatFloat(sum, 2) + formatFloat(obj.avgDistances, 2), 0) / orderCount, 1) +'km';

                                        $("#jqGrid").setRowData(rowId, objRowData);
                                    }else{
                                        $("#jqGrid").delRowData(rowId);
                                    }
                                }
                            }
                        }
                    });

                }
            });

            $("#jqGrid").jqGrid('destroyGroupHeader');
            $("#jqGrid").jqGrid('setGroupHeaders', {
                useColSpanStyle: true,
                groupHeaders:[
                    {startColumnName: 'orderPickup', numberOfColumns: 6, titleText: label_average_time},
                    {startColumnName: 'minD7Below', numberOfColumns: 7, titleText: label_percent_completed},
                    {startColumnName: 'totalSales', numberOfColumns: 8, titleText: label_productivity}
                ]
            });

            resizeJqGrid('#jqGrid'); //그리드 리사이즈
            loading.hide();
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
    let startDate = $('#startDate').val();
    let endDate = $('#endDate').val();

    let diffDate = Math.ceil((new Date(endDate).getTime() - new Date(startDate).getTime()) / (1000*3600*24));
    if (diffDate > 31){
        return;
    }

    // 20.12.24 시간 범위도 포함 유무 체크 후 값 보내기
    let chkTime = $('#chkTime').is(":checked");
    let peakTime = $('#chkPeakTime').is(":checked");

    if (chkTime && !peakTime){       // 체크가 되어 있다면 날짜 범위 체크
        let startDT = $('#startDate').datetimepicker('getDate');
        let endDT = $('#endDate').datetimepicker('getDate');

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
            sDate : startDate,
            eDate : endDate,
            chkTime : chkTime,
            chkPeakTime : peakTime,
            peakType : $('#sel_peak_time').val(),
            groupId : $("#statisticsGroupList").val(),
            subgroupId : $("#statisticsSubGroupList").val(),
            storeId : $("#statisticsStoreList").val(),
        },
        successCallback: function(url){
            loading.hide();
        },
        failCallback: function(responseHtml,url){
            loading.hide();
        }
    })
}

function fillteringData(objData){
    let groupOption = $("#statisticsGroupList")[0].options;
    rcGroup = new Map();

    // RC 기준의 Row 데이터 저장
    for (const g of groupOption) {
        if (g.value !== "reset"){
            rcGroup.set(g.text, filterJoin1(objData, g.text));
        }
    }

    let subGroupOption = $("#statisticsSubGroupList")[0].options;
    acGroup = new Map();

    // AC 기준의 Row 데이터 저장
    for (const sg of subGroupOption) {
        if (sg.value !== "reset"){
            acGroup.set(sg.text, filterJoin2(objData, sg.text));
        }
    }
}

// 필터 조건 1 RC 기준
function filterJoin1(item, conditional){
    return item.filter((obj) => {
        return obj.group_name.toUpperCase() === conditional.toUpperCase();
    });
}

// 필터 조건 2 AC 기준
function filterJoin2(item, conditional){
    return item.filter((obj) => {
        return obj.subGroup_name.split('-')[0].toUpperCase() === conditional.toUpperCase();
    });
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

                $("#statisticsGroupList").off().on("change", function () {
                    getStatisticsSubGroupList($("#statisticsGroupList option:selected").val());
                    selectId = $(this);
                    selectIdOption = $('option:selected', this);
                    searchList(selectId, selectIdOption);
                    getStoreStatisticsByDate();
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
                    pstatisticsSubGroupListHtml += "<option value='" + data[i].name  + "'>" + data[i].name + "</option>";
                }
                $("#statisticsSubGroupList").html(pstatisticsSubGroupListHtml);

                $("#statisticsSubGroupList").off().on("change", function () {
                    getStatisticsStoreList($("#statisticsSubGroupList option:selected").val(),$("#statisticsGroupList option:selected").val());
                    selectId = $(this);
                    selectIdOption = $('option:selected', this);
                    searchList(selectId, selectIdOption);
                    getStoreStatisticsByDate();
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
            subGroupName : selecSubGroupId
        },
        async : false,
        dataType : 'json',
        success : function(data){
            if(data) {
                var statisticsStoreListHtml = "<option value='reset'>" + list_search_all_store + "</option>";
                for (var i in data){
                    statisticsStoreListHtml += "<option value='" + data[i].storeId  + "'>" + data[i].storeName + "</option>";
                }
                $("#statisticsStoreList").html(statisticsStoreListHtml);

                $("#statisticsStoreList").off().on("change", function () {
                    selectId = $(this);
                    selectIdOption = $('option:selected', this);
                    searchList(selectId, selectIdOption);
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
}