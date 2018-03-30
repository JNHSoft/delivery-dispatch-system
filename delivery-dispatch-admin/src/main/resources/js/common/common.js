$(document).ready(function() {
    $('#logout').click(function () {
        $.ajax({
            url : "/logoutProcess",
            success : function () {
                location.href = "/login"
            }
        })
    })
});