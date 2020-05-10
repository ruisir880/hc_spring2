function deleteDevice(deviceId){
    $.ajax({
        type:"POST",
        url:"/hc/deleteDevice",
        data:{"deviceId":deviceId},
        dataType : "text",
        success:function(data){
            if(data=="0"){
                art.dialog({icon:'succeed', title:'友情提示', drag:false, resize:false, content:'删除成功', ok:true,});
                search();
            }else{
                art.dialog({icon:'error', title:'友情提示', drag:false, resize:false, content:'删除失败', ok:true,});
            }
        }
    });
}
function search(){
    var defenseArea = $("#defenseArea").val();
    $.ajax({
        type: "GET",
        url: "/hc/queryDevices",
        data: {"defenseArea":defenseArea},
        success: function (result) {
            initHtml();
            var item;
            var elem;
            for (var i in result) {
                elem = result[i]
                item = "<tr><td>"+elem.defenseArea.defenseName
                    +"</td> <td>"+elem.ip
                    +"</td> <td>"+elem.account
                    +"</td> <td>"+elem.password
                    +"</td> <td>"+elem.port
                    +"</td> <td> <a href='deviceEdit?deviceId="+elem.id
                    + "' class='edit'>编辑</a> <a href='javascript:deleteDevice("+elem.id+");'>删除</a>"
                    +"</td> </tr>";
                $('.table').append(item);
            }

        }
    })
}

function initHtml() {
    $("#deviceListTable tr:not(:first)").empty("");
}
