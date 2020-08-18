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

            if (data.brandCode != undefined && data.brandCode != ''){
                //$('.imgTitle').attr("src", "../resources/images/common/logo" + data.brandCode + ".jpg");
                $('.imgTitle').attr("src", data.brandImg);
                $('.imgTitle').attr("alt", data.brandName);
                // if (data.brandCode == "1"){
                //     $('#menu_default').remove()
                //     $('#menu_kfc').css('display', '');
                // }else{
                //     $('#menu_kfc').remove();
                //     $('#menu_default').css('display', '');
                // }
            }else{
                // $('#menu_kfc').remove();
                // $('#menu_default').css('display', '');
            }
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