
//**********************1.最左面，年度总达成
	var middleOption = {
        backgroundColor:"#fff",
		tooltip : {
			formatter: "{a} <br/>{b} = {c}%"
		},

		toolbox: {
            show: true,
            /*feature: {
            	/!*myDownload: {
                    show: true,
                    title: '数据导出',
                    icon: 'image://http://localhost:8080/smbi/image/excel.svg',
                    onclick: function (){
                        alert('暂不支持');
                    }
                },*!/
                saveAsImage: {
                	title:"保存图片",


                }
            }*/
        },

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
					width: 20,
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
	        tooltip: {
	            trigger: 'axis',
	            formatter: function(params, ticket, callback) {

	                var res = params[0].name;

	                for (var i = 0, l = params.length; i < l; i++) {
	                    if (params[i].seriesType === 'line') {
	                    	if (params[i].seriesName === 'Ach%'){
								res += '<br/>' + params[i].seriesName + ' : ' + (params[i].value*100 ? params[i].value : '-') + '%';
							}else {
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
	        	left:"8%",
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
	        	name:"大区",
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
	                "show": true
	            },
	            "axisLabel": {
	                "interval": 0,
	
	            },
	            "splitArea": {
	                "show": false
	            },
	            axisLabel: {
                    margin: 20,
                    formatter: '{value}k',
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
	
	        /*toolbox: {
        	show: true,
				feature: {
					/!*myDownload: {
                        show: true,
                        title: '数据导出',
                        icon: 'image://http://localhost:8080/smbi/image/excel.svg',
                        onclick: function (){
                            alert('暂不支持');
                        }
                    },*!/
					saveAsImage: {}
				}
        	},*/

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
	                "itemStyle": {

	                },
	                "data": [
						295.47,
						98.13,
						85.21,
						91.68,
						211.84


	                ]
	            },{
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
			 tooltip: {
			        trigger: 'axis',
			        formatter: function(params, ticket, callback) {

			            var res = params[0].name;

			            for (var i = 0, l = params.length; i < l; i++) {
			                if (params[i].seriesType === 'line') {
			                	if (params[i].seriesName === 'Ach%') {
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
	        backgroundColor: "#fff",
	        "title": {
	            "text": "",
	            x: "4%",
	
	            textStyle: {
	                color: '#fff',
	                fontSize: '22'
	            },
	        },
	        "grid": {
	        	containLabel :true,
	        	top:"24%",
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
	        	name:"产品线",
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
	            "axisLabel": {
	                "interval": 0,
	                formatter:'{value}k',
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
	
	        /*toolbox: {
        	show: true,
				feature: {
/!*					myDownload: {
                        show: true,
                        title: '数据导出',
                        icon: 'image://http://localhost:8080/smbi/image/excel.svg',
                        onclick: function (){
                            alert('暂不支持');
                        }
                    },*!/
					saveAsImage: {}
				}
        	},*/

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
	            },{
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
						65.21,
						81.84
	                ]
	            },
	        ]
	    };	

	
//**********************4.下中，经销商排名
	
    var distributorOption = {
        //color: ['#ff9080', '#00bfb7', '#988280', '#ffcc00','#37a2da'],
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
            	data:['LYTD','YTD','指标','Ach%','增长']
            },
            grid:{
            	left:'12%',
            	bottom:'20%',
	        	top:'15%',
	        	right:'5%',
            },
            
            xAxis: {
                data: [],
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
                    rotate:40,
					formatter: function (data) {
						return (data.length > 8 ? (data.slice(0,7)+"...") : data );
					},
                },


            },
            dataZoom:[
                      {
                        type:'slider',
                        show:true,
                      }
                    ],
            yAxis: [{
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
           /*toolbox: {
			   show: true,
			   feature: {
/!*				   myDownload: {
                       show: true,
                       title: '数据导出',
                       icon: 'image://http://localhost:8080/smbi/image/excel.svg',
                       onclick: function (){
                           alert('暂不支持');
                       }
                   },*!/
				   saveAsImage: {}
			   }
		   },*/

		series: [{
                type: 'bar',
                name:'LYTD',

                data: []
            }
            ,
            {
                type: 'bar',
                name:'YTD',

                data: []
            },
            {
                type: 'bar',
                name:'指标',

                data: []
            },
            {
                type: 'line',
                name:'Ach%',
                yAxisIndex: 1,
                data: []
            },
            {
                type: 'line',
                name:'增长',
                yAxisIndex: 1,
                data: []
            }

            ]
        };




var topicMap = [
    {
        id:"saleachieve",
        topicCode: "achieve",
        fields: "",
        dataname:"getDistributorGaugeAchieve",
        type:"total",
        dataType:"Distributor",
		k:"target;sumdata",
        elementMap:{
            "series[0];data[0];value":"achieve",
            "#saletarget":"target",
            "#salereal":"sumdata"

        },
        filterCode:"ytd"
    }
    ,
    {
        id:"salehistory",
        topicCode: "achieve",
        fields: "",
        dataname:"getDistributorAreaAchieve",
        type:"total",
        dataType:"Distributor",
        k:"target;sumdata",
        elementMap:{
            "xAxis[0];data":"region",
            "series[0];data":"sumdata",
            "series[1];data":"target",
            "series[2];data":"achieve",
            "series[3];data":"growth"
        },

    },
    {
        id:"productlineSale",
        topicCode: "achieve",
        fields: "",
        dataname:"getDistributorBrandAchieve",
        type:"total",
        dataType:"Distributor",
        k:"target;sumdata",
        elementMap:{
            "xAxis[0];data":"brand",
            "series[0];data":"sumdata",
            "series[1];data":"target",
            "series[2];data":"achieve",
            "series[3];data":"growth"
        },
		//orderBy: " order by brand desc"

    },
    {
        id:"distributorRank",
        topicCode: "achieve",
        //fields: "",
        dataname:"getDistributorAchieve",
        type:"total",
        dataType:"Distributor",
        k:"presumdata;sumdata;target",
        elementMap:{
            "xAxis[0];data":"DistributorName",
            "series[0];data":"presumdata",
            "series[1];data":"sumdata",
            "series[2];data":"target",
            "series[3];data":"achieve",
            "series[4];data":"growth"
        },


    }

];