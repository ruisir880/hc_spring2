/** 表单验证  **/
function validateForm(){
    if($("#username").val()==""){
        art.dialog({icon:'error', title:'友情提示', drag:false, resize:false, content:'用户名', ok:true,});
        return false;
    }
    if($("#password").val()==""){
        art.dialog({icon:'error', title:'友情提示', drag:false, resize:false, content:'密码', ok:true,});
        return false;
    }

    return true;
}

function initHtml() {
    $("#userListTable tr:not(:first)").empty("");
    $("#totalCount").empty()
    $("#page").empty()
}


/** 模糊查询来电用户  **/
function search(){
    var username = $("#username").val();
    var realName = $("#realName").val();
    var mobile = $("#mobile").val();
    $.ajax({
        type: "GET",
        cache: false,
        url: "/hc/queryUserList",
        data: {"username":username,
            "realName":realName,
            "mobile":mobile
        },
        success: function (result) {
            initHtml();
            var item;
            var elem;
            for (var i in result) {
                elem = result[i]
                item = "<tr><td>"+elem.username
                    +"</td> <td>"+elem.realName
                    +"</td> <td>"+elem.mobile
                    +"</td> <td>"+elem.email
                    +"</td> <td> <a href='userEdit?userId="+elem.uid
                    + "' class='edit'>编辑</a> <a href='javascript:deleteUser("+elem.uid+");'>删除</a>"
                    +"</td> </tr>";
                $('.table').append(item);
            }

        }
    })

}

/** 模糊查询来电用户  **/
function deleteUser(userId){
    myajax  = $.ajax({
        type:"POST",
        url:"/hc/deleteUser",
        data:{"userId":userId},
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