
function search(page){
    var operator = $("#operator").val();
    var startTime = $("#startTime").val();
    var endTime = $("#endTime").val();
    $.ajax({
        type: "GET",
        cache: false,
        url: "/hc/queryOperationLog",
        data: {"page":page,
            "operator":operator,
            "startTime":startTime,
            "endTime":endTime,
        },
        success: function (result) {
            initHtml();
            currentPage= result.page
            totalPage = result.totalPage
            $("#totalCount").append(result.totalCount)
            $("#page").append(currentPage)
            var item;
            var elem;
            for (var i in result.operationLogList) {
                elem = result.operationLogList[i]
                item = "<tr><td>"+elem.user
                    +"</td> <td>"+elem.operation
                    +"</td> <td>"+elem.timeFormat
                    +"</td> </tr>";
                $('.table').append(item);
            }

        }
    })
    $("#jumpNumTxt").val(page)
}

function initHtml() {
    $("#operationLogTable tr:not(:first)").empty("");
    $("#totalCount").empty()
    $("#page").empty()
}

function jumpNextPage(){
    if(currentPage < totalPage){
        search(currentPage+1);
    }
}

function jumpLastPage(){
    if(currentPage>1){
        search(currentPage-1);
    }
}


/** 输入页跳转 **/
function jumpInputPage(pageNumber){
    // 如果“跳转页数”不为空
    if($("#jumpNumTxt").val() != ''){
        var pageNum = parseInt($("#jumpNumTxt").val());
        // 如果跳转页数在不合理范围内，则置为1
        if(pageNum<1 | pageNum>pageNumber){
            art.dialog({icon:'error', title:'友情提示', drag:false, resize:false, content:'请输入合适的页数', ok:true,});
            pageNum = 1;
        }
        $("#submitForm").attr("action", "house_list.html?page=" + pageNum).submit();
    }else{
        // “跳转页数”为空
        art.dialog({icon:'error', title:'友情提示', drag:false, resize:false, content:'请输入合适的页数', ok:true,});
        $("#submitForm").attr("action", "house_list.html?page=" + 1).submit();
    }
}