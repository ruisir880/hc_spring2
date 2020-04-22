function hideHover(flt) {
    flt.hide();
    var x=-1000,y=-1000;
    flt.css({"top":y,"left":x});
}


function monitorPointSelect() {
    $("#terminal option:not(:first)").remove();
    var monitorPoint = $("#monitorPoint").val()
    $.ajax({
        type: "GET",
        url: "/monitor/getTerminal",
        data: {"monitorPointId":monitorPoint},
        success: function (result) {
            var terminal
            for (var i in result) {
                terminal = result[i]
                $("#terminal").append(" <option value="+terminal.id+">终端:"+terminal.name+"</option>")
            }
        }
    });
}