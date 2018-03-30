$(document).ready(function() {
//     var supportsWebSockets = 'WebSocket' in window || 'MozWebSocket' in window;
//     if (supportsWebSockets) {
//         var socket = io(websocket_localhost, {
//             path: '/socket.io', // 서버 사이드의 path 설정과 동일해야 한다
//             transports: ['websocket'] // websocket만을 사용하도록 설정
//         });
//         socket.on('message', function (data) {
// //                alert(data);//data 받는부분
//         });
//         $(function () {
//             $('#test').click(function () {
//                 socket.emit('message', 'websocketTest');//data보내는부분
//             });
//         })
//     } else {
//         alert('websocket을 지원하지 않는 브라우저입니다.');
//     }

// 서드 파티 수정 버튼 
$(document).on('click', 'button[name="thirdPartySave"]', function() {
    var $tr = $(this).closest('tr');


    // 수정 버튼을 찾는다.
    if($(this).html()===group_mod){
        // tbody에 input 이 된 놈을 찾는다.
        if($(this).closest('tbody').find('tr').find('td input[type="text"]')){
            // input 이 된 애들중에 tr 를 찾는다.
            var $tbodyTr = $(this).closest('tbody').find('tr');

            
            var $inputTr = $tbodyTr.find('td input[type="text"]').closest('tr');
            // 다른 수정을 눌렀을때 색깔 지운다.
            $inputTr.removeClass('selected');
            // 다른 수정을 누르면 저장 -> 수정
            $inputTr.find('button[name="thirdPartySave"]').html(group_mod);
            // 다른 수정을 누르면 취소 -> 삭제
            $inputTr.find('button[name="thirdPartyDelete"]').html(group_del);

            // closest 가까운 부모를 찾고 input 테그의 기본 값을 가져온다. 주의 input hidden ㅋㅋ
            $tbodyTr.find('td input[type="text"]').closest('td').html($inputTr.find('input[name="defaultThirdPartyName"]').val());
        }
        // tr 객체를 찾아온다.
        var $targetTr = $(this).closest('tr');
        // tr을 찾고 td 에서 name이 같은걸 찾고 html로 input 을 넣어주고 그 값을 value로 가져와서 html 로 다시 뿌린다.
        $targetTr.find('td[name="tdThirdPartyName"]').html('<input type="text" value="'+ $targetTr.find('td[name="tdThirdPartyName"]').html()+'"/>');

        // 수정 -> 저장 변경!
        $(this).html(btn_save);
        // 삭제 -> 취소 변경!
        $targetTr.find('button[name="thirdPartyDelete"]').html(btn_cancel);
        // 선택하는거 색칠
        $targetTr.addClass('selected');
    } 
    // 저장 으로 변경 된 후 버튼을 눌렀을때
    else {
        var thirdPartyParams = {
            // thirdParty id
            id : $tr.data('thirdpartyId'),
            name : $tr.find('td[name="tdThirdPartyName"] input').val()
        }
        if($(this).closest('div').attr('id')==='thirdPratyList'){
            putThirdParty($tr,thirdPartyParams);
        }
    }
});

    // 서드 파티 삭제 버튼을 눌렀을때
    $(document).on('click', 'button[name="thirdPartyDelete"]', function() {
        var $tr = $(this).closest('tr');
        if($(this).html() === group_del){
            var param = {
                id : $tr.data('thirdpartyId')
            };
            console.log(param);
            deleteThirdParty(param);
        }
        // 취소 버튼을 눌렀을때
        else{
            $(this).closest('tr').find('td input[type="text"]').closest('td').html($(this).closest('tr').find('input[name="defaultThirdPartyName"]').val());
            $(this).closest('tr').removeClass('selected');
            $(this).html(group_del);
            $(this).closest('tr').find('button[name="thirdPartySave"]').html(group_mod);
        }
    });
});







$(document).on('click', 'button[id="thirdPartyDel"]', function() {
    alert(befoore_function);
    return false;
});

$(document).on('click', 'button[id="assignedAdvanceMod"]', function() {
    alert(befoore_function);
    return false;
});

$(document).on('click', 'button[id="assignedAdvanceDel"]', function() {
    alert(befoore_function);
    return false;
});

$(document).on('click', 'button[id="assignedRejectMod"]', function() {
    alert(befoore_function);
    return false;
});

$(document).on('click', 'button[id="assignedRejectDel"]', function() {
    alert(befoore_function);
    return false;
});

// 서드 파티 추가
function postThirdParty() {
    if ($('#inpThirdParty').val() == null || $('#inpThirdParty').val() == '') {
        alert(none_parameter);
        return false;
    }
    $.ajax({
        url: "/postThirdParty",
        type: 'get',
        data: {'name':$('#inpThirdParty').val()
        },
        dataType: 'json',
        async : false,
        success: function (data) {
            console.log(data);
            $('#tblThirdParty > tbody:last').append('<tr><td class="t_left" name="tdThirdPartyName">' + data.name + '</td><td><button class="button h20">' + btn_mod + '</button><button class="button h20">' + btn_del + '</button></td></tr>');
            $('#inpThirdParty').val('');
        }
    });
}
// 서드 파티 수정
function putThirdParty($target,param) {

    $.ajax({
        url: "/putThirdParty",
        type: 'put',
        data: param,
        dataType: 'json',
        async : false,
        success: function (data) {
            console.log(data);
            $target.find('td[name="tdThirdPartyName"]').html(param.name);
            $target.find('input[name="defaultThirdPartyName"]').val(param.name);
            $target.removeClass('selected');
            $target.find('button[name="thirdPartySave"]').html(group_mod);
            $target.find('button[name="thirdPartyDelete"]').html(group_del);
        }
    });
}

// 서드 파티 삭제
function deleteThirdParty(param) {
    if(!confirm("你确定吗?")) return;
    $.ajax({
        url: "/deleteThirdParty",
        type: 'put',
        data: param,
        dataType: 'json',
        async : false,
        success: function (data) {
            location.reload();
        }
    });
}










function postAssignedAdvance() {
    if ($('#inpAssignedAdvance').val() == null || $('#inpAssignedAdvance').val() == '') {
        alert(none_parameter);
        return false;
    }

    $.ajax({
        url: "/postAssignedAdvance",
        type: 'get',
        data: {'reason':$('#inpAssignedAdvance').val()
        },
        dataType: 'json',
        async : false,
        success: function (data) {
            console.log(data);
            $('#tblAssignedAdvance > tbody:last').append('<tr><td class="t_left" name="tdAssignedAdvanceName">' + data.reason + '</td><td><button class="button h20">' + btn_mod + '</button><button class="button h20">' + btn_del + '</button></td></tr>');
            $('#inpAssignedAdvance').val('');
        }
    });
}

function postAssignedReject() {
    if ($('#inpAssignedReject').val() == null || $('#inpAssignedReject').val() == '') {
        alert(none_parameter);
        return false;
    }

    $.ajax({
        url: "/postAssignedReject",
        type: 'get',
        data: {'reason':$('#inpAssignedReject').val()
        },
        dataType: 'json',
        async : false,
        success: function (data) {
            console.log(data);
            $('#tblAssignedReject > tbody:last').append('<tr><td class="t_left" name="tdassignedRejectName">' + data.reason + '</td><td><button class="button h20">' + btn_mod + '</button><button class="button h20">' + btn_del + '</button></td></tr>');
            $('#inpAssignedReject').val('');
        }
    });
}

