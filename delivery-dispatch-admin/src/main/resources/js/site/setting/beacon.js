/*<![CDATA[*/
let loading= $('<div id="loading"><div><p style="background-color: #838d96"/></div></div>').appendTo(document.body).hide();

$(document).ready(function() {
    getStoreList();
});

function getStoreList(){
    var $mydata = [];

    $.ajax({
        url: "/getStoreList",
        type: 'get',
        dataType: 'json',
        success: function (data) {
            var storeCount = 0;
            for(var key in data) {
                storeCount++;
                var $tmpData = new Object();
                if (data.hasOwnProperty(key)) {
                    $tmpData.count = storeCount;
                    $tmpData.id = data[key].id;
                    if(data[key].group != null){
                        $tmpData.groupId = data[key].group.id;
                        $tmpData.subGroupId = data[key].subGroup.id;
                    }
                    $tmpData.name = data[key].storeName;
                    $tmpData.storeCode = data[key].code;
                    $tmpData.chkSave = "No";

                    // 비콘 정보 입력
                    if (data[key].beacon){
                        //UUID 입력
                        $tmpData.uuid = data[key].beacon.uuid;
                        //major
                        $tmpData.major = data[key].beacon.major;
                        //minor
                        $tmpData.minor = data[key].beacon.minor;
                        //rssi
                        $tmpData.uuid = data[key].beacon.uuid;
                    }


                    $mydata.push($tmpData);
                }
            }
            if ($mydata != null) {
                jQuery('#jqGrid').jqGrid('clearGridData')
                jQuery('#jqGrid').jqGrid('setGridParam', {data: $mydata, page: 1})
                jQuery('#jqGrid').trigger('reloadGrid');
            }
            $("#jqGrid").trigger("reloadGrid");
            $("#jqGrid").jqGrid({
                datatype:"local",
                data:$mydata,
                colModel:[
                    {label:'No', name:'count', width:25, align:'center'},
                    {label:'No', name:'id', width:25, align:'center', hidden:true},
                    {label:store_name, name:'name', width:80, align:'center'},
                    {label:store_code, name:'storeCode', width:60, align:'center'},
                    {label:beaconId, name:'uuid', width:60, align:'center', editable: true, edittype: 'text'},
                    {label:titMajor, name:'major', width:60, align:'center', editable: true, edittype: 'text', editrules:{number:true}},
                    {label:titMinor, name:'minor', width:60, align:'center', editable: true, edittype: 'text', editrules:{number:true}},
                    {label:titChkApply, name:'chkSave', width:30, align:'center', editable: true, edittype: 'checkbox'},
                    {label:'그룹ID', name:'groupId', width:60, hidden:'hidden'},
                    {label:'서브그룹ID', name:'subGroupId', width:60, hidden:'hidden'}
                ],
                height:680,
                autowidth:true,
                rowNum:20,
                cellEdit: true,
                cellsubmit: "clientArray",
                pager:"#jqGridPager",
                afterEditCell: function (rowId, cellName, value, iRow, iCol) {
                    $("#" + iRow + "_" + cellName).off('blur').on('blur', function () {
                        $("#jqGrid").jqGrid("saveCell", iRow, iCol);
                        var changeValue = $("#jqGrid").jqGrid("getCell", rowId, iCol);
                        if (changeValue && changeValue !== value && (iCol === 4 || iCol === 5 || iCol === 6)) {
                            $("#jqGrid").jqGrid("setCell", rowId, 7, 'Yes');
                        }
                    });
                }
            });

            resizeJqGrid('#jqGrid'); //그리드 리사이즈
        }
    });
}

/**
 * 2022-02-06 Beacon Info 저장하는 프로세스
 * */
function saveBeaconInfos() {

    var beaconList = $("#jqGrid").jqGrid("getRowData").filter(x => x.chkSave === "Yes");

    if (beaconList.size() > 0) {
        $.ajax({
            url: '/setBeaconInfo',
            type: 'post',
            contentType: 'application/json',
            data: JSON.stringify(beaconList),
            dataType: 'json',
            success: function (data, e) {
                alert(msgSaveCompleted);

                getStoreList();
            },
            error: function (e) {

            }
        });
    }

}

/*]]>*/