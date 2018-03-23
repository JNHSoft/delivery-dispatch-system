$(document).ready(function() {
    var supportsWebSockets = 'WebSocket' in window || 'MozWebSocket' in window;
    if (supportsWebSockets) {
        var socket = io('13.125.18.185:3000', {
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

    getNoticeList();

    $('#nTargetGroup').change(function() {
        toGroup($(this).val());
    });

    $('#nTargetSubGroup').change(function() {
        toSubGroup($(this).val());
    });

    $('#nTargetStore').change(function() {
        $('#toStoreId').val($(this).val());
    });

});

function getNoticeList() {
    var mydata = [];

    $.ajax({
        url: '/getNoticeList',
        type: 'get',
        dataType: 'json',
        success: function (data) {
            console.log(data);

            var i = 1;

            for (var key in data) {
                if (data.hasOwnProperty(key)) {
                    var tmpdata = new Object();

                    tmpdata.no = i;
                    tmpdata.id = data[key].id;

                    if (data[key].toGroupId != 0) {
                        tmpdata.target = data[key].groupName;
                        if (data[key].toSubGroupId != 0) {
                            tmpdata.target = tmpdata.target + '/' + data[key].subgroupName;
                            if (data[key].toStoreId != 0) {
                                tmpdata.target = tmpdata.target + '/' + data[key].storeName;
                            } else {
                                tmpdata.target = tmpdata.target + '/' + notice_target_all;
                            }
                        } else {
                            tmpdata.target = tmpdata.target + '/' + notice_target_all;
                        }
                    } else {
                        tmpdata.target = notice_target_all;
                    }

                    tmpdata.title = data[key].title;
                    if (data[key].fileName != null) {
                        tmpdata.file = '<a href="#" class="button h20">' + notice_attach_download + '</a>'
                    } else {
                        tmpdata.file = notice_attach_none;
                    }

                    tmpdata.date = data[key].createdDatetime;
                    tmpdata.check = data[key].confirmedCount;

                    if (data[key].toGroupId != 0 && data[key].toSubGroupId != 0 && data[key].toStoreId != 0) {
                        tmpdata.check = tmpdata.check + '/' + data[key].toStoreCount;
                    } else if (data[key].toGroupId != 0 && data[key].toSubGroupId != 0 && data[key].toStoreId == 0) {
                        tmpdata.check = tmpdata.check + '/' + data[key].toSubgroupCount;
                    } else if (data[key].toGroupId != 0 && data[key].toSubGroupId == 0 && data[key].toStoreId == 0) {
                        tmpdata.check = tmpdata.check + '/' + data[key].toGroupCount;
                    } else {
                        tmpdata.check = tmpdata.check + '/' + data[key].toAllCount;
                    }

                    i++;

                    mydata.push(tmpdata);

                    console.log('mydata');
                    console.log(mydata);

                    if (mydata != null) {
                        jQuery('#jqGrid').jqGrid('clearGridData')
                        jQuery('#jqGrid').jqGrid('setGridParam', {data: mydata, page: 1})
                        jQuery('#jqGrid').trigger('reloadGrid');
                    }

                    $('#jqGrid').jqGrid({
                        datatype: 'local',
                        data: mydata,
                        colModel: [
                            {label: 'id', name: 'id', width: 25, key: true, align: 'center', hidden:true},
                            {label: 'No', name: 'no', width: 25, key: true, align: 'center'},
                            {label: notice_target, name: 'target', width: 60, align: 'center'},
                            {label: notice_subject, name: 'title', width: 300},
                            {label: notice_attach, name: 'file', width: 100, align: 'center'},
                            {label: notice_created, name: 'date', width: 100, align: 'center'},
                            {label: notice_confirmed_check, name: 'check', width: 100, align: 'center'}
                        ],
                        width: 'auto',
                        height: 520,
                        autowidth: true,
                        rowNum: 20,
                        pager: '#jqGridPager',
                        onCellSelect: function (rowid, icol, cellcontent, e) {
                            var rowData = jQuery(this).getRowData(rowid);
                            var noticeId = rowData['id'];
                            getNoticeDetail(noticeId);
                            popOpen('#popNotice') //상세보기 열기
                        }
                    });

                    resizeJqGrid('#jqGrid'); //그리드 리사이즈

                    $('.store_chk .open').click(function (e) {
                        e.preventDefault();
                        $(this).next().slideDown();
                    });

                    $('.store_chk .close').click(function (e) {
                        e.preventDefault();
                        $(this).closest('div').slideUp();
                    });
                }
            }
        }
    });
}

function getNoticeDetail(noticeId) {
    console.log('noticeId: ' + noticeId);
    $.ajax({
        url : '/getNotice',
        type : 'get',
        data : {
            id : noticeId
        },
        async : false, //비동기 -> 동기
        dataType : 'json',
        success : function (data) {
            console.log(data);

            $('#nTargetGroup').empty();
            $('#nTargetGroup').append('<option value="0">' + notice_target_all_group + '</option>');
            $('#nTargetSubGroup').empty();
            $('#nTargetSubGroup').append('<option value="0">' + notice_target_all_subgroup + '</option>');
            $('#nTargetStore').empty();
            $('#nTargetStore').append('<option value="0">' + notice_target_all_store + '</option>');

            $('#nTitle').val(data.S_Notice.title);

            var tmpTarget = '';

            tmpTarget = data.S_Notice.confirmedCount;

            if (data.S_Notice.toGroupId != 0 && data.S_Notice.toSubGroupId != 0 && data.S_Notice.toStoreId != 0) {
                tmpTarget = tmpTarget + '/' + data.S_Notice.toStoreCount + notice_confirmed_check_store;
            } else if (data.S_Notice.toGroupId != 0 && data.S_Notice.toSubGroupId != 0 && data.S_Notice.toStoreId == 0) {
                tmpTarget = tmpTarget + '/' + data.S_Notice.toSubgroupCount + notice_confirmed_check_store;
            } else if (data.S_Notice.toGroupId != 0 && data.S_Notice.toSubGroupId == 0 && data.S_Notice.toStoreId == 0) {
                tmpTarget = tmpTarget + '/' + data.S_Notice.toGroupCount + notice_confirmed_check_store;
            } else {
                tmpTarget = tmpTarget + '/' + data.S_Notice.toAllCount + notice_confirmed_check_store;
            }

            $('#nConfirm').html(tmpTarget);
            $('#nContent').val(data.S_Notice.content);

            if (data.S_Notice.fileName != null) {
                $('#nFile').html(data.S_Notice.oriFileName + '(' + data.S_Notice.fileSize + ')');
                $('#btnDelete').show();
            } else {
                $('#nFile').html(notice_attach_none);
                $('#btnDelete').hide();
            }

            $('#nTarget').empty();
            if (data.C_Notice != null) {
                for (var key in data.C_Notice) {
                    $('#nTarget').append(
                        $('<li>').append(data.C_Notice[key].storeName + ' O')
                    )
                }
            }

            if (data.S_Group != null) {
                for (var key in data.S_Group) {
                    $('#nTargetGroup').append('<option value="' + data.S_Group[key].id + '">' + data.S_Group[key].name + '</option>');
                }
            }
        }
    });
}

function toGroup(toGroupId) {
    $('#toGroupId').val(toGroupId);

    $.ajax({
        url: "/getAdminSubGroupList",
        type: 'get',
        data: {'toGroupId': toGroupId},
        dataType: 'json',
        success: function (data) {
            console.log(data);

            $('#nTargetSubGroup').empty();
            $('#nTargetSubGroup').append('<option value="0">' + notice_target_all_subgroup + '</option>');
            for (var key in data) {
                $('#nTargetSubGroup').append('<option value="' + data[key].id + '">' + data[key].name + '</option>');
            }
        }
    });
}

function toSubGroup(toSubGroupId) {
    $('#toSubGroupId').val(toSubGroupId);

    $.ajax({
        url: "/getAdminSubGroupStoreList",
        type: 'get',
        data: {'toGroupId': $('#toGroupId').val()
            ,'toSubGroupId': toSubGroupId
        },
        dataType: 'json',
        success: function (data) {
            console.log(data);

            $('#nTargetStore').empty();
            $('#nTargetStore').append('<option value="0">' + notice_target_all_store + '</option>');
            for (var key in data) {
                $('#nTargetStore').append('<option value="' + data[key].id + '">' + data[key].storeName + '</option>');
            }
        }
    });
}
