/*<![CDATA[*/
$(document).ready(function () {
    getStoreList();
    getGroupList();


    // 검색버튼을 누를시에 
    $("#searchButton").click(function () {
        // 입력받는 text 값 가져온다 
        var searchText = $("#searchText").val();
        console.log(searchText);
        // 필터 설정
        var filter = {
            groupOp: "OR",
            rules: []
        };
        // 검색 select 박스 변경 시 값 전송
        var select = $("#searchSelect option:selected").val();
        console.log(select);

         // html option 값의 value에 jq 그리드에 name 값을 넣어서 매칭시킨다.
        if(select == 'th0'){
            filter.rules.push({
                field : select,
                op : "eq",
                data : searchText
            });
            // 전체 검색 일때 다 뿌린다. eq 는 같다라는 뜻임
        }else if(select == 'all'){
            filter.rules.push({
                field : 'th0',
                op : "eq",
                data : searchText
            });

            filter.rules.push({
                field : 'th1',
                op : "eq",
                data : searchText
            });

            filter.rules.push({
                field : 'th3',
                op : "cn",
                data : searchText
            });

            filter.rules.push({
                field : 'th5',
                op : "cn",
                data : searchText
            });

            filter.rules.push({
                field : 'th3',
                op : "cn",
                data : searchText
            });

            filter.rules.push({
                field : 'th8',
                op : "cn",
                data : searchText
            });

            filter.rules.push({
                field : 'th10',
                op : "eq",
                data : searchText
            });

        }else{
            filter.rules.push({
                field : select,
                op : "cn",
                data : searchText
            });
        }
        var grid = jQuery('#jqGrid');
        grid[0].p.search = filter.rules.length > 0;
        $.extend(grid[0].p.postData, { filters: JSON.stringify(filter) });
        grid.trigger("reloadGrid", [{ page: 1 }]);
    });
    getStoreList();







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
                console.log(data);
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
                        $tmpData.th11 = order_assign_mode_store;
                    }
                    else if (data[key].assignmentStatus == "1"){
                        $tmpData.th11 = order_assign_mode_auto;
                    }
                    else if (data[key].assignmentStatus == "2"){
                        $tmpData.th11 = order_assign_mode_rider;
                    }

                    $mydata.push($tmpData);
                }
            }
            if ($mydata != null) {
                jQuery('#jqGrid').jqGrid('clearGridData')
                jQuery('#jqGrid').jqGrid('setGridParam', {data: $mydata, page: 1})
                jQuery('#jqGrid').trigger('reloadGrid');
            }
            $("#jqGrid").trigger("reloadGrid");

            $("#jqGrid").jqGrid({
                datatype:"local",
                data:$mydata,
                colModel:[
                    {label:'No', name:'th0', width:25, align:'center'},
                    {label:store_belong_group, name:'th1', width:60, align:'center'},
                    {label:store_belong_subgroup, name:'th2', width:60, align:'center'},
                    {label:store_name, name:'th3', width:80, align:'center'},
                    {label:store_code, name:'th4', width:60, align:'center'},
                    {label:store_phone, name:'th5', width:80, align:'center'},
                    {label:store_manager_name, name:'th6', width:60, align:'center'},
                    {label:store_manager_phone, name:'th7', width:80, align:'center'},
                    {label:store_address, name:'th8', width:200},
                    {label:store_address_detail, name:'th9', width:200},
                    {label:login_id, name:'th10', width:60, align:'center'},
                    {label:order_assign_mode, name:'th11', width:60, align:'center'},
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
                console.log(data);
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
                if(data.groupList !=null) {
                    var storeDetailGroupHtml = "<option value='none'>" + group_choise + "</option>";
                        // "<option value='none'>" + group_choise + "</option>";

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

                    var storeDetailSubGroupHtml = "<option value='none'>" +group_choise+ "</option>";
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
                    // "<option value='none'>" + "-" + "</option>";
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

                var postStoreGroupHtml = "<option value=''>" + group_choise + "</option>";
                // var postStoreGroupHtml = "";
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

                var postStoreSubGroupHtml = "<option value=''>" + group_choise + "</option>";
                // var postStoreSubGroupHtml = "";
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
    // 수정 보내는 값들
    $.ajax({
        url : "/putStoreDetail",
        type : 'put',
        dataType : 'json',
        async : false,
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
                alert("完成修復");
                popClose('#popStoreDetail');
                getStoreList()
                // 완료후 페이지 호출
                location.reload();
        }
    });

}

/**
 * Store 등록
 */
function postStore() {
    var $loginId = $("#postStoreLoginId").val();
    var $loginPw = $("#postStoreloginPw").val();
    var $code = $("#postStoreCode").val();
    var $storeName = $("#postStoreName").val();
    var $storePhone = $("#postStorePhone").val();
    var $name = $("#postStoreUserName").val();
    var $phone = $("#postStoreUserPhone").val();
    var $address = $("#postStoreAddress").val();
    var $detailAddress = $("#postStoreDetailAddress").val();

    if($loginId==""){
        alert("請檢查您的ID");
        return;
    }

    if($loginPw==""){
        alert("請檢查您的密碼");
        return;
    }

    if($code==""){
        alert("請輸入代碼");
        return;
    }

    if($storeName==""){
        alert("請輸入商店名稱");
        return;
    }

    if($storePhone==""){
        alert("請輸入您的商店編號");
        return;
    }

    if($name==""){
        alert("請輸入經理姓名");
        return;
    }

    if($phone==""){
        alert("請輸入經理電話號碼");
        return;
    }

    if($address==""){
        alert("請輸入地址");
        return;
    }

    if($detailAddress==""){
        alert("請輸入您的街道地址");
        return;
    }
    $.ajax({
        url: "/postStore",
        type: 'post',
        dataType: 'json',
        async : false,
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

            console.log(data);
            alert("商店註冊完成");
            popClose('#popStore');
            getStoreList();
            location.reload();
        }
    });
    }


/**
 * 상점 삭제
 */
function deleteStore() {
    var storeId =  $("#selectedStoreId").val()

    if(!confirm("您確定要刪除此商店嗎?")) return;
    console.log(storeId);
    $.ajax({
        url: "/deleteStore",
        type: 'put',
        dataType: 'text',
        data: {
            storeId: storeId
        },
        success: function (data) {
            if (data === 'true') {
                location.reload();
            }
        }
    });
}



/*]]>*/