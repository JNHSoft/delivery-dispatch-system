/*<![CDATA[*/
let loading = $('<div id="loading"><div><p style="background-color: #838d96"/></div></div>').appendTo(document.body).hide();
let selectId = $("#statisticsStoreList");
let selectIdOption = $("#statisticsStoreList option:selected");
let intervalTime = 10;
let objChart;

/**
 * 페이지 진입 시 처음 실행
 * */
$(function(){
    console.log("대시보드 페이지 오픈");
    loading.show();

    let date = $.datepicker.formatDate('yy-mm-dd', new Date);
    $('#startDate, #endDate').val(date);

    makeEventBind();
    getGroupList();

    // D30 페이지인 경우, 배정 ~ 도착의 시간 구분별에 대한 Excel을 다운로드 할 수 있도록 버튼 생성
    if (second_column == "D30"){
        $("#btnExcel").before('<button class="btn-excel" style="margin-right: 5px;" id="btnTimeSectionExcel">' + 'TimeSection Excel' + '</button>');
        $("#btnTimeSectionExcel").off().on('click', function (){
            // 정보들을 모두 설정한다.
            let startDate = $('#startDate').val();
            let endDate = $('#endDate').val();

            let diffDate = Math.ceil((new Date(endDate).getTime() - new Date(startDate).getTime()) / (1000*3600*24));
            if (diffDate > 31){
                return;
            }

            $.fileDownload("/downloadTimeSectionExcel", {
                httpMethod: "POST",
                data: {
                    groupId: $("#statisticsGroupList option:selected").val(),
                    subgroupId: $("#statisticsSubGroupList option:selected").val(),
                    storeId: $("#statisticsStoreList option:selected").val(),
                    sDate: startDate,
                    eDate: endDate,
                    peakType: $("#sel_peak_time").val(),
                },
                successCallback: function (url){

                }, failCallback: function (responseHtml, url, err){

                }
            });
        });
    }

    // 검색 조건 세팅
    if (localStorage.getItem("keepDashboard") === "Y"){
        let objSearch = JSON.parse(localStorage.getItem("objSearch"));

        localStorage.removeItem("objSearch");

        // obejct 위치에 맞게 데이터 세팅
        $("#startDate").val(objSearch.sDate);
        $("#endDate").val(objSearch.eDate);
        $("#sel_peak_time").val(objSearch.peakType);

        // 그룹
        $("#statisticsGroupList").val(objSearch.groupId);
        getStatisticsSubGroupList(objSearch.groupId);

        // 하위 그룹
        $("#statisticsSubGroupList").val(objSearch.subgroupId);
        getStatisticsStoreList(objSearch.subgroupId, objSearch.groupId);

        // 스토어
        $("#statisticsStoreList").val(objSearch.storeId);

        $(".search-date").removeClass("on");
        if (objSearch.toDay){
            $("#btnToday").addClass("on");
        }else if(objSearch.lastSeven){
            $("#btnLastSeven").addClass("on");
        }else if(objSearch.cMonth){
            $("#btnCurrentMonth").addClass("on");
        }

        $("#btnTotalTime").removeClass("on");
        if(objSearch.btnTotal){
            $("#btnTotalTime").addClass("on");
        }

        localStorage.removeItem("keepDashboard");
    }

    setTimeout(() => {
        getDashBoardDetail()
    }, intervalTime);
});


/**
 * Object에 대한 Event 바인딩
 * */
function makeEventBind(){
    // 픽업 타임에 대한 이벤트
    $("#sel_peak_time").off().on('change', function (){
        loading.show();

        setTimeout(() => {
            if ($('option:selected', this).val() === "0"){
                $("#btnTotalTime").removeClass('on');
                $("#btnTotalTime").addClass('on');
            }else{
                $("#btnTotalTime").removeClass('on');
            }

            searchList();
        }, intervalTime);
    });

    // 전체 시간에 대한 이벤트
    $("#btnTotalTime").off().on('click', function (){
        // total 기능은 off가 되어 있을 때만 작동하도록 한다.
        if(!($(this).hasClass('on'))){
            loading.show();
            setTimeout(() => {
                $(this).addClass('on');
                $("#sel_peak_time").val("0");
                searchList();
            }, intervalTime);
        }
    });

    $(".search-date").off().on('click', function(){
        // 클릭한 객체가 이미 클릭 상태로 되어 있는 경우 이벤트를 넘긴다.
        if ($(this).hasClass('on')){
            return;
        }

        loading.show();
        setTimeout(() => {
            // 오늘일자, 시작일자, 종료일자를 변수에 담아 놓는다.
            let today = new Date($.datepicker.formatDate('yy-mm-dd', new Date));
            let startDate = new Date($("#startDate").val());
            let endDate = new Date($("#endDate").val());

            // 현재 클래스 중 on 항목에 대한 내용 제거
            $(".search-date").removeClass('on');
            // 현재 클릭 이벤트가 발생된 곳에 on Class 추가
            $(this).addClass('on');

            console.log("value = > ", $(this).val());

            // 클릭된 항목에 따라, 이벤트가 진행되도록 함.
            switch ($(this).val()){
                case "today":
                    // 조회 시작일과 종요일을 오늘 날짜로 세팅한다.
                    $("#startDate, #endDate").val($.datepicker.formatDate('yy-mm-dd', today));
                    break;
                case "lseven":
                    // 1. 시작일로 부터 당일까지 7일 미만인 경우, 종료일을 기준으로 7일 전 데이터로 보여준다.
                    if (today.getTime() - startDate.getTime() < 6 * 24 * 3600 * 1000){
                        let beforeSeven = endDate;

                        beforeSeven.setDate(endDate.getDate() - 6);

                        $("#startDate").val($.datepicker.formatDate('yy-mm-dd', beforeSeven));
                        console.log("No1.");
                    }
                    // 2. 시작일 ~ 종료일 차이가 7일 차이가 아니라면, 시작일 ~ 당일까지의 차이가 7일 초과인 경우에는 시작일을 기준으로 7일을 보여준다.
                    else if (((startDate.getTime() - endDate.getTime() < 6 * 24 * 3600 * 1000) || (startDate.getTime() - endDate.getTime() > 6 * 24 * 3600 * 1000))){
                        let beforeSeven;
                        let changeObj;

                        if ( (today.getTime() - startDate.getTime() < 6 * 24 * 3600 * 1000)){
                            // 시작일 ~ 오늘까지의 차이가 7일 이상인 경우, 종료일을 기준으로 계산 # 변화는 StartDate가
                            beforeSeven = endDate;
                            changeObj = $("startDate");

                            beforeSeven.setDate(endDate.getDate() - 6);
                            console.log("No2.");
                        }else {
                            // 위 조건 이외에는 종료일자 기준으로 7일 계산 # 변화는 EndDate가
                            beforeSeven = startDate;
                            changeObj = $("#endDate");

                            beforeSeven.setDate(startDate.getDate() + 6);
                            console.log("No3.");
                        }

                        changeObj.val($.datepicker.formatDate('yy-mm-dd', beforeSeven));
                    }
                    // 위 2가지 조건이 모두 불충분할 때
                    else{
                        let beforeSeven = endDate;

                        beforeSeven.setDate(endDate.getDate() - 6);

                        $("#startDate").val($.datepicker.formatDate('yy-mm-dd', beforeSeven));
                        console.log("No4.");
                    }

                    break;
                case "cmonth":          // 이번달의 데이터이므로, today 기준의 달로 변경한다.
                    let monthFirstDay = new Date(today.getFullYear(), today.getMonth(), 1);

                    $("#endDate").val($.datepicker.formatDate('yy-mm-dd', today));
                    $("#startDate").val($.datepicker.formatDate('yy-mm-dd', monthFirstDay));

                    break;
            }

            // 날짜 지정이 완료된 후 데이터 갱신
            searchList();
        }, intervalTime);
    });

    // 달력의 시작일자에 대한 이벤트
    $("#startDate").off().on('change', function (){
        loading.show();
        setTimeout(() => {
            let today = $.datepicker.formatDate('yy-mm-dd', new Date);
            let startDate = $(this).val();
            let endDate = $("#endDate").val();

            console.log("start Date 변경 이벤트 발생");

            // 오늘보다 큰 경우, 오늘 일자로 강제 변경
            if (today < startDate){
                $(this).val(today);
                $("#endDate").val(today);
            }
            // 시작일이 종료일보다 큰 경우 종료일을 시작일로 변경
            else if (startDate > endDate){
                $("#endDate").val(startDate);
            }

            // 날짜 범위를 체크하여 today, last 7 days, current Month 를 초기화 하거나 선택적으로 on을 시킨다.
            $(".search-date").removeClass('on');
            let nowDate = new Date(today);
            let sDate = new Date($("#startDate").val());
            let eDate = new Date($("#endDate").val());
            let cMonthFirstDay = new Date(nowDate.getFullYear(), nowDate.getMonth(), 1, 9);

            if (sDate.getTime() === eDate.getTime() && sDate.getTime() === nowDate.getTime()){
                $("#btnToday").addClass('on');
            }else if (eDate.getTime() - sDate.getTime() === 6 * 24 * 3600 * 1000){
                $("#btnLastSeven").addClass('on');
            }else if (sDate.getTime() === cMonthFirstDay.getTime() && eDate.getTime() === nowDate.getTime()){
                $("#btnCurrentMonth").addClass('on');
            }
            searchList();
        }, intervalTime);
    });

    // 달력의 종료일자에 대한 이벤트
    $("#endDate").off().on('change', function (){
        loading.show();
        setTimeout(() => {
            let today = $.datepicker.formatDate('yy-mm-dd', new Date);
            let startDate = $("#startDate").val();
            let endDate = $(this).val();

            console.log("end Date 변경 이벤트 발생");

            // 오늘보다 큰 경우, 오늘 일자로 강제 변경
            if (today < endDate){
                $(this).val(today);
                //$("#startDate").val(today);
            }
            // 종료일이 시작일보다 작은 경우 시작일을 종료일 기간으로 맞춘다.
            else if (startDate > endDate){
                $("#startDate").val(endDate);
            }

            // 날짜 범위를 체크하여 today, last 7 days, current Month 를 초기화 하거나 선택적으로 on을 시킨다.
            $(".search-date").removeClass('on');
            let nowDate = new Date(today);
            let sDate = new Date($("#startDate").val());
            let eDate = new Date($("#endDate").val());
            let cMonthFirstDay = new Date(nowDate.getFullYear(), nowDate.getMonth(), 1, 9);

            if (sDate.getTime() === eDate.getTime() && sDate.getTime() === nowDate.getTime()){
                $("#btnToday").addClass('on');
            }else if (eDate.getTime() - sDate.getTime() === 6 * 24 * 3600 * 1000){
                $("#btnLastSeven").addClass('on');
            }else if (sDate.getTime() === cMonthFirstDay.getTime() && eDate.getTime() === nowDate.getTime()){
                $("#btnCurrentMonth").addClass('on');
            }

            searchList();
        }, intervalTime);
    });

    // 엑셀 다운로드 이벤트
    $("#btnExcel").off().on('click', function (){
        loading.show();
        setTimeout(() => {
            dashBoardDetailDownloadExcel();
            loading.hide();
        }, intervalTime);
    });
}

/**
 * 대시 보드에서 보여줄 모든 정보를 호출한다.
 * */
function getDashBoardDetail(){
    // 정보들을 모두 설정한다.
    let startDate = $('#startDate').val();
    let endDate = $('#endDate').val();
    let ctx = document.getElementById('myChart').getContext('2d');

    let diffDate = Math.ceil((new Date(endDate).getTime() - new Date(startDate).getTime()) / (1000*3600*24));
    if (diffDate > 31){
        loading.hide();
        alert(search_over_days);
        return;
    }

    console.log(objChart);
    if (objChart !== undefined){
        objChart.destroy();
    }
    $(".no_chart_wrap").remove();
    $("#storeRankDetail").empty();

    $.ajax({
        url : this.url,
        type : 'post',
        data : {
            groupId: $("#statisticsGroupList option:selected").val(),
            subgroupId: $("#statisticsSubGroupList option:selected").val(),
            storeId: $("#statisticsStoreList option:selected").val(),
            sDate: startDate,
            eDate: endDate,
            peakType: $("#sel_peak_time").val(),
        },
        async : false,
        dataType : 'json',
        success : function(data) {
            console.log(data);

            if (data != undefined && data["status"] === "OK"){
                if (data["chartInfo"] != undefined){
                    let config = null;

                    if (data["chartInfo"].chartType === 1){
                        // Line 차트
                        config = makeLineChart(data["chartInfo"]);
                    }else{
                        // 기본적으로 Bar 차트를 보여준다.
                        config = makeBarChart(data["chartInfo"]);
                    }

                    objChart = new Chart(ctx, config);
                    
                    // Store Ranking 등록
                    if (data["rankInfo"] != undefined){
                        $("#storeRankDetail").append(makeTableRow(data["rankInfo"]));
                    }
                }else {
                    $("#chart_content").append(`<div class="no_chart_wrap" style="line-height: 150px;">${result_none}</div>`);
                }
            }else {
                $("#chart_content").append(`<div class="no_chart_wrap" style="line-height: 150px;">${result_none}</div>`);
            }
        },
        error: function (err){
            $("#chart_content").append(`<div class="no_chart_wrap" style="line-height: 150px;">${result_none}</div>`);
        },
        complete: function (){
            console.log("완료");
            loading.hide();
        }
    });
}

// Bar 차트 만들기
function makeBarChart(objData){
    let config = Object;
    let bodyData = Object;
    let frankI = 0;

    let arrLabel = [];
    let arrData = [];
    let arrColor = [];

    let startDate = new Date(objData.minX);
    let endDate = new Date(objData.maxX);
    let diffDate = Math.ceil((new Date(endDate).getTime() - new Date(startDate).getTime()) / (1000*3600*24)) + 1;

    startDate.setDate(startDate.getDate() - 1);

    for (let i = 0; i < diffDate; i++) {
        startDate.setDate(startDate.getDate() + 1);
        let addDate = $.datepicker.formatDate('yy-mm-dd', startDate);

        arrLabel.push(addDate);
        arrColor.push('rgb(107, 87, 236)');
        
        // Row 데이터 추가
        if (objData.detail[frankI].hasOwnProperty('createdDatetime')){
            if (objData.detail[frankI].createdDatetime === addDate){
                arrData.push(objData.detail[frankI].mainValue);
                frankI++;
            }else {
                arrData.push(0);
            }
        }
    }

    bodyData = {
        labels: arrLabel,
        datasets:[{
            label: '',
            data: arrData,
            backgroundColor: arrColor,
            borderWidth: 0,
            borderRadius: 4,
            barThickness: 28,
        }]
    };

    config = {
        plugins: [ChartDataLabels],
        type: 'bar',
        data: bodyData,
        options: {
            plugins: {
                legend:{
                    display: false,
                },
                datalabels: {
                    align: 'end',
                    anchor: 'end',
                    color: '#6b57ec',
                    padding: 8,
                    formatter: function (val, cont){
                        return formatFloat(val, 2, true) + '%';
                    }
                }
            },
            scales: {
                x: {
                    grid:{
                        display: false,
                    }
                },
                y: {
                   beginAtZero: true,
                   min: objData.minY,
                   max: objData.maxY,
                   ticks: {
                       count: objData.intervalY,
                       callback: function (val, index, values){
                           return formatFloat(val, 2, true) + '%';
                       }
                   }
                }
            }
        },
    };

    return config;
}

// Line 차트 만들기
function makeLineChart(objData){
    let config = Object;
    let bodyData = Object;
    let frankI = 0;

    let arrLabel = [];
    let arrData = [];

    let startDate = new Date(objData.minX);
    let endDate = new Date(objData.maxX);
    let diffDate = Math.ceil((new Date(endDate).getTime() - new Date(startDate).getTime()) / (1000*3600*24)) + 1;

    startDate.setDate(startDate.getDate() - 1);

    // 하루치인 경우에는 앞 뒤로 1일씩 추가하여, 중앙에 표시 될 수 있도록 적용한다.
    if (diffDate == 1){
        let addDate = $.datepicker.formatDate('yy-mm-dd', startDate);

        arrLabel.push(addDate);
        arrData.push(NaN);
    }

    for (let i = 0; i < diffDate; i++) {
        startDate.setDate(startDate.getDate() + 1);
        let addDate = $.datepicker.formatDate('yy-mm-dd', startDate);

        arrLabel.push(addDate);

        // Row 데이터 추가
        if (objData.detail[frankI].hasOwnProperty('createdDatetime')){
            if (objData.detail[frankI].createdDatetime === addDate){
                arrData.push(objData.detail[frankI].mainValue);
                frankI++;
            }else {
                arrData.push(NaN);
            }
        }
    }

    // 하루치인 경우에는 앞 뒤로 1일씩 추가하여, 중앙에 표시 될 수 있도록 적용한다.
    if (diffDate == 1){
        startDate.setDate(startDate.getDate() + 1);
        let addDate = $.datepicker.formatDate('yy-mm-dd', startDate);

        arrLabel.push(addDate);
        arrData.push(NaN);
    }

    bodyData = {
        labels: arrLabel,
        datasets:[{
            label: '',
            data: arrData,
            borderColor: '#6b57ec',
            datalabels: {
                align: 'end',
                anchor: 'end'
            }
        }]
    };

    config = {
        plugins: [ChartDataLabels],
        type: 'line',
        data: bodyData,
        options: {
            plugins: {
                legend: {
                    display: false,
                },
                datalabels: {
                    color: '#6b57ec',
                    borderRadius: 1,
                    padding: 8,
                    formatter: function (val, cont){
                        return formatFloat(val, 2, true);
                    }
                }
            },
            scales: {
                x: {
                    grid: {
                        display: false,
                    }
                },
                y: {
                    beginAtZero: true,
                    min: objData.minY,
                    max: objData.maxY,
                    ticks: {
                        count: objData.intervalY,
                        callback: function (val, index, values){
                            return formatFloat(val, 2, true);
                        }
                    }
                }
            }
        },
    };

    return config;
}

// 테이블 내용 세팅
function makeTableRow(objData){
    let resultHtml = "";
    let headerHtml = "";
    let colHeadHtml = "";
    let bFirst = true;

    $("#colHeader").empty();
    $("#rankHeader").empty();


    headerHtml += "<th>";
    headerHtml += first_column;
    headerHtml += "</th>";
    headerHtml += "<th>";
    headerHtml += second_column;
    headerHtml += "</th>";

    colHeadHtml += "<col width='28%' />";
    colHeadHtml += "<col width='*' />";

    for (let key in objData) {
        if (objData.hasOwnProperty(key)){

            resultHtml += "<tr>";
            resultHtml += "<td>";
            resultHtml += objData[key].storeName;
            resultHtml += "</td>";
            resultHtml += "<td>";
            resultHtml += formatFloat(objData[key].value, 2, true);
            resultHtml += "</td>";

            if (objData[key].hasOwnProperty("achievementRate")){
                resultHtml += "<td>";
                resultHtml += formatFloat(objData[key].achievementRate, 2, true) + '%';
                resultHtml += "</td>";

                if (bFirst){
                    headerHtml += "<th>";
                    headerHtml += third_column;
                    headerHtml += "</th>";
                    colHeadHtml += "<col width='28%' />";
                    bFirst = false;
                }
            }
            resultHtml += "</tr>";
        }
    }

    $("#colHeader").append(colHeadHtml);
    $("#rankHeader").append(headerHtml);

    return resultHtml;
}

/**
 * 시간 형식 변환
 * */
function changeTime(time){
    if (time) {
        let d = new Date(time);

        if(time>=(60 * 60 * 1000)) {
            return ('0' + d.getUTCHours()).slice(-2) + 'h ' + ('0' + d.getUTCMinutes()).slice(-2) + 'm ' + ('0' + d.getUTCSeconds()).slice(-2) + 's';
        }else if (time >= (60 * 1000)) {
            return ('0' + d.getUTCMinutes()).slice(-2) + 'm ' + ('0' + d.getUTCSeconds()).slice(-2) + 's';
        }else if (time >= 1000){
            return ('0' + d.getUTCSeconds()).slice(-2) + 's';
        }
    } else {
        return "-";
    }
}

/**
 * 검색 조건에 대한 이벤트
 * */
function searchList(){
    // 그룹에서 reset을 클릭 시 초기화
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

    // 그룹의 초기화 등으로 인한 페이지 갱신
    getDashBoardDetail();
}

/**
 * RC (대그룹) 정보 불러오기
 * */
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
                    console.log("Group Change");
                    getStatisticsSubGroupList($("#statisticsGroupList option:selected").val());
                    selectId = $(this);
                    selectIdOption = $('option:selected',this);
                    if (!(localStorage.getItem("keepDashboard") === "Y")) {
                        loading.show();
                        setTimeout(() => {
                            searchList();
                        }, intervalTime);
                    }
                });
            }
        }
    });
}

/**
 * 서브 그룹 List 불러오기
 */
function getStatisticsSubGroupList(gId) {
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
                    pstatisticsSubGroupListHtml += "<option value='" + data[i].name  + "'>" + data[i].name + "</option>";
                }
                $("#statisticsSubGroupList").html(pstatisticsSubGroupListHtml);

                $("#statisticsSubGroupList").off().on("change", function () {
                    getStatisticsStoreList($("#statisticsSubGroupList option:selected").val(),$("#statisticsGroupList option:selected").val());
                    selectId = $(this);
                    selectIdOption = $('option:selected',this);
                    if (!(localStorage.getItem("keepDashboard") === "Y")) {
                        loading.show();
                        setTimeout(() => {
                            searchList();
                        }, intervalTime);
                    }
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
                $("#statisticsStoreList").off().on("change", function (){
                    selectId = $(this);
                    selectIdOption = $('option:selected',this);
                    if (!(localStorage.getItem("keepDashboard") === "Y")) {
                        loading.show();
                        setTimeout(() => {
                            searchList();
                        }, intervalTime);
                    }
                });
            }
        }
    });
}


/**
 * Excel 다운로드
 * */
function dashBoardDetailDownloadExcel(){
    // 정보들을 모두 설정한다.
    let startDate = $('#startDate').val();
    let endDate = $('#endDate').val();

    let diffDate = Math.ceil((new Date(endDate).getTime() - new Date(startDate).getTime()) / (1000*3600*24));
    if (diffDate > 31){
        return;
    }

    $.fileDownload("/dashboardDetailExcel" + location.search, {
        httpMethod: "POST",
        data: {
            groupId: $("#statisticsGroupList option:selected").val(),
            subgroupId: $("#statisticsSubGroupList option:selected").val(),
            storeId: $("#statisticsStoreList option:selected").val(),
            sDate: startDate,
            eDate: endDate,
            peakType: $("#sel_peak_time").val(),
        },
        successCallback: function (url){

        }, failCallback: function (responseHtml, url, err){

        }
    });
}

/**
 * 뒤로가기 클릭 이벤트
 * */
function btnBackMove(){
    let objSearch = new Object();
    objSearch.groupId = $("#statisticsGroupList option:selected").val();
    objSearch.subgroupId = $("#statisticsSubGroupList option:selected").val();
    objSearch.storeId = $("#statisticsStoreList option:selected").val();
    objSearch.sDate = $('#startDate').val();
    objSearch.eDate = $('#endDate').val();
    objSearch.peakType = $("#sel_peak_time").val();
    objSearch.toDay = $("#btnToday").hasClass("on");
    objSearch.lastSeven = $("#btnLastSeven").hasClass("on");
    objSearch.cMonth = $("#btnCurrentMonth").hasClass("on");
    objSearch.btnTotal = $("#btnTotalTime").hasClass("on");

    localStorage.setItem("keepDashboard", "Y");
    localStorage.setItem("objSearch", JSON.stringify(objSearch));

    // 페이지 이동
    location.href = "/dashboard";
}
/*]]>*/