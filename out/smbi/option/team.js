//*******************达成仪表板
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
            saveAsImage: {},
        }
    },*/
		series: [{
			name: '总达成',
			type: "gauge",
			startAngle: 180,
			endAngle: 0,
			min: 0,
			max: 150,
			radius: "150%",
			center: ["50%", "80%"],
			axisLine: {
				show: false,
				lineStyle: {
					width: 16,
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
		}]};

//**************散点图******
var scatterOption = {
    color:["#373e41", "#446d85", "#3d67c7",'#37a2da','#988280'],
	    yAxis: {
	        name:"增长率",
			type:"value",
            scale:"true",
            axisLabel: {
	            formatter: '{value} %'
	        },
	    },
	    xAxis: {
	    	name:"Ach%",
            type:"value",
            scale:"true",
	        axisLabel: {
	            formatter: '{value} %'
	        },
	    },
	    tooltip:{
	        show:true,
	        formatter: function(param) {
	                    let data = param.data;
	                    let value = "成员：" + data[3]+ "<br/>" + "增长率:" + data[0]  + "%"
	                    + "<br/>" + "Ach%:" + data[1] + "%"
	                    + "<br/>" + "销售额:" + data[2]
	                    return value;
	                }
	    },
	   /*toolbox: {        show: true,        feature: {  myDownload: {
                   show: true,
                   title: '数据导出',
                   icon: 'image://http://localhost:8080/smbi/image/excel.svg',
                   onclick: function (){
                       alert('暂不支持');
                   }
               },          saveAsImage: {}        }    },*/

	series: [{
	        type: 'scatter',
	        symbol: 'circle', //'circle', 'rect', 'roundRect', 'triangle', 'diamond', 'pin', 'arrow'
	        symbolSize: function(data) {
	            return Math.sqrt(data[2])/3;
	        },
	        itemStyle: {

	        },
	        label: {
	        	normal:{
		              show:true  ,
		              formatter:function(params){
		        		return params.data[3];
		        	},
		            },
	            emphasis: {
	                show: true,
	            }
	        },
	        data:[
	            [123, 52, 9613,'SimonWu'],
	            [140, 58, 16953,'SherryQu'],
	            [98, 124, 3626,'RichardYang'],
	            [102, 75, 2961,'KevinYang'],
	            [131, 75, 5546,'JennyWu'],
	            [166, 75, 11702,'HaleYu'],
	        ]
	    }]
	}

//****************各成员排名

var salerank = {
    color:["#373e41", "#446d85", "#3d67c7",'#37a2da','#988280'],
		"legend": {
            textStyle: {
                color: '#222',
            },
            "data": ['销售', '指标', 'Ach%']
        },
	    tooltip: {
	        trigger: 'axis',
		        formatter: function(params) {
		            var index = params[0].dataIndex;
		        	var value = salesdataset[0][index] + "<br/>指标："+ formatterOneMoney(salesdataset[1][index]) + "k<br/>销售额："+
		        	formatterOneMoney(salesdataset[2][index])
		        	 + "k<br/>Ach%："+ salesdataset[3][index] + "%";
		        	return value;
		        },
	    },
	    xAxis: [{
	        type: 'category',
	        show: true,
	        data:salesdataset[0],
	        axisLine: {
	            show: true,
	        },
	        "axisTick": { //y轴刻度线
	            "show": true
	        },
	        axisLabel: {
	            rotate: 45
	        }
	    }],
	    yAxis: [{
	        name:'销售额(k)',
	        type: 'value',
	        //  position:'top',
	        show: true,
	        axisTick: {
	            show: true
	        },
	        axisLine: {
	            show: true,
	            
	        },
	        splitLine: {
	            show: false
	        },
	        axisLabel: {
	            rotate: 45,
	            formatter:'{value}k',
	            textStyle: {
	                color: '#737373'
	            }
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
                margin: 20,
                formatter: '{value}%',
                textStyle: {
                    color: '#000',
                },
            },
            axisTick: {
                show: true,
            }

    }],
	   /*toolbox: {        show: true,        feature: {     myDownload: {
                   show: true,
                   title: '数据导出',
                   icon: 'image://http://localhost:8080/smbi/image/excel.svg',
                   onclick: function (){
                       alert('暂不支持');
                   }
               },       saveAsImage: {}        }    },*/

	series: [{
            "name": "销售",
            "type": "bar",
            "barGap": "10%",
            "itemStyle": {

            },
            "data":salesdataset[2],
        },

            {
                "name": "指标",
                "type": "bar",
                "itemStyle": {

                },
                "data": salesdataset[1]
            },
		{
                "name": "Ach%",
                "type": "line",
                symbolSize:12,
                symbol:'circle',
                yAxisIndex: 1,
                "itemStyle": {

                },
                "data": salesdataset[3]
            },
	       ]
};

var businessrank = {
    color:["#373e41", "#446d85", "#3d67c7",'#37a2da','#988280'],
		"legend": {
            textStyle: {
                color: '#222',
            },
            "data": ['销售', '指标', 'Ach%']
        }, 
	    tooltip: {
	        trigger: 'axis',
		        formatter: function(params) {
		            var index = params[0].dataIndex;
		        	var value = purchasedataset[0][index] + "<br/>指标："+ formatterOneMoney(purchasedataset[1][index]) + "k<br/>销售额："+
		        	formatterOneMoney(purchasedataset[2][index]);
		        	 + "k<br/>Ach%："+ purchasedataset[3][index] + "%";
		        	return value;
		        },
	    },
	    xAxis: [{
	        type: 'category',
	        show: true,
	        data:purchasedataset[0],
	        axisLine: {
	            show: true,
	        },
	        "axisTick": { //y轴刻度线
	            "show": true
	        },
	        axisLabel: {
	            rotate: 45
	        }
	    }],
	    yAxis: [{
	        name:'销售额(k)',
	        type: 'value',
	        //  position:'top',
	        show: true,
	        axisTick: {
	            show: true
	        },
	        axisLine: {
	            show: true,
	            
	        },
	        splitLine: {
	            show: false
	        },
	        axisLabel: {
	            rotate: 45,
	            formatter:'{value}k',
	            textStyle: {
	                color: '#737373'
	            }
	        }
	    },{
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
                margin: 20,
                formatter: '{value}%',
                textStyle: {
                    color: '#000',
                },
            },
            axisTick: {
                show: true,
            }

    }],
	   /*toolbox: {        show: true,        feature: {   myDownload: {
                   show: true,
                   title: '数据导出',
                   icon: 'image://http://localhost:8080/smbi/image/excel.svg',
                   onclick: function (){
                       alert('暂不支持');
                   }
               },         saveAsImage: {}        }    },*/

	series: [{
            "name": "销售",
            "type": "bar",
            "barGap": "10%",
            "itemStyle": {

            },
            "data":purchasedataset[2],
        },

            {
                "name": "指标",
                "type": "bar",
                "itemStyle": {

                },
                "data": purchasedataset[1]
            }, {
                "name": "Ach%",
                "type": "line",
                symbolSize:12,
                symbol:'circle',
                yAxisIndex: 1,
                "itemStyle": {

                },
                "data": purchasedataset[3]
            },
	       ]
};