/*<![CDATA[*/
function footerRiders() {
    if(footerRiderList[2]){
        $('#rest').text(parseInt(footerRiderList[2].workCount) + parseInt(footerRiderList[2].orderCount));//휴식
    }else {
        $('#rest').text('0');
    }
    if(footerRiderList[1]){
        $('#standby').text(parseInt(footerRiderList[1].workCount) - parseInt(footerRiderList[1].orderCount));// 대기
        $('#work').text(footerRiderList[1].orderCount);//근무
    }else {
        $('#standby').text('0');
        $('#work').text('0');
    }
}
function footerOrders() {
    var newCnt = 0;
    var assignedCnt = 0;
    var completedCnt = 0;
    var canceledCnt = 0;
    for (i =0; i <footerOrderList.length; i++){
        if(footerOrderList[i].status=="0"){
            newCnt += parseInt(footerOrderList[i].count);
        }else if(footerOrderList[i].status=="1"){
            assignedCnt += parseInt(footerOrderList[i].count);
        }else if(footerOrderList[i].status=="2"){
            assignedCnt += parseInt(footerOrderList[i].count);
        }else if(footerOrderList[i].status=="3"){
            completedCnt += parseInt(footerOrderList[i].count);
        }else if(footerOrderList[i].status=="4"){
            canceledCnt += parseInt(footerOrderList[i].count);
        }else if(footerOrderList[i].status=="5"){
            newCnt += parseInt(footerOrderList[i].count);
        }
    }
    $('#new').text(newCnt);
    $('#assigned').text(assignedCnt);
    $('#completed').text(completedCnt);
    $('#canceled').text(canceledCnt);
}
$(function() {
    footerRiders();
    footerOrders();
    getNoticeList()
});
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

                    if (data[key].toGroupId) {
                        tmpdata.target = data[key].groupName;
                        if (data[key].toSubGroupId) {
                            tmpdata.target = tmpdata.target + '/' + data[key].subgroupName;
                            if (data[key].toStoreId) {
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
                        tmpdata.file = '<a href="#" class="button h20">' + notice_attach_download + '</a>'
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
                width: 'auto',
                height: 700,
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
/*]]>*/