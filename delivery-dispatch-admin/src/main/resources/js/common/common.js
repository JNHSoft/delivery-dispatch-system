$(document).ready(function() {
    $('#logout').click(function () {
        $.ajax({
            url : "/logoutProcess",
            success : function () {
                location.href = "/login";
            }
        })
    })
    $.ajax({
        url : "/adminInfo",
        async : false,
        success : function (data) {
            $('#myAdminName').text(data.name);
            $('#defaultSoundStatus').val(data.defaultSoundStatus);
        }
    });
});

function regOrderIdReduce(regOrderId) {
    if(regOrderId.indexOf('-') != -1){
        var reduceId = regOrderId.split('-');
        if(reduceId.length >= 1){
            return reduceId[reduceId.length-2]+'-'+ reduceId[reduceId.length-1];
        }
    }else {
        return regOrderId;
    }
}