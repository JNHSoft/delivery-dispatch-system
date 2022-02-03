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
                    $tmpData.th0 = data[key].id
                    if(data[key].group != null){
                        $tmpData.th12 = data[key].group.id
                        $tmpData.th13 = data[key].subGroup.id
                    }
                    $tmpData.th3 = data[key].storeName
                    $tmpData.th4 = data[key].code

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
                    {label:'No', name:'th0', width:25, align:'center', hidden:true},
                    {label:store_name, name:'th3', width:80, align:'center'},
                    {label:store_code, name:'th4', width:60, align:'center'},
                    {label:'그룹ID', name:'th12', width:60, hidden:'hidden'},
                    {label:'서브그룹ID', name:'th13', width:60, hidden:'hidden'}
                ],
                height:680,
                autowidth:true,
                rowNum:20,
                pager:"#jqGridPager"
            });

            resizeJqGrid('#jqGrid'); //그리드 리사이즈

        }
    });
}

/*]]>*/