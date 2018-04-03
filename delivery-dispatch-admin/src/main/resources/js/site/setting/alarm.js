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

function deleteAlarm(alarmId) {
    $.ajax({
        url: "/deleteAlarm",
        type: 'get',
        data: {'id':alarmId
        },
        dataType: 'json',
        async : false,
        success: function (data) {
            console.log(data);
            location.reload();
        }
    });
}

function preview(fileName) {
    if (fileName == null && fileName == '') {
        alert(result_none);
        return false;
    }
    
    var audio = new Audio('/alarmFiles/alarm/'+fileName);
    audio.play();
}