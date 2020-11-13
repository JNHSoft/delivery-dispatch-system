/*<![CDATA[*/
let loading = $('<div id="loading"><div><p style="background-color: #838d96"></p></div></div>').appendTo(document.body).hide();
// Start Function after page loading completed.
$(function (){
    getApprovalRiderList();
    // let date = $.datepicker.formatDate('yy-mm-dd', new Date);

    // $("#riderExpDate").val(date);

});

function getApprovalRiderList(){

    let gridData = [];
    loading.show();
    // makeGrid(gridData);

    $.ajax({
        url: "/getApprovalRiderList",
        type: "get",
        data:{
        },
        dataType: "json",
        success: function (data, status){
            let i = 0;

            for (var key in data){
                if (data.hasOwnProperty(key)){
                    let tmpObj = new Object();

                    tmpObj.id = data[key].id == undefined ? "" : data[key].id;
                    tmpObj.No = ++i;
                    tmpObj.riderID = data[key].loginId == undefined ? "" : data[key].loginId;
                    tmpObj.riderName = data[key].name == undefined ? "" : data[key].name;
                    tmpObj.contactNum = data[key].phone;
                    //tmpObj.createdDate = data[key].createdDatetime == undefined ? "-" : dateFormat(data[key].createdDatetime);

                    tmpObj.expirationDate = data[key].session == undefined ? "-" : dateFormat(data[key].session.expiryDatetime);
                    tmpObj.setting = makeRowButton(data[key]);
                    tmpObj.status = getStatusValue(data[key].approvalStatus);

                    if (data[key].approvalStatus =="1" || data[key].approvalStatus == "5" || data[key].approvalStatus == "4"){
                        tmpObj.statusDate = data[key].acceptDatetime == undefined ? "-" : dateFormat(data[key].acceptDatetime);
                    }else if (data[key].approvalStatus =="2" || data[key].approvalStatus =="3"){
                        tmpObj.statusDate = data[key].rejectDatetime == undefined ? "-" : dateFormat(data[key].rejectDatetime);
                    }else{
                        tmpObj.statusDate = "-";
                    }

                    tmpObj.approvalStatus = data[key].approvalStatus;

                    gridData.push(tmpObj);
                }
            }
        },
        error: function (error){
            console.log(error);
        },
        complete: function (data){
            makeGrid(gridData);
            // resizeJqGrid('#jqGrid');
            formStyle();
            loading.hide();
        }
    });
}

function makeGrid(data){
    if (data != null){
        jQuery('#jqGrid').jqGrid('clearGridData');
        jQuery('#jqGrid').jqGrid('setGridParam', {data: data, page: 1});
        jQuery('#jqGrid').trigger('reloadGrid');
    }

    $("#jqGrid").jqGrid({
        datatype: "local",
        data: data,
        colModel:[
            {label: '', name: 'id', width: 25, key: true, align: 'center', hidden: true},
            {label: 'No', name: 'No', width: 25, align: 'center'},
            {label: riderLoginID, name: 'riderID', width: 80, align: 'center'},
            {label: riderName, name: 'riderName', width: 80, align: 'center'},
            {label: riderPhone, name: 'contactNum', width: 80, align: 'center'},
            {label: responseDate, name: 'statusDate', width: 80, align: 'center'},
            {label: expDate, name: 'expirationDate', width: 80, align: 'center'},
            {label: setting, name: 'setting', width: 300, align: 'center'},
            {label: approvalStatus, name: 'status', width: 80, align: 'center'},
            {label: '', name: 'approvalStatus', width: 80, align: 'center', hidden:true},
        ],
        height: 660,
        autowidth: true,
        rowNum: 20,
        pager: "#jqGridPager",
        loadComplete: function (data) {
            let ids = $("#jqGrid").getDataIDs();

            $.each(ids, function (idx, rowId) {
                let objRowData = $("#jqGrid").getRowData(rowId);

                if (objRowData.approvalStatus == "2" || objRowData.approvalStatus == "3"){
                    $("#jqGrid").setRowData(rowId, false, {background: '#FFAA55'});
                }else if (objRowData.approvalStatus == "4"){
                    $("#jqGrid").setRowData(rowId, false, {background: '#f5c717'});
                }
            });
        }
    });

    resizeJqGrid("#jqGrid");
}

//버튼 생성문
function makeRowButton(obj){
    if (obj == undefined){
        return "";
    }

    let btn_approval = "";
    let btn_edit = "";
    let btn_disapproval = "";
    let btn_deleteRowData = "";

    let expDate = obj.session == undefined ? "-" : dateFormat(obj.session.expiryDatetime);
    let nowDate = dateFormat(new Date);
    let bExpDate = !(expDate != "-" && expDate < nowDate);

    switch (obj.approvalStatus){
        case "0":           // 요청
            btn_approval = "<button class='button btn_pale_green h30 w100 mr10' style='font-size: 14px;' onclick='javascript:riderApprovalStatus(" + obj.id + ", 1)'>" + approval + "</button>";
            btn_edit = "<button class='button btn_gray2 h30 w80 mr10' style='font-size: 14px;' disabled>" + btnEdit + "</button>"
            btn_disapproval = "<button class='button h30 w100 btn_blue mr10' onclick='statusDisapproval(" + obj.id + ", " + obj.approvalStatus + ")'>" + disapproval + "</button>"
            //
            break;
        case "1":           // 수락
        case "5":
            btn_edit = "<button class='button btn_blue h30 w80 mr10' style='font-size: 14px;' onclick='javascript:searchRiderApprovalDetail(" + obj.id + ")'>" + btnEdit +  "</button>"
            // if (bExpDate){
            //     btn_edit = "<button class='button btn_blue h30 w80 mr10' style='font-size: 14px;' onclick='javascript:searchRiderApprovalDetail(" + obj.id + ")'>" + btnEdit +  "</button>"
            // }else{
            //     btn_edit = "<button class='button btn_gray2 h30 w80 mr10' style='font-size: 14px;' disabled>" + btnEdit + "</button>"
            // }
            break;
        case "2":           // 거절
        case "3":           // 승인 후 거절
            btn_deleteRowData = "<button class='button btn_red h30 w80' style='font-size: 14px;' onclick='deleteApprovalRiderRow(" + obj.id + ")'>" + btnDelete + "</button>"
            break;
        case "4":           // 유효기간 만료
            btn_deleteRowData = "<button class='button btn_red h30 w80' style='font-size: 14px;' onclick='deleteApprovalRiderRow(" + obj.id + ")'>" + btnDelete + "</button>"
            break;
    }

    return  btn_approval + btn_disapproval + btn_edit + btn_deleteRowData;
}

// 승인 허용
function riderApprovalStatus(rowID, status){
    //changeApprovalStatus
    // console.log($('#jqGrid').getRowData($('#jqGrid').getGridParam('selrow')).expirationDate);
    let current = dateFormat(new Date());
    let exp = dateFormat($("#expDate" + rowID).val());

    if (exp != "-" && exp < current){
        return false;
    }

    loading.show();
    $.ajax({
        url: "/approvalAccept",
        type: "post",
        data:{
            id: rowID,
            approvalStatus: status
        },
        dataType: "json",
        success: function (data){
            if (data){
                alert(msgChangeSuccess);
            }else{
                alert(msgChangeFailed);
            }

        },
        error: function (error){
            alert(msgChangeFailed);
        },
        complete: function (data){
            getApprovalRiderList();
            console.log("승인 성공");
        }
    });

}

// popUp 상태 변경
function popUpChangeStatus(){
    let approvalID = $("#approvalID").val();
    let approvalStatus = $("#approvalStatus").val();

    statusDisapproval(approvalID, approvalStatus);
}


function statusDisapproval(approvalID, approvalStatus){
    let current = dateFormat(new Date());
    let exp = dateFormat($("#expDate" + approvalID).val());

    if (approvalID == undefined || approvalID == "" || (exp != "-" && exp < current)){
        return false;
    }

    if (!confirm(msgDisapproval)){
        return false;
    }

    loading.show();

    let setStatus = "2";

    if (approvalStatus == "1"){
        // 승인 후 취소일 수도 있으므로,
        setStatus = "3";
    }

    $.ajax({
        url: "/changeApprovalStatus",
        type: "post",
        data:{
            id: approvalID,
            approvalStatus: setStatus
        },
        dataType: "json",
        success: function (data){
            alert(msgChangeSuccess);
        },
        error: function (error){
            alert(msgChangeFailed);
        },
        complete: function (data){
            getApprovalRiderList();
            popClose("#popRiderInfo");
        }
    });
}

// popUp 데이터 저장
function popUpSaveData(){
    let approvalID = $("#approvalID").val();
    let approvalStatus = $("#approvalStatus").val();
    let riderName = $("#riderName").val();

    let current = dateFormat(new Date());
    let exp = dateFormat($("#expDate" + approvalID).val());

    if (approvalID == undefined || approvalID.trim() == "" || (exp != "-" && exp < current)){
        return false;
    }

    if (riderName == undefined || riderName.trim() == ""){
        return false;
    }

    if (!confirm(msgDetailInfoSave)){
        return false;
    }

    let riderCode = $("#riderCode").val();
    let riderVehicle = $("#riderVehicle").val();

    $.ajax({
        url: "/changeRiderInfo",
        type: "post",
        data:{
            id: approvalID,
            vehicleNumber: riderVehicle,
            code: riderCode,
            name: riderName
        },
        dataType: "json",
        success: function (data){
            alert(msgChangeSuccess);
        },
        error: function (error){
            alert(msgChangeFailed);
        },
        complete: function (data){
            getApprovalRiderList();
            popClose("#popRiderInfo");
        }
    });
}

// 라이더 승인 상세 조회
function searchRiderApprovalDetail(rowID){
    $.ajax({
        url: "/getRiderApprovalInfo",
        type: "post",
        data:{
            id: rowID
        },
        dataType: "json",
        success: function (data){
            $("#riderLoginID").val(data.loginId);
            $("#riderStore").val(data.riderDetail.riderStore.storeName);
            $("#riderName").val(data.name);
            $("#riderPhone").val(data.phone);
            $("#riderCode").val(data.riderDetail.code);
            $("#riderVehicle").val(data.vehicleNumber);
            $("#riderExpDate").val(data.session == undefined ? "" : dateFormat(data.session.expiryDatetime));
            $("#approvalID").val(data.id);
            $("#approvalStatus").val(data.approvalStatus);

            $("#riderExpDate").datepicker({
                minDate: new Date,
                onSelect: function (selectDate, obj){
                    if(checkExpDate(selectDate, obj)){
                        updateExpDate(selectDate, rowID);
                    }
                }
            });

            if (data.approvalStatus == "5"){
                $("#changePause").text(approval);
                $("#changePause").off().on("click", function (){
                    changeStatus(data.id, "1");
                });
            }else{
                $("#changePause").text(pauseApproval);
                $("#changePause").off().on("click", function (){
                    changeStatus(data.id, "5");
                });
            }

            popOpen("#popRiderInfo");
        },
        error: function (error){
            console.log(error);
        },
        complete: function (data){
        }
    });
}

// 유효기간 선택 후 프로세스
function checkExpDate(selectedDate, obj){
    var date = dateFormat(obj.lastVal == undefined ? $('#jqGrid').getRowData($('#jqGrid').getGridParam('selrow')).expirationDate : obj.lastVal);
    var expDate = dateFormat($('#jqGrid').getRowData($('#jqGrid').getGridParam('selrow')).expirationDate);
    var nowDate = dateFormat(new Date);

    selectedDate = dateFormat(selectedDate);

    if ((date == "-" || date != selectedDate) && !(expDate != "-" && expDate < nowDate)){
        // 알림을 띄운 후 저장 프로세스 생성
        if (confirm(msgChangeExpDate)){
            return true;
        }
    }
    $("#" + obj.id).val(dateFormat(date));

    return false;
}

// 유효기간 달력 OPEN
function showExpDateCalendar(rowID){
    //let date = $.datepicker.formatDate('yyyy-mm-dd', new Date);

    $("#expDate" + rowID).datepicker({
        minDate: new Date,
        onSelect: function (selectDate, obj){
            if(checkExpDate(selectDate, obj)){
                updateExpDate(selectDate, rowID);
            }
        }
    });

    $("#expDate" + rowID).datepicker('show');
}

function dateFormat(date){
    if (date) {
        let d = new Date(date);
        return $.datepicker.formatDate('yy-mm-dd', d);
    } else {
        return "-";
    }
}

function dateFormat2(date){
    if (date) {
        let d = new Date(date);
        //return $.datepicker.formatDate('mm/dd/yy', d);
        return $.datepicker.formatDate('yy-mm-dd', d);
    } else {
        return "-";
    }
}

/**
 * 유효기간 변경
 * */
function updateExpDate(date, rowid){
    loading.show();

    let status = $('#jqGrid').getRowData($('#jqGrid').getGridParam('selrow')).approvalStatus;

    $.ajax({
        url: "/setRiderExpDate",
        type: "post",
        data:{
            id: rowid,
            expiryDate: dateFormat(date)
        },
        dataType: "json",
        success: function (data){
            if (status == "0"){
                riderApprovalStatus(rowid, "1");
            }else{
                alert(msgChangeSuccess);
            }
        },
        error: function (error){
            alert(msgChangeFailed);
        },
        complete: function (data){
            getApprovalRiderList();
            loading.hide();
        }
    });
}

// 라이더 Approval Row 삭제
function deleteApprovalRiderRow(rowid){
    loading.show();

    $.ajax({
        url: "/deleteApprovalRiderRowData",
        type: "post",
        data:{
            id: rowid
        },
        dataType: "json",
        success: function (data){
            alert(msgChangeSuccess);
        },
        error: function (error){
            alert(msgChangeFailed);
        },
        complete: function (data){
            getApprovalRiderList();
            loading.hide();
        }
    });
}

// 엑셀 다운로드
function excelDownload(){
    loading.show();

    // ajax 통신
    $.fileDownload("/excelDownloadApprovalRiderList", {
        httpMethod: "get",
        data:{

        },
        successCallback:function (url){
            loading.hide();
        },
        failCallback: function (responseHtml, url){
            loading.hide();
        }
    });

}

// status value check
function getStatusValue(status){
    let statusValue = "";

    switch (status){
        case "0":
            statusValue = waitApproval;
            break;
        case "1":
            statusValue = successApproval;
            break;
        case "2":
        case "3":
            statusValue = disApproval;
            break;
        case "4":
            statusValue = expiryApproval;
            break;
        case "5":
            statusValue = pauseApproval;
            break;
    }

    return statusValue;
}

// 일시 정지 / 재가동 상태로 적용
function changeStatus(id, status){
    if (id == undefined || status == undefined){
        return;
    }

    if (id == "" || status == ""){
        return;
    }

    // 기본 데이터 추출
    let orgStatus = $("#approvalStatus").val();

    // 허용으로 변경하기 위해서
    if (status == "1"){
        if (orgStatus != "5"){
            return;
        }
    }
    // 일시정지로 변경하기
    else if(status == "5"){
        if (orgStatus != "1"){
            return;
        }
    }else{
        return;
    }

    loading.show();

    $.ajax({
        url: "/changeStatus",
        type: "post",
        data:{
            id: id,
            approvalStatus: status
        },
        dataType: "json",
        success: function (data){
            if (data){
                alert(msgChangeSuccess);
                popClose("#popRiderInfo");
            }else {
                alert(msgChangeFailed);
            }
        },
        error: function (error){
            alert(msgChangeFailed);
            console.log(error);
        },
        complete: function (data){
            getApprovalRiderList();
            loading.hide();
        }
    });

}

/**
 * resetPassword 클릭 시 비밀번호 초기화
 */
function resetRiderPw() {
    $.ajax({
        url : "/putRiderPwReset",
        type : 'put',
        dataType : 'text',
        data : {
            id	: $("#approvalID").val()
        },
        success : function(data){
            alert(msgChangeSuccess);
        }
    });
}

/*]]>*/