
//**********************1.最左面，年度总达成
	var middleOption = {
        backgroundColor:"#fff",
		tooltip : {
			formatter: "{a} <br/>{b} = {c}%"
		},
        /*toolbox: {
            show: true,
            feature: {myDownload: {
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
	                    	if(params[i].seriesName === 'Ach%') {
								res += '<br/>' + params[i].seriesName + ' : ' + (params[i].value ? (params[i].value * 100)  : '-') + '%';
							} else{
								res += '<br/>' + params[i].seriesName + ' : ' + (params[i].value ? params[i].value  : '-') + '%';
							}
	                    } else {
	                        res += '<br/>' + params[i].seriesName + ' : ' + (params[i].value ? formatterOneMoney(params[i].value) : '-') + 'k';
	                    }
	                }
	                return res;

	            }
	        },
	        "grid": {
	        	bottom:'15%',
	        	top:'15%',
	        	right:'12%',
				left:'20%',
	            textStyle: {
	                color: "#222"
	            }
	        },
	        "legend": {
	            textStyle: {
	                color: '#222',
	            },
	            "data": ['销售', '指标', 'Ach%','增长']
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
	        	name:"销售额(k)",
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
	                "show":  true
	            },
	            "axisLabel": {
	                "interval": 0,
	
	            },
	            "splitArea": {
	                "show": false
	            },
	            axisLabel: {
                    margin: 20,
                    formatter: '{value}',
                    textStyle: {
                        color: '#000',
                    },
                },
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
	
	        /*toolbox: {
						show: true,
						feature: {   myDownload: {
								show: true,
								title: '数据导出',
								icon: 'image://http://localhost:8080/smbi/image/excel.svg',
								onclick: function (){
									alert('暂不支持');
								}
							},         saveAsImage: {}
						}
                    },*/
		    series: [{
	            "name": "销售",
	            "type": "bar",
	            "barGap": "10%",
	            "itemStyle": {

	            },
	            "data": [
	                30677,
	                19696,
	                12798,
	                24275,
	                4278
	            ],
	        },
	
	            {
	                "name": "指标",
	                "type": "bar",
	                "itemStyle": {

	                },
	                "data": [
	                    31022,
	                    10832,
	                    8833,
	                    9175,
	                    5656
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
	                    98.89,
	                    181.83,
						144.90,
						264.57,
						75.65
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
        //color:["#373e41", "#446d85", "#3d67c7",'#37a2da','#988280'],
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
	                console.info(params)
	                for (var i = 0, l = params.length; i < l; i++) {
	                    if (params[i].seriesType === 'line') {
	                    	if (params[i].seriesName === 'Ach%') {
								res += '<br/>' + params[i].seriesName + ' : ' + (params[i].value ? (params[i].value*100) : '-') + '%';
							} else{
								res += '<br/>' + params[i].seriesName + ' : ' + (params[i].value ?  params[i].value : '-') + '%';
							}

	                    } else {
	                        res += '<br/>' + params[i].seriesName + ' : ' + (params[i].value ? formatterOneMoney(params[i].value) : '-') + 'k';
	                    }
	                }
	                return res;

	            }
	        },
	        "grid": {
	        	containLabel :true,
	        	left:'3%',
	        	bottom:'5%',
	        	top:'28%',
	            textStyle: {
	                color: "#222"
	            }
	        },
	        "legend": {
	            textStyle: {
	                color: '#222',
	            },
	            "data": ['销售', '指标', 'Ach%','增长']
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
	        	 name:"销售额(k)",
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
	            },
	            "splitArea": {
	                "show": false
	            },
	            axisLabel: {
                    formatter: '{value}k',
                    textStyle: {
                        color: '#000',
                    },
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
	            "name": "销售",
	            "type": "bar",
	            "barGap": "10%",
	            "itemStyle": {

	            },
	            "data": [
	                   84505,
	                   7222
	            ],
	        },
	
	            {
	                "name": "指标",
	                "type": "bar",
	                "itemStyle": {

	                },
	                "data": [
						59835,
						5813
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
						141,
						124
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
						41,
						14
	                ]
	            },
	        ]
	    };	

//**********************4.下左，经销商排名	
    var distributorOption = {
        color:["#373e41", "#446d85", "#3d67c7",'#37a2da','#988280'],
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
            legend:{
            	show:true,
            	data:['公立','LYTD公立','民营', 'LYTD民营'],

            },
            grid:{
            	left:'22%',
            	bottom:'20%',
	        	top:'15%',
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
            yAxis: {
            	 name:"销售额(k)",
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
                    formatter: '{value}',
                    textStyle: {
                        color: '#222',
                    },
                },
                axisTick: {
                    show: false,
                },
            },
           toolbox: {        show: true,        feature: {    myDownload: {
                       show: true,
                       title: '数据导出',
                       icon: 'image://http://localhost:8080/smbi/image/excel.svg',
                       onclick: function (){
                           alert('暂不支持');
                       }
                   },        saveAsImage: {}        }    },series: [{
                type: 'bar',
                name:'民营',
                barGap: "10%",
                itemStyle: {
                    normal: {

                        barBorderRadius: [10,10,0,0],

                    }
                },
                data: [10150, 8337, 7568, 7377, 5078, 4689, 4486, 4420, 2789, 2507]
            }
            ,
            {
                type: 'bar',
                name:'LYTD民营',
                barGap: "10%",
                itemStyle: {
                    normal: {
                    	 barBorderRadius: [10,10,0,0],

                    }
                },
                data: [1050, 837, 758, 737, 507, 468, 446, 420, 278, 250]
            }
		]
        };
	
    
//**********************5.右下，a/i排名
    var provienceOption = {
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
	                console.info(params)
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
				left:'15%',
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
	        	 name:"颗数",
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
	                "show":  true
	            },
	            "axisLabel": {
	                "interval": 0,
	                formatter:"{value}",
	            },
	            "splitArea": {
	                "show": false
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
                        formatter: '{value}',
                        textStyle: {
                            color: '#000',
                        },
                    },
                    axisTick: {
                        show: true,
                    }
	
	        }],
	
	       /* toolbox: {
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
	            "name": "Abutment",
	            "type": "bar",
	            "barGap": "10%",
	            "itemStyle": {

	            },
	            "data": [
	                30677,
	                19696,
	                12798,
	                24275,
	                4278
	            ],
	        },
	
	            {
	                "name": "Implant",
	                "type": "bar",
	                "itemStyle": {

	                },
	                "data": [
	                    31022,
	                    10832,
	                    8833,
	                    9175,
	                    5656
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
	                    0.89,
	                    1.83,
						1.90,
						2.57,
						0.76
	                ]
	            },
	        ]
	    };
	
	//---------------------------------------
    
   var dsoOption = {
       color:["#373e41", "#446d85", "#3d67c7",'#37a2da','#988280'],
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
	                        res += '<br/>' + params[i].seriesName + ' : ' + (params[i].value ? params[i].value : '') + '%';
	                    } else {
	                        res += '<br/>' + params[i].seriesName + ' : ' + (params[i].value ? formatterOneMoney(params[i].value) : '') + 'k';
	                    }
	                }
	                return res;

	            }
	        },
    grid:{
    	left:'24%',
    	bottom:'20%',
    	top:'15%',
    	right:'5%',
    },
    dataZoom:[
              {
                type:'slider',
                show:true,
              }
            ],
    xAxis: {
        data: ['324医院', '401医院', '88医院', '89医院', '阿霞牙医诊所', '艾菲尔口腔', '艾柯口腔', '爱思特', '爱心口腔', '安柏口腔'],
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
       legend:{
		   	show:true,
		   data:['YTD','LYTD']
	   },
    yAxis: {
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
            formatter: '{value}',
            textStyle: {
                color: '#222',
            },
        },
        axisTick: {
            show: false,
        },
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
		   	name:'YTD',
        type: 'bar',
        barGap: "10%",
        itemStyle: {
            normal: {

                barBorderRadius: [10,10,0,0],

            }
        },
        data: [10150, 8337, 7568, 7377, 5078, 4689, 4486, 4420, 2789, 2507]
    },
           {
               name:'LYTD',
               type: 'bar',
               barGap: "10%",
               itemStyle: {
                   normal: {

                       barBorderRadius: [10,10,0,0],

                   }
               },
               data: [10150, 8337, 7568, 7377, 5078, 4689, 4486, 4420, 2789, 2507]
           }
    ]};


var topicMap = [

    {
        id:"saleachieve",
        topicCode: "achieve",
        //fields: "",
        dataname:"getGaugeAchieve",
        dataType:'sellout',
        k:"target;sumdata",

        elementMap:{
            "series[0];data[0];value":"achieve",
            "#saletarget":"target",
            "#salereal":"sumdata",
			"*saletarget":"Quantity",
			"*salereal":"preQuantity",

        },
    }
    ,
    {

        id:"salehistory",
        topicCode: "achieve",
        //fields: "",
        dataname:"getAreaAchieve",
        dataType:'sellout',
        k:"target;sumdata",
        elementMap:{
            "xAxis[0];data":"region",
            "series[0];data":"sumdata",
            "series[1];data":"target",
            "series[2];data":"achieve",
            "series[3];data":"growth"
        },
    }
    ,{
        id:"productlineSale",
        topicCode: "achieve",
        //fields: "",
        dataname:"getBrandAchieve",
        dataType:'sellout',
        k:"target;sumdata",
        elementMap:{
            "xAxis[0];data":"brand",
            "series[0];data":"sumdata",
            "series[1];data":"target",
            "series[2];data":"achieve",
        },
		orderBy: " order by brand desc"

    },
    {
        id:"distributorRank",
        topicCode: "achieve",
        dataname:"getCompanySum",
        aggcode:'brand;CompanyCode;peroid',
        k:"Amount;preAmount",
        elementMap:{
            "xAxis[0];data":"Family",
            "series[0];data":"Amount",
            "series[1];data":"preAmount",

        },
        filter: " and b.CompanyType = '民营'"

    }
    ,{
        id:"dsosale",
        topicCode: "achieve",
        dataname:"getDsoAmount",
		aggcode:'brand;CompanyCode;peroid',
        k:"Amount;preAmount",
        elementMap:{
            "xAxis[0];data":"terminalname",
            "series[0];data":"Amount",
            "series[1];data":"preAmount",

        },
    },
	{
        id:"provincceRank",
        topicCode: "achieve",
        dataname:"getai",
        dataType:"Sale",
		q:"Quantity;preQuantity",
		aggcode:'DistributorCode;peroid;productCode;region',
        elementMap:{
            "xAxis[0];data":"Region",
            "series[0];data":"Quantity",
            "series[1];data":"preQuantity",
            "series[2];data":"rate"
        },
    }


];