/*<![CDATA[*/
let loading = $('<div id="loading"><div><p style="background-color: #838d96"></p></div></div>').appendTo(document.body).hide();
// Start Function after page loading completed.
$(function (){
    getApprovalRiderList();
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
                    tmpObj.createdDate = data[key].createdDatetime == undefined ? "-" : dateFormat(data[key].createdDatetime);

                    tmpObj.expirationDate = data[key].session == undefined ? "-" : dateFormat(data[key].session.expiryDatetime);
                    tmpObj.setting = makeRowButton(data[key]);

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
            {label: 'No', name: 'id', width: 25, key: true, align: 'center', hidden: true},
            {label: 'No', name: 'No', width: 25, align: 'center'},
            {label: 'Rider ID', name: 'riderID', width: 80, align: 'center'},
            {label: 'Rider Name', name: 'riderName', width: 80, align: 'center'},
            {label: 'Contact No.', name: 'contactNum', width: 80, align: 'center'},
            {label: 'Request Date', name: 'createdDate', width: 80, align: 'center'},
            {label: 'Expiration Date', name: 'expirationDate', width: 80, align: 'center'},
            {label: 'Setting', name: 'setting', width: 300, align: 'center'},
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

    var btn_approval = "";
    var btn_setDate = "";
    var btn_edit = "";

    switch (obj.approvalStatus){
        case "0":           // 요청
            btn_approval = "<button class='button btn_pale_green h30 w110 mr10' style='font-size: 14px;' onclick='javascript:riderApprovalStatus(" + obj.id + ", 1)'> Approval</button>";
            btn_setDate = "<input type='hidden' name='datepicker' id='expDate" + obj.id + "' value='" + (obj.session == undefined ? "" : dateFormat2(obj.session.expiryDatetime)) + "' class='input picker' />" +
                        "<button class='button btn_pink h30 w180 mr10' style='font-size: 14px;' onclick='javascript:showExpDateCalendar(" + obj.id + ")'>Validity period Setting</button>"
            btn_edit = "<button class='button btn_blue h30 w80' style='font-size: 14px' onclick='javascript:searchRiderApprovalDetail(" + obj.id + ")'>Edit</button>"
            break;
        case "1":           // 수락
            btn_approval = "<button class='button btn_onahau h30 w110 mr10' style='font-size: 12px;'><i class='fa fa-check mr5' />Approval</button>";
            btn_setDate = "<input type='hidden' name='datepicker' id='expDate" + obj.id + "' value='" + (obj.session == undefined ? "" : dateFormat2(obj.session.expiryDatetime)) + "' class='input picker' />" +
                        "<button class='button btn_weppep h30 w180 mr10' style='font-size: 12px;' onclick='javascript:showExpDateCalendar(" + obj.id + ")'><i class='fa fa-check mr5 t_pink' />Validity period Setting</button>"
            btn_edit = "<button class='button btn_blue h30 w80' style='font-size: 14px;' onclick='javascript:searchRiderApprovalDetail(" + obj.id + ")'>Edit</button>"
            break;
        case "2":           // 거절
        case "3":           // 승인 후 거절
            btn_approval = "<button class='button btn_gray2 h30 w110 mr10' style='font-size: 12px;' disabled><i class='fa fa-check mr5' />Disapproval</button>";
            btn_setDate = "<button class='button btn_gray2 h30 w180 mr10' style='font-size: 12px;' disabled><i class='fa fa-check mr5' />Validity period Setting</button>"
            btn_edit = "<button class='button btn_blue h30 w80' style='font-size: 14px' disabled>Edit</button>"
            break;
    }

    return  btn_approval + btn_setDate + btn_edit;
}

// 상태 변경
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
        url: "/changeApprovalStatus",
        type: "post",
        data:{
            id: rowID,
            approvalStatus: status
        },
        dataType: "json",
        success: function (data){
            alert("상태 변경 완료");
        },
        error: function (error){
            alert("상태 변경 오류");
        },
        complete: function (data){
            getApprovalRiderList();
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
            $("#riderStore").val(data.riderDetail.riderStore.id);
            $("#riderName").val(data.name);
            $("#riderPhone").val(data.phone);
            $("#riderCode").val(data.riderDetail.code);
            $("#riderVehicle").val(data.vehicleNumber);
            $("#riderExpDate").val(data.session == undefined ? "" : dateFormat(data.session.expiryDatetime));

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
        if (confirm("변경하신 날짜로 저장하시겠습니까?")){
            return true;
        }
    }
    $("#" + obj.id).val(dateFormat2(date));

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
        return $.datepicker.formatDate('mm/dd/yy', d);
    } else {
        return "-";
    }
}

/**
 * 유효기간 변경
 * */
function updateExpDate(date, rowid){
    loading.show();

    $.ajax({
        url: "/setRiderExpDate",
        type: "post",
        data:{
            id: rowid,
            expiryDate: dateFormat(date)
        },
        dataType: "json",
        success: function (data){
            alert("상태 변경 완료");
        },
        error: function (error){
            alert("상태 변경 실패");
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

        },
        failCallback: function (responseHtml, url){

        },
        complete: function (){
            loading.hide();
        }
    });

}

/*]]>*/