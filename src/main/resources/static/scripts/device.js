/** 表单验证  **/
function validateForm(){
    if($("#deviceIp").val()==""){
        art.dialog({icon:'error', title:'友情提示', drag:false, resize:false, content:'IP地址', ok:true,});
        return false;
    }
    if($("#account").val()==""){
        art.dialog({icon:'error', title:'友情提示', drag:false, resize:false, content:'管理员密码', ok:true,});
        return false;
    }
    if($("#password").val()==""){
        art.dialog({icon:'error', title:'友情提示', drag:false, resize:false, content:'密码', ok:true,});
        return false;
    }
    if($("#port").val()==""){
        art.dialog({icon:'error', title:'友情提示', drag:false, resize:false, content:'端口', ok:true,});
        return false;
    }
    if($("#defenseArea").val()==""){
        art.dialog({icon:'error', title:'友情提示', drag:false, resize:false, content:'所属防区', ok:true,});
        return false;
    }

    return true;
}