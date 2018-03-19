$(document).ready(function () {
    getStoreList();
    getGroupList();
    getPostSubGroupList();


});

/**
 * Store 목록 List
 */
function getStoreList() {
    var $mydata = [];

    $.ajax({
        url: "/getStoreList",
        type: 'get',
        dataType: 'json',
        success: function (data) {

            for(var key in data) {
                var $tmpData = new Object();

                if (data.hasOwnProperty(key)) {


                    $tmpData.th0 = data[key].id

                    if(data[key].group != null){
                        $tmpData.th12 = data[key].group.id
                        $tmpData.th13 = data[key].subGroup.id
                        $tmpData.th1 = data[key].group.name
                    }
                    if(data[key].subGroup != null) {
                        $tmpData.th2 = data[key].subGroup.name
                    }
                    $tmpData.th3 = data[key].storeName
                    $tmpData.th4 = data[key].code
                    $tmpData.th5 = data[key].storePhone
                    $tmpData.th6 = data[key].name
                    $tmpData.th7 = data[key].phone
                    $tmpData.th8 = data[key].address
                    $tmpData.th9 = data[key].detailAddress
                    $tmpData.th10 = data[key].loginId
                    // 0: 수동배정, 1: 자동배정, 2: 기사배정
                    if(data[key].assignmentStatus == "0"){
                        $tmpData.th11 = "수동"
                    }
                    else if (data[key].assignmentStatus == "1"){
                        $tmpData.th11 = "자동"
                    }
                    else if (data[key].assignmentStatus == "2"){
                        $tmpData.th11 = "기사배정"
                    }

                    $mydata.push($tmpData);
                }
            }

            $("#jqGrid").jqGrid({
                datatype:"local",
                data:$mydata,
                colModel:[
                    {label:'No', name:'th0', width:25, align:'center'},
                    {label:'소속그룹', name:'th1', width:60, align:'center'},
                    {label:'소그룹', name:'th2', width:60, align:'center'},
                    {label:'매장명', name:'th3', width:80, align:'center'},
                    {label:'매장코드', name:'th4', width:60, align:'center'},
                    {label:'매장전화번호', name:'th5', width:80, align:'center'},
                    {label:'점장명', name:'th6', width:60, align:'center'},
                    {label:'점장 전화번호', name:'th7', width:80, align:'center'},
                    {label:'주소', name:'th8', width:200},
                    {label:'상세주소', name:'th9', width:200},
                    {label:'아이디', name:'th10', width:60, align:'center'},
                    {label:'배정모드', name:'th11', width:60, align:'center'},
                    {label:'그룹ID', name:'th12', width:60, hidden:'hidden'},
                    {label:'서브그룹ID', name:'th13', width:60, hidden:'hidden'}
        ],
                width:'auto',
                height:520,
                autowidth:true,
                rowNum:20,
                pager:"#jqGridPager",
                ondblClickRow: function(rowId) {

                     var rowData = jQuery(this).getRowData(rowId);

                     var storeId = rowData['th0']
                     $("#selectedStoreId").val(storeId);

                     var groupId = rowData['th12']
                     $("#selectedGroupId").val(groupId);

                     var subGroupId = rowData['th13']
                     $("#selectedSubGroupId").val(subGroupId);

                     getStoreDetail();
                }

            });

            resizeJqGrid('#jqGrid'); //그리드 리사이즈

        }
    });
}

/**
 * Store 상세 보기
 */
function getStoreDetail() {
    var storeId = $("#selectedStoreId").val();

    var groupId = $("#selectedGroupId").val();

    var subGroupId = $("#selectedSubGroupId").val();

    if(storeId == null || storeId == "") {
        alert("매장을 선택해 주세요.");
    } else {
        $.ajax({
            url : "/getStoreDetail",
            type : 'get',
            data : {
                storeId : storeId,
                groupId : groupId,
                subGroupId : subGroupId
            },
            dataType : 'json',

            success : function(data){
                // loginid
                $("#storeDetailLoginId").val(data.A_Store.loginId);
                // password

                // 매장코드
                $("#storeDetailStoreCode").val(data.A_Store.code);
                // 매장명
                $("#storeDetailStoreName").val(data.A_Store.storeName);
                // 매장 전화번호
                $("#storeDetailStorePhone").val(data.A_Store.storePhone);
                // 배정 모드
                $("#storeDetailAssignStatusSelectBox").val(data.A_Store.assignmentStatus);

                // 소속 그룹
                if(data.groupList != null) {
                    var storeDetailGroupHtml = "";
                    for (var i in data.groupList){
                        storeDetailGroupHtml += "<option value='" + data.groupList[i].id  + "'>" + data.groupList[i].name + "</option>";
                    }
                    $("#storeDetailGroup").html(storeDetailGroupHtml);

                    $("#storeDetailGroup").on("change", function(){
                        getSubGroupList($("#storeDetailGroup option:selected").val());
                    });

                    if (data.A_Store.group == null){
                        $("#hasGroup").val("F");
                        $("#storeDetailGroup").val("");
                    } else if (data.A_Store.group.id != null){
                        $("#hasGroup").val("T");
                        $("#storeDetailGroup").val(data.A_Store.group.id).prop("selected", true);
                    }
                }

                // 소속 소그룹
                if(data.subGroupList != null ) {
                    // groupId , subGroupId 값 넘겨줌
                    getSubGroupList($("#storeDetailGroup option:selected").val(),data.A_Store.subGroup);
                    var storeDetailSubGroupHtml = "";
                    for (var i in data.subGroupList){
                        storeDetailSubGroupHtml += "<option value='" + data.subGroupList[i].id  + "'>" + data.subGroupList[i].name + "</option>";
                    }
                    $("#storeDetailSubGroup").html(storeDetailSubGroupHtml);

                }
                // 점장명
                $("#storeDetailStoreUserName").val(data.A_Store.name);
                // 점장 전화번호
                $("#storeDetailStoreUserPhone").val(data.A_Store.phone);
                // 매장 주소
                $("#storeDetailStoreAddress").val(data.A_Store.address);
                // 매장 상세 주소
                $("#storeDetailStoreDetailAddress").val(data.A_Store.detailAddress);


                popOpen("#popStoreDetail");
            }
        });
    }
}

/**
 * Store Detail 서브 그룹 List 불러오기
 */
function getSubGroupList(gId, subGroup) {
    var selectGroupId = gId;

    $.ajax({
        url : "/getSubGroupList",
        type : 'get',
        data : {
            groupId : selectGroupId
        },
        dataType : 'json',
        success : function(data){
            if(data) {
                var storeDetailSubGroupHtml = "";
                for (var i in data){
                    storeDetailSubGroupHtml += "<option value='" + data[i].id  + "'>" + data[i].name + "</option>";
                }
                $("#storeDetailSubGroup").html(storeDetailSubGroupHtml);
                // 받은 subGroup 보여준다.
                if(subGroup) {
                    $("#storeDetailSubGroup").val(subGroup.id);
                }
            }
        }
    });
}

/**
 * Store  등록시 그룹 List 불러오기
 */
function getGroupList() {
    $.ajax({
        url : "/getGroupList",
        type : 'get',
        data : {

        },
        dataType : 'json',
        success : function(data) {
            if (data) {
                var postStoreGroupHtml = "";
                for (var i in data) {
                    postStoreGroupHtml += "<option value='" + data[i].id + "'>" + data[i].name + "</option>";
                }
                $("#postStoreGroup").html(postStoreGroupHtml);

                $("#postStoreGroup").on("change", function () {
                    getPostSubGroupList($("#postStoreGroup option:selected").val());
                });
            }
        }
    });
}

/**
 * Store 등록시 서브 그룹 List 불러오기
 */
function getPostSubGroupList(gId, subGroup) {
    var selectGroupId = null;

    if (gId == null) {
        selectGroupId = '1';
    } else {
        selectGroupId = gId
    }

    $.ajax({
        url : "/getSubGroupList",
        type : 'get',
        data : {
            groupId : selectGroupId
        },
        dataType : 'json',
        success : function(data){
            if(data) {
                var postStoreSubGroupHtml = "";
                for (var i in data){
                    postStoreSubGroupHtml += "<option value='" + data[i].id  + "'>" + data[i].name + "</option>";
                }
                $("#postStoreSubGroup").html(postStoreSubGroupHtml);
                // 받은 subGroup 보여준다.
                if(subGroup) {
                    $("#postStoreSubGroup").val(subGroup.id);
                }
            }
        }
    });
}




/**
 * Store 수정
 */
function putStoreDetail() {
    // 주문 수정 보내는 값들
    $.ajax({
        url : "/putStoreDetail",
        type : 'put',
        dataType : 'json',
        data : {
            storeId				: $("#selectedStoreId").val(),
            code    			: $("#storeDetailStoreCode").val(),
            storeName			: $("#storeDetailStoreName").val(),
            storePhone			: $("#storeDetailStorePhone").val(),
            assignmentStatus	: $("#storeDetailAssignStatusSelectBox").val(),
            groupId		        : $("#storeDetailGroup").val(),
            subGroupId			: $("#storeDetailSubGroup").val(),
            name	            : $("#storeDetailStoreUserName").val(),
            phone			    : $("#storeDetailStoreUserPhone").val(),
            address				: $("#storeDetailStoreAddress").val(),
            detailAddress		: $("#storeDetailStoreDetailAddress").val(),
            hasGroup		    : $("#hasGroup").val()
        },
        success : function(data){
                alert("수정 완료");
                popClose('#popStoreDetail');
                getStoreList()
                // 완료후 페이지 호출
                location.href = "/store";
        }
    });

}

/**
 * Store 등록
 */
function postStore() {

    $.ajax({
        url: "/postStore",
        type: 'post',
        dataType: 'json',
        data: {
            loginId				: $("#postStoreLoginId").val(),
            loginPw             : $("#postStoreloginPw").val(),
            code    			: $("#postStoreCode").val(),
            storeName			: $("#postStoreName").val(),
            storePhone			: $("#postStorePhone").val(),
            assignmentStatus	: $("#postStoreAssignStatusSelectBox").val(),
            groupId		        : $("#postStoreGroup").val(),
            subGroupId			: $("#postStoreSubGroup").val(),
            name	            : $("#postStoreUserName").val(),
            phone			    : $("#postStoreUserPhone").val(),
            address				: $("#postStoreAddress").val(),
            detailAddress		: $("#postStoreDetailAddress").val()
        },
        success: function (data) {

            popClose('#popStore');
            getStoreList()
            location.href = "/store";

        }
    });
    }





