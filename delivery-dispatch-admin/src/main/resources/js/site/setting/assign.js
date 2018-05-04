$(document).ready(function() {
//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@서드 파티
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

// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@우선 배정
    // 우선 배정 수정 버튼
    $(document).on('click', 'button[name="assignedAdvanceSave"]', function() {
        var $tr = $(this).closest('tr');

        if($(this).html() === group_mod){
            // tbody에 input 이 된 놈을 찾는다.
            if($(this).closest('tbody').find('tr').find('td input[type="text"]')){

                // input 이 된 애들중에 tr 를 찾는다.
                var $ipTr = $(this).closest('tbody').find('tr').find('td input[type="text"]');
                $ipTr.closest('tr');
                // 다른 수정을 눌렀을때 색깔 지운다.
                $ipTr.closest('tr').removeClass('selected');
                // 다른 수정을 누르면 저장 -> 수정
                $(this).html(group_mod);
                // 다른 수정을 누르면 취소 -> 삭제
                $ipTr.closest('tr').find('button[name="assignedAdvanceDelete"]').html(group_del);

                // closest 가까운 부모를 찾고 input 테그의 기본 값을 가져온다. 주의 input hidden ㅋㅋ
                $ipTr.closest('td').html($(this).closest('tr').find('input[name="defaultAssignedAdvanceName"]').val());
            }
            // tr 객체를 찾아온다.
            var $targetTr = $(this).closest('tr');
            // tr을 찾고 td 에서 name이 같은걸 찾고 html로 input 을 넣어주고 그 값을 value로 가져와서 html 로 다시 뿌린다.
            $targetTr.find('td[name="tdAssignedAdvanceName"]').html('<input type="text" value="'+ $targetTr.find('td[name="tdAssignedAdvanceName"]').html()+'"/>')
            // 수정 -> 저장 변경!
            $(this).html(btn_save);
            // 삭제 -> 취소 변경!
            $targetTr.find('button[name="assignedAdvanceDelete"]').html(btn_cancel);
            // 선택하는거 색칠
            $targetTr.addClass('selected');
        }
        // 저장 으로 변경 된 후 버튼을 눌렀을때
        else {
            var assignedAdvanceParams = {
                id : $tr.data('assignedadvanceId'),
                reason : $tr.find('td[name="tdAssignedAdvanceName"] input').val()
            }
            if($(this).closest('div').attr('id')==='assignedAdvanceList'){
                putAssignedAdvance($tr,assignedAdvanceParams);
            }
        }
    });
    // 우선 배정 삭제 버튼을 눌렀을때
    $(document).on('click', 'button[name="assignedAdvanceDelete"]', function() {
        var $tr = $(this).closest('tr');

        if($(this).html() === group_del){
            var param = {
                id : $tr.data('assignedadvanceId')
            };
            deleteAssignedAdvance(param);
        }
        // 취소 버튼을 눌렀을때
        else{
            $(this).closest('tr').find('td input[type="text"]').closest('td').html($(this).closest('tr').find('input[name="defaultAssignedAdvanceName"]').val());
            $(this).closest('tr').removeClass('selected');
            $(this).html(group_del);
            $(this).closest('tr').find('button[name="assignedAdvanceSave"]').html(group_mod);
        }
    });
// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@배정 거절 사유
// 배정 거절 사유 수정시
    $(document).on('click', 'button[name="assignedRejectSave"]', function() {
        var $tr = $(this).closest('tr');

        if($(this).html() === group_mod){
            // tbody에 input 이 된 놈을 찾는다.
            if($(this).closest('tbody').find('tr').find('td input[type="text"]')){

                // input 이 된 애들중에 tr 를 찾는다.
                var $ipTr = $(this).closest('tbody').find('tr').find('td input[type="text"]');
                $ipTr.closest('tr');
                // 다른 수정을 눌렀을때 색깔 지운다.
                $ipTr.closest('tr').removeClass('selected');
                // 다른 수정을 누르면 저장 -> 수정
                $(this).html(group_mod);
                // 다른 수정을 누르면 취소 -> 삭제
                $ipTr.closest('tr').find('button[name="assignedRejectDelete"]').html(group_del);

                // closest 가까운 부모를 찾고 input 테그의 기본 값을 가져온다. 주의 input hidden ㅋㅋ
                $ipTr.closest('td').html($(this).closest('tr').find('input[name="defaultassignedRejectName"]').val());
            }
            // tr 객체를 찾아온다.
            var $targetTr = $(this).closest('tr');
            // tr을 찾고 td 에서 name이 같은걸 찾고 html로 input 을 넣어주고 그 값을 value로 가져와서 html 로 다시 뿌린다.
            $targetTr.find('td[name="tdassignedRejectName"]').html('<input type="text" value="'+ $targetTr.find('td[name="tdassignedRejectName"]').html()+'"/>')
            // 수정 -> 저장 변경!
            $(this).html(btn_save);
            // 삭제 -> 취소 변경!
            $targetTr.find('button[name="assignedRejectDelete"]').html(btn_cancel);
            // 선택하는거 색칠
            $targetTr.addClass('selected');
        }
        // 저장 으로 변경 된 후 버튼을 눌렀을때
        else {
            var assignedRejectParams = {
                id : $tr.data('assignedrejectId'),
                reason : $tr.find('td[name="tdassignedRejectName"] input').val()
            }
            if($(this).closest('div').attr('id')==='assignedRejectList'){
                putAssignedReject($tr,assignedRejectParams);
            }
        }
    });
    // 배정거절 삭제 버튼을 눌렀을때
    $(document).on('click', 'button[name="assignedRejectDelete"]', function() {
        var $tr = $(this).closest('tr');

        if($(this).html() === group_del){
            var param = {
                id : $tr.data('assignedrejectId')
            };
            deleteAssignedReject(param);
        }
        // 취소 버튼을 눌렀을때
        else{
            $(this).closest('tr').find('td input[type="text"]').closest('td').html($(this).closest('tr').find('input[name="defaultassignedRejectName"]').val());
            $(this).closest('tr').removeClass('selected');
            $(this).html(group_del);
            $(this).closest('tr').find('button[name="assignedRejectSave"]').html(group_mod);
        }
    });
    
    
    
    
    
    
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
            $('#tblThirdParty > tbody:last').append('<tr><td class="t_left" name="tdThirdPartyName">' + data.name + '</td><td><button class="button h20">' + group_mod + '</button><button class="button h20">' + group_del + '</button></td></tr>');
            $('#inpThirdParty').val('');
            location.reload();
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
// 우선 배정 추가
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
            $('#tblAssignedAdvance > tbody:last').append('<tr><td class="t_left" name="tdAssignedAdvanceName">' + data.reason + '</td><td><button class="button h20">' + group_mod + '</button><button class="button h20">' + group_del + '</button></td></tr>');
            $('#inpAssignedAdvance').val('');
        }
    });
}

// 우선 배정 수정
function putAssignedAdvance($target,param) {
    $.ajax({
        url: "/putAssignedAdvance",
        type: 'put',
        data: param,
        dataType: 'json',
        async : false,
        success: function (data) {
            $target.find('td[name="tdAssignedAdvanceName"]').html(param.reason);
            $target.find('input[name="defaultAssignedAdvanceName"]').val(param.reason);
            $target.removeClass('selected');
            $target.find('button[name="assignedAdvanceSave"]').html(group_mod);
            $target.find('button[name="assignedAdvanceDelete"]').html(group_del);
        }
    });
}

// 우선 배정 삭제
function deleteAssignedAdvance(param) {
    if(!confirm("你确定吗?")) return;
    $.ajax({
        url: "/deleteAssignedAdvance",
        type: 'put',
        data: param,
        dataType: 'json',
        async : false,
        success: function (data) {
            location.reload();
        }
    });
}

// 배정 거절 사유 추가
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
            $('#tblAssignedReject > tbody:last').append('<tr><td class="t_left" name="tdassignedRejectName">' + data.reason + '</td><td><button class="button h20">' + group_mod + '</button><button class="button h20">' + group_del + '</button></td></tr>');
            $('#inpAssignedReject').val('');
        }
    });
}

// 배정 거절 사유 수정
function putAssignedReject($target,param) {
    $.ajax({
        url: "/putAssignedReject",
        type: 'put',
        data: param,
        dataType: 'json',
        async : false,
        success: function (data) {
            $target.find('td[name="tdassignedRejectName"]').html(param.reason);
            $target.find('input[name="defaultassignedRejectName"]').val(param.reason);
            $target.removeClass('selected');
            $target.find('button[name="assignedRejectSave"]').html(group_mod);
            $target.find('button[name="assignedRejectDelete"]').html(group_del);
        }
    });
}

// 배정 거절 사유 삭제
function deleteAssignedReject(param) {
    if(!confirm("你确定吗?")) return;
    $.ajax({
        url: "/deleteAssignedReject",
        type: 'put',
        data: param,
        dataType: 'json',
        async : false,
        success: function (data) {
            location.reload();
        }
    });
}