/*$(document).ready(function() {
    var supportsWebSockets = 'WebSocket' in window || 'MozWebSocket' in window;
    if (supportsWebSockets) {
        var socket = io(websocketHost, {
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
});*/

$(document).ready(function() {
    if ($('#defaultSoundStatus').val() == 'true') {
        $('#defaultSoundChk').prop('checked', true);
        $('.adminSound').hide();
        $('.defaultSound').show();
    } else {
        $('#defaultSoundChk').prop('checked', false);
        $('.defaultSound').hide();
        $('.adminSound').show();
    }

    $('#defaultSoundChk').click(function() {
        defaultSoundChk(this);
    });
});

function defaultSoundChk(defaultSound) {
    if(defaultSound.checked) {
        $('.adminSound').hide();
        $('.defaultSound').show();
    } else {
        $('.defaultSound').hide();
        $('.adminSound').show();
    }
}

function deleteAlarm(alarmId) {
    $.ajax({
        url: "/deleteAlarm",
        type: 'get',
        data: {'id':alarmId
        },
        dataType: 'json',
        async : false,
        success: function (data) {
            location.reload();
        }
    });
}

function preview(fileName, defaultSoundStatus) {
    if (fileName == null && fileName == '') {
        alert(result_none);
        return false;
    }

    var subDir = regionLocale;
    if (selectedLang != null) {
        if (selectedLang == 'ko_KR' || selectedLang == 'en_US' || selectedLang == 'zh_TW' || selectedLang == 'zh_HK') {
            subDir = selectedLang;
        } else {
            subDir = 'en_US';
        }
    }

    if (defaultSoundStatus) {
        var audio = new Audio('/alarmFiles/alarm/default/'+subDir+'/'+fileName);
        audio.play();
    } else {
        var audio = new Audio('/alarmFiles/alarm/'+fileName);
        audio.play();
    }
    
}