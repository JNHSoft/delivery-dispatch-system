/*<![CDATA[*/
let loading = $('<div id="loading"><div><p style="background-color: #838d96"/></div></div>').appendTo(document.body).hide();
let selectId = $("#statisticsStoreList");
let selectIdOption = $("#statisticsStoreList option:selected");

/**
 * 페이지 진입 시 처음 실행
 * */
$(function(){
    console.log("대시보드 페이지 오픈");
    makeEventBind();
    getGroupList();
    getDashBoardInfos()
});


/**
 * Object에 대한 Event 바인딩
 * */
function makeEventBind(){
    $(".select").off().on('change', function (){
        selectId = $(this);
        selectIdOption = $('option:selected',this);
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
            groupId: $("#statisticsGroupList option:selected").val()
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