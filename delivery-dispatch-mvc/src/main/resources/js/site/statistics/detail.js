let loading= $('<div id="loading"><div><p style="background-color: #838d96"/></div></div>').appendTo(document.body).hide();
$(function () {
    let date = $.datepicker.formatDate('yy-mm-dd', new Date);
    $('#day1, #day2').val(date);

    $('#day1').datepicker({
        maxDate : date,
        onClose: function(selectedDate) {
            $('#day2').datepicker('option', 'minDate', selectedDate);
            getStoreOrderList();
        }
    });

    $('#day2').datepicker({
        minDate : date,
        onClose: function( selectedDate ) {
            $('#day1').datepicker('option', 'maxDate', selectedDate);
            getStoreOrderList();
        }
    });

    getStoreOrderList();
});

// Store Order List Data
function getStoreOrderList() {
    let diffDate = Math.ceil((new Date($('#day2').val()).getTime() - new Date($('#day1').val()).getTime()) / (1000*3600*24));
    if (diffDate > 31){
        return;
    }

    let myData = [];
    loading.show();

    // Call Ajax
    $.ajax({
        url: "/getStoreOrderList",
        type: "get",
        data:{
            startDate: $("#day1").val(),
            endDate: $("#day2").val()
        },
        datatype: "json",
        success: function (data) {
            let rowNum = 0;

            // 데이터 입력
            for (let key in data){
                if (data.hasOwnProperty(key)){
                    let tmpData = new Object();
                    tmpData.No = ++rowNum;
                    tmpData.origin_reg_order_id = data[key].regOrderId;
                    tmpData.reg_order_id = regOrderIdReduce(data[key].regOrderId);
                    tmpData.state = getStatusName(data[key].status);
                    tmpData.id = data[key].id;
                    tmpData.createdDatetime = timeSet(data[key].createdDatetime);
                    // tmpData.address = data[key].address;
                    // tmpData.message = (!data[key].message)? "-":data[key].message;
                    // tmpData.phone = (!data[key].phone)? "-":data[key].phone;
                    tmpData.assignedDatetime = timeSet(data[key].assignedDatetime);
                    tmpData.pickedUpDatetime = timeSet(data[key].pickedUpDatetime);
                    tmpData.arrivedDatetime = timeSet(data[key].arrivedDatetime);
                    tmpData.completedDatetime = timeSet(data[key].completedDatetime);
                    tmpData.returnDatetime = timeSet(data[key].returnDatetime);

                    // 21.04.07 라이더 쉐어 유무
                    tmpData.sharedRider = data[key].rider.sharedStatus;

                    myData.push(tmpData);
                }
            }

            // 데이터 화면 출력
            if (myData != null){
                jQuery("#jqGrid").jqGrid('clearGridData');
                jQuery("#jqGrid").jqGrid("setGridParam", {data:myData, page: 1});
                jQuery("#jqGrid").trigger("reloadGrid");
            }

            $("#jqGrid").jqGrid({
                datatype: "local",
                data: myData,
                colModel:[
                   {label: order_reg_order_id, name: 'reg_order_id', width: 80, align: 'center'},
                   {label: order_reg_order_id, name: 'origin_reg_order_id', width: 80, align: 'center', hidden: true},
                   {label: order_status, name: 'state', width: 80, align: 'center'},
                   {label: order_id, name: 'id', width: 80, align: 'center', hidden: true},
                   {label: order_created, name: 'createdDatetime', width: 80, align: 'center'},
                   // {label: order_address, name: 'address', width: 200},
                   // {label: order_message, name: 'message', width: 80, align: 'center'},
                   // {label: order_customer_phone, name: 'phone', width: 80, align: 'center'},
                   {label: order_assigned, name: 'assignedDatetime', width: 80, align: 'center'},
                   {label: order_pickedup, name: 'pickedUpDatetime', width: 80, align: 'center'},
                   {label: order_arrived, name: 'arrivedDatetime', width: 80, align: 'center'},
                   {label: order_completed, name: 'completedDatetime', width: 80, align: 'center'},
                   {label: order_return, name: 'returnDatetime', width: 80, align: 'center'},
                    {label: rider_shared, name: 'sharedRider', width: 80, align: 'center'}
                   ],
                height: 680,
                autowidth: true,
                rowNum: 20,

                pager: "#jqGridPager"
            });

            resizeJqGrid("#jqGrid");

            loading.hide();
        }
    });

    function getStatusName(status) {
        var str = '';
        if (status == 0 || status == 5) {
            str = '<i class="ic_txt ic_green">' + status_new + '</i>';
        }
        else if (status == 1) {
            str = '<i class="ic_txt ic_blue">' + status_assigned + '</i>';
        }
        else if (status == 2) {
            str = '<i class="ic_txt ic_blue">' + status_pickedup + '</i>';
        }
        // 2020.05.08 Arrived Status 추가
        else if (status == 6) {
            str = '<i class="ic_txt ic_blue">' + status_arrived + '</i>';
        }
        else if (status == 3) {
            str = '<i class="ic_txt">' + status_completed + '</i>';
        }
        else {
            str = '<i class="ic_txt ic_red">' + status_canceled + '</i>';
        }
        return str;
    }

    function timeSet(time) {
        if (time) {
            var t = time.split(/[- :]/);
            var d = new Date(t[0], t[1]-1, t[2], t[3], t[4], t[5]);
            return $.datepicker.formatDate('mm/dd ', d) + ('0' + d.getHours()).slice(-2) + ':' + ('0' + d.getMinutes()).slice(-2);
        } else {
            return "-";
        }
    }
}

// Store Order List Data Download
function excelDownloadByOrderList() {
    // let count = $("#jqGrid").getGridParam("records");
    let startDate = $('#day1').val();
    let endDate = $('#day2').val();

    let diffDate = Math.ceil((new Date(endDate).getTime() - new Date(startDate).getTime()) / (1000*3600*24));
    if (diffDate > 31){
        return;
    }

    loading.show();

    // ajax 통신
    $.fileDownload("/excelDownloadByOrderList",{
        httpMethod: "get",
        data:{
            startDate: startDate,
            endDate: endDate
        },
        successCallback: function (url) {
            loading.hide();
        },failCallback: function (responseHtml, url) {
            loading.hide();
        }
    });

}