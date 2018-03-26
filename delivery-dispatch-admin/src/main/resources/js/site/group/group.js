$(function() {
    //<![CDATA[
    $(document).on('click', 'button[name="save"]', function() {
        var $tr = $(this).closest('tr');
        $(this).closest('tbody').find('tr').removeClass('selected');
        $tr.addClass('selected'); // class 에 add 해준다.
        
        // 수정을 눌렀을때
        if($(this).html() === group_mod) {
            var $targetTr = $(this).closest('tbody').find('tr');
            if($targetTr.find('td[name="groupName"] input')) {
                var defaultGroupName = $targetTr.find('td[name="groupName"] input').closest('tr').find('input[name="defaultGroupName"]').val();
                $targetTr.find('td[name="groupName"] input').closest('td').html(defaultGroupName);
            }
            $tr.find('td[name="groupName"]').html('<input type="text" class="input w_100" value="' + $tr.find('td[name="groupName"]').html() +'"/>');


            $(this).closest('tbody').find('button[name="save"]').html(group_mod);
            $(this).closest('tbody').find('button[name="delete"]').html(group_del);
            $(this).closest('td').find('button[name="delete"]').html(btn_cancel);
            $(this).html(btn_save);
        } else {
            var params = {
                groupId : $tr.data('group-id'),
                groupName : $tr.find('td[name="groupName"] input').val(),
                subGroupId : $tr.data('sub-id')
            }
            // 그룹 수정
            if($(this).closest('div').attr('id') === 'groupList') {
                putGroupName($tr, params);
            }
            // 서브 그룹 수정
            else if($(this).closest('div').attr('id') === 'subGroupList') {
                putSubGroupName($tr, params);
            }
            // 매장 수정
            else if($(this).closest('div').attr('id') === 'storeList'){

            }
        }
    });

    // 삭제
    $(document).on('click','button[name="delete"]', function() {
        if($(this).html() === btn_cancel) {
            var defaultGroupName = $(this).closest('tr').find('input[name="defaultGroupName"]').val();
            $(this).closest('tr').removeClass('selected');
            $(this).closest('td').find('button[name="save"]').html(group_mod);
            $(this).closest('tr').find('td[name="groupName"]').closest('td').html(defaultGroupName);
            $(this).html(group_del);
            return;
        }


        if(($(this).html() === group_del && !confirm("Delete?"))) return;
        var $tr = $(this).closest('tr');
        var params = {
            groupId : $tr.data('group-id'),
            subGroupId : $tr.data('sub-id'),
            storeId : $tr.find('td[name="storeName"]').html()
        }

        console.log("@@@@@@@");
        console.log(params);

        // 그룹 소그룹 삭제 부분
        if($(this).closest('div').attr('id') === 'groupList') {
            deleteGroup(params);
        } else if($(this).closest('div').attr('id') === 'subGroupList') {
            deleteSubGroup(params);
        } else  {
            deleteStoreGroup(params)
        }
    });

    // 더블 클릭 이벤트 
    // 그룹
    $('#groupList').on('dblclick', 'table tbody tr', function() {
        var $tr = $(this).closest('tr');
        var $targetTr = $(this).closest('tbody').find('tr');

        if($targetTr.find('td[name="groupName"] input')) {
            var defaultGroupName = $targetTr.find('td[name="groupName"] input').closest('tr').find('input[name="defaultGroupName"]').val();
            $targetTr.find('td[name="groupName"] input').closest('td').html(defaultGroupName);
        }
        $(this).closest('tbody').find('tr').removeClass('selected');
        $(this).closest('tbody').find('tr').find('button[name="save"]').html(group_mod);
        $(this).closest('tbody').find('tr').find('button[name="delete"]').html(group_del);

        if(!$(this).is('#noneGroup')){
            $('#subGroupHtml').show();
            $('#storeHtml').hide();
            $('#noneGroupHtml').hide();
            $('#subGroupList').getLoad('/subGroup?groupId=' + $tr.data('group-id'));
            $('#store_group_name').html($(this).find('td[name="groupName"]').text());
        }else{
            $('#subGroupHtml').hide();
            $('#storeHtml').hide();
            $('#noneGroupHtml').show();

        }

        $tr.addClass('selected');

    });

    // 서브 그룹
    $('#subGroupList').on('dblclick', 'table tbody tr', function() {
        $('#storeHtml').show();
        var $tr = $(this).closest('tr');
        var $targetTr = $(this).closest('tbody').find('tr');
        if($targetTr.find('td[name="groupName"] input')) {
            var defaultGroupName = $targetTr.find('td[name="groupName"] input').closest('tr').find('input[name="defaultGroupName"]').val();
            $targetTr.find('td[name="groupName"] input').closest('td').html(defaultGroupName);
        }
        $(this).closest('tbody').find('tr').removeClass('selected');
        $(this).closest('tbody').find('tr').find('button[name="save"]').html(group_mod);
        $(this).closest('tbody').find('tr').find('button[name="delete"]').html(group_del)
        $('#storeList').getLoad('/storeList?groupId=' + $tr.data('group-id') + '&subGroupId=' + $tr.data('sub-id'));
        $tr.addClass('selected');

        $('#store_subgroup_name').html($(this).find('td[name="groupName"]').text());

    });

    // 스토어
    $('#storeList').on('dblclick', 'table tbody tr', function() {
        var $tr = $(this).closest('tr');
        // var $targetTr = $(this).closest('tbody').find('tr');
        $(this).closest('tbody').find('tr').removeClass('selected');
        $tr.addClass('selected');
    });

    $('#storeList').on("change", 'select', function() {
        if(!confirm("你想編輯它嗎?")) return;
        var $tr = $(this).closest('tr');
        var params = {
            groupId : $tr.data('group-id'),
            subGroupId : $(this).val(),
            storeId : $tr.find('td[name="storeName"]').html()
        }
        putStoreSubGroup(params);
    });

    // 그룹 select 박스 변경시 서브 그룹 리스트 호출
    $('#noneGroupList').on("change", 'select', function () {
        if($(this).attr('name') === 'groupList') {
            var params = {
                groupId : $(this).val()
            }
            getNoneStoreSubGroupList(params);
        }
        // 서브그룹 select 박스 변경시 insert
        else if($(this).attr('name') === 'subGroupList') {
            if(!confirm("수정하시겠습니까?")) return;
            var $tr = $(this).closest('tr');
            var params = {
                groupId : $tr.find('select[name="groupList"]').val(),
                subGroupId : $(this).val(),
                storeId : $tr.find('td[name="storeId"]').html()
            }
            console.log(params);
            postStoreGroupSubGroup(params);
        }
    });


    // checkbox
    $('input[name=checkbox]:checkbox').click(function () {
        if(this.checked){
            $('#postSubGroupName').attr("disabled", false);
        }else{
            $('#postSubGroupName').attr("disabled", true);
        }
    });

});

/**
 * group 수정
 */
function putGroupName($target, params) {
    // 수정 보내는 값들
    $.ajax({
        url : "/putGroupName",
        type : 'put',
        dataType : 'text',
        data : params,
        success : function(data){
            if (data === 'true') {
                $target.find('td[name="groupName"]').html(params.groupName);
                $target.find('input[name="defaultGroupName"]').val(params.groupName);
                $target.removeClass('selected');
                $target.find('button[name="save"]').html(group_mod);
                $target.find('button[name="delete"]').html(group_del);
                alert("수정 완료");
            } else {
                alert("오류");
            }
        },
        error : function(e) {
            console.log(e);
            alert('오류');
        }
    });
}
/**
 * subGroup 수정
 */
function putSubGroupName($target, params) {
    $.ajax({
        url : "/putSubGroupName",
        type : 'put',
        dataType : 'text',
        data : params,
        success : function(data){
            if (data === 'true') {
                $target.find('td[name="groupName"]').html(params.groupName);
                $target.find('input[name="defaultGroupName"]').val(params.groupName);
                $target.removeClass('selected');
                $target.find('button[name="save"]').html(group_mod);
                $target.find('button[name="delete"]').html(group_del);
                alert("수정 완료");
            } else {
                alert("오류");
            }
        },
        error : function(e) {
            console.log(e);
            alert('오류');
        }
    });

}

/**
 * group 추가 등록
 */
function postGroup() {
    $.ajax({
        url: "/postGroup",
        type: 'post',
        dataType: 'text',
        data: {
            groupName : $("#postGroupName").val()
        },
        success: function (data) {
            if (data === 'true') {
                location.reload();
            }

        }
    });
}
/**
 * group 삭제
 */
function deleteGroup(params) {
    $.ajax({
        url: "/deleteGroup",
        type: 'put',
        dataType: 'text',
        data : params,
        success: function (data) {
            if (data === 'true') {
                location.reload();
            }

        }
    });
}

/**
 * subGroup 삭제
 */
function deleteSubGroup(params) {
    $.ajax({
        url: "/deleteSubGroup",
        type: 'put',
        dataType: 'text',
        data : params,
        success: function (data) {
            if (data === 'true') {
                location.reload();
            }

        }
    });
}

/**
 * 상점 subGroup만 수정
 */
function putStoreSubGroup(params) {
    // 수정 보내는 값들
    $.ajax({
        url : "/putStoreSubGroup",
        type : 'put',
        dataType : 'text',
        data : params,
        success : function(data){
            if (data === 'true') {
                alert("수정 완료");
                location.reload();
            } else {
                alert("오류");
            }
        },
        error : function(e) {
            console.log(e);
            alert('오류');
        }
    });
}

/**
 * subgroup 추가 등록
 */
function postSubGroup() {
        if($('#groupList').find('tr.selected').data('group-id') == null){
            alert("그룹을 선택해주세요");
            return;
        }
    $.ajax({
        url: "/postSubGroup",
        type: 'post',
        dataType: 'text',
        data: {
            groupId : $('#groupList').find('tr.selected').data('group-id'),
            subGroupName : $("#postSubGroupName").val()
        },
        success: function (data) {
            if (data === 'true') {
                alert("추가완료");
                location.reload();
            }

        }
    });
}


/**
 * 미등록 상점 등록시 서브 그룹 List 불러오기
 */
function getNoneStoreSubGroupList(params) {
    $.ajax({
        url : "/getNoneStoreSubGroupList",
        type : 'get',
        data : params,
        dataType : 'json',
        success : function(data){
            if(data.length > 0) {
                var noneStoreSubGroupHtml = "";
                for (var i in data){
                    noneStoreSubGroupHtml += "<option value='" + data[i].id  + "'>" + data[i].name + "</option>";
                }
                $("#noneStoreSubGroup").html(noneStoreSubGroupHtml);
            } else{
                $("#noneStoreSubGroup").html('<option>없음</option>');
            }

        }
    });
}

/**
 * 상점 그룹 소그룹 삭제
 */
function deleteStoreGroup(params) {
    $.ajax({
        url: "/deleteStoreGroup",
        type: 'put',
        dataType: 'text',
        data : params,
        success: function (data) {
            if (data === 'true') {
                location.reload();
            }

        }
    });
}

/**
 * 상점 그룹 소그룹 등록
 */
function postStoreGroupSubGroup(params) {
    $.ajax({
        url: "/postStoreGroupSubGroup",
        type: 'post',
        dataType: 'text',
        data:params,
        success: function (data) {
            if (data === 'true') {
                location.reload();
            }

        }
    });
}