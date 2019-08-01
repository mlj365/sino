
// 饼图
function h_pie(demoid, title_name, series_params) {
	demoid.highcharts({
		chart: {
            type: 'pie',
            options3d: {
                enabled: true,
                alpha: 30,
                beta: 0
            }
        },
	    title : {
	    	floating: true,
	    	align: 'left',
	        text: title_name,
	        y: 20
	    },
      	tooltip : {
	    	pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
	    }, 
	    plotOptions: {
            pie: {
            	depth: 45,
                allowPointSelect: true,//分离
                cursor: 'pointer',
                dataLabels: {
                    enabled: true,
                    color: '#000',
			   		distance: 13,
                    format: '<b>{point.percentage:.1f}%</b>',
	                borderRadius: 5,
	                backgroundColor: 'rgba(252, 255, 197, 0.7)',
	                borderWidth: 1,
	                borderColor: '#AAA'
                },
				showInLegend:  true
            }
        },
	     legend: {
            layout: 'vertical',
            floating: true,
            align: 'left',
            verticalAlign: 'bottom', //top, middle, bottom
            borderWidth: 1,
          labelFormatter: function() {
                return this.name+'&nbsp';
            },
            useHTML:true
        },
	    series : series_params
	    	/*[{
    		 type: 'pie',
			 name: '占比',
	    	 data: [
				{name: "活跃", y: 69.51 , color: '#039452',selected:true,sliced:true},//默认选中并分离出来
				{name: "一般", y: 13.92 , color:'#f3990c'},
				{name: "消极", y: 10.51 , color: '#8cbf3e'},
				{name: "死亡", y: 6.06  , color: '#f71818'}
        	]
	    }]*/
	});
}

//双坐标轴组合图（柱形图与折线图）
function h_column_spline(demoid, title_name, xAxis_categories, yAxis_text, yAxis_text2, series_params) {
	demoid.highcharts({
		title : {
			floating: true,
	    	align: 'left',
			text : title_name,
			y: 20
		},
		 chart: {
            zoomType: 'xy'
        },
		tooltip : {
			shared: true
		},
		 legend : {
			floating: false,   //true, false
			align: 'center',  //left，center 和 right
			layout: 'horizontal'   //horizontal, vertical
		}, 
		xAxis : [{
			 categories: xAxis_categories, //[ "一月", "二月", "三月", "四月", "五月", "六月" ],
			 crosshair: true
		}],
		yAxis : [{
            labels: {
                style: {
                    color: Highcharts.getOptions().colors[0]
                }
            },
            title: {
                text: yAxis_text,
                style: {
                    color: Highcharts.getOptions().colors[0]
                }
            }
       	 }, {
             labels: {
            	 format: '{value} %',
                 style: {
                     color: Highcharts.getOptions().colors[1]
                 }
             },
             title: {
                 text: yAxis_text2,
                 style: {
                     color: Highcharts.getOptions().colors[1]
                 }
             },
             opposite: true   //对面显示
        }],
		toolbox: {        show: true,        feature: {  myDownload: {
                    show: true,
                    title: '数据导出',
                    icon: 'image://http://localhost:8080/smbi/image/excel.svg',
                    onclick: function (){
                        alert('暂不支持');
                    }
                },          saveAsImage: {}        }    },series: series_params,
		/*[{
            name: '销量',
            type: 'column',
            data : [ 5, 20, 36, 6, 43, 67]
          
        }, {
            name: '指标',
            type: 'spline',
            data : [ 5, 20, 36, 6, 43, 67]
        }]*/
	}); 
}

//柱形图
function h_column(demoid, title_name, xAxis_categories, yAxis_text, series_params) {
	demoid.highcharts({
		title : {
			floating: true,
	    	align: 'left',
			text : title_name,
			y: 20
		},
		 chart: {
            zoomType: 'xy'
        },
		tooltip : {
		//	pointFormat: '{series.name}: <b>{point.percentage:.0f}%</b>',
			shared: true
		},
		 legend : {
			floating: false,   //true, false
			align: 'center',  //left，center 和 right
			layout: 'horizontal'   //horizontal, vertical
		}, 
		xAxis : [{
			 categories: xAxis_categories, //[ "一月", "二月", "三月", "四月", "五月", "六月" ],
			 crosshair: true
		}],
		yAxis : [{
            labels: {
                style: {
                    color: Highcharts.getOptions().colors[0]
                }
            },
            title: {
                text: yAxis_text,
                style: {
                    color: Highcharts.getOptions().colors[0]
                }
            }
       	 }],
		toolbox: {        show: true,        feature: {   myDownload: {
                    show: true,
                    title: '数据导出',
                    icon: 'image://http://localhost:8080/smbi/image/excel.svg',
                    onclick: function (){
                        alert('暂不支持');
                    }
                },         saveAsImage: {}        }    },series: series_params
	}); 
}

function h_gauge(demoid, title_name, yAxis_text, series_params) {
	demoid.highcharts({
		chart: {
            type: 'gauge',
            plotBackgroundColor: null,
            plotBackgroundImage: null,
            plotBorderWidth: 0,
            plotShadow: false
        },
        title: {
        	floating: true,
            text: title_name,
            align: 'left',
            y: 20
        },
        pane: [{
            startAngle: -180,
            endAngle: 45,
            center: ['25%', '50%'],
            background: [{
                backgroundColor: '#fff',
                borderWidth: 0,
                outerRadius: '110%',
                innerRadius: '11%'
            }],
            size: 200
        },{
            startAngle: -45,
            endAngle: 180,
            center: ['75%', '50%'],
            background: [{
                backgroundColor: '#fff',
                borderWidth: 0,
                outerRadius: '110%',
                innerRadius: '11%'
            }],
            size: 200
        },{
            startAngle: -150,
            endAngle: 150,
            center: ['50%', '50%'],
            background: [{
                backgroundColor: '#fff',
                borderWidth: 0,
                outerRadius: '110%',
                innerRadius: '11%'
            }],
            size: 200
        }],
        yAxis: [{
            min: 0,
            max: 200,
            minorTickInterval: 'auto',
            minorTickWidth: 1,
            minorTickLength: 10,
            minorTickPosition: 'inside',
            minorTickColor: '#666',
            tickPixelInterval: 30,
            tickWidth: 2,
            tickPosition: 'inside',
            tickLength: 12,
            tickColor: '#666',
            labels: {
                step: 2,
            },
            title: {
                text: yAxis_text[0],
                y: 160
            },
            plotBands: [{
                from: 0,
                to: 120,
                color: '#55BF3B' // green
            }, {
                from: 120,
                to: 160,
                color: '#DDDF0D' // yellow
            }, {
                from: 160,
                to: 200,
                color: '#DF5353' // red
            }],
            pane: 0
        },{
        	min: 0,
            max: 200,
            minorTickInterval: 'auto',
            minorTickWidth: 1,
            minorTickLength: 10,
            minorTickPosition: 'inside',
            minorTickColor: '#666',
            tickPixelInterval: 30,
            tickWidth: 2,
            tickPosition: 'inside',
            tickLength: 12,
            tickColor: '#666',
            labels: {
                step: 2,
            },
            title: {
                text: yAxis_text[2],
                y: 160
            },
            plotBands: [{
                from: 0,
                to: 120,
                color: '#55BF3B' // green
            }, {
                from: 120,
                to: 160,
                color: '#DDDF0D' // yellow
            }, {
                from: 160,
                to: 200,
                color: '#DF5353' // red
            }],
            pane: 1
        }, {
        	min: 0,
            max: 200,
            minorTickInterval: 'auto',
            minorTickWidth: 1,
            minorTickLength: 10,
            minorTickPosition: 'inside',
            minorTickColor: '#666',
            tickPixelInterval: 30,
            tickWidth: 2,
            tickPosition: 'inside',
            tickLength: 12,
            tickColor: '#666',
            labels: {
                step: 2,
            },
            title: {
                text: yAxis_text[1],
                y: 160
            },
            plotBands: [{
                from: 0,
                to: 120,
                color: '#55BF3B' // green
            }, {
                from: 120,
                to: 160,
                color: '#DDDF0D' // yellow
            }, {
                from: 160,
                to: 200,
                color: '#DF5353' // red
            }],
            pane: 2
        }],
       toolbox: {        show: true,        feature: {     myDownload: {
                   show: true,
                   title: '数据导出',
                   icon: 'image://http://localhost:8080/smbi/image/excel.svg',
                   onclick: function (){
                       alert('暂不支持');
                   }
               },       saveAsImage: {}        }    },series: series_params
        /*[{
            name: 'Speed',
            data: [80],
            yAxis: 0,
            tooltip: {
                valueSuffix: ' km/h'
            }
        },
         {
             name: 'Speed',
             data: [80],
             yAxis: 1,
             tooltip: {
                 valueSuffix: ' km/h'
             }
         }]*/
    });
}

//两个电压表
function h_gauge_v(demoid, title_name, yAxis_text, series_params) {
	demoid.highcharts({
        chart: {
            type: 'gauge',
            plotBorderWidth: 0,
            plotBackgroundColor: {
                linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
                stops: [
                    [0, '#CEEDFF'],
                    [0.3, '#FFFFFF'],
                    [0.8, '#CEEDFF']
                ]
            },
            plotBackgroundImage: null,
            height: 270
        },
        title: {
        	//floating: true,
        	align: 'left',
            text: title_name,    //'伏压图'
            y: 20
        },
        pane: [{
            startAngle: -45,
            endAngle: 45,
            background: null,
            center: ['25%', '115%'],
            size: 300
        }, {
            startAngle: -45,
            endAngle: 45,
            background: null,
            center: ['75%', '115%'],
            size: 300
        }],
        yAxis: [{
            min: 0,
            max: 100,
            minorTickPosition: 'outside',
            tickPosition: 'outside',
            labels: {
                rotation: 'auto',
                distance: 20
            },
            plotBands: [{
                from: 0,
                to: 6,
                color: '#C02316',
                innerRadius: '100%',
                outerRadius: '105%'
            }],
            pane: 0,
            title: {
                text: yAxis_text[0],  //'VU<br/><span style="font-size:8px">Channel A</span>',
                y: -40
            }
        }, {
            min: 0,
            max: 100,
            minorTickPosition: 'outside',
            tickPosition: 'outside',
            labels: {
                rotation: 'auto',
                distance: 20
            },
            plotBands: [{
                from: 0,
                to: 6,
                color: '#C02316',
                innerRadius: '100%',
                outerRadius: '105%'
            }],
            pane: 1,
            title: {
                text: yAxis_text[1],    //'VU<br/><span style="font-size:8px">Channel B</span>',
                y: -40
            }
        }],
        plotOptions: {
            gauge: {
                dataLabels: {
                    enabled: false
                },
                dial: {
                    radius: '100%'
                }
            }
        },
       toolbox: {        show: true,        feature: { myDownload: {
                   show: true,
                   title: '数据导出',
                   icon: 'image://http://localhost:8080/smbi/image/excel.svg',
                   onclick: function (){
                       alert('暂不支持');
                   }
               },           saveAsImage: {}        }    },series: series_params
        /*[{
            data: [-20],
            yAxis: 0
        }, {
            data: [-20],
            yAxis: 1
        }]*/
    });
}

//单个电压表
function h_gauge_anV(demoid, title_name, yAxis_text, series_params) {
	demoid.highcharts({
        chart: {
            type: 'gauge',
            plotBorderWidth: 0,
            plotBackgroundColor: {
                linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
                stops: [
                    [0, '#CEEDFF'],
                    [0.3, '#FFFFFF'],
                    [0.8, '#CEEDFF']
                ]
            },
            plotBackgroundImage: null,
            height: 240
        },
        title: {
        	//floating: true,
        	align: 'left',
            text: title_name,    //'伏压图'
            y: 20
        },
        pane: [{
            startAngle: -60,
            endAngle: 50,
            background: null,
            center: ['50%', '115%'],
            size: 300
        }],
        yAxis: [{
            min: -10,
            max: 100,
            
            minorTickInterval: 'auto',
            minorTickWidth: 1,
            minorTickLength: 10,
            minorTickPosition: 'outside',
            minorTickColor: '#666',
            tickPixelInterval: 30,
            tickWidth: 2,
            tickPosition: 'inside',
            tickLength: 12,
            tickColor: '#666',
            minorTickPosition: 'outside',
            tickPosition: 'outside',
            labels: {
                rotation: 'auto',
                distance: 20
            },
            plotBands: [{
                from: -10,
                to: 0,
                color: '#C02316',
                innerRadius: '100%',
                outerRadius: '105%'
            }],
            pane: 0,
            title: {
                text: yAxis_text,  //'VU<br/><span style="font-size:8px">Channel A</span>',
                y: -40
            }
        }],
        plotOptions: {
            gauge: {
                dataLabels: {
                    enabled: false
                },
                dial: {
                    radius: '100%'
                }
            }
        },
       toolbox: {        show: true,        feature: { myDownload: {
                   show: true,
                   title: '数据导出',
                   icon: 'image://http://localhost:8080/smbi/image/excel.svg',
                   onclick: function (){
                       alert('暂不支持');
                   }
               },           saveAsImage: {}        }    },series: series_params
        /*[{
            data: [-20],
            yAxis: 0
        }, {
            data: [-20],
            yAxis: 1
        }]*/
    });
}

//时间轴曲线图
function h_spline_datetime(demoid,load_series, title_name, yAxis_text, series_params) {
	demoid.highcharts({
	    chart: {
	        type: 'spline',
	        animation: Highcharts.svg, 
	//        marginRight: 10,
	        events: {
	            load: load_series
	            /*function () {
	                var series0 = this.series[0],
	                	series1 = this.series[1],
	                	series2 = this.series[2],
	                    chart = this;
	                setInterval(function () {
	                    var x = (new Date()).getTime(), 
	                        y0 = Math.random() * 10,
	                        y1 = Math.random()* 10,
	                        y2 = Math.random() * 10;
	                    	
	                    series0.addPoint([x, y0], false, true);
	                    series1.addPoint([x, y1], false, true);
	                    series2.addPoint([x, y2], false, true);
	                    chart.redraw();
	                }, 1000);
	            }*/
	        }
	    },
	    title: {
	        text: title_name  //'动态模拟实时数据'
	    },
	    legend: {
            layout: 'horizontal', //'vertical',
            align: 'center', //'left',
            verticalAlign: 'bottom',
          //  x: 10,
          //  y: 20,
            floating:  false,//true,
            borderWidth: 0,
            backgroundColor: (Highcharts.theme && Highcharts.theme.legendBackgroundColor) || '#FFFFFF'
        },
	    xAxis: {
	        type: 'datetime',
	        tickPixelInterval: 150
	    },
	    yAxis: [{
	    	labels: {
	    		format: '{value} %',
                style: {
                    color: series_params[0].color
                }
            },
	        title: {
	            text: yAxis_text[0]  
	            
	        },
	        plotLines: [{
	            value: 0,
	            width: 1,
	            color: '#808080'
	        }],
	    //    opposite: true   //对面显示
	    },{
	    	labels: {
	    		format: '{value} %',
                style: {
                    color: series_params[1].color
                }
            },
	        title: {
	            text: yAxis_text[1]  
	        },
	        opposite: true   //对面显示
	    }, {
	    	labels: {
	    		format: '{value} %',
                style: {
                    color: series_params[2].color
                }
            },
	        title: {
	            text: yAxis_text[2]
	        },
	        plotLines: [{
	            value: 0,
	            width: 1,
	            color: '#808080'
	        }],
	     //   opposite: true   //对面显示
	    }],
	    
	    tooltip: {
	    	shared: true,
	    	valueSuffix: ' %'
	        /*formatter: function () {
	            return '<b>' + this.series.name + '</b><br/>' +
	                Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x) + '<br/>' +
	                Highcharts.numberFormat(this.y, 2);
	        }*/
	    },
	    exporting: {
	        enabled: false
	    },
	   toolbox: {        show: true,        feature: { myDownload: {
                   show: true,
                   title: '数据导出',
                   icon: 'image://http://localhost:8080/smbi/image/excel.svg',
                   onclick: function (){
                       alert('暂不支持');
                   }
               },           saveAsImage: {}        }    },series: series_params
	    	
	    	/*[{
	        name: '随机数据',
	        data: (function () {
	            // generate an array of random data
	            var data = [],
	                time = (new Date()).getTime(),
	                i;
	            for (i = -19; i <= 0; i += 1) {
	                data.push({
	                    x: time + i * 1000,
	                    y: Math.random()
	                });
	            }
	            return data;
	        }())
	    }]*/
	});
}

//面积图
function h_areaspline(demoid, load_series, title_name, yAxis_text, series_params) {
	demoid.highcharts({
        chart: {
            type: 'areaspline',
            events: {
            	load: load_series
            }
        },
        
        title: {
            text: title_name   //'不同家庭一周水果消费情况'
        },
        legend: {
            layout: 'horizontal', //'vertical',
            align: 'center', //'left',
            verticalAlign: 'bottom',
            floating:  false,//true,
            borderWidth: 0,
            backgroundColor: (Highcharts.theme && Highcharts.theme.legendBackgroundColor) || '#FFFFFF'
        },
        xAxis: {
	        type: 'datetime',
	        tickPixelInterval: 150
	    },
        yAxis: {
        	labels: {
	    		format: '{value} %',
                style: {
                    color: Highcharts.getOptions().colors[1]
                }
            },
            title: {
                text: yAxis_text  //'水果 单位'
            },
            opposite: true   //对面显示
        },
        tooltip: {
            shared: true,
            valueSuffix: ' %'
        },
        credits: {
            enabled: false
        },
        plotOptions: {
            areaspline: {
                fillOpacity: 0.5
            }
        },
       toolbox: {        show: true,        feature: { myDownload: {
                   show: true,
                   title: '数据导出',
                   icon: 'image://http://localhost:8080/smbi/image/excel.svg',
                   onclick: function (){
                       alert('暂不支持');
                   }
               },           saveAsImage: {}        }    },series: series_params
        
        /*[{
            name: '小张',
            data: [3, 4, 3, 5, 4, 10, 12]
        }, {
            name: '小潘',
            data: [1, 3, 4, 3, 3, 5, 4]
        }]*/
    });
}
