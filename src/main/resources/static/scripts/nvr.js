/** 表单验证  **/
function validateForm(){
    if($("#deviceIp").val()==""){
        art.dialog({icon:'error', title:'友情提示', drag:false, resize:false, content:'IP地址', ok:true,});
        return false;
    }
    if($("#account").val()==""){
        art.dialog({icon:'error', title:'友情提示', drag:false, resize:false, content:'管理员账户', ok:true,});
        return false;
    }
    if($("#password").val()==""){
        art.dialog({icon:'error', title:'友情提示', drag:false, resize:false, content:'管理员密码', ok:true,});
        return false;
    }
    if($("#port").val()==""){
        art.dialog({icon:'error', title:'友情提示', drag:false, resize:false, content:'端口', ok:true,});
        return false;
    }

    return true;
}

function onselectIPC(ip){
    document.getElementById("selectedDevice").value=ip;
}

function onselectChannel(channelNo){
    document.getElementById("selectedChannel").value=channelNo;
}

function addChannel(){
    var selectedDevice = $("#selectedDevice").val();
    var selectedChannel = $("#selectedChannel").val();
    if($("#selectedDevice").val()==""){
        art.dialog({icon:'error', title:'友情提示', drag:false, resize:false, content:'未选中设备', ok:true,});
        return false;
    }
    if($("#selectedChannel").val()==""){
        art.dialog({icon:'error', title:'友情提示', drag:false, resize:false, content:'未选中通道', ok:true,});
        return false;
    }
    $.ajax({
        type: "POST",
        url: "addChannel",
        data: {"selectedDevice":selectedDevice,"selectedChannel":selectedChannel},
        success: function (data) {
            if (data.code == "0") {
                art.dialog({icon: 'succeed', title: '友情提示', drag: false, resize: false, content: data.msg, ok: true});
            }
        }
    });
}

function deleteChannel(){
    var selectedChannel = $("#selectedChannel").val();
    if($("#selectedChannel").val()==""){
        art.dialog({icon:'error', title:'友情提示', drag:false, resize:false, content:'未选中通道', ok:true,});
        return false;
    }
    $.ajax({
        type: "POST",
        url: "deleteChannel",
        data: {"selectedChannel":selectedChannel},
        success: function (data) {
            if (data.code == "0") {
                art.dialog({icon: 'succeed', title: '友情提示', drag: false, resize: false, content: data.msg, ok: true});
            }
        }
    });
}

function refreshNvr(){
    $.ajax({
        type: "get",
        cache: false,
        url: "refreshNvr",
        success: function (result) {
            $("#nvrChannelTable tr:not(:first)").empty("");
            var item;
            var elem;
            for (var i in result) {
                channelInfo = result[i]
                item = "<tr><td>" + channelInfo.no
                    + "</td> <td>" + channelInfo.channelNo
                    + "</td> <td>" + channelInfo.ipcIp
                    + "</td> <td>" + channelInfo.ipcPort
                    + "</td> <td>" + channelInfo.ipChannel
                    + "</td> <td>" + channelInfo.online
                    + "</td> <td> <input type=\"button\" value=\"选择\" onclick=\"onselectChannel('"+channelInfo.channelNo+"')\" class=\"ui_input_btn01\"/>"
                    + "</td> </tr>";
                $("#nvrChannelTable").append(item);
            }
        }
    });
}

$(document).ready(function() {
    $("#submitbutton").click(function () {
        if (validateForm()) {
            checkFyFhSubmit();
        }
    });

    /*
 * 取消
 */
    $("#cancelbutton").click(function () {
        $('#deviceIp').val('');
        $("#account").val('');
        $("#password").val('');
        $("#port").val('');
    });

    var result = 'null';
    if (result == 'success') {
        /**  关闭弹出iframe  **/
        window.parent.$.fancybox.close();
    }

    function checkFyFhSubmit() {
        var deviceId = $('#deviceId').val();
        var deviceIp = $('#deviceIp').val();
        var account = $('#account').val();
        var password = $('#password').val();
        var port = $("#port").val();

        if (deviceIp != "" && account != "" && password != "" && port != "") {
            $.ajax({
                type: "POST",
                url: "editNvr",
                data: {
                    "id": deviceId, "ip": deviceIp, "account": account, "password": password,
                    "port": port
                },
                dataType: "text",
                success: function (data) {
                    if (data == "0") {
                        art.dialog({icon: 'succeed', title: '友情提示', drag: false, resize: false, content: '编辑成功', ok: true,});
                    } else {
                        art.dialog({icon: 'error', title: '友情提示', drag: false, resize: false, content: '连接设备失败，请检查！', ok: true,});
                    }
                }
            });
        }
        return true;
    }
})