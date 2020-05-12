var websocket = null;

var ipHost = window.location.host;

if('WebSocket' in window){
    websocket = new WebSocket("ws:"+ipHost+"/hc/websocket");
}
else{
    alert('Not support websocket')
}
window.onclose=function () {
    websocket.close();
}

//接收到消息的回调方法
websocket.onmessage = function(event){
    document.getElementById("areaJpg"+event.data).className="alert selected";
}
