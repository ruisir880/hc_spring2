var websocket = null;

var curWwwPath=window.document.location.href;
//获取主机地址之后的目录如：/Tmall/index.jsp
var pathName=window.document.location.pathname;
var pos=curWwwPath.indexOf(pathName);

//获取主机地址，如： http://localhost:8080
var localhostPaht=curWwwPath.substring(0,pos);
//判断当前浏览器是否支持WebSocket
if('WebSocket' in window){
    websocket = new WebSocket("ws://"+localhostPaht+":8084/websocket");
}
else{
    alert('Not support websocket')
}
window.onclose=function () {
    websocket.close();
}

//接收到消息的回调方法
websocket.onmessage = function(event){
    $("#areaJpg"+event.data).className("alert selected");
}
