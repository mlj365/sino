var staticdatas = {
    success: true,
    widget: {
    id: "001",
        name: "test",
        hasGrid: false,
        chartTypeList: [{
        minDimensionNum: 1,
        minMensurmentNum: 1,
        type: "bar",
        isOpen: true
    }, {
        minDimensionNum: 1,
        minMensurmentNum: 1,
        type: "line",
        isOpen: true
    }],
        defaultType: "bar",
        axisList: [{
        positon: "bottom",
        type: "dimension",
        inverse: false,
        axis: "x",
        fieldList: [{
            name: "DistributorCode",
            type: "bar"
        }, {
            name: "year",
            type: "bar"
        }, {
            name: "month",
            type: "bar"
        }]
    }, {
        positon: "left",
        type: "measurment",
        inverse: false,
        axis: "y",
        fieldList: [{
            name: "ConsignmentQuantity",
            type: "bar"
        }, {
            name: "Quantity",
            type: "bar"
        }],
        name: "左轴"
    }],
        dataType: "sql",
        dataName: "getDistributorByMonth",
        segmentList: [],
        gridOption: {
        isShow: true,
            showPage: true,
            hasTitle: true,
            multiSelect: false,
            enableAllSelect: false,
            page: {
            pageSize: 10,
                recordCount: 59,
                pageNo: 1
        },
        fieldList: [{
            field: "DistributorCode",
            caption: "客户编码",
            width: "120",
            align: "left",
            istitle: true,
            groupBy: true
        }, {
            field: "year",
            caption: "年",
            width: "120",
            align: "right",
            istitle: true,
            groupBy: true
        }, {
            field: "month",
            caption: "月",
            width: "120",
            align: "right",
            istitle: true,
            groupBy: true
        }, {
            field: "ConsignmentQuantity",
            caption: "原始数量",
            width: "120",
            align: "right",
            istitle: true,
            groupBy: true
        }, {
            field: "Quantity",
            caption: "数量",
            width: "120",
            align: "right",
            istitle: true,
            groupBy: true
        }],
            data: [{
            DistributorCode: "56500040",
            aggcode: "DistributorCode-monthno-ConsignmentQuantity-Quantity",
            year: "2017",
            month: "11",
            ConsignmentQuantity: "0.000",
            Quantity: "2989.900"
        }, {
            DistributorCode: "56500041",
            aggcode: "DistributorCode-monthno-ConsignmentQuantity-Quantity",
            year: "2017",
            month: "8",
            ConsignmentQuantity: "10844.800",
            Quantity: "3708.500"
        }, {
            DistributorCode: "56500041",
            aggcode: "DistributorCode-monthno-ConsignmentQuantity-Quantity",
            year: "2017",
            month: "10",
            ConsignmentQuantity: "9571.750",
            Quantity: "2615.000"
        }, {
            DistributorCode: "56500041",
            aggcode: "DistributorCode-monthno-ConsignmentQuantity-Quantity",
            year: "2017",
            month: "11",
            ConsignmentQuantity: "86319.000",
            Quantity: "12127.050"
        }, {
            DistributorCode: "56500045",
            aggcode: "DistributorCode-monthno-ConsignmentQuantity-Quantity",
            year: "2017",
            month: "7",
            ConsignmentQuantity: "0.000",
            Quantity: "5008.500"
        }, {
            DistributorCode: "56500045",
            aggcode: "DistributorCode-monthno-ConsignmentQuantity-Quantity",
            year: "2017",
            month: "8",
            ConsignmentQuantity: "0.000",
            Quantity: "863.500"
        }, {
            DistributorCode: "56500045",
            aggcode: "DistributorCode-monthno-ConsignmentQuantity-Quantity",
            year: "2017",
            month: "10",
            ConsignmentQuantity: "0.000",
            Quantity: "1254.500"
        }, {
            DistributorCode: "56500045",
            aggcode: "DistributorCode-monthno-ConsignmentQuantity-Quantity",
            year: "2017",
            month: "11",
            ConsignmentQuantity: "0.000",
            Quantity: "5660.500"
        }, {
            DistributorCode: "56500051",
            aggcode: "DistributorCode-monthno-ConsignmentQuantity-Quantity",
            year: "2017",
            month: "8",
            ConsignmentQuantity: "0.000",
            Quantity: "1900.000"
        }, {
            DistributorCode: "56500051",
            aggcode: "DistributorCode-monthno-ConsignmentQuantity-Quantity",
            year: "2017",
            month: "10",
            ConsignmentQuantity: "0.000",
            Quantity: "1853.000"
        }]
    },
    chartoption: {
        theme: "infographic",
            option: {
            animation: true,
                title: {
                id: "",
                    text: "test",
                    link: "",
                    target: "blank",
                    subtext: "",
                    sublink: "",
                    textStyle: {
                    color: "#ffffff"
                },
                subtextStyle: {
                    color: "#dddddd"
                },
                show: true,
                    backgroundColor: "transparent",
                    borderColor: "#ccc",
                    borderWidth: 0,
                    itemGap: 50,
                    zlevel: 0,
                    z: 2,
                    left: "auto",
                    top: "auto",
                    right: "auto",
                    bottom: "auto",
                    shadowBlur: 0,
                    shadowColor: "",
                    shadowOffsetX: 0,
                    shadowOffsetY: 0
            },
            toolbox: {
                id: "",
                    feature: {},
                orient: "horizontal",
                    itemSize: 15,
                    showTitle: true,
                    iconStyle: {
                    normal: {
                        borderColor: "#999999"
                    },
                    emphasis: {
                        borderColor: "#666666"
                    }
                },
                show: true,
                    itemGap: 10,
                    zlevel: 0,
                    z: 2,
                    left: "auto",
                    top: "auto",
                    right: "auto",
                    bottom: "auto",
                    width: "auto",
                    height: "auto"
            },
            tooltip: {
                showContent: true,
                    trigger: "item",
                    triggerOn: "mousemove|click",
                    showDelay: 0,
                    hideDelay: 0,
                    transitionDuration: 0.4,
                    enterable: false,
                    axisPointer: {
                    lineStyle: {
                        color: "#cccccc",
                            width: 1
                    },
                    crossStyle: {
                        color: "#cccccc",
                            width: 1
                    }
                },
                textStyle: {
                    color: "#fff",
                        fontSize: 14,
                        fontStyle: "normal",
                        fontWeight: "normal"
                },
                alwaysShowContent: false,
                    show: true,
                    backgroundColor: "rgba(50,50,50,0.7)",
                    borderColor: "#333",
                    borderWidth: 0
            },
            legend: {
                id: "",
                    orient: "horizontal",
                    type: "plain",
                    itemWidth: 25,
                    itemHeight: 14,
                    textStyle: {
                    color: "#999999"
                },
                selectedMode: "true",
                    selected: {},
                data: ["ConsignmentQuantity", "Quantity"],
                    align: "auto",
                    show: true,
                    backgroundColor: "transparent",
                    borderColor: "#ccc",
                    borderWidth: 1,
                    itemGap: 10,
                    zlevel: 0,
                    z: 2,
                    shadowBlur: 0,
                    shadowColor: "",
                    shadowOffsetX: 0,
                    shadowOffsetY: 0
            },
            grid: {
                id: "",
                    containLabel: false,
                    show: true,
                    backgroundColor: "transparent",
                    borderColor: "#ccc",
                    borderWidth: 0,
                    zlevel: 0,
                    z: 2,
                    left: "10%",
                    top: "60",
                    right: "10%",
                    bottom: "60",
                    width: "auto",
                    height: "auto",
                    shadowBlur: 0,
                    shadowColor: "",
                    shadowOffsetX: 0,
                    shadowOffsetY: 0
            },
            xAxis: [{
                type: "category",
                position: "bottom",
                data: ["56500040\n2017\n11", "56500041\n2017\n8", "56500041\n2017\n10", "56500041\n2017\n11", "56500045\n2017\n7", "56500045\n2017\n8", "56500045\n2017\n10", "56500045\n2017\n11", "56500051\n2017\n8", "56500051\n2017\n10", "56500051\n2017\n11", "56501014\n2017\n11", "56501375\n2017\n11", "56501400\n2017\n6", "56501400\n2017\n7", "56501400\n2017\n8", "56501400\n2017\n10", "56501400\n2017\n11", "56501460\n2017\n11", "56501465\n2017\n7", "56501465\n2017\n8", "56501465\n2017\n10", "56501465\n2017\n11", "56501721\n2017\n10", "56501721\n2017\n11", "56501920\n2017\n7", "56501920\n2017\n8", "56501920\n2017\n10", "56501920\n2017\n11", "56501922\n2017\n8", "56501922\n2017\n10", "56501922\n2017\n11", "56501923\n2017\n6", "56501923\n2017\n7", "56501923\n2017\n8", "56501923\n2017\n10", "56501923\n2017\n11", "56501981\n2017\n8", "56501981\n2017\n10", "56501981\n2017\n11", "56502021\n2017\n8", "56502021\n2017\n10", "56502021\n2017\n11", "56502095\n2017\n6", "56502095\n2017\n7", "56502095\n2017\n10", "56502095\n2017\n11", "56502647\n2017\n10", "56502647\n2017\n11", "56502786\n2017\n8", "56502786\n2017\n10", "56502786\n2017\n11", "56503112\n2017\n8", "56503112\n2017\n10", "56503112\n2017\n11", "56503370\n2017\n8", "56503370\n2017\n10", "56503370\n2017\n11", "56503390\n2017\n11"]
            }],
                yAxis: [{
                type: "value",
                name: "左轴"
            }],
               toolbox: {        show: true,        feature: {   myDownload: {
                           show: true,
                           title: '数据导出',
                           icon: 'image://http://localhost:8080/smbi/image/excel.svg',
                           onclick: function (){
                               alert('暂不支持');
                           }
                       },         saveAsImage: {}        }    },series: [{
                barMinHeight: 0,
                barWidth: {},
                barMaxWidth: 0,
                barGap: "30%",
                defaultMap: {},
                legendHoverLink: true,
                xAxisIndex: 0,
                yAxisIndex: 0,
                name: "ConsignmentQuantity",
                type: "bar",
                stack: "",
                tooltip: {
                    textStyle: {
                        color: "#fff",
                        fontSize: 14,
                        fontStyle: "normal",
                        fontWeight: "normal"
                    },
                    backgroundColor: "rgba(50,50,50,0.7)",
                    borderColor: "#333",
                    borderWidth: 0
                },
                itemStyle: {},
                markPoint: {
                    symbol: "pin",
                    symbolSize: "50",
                    itemStyle: {},
                    data: [],
                    animation: true,
                    animationDuration: 1000,
                    animationEasing: "cubicOut",
                    animationDurationUpdate: 300,
                    animationEasingUpdate: "cubicOut"
                },
                markLine: {
                    symbol: {},
                    symbolSize: {},
                    precision: 2,
                    data: [],
                    animation: true,
                    animationDuration: 1000,
                    animationEasing: "cubicOut",
                    animationDurationUpdate: 300,
                    animationEasingUpdate: "cubicOut"
                },
                zlevel: 0,
                z: 2,
                label: {
                    color: "#fff"
                },
                coordinateSystem: "cartesian2d",
                data: ["0.000", "10844.800", "9571.750", "86319.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "311113.000", "25107.000", "13214.000", "54474.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000"]
            }, {
                barMinHeight: 0,
                barWidth: {},
                barMaxWidth: 0,
                barGap: "30%",
                defaultMap: {},
                legendHoverLink: true,
                xAxisIndex: 0,
                yAxisIndex: 0,
                name: "Quantity",
                type: "bar",
                stack: "",
                tooltip: {
                    textStyle: {
                        color: "#fff",
                        fontSize: 14,
                        fontStyle: "normal",
                        fontWeight: "normal"
                    },
                    backgroundColor: "rgba(50,50,50,0.7)",
                    borderColor: "#333",
                    borderWidth: 0
                },
                itemStyle: {},
                markPoint: {
                    symbol: "pin",
                    symbolSize: "50",
                    itemStyle: {},
                    data: [],
                    animation: true,
                    animationDuration: 1000,
                    animationEasing: "cubicOut",
                    animationDurationUpdate: 300,
                    animationEasingUpdate: "cubicOut"
                },
                markLine: {
                    symbol: {},
                    symbolSize: {},
                    precision: 2,
                    data: [],
                    animation: true,
                    animationDuration: 1000,
                    animationEasing: "cubicOut",
                    animationDurationUpdate: 300,
                    animationEasingUpdate: "cubicOut"
                },
                zlevel: 0,
                z: 2,
                label: {
                    color: "#fff"
                },
                coordinateSystem: "cartesian2d",
                data: ["2989.900", "3708.500", "2615.000", "12127.050", "5008.500", "863.500", "1254.500", "5660.500", "1900.000", "1853.000", "6482.000", "4376.000", "12752.500", "100254.000", "253054.000", "13051.500", "10369.250", "41221.000", "38463.000", "5369.000", "897.000", "1836.000", "1836.000", "1835.500", "4018.250", "122634.000", "15754.000", "6899.000", "28005.000", "6215.000", "4259.000", "20718.000", "110805.000", "194894.000", "7391.000", "7558.000", "43360.000", "3775.000", "4293.000", "13991.000", "1354.000", "1152.000", "7423.000", "878.000", "10238.000", "1614.000", "4269.000", "22154.250", "108126.500", "663.000", "575.000", "3212.000", "2755.000", "2295.000", "22614.000", "720.000", "802.000", "3319.000", "2315.000"]
            }],
                animationThreshold: 2000,
                animationDuration: 1000,
                animationDurationUpdate: 300,
                animationEasing: "linear",
                animationEasingUpdate: "cubicOut",
                radar: {
                id: "",
                    zlevel: 0,
                    z: 2,
                    center: ["50%", "50%"],
                    radius: "75%",
                    startAngle: 90,
                    name: {
                    show: true,
                        textStyle: {
                        color: "#333"
                    }
                },
                nameGap: 15,
                    splitNumber: 5,
                    scale: false,
                    silent: false,
                    triggerEvent: false,
                    axisLine: "true         true                  none         [10, 15]         [0, 0]                      #333             1             solid",
                    splitLine: "true         auto                      #333             solid",
                    splitArea: "true         auto                      ['rgba(250,250,250,0.3)','rgba(200,200,200,0.3)']                                       0             0",
                    itemStyle: {
                    normal: {
                        borderWidth: "4"
                    }
                },
                lineStyle: {
                    normal: {
                        width: "3"
                    }
                },
                symbolSize: "0",
                    symbol: "circle",
                    smooth: true
            },
            color: ["#fc97af", "#87f7cf", "#f7f494", "#72ccff", "#f7c5a0", "#d4a4eb", "#d2f5a6", "#76f2f2"],
                backgroundColor: "rgba(41,52,65,1)",
                textStyle: {},
            line: {
                itemStyle: {
                    normal: {
                        borderWidth: "4"
                    }
                },
                lineStyle: {
                    normal: {
                        width: "3"
                    }
                },
                symbolSize: "0",
                    symbol: "circle",
                    smooth: true
            },
            bar: {
                itemStyle: {
                    normal: {
                        barBorderWidth: 0,
                            barBorderColor: "#ccc"
                    },
                    emphasis: {
                        barBorderWidth: 0,
                            barBorderColor: "#ccc"
                    }
                }
            },
            pie: {
                itemStyle: {
                    normal: {
                        borderWidth: 0,
                            borderColor: "#ccc"
                    },
                    emphasis: {
                        borderWidth: 0,
                            borderColor: "#ccc"
                    }
                }
            },
            scatter: {
                itemStyle: {
                    normal: {
                        borderWidth: 0,
                            borderColor: "#ccc"
                    },
                    emphasis: {
                        borderWidth: 0,
                            borderColor: "#ccc"
                    }
                }
            },
            boxplot: {
                itemStyle: {
                    normal: {
                        borderWidth: 0,
                            borderColor: "#ccc"
                    },
                    emphasis: {
                        borderWidth: 0,
                            borderColor: "#ccc"
                    }
                }
            },
            parallel: {
                itemStyle: {
                    normal: {
                        borderWidth: 0,
                            borderColor: "#ccc"
                    },
                    emphasis: {
                        borderWidth: 0,
                            borderColor: "#ccc"
                    }
                }
            },
            sankey: {
                itemStyle: {
                    normal: {
                        borderWidth: 0,
                            borderColor: "#ccc"
                    },
                    emphasis: {
                        borderWidth: 0,
                            borderColor: "#ccc"
                    }
                }
            },
            funnel: {
                itemStyle: {
                    normal: {
                        borderWidth: 0,
                            borderColor: "#ccc"
                    },
                    emphasis: {
                        borderWidth: 0,
                            borderColor: "#ccc"
                    }
                }
            },
            gauge: {
                itemStyle: {
                    normal: {
                        borderWidth: 0,
                            borderColor: "#ccc"
                    },
                    emphasis: {
                        borderWidth: 0,
                            borderColor: "#ccc"
                    }
                }
            },
            candlestick: {
                itemStyle: {
                    normal: {
                        color: "#fc97af",
                            color0: "transparent",
                            borderColor: "#fc97af",
                            borderColor0: "#87f7cf",
                            borderWidth: "2"
                    }
                }
            },
            graph: {
                itemStyle: {
                    normal: {
                        borderWidth: 0,
                            borderColor: "#ccc"
                    }
                },
                lineStyle: {
                    normal: {
                        width: "1",
                            color: "#ffffff"
                    }
                },
                symbolSize: "0",
                    symbol: "circle",
                    smooth: true,
                    color: ["#fc97af", "#87f7cf", "#f7f494", "#72ccff", "#f7c5a0", "#d4a4eb", "#d2f5a6", "#76f2f2"],
                    label: {
                    normal: {
                        textStyle: {
                            color: "#293441"
                        }
                    }
                }
            },
            map: {
                itemStyle: {
                    normal: {
                        areaColor: "#f3f3f3",
                            borderColor: "#999999",
                            borderWidth: 0.5
                    },
                    emphasis: {
                        areaColor: "rgba(255,178,72,1)",
                            borderColor: "#eb8146",
                            borderWidth: 1
                    }
                },
                label: {
                    normal: {
                        textStyle: {
                            color: "#893448"
                        }
                    },
                    emphasis: {
                        textStyle: {
                            color: "rgb(137,52,72)"
                        }
                    }
                }
            },
            geo: {
                itemStyle: {
                    normal: {
                        areaColor: "#f3f3f3",
                            borderColor: "#999999",
                            borderWidth: 0.5
                    },
                    emphasis: {
                        areaColor: "rgba(255,178,72,1)",
                            borderColor: "#eb8146",
                            borderWidth: 1
                    }
                },
                label: {
                    normal: {
                        textStyle: {
                            color: "#893448"
                        }
                    },
                    emphasis: {
                        textStyle: {
                            color: "rgb(137,52,72)"
                        }
                    }
                }
            },
            categoryAxis: {
                axisLine: {
                    show: true,
                        lineStyle: {
                        color: "#666666"
                    }
                },
                axisTick: {
                    show: false,
                        lineStyle: {
                        color: "#333"
                    }
                },
                axisLabel: {
                    show: true,
                        textStyle: {
                        color: "#aaaaaa"
                    }
                },
                splitLine: {
                    show: false,
                        lineStyle: {
                        color: ["#e6e6e6"]
                    }
                },
                splitArea: {
                    show: false,
                        areaStyle: {
                        color: ["rgba(250,250,250,0.05)", "rgba(200,200,200,0.02)"]
                    }
                }
            },
            valueAxis: {
                axisLine: {
                    show: true,
                        lineStyle: {
                        color: "#666666"
                    }
                },
                axisTick: {
                    show: false,
                        lineStyle: {
                        color: "#333"
                    }
                },
                axisLabel: {
                    show: true,
                        textStyle: {
                        color: "#aaaaaa"
                    }
                },
                splitLine: {
                    show: false,
                        lineStyle: {
                        color: ["#e6e6e6"]
                    }
                },
                splitArea: {
                    show: false,
                        areaStyle: {
                        color: ["rgba(250,250,250,0.05)", "rgba(200,200,200,0.02)"]
                    }
                }
            },
            logAxis: {
                axisLine: {
                    show: true,
                        lineStyle: {
                        color: "#666666"
                    }
                },
                axisTick: {
                    show: false,
                        lineStyle: {
                        color: "#333"
                    }
                },
                axisLabel: {
                    show: true,
                        textStyle: {
                        color: "#aaaaaa"
                    }
                },
                splitLine: {
                    show: false,
                        lineStyle: {
                        color: ["#e6e6e6"]
                    }
                },
                splitArea: {
                    show: false,
                        areaStyle: {
                        color: ["rgba(250,250,250,0.05)", "rgba(200,200,200,0.02)"]
                    }
                }
            },
            timeAxis: {
                axisLine: {
                    show: true,
                        lineStyle: {
                        color: "#666666"
                    }
                },
                axisTick: {
                    show: false,
                        lineStyle: {
                        color: "#333"
                    }
                },
                axisLabel: {
                    show: true,
                        textStyle: {
                        color: "#aaaaaa"
                    }
                },
                splitLine: {
                    show: false,
                        lineStyle: {
                        color: ["#e6e6e6"]
                    }
                },
                splitArea: {
                    show: false,
                        areaStyle: {
                        color: ["rgba(250,250,250,0.05)", "rgba(200,200,200,0.02)"]
                    }
                }
            },
            markPoint: {
                label: {
                    normal: {
                        textStyle: {
                            color: "#293441"
                        }
                    },
                    emphasis: {
                        textStyle: {
                            color: "#293441"
                        }
                    }
                }
            }
        }
    }
}
}