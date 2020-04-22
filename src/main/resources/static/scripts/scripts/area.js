function getSonArea(areaId, nextSelect){
    var controll
    if (nextSelect == 'city') {
        controll= $('#city')
        $("#city option:not(:first)").remove();
    }else {
        controll= $('#district')
        $("#district option:not(:first)").remove();
    }
    // controll.empty()



    var firstId
    $.ajax({
        type:"GET",
        url:"/monitor/getSonAreas",
        data:{"areaId":areaId},
        dataType : "text",
        success:function(data){
            var elem
            var result = eval(data);
            firstId = result[0].id
            for (var i in result) {
                elem = result[i]
                controll.append("<option value=" + elem.id + ">" + elem.name + "</option>");
            }

            if (nextSelect == 'city') {
                getSonArea(firstId,'district')
            }
        }
    });

}