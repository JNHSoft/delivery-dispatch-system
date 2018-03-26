$(document).ready(function () {
    getRiderList();
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


                    $tmpData.th0 = data[key].id
                    $tmpData.th1 = data[key].name
                    $tmpData.th2 = data[key].code
                    if(data[key].gender == "0"){
                        $tmpData.th3 = "남자"    
                    } else if (data[key].gender == "1"){
                        $tmpData.th3 = "여자"
                    }
                    $tmpData.th4 = data[key].phone
                    $tmpData.th5 = data[key].emergencyPhone
                    $tmpData.th6 = data[key].address
                    $tmpData.th7 = data[key].vehicleNumber
                    if(data[key].teenager=="0"){
                        $tmpData.th8 = "X"
                    } else if(data[key].teenager=="1"){
                        $tmpData.th8 = "O"
                    }
                    $tmpData.th9 = data[key].loginId

                    if(data[key].group != null){
                        $tmpData.th10 = data[key].group.id

                    }
                    if(data[key].subGroup != null) {
                        $tmpData.th11 = data[key].subGroup.id
                    }

                    if(data[key].subGroupRiderRel != null) {
                        $tmpData.th12 = data[key].subGroupRiderRel.storeId
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
                    {label:rider_name, name:'th1', width:80, align:'center'},
                    {label:rider_code, name:'th2', width:60, align:'center'},
                    {label:rider_gender, name:'th3', width:40, align:'center'},
                    {label:rider_phone, name:'th4', width:100, align:'center'},
                    {label:rider_phone_emergency, name:'th5', width:100, align:'center'},
                    {label:rider_address, name:'th6', width:200},
                    {label:rider_vehicle_number, name:'th7', width:80, align:'center'},
                    {label:rider_teenager, name:'th8', width:60, align:'center'},
                    {label:rider_login_id, name:'th9', width:80, align:'center'},
                    {label:'그룹ID', name:'th10', width:60, hidden:'hidden'},
                    {label:'서브그룹ID', name:'th11', width:60, hidden:'hidden'},
                    {label:'매장ID', name:'th12', width:60, hidden:'hidden'}
                ],
                width:'auto',
                height:520,
                autowidth:true,
                rowNum:20,
                pager:"#jqGridPager",
                ondblClickRow: function(rowId) {

                     var rowData = jQuery(this).getRowData(rowId);

                     var riderId = rowData['th0']
                     $("#selectedRiderId").val(riderId);

                     var groupId = rowData['th10']
                     $("#selectedGroupId").val(groupId);

                     var subGroupId = rowData['th11']
                     $("#selectedSubGroupId").val(subGroupId);

                     var storeId = rowData['th12']
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
                $("#riderDetailPassword").val(data.A_Rider.loginPw);
                // 직원코드
                $("#riderDetailCode").val(data.A_Rider.code);
                // 기사명
                $("#riderDetailName").val(data.A_Rider.name);
                // 성별
                if(data.A_Rider.gender == "0"){
                    $("#riderDetailGenderMale").attr("checked", true);
                }
                else if(data.A_Rider.gender == "1"){
                    $("#riderDetailGenderFeMale").attr("checked", true);
                }
                // 청소년 여부
                if(data.A_Rider.teenager == "0"){
                    $("#riderDetailNoneTeenager").attr("checked", true);
                }
                else if(data.A_Rider.teenager == "1"){
                    $("#riderDetailTeenager").attr("checked", true);
                }
                // 기사 연락처
                $("#riderDetailPhone").val(data.A_Rider.phone);
                // 지원 긴급 연락처
                $("#riderDetailEmergencyPhone").val(data.A_Rider.emergencyPhone);
                // 주소
                $("#riderDetailAddress").val(data.A_Rider.address);

                // 소속 매장
                if(data.storeList !=null){
                    var riderDetailStoreNameHtml = "";
                    for (var i in data.storeList){
                        riderDetailStoreNameHtml += "<option value='" + data.storeList[i].id  + "'>" + data.storeList[i].storeName + "</option>";
                    }
                    $("#riderDetailStoreName").html(riderDetailStoreNameHtml);


                    $("#riderDetailStoreName").on("change", function(){
                        console.log($("#riderDetailStoreName option:selected").val());
                    });

                    $("#riderDetailStoreName").val($("#riderDetailStoreName option:selected").val());


                    if(data.A_Rider.group == null){
                        $("#riderDetailStoreName").val();
                    } else if (data.A_Rider.subGroup == null){
                        $("#riderDetailStoreName").val();
                    } else if (data.A_Rider.subGroupRiderRel == null) {
                        $("#riderDetailStoreName").val();
                    }

                    if(data.A_Rider.group != null && data.A_Rider.subGroup != null && data.A_Rider.subGroupRiderRel != null){
                        $("#riderDetailStoreName").val(data.A_Rider.subGroupRiderRel.storeId).prop("selected", true);
                    }



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
        data : {
            riderId				: riderId,
            storeId				: storeId,
            code    			: $("#riderDetailCode").val(),
            name	            : $("#riderDetailName").val(),
            gender              : $("input[type='radio'][name='riderDetailGender']:checked").val(),
            groupId		        : $("#selectedGroupId").val(),
            subGroupId			: $("#selectedSubGroupId").val(),
            phone			    : $("#riderDetailPhone").val(),
            emergencyPhone      : $("#riderDetailEmergencyPhone").val(),
            address				: $("#riderDetailAddress").val(),
            teenager            : $("input[type='radio'][name='riderDetailTeenager']:checked").val(),
            workingHours        : tmpWorking,
            restHours           : restHours,
            vehicleNumber       : $("#riderDetailVehicleNumber").val()
        },
        success : function(data){
                alert("수정 완료");
                popClose('#popRiderDetail');
                getRiderList();
                // // 완료후 페이지 호출
                // location.href = "/staff";
        }
    });

}

/**
 * Rider 등록
 */
function postRider() {
    if($("#postRiderStoreList option:selected").val()=="none"){
        alert("매장을 선택해주세요.");
        return;
    }

    var storeId =$("#selectedStoreId").val();
    var groupId = $("#selectedGroupId").val();
    var subGroupId = $("#selectedSubGroupId").val();

    var tmpHours = [];
    $('input[name="riderRestTime"]:checked').each(function(index, element) {
        tmpHours.push($(element).val());
    });
    var restHours = tmpHours.join("|");
    var tmpWorking = $("#postRiderWorkStartTime").val() * 60 + "|" + $("#postRiderWorkEndTime").val() * 60;


    $.ajax({
        url: "/postRider",
        type: 'post',
        dataType: 'json',
        data: {
            loginId				: $("#postRiderLoginId").val(),
            loginPw             : $("#postRiderLoginPw").val(),
            storeId             : storeId,
            code    			: $("#postRiderCode").val(),
            name	            : $("#postRiderName").val(),
            gender              : $("input[type='radio'][name='postRiderGender']:checked").val(),
            groupId		        : groupId,
            subGroupId			: subGroupId,
            phone			    : $("#postRiderPhone").val(),
            emergencyPhone		: $("#postRiderEmergencyPhone").val(),
            address				: $("#postRiderAddress").val(),
            teenager            : $("input[type='radio'][name='postRiderTeenager']:checked").val(),
            workingHours        : tmpWorking,
            restHours           : restHours,
            vehicleNumber       : $("#postRiderVehicleNumber").val()
        },
        success: function (data) {
            alert("등록완료");
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
                 var postRiderStoreHtml = "<option value='none'>" + "매장을 선택해주세요" + "</option>";

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

    if(!confirm("해당 기사를 삭제하시겠습니까?")) return;


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



