/*<![CDATA[*/

function getNoticeList() {
    var mydata = [];

    $.ajax({
        url: '/getNoticeList',
        type: 'get',
        dataType: 'json',
        success: function (data) {
            var i = 1;
            for (var key in data) {
                if (data.hasOwnProperty(key)) {
                    var tmpdata = new Object();

                    tmpdata.no = i;
                    tmpdata.id = data[key].id;

                    if (data[key].groupName) {
                        tmpdata.target = data[key].groupName;
                        if (data[key].subgroupName) {
                            tmpdata.target = tmpdata.target + '/' + data[key].subgroupName;
                            if (data[key].storeName) {
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
                    if (data[key].fileName) {
                        tmpdata.file = '<a href="/noticeFileDownload?fileName=' + data[key].fileName + ' " class="button h20" download>' + notice_attach_download + '</a>'
                    } else {
                        tmpdata.file = notice_attach_none;
                    }

                    tmpdata.date = data[key].createdDatetime;

                    if (data[key].confirmedDatetime) {
                        tmpdata.check = data[key].confirmedDatetime;
                    } else {
                        tmpdata.check = notice_confirmed_before
                    }

                    i++;

                    mydata.push(tmpdata);

                }
            }
            if (mydata) {
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
                    {label: notice_confirmed, name: 'check', width: 100, align: 'center'}
                ],
                height: 680,
                autowidth: true,
                rowNum: 20,
                pager: '#jqGridPager',
                ondblClickRow: function (rowid, icol, cellcontent, e) {
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
    });
}

function getNoticeDetail(noticeId) {
    $('#noticeId').val(noticeId);
    $.ajax({
        url : '/getNotice',
        type : 'get',
        data : {
            id : noticeId
        },
        dataType : 'json',
        success : function (data) {

            $('#nTitle').html(data.title);

            var tmpTarget = '';
            if (data.toGroupId != 0) {
                tmpTarget = data.groupName;
                if (data.toSubGroupId != 0) {
                    tmpTarget = tmpTarget + '/' + data.subgroupName;
                    if (data.toStoreId != 0) {
                        tmpTarget = tmpTarget + '/' + data.storeName;
                    } else {
                        tmpTarget = tmpTarget + '/' + notice_target_all;
                    }
                } else {
                    tmpTarget = tmpTarget + '/' + notice_target_all;
                }
            } else {
                tmpTarget = notice_target_all;
            }

            $('#nTarget').html(tmpTarget);

            $('#nDate').html(data.createdDatetime);

            if (data.confirmedDatetime) {
                $('#nConfirm').html(data.confirmedDatetime);
            } else {
                $('#nConfirm').html(notice_confirmed_before);
            }

            $('#nContent').html(data.content);
            $('#nFile').html(data.title);

            if (data.fileName != null) {
                $('#nFile').html(data.oriFileName + '(' + data.fileSize + ')');
                $('#btnDownload').show();
                $('#btnDownload').attr("href","/noticeFileDownload?fileName="+data.fileName);
                $('#btnDownload').attr("download", true);
            } else {
                $('#nFile').html(notice_attach_none);
                $('#btnDownload').hide();
            }
        }
    });
}

function putNoticeConfirm() {
    $.ajax({
        url: '/putNoticeConfirm',
        type: 'put',
        data: {
            id : $('#noticeId').val()
        },
        dataType : 'json',
        success : function (data) {
             location.href="/setting-notice";
        }
    });
}

function noticeFileDownload(fileName) {
    var form = document.getElementById("fileForm");
    var fileField = form.elements["fileName"];

    fileField.value = fileName;

    form.submit();
}
/*]]>*/