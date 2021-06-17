/*<![CDATA[*/
let loading = $('<div id="loading"><div><p style="background-color: #838d96"/></div></div>').appendTo(document.body).hide();
let selectId = $("#statisticsStoreList");
let selectIdOption = $("#statisticsStoreList option:selected");

/**
 * 페이지 진입 시 처음 실행
 * */
$(function(){
    console.log("대시보드 페이지 오픈");
    let date = $.datepicker.formatDate('yy-mm-dd', new Date);
    $('#startDate, #endDate').val(date);

    makeEventBind();
    getGroupList();
    getDashBoardInfos()
});


/**
 * Object에 대한 Event 바인딩
 * */
function makeEventBind(){
    // 그룹, 하위그룹, 스토어에 대한 Select 이벤트
    $(".search-select").off().on('change', function (){
        selectId = $(this);
        selectIdOption = $('option:selected',this);
        searchList();
    });

    // 픽업 타임에 대한 이벤트
    $("#sel_peak_time").off().on('change', function (){
        if ($('option:selected', this).val() === "0"){
            $("#btnTotalTime").removeClass('on');
            $("#btnTotalTime").addClass('on');
        }else{
            $("#btnTotalTime").removeClass('on');
        }

        searchList();
    });

    // 전체 시간에 대한 이벤트
    $("#btnTotalTime").off().on('click', function (){
        // total 기능은 off가 되어 있을 때만 작동하도록 한다.
        if(!($(this).hasClass('on'))){
            $(this).addClass('on');
            $("#sel_peak_time").val("0");
            searchList();
        }
    });

    // 달력의 Today, Last 7 Days, Current Month 이벤트
    $(".search-date").off().on('click', function(){
        // 클릭한 객체가 이미 클릭 상태로 되어 있는 경우 이벤트를 넘긴다.
        if ($(this).hasClass('on')){
            return;
        }

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
    });
    
    // 달력의 시작일자에 대한 이벤트
    $("#startDate").off().on('change', function (){
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
    });

    // 달력의 종료일자에 대한 이벤트
    $("#endDate").off().on('change', function (){
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
    });
}

/**
 * 대시 보드에서 보여줄 모든 정보를 호출한다.
 * */
function getDashBoardInfos(){
    // 정보들을 모두 설정한다.
    loading.show();

    console.log("대쉬보드 인포스 S");
    console.log($("#statisticsGroupList option:selected").val());
    console.log($("#statisticsSubGroupList option:selected").val());
    console.log($("#statisticsStoreList option:selected").val());
    console.log($("input[name=searchDate]:checked").val());
    console.log("대쉬보드 인포스 F");

    $.ajax({
        url : "/totalStatistsc",
        type : 'post',
        data : {
            groupId: $("#statisticsGroupList option:selected").val(),
            subgroupId: $("#statisticsSubGroupList option:selected").val(),
            storeId: $("#statisticsStoreList option:selected").val(),
            sDate: $("#startDate").val(),
            eDate: $("#endDate").val(),
            peakType: $("#sel_peak_time").val(),
        },
        async : false,
        dataType : 'json',
        success : function(data) {
            console.log(data);
        },
        error: function (err){
            console.log("에러 => ", err);
        },
        complete: function (){
            console.log("완료");
        }
    });

    loading.hide();
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
    getDashBoardInfos();
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
                    pstatisticsSubGroupListHtml += "<option value='" + data[i].name  + "'>" + data[i].name + "</option>";
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

                // $("#statisticsStoreList").on("change", function () {
                //     getStoreStatisticsByDate();
                // });

            }
        }
    });
}

/*]]>*/