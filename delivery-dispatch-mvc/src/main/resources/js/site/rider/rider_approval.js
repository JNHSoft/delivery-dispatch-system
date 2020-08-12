/*<![CDATA[*/
let loading = $('<div id="loading"><div><p style="background-color: #838d96"></p></div></div>').appendTo(document.body).hide();
let dateMap = new Map();
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
            for (var key in data){
                if (data.hasOwnProperty(key)){
                    let tmpObj = new Object();

                    console.log(data[key]);

                    tmpObj.No = data[key].id == undefined ? "" : data[key].id;
                    tmpObj.riderID = data[key].loginId == undefined ? "" : data[key].loginId;
                    tmpObj.riderName = data[key].name == undefined ? "" : data[key].name;
                    tmpObj.contactNum = data[key].phone;
                    tmpObj.createdDate = data[key].createdDatetime == undefined ? "" : data[key].createdDatetime;

                    tmpObj.expirationDate = data[key].session == undefined ? "" : data[key].session.expiryDatetime;
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
            {label: 'No', name: 'No', width: 25, key: true, align: 'center'},
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
            console.log("loadCompleted");
        }
    });

    resizeJqGrid("#jqGrid");

    // POPUP 창 닫기 이벤트 등을 적용한다.
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
            btn_approval = "<button class='button btn_pale_green h30 w110 mr10' style='font-size: 14px;' onclick='javascript:riderApprovalStatus(" + obj.id + ")'> Approval</button>";
            btn_setDate = "<input type='hidden' name='datepicker' id='expDate" + obj.id + "' class='input picker' />" +
                        "<button class='button btn_pink h30 w180 mr10' style='font-size: 14px;' onclick='javascript:showExpDateCalendar(this, " + obj.id + ")'>Validity period Setting</button>"
            btn_edit = "<button class='button btn_blue h30 w80' style='font-size: 14px' onclick='javascript:searchRiderApprovalDetail(" + obj.id + ")'>Edit</button>"
            break;
        case "1":           // 수락
            btn_approval = "<button class='button btn_onahau h30 w110 mr10' style='font-size: 12px;'><i class='fa fa-check mr5' />Approval</button>";
            btn_setDate = "<input type='hidden' name='datepicker' id='expDate" + obj.id + "' class='input picker' />" +
                        "<button class='button btn_weppep h30 w180 mr10' style='font-size: 12px;' onclick='javascript:showExpDateCalendar(this, " + obj.id + ")'><i class='fa fa-check mr5 t_pink' />Validity period Setting</button>"
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
function riderApprovalStatus(rowID){
    //changeApprovalStatus
    loading.show();
    $.ajax({
        url: "/changeApprovalStatus",
        type: "post",
        data:{
            id: rowID
        },
        dataType: "json",
        success: function (data){
            console.log(data);
        },
        error: function (error){
            console.log(error);
        },
        complete: function (data){
            console.log(data);
            getApprovalRiderList();
            loading.hide();
        }
    });

}

// 라이더 승인 상세 조회
function searchRiderApprovalDetail(rowID){
    console.log(rowID);

    $.ajax({
        url: "/getRiderApprovalInfo",
        type: "post",
        data:{
            id: rowID
        },
        dataType: "json",
        success: function (data){
            console.log("success : ");
            console.log(data);

            popOpen("#popRiderInfo");
        },
        error: function (error){
            console.log(error);
        },
        complete: function (data){
            console.log("complete");
            console.log(data);
        }
    });
}

function showExpDateCalendar(obj, rowID){
    console.log("showExpDateCalendar");
    $("#expDate" + rowID).datepicker({
        onSelect: function (selectDate, obj){
            checkExpDate(rowID, selectDate, obj);
        }
    });
    $("#expDate" + rowID).datepicker('show');
}

function checkExpDate(rowid, selectedDate, obj){
    var date = dateMap.has(rowid) ? dateMap.get(rowid) : "";

    if (date != selectedDate){
        dateMap.set(rowid, selectedDate);
    }

    // 알림을 띄운 후 저장 프로세스 생성

    // 저장 프로세스 만들기
}

/*]]>*/