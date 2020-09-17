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
            {label: requestDate, name: 'createdDate', width: 80, align: 'center'},
            {label: expDate, name: 'expirationDate', width: 80, align: 'center'},
            {label: setting, name: 'setting', width: 300, align: 'center'},
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

    let expDate = obj.session == undefined ? "-" : dateFormat(obj.session.expiryDatetime);
    let nowDate = dateFormat(new Date);
    let bExpDate = !(expDate != "-" && expDate < nowDate);

    if (bExpDate){
        btn_setDate = "<input type='hidden' name='' id='expDate" + obj.id + "' value='" + (obj.session == undefined ? "-" : dateFormat2(obj.session.expiryDatetime)) + "' class='input datepicker'/>" +
            "<button class='button btn_pink h30 w180 mr10' style='font-size: 14px;' onclick='javascript:showExpDateCalendar(" + obj.id + ")'>" + expDateSetting + "</button>"
    }else{
        btn_setDate ="<button class='button btn_weppep h30 w180 mr10' style='font-size: 12px;' disabled><i class='fa fa-check mr5 t_pink' />" + expDateSetting + "</button>"
    }

    switch (obj.approvalStatus){
        case "0":           // 요청
            btn_approval = "<button class='button btn_pale_green h30 w110 mr20' style='font-size: 14px;' onclick='javascript:riderApprovalStatus(" + obj.id + ", 1)'>" + approval + "</button>";
            if (bExpDate){
                btn_edit = "<button class='button btn_blue h30 w80' style='font-size: 14px;' onclick='javascript:searchRiderApprovalDetail(" + obj.id + ")'>Edit</button>"
            }else{
                btn_edit = "<button class='button btn_blue h30 w80' style='font-size: 14px;' disabled>Edit</button>"
            }
            break;
        case "1":           // 수락
            btn_approval = "<button class='button btn_onahau h30 w110 mr20' style='font-size: 12px;'><i class='fa fa-check mr5' />" + approval + "</button>";
            if (bExpDate){
                btn_edit = "<button class='button btn_blue h30 w80' style='font-size: 14px;' onclick='javascript:searchRiderApprovalDetail(" + obj.id + ")'>Edit</button>"
            }else{
                btn_edit = "<button class='button btn_blue h30 w80' style='font-size: 14px;' disabled>Edit</button>"
            }
            break;
        case "2":           // 거절
        case "3":           // 승인 후 거절
            btn_approval = "<button class='button btn_gray2 h30 w110 mr10' style='font-size: 12px;' disabled><i class='fa fa-check mr5' />" + disapproval + "</button>";
            btn_setDate = "<button class='button btn_gray2 h30 w180 mr10' style='font-size: 12px;' disabled><i class='fa fa-check mr5' />" + expDateSetting + "</button>"
            btn_edit = "<button class='button btn_blue h30 w60 w80' style='font-size: 14px;' disabled>Edit</button>"
            break;
    }

    return  btn_approval + btn_setDate + btn_edit;
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
            alert(msgChangeSuccess);
        },
        error: function (error){
            alert(msgChangeFailed);
        },
        complete: function (data){
            getApprovalRiderList();
        }
    });

}

// popUp 상태 변경
function popUpChangeStatus(){

    let approvalID = $("#approvalID").val();
    let approvalStatus = $("#approvalStatus").val();

    let current = dateFormat(new Date());
    let exp = dateFormat($("#expDate" + approvalID).val());

    if (approvalID == undefined || approvalID.trim() == "" || (exp != "-" && exp < current)){
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

    let current = dateFormat(new Date());
    let exp = dateFormat($("#expDate" + approvalID).val());

    if (approvalID == undefined || approvalID.trim() == "" || (exp != "-" && exp < current)){
        return false;
    }

    if (!confirm(msgDetailInfoSave)){
        return false;
    }

    let riderCode = $("#riderCode").val();
    let riderVehicle = $("#riderVehicle").val();
    let approvalId =  $("#approvalID").val();

    $.ajax({
        url: "/changeRiderInfo",
        type: "post",
        data:{
            id: approvalId,
            vehicleNumber: riderVehicle,
            code: riderCode
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

    $.ajax({
        url: "/setRiderExpDate",
        type: "post",
        data:{
            id: rowid,
            expiryDate: dateFormat(date)
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
    $.fileDownload("/excelDownloadApprovalRiderListforAdmin", {
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

/*]]>*/