
//**********************1.最左面，年度总达成
	var middleOption = {
        backgroundColor:"#fff",
		tooltip : {
			formatter: "{a} <br/>{b} = {c}%"
		},

		/*toolbox: {
            show: true,
            feature: {
                myDownload: {
                    show: true,
                    title: '数据导出',
                    icon: 'image://http://localhost:8080/smbi/image/excel.svg',
                    onclick: function (){
                        alert('暂不支持');
                    }
                },
                saveAsImage: {}
            }
        },*/

		series: [{
			name: '总达成',
			type: "gauge",
			startAngle: 180,
			endAngle: 0,
			min: 0,
			max: 200,
			radius: "120%",
			center: ["50%", "70%"],

			axisLine: {
				show: true,
				lineStyle: {
					width: 15,
					shadowBlur: 0,
					color: [ [0.6, '#fd666d'],[0.8, '#37a2da'],[1, '#67e0e3']]
				}
			},

			splitLine:{
				length:'20%'
			},
			axisTick:{
				
				length:'6'
			},
			pointer:{
				width:'5'
			},
			axisLabel:{
				distance:'2'
			},
		   itemStyle: {
				normal: {
					shadowBlur: 10
				}
			},
			detail: {
				fontSize: 25,
				offsetCenter:[0, '20%'],
				formatter:'{value}%'
			},
			data: [{value:50}]
		}]
	};

	
//**********************2.中间，区域达成
	var barlineoption = {
        color:["#373e41", "#446d85", "#3d67c7",'#37a2da','#988280'],
	        backgroundColor: "#fff",
	        "tooltip": {
	            "trigger": "axis",
	            "axisPointer": {
	                "type": "shadow",
	                textStyle: {
	                    color: "#fff"
	                }
	
	            },
	            formatter: function(params, ticket, callback) {

	                var res = params[0].name;

	                for (var i = 0, l = params.length; i < l; i++) {
	                    if (params[i].seriesType === 'line') {
	                    	if(params[i].seriesName === 'Ach%'){
								res += '<br/>' + params[i].seriesName + ' : ' + (params[i].value ? params[i].value*100 : '-') + '%';
							} else {
								res += '<br/>' + params[i].seriesName + ' : ' + (params[i].value ? params[i].value : '-') + '%';
							}

	                    } else {
	                        res += '<br/>' + params[i].seriesName + ' : ' + (params[i].value ? formatterOneMoney(params[i].value) : '-') + 'k';
	                    }
	                }
	                return res;

	            }
	        },
	        "grid": {
	        	containLabel: true,
	        	top:"25%",
	        	bottom:"12%",
	        	left:"3%",
	        	right:"2%",
	            textStyle: {
	                color: "#222"
	            }
	        },
	        "legend": {
	            textStyle: {
	                color: '#222',
	            },
	            "data": ['采购', '指标', 'Ach%','增长']
	        },
	
	
	        "calculable": true,
	        "xAxis": [{
	            "type": "category",
	            "axisLine": {
	                lineStyle: {
	                    color: '#222'
	                }
	            },
	            "splitLine": {
	                "show": false
	            },
	            "axisTick": {
	                "show": false
	            },
	            "splitArea": {
	                "show": false
	            },
	            "axisLabel": {
	                "interval": 0,
	
	            },
	            "data": ['east','south','west','north','northeast'],
	        }],
	        "yAxis": [{
	        	name:"采购额(k)",
	            "type": "value",
	            "splitLine": {
	                "show": false
	            },
	            "axisLine": {
	                lineStyle: {
	                    color: '#222'
	                }
	            },
	            "axisTick": {
	                "show": true
	            },
	            "axisLabel": {
	                "interval": 0,
	
	            },
	            "splitArea": {
	                "show": false
	            },
	            axisLabel: {
                    formatter: '{value}',
                    textStyle: {
                        color: '#000',
                    },
                },
	        },{
	        		name:"达成率(%)",
                    type: 'value',
                    // max: 140,
                    splitNumber: 7,
                    splitLine: {
                        show: false,
                        lineStyle: {
                            color: '#0a3256'
                        }
                    },
                    axisLine: {
                        show: true,
                    },
                    axisLabel: {
                        formatter: '{value}%',
                        textStyle: {
                            color: '#000',
                        },
                    },
                    axisTick: {
                        show: true,
                    }
	
	        },
	        ],
	
	        /*toolbox: {        show: true,        feature: {   myDownload: {
                        show: true,
                        title: '数据导出',
                        icon: 'image://http://localhost:8080/smbi/image/excel.svg',
                        onclick: function (){
                            alert('暂不支持');
                        }
                    },         saveAsImage: {}        }    },*/

		series: [{
	            "name": "采购",
	            "type": "bar",
	            "barGap": "10%",
	            "itemStyle": {

	            },
	            "data": [34888,11227,9582,31655,11256],
	        },
	
	            {
	                "name": "指标",
	                "type": "bar",
	                "itemStyle": {

	                },
	                "data": [
							11807,
							11442,
							11246,
							34529,
							5313
	                ]
	            }, {
	                "name": "Ach%",
	                "type": "line",
	                symbolSize:12,
	                symbol:'circle',
	                yAxisIndex: 1,

	                "data": [
						295.47,
						98.13,
						85.21,
						91.68,
						211.84


	                ]
	            },
	            {
	                "name": "增长",
	                "type": "line",
	                symbolSize:12,
	                symbol:'circle',
	                yAxisIndex: 1,
	                "itemStyle": {
	                    "normal": {
	                        "barBorderRadius": 0,
	                    }
	                },
	                "data": [
						95.47,
						98.13,
						65.21,
						91.68,
						81.84


	                ]
	            },
	        ]
	    };
	
	
//**********************3.上右，品牌达成
	var productlineoption = {
        color:["#373e41", "#446d85", "#3d67c7",'#37a2da','#988280'],
	        backgroundColor: "#fff",
	        "title": {
	            "text": "",
	            x: "4%",
	
	            textStyle: {
	                color: '#fff',
	                fontSize: '22'
	            },
	        },
	        "tooltip": {
	            "trigger": "axis",
	            "axisPointer": {
	                "type": "shadow",
	                textStyle: {
	                    color: "#fff"
	                }
	
	            },
	            formatter: function(params, ticket, callback) {

	                var res = params[0].name;
	                console.info(params)
	                for (var i = 0, l = params.length; i < l; i++) {
	                    if (params[i].seriesType === 'line') {
	                        res += '<br/>' + params[i].seriesName + ' : ' + (params[i].value ? params[i].value : '-') + '%';
	                    } else {
	                        res += '<br/>' + params[i].seriesName + ' : ' + (params[i].value ? formatterOneMoney(params[i].value) : '-') + 'k';
	                    }
	                }
	                return res;

	            }
	        },
	        "grid": {
	        	containLabel :true,
	        	top:"28%",
	        	bottom:"8%",
	        	left:"5%",
	        	right:"12%",
	            textStyle: {
	                color: "#222"
	            }
	        },
	        "legend": {
	            textStyle: {
	                color: '#222',
	            },
	            "data": ['采购', '指标', 'Ach%','增长']
	        },
	
	
	        "calculable": true,
	        "xAxis": [{
	            "type": "category",
	            "axisLine": {
	                lineStyle: {
	                    color: '#222'
	                }
	            },
	            "splitLine": {
	                "show": false
	            },
	            "axisTick": {
	                "show": false
	            },
	            "splitArea": {
	                "show": false
	            },
	            "axisLabel": {
	                "interval": 0,
	
	            },
	            "data": ['Straumann','Anthogyr'],
	        }],
	        "yAxis": [{
	        	name:"采购数额(k)",
	            "type": "value",
	            "splitLine": {
	                "show": false
	            },
	            "axisLine": {
	                lineStyle: {
	                    color: '#222'
	                }
	            },
	            "axisTick": {
	                "show": false
	            },
	            axisLabel: {
                    formatter: '{value}k',
                    textStyle: {
                        color: '#000',
                    },
                },
	            "splitArea": {
	                "show": false
	            }
	        },{
	        		name:"达成率(%)",
                    type: 'value',
                    min: 0,
                    // max: 140,
                    splitNumber: 7,
                    splitLine: {
                        show: false,
                        lineStyle: {
                            color: '#0a3256'
                        }
                    },
                    axisLine: {
                        show: true,
                    },
                    axisLabel: {
                        formatter: '{value}%',
                        textStyle: {
                            color: '#000',
                        },
                    },
                    axisTick: {
                        show: true,
                    }
	
	        }],
	
	        /*toolbox: {        show: true,        feature: {  myDownload: {
                        show: true,
                        title: '数据导出',
                        icon: 'image://http://localhost:8080/smbi/image/excel.svg',
                        onclick: function (){
                            alert('暂不支持');
                        }
                    },          saveAsImage: {}        }    },*/

		series: [{
	            "name": "采购",
	            "type": "bar",
	            "barGap": "10%",
	            "itemStyle": {

	            },
	            "data": [
	                   91612,
	                   6979
	            ],
	        },
	
	            {
	                "name": "指标",
	                "type": "bar",
	                "itemStyle": {

	                },
	                "data": [
						67602,
						6730
	                ]
	            }, {
	                "name": "Ach%",
	                "type": "line",
	                symbolSize:12,
	                symbol:'circle',
	                yAxisIndex: 1,
	                "itemStyle": {

	                },
	                "data": [
						135,
						103
	                ]
	            },
	            {
	                "name": "增长",
	                "type": "line",
	                symbolSize:12,
	                symbol:'circle',
	                yAxisIndex: 1,
	                "itemStyle": {

	                },
	                "data": [
						65.21,
						91.68,

	                ]
	            },
	        ]
	    };	

	
//**********************4.下中，经销商排名
	
    var distributorOption = {
        color:["#373e41", "#446d85", "#3d67c7",'#37a2da','#988280'],
    		 tooltip: {
    		        trigger: 'axis',
    		        formatter: function(params, ticket, callback) {

    		            var res = params[0].name;

    		            for (var i = 0, l = params.length; i < l; i++) {
    		                if (params[i].seriesType === 'line') {
    		                    res += '<br/>' + params[i].seriesName + ' : ' + (params[i].value ? params[i].value : '-') + '%';
    		                } else {
    		                    res += '<br/>' + params[i].seriesName + ' : ' + (params[i].value ? formatterOneMoney(params[i].value) : '-') + 'k';
    		                }
    		            }
    		            return res;

    		        }
    		    },
            legend:{
            	show:true,
            	data:['2018','2017','Ach%','增长']
            },
            grid:{
            	left:'12%',
            	bottom:'40%',
	        	top:'1%',
	        	right:'5%',
            },


            xAxis: {
                data: ['上海思创', '杭州昆德', '广东益升', '北京医健通', '江苏美安', '成都众合', '山东瑞康德', '北京麦创', '北京同心', '北京德诺'],
                boundaryGap: true,
                axisLine: { //坐标轴轴线相关设置。数学上的x轴
                    show: true,
                    lineStyle: {
                        color: '#222'
                    },
                },

                axisLabel: { //坐标轴刻度标签的相关设置
                    textStyle: {
                        color: '#222',
                        margin: 15,
                    },
                    rotate:40
                },


            },
            dataZoom:[
                      {
                        type:'slider',
                        show:true,
                      }
                    ],
            yAxis: [{
    		 	name:"",
                left:'10%',
                splitLine: {
                    show: false,
                    lineStyle: {
                        color: '#0a3256'
                    }
                },
                axisLine: {
                    show: true,
                },
                axisLabel: {
                    formatter: '{value}k',
                    textStyle: {
                        color: '#222',
                    },
                },
                axisTick: {
                    show: false,
                },
            },{
                type: 'value',
                // max: 140,
                splitNumber: 7,
                splitLine: {
                    show: false,
                },
                axisLine: {
                    show: true,
                },
                axisLabel: {
                    formatter: '{value}%',
                    textStyle: {
                        color: '#000',
                    },
                },
                axisTick: {
                    show: true,
                }

        }],
           /*toolbox: {        show: true,        feature: {  myDownload: {
                       show: true,
                       title: '数据导出',
                       icon: 'image://http://localhost:8080/smbi/image/excel.svg',
                       onclick: function (){
                           alert('暂不支持');
                       }
                   },          saveAsImage: {}        }    },*/

		series: [{
                type: 'bar',
                name:'2017',

                data: [10150, 8337, 7568, 7377, 5078, 4689, 4486, 4420, 2789, 2507]
            }
            ,
            {
                type: 'bar',
                name:'2018',
                data: [10500, 8337, 7582, 7237, 5507, 4668, 4446, 7420, 1278, 9250]
            },
            {
                type: 'line',
                yAxisIndex:1,
                name:'Ach%',
                itemStyle: {
                    normal: {
                        label: {
                            show: false,
                            position: 'top',
                            formatter: '{c}',
                            rotate:40
                        },
                    }
                },
                data: [105, 87, 82, 37, 57, 48, 46, 70, 78, 90]
            },
            {
                type: 'line',
                name:'增长',
                yAxisIndex:1,
                itemStyle: {
                    normal: {
                        label: {
                            show: false,
                            position: 'top',
                            formatter: '{c}',
                            rotate:40
                        },

                    }
                },
                data: [105, -87, 82, -37, 57, -48, 46, 70, -78, 90]
            }
            ]
        };
    
//**********************5.右下，省份排名
    var provienceOption = {
        color:["#373e41", "#446d85", "#3d67c7",'#37a2da','#988280'],
	        backgroundColor: "#fff",
	        tooltip: {
	            trigger: 'axis',
	            formatter: function(params, ticket, callback) {

	                var res = params[0].name;

	                for (var i = 0, l = params.length; i < l; i++) {
	                    if (params[i].seriesType === 'line') {
	                        res += '<br/>' + params[i].seriesName + ' : ' + (params[i].value ? params[i].value : '-') + '';
	                    } else {
	                        res += '<br/>' + params[i].seriesName + ' : ' + (params[i].value ? formatterOneMoney(params[i].value) : '-') + '';
	                    }
	                }
	                return res;

	            }
	        },
	        "grid": {
	        	bottom:'15%',
	        	top:'15%',
	        	right:'12%',
	            textStyle: {
	                color: "#222"
	            }
	        },
	        "legend": {
	            textStyle: {
	                color: '#222',
	            },
	            "data": ['Abutment', 'Implant', 'A/I']
	        },
	
	
	        "calculable": true,
	        "xAxis": [{
	            "type": "category",
	            "axisLine": {
	                lineStyle: {
	                    color: '#222'
	                }
	            },
	            "splitLine": {
	                "show": false
	            },
	            "axisTick": {
	                "show": false
	            },
	            "splitArea": {
	                "show": false
	            },
	            "axisLabel": {
	                "interval": 0,
	
	            },
	            "data": ['east','south','west','north','northeast'],
	        }],
	        "yAxis": [{
	        	name:'颗数',
	            "type": "value",
	            "splitLine": {
	                "show": false
	            },
	            "axisLine": {
	                lineStyle: {
	                    color: '#222'
	                }
	            },
	            axisLabel: {
                    formatter: '{value}',
                    textStyle: {
                        color: '#000',
                    },
                },
	            "axisTick": {
	                "show":  true
	            },
	            
	            "splitArea": {
	                "show": false
	            }
	        },{
                    type: 'value',
                    // max: 140,
                    splitNumber: 7,
                    splitLine: {
                        show: false,
                        lineStyle: {
                            color: '#0a3256'
                        }
                    },
                    axisLine: {
                        show: true,
                    },
                    axisLabel: {
                        formatter: '{value}',
                        textStyle: {
                            color: '#000',
                        },
                    },
                    axisTick: {
                        show: true,
                    }
	
	        }],
	
	       /* toolbox: {        show: true,        feature: { myDownload: {
                        show: true,
                        title: '数据导出',
                        icon: 'image://http://localhost:8080/smbi/image/excel.svg',
                        onclick: function (){
                            alert('暂不支持');
                        }
                    },           saveAsImage: {}        }    },*/

		series: [{
	            "name": "Abutment",
	            "type": "bar",
	            "barGap": "10%",
	            "itemStyle": {

	            },
	            "data": [
	                0,
	                0,
	                0,
	                0,
	                0
	            ],
	        },
	
	            {
	            	"name": "Implant",
	                "type": "bar",
	                "itemStyle": {

	                },
	                "data": [
	                    0,
	                    0,
	                    0,
	                    0,
	                    0
	                ]
	            }, {
	                "name": "A/I",
	                "type": "line",
	                symbolSize:12,
	                symbol:'circle',
	                yAxisIndex: 1,
	                "itemStyle": {

	                },
	                "data": [
	                    0,
	                    0,
						0,
						0,
						0
	                ]
	            },
	        ]
	    };

var pieclick = false;
var growthdata = [{
		    value: 0,
		    name: 'East'
		},
		{
		    value: 0,
		    name: 'South'
		},
		{
		    value: 0,
		    name: 'West'
		},
		{
		    value: 0,
		    name: 'North'
		},
		{
		    value: 0,
		    name: 'Northeast'
		}];
var purchasedata = [{
    value: 0,
    name: 'East'
},
{
    value: 0,
    name: 'South'
},
{
    value: 0,
    name: 'west'
},
{
    value: 0,
    name: 'North'
},
{
    value: 0,
    name: 'Northeast'
}
];


var growthoption = {
    color:["#373e41", "#446d85", "#3d67c7",'#37a2da','#988280'],
    backgroundColor: "#fff",
    "tooltip": {
        "trigger": "axis",
        "axisPointer": {
            "type": "shadow",
            textStyle: {
                color: "#fff"
            }

        },
        formatter: function(params, ticket, callback) {

            var res = params[0].name;

            for (var i = 0, l = params.length; i < l; i++) {
                if (params[i].seriesType === 'line') {
                    res += '<br/>' + params[i].seriesName + ' : ' + (params[i].value ? params[i].value : '-') + '%';
                } else {
                    res += '<br/>' + params[i].seriesName + ' : ' + (params[i].value ? formatterOneMoney(params[i].value) : '-') + 'k';
                }
            }
            return res;

        }
    },
    "grid": {
        containLabel: true,
        top:"25%",
        bottom:"12%",
        left:"3%",
        right:"2%",
        textStyle: {
            color: "#222"
        }
    },
    "legend": {
        textStyle: {
            color: '#222',
        },
        "data": ['增长额']
    },


    "calculable": true,
    "xAxis": [{
        "type": "category",
        "axisLine": {
            lineStyle: {
                color: '#222'
            }
        },
        "splitLine": {
            "show": false
        },
        "axisTick": {
            "show": false
        },
        "splitArea": {
            "show": false
        },
        "axisLabel": {
            "interval": 0,

        },
        "data": [],
    }],
    "yAxis": [{
        name:"增长额(k)",
        "type": "value",
        "splitLine": {
            "show": false
        },
        "axisLine": {
            lineStyle: {
                color: '#222'
            }
        },
        "axisTick": {
            "show": true
        },
        "axisLabel": {
            "interval": 0,

        },
        "splitArea": {
            "show": false
        },
        axisLabel: {
            formatter: '{value}',
            textStyle: {
                color: '#000',
            },
        },
    }
    ],

    /*toolbox: {        show: true,        feature: {   myDownload: {
                show: true,
                title: '数据导出',
                icon: 'image://http://localhost:8080/smbi/image/excel.svg',
                onclick: function (){
                    alert('暂不支持');
                }
            },         saveAsImage: {}        }    },*/

	series: [{
        "name": "增长额",
        "type": "bar",
        "barGap": "10%",
        "itemStyle": {

        },
        "data": [],
    },

    ]
};

var pieoption = {
	    tooltip: {
	        trigger: 'item',
			//formatter: "{a} <br/>{b} : {c} ({d}%)",
	        formatter: function (params) {
                var data = params.data.value;
                var percent = params.percent;
                var name = params.name;
                return name + ":\n" + formatterOneMoney(data) + "k (" + percent+ "%" +")";
            }
	    },
	    //color: ['#ff9080', '#00bfb7', '#988280', '#ffcc00','#37a2da'],
    	color:["#373e41", "#446d85", "#3d67c7",'#37a2da','#988280'],
	    legend: {
	        data: ['East', 'South', 'west', 'North', 'Northeast'],
			formatter: function (name) {
				return name[0].toUpperCase() + name.substring(1, name.length);
            }
	    },
	   /*toolbox: {        show: true,        feature: {    myDownload: {
                   show: true,
                   title: '数据导出',
                   icon: 'image://http://localhost:8080/smbi/image/excel.svg',
                   onclick: function (){
                       alert('暂不支持');
                   }
               },        saveAsImage: {}        }    },*/

	series: [{
	        type: 'pie',
	        radius : '60%',
	        center:['50%', '60%'],
	        data: purchasedata,
	        itemStyle: {
	            emphasis: {
	                shadowBlur: 10,
	                shadowOffsetX: 0,
	                shadowColor: 'rgba(0, 0, 0, 0.5)'
	            }
	        },
	        itemStyle: {
	            normal: {
	                label: {
	                    show: true,
	                    //	                            position:'inside',
	                    formatter: function (params) {
	                    	var data = params.data.value;
	                    	var percent = params.percent;
							return  formatterOneMoney(data) + "\n" + percent + "%";
                        }
	                }
	            },
	            labelLine: {
	                show: true,
	                length:1,
	                length2:1,
	                smooth:0.5
	            }
	        }
	    }],
	};


var topicMap = [
   {
        id:"saleachieve",
        topicCode: "achieve",
        //fields: "Amount;target;achieve",
        dataname:"getGaugeAchieve",
        type:"total",
        dataType:"sellin",
       k:"target;sumdata",
        elementMap:{
            "series[0];data[0];value":"achieve",
            "#saletarget":"target",
            "#salereal":"sumdata"

        },
    }
    ,
    {
        id:"salehistory",
        topicCode: "achieve",
        //fields: "Amount;target;achieve;ygrowth;region",
        dataname:"getAreaAchieve",
        type:"total",
        dataType:"sellin",
        k:"target;sumdata",
        elementMap:{
            "xAxis[0];data":"region",
            "series[0];data":"sumdata",
            "series[1];data":"target",
            "series[2];data":"achieve",
            "series[3];data":"ygrowth"
        },

    },
    {
        id:"productlineSale",
        topicCode: "achieve",
        //fields: "Amount;target;achieve;ygrowth;brand",
        dataname:"getBrandAchieve",
        type:"total",
        dataType:"sellin",
        k:"target;sumdata",
        elementMap:{
            "xAxis[0];data":"brand",
            "series[0];data":"sumdata",
            "series[1];data":"target",
            "series[2];data":"achieve",
            "series[3];data":"ygrowth"

        },
		orderBy: " order by brand desc"

    },

    {
        id:"purchase",
        topicCode: "Purchase",
        fields:"region;Quantity",
        elementMap:{
            "series[0];data.name":"region",
            "series[0];data.value":"Quantity",

        },
    }
    ,
    {
        id:"provincceRank",
        topicCode: "achieve",
        dataname:"getai",
        dataType:"Purchase",
        aggcode:'DistributorCode;peroid;productCode;region',
        elementMap:{
            "xAxis[0];data":"Region",
            "series[0];data":"Quantity",
            "series[1];data":"preQuantity",
            "series[2];data":"rate",
        },
    },
    /*{
        id:"distributor",
        topicCode: "achieve",
        dataname:"getRegionGrowrthAmount",
        dataType:"Purchase",
        aggcode:'brand;peroid;region',
        elementMap:{
            "series[0];data.name":"region",
            "series[0];data.value":"added",
        },
    }*/


];