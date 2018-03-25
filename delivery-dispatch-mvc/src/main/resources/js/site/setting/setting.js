/*<![CDATA[*/
function putStoreInfo() {
    var id = $('#storeId').val();
    var storePhone = $('#storePhone').val();
    var name = $('#mangerName').val();
    var phone = $('#phone').val();
    var address = $('#address').val();
    var detailAddress = $('#detailAddress').val();
    console.log(id, storePhone, name, phone, address,detailAddress)
    $.ajax({
        url: '/putStoreInfo',
        type: 'put',
        data: {
            id : id,
            storePhone : storePhone,
            name : name,
            phone : phone,
            address : address,
            detailAddress : detailAddress
        },
        dataType : 'json',
        success : function (data) {
            alert('success');
            location.href = "/setting-account";
        }
    });
}
function putStoreAssignInfo() {
    var thirdParty ="";
    var checkboxs = [];
    $('input[type="checkbox"]:checked').each(function(index, element) {
        checkboxs.push($(element).val());
        console.log(index, element, $(element).val());
    });

    var assignmentLimit = $("#maxSelect option:selected").val();
    var assignmentStatus = $('input[name="assign"]:checked').val();
    // console.log(thirdParty, assignmentLimit, assignmentStatus, checkboxs.join('|'));
    thirdParty = checkboxs.join('|');
    $.ajax({
        url: '/putAssign',
        type: 'put',
        data: {
            thirdParty : thirdParty,
            assignmentLimit : assignmentLimit,
            assignmentStatus : assignmentStatus

        },
        dataType : 'json',
        success : function (data) {
            alert('success');
            location.href = "/setting-assign";
        }
    });
}

function getStatisticsInfo(orderId) {
    $.ajax({
        url : "/getStatisticsInfo",
        type : 'get',
        data : {
            id : orderId
        },
        dataType : 'json',
        success : function (data) {
            console.log(data);
            if (data.status == 0 || data.status == 5) {
                $status = '<i class="ic_txt ic_green">' + status_new + '</i>';
            }
            else if (data.status == 1) {
                $status = '<i class="ic_txt ic_blue">' + status_assigned + '</i>';
            }
            else if (data.status == 2) {
                $status = '<i class="ic_txt ic_blue">' + status_pickedup + '</i>';
            }
            else if (data.status == 3) {
                $status = '<i class="ic_txt">' + status_completed + '</i>';
            }
            else {
                $status = '<i class="ic_txt ic_red">' + status_canceled + '</i>';
            }
            $('.tit').html('<h2>'+order_detail + ' - '+ data.id + '</h2>'+$status);
            $('.tit').attr("orderId", data.id);

            $('#createdDatetime').html(timeSet(data.createdDatetime));
            $('#reservationDatetime').html(timeSet(data.reservationDatetime));
            $('#assignedDatetime').html(timeSet(data.assignedDatetime));
            $('#pickedUpDatetime').html(timeSet(data.pickedUpDatetime));
            $('#completedDatetime').html(timeSet(data.completedDatetime));
            $('#passtime').html(minusTimeSet(data.createdDatetime, data.completedDatetime));
            $('#menuName').html(data.menuName);
            $('#cookingTime').html(data.cookingTime);
            $('#menuPrice').html(data.menuPrice);
            $('#deliveryPrice').html(data.deliveryPrice);
            $('#totalPrice').html(data.totalPrice);
            if(data.paid == 0){
                $paid = order_payment_cash;
            }else if(data.paid == 1){
                $paid = order_payment_card;
            }else if(data.paid ==2){
                $paid = order_payment_prepayment;
            }else{
                $paid = order_payment_service;
            }
            $('#paid').val($paid);
            if(data.combinedOrderId != null){
                $('#combinedOrder').val(data.combinedOrderId);
            }

            if(data.riderId != null){
                $('#riderName').val(data.rider.name);
                $('#riderPhone').val(data.rider.phone);
            }else {
                $('#riderName').val("-");
                $('#riderPhone').val("-");
            }
            if(data.memo != null){
                $('#memo').html(data.message);
            }
            $('#userPhone').html(data.phone);
            $('#userAddress').html(data.address);
            $('#distance').html(data.distance);
            map.setCenter({lat: parseFloat(data.latitude), lng: parseFloat(data.longitude)});
            marker.setPosition({lat: parseFloat(data.latitude), lng: parseFloat(data.longitude)});
        }
    });
}

function getRiderInfo(riderId) {
    $.ajax({
        url : "/getRiderInfo",
        type : 'get',
        data : {
            id : riderId
        },
        dataType : 'json',
        success : function (data) {
            console.log(data);
            $('#userId').val(data.loginId);
            $('#emergencyPhone').val(data.emergencyPhone);
            $('#address').val(data.address);
            $('#code').val(data.code);
            $('#riderStoreName').text($('#storeName').val());
            $('#riderName').val(data.name);
            if(data.workingHours){
                var splitWorkingHours = (data.workingHours).split('|');
                $('#workingHour1').val(splitWorkingHours[0]/60).prop('selected', true);
                $('#workingHour2').val(splitWorkingHours[1]/60).prop('selected', true);
            }else{
                $('#workingHour1').val('empty').prop('selected', true);
                $('#workingHour2').val('empty').prop('selected', true);
            }
            $('input[name="gender"]:input[value="'+data.gender+'"]').prop('checked', true);
            if(data.restHours){
                var restHours = (data.restHours).split('|');
                for(var i = 0; i < data.restHours.length; i++){
                    $('input[name="restChk"]:input[value="'+restHours[i] +'"]').prop('checked', true);
                }
            }
            $('input[name="teenager"]:input[value="'+data.teenager+'"]').prop('checked', true);
            $('#phone').val(data.phone);
            $('#vehicleNumber').val(data.vehicleNumber);
            $('#riderId').val(riderId);
        }
    });
}

function putRIderInfo() {
    var id = $('#riderId').val();
    var emergencyPhone = $('#emergencyPhone').val();
    var loginPw = $('#riderPw').val();
    var address = $('#address').val();
    var name = $('#riderName').val();
    var workingTime = $('#workingHour1').val() + "|" + $('#workingHour2').val();
    var gender = $('input[name="gender"]:checked').val();
    var teenager = $('input[name="teenager"]:checked').val();
    var tmpHours = [];
    $('input[name="restChk"]:checked').each(function(index, element) {
        tmpHours.push($(element).val());
        console.log(tmpHours);
    });
    var restHours = tmpHours.join("|");
    var phone = $('#phone').val();
    var vehicleNumber = $('#vehicleNumber').val();
    console.log(emergencyPhone, loginPw, address, name,workingTime, gender ,teenager,restHours,phone,vehicleNumber);
    $.ajax({
        url: '/putRIderInfo',
        type: 'put',
        data: {
            id : id,
            emergencyPhone : emergencyPhone,
            loginPw : loginPw,
            address : address,
            name : name,
            workingTime : workingTime,
            gender : gender,
            teenager : teenager,
            restHours : restHours,
            phone : phone,
            vehicleNumber : vehicleNumber
        },
        dataType : 'json',
        success : function (data) {
            alert('success');
            getRiderInfo(id);
        }
    });
}
function putStoreAlarm() {
    var id = $('#storeId').val();
    var tmpAlarm = [];
    $('input[type="radio"]:checked').each(function(index, element) {
        tmpAlarm.push($(element).val());
        console.log(tmpAlarm);
    });
    var alarm = tmpAlarm.join("|");
    $.ajax({
        url: '/putStoreAlarm',
        type: 'put',
        data: {
            id : id,
            alarm : alarm
        },
        dataType : 'json',
        success : function (data) {
            alert('success');
            location.href="/setting-alarm";
        }
    });
}
/*]]>*/