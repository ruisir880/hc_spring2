var red="#900"
var yellow="#96992a"
var green="#14991c"

function showData() {
    myChart.refresh()
    $.ajax({
        type: "GET",
        url: "/monitor/checkSensorInfo",
        data: {"monitorPointId":$("#monitorPoint").val(),
            "terminalId":$("#terminal").val()},
        success: function (result) {
            initChart()
            option.series[0].data[0].name = result.monitorPointName
            for (var index in result.terminalVOList) {
                var terminal = result.terminalVOList[index]

                var t = {
                    name: '终端' + terminal.name,
                    symbol: 'circle',
                    symbolSize: 20,
                    children: [
                    ]
                }
                option.series[0].data[0].children[index] = t

                for (var i in terminal.sensorVOList) {
                    var sensor = terminal.sensorVOList[i]
                    var color;
                    if (sensor.threshold>sensor.currentTemp) {
                        color = yellow;
                    } else {
                        color = red;
                    }
                    var a = {
                        name: '传感器' + terminal.sensorVOList[i].sensorName + ' 阈值：' + terminal.sensorVOList[i].threshold,
                        symbol: 'circle',
                        symbolSize: 20,
                        value: terminal.sensorVOList[i].crruentTemp,
                        itemStyle: {
                            normal: {

                                color: color,
                                label: {
                                    show: true,
                                    position: 'right'
                                },

                            },
                            emphasis: {
                                label: {
                                    show: false
                                },
                                borderWidth: 0
                            }
                        }
                    }
                    option.series[0].data[0].children[index].children[i] = a
                }
                myChart.clear();
                myChart.setOption(option)
            }
        }
    });
}


function initChart() {
    option = {
        title : {
            text: '传感器信息',
        },
        tooltip : {
            trigger: 'item',
            formatter: "{b}: {c}"
        },
        toolbox: {
            show : true,
            feature : {
                saveAsImage : {show: true}
            }
        },
        calculable : false,

        series : [
            {
                name:'树图',
                type:'tree',
                orient: 'horizontal',  // vertical horizontal
                rootLocation: {x: 100, y: '50%'}, // 根节点位置  {x: 'center',y: 10}
                nodePadding: 20,
                symbol: 'circle',
                symbolSize: 40,
                itemStyle: {
                    normal: {
                        label: {
                            show: true,
                            position: 'inside',
                            textStyle: {
                                color: '#cc9999',
                                fontSize: 15,
                                fontWeight:  'bolder'
                            }
                        },
                        lineStyle: {
                            color: '#000',
                            width: 1,
                            type: 'broken' // 'curve'|'broken'|'solid'|'dotted'|'dashed'
                        }
                    },
                    emphasis: {
                        label: {
                            show: true
                        }
                    }
                },
                data: [
                    {
                        name: '',
                        value: 6,
                        symbolSize: [90, 70],
                        children: [


                        ]
                    }
                ]
            }
        ]
    };

}