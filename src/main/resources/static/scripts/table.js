function IniEvent() {
    var tbl = document.getElementById("deviceListTable");
    var trs = tbl.getElementsByTagName("tr"); //getElementsByTagName(按标记名获取元素)
    for (var i = 0; i < trs.length; i++) {
        trs[i].onclick = TrOnClick;
    }
}

function TrOnClick() {
    var tbl = document.getElementById("deviceListTable");
    var trs = tbl.getElementsByTagName("tr");
    for (var i = 0; i < trs.length; i++) {
        if (trs[i] == this) {  //判断是不是当前选择的行
            trs[i].style.background = "yellow";
        } else {
            trs[i].style.background = "white";
        }
    }
}