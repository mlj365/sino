function initChart(t, s0, s1) {
	var option={};
	option.chart={};
	option.chart.type=t;
	option.title={};
	option.title.text=s0;
	option.title.style={color:"#0070C0", fontSize:"16px", fontWeight:"bold"};
	option.subtitle={};
	option.subtitle.text=s1;
	option.subtitle.style={color:"#A0A0A0", fontSize:"12px"};
	option.xAxis={categories:[], title:{text:null}};
	option.yAxis={title:{text:null}};
	option.series=[];
	option.legend={};
	option.legend.borderWidth=1;
	option.legend.borderColor="#E0E0E0";
	option.legend.backgroundColor="rgba(240,240,240,0.5)";
	option.legend.enabled=true;
    option.plotOptions={};
    option.tooltip={};
    option.exporting = {
    	enabled:true, //用来设置是否显示'打印'/'导出'等功能按钮，不设置时默认为显示  
        buttons: {
            contextButton: {
                menuItems: [{
                    text: '下载PNG格式(小)',
                    onclick: function () {
                        this.exportChart({
                            width: 250,
                        });
                    }
                }, {
                    text: '下载PNG格式(大)',
                    onclick: function () {
                        this.exportChart();
                    },
                    separator: false,
                }]
                
            }
        }
    };
	option.colors=[
		"rgba(0,160,255,0.8)", "rgba(255,160,60,0.8)", "rgba(160,120,220,0.8)", 
		"rgba(120,220,0,0.8)", "rgba(220,220,60,0.8)", "rgba(255,80,80,0.8)", 
		"rgba(120,220,220,0.8)", "rgba(255,120,220,0.8)", "rgba(160,255,120,0.8)",
		"rgba(160,160,160,0.8)"];
	if (t=="gauge") {
		option=initGauge(option);
	} else if (t=="waterfall") {
		option=initWaterfall(option);
	} else if (t=="pie") {
		option=initPie(option);
	} else if (t=="bubble") {
		option=initBubble(option);
	}
	return option;
}

function initGauge(option) {
	option.chart= {
		type: 'gauge',
		plotBackgroundColor: null,
		plotBackgroundImage: null,
		plotBorderWidth: 0,
		plotShadow: false
	};
    option.pane= {
		startAngle: -150,
		endAngle: 150,
		background: [{
			backgroundColor: {linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 }, stops: [[0, '#FFF'], [1, '#333']]},
			borderWidth: 0,
			outerRadius: '109%'
		}, {
			backgroundColor: {linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 }, stops: [[0, '#333'],[1, '#FFF']]},
			borderWidth: 1,
			outerRadius: '107%'
		}, {
		}, {
			backgroundColor: '#DDD',
			borderWidth: 0,
			outerRadius: '105%',
			innerRadius: '103%'
		}]
    };
	option.yAxis= {min: 0, max: 200,
		minorTickInterval: 'auto',
		minorTickWidth: 1,
		minorTickLength: 10,
		minorTickPosition: 'inside',
		minorTickColor: '#666',
		tickPixelInterval: 30,
		tickWidth: 2,
		tickPosition: 'inside',
		tickLength: 10,
		tickColor: '#666',
		labels: {step: 2,	rotation: 'auto'},
		title: {text: "达成率%"},
		plotBands: [
			{from: 0, to: 70, color: '#E06060'},
			{from: 70, to: 100, color: '#E0E060'},
			{from: 100, to: 140, color: '#60E060'},
			{from: 140, to: 200, color: '#60A0E0'}
		]
	};
    return option;
}

function initWaterfall(option) {
	option.xAxis={type: 'category'};
	option.legend={enabled: false};
	option.series=[{
		borderColor:"rgba(0,0,0,0.3)",
		data: [],
		dataLabels: { enabled:true, style:{color:"#FFFFFF"} },
        pointPadding: 0
	}];
	option.legend.enabled=false;
	return option;
}

function initPie(option) {
 	option.chart.options3d={enabled:true, alpha:45, beta:0};
    option.tooltip.pointFormat="{series.name}: {point.percentage:.1f}%";
    option.plotOptions.pie={allowPointSelect:true, depth:45, innerSize:80};
    option.plotOptions.pie.dataLabels={enabled:true, format:"{point.name}", color:"#808080"};
    option.series=[{type:"pie", name:"构成", data:[]}];
	return option;
}

function initBubble(option) {
	option.chart.plotBorderWidth=1;
	option.chart.zoomType="xy";
	option.xAxis.gridLineWidth=1;
	option.xAxis.tickInterval=10;
	option.xAxis.startOnTick=true;
	option.xAxis.endOnTick=true;
	option.yAxis.tickInterval=10;
	option.yAxis.startOnTick=true;
	option.yAxis.endOnTick=true;
	option.plotOptions.series={dataLabels:{enabled:true, format:"{series.name}"}};
	option.series=[];
	for (var i=0; i<6; i++) {
		var fill={};
		fill.radialGradient={ cx: 0.4, cy: 0.3, r: 0.7 };
		fill.stops=[ [0, "rgba(255,255,255,0.5)"], [1, ""] ];
		fill.stops[1][1]=option.colors[i];
		option.series[i]={marker:{fillColor:fill}};
	}
	return option;
}