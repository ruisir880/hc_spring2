var ws = null;

var ipHost = window.location.host;

if('WebSocket' in window){
    ws = new WebSocket("ws://"+ipHost+"/hc/websocket");
}
else{
    alert('Not support websocket')
}
window.onclose=function () {
    ws.close();
}

//接收到消息的回调方法
ws.onmessage = function(event){
    document.getElementById("areaJpg" + event.data).className = "alert selected";
    document.getElementById("buzzer").src = "images/common/buzzer_red.png";
    document.getElementById("light" + event.data).src = "images/common/light_red.jpg";
    art.dialog({icon:'error', title:'友情提示', drag:false, resize:false, content:'有警报', ok:true,});
    load();
}

function load() {
    $("#latestLogTable>tbody").html("");
    $.ajax({
        type: "GET",
        cache: false,
        url: "latestLog",
        success: function (result) {
            var item;
            var elem;
            for (var i in result) {
                elem = result[i]
                item = "<tr><td>" + ++i
                    + "</td> <td>" + elem.defenseArea
                    + "</td> <td>" + elem.state
                    + "</td> <td>" + elem.alarmTime
                    + "</td> <td>" + elem.endTime
                    + "</td><td><input type='button' value='选择' onclick=\"dealWarn(\'" + elem.id + "\')\" class='ui_input_btn01'/>"
                    + "</td> </tr>";
                $("#latestLogTable").append(item);
            }
        }
    });
}

function choseDefenseArea(node){
    $("#videoListUl").empty();
    var areaNode = document.getElementById(node);
    var index = areaNode.getAttribute("id").substr(8);
    for(var i =1;i<5;i++){
        var area = document.getElementById("areaDiv"+i);
        area.className="";
        document.getElementById("areaSpan"+i).className="";
    }
    document.getElementById("areaDiv"+index).className="selected";
    document.getElementById("areaSpan"+index).className="selection";
    //window.location.href = "main?area="+index;
    $.ajax({
        type: "GET",
        cache: false,
        url: "getVideoList",
        data: {"area":index,
            "time":new Date().getTime()},
        success: function (result) {
            $("#videoListUl li").empty("");
            var item;
            var elem;
            for (var i in result) {
                elem = result[i]
                item = "<li>\n" +
                    "<object style='' type='application/x-vlc-plugin' pluginspage='http://www.videolan.org/' id='vlc'"+i +
                    " events='false' width='400' height='350'>\n" +
                    "<param name='mrl' value='" +elem+ "' />\n" +
                    "<param name='volume' value='50' />\n" +
                    "<param name='autoplay' value='true' />\n" +
                    "<param name='loop' value='true' />\n" +
                    "<param name='fullscreen' value='true' />\n" +
                    "<param name='controls' value='true' />\n" +
                    "</object>\n" +
                    "</li>";

                $('#videoListUl').append(item);
            }
            var veidos = document.getElementsByTagName("object");
            for(var i=0;i<veidos.length;i++){
               veidos[i].setAttribute("style","");

            }
        }
    });
}
function startAlarm() {
    $.ajax({
        type: "POST",
        url: "startController",
        dataType: "text",
        success: function (result) {
            if (result == "0") {
                for (i = 1; i < 5; i++) {
                    document.getElementById("light" + i).src = "images/common/light_red.jpg";
                }
                document.getElementById("buzzer").src = "images/common/buzzer_red.jpg";
                art.dialog({icon: 'succeed', title: '友情提示', drag: false, resize: false, content: '操作成功', ok: true,});
            } else {
                art.dialog({icon: 'error', title: '友情提示', drag: false, resize: false, content: '操作失败', ok: true,});
            }

        }
    });
}

function stopAlarm() {
    $.ajax({
        type: "POST",
        url: "closeController",
        dataType: "text",
        success: function (result) {
            if (result == "0") {
                for (i = 1; i < 5; i++) {
                    document.getElementById("areaJpg" + i).className = "alert";
                    document.getElementById("light" + i).src = "images/common/light-icon.jpg";
                }
                document.getElementById("buzzer").src = "images/common/buzzer-icon.jpg";
                art.dialog({icon: 'succeed', title: '友情提示', drag: false, resize: false, content: '操作成功', ok: true,});
            } else {
                art.dialog({icon: 'error', title: '友情提示', drag: false, resize: false, content: '操作失败', ok: true,});
            }

        }
    });
}

function dealWarn(id) {
    $.ajax({
        type: "POST",
        url: "dealWarn",
        data: {"id":id},
        dataType: "text",
        success: function (result) {
            if (result == "0") {
                art.dialog({icon: 'succeed', title: '友情提示', drag: false, resize: false, content: '操作成功', ok: true,});
                load();
            }
        }
    });
}
