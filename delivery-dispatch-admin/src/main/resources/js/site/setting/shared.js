/* Document Loaded */
$(document).ready(function () {
    getAllowDefaultInfo();      // 그룹 및 허용된 관리자 정보 추출
    getSharedStoreInfo();        // List Data 가져오기
    setOptionEvent();           // selectBox에 대한 change Event 입력
});

let groupInfo;
let allowAdminInfo;
let test;

/**
 * 로그인된 관리자의 Group ID 및 공유 허용된 관리자 ID 획득
 * */
function getAllowDefaultInfo(){

    $.ajax({
        url: '/getGroupList',
        type: 'get',
        dataType: 'json',
        async: false,
        success: function (data) {
            // $('select[name=groupid]').empty();
            // $('select[name=groupid]').append('<option value="">' +  '전체' + '</option>');
            //
            // for (var key in data){
            //     $('select[name=groupid]').append('<option value="' + data[key].id + '">' + data[key].name + '</option>');
            // }
            // groupInfo = $('select[name=groupid]:eq(0)').clone();

            groupInfo = data;
        }
    });

    $.ajax({
        url: '/getSharedAdminInfo',
        type: 'get',
        dataType: 'json',
        // data: {'toGroupId': groupID.val()},
        async: false,
        success: function(data){
            // $('select[name=shared_adminid]').empty();
            // $('select[name=shared_adminid]').append('<option value="">' +  '전체' + '</option>');
            //
            // for (var key in data){
            //     $('select[name=shared_adminid]').append('<option value="' + data[key].loginId + '">' + data[key].name + '</option>');
            // }

            // allowAdminInfo = $('select[name=shared_adminid]:eq(0)').clone();
            allowAdminInfo = data;
        }
    });
};

/**
 * 로그인된 관리자의 subGroupID 획득
 * */
function getSubGroupInfo(groupID){
    let $idx = groupID.parent().parent().parent().index();
    let $subObj = $("#sortable").find("li:eq(" + $idx + ")").find("select[name=subgroupid]");

    $.ajax({
        url: "/getAdminSubGroupList",
        type: 'get',
        data: {toGroupId: groupID.val()},
        dataType: 'json',
        async : false,
        success: function (data) {
            $subObj.empty();
            $subObj.append('<option value="">' + AllSubGroupList + '</option>');
            for (var key in data) {
                $subObj.append('<option value="' + data[key].id + '">' + data[key].name + '</option>');
            }
        }
    });
};

/**
 * 로그인된 관리자의 storeID 획득
 * */
function getStoreInfo(groupID, subGroupID){

    let $idx = groupID.parent().parent().parent().index();
    let $storeObj = $("#sortable").find("li:eq(" + $idx + ")").find("select[name=storeid]");

    $.ajax({
        url: "/getAdminSubGroupStoreList",
        type: 'get',
        data: {toGroupId: groupID.val()
            ,toSubGroupId: subGroupID.val()
        },
        dataType: 'json',
        async : false,
        success: function (data) {
            $storeObj.empty();
            $storeObj.append('<option value="">' + AllStoreList + '</option>');
            for (var key in data) {
                $storeObj.append('<option value="' + data[key].storeId + '">' + data[key].storeName + '</option>');
            }
        }
    });
};

/**
 * 허용된 관리자의 그룹 정보 가져오기
 * */
function getAllowGroupInfo(adminID){
    let $idx = adminID.parent().parent().parent().index();
    let $subObj = $("#sortable").find("li:eq(" + $idx + ")").find("select[name=shared_groupid]");

    $.ajax({
        url: "/getSharedGroupInfo",
        type: 'get',
        data: {sharedAdminID: adminID.val()},
        dataType: 'json',
        async : false,
        success: function (data) {
            $subObj.empty();
                $subObj.append('<option value="">' + AllGroupList + '</option>');
            for (var key in data) {
                $subObj.append('<option value="' + data[key].id + '">' + data[key].name + '</option>');
            }
        }
    });

}

/**
 * 허용된 관리자의 하위 그룹 정보 가져오기
 * */
function getAllowSubGroupInfo(adminID, groupID) {
    let $idx = adminID.parent().parent().parent().index();
    let $subObj = $("#sortable").find("li:eq(" + $idx + ")").find("select[name=shared_subgroupid]");

    $.ajax({
        url: "/getSharedSubGroupInfo",
        type: 'get',
        data: {
            sharedAdminID: adminID.val(),
            sharedGroupID: groupID.val()
        },
        dataType: 'json',
        async : false,
        success: function (data) {
            $subObj.empty();
            $subObj.append('<option value="">' + AllSubGroupList + '</option>');
            for (var key in data) {
                $subObj.append('<option value="' + data[key].id + '">' + data[key].name + '</option>');
            }
        }
    });
}

/**
 * 허용된 관리자의 스토어 정보 가져오기
 * */
function getAllowStoreInfo(adminID, groupID, subGroupID) {
    let $idx = adminID.parent().parent().parent().index();
    let $subObj = $("#sortable").find("li:eq(" + $idx + ")").find("select[name=shared_storeid]");

    $.ajax({
        url: "/getSharedStoreInfo",
        type: 'get',
        data: {
            sharedAdminID: adminID.val(),
            sharedGroupID: groupID.val(),
            sharedSubGroupID: subGroupID.val()
        },
        dataType: 'json',
        async : false,
        success: function (data) {
            $subObj.empty();
            $subObj.append('<option value="">' + AllStoreList + '</option>');
            for (var key in data) {
                $subObj.append('<option value="' + data[key].id + '">' + data[key].storeName + '</option>');
            }
        }
    });
}

/**
 * group append 후 scroller event insert
 * */
function setOptionEvent(){
    // Subgroup 호출 이벤트 추가
    $("select[name=groupid]").off().on("change", function () {
        getSubGroupInfo($(this));
    });
    // 스토어 정보 호출 이벤트 추가
    $("select[name=subgroupid]").off().on("change", function () {
        let $idx = $(this).parent().parent().parent().index();
        let $groupObj = $("#sortable").find("li:eq(" + $idx + ")").find("select[name=groupid]");
        getStoreInfo($groupObj, $(this));
    });
    //허용 그룹 정보 호출 이벤트 추가
    $("select[name=shared_adminid]").off().on("change", function () {
        // shared Group ID 적용 ($(this))
        getAllowGroupInfo($(this));
    });
    // 허용 서브 그룹 정보 호출 이벤트 추가
    $("select[name=shared_groupid]").off().on("change",function () {
        let $idx = $(this).parent().parent().parent().index();
        let $adminObj = $("#sortable").find("li:eq(" + $idx + ")").find("select[name=shared_adminid]");

        // subGroup Id 호출 ($adminObj, $(this));
        getAllowSubGroupInfo($adminObj, $(this));
    });
    // 허용 스토어 정보 호출 이벤트 추가
    $("select[name=shared_subgroupid]").off().on("change", function () {
        let $idx = $(this).parent().parent().parent().index();
        let $adminObj = $("#sortable").find("li:eq(" + $idx + ")").find("select[name=shared_adminid]");
        let $groupObj = $("#sortable").find("li:eq(" + $idx + ")").find("select[name=shared_groupid]");

        // store ID 리스트 적용 ($adminOjb, $groupObj, $(this));
        getAllowStoreInfo($adminObj, $groupObj, $(this));
    });

    // 삭제 문구 표시
    $(".ui-state-default").off().on({
        mouseenter: function () {
            $(this).find(".deleteBox").show();
        },
        mouseleave: function () {
            $(this).find(".deleteBox").hide();
        },
    });

    // 삭제 문구 클릭 이벤트
    $(".deleteBox").off().on("click", function () {
        let $idx = $(this).parent().parent().index();
        $("#sortable").find("li:eq(" + $idx + ")").find("input[name=del]").val(DateFormat());

        delObjEvent($("#sortable").find("li:eq(" + $idx + ")"));
    })
}

/**
 * Object Detach() 삭제
 * */
function delObjEvent(delObj){
    //delObj.detach();
    if (delObj.find("input[name=seq]").val() != ""){
        test = delObj.detach();
        delObjServer(test);
    }else{
        delObj.remove();
    }
    // delObj.detach();
}

/**
 * Object 삭제 후 서버 반영
 * */
function delObjServer(delObj){

    $.ajax({
        url: '/update-shared-info',
        type: 'post',
        dataType: 'text',
        async : false,
        data:{
            deleted: delObj.find("input[name=del]").val(),
            groupid: delObj.find("select[name=groupid]").val(),
            subgroupid: delObj.find("select[name=subgroupid]").val(),
            storeid: delObj.find("select[name=storeid]").val(),
            shared_adminid: delObj.find("select[name=shared_adminid]").val(),
            shared_groupid: delObj.find("select[name=shared_groupid]").val(),
            shared_subgroupid: delObj.find("select[name=shared_subgroupid]").val(),
            shared_storeid: delObj.find("select[name=shared_storeid]").val(),
            use_flag: delObj.find("select[name=use_flag]").val(),
            index: delObj.find("input[name=idx]").val(),
            seq: delObj.find("input[name=seq]").val()
        },
        success: function (data) {
            reorder();
            saveRiderInfo();
        },
        error: function () {
            // console.log("오류");
        }
    });
}

/* 정렬 */
$(function (){
    $( "#sortable" ).sortable({
        start: function (event, ui) {
            ui.item.data('start_pos', ui.item.index());
        },
        stop:function (event, ui) {
            var spos = ui.item.data('start_pos');
            var epos = ui.item.index();
            reorder();
        }
    });
    $( "#sortable" ).disableSelection();
});

/* index 정렬 */
function reorder() {
    $("#sortable>li").each(function(i, box) {
        $(box).find("input[name=idx]").val(i + 1);
    });
};

function saveRiderInfo() {
    let lis = $("#sortable").find("li");

    if (lis.length < 1){
        return;
    }

    let sharedAdminSize = lis.find("select[name=shared_adminid]")
                            .filter(function () {
                                return $(this).val() == "";
                            }).length;

    let groupSize = lis.find("select[name=groupid]")
        .filter(function () {
            return $(this).val() == "";
        }).length;

    if (sharedAdminSize + groupSize > 0){
        return;
    }

    var sender = new Array();

    lis.each(function () {
        var obj = new Object();

        obj.deleted = $(this).find("input[name=del]").val();
        obj.groupid = $(this).find("select[name=groupid]").val();
        obj.subgroupid = $(this).find("select[name=subgroupid]").val();
        obj.storeid = $(this).find("select[name=storeid]").val();
        obj.shared_adminid = $(this).find("select[name=shared_adminid]").val();
        obj.shared_groupid = $(this).find("select[name=shared_groupid]").val();
        obj.shared_subgroupid = $(this).find("select[name=shared_subgroupid]").val();
        obj.shared_storeid = $(this).find("select[name=shared_storeid]").val();
        obj.use_flag = $(this).find("select[name=use_flag]").val();
        obj.index = $(this).find("input[name=idx]").val();
        obj.seq = $(this).find("input[name=seq]").val();

        sender.push(obj);
    });

    var jsonInfo = JSON.stringify(sender);

    $.ajax({
        url: "/save-shared-rider",
        type: 'post',
        dataType: 'text',
        async: false,
        data: {
            sharedRiders: jsonInfo
        },
        success: function (data) {
            if (data == 'failed'){
                alert(ChangeFailed);
            }else{
                alert(ChangeSuccess);
                location.reload();
            }
        }
    });
};

// 리스트 항목 출력
function getSharedStoreInfo(){
    $.ajax({
        url: '/getSharedInfoList',
        type: 'get',
        dataType: 'json',
        async : false,
        success: function (data) {
            for (var key in data){
                let $liData = addlistItem();

                $liData.find("select[name=groupid]").val(data[key].groupid).prop("selected", true);
                getSubGroupInfo($liData.find("select[name=groupid]"));

                $liData.find("select[name=subgroupid]").val(data[key].subgroupid).prop("selected",true);
                getStoreInfo($liData.find("select[name=groupid]"), $liData.find("select[name=subgroupid]"));

                $liData.find("select[name=storeid]").val(data[key].storeid).prop("selected", true);

                $liData.find("select[name=shared_adminid]").val(data[key].shared_adminid).prop("selected", true);
                getAllowGroupInfo($liData.find("select[name=shared_adminid]"));

                $liData.find("select[name=shared_groupid]").val(data[key].shared_groupid).prop("selected", true);
                getAllowSubGroupInfo($liData.find("select[name=shared_adminid]"), $liData.find("select[name=shared_groupid]"));

                $liData.find("select[name=shared_subgroupid]").val(data[key].shared_subgroupid).prop("selected", true);
                getAllowStoreInfo($liData.find("select[name=shared_adminid]"), $liData.find("select[name=shared_groupid]"), $liData.find("select[name=shared_subgroupid]"));

                $liData.find("select[name=shared_storeid]").val(data[key].shared_storeid).prop("selected", true);

                // 공유 허용 유무
                $liData.find("select[name=use_flag]").val(data[key].use_flag).prop("selected", true);

                // PK 입력
                $liData.find("input[name=seq]").val(data[key].seq);
                // Index 입력
                $liData.find("input[name=idx]").val(data[key].index);
            }
        }
    });
}

// List 항목 추가
function addlistItem(){
    if (groupInfo.length < 1 || allowAdminInfo.length < 1){
        alert("Please wait for the process to complete.");
        return;
    }

    let contents = `<li class="ui-state-default" style="margin:5px 20px;">
                        <div style="display: inline-grid;">
                            <div>
                                <input type="hidden" name="seq" value="" />
                                <input type="hidden" name="idx" value="" />
                                <input type="hidden" name="del" value="" />
                                <span style="width: 110px; display: inline-block; text-align: right; padding: 5px; margin: 10px 0px 5px 5px;">${TargetValue}</span>
                                <select style="height: 30px;width: 150px;" name="groupid">
                                    <option value="">${AllGroupList}</option>
                                </select>
                                <select style="height: 30px;width: 150px;" name="subgroupid">
                                    <option value="">${AllSubGroupList}</option>
                                </select>
                                <select style="height: 30px;width: 150px;" name="storeid">
                                    <option value="">${AllStoreList}</option>
                                </select>
                            </div>
                            <div>
                                <span style="width: 110px; display: inline-block; text-align: right; padding: 5px; margin: 10px 0px 5px 4px;">${ShareTargetValue}</span>
                                <select style="height: 30px;width: 150px;" name="shared_adminid">
                                    <option value="">${AllAdminList}</option>
                                </select>
                                <select style="height: 30px;width: 150px;" name="shared_groupid">
                                    <option value="">${AllGroupList}</option>
                                </select>
                                <select style="height: 30px;width: 150px;" name="shared_subgroupid">
                                    <option value="">${AllSubGroupList}</option>
                                </select>
                                <select style="height: 30px;width: 150px;" name="shared_storeid">
                                    <option value="">${AllStoreList}</option>
                                </select>
                                <span>${SharedTitle}</span>
                                <select style="height: 30px;width: 150px;" name="use_flag">
                                    <option value="1" selected>${SharedValue}</option>
                                    <option value="0">${UnSharedValue}</option>
                                </select>
<!--                                <input type="text" name="idx" value="#{btn.store}" />-->
                            </div>
                        </div>
                        <a href="#"><div class="deleteBox" style="float:right;display: none;margin-right: 10px;margin-top:34px;">[${DelButtonText}]</div></a>
                    </li>`;

    $(contents).appendTo("#sortable");

    let $liObj = $("#sortable").find("li:last");

    addOptions($liObj.find("select[name=groupid]"), groupInfo, AllGroupList);
    addOptions($liObj.find("select[name=shared_adminid]"), allowAdminInfo, AllAdminList);

    setOptionEvent();
    reorder();

    return $liObj;
}

function addOptions(tarObj, sender, firstText){
    let copyData = sender;

    tarObj.empty();
    tarObj.append('<option value="">' + firstText + '</option>');

    for(var key in copyData){
        tarObj.append('<option value="' + copyData[key].id + '">' + copyData[key].name + '</option>');
    }
}

function DateFormat() {

    // time = time.replace(' ', 'T') + 'Z';

    var currentDate = new Date();

    var yyyy = currentDate.getFullYear().toString();
    var mm = (currentDate.getMonth() + 1).toString();
    var dd = currentDate.getDate().toString();

    var hh = currentDate.getHours().toString();
    var nn = currentDate.getMinutes().toString();
    var ss = currentDate.getSeconds().toString();

    return yyyy + '-' + (mm[1] ? mm : '0' + mm[0]) + '-' + (dd[1]?dd:'0' + dd[0]) + ' '+
        (hh[1] ? hh : '0' + hh[0]) + ':' + (nn[1] ? nn : '0' + nn[0]) + ':' + (ss[1] ? ss : '0' + ss[0]);
}
