$(document).ready(function() {
    var supportsWebSockets = 'WebSocket' in window || 'MozWebSocket' in window;
    if (supportsWebSockets) {
        var socket = io(websocket_localhost, {
            path: '/socket.io', // 서버 사이드의 path 설정과 동일해야 한다
            transports: ['websocket'] // websocket만을 사용하도록 설정
        });
        socket.on('message', function (data) {
//                alert(data);//data 받는부분
        });
        $(function () {
            $('#test').click(function () {
                socket.emit('message', 'websocketTest');//data보내는부분
            });
        })
    } else {
        alert('websocket을 지원하지 않는 브라우저입니다.');
    }
});

$(document).on('click', 'button[id="thirdPartyMod"]', function() {
    alert(befoore_function);
    return false;
    // var $targetTr = $(this).closest('tbody').find('tr');
    // if($targetTr.find('td[name="tdThirdPartyName"] input')) {
    //     var tdThirdPartyName = $targetTr.find('td[name="tdThirdPartyName"] input').closest('tr').find('input[name="defaultThirdPartyName"]').val();
    //     alert(tdThirdPartyName);
    //     $targetTr.find('td[name="tdThirdPartyName"] input').closest('td').html(tdThirdPartyName);
    // }
    // $tr.find('td[name="tdThirdPartyName"]').html('<input type="text" class="input w_100" value="' + $tr.find('td[name="tdThirdPartyName"]').html() +'"/>');
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

function putThirdParty() {
    var $targetTr = $(this).closest('tbody').find('tr');
    alert($(this).html());
    if($targetTr.find('td[name="tdThirdPartyName"]')) {
        alert($targetTr.find('td[name="tdThirdPartyName"]').text());
    }

    // $.ajax({
    //     url: "/postThirdParty",
    //     type: 'get',
    //     data: {'name':$('#inpThirdParty').val()
    //     },
    //     dataType: 'json',
    //     async : false,
    //     success: function (data) {
    //         console.log(data);
    //         $('#tblThirdParty > tbody:last').append('<tr><td class="t_left">' + data.name + '</td><td><button class="button h20">' + btn_mod + '</button><button class="button h20">' + btn_del + '</button></td></tr>');
    //         $('#inpThirdParty').val('');
    //     }
    // });
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

