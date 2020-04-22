

function search(jumpPage) {
    var startTime = $("#startTime").val();
    var endTime = $("#endTime").val();
    var state = $("#state").val();
    var endTime = $("#endTime").val();
    var monitorPoint = $("#monitorPoint").val();
    $.ajax({
        type: "GET",
        url: "/monitor/checkTempInfo",
        data: {"monitorPointId":monitorPoint,
            "terminalId":$("#terminal").val(),
            "startTime":startTime,
            "endTime":endTime,
            "state":state,
            page:jumpPage
        },
        success: function (result) {
            initHtml()
            var item;
            var elem;
            var  sensorname;
            var terminalName;
            currentPage= result.page + 1
            totalPage = result.totalPage
            $("#totalCount").append(result.totalCount)
            $("#page").append(currentPage)
            for (var i in result.tempVO.sensorVOList) {
                sensorname = result.tempVO.sensorVOList[i].sensorName
                terminalName = result.tempVO.sensorVOList[i].terminalName
                for(var j in result.tempVO.sensorVOList[i].tempHistoryVOList) {
                    elem = result.tempVO.sensorVOList[i].tempHistoryVOList[j]
                    item = "<tr><td>"+sensorname
                        +"</td> <td>"+terminalName
                        +"</td> <td>"+elem.genTime
                        +"</td> <td>"+elem.temp
                        +"</td> <td>"+elem.state
                        +"</td> <td>"+result.tempVO.sensorVOList[i].threshold
                        +"</td> </tr>";
                    $('.table').append(item);
                }
            }
        }
    })
    $("#jumpNumTxt").val(jumpPage)
}
function initHtml() {
    $("#temoInfoTable tr:not(:first)").empty("");
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