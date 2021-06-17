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
    getDashBoardDetail()
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

    $(".search-date").off().on('click', function(){
        // 클릭한 객체가 이미 클릭 상태로 되어 있는 경우 이벤트를 넘긴다.
        if ($(this).hasClass('on')){
            return;
        }

        // 현재 클래스 중 on 항목에 대한 내용 제거
        $(".search-date").removeClass('on');
        // 현재 클릭 이벤트가 발생된 곳에 on Class 추가
        $(this).addClass('on');

        // 클릭된 항목에 따라, 이벤트가 진행되도록 함.
        console.log($(this).val());

    });
}

/**
 * 대시 보드에서 보여줄 모든 정보를 호출한다.
 * */
function getDashBoardDetail(){
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
            }
        }
    });
}

/*]]>*/