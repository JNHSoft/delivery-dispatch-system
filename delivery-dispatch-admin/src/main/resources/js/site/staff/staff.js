/*<![CDATA[*/

$(document).ready(function () {
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
                field : 'th1',
                op : "eq",
                data : searchText
            });

            filter.rules.push({
                field : 'th2',
                op : "eq",
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
                op : "cn",
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
    getRiderList();
    getRiderStoreList();

});

/**
 * rider 목록 List
 */
function getRiderList() {
    var $mydata = [];
    $.ajax({
        url: "/getRiderList",
        type: 'get',
        dataType: 'json',
        success: function (data) {
            for(var key in data) {
                var $tmpData = new Object();
                console.log(data);
                if (data.hasOwnProperty(key)) {
                    // 아이디 / 소속 매장 / 이름 / 코드 / 성별 / 연락처 / 긴급연락처 / 주소 / 번호판 / 청소년 / 아이디
                    $tmpData.th0 = data[key].id
                    $("#selectedRiderId").val(data[key].id);

                    if(!data[key].store){
                        $tmpData.th1 = "-";
                    } else{
                        $tmpData.th1 = data[key].store.storeName
                    }

                    $tmpData.th2 = data[key].name

                    $tmpData.th3 = data[key].code

                    /*if(data[key].gender == "0"){
                        $tmpData.th4 = "男"
                    } else if (data[key].gender == "1"){
                        $tmpData.th4 = "女"
                    }*/
                    $tmpData.th5 = data[key].phone
                    // $tmpData.th6 = data[key].emergencyPhone
                    // $tmpData.th7 = data[key].address
                    $tmpData.th8 = data[key].vehicleNumber

                    /*if(data[key].teenager=="0"){
                        $tmpData.th9 = "X"
                    } else if(data[key].teenager=="1"){
                        $tmpData.th9 = "O"
                    }*/

                    $tmpData.th10 = data[key].loginId

                    if(data[key].group != null){
                        $tmpData.th11 = data[key].group.id

                    }
                    if(data[key].subGroup != null) {
                        $tmpData.th12 = data[key].subGroup.id

                    }

                    if(data[key].subGroupRiderRel != null) {
                        $tmpData.th13 = data[key].subGroupRiderRel.storeId

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
                    {label:'No', name:'th0', width:25, key:true, align:'center'},
                    {label:rider_belong_store, name:'th1', width:80, align:'center'},
                    {label:rider_name, name:'th2', width:80, align:'center'},
                    {label:rider_code, name:'th3', width:60, align:'center'},
                    // {label:rider_gender, name:'th4', width:40, align:'center'},
                    {label:rider_phone, name:'th5', width:100, align:'center'},
                    // {label:rider_phone_emergency, name:'th6', width:100, align:'center'},
                    // {label:rider_address, name:'th7', width:200},
                    {label:rider_vehicle_number, name:'th8', width:80, align:'center'},
                    // {label:rider_teenager, name:'th9', width:60, align:'center'},
                    {label:rider_login_id, name:'th10', width:80, align:'center'},
                    {label:'그룹ID', name:'th11', width:60, hidden:'hidden'},
                    {label:'서브그룹ID', name:'th12', width:60, hidden:'hidden'},
                    {label:'매장ID', name:'th13', width:60, hidden:'hidden'}
                ],
                width:'auto',
                height:520,
                async : false,
                autowidth:true,
                rowNum:20,
                pager:"#jqGridPager",
                ondblClickRow: function(rowId) {

                     var rowData = jQuery(this).getRowData(rowId);

                     var riderId = rowData['th0']
                     $("#selectedRiderId").val(riderId);

                     var groupId = rowData['th11']
                     $("#selectedGroupId").val(groupId);

                     var subGroupId = rowData['th12']
                     $("#selectedSubGroupId").val(subGroupId);

                     var storeId = rowData['th13']
                     $("#selectedStoreId").val(storeId);

                     getRiderDetail();
                }
            });
            resizeJqGrid('#jqGrid'); //그리드 리사이즈

        }
    });
}
/**
 * Rider 상세 보기
 */
function getRiderDetail() {
    var riderId = $("#selectedRiderId").val();
    var storeId = $("#selectedStoreId").val();
    var groupId = $("#selectedGroupId").val();
    var subGroupId = $("#selectedSubGroupId").val();
    
    if(riderId == null || riderId == "") {
        alert("직원을 선택해 주세요.");
    } else {
        $.ajax({
            url : "/getRiderDetail",
            type : 'get',
            data : {
                storeId : storeId,
                riderId : riderId,
                groupId : groupId,
                subGroupId : subGroupId
            },
            dataType : 'json',
            success : function(data){
                console.log("상세보기");
                console.log(data);
                // 아이디
                $("#riderDetailLoginId").val(data.A_Rider.loginId);
                //password
                // $("#riderDetailPassword").val(data.A_Rider.loginPw);
                // 직원코드
                $("#riderDetailCode").val(data.A_Rider.code);
                // 기사명
                $("#riderDetailName").val(data.A_Rider.name);
                // 성별
                /*if(data.A_Rider.gender == "0"){
                    $("#riderDetailGenderMale").attr("checked", true);
                }
                else if(data.A_Rider.gender == "1"){
                    $("#riderDetailGenderFeMale").attr("checked", true);
                }*/
                // 청소년 여부
                /*if(data.A_Rider.teenager == "0"){
                    $("#riderDetailNoneTeenager").attr("checked", true);
                }
                else if(data.A_Rider.teenager == "1"){
                    $("#riderDetailTeenager").attr("checked", true);
                }*/
                // 기사 연락처
                $("#riderDetailPhone").val(data.A_Rider.phone);
                // 지원 긴급 연락처
                // $("#riderDetailEmergencyPhone").val(data.A_Rider.emergencyPhone);
                // 주소
                // $("#riderDetailAddress").val(data.A_Rider.address);

                var riderDetailStoreNameHtml = "<option value=''>" + "不明" + "</option>";
                // 소속 매장
                if(data.storeList !=null){
                    for (var i in data.storeList){
                        riderDetailStoreNameHtml += "<option value='" + data.storeList[i].id  + "'>" + data.storeList[i].storeName + "</option>";
                    }
                    $("#riderDetailStoreName").html(riderDetailStoreNameHtml);

                    // $("#riderDetailStoreName").val($("#riderDetailStoreName option:selected").val());

                    $("#riderDetailStoreName").on("change", function(){
                        getStoreInfo($("#riderDetailStoreName option:selected").val());
                        console.log($("#riderDetailStoreName option:selected").val());

                    });
                    if(data.A_Rider.group == null){
                        $("#riderDetailStoreName").val();
                        $("#hasGroup").val("F");
                        $("#riderDetailStoreName").val("");
                    } else if (data.A_Rider.group.id != null){
                        $("#hasGroup").val("T");
                        $("#riderDetailStoreName").val();
                    }

                    if(data.A_Rider.subGroup == null){
                        $("#riderDetailStoreName").val();
                        $("#hasGroup").val("F");
                        $("#riderDetailStoreName").val("");
                    } else if (data.A_Rider.subGroup.id != null){
                        $("#hasGroup").val("T");
                        $("#riderDetailStoreName").val();
                    }

                    if (data.A_Rider.subGroupRiderRel == null) {
                        $("#hasGroup").val("F");
                        $("#riderDetailStoreName").val();
                    }else if (data.A_Rider.subGroupRiderRel.storeId){
                        $("#hasGroup").val("T");
                        $("#riderDetailStoreName").val(data.A_Rider.subGroupRiderRel.storeId).prop("selected", true);
                    }

                    // if(data.A_Rider.group != null && data.A_Rider.subGroup != null && data.A_Rider.subGroupRiderRel != null){
                    //
                    // }
                }

                // 근무 시간
                if(data.A_Rider.workingHours !=null){
                    var tmpHours = data.A_Rider.workingHours.split('|');
                    $('#riderDetailWorkStartTime').val(tmpHours[0]/60).prop('selected', true);
                    $('#riderDetailWorkEndTime').val(tmpHours[1]/60).prop('selected', true);
                } else {
                    $('#riderDetailWorkStartTime').val('empty').prop('selected', true);
                    $('#riderDetailWorkEndTime').val('empty').prop('selected', true);
                }
                // 휴식 시간
                if(data.A_Rider.restHours !=null){
                    var tmpHours = data.A_Rider.restHours.split('|');
                    for(var i = 0; i < tmpHours.length; i++){
                        var checkId = "#riderRestTime" + tmpHours[i];
                        $(checkId).attr("checked", "checked");
                    }
                }
                // 번호판
                $("#riderDetailVehicleNumber").val(data.A_Rider.vehicleNumber);

                popOpen("#popRiderDetail");
            }
        });
    }
}

/**
 * Rider 수정
 */
function putRiderDetail() {
    var riderId = $("#selectedRiderId").val();
    var storeId = $("#riderDetailStoreName option:selected").val();
    var groupId = $("#selectedGroupId").val();
    var subGroupId = $("#selectedSubGroupId").val();
    var tmpHours = [];
    $('input[name="riderRestTime"]:checked').each(function(index, element) {
        tmpHours.push($(element).val());
    });
    var restHours = tmpHours.join("|");
    var tmpWorking = $("#riderDetailWorkStartTime").val() * 60 + "|" + $("#riderDetailWorkEndTime").val() * 60;
    // 수정 보내는 값들
    $.ajax({
        url : "/putRiderDetail",
        type : 'put',
        dataType : 'json',
        async : false,
        data : {
            riderId				: riderId,
            storeId				: storeId,
            code    			: $("#riderDetailCode").val(),
            name	            : $("#riderDetailName").val(),
            // gender              : $("input[type='radio'][name='riderDetailGender']:checked").val(),
            //groupId		        : groupId,
            //subGroupId			: subGroupId,
            phone			    : $("#riderDetailPhone").val(),
            // emergencyPhone      : $("#riderDetailEmergencyPhone").val(),
            // address				: $("#riderDetailAddress").val(),
            // teenager            : $("input[type='radio'][name='riderDetailTeenager']:checked").val(),
            workingHours        : tmpWorking,
            restHours           : restHours,
            vehicleNumber       : $("#riderDetailVehicleNumber").val(),
            hasGroup		    : $("#hasGroup").val()
        },
        success : function(data){
                alert(alert_confirm_mod_success);
                getRiderList();
                location.reload();
        }
    });

}

/**
 * Rider 등록
 */
function postRider() {
    var storeId =$("#postRiderStoreList option:selected").val();
    /*var groupId = $("#selectedGroupId").val();
    var subGroupId = $("#selectedSubGroupId").val();*/

    var tmpHours = [];
    $('input[name="riderRestTime"]:checked').each(function(index, element) {
        tmpHours.push($(element).val());
    });
    var restHours = tmpHours.join("|");
    var tmpWorking = $("#postRiderWorkStartTime").val() * 60 + "|" + $("#postRiderWorkEndTime").val() * 60;

    if($.cookie('riderLoginIdChk')!=$("#postRiderLoginId").val()){
        alert(loginid_check);
        return;
    }
    $.ajax({
        url: "/postRider",
        type: 'post',
        dataType: 'text',
        data: {
            loginId				: $("#postRiderLoginId").val(),
            loginPw             : $("#postRiderLoginPw").val(),
            storeId             : storeId,
            code    			: $("#postRiderCode").val(),
            name	            : $("#postRiderName").val(),
            // gender              : $("input[type='radio'][name='postRiderGender']:checked").val(),
            // groupId		        : groupId,
            // subGroupId			: subGroupId,
            phone			    : $("#postRiderPhone").val(),
            // emergencyPhone		: $("#postRiderEmergencyPhone").val(),
            // address				: $("#postRiderAddress").val(),
            // teenager            : $("input[type='radio'][name='postRiderTeenager']:checked").val(),
            workingHours        : tmpWorking,
            restHours           : restHours,
            vehicleNumber       : $("#postRiderVehicleNumber").val()
        },
        success: function (data) {
            if(data == 'err'){
                return false;
            }else{
                $.removeCookie("riderLoginIdChk");
                alert(alert_created_success);
                popClose('#popRiderDetail');
                getRiderList();
                location.reload();
            }
        }
    });
}

/**
 * Rider 등록시 상점 List 불러오기
 */
function getRiderStoreList() {
    $.ajax({
        url : "/getRiderStoreList",
        type : 'get',
        data : {
        },

        dataType : 'json',
        success : function(data) {
            if (data) {
                 var postRiderStoreHtml = "<option value='none'>" + "請選擇" + "</option>";

                // var postRiderStoreHtml = "";
                for (var i in data) {
                    postRiderStoreHtml += "<option value='" + data[i].id + "'>" + data[i].storeName + "</option>";
                }
                $("#postRiderStoreList").html(postRiderStoreHtml);

                $("#postRiderStoreList").on("change", function () {
                    getStoreInfo($("#postRiderStoreList option:selected").val());
                    console.log($("#postRiderStoreList option:selected").val());
                });
            }
        }
    });
}

/**
 * Store 정보 가져오기
 */
function getStoreInfo(storeId) {
        $.ajax({
            url : "/getRiderStoreInfo",
            type : 'get',
            data : {
                storeId : storeId,
            },
            dataType : 'json',
            success : function(data) {
                console.log(data);
                $("#selectedGroupId").val(data.group.id);
                $("#selectedSubGroupId").val(data.subGroup.id);
                $("#selectedStoreId").val(data.id);
            }
        });
}

/**
 * 기사 삭제
 */
function deleteRider() {
    var riderId =  $("#selectedRiderId").val()

    if(!confirm("Delete?")) return;


    console.log(riderId);
    $.ajax({
        url: "/deleteRider",
        type: 'put',
        dataType: 'text',
        data: {
            riderId: riderId
        },
        success: function (data) {
            if (data === 'true') {
                location.reload();
            }

        }
    });
}


/**
 * 기사 아이디 중복 체크
 */
function riderLoginIdCheck() {
    var loginId = $("#postRiderLoginId").val();

    $.ajax({
        url: "/selectRiderLoginIdCheck",
        type: 'get',
        dataType: 'text',
        data: {
            loginId: loginId
        },
        success: function (data) {
            console.log(data);
            if(data>0){
                alertTip('#postRiderLoginId',loginid_uncheck);
            } else{
                $.cookie("riderLoginIdChk", loginId, {"expires" : 1});
                alertTip('#postRiderLoginId',loginid_check);
            }
        }
    });
}

/*]]>*/


