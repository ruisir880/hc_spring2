

/** 输入页跳转 **/
function jumpInputPage(pageNumber){
    // 如果“跳转页数”不为空
    if($("#jumpNumTxt").val() != ''){
        var pageNum = parseInt($("#jumpNumTxt").val());
        // 如果跳转页数在不合理范围内，则置为1
        if(pageNum<1 | pageNum>pageNumber){
            art.dialog({icon:'error', title:'友情提示', drag:false, resize:false, content:'请输入合适的页数', ok:true,});
            pageNum = 1;
        }else {
            jumpPage(pageNum);
        }
    }else{
        // “跳转页数”为空
        art.dialog({icon:'error', title:'友情提示', drag:false, resize:false, content:'请输入合适的页数', ok:true,});
        $("#submitForm").attr("action", "house_list.html?page=" + 1).submit();
    }
}