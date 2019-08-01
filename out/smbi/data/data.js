var report = {
	theme: {
		name: "销售达成增长主题",
		chart: ["line", "line", "pie", "pie", "bar", "bar"],
		dimension: [
		    {code: "period", text: "期间", fields: [
       		    {caption: "年月", field: "year", filterField: "year"},
    		    {caption: "季度", field: "season", filterField: "season"},
    		    {caption: "月份", field: "month", filterField: "month"},
    		    	                               
		     ]},
			 {code: "hierarchy", text: "架构", fields: [
			    {caption: "架构", field: "hierarchy"}                      
			 ]},
			 {code: "area", text: "区域", fields: [
			    {caption: "大区", field: "region", filterField: "regionid"},
				{caption: "省份", field: "province", filterField: "province"},
				{caption: "城市", field: "city", filterField: "city"}
			 ]},
			 {code: "customer", text: "客户", fields: [
  			    {caption: "客户编码", field: "customerCode", filterField: "customerid"},
  			    {caption: "客户名称", field: "customerName", filterField: "customerid"},
  			 ]},
			 {code: "porduct", text: "产品", fields: [
			    {caption: "产品编码", field: "productCode", filterField: "productid"},
			    {caption: "产品名称", field: "productName", filterField: "productid"},
			    {caption: "品牌", field: "brand", filterField: "brandid"}
			 ]}
		],
		value: [
		    {caption: "销量", field: "qty_actual"},
		    {caption: "指标", field: "qty_target"},
		    {caption: "达成", field: "rate_achieve"},
		    {caption: "环比", field: "rate_priorPeriod"},
		    {caption: "同比", field: "rate_lastYear"}
		]
	},
	operate: ["outputData", "outputChart"],
	name: "销售达成增长",
	chart: "",
	axisY: {
		type: "value",
		fields: ["achieve", "growth_lastYear"],
		editable: true
	},
	axisX: {
		type: "dimension",
		fields: ["region"],
		editable: true
	},
	showDataGrid: true,
	datasource: "",
	data: ""
	
};


function toFieldArray(datas) {
	var result = {}; var idx = 0;
	
	for (var i = 0; i < datas.length; i++) {
		var data = datas[i];
		
		var fields = data.fields;
		for (var j = 0; j < fields.length; j++) {
			var field = fields[j];
			result[idx++] = {option: {
				caption: field.caption,
				field: field.field,
				group: data,
				type: field.type,
				defaultvalue: field.defaultvalue,
				url: field.url,
				must: field.must,
				equal:field.equal,
				}
			};
		}
	}
	
	return result;
}

var salesDetailFields = [
    {code: "period", text: "期间", fields: [
 		{caption: "开始日期", field: "startDate",type:'date',must:true},
 		{caption: "结束日期", field: "endDate",type:'date',must:true},
 		{caption: "销售日期", field: "bizdate",type:'date'},
 		{caption: "月", field: "month",type:'select',
 			defaultvalue:[
 			              {code:"01",name:"1"},{code:"02",name:"2"},{code:"03",name:"3"},
 			              {code:"04",name:"4"},{code:"05",name:"5"},{code:"06",name:"6"},{code:"07",name:"7"},
 			              {code:"08",name:"8"},{code:"09",name:"9"},{code:"10",name:"10"},{code:"11",name:"11"},{code:"12",name:"12",selected:true},
 			              ]
 		},
     ]},
     {code: "company", text: "经销商", fields: [
         {caption: "经销商编码", field: "DistributorCode", },
	    {caption: "经销商名称", field: "distributorname", },
	    {caption: "经销商级别", field: "DistributorLevel",  type:'select', url:'root/data/procedure/getSelectDistributorLevel/getDataSet?filter=1=1'},
 	 ]},
	 {code: "customer", text: "客户", fields: [
		    {caption: "客户编码", field: "customercode", filterField: "customerid"},
		    {caption: "客户名称", field: "customername", filterField: "customerid"},
		    {caption: "客户母公司", field: "Family", filterField: "customerid"},
		    {caption: "客户类型", field: "CustomerType", filterField: "customerid"},
		    {caption: "客户省", field: "CustomerProvince", filterField: "customerid",},
		    {caption: "客户市", field: "CustomerCity", filterField: "customerid",}
		 ]},
	 {code: "porduct", text: "产品", fields: [
	    {caption: "产品线", field: "brand", filterField: "brandid",url:'root/data/procedure/getSelectProductLine/getDataSet?filter=1=1',  type: "select", defaultvalue:[{code:"Straumann",name:"Straumann"},{code:"Anthogyr",name:"Anthogyr"}]},
	    {caption: "产品代码", field: "productcode", filterField: "productid"},
	    {caption: "产品名称", field: "productname", filterField: "brandid"},
	    {caption: "产品类型", field: "producttype", filterField: "brandid"},
	    {caption: "产品规格", field: "specification", filterField: "brandid"},
	    {caption: "批号", field: "lotno", filterField: "lot"},
	    {caption: "单位", field: "unitname", filterField: "unit"},
	    {caption: "生产厂家", field: "manufacturer", filterField: "vendor"},
	    {caption: "原始产品规格", field: "rawspecification", filterField: "rowspec"},
	    {caption: "PRH4", field: "PRH4", filterField: "PRH4",},
	    {caption: "PRH5", field: "PRH5", filterField: "PRH5"},
	 ]}
];

var purchaseDetailFields = [
{code: "period", text: "期间", fields: [
	{caption: "开始日期", field: "startDate",type:'date',must:true},
	{caption: "结束日期", field: "endDate",type:'date',must:true},
	{caption: "采购日期", field: "bizDate",type:'date'},
	{caption: "月", field: "month",type:'select',
		defaultvalue:[
		              {code:"01",name:"1"},{code:"02",name:"2"},{code:"03",name:"3"},
		              {code:"04",name:"4"},{code:"05",name:"5"},{code:"06",name:"6"},{code:"07",name:"7"},
		              {code:"08",name:"8"},{code:"09",name:"9"},{code:"10",name:"10"},{code:"11",name:"11"},{code:"12",name:"12",selected:true},
			              ]
		},
	   ]},
{code: "company", text: "经销商", fields: [
	   {caption: "经销商编码", field: "distributorcode", },
	   {caption: "经销商名称", field: "distributorname", },
	 ]},
{code: "porduct", text: "产品", fields: [
	{caption: "产品线", field: "brand", filterField: "brandid", url:'root/data/procedure/getSelectProductLine/getDataSet?filter=1=1', type: "select", defaultvalue:[{code:"Straumann",name:"Straumann"},{code:"Anthogyr",name:"Anthogyr"}]},
	{caption: "产品代码", field: "productcode", filterField: "productid"},
	{caption: "产品名称", field: "productname", filterField: "brandid"},
	{caption: "产品类型", field: "producttype", filterField: "brandid"},
	{caption: "产品规格", field: "specification", filterField: "brandid"},
	{caption: "批号", field: "lotno", filterField: "lot"},
	{caption: "单位", field: "unitname", filterField: "unit"},
	{caption: "供应商", field: "vendorname", filterField: "unit"},
	{caption: "生产厂家", field: "manufacturer", filterField: "vendor"},
	{caption: "PRH4", field: "PRH4", filterField: "PRH4" },
	{caption: "PRH5", field: "PRH5", filterField: "PRH5"},
                     	 ]}
                     ];

var inventoryDetailFields = [
{code: "period", text: "期间", fields: [
              	{caption: "库存日期", field: "endDate",type:'date',must:true},
              	{caption: "月", field: "month",type:'select',
              		defaultvalue:[
              		            {code:"01",name:"1"},{code:"02",name:"2"},{code:"03",name:"3"},
              		            {code:"04",name:"4"},{code:"05",name:"5"},{code:"06",name:"6"},{code:"07",name:"7"},
              		            {code:"08",name:"8"},{code:"09",name:"9"},{code:"10",name:"10"},{code:"11",name:"11"},{code:"12",name:"12",selected:true},
              		            ]
              		},
              	   ]},
              	   {code: "company", text: "经销商", fields: [
              	   {caption: "经销商编码", field: "distributorCode", },
              	   {caption: "经销商名称", field: "distributorname", },
              	   {caption: "经销商级别", field: "distributorlevel",type:'select',url: "root/data/procedure/getSelectDistributorLevel/getDataSet?filter=1=1" },
              	   {caption: "经销商省", field: "distributorprovince", filterField: "customerid", },
              	   {caption: "经销商市", field: "distributorcity", filterField: "customerid", },
              	 ]},
              {code: "porduct", text: "产品", fields: [
              	{caption: "产品线", field: "brand", filterField: "brandid",url: "root/data/procedure/getSelectProductLine/getDataSet?filter=1=1", type: "select", defaultvalue:[{code:"Straumann",name:"Straumann"},{code:"Anthogyr",name:"Anthogyr"}]},
              	{caption: "产品代码", field: "productcode", filterField: "productid"},
              	{caption: "产品名称", field: "productname", filterField: "brandid"},
              	{caption: "产品类型", field: "producttype", filterField: "brandid"},
              	{caption: "原始产品规格", field: "rawspecification", filterField: "brandid"},
              	{caption: "批号", field: "lotno", filterField: "lot"},
              	{caption: "单位", field: "unitname", filterField: "unit"},
              	{caption: "生产厂家", field: "manufacturer", filterField: "vendor"},
              	{caption: "PRH4", field: "PRH4", filterField: "PRH4",type:'select', url: "root/data/procedure/getSelectPRH4/getDataSet?filter=1=1"},
              	{caption: "PRH5", field: "PRH5", filterField: "PRH5"},
                     	 ]}
                     ];

var totalSalesFields = [
                         {code: "period", text: "期间", fields: [
							{caption: "年份", field: "year", filterField: "year", type: "select", defaultvalue:[{code:"2018",name:"2018"},{code:"2019",name:"2019"}]},
							{caption: "月份", field: "month", filterField: "month", type: "select", defaultvalue:[{code:"12",name:"12"},{code:"11",name:"11"}]},
                          ]},
                     	 {code: "hierarchy", text: "架构", fields: [
                     	    {caption: "区域经理", field: "hierarchy1", type: "select", defaultvalue:[{code:"FredZheng",name:"FredZheng"},{code:"EileenFeng",name:"EileenFeng"}]},                    
                     	    {caption: "主管", field: "hierarchy1", type: "select", defaultvalue:[{code:"RichardYang",name:"RichardYang"},{code:"EvenLiu",name:"EvenLiu"}]},                      
                     	    {caption: "负责销售", field: "hierarchy", type: "select", defaultvalue:[{code:"CocoMao",name:"CocoMao"},{code:"MessiMei",name:"MessiMei"}]}                      
                     	 ]},
                     	 {code: "area", text: "区域", fields: [
                     	    {caption: "大区", field: "region", filterField: "regionid", type: "select", defaultvalue:[ {code:"east",name:"east"},{code:"west",name:"west"},{code:"south",name:"south"},{code:"southeast",name:"southeast"},{code:"northeast",name:"northeast"},{code:"north",name:"north"}]},
                     		{caption: "省份", field: "province", filterField: "province", type: "select", defaultvalue:[{code:"北京市",name:"北京市"},{code:"上海市",name:"上海市"}]},
                     		{caption: "城市", field: "city", filterField: "city", type: "select", defaultvalue:[{code:"北京市",name:"北京市"},{code:"上海市",name:"上海市"}]}
                     	 ]},
                     	 {code: "customer", text: "客户", fields: [
                     		    {caption: "终端编码", field: "customerCode", filterField: "customerid"},
                     		    {caption: "终端名称", field: "customerName", filterField: "customerid"},
                     		    {caption: "客户母公司", field: "family", filterField: "customerid"},
                     		    {caption: "终端类型", field: "customertype", filterField: "customerid"},
                     		    {caption: "诊所授牌级别 ", field: "customertype", filterField: "customerid"},
                     		 ]},
                     	 {code: "distributor", text: "经销商", fields: [
                		    {caption: "经销商编码", field: "distributorCode", filterField: "customerid"},
                		    {caption: "经销商名称", field: "distributorName", filterField: "customerid"},
                		    {caption: "经销商级别", field: "distributorType", filterField: "customerid"},
                		 ]},
                     	 {code: "porduct", text: "产品", fields: [
                     	    {caption: "产品线", field: "brand", filterField: "brandid", type: "select", defaultvalue:[{code:"Straumann",name:"Straumann"},{code:"Anthogyr",name:"Anthogyr"}]},
                     	    {caption: "产品代码", field: "productcode", filterField: "productid"},
                     	    {caption: "产品名称", field: "productname", filterField: "brandid"},
                     	    {caption: "产品类型", field: "producttype", filterField: "brandid"},
                     	    {caption: "产品数量", field: "spec", filterField: "brandid"},
                     	    {caption: "单位", field: "unit", filterField: "brandid"}
                     	 ]}
                     ];

var comparisonTotalFields = [
                        {code: "period", text: "期间", fields: [
							{caption: "年份", field: "year", filterField: "year", type: "select", defaultvalue:[{code:"2018",name:"2018"},{code:"2019",name:"2019"}]},
							{caption: "月份", field: "month", filterField: "month", type: "select", defaultvalue:[{code:"12",name:"12"},{code:"11",name:"11"}]},
                         ]},
                    	 {code: "hierarchy", text: "架构", fields: [
                              {caption: "大区经理", field: "hierarchy1", type: "select", defaultvalue:[{code:"FredZheng",name:"FredZheng"},{code:"EileenFeng",name:"EileenFeng"}]},                    
                              {caption: "主管", field: "hierarchy1", type: "select", defaultvalue:[{code:"RichardYang",name:"RichardYang"},{code:"EvenLiu",name:"EvenLiu"}]},                      
                     
                    	 ]},
                    	 {code: "area", text: "区域", fields: [
							{caption: "大区", field: "region", filterField: "regionid", type: "select", defaultvalue:[ {code:"east",name:"east"},{code:"west",name:"west"},{code:"south",name:"south"},{code:"southeast",name:"southeast"},{code:"northeast",name:"northeast"},{code:"north",name:"north"}]},
							{caption: "负责区域", field: "region", filterField: "regionid",  defaultvalue:[ {code:"east",name:"east"},{code:"west",name:"west"},{code:"south",name:"south"},{code:"southeast",name:"southeast"},{code:"northeast",name:"northeast"},{code:"north",name:"north"}]},
						 ]},
                    	 {code: "porduct", text: "产品", fields: [
                    	    {caption: "产品线", field: "brand", filterField: "brandid", type: "select", defaultvalue:[{code:"Straumann",name:"Straumann"},{code:"Anthogyr",name:"Anthogyr"}]},
                    	 ]},
						 {code: "distributor", text: "经销商", fields: [
						                                             {caption: "经销商代码", field: "distributorcode", filterField: "productid"},
						                                     	    {caption: "经销商名称", field: "distributorname", filterField: "productid"},
                      ]}
                    ];

var comparisondetailFields = [
                             {code: "period", text: "期间", fields: [
     							{caption: "年份", field: "year", filterField: "year", type: "select", defaultvalue:[{code:"2018",name:"2018"},{code:"2019",name:"2019"}]},
     							{caption: "月份", field: "month", filterField: "month", type: "select", defaultvalue:[{code:"12",name:"12"},{code:"11",name:"11"}]},
                              ]},
                         	 {code: "hierarchy", text: "架构", fields: [
                                   {caption: "大区经理", field: "hierarchy1", type: "select", defaultvalue:[{code:"FredZheng",name:"FredZheng"},{code:"EileenFeng",name:"EileenFeng"}]},                    
                                   {caption: "主管", field: "hierarchy1", type: "select", defaultvalue:[{code:"RichardYang",name:"RichardYang"},{code:"EvenLiu",name:"EvenLiu"}]},                      
                          
                         	 ]},
                         	 {code: "area", text: "区域", fields: [
     							{caption: "大区", field: "region", filterField: "regionid", type: "select", defaultvalue:[ {code:"east",name:"east"},{code:"west",name:"west"},{code:"south",name:"south"},{code:"southeast",name:"southeast"},{code:"northeast",name:"northeast"},{code:"north",name:"north"}]},
     							{caption: "负责区域", field: "region", filterField: "regionid",  defaultvalue:[ {code:"east",name:"east"},{code:"west",name:"west"},{code:"south",name:"south"},{code:"southeast",name:"southeast"},{code:"northeast",name:"northeast"},{code:"north",name:"north"}]},
     							{caption: "省", field: "region", filterField: "regionid",  defaultvalue:[ {code:"east",name:"east"},{code:"west",name:"west"},{code:"south",name:"south"},{code:"southeast",name:"southeast"},{code:"northeast",name:"northeast"},{code:"north",name:"north"}]},
     							{caption: "市", field: "region", filterField: "regionid",  defaultvalue:[ {code:"east",name:"east"},{code:"west",name:"west"},{code:"south",name:"south"},{code:"southeast",name:"southeast"},{code:"northeast",name:"northeast"},{code:"north",name:"north"}]},
     						 ]},
                         	 {code: "porduct", text: "产品", fields: [
                         	    {caption: "产品线", field: "brand", filterField: "brandid", type: "select", defaultvalue:[{code:"Straumann",name:"Straumann"},{code:"Anthogyr",name:"Anthogyr"}]},
                         	    {caption: "产品代码", field: "productcode", filterField: "productid"},
                        	    {caption: "产品名称", field: "productname", filterField: "brandid"},
                        	    {caption: "产品类型", field: "producttype", filterField: "brandid"},
                        	    {caption: "产品数量", field: "spec", filterField: "brandid"},
                              	{caption: "PRH4", field: "PRH4", filterField: "PRH4"},
                              	{caption: "PRH4Description", field: "PRH4", filterField: "PRH4"},
                              	{caption: "PRH5", field: "PRH5", filterField: "PRH5"},
                         	 ]},
     						 {code: "distributor", text: "经销商", fields: [
     						                                             {caption: "经销商代码", field: "distributorcode", filterField: "productid"},
     						                                     	    {caption: "经销商名称", field: "distributorname", filterField: "productid"},
                           ]}
                         ];

var inventorySumFields = [
                        {code: "period", text: "期间", fields: [
                     		{caption: "时间", field: "endtime",type:'date'}
                         ]},
                    	 {code: "area", text: "区域", fields: [
                    	    {caption: "大区", field: "region", filterField: "regionid", type: "select", defaultvalue:[ {code:"east",name:"east"},{code:"west",name:"west"},{code:"south",name:"south"},{code:"southeast",name:"southeast"},{code:"northeast",name:"northeast"},{code:"north",name:"north"}]},
                    	 ]},
                    	 {code: "distributor", text: "经销商", fields: [
                    		    {caption: "经销商编码", field: "distributorcode", filterField: "customerid"},
                    		    {caption: "经销商名称", field: "distributorname", filterField: "customerid"},
                    		    {caption: "经销商级别", field: "distributorlevel", filterField: "customerid", type: "select", defaultvalue:[{code:"一级商",name:"一级商"},{code:"二级商",name:"二级商"}]}
                    	 ]},
                    	 {code: "porduct", text: "产品", fields: [
                    	    {caption: "产品线", field: "brand", filterField: "brandid", type: "select", defaultvalue:[{code:"Straumann",name:"Straumann"},{code:"Anthogyr",name:"Anthogyr"}]},
                    	 ]},
                    ];

var distributorAccountFields = [
                        {code: "period", text: "期间",
							fields: [
                          {caption: "年份", field: "year", must:true, filterField: "year", type: "select", defaultvalue:[{code:"2017",name:"2017"},{code:"2018",name:"2018"},{code:"2019",name:"2019"}]},
                         ]},
                    	 {code: "area", text: "区域", fields: [
							{caption: "大区", field: "region", filterField: "region", type: "select", defaultvalue:[ {code:"east",name:"east"},{code:"west",name:"west"},{code:"south",name:"south"},{code:"southeast",name:"southeast"},{code:"northeast",name:"northeast"},{code:"north",name:"north"}]},
						 ]},
                    	 {code: "distributor", text: "经销商", fields: [
                    	    {caption: "经销商代码", field: "distributorcode", filterField: "productid"},
                    	    {caption: "经销商名称", field: "distributorname", filterField: "productid"},
                    	    {caption: "经销商级别", field: "distributorlevel", filterField: "brandid", type: "select", defaultvalue:[{code:"一级商",name:"一级商"},{code:"二级商",name:"二级商"}]}
                    	 ]}
                    ];

var totalTerminalFields = [
                        {code: "period", text: "期间", fields: [
							{caption: "年份", field: "year", filterField: "year", type: "select", must: true, defaultvalue:[{code:"2017",name:"2017"},{code:"2018",name:"2018"},{code:"2019",name:"2019"}]},
							{caption: "月份", field: "month", filterField: "month", type: "select", must: true, defaultvalue:[{code:"1",name:"1"},{code:"2",name:"2"},{code:"3",name:"3"},
                                    {code:"4",name:"4"},{code:"5",name:"5"},{code:"6",name:"6"},{code:"7",name:"7"},
                                    {code:"8",name:"8"},{code:"9",name:"9"},{code:"10",name:"10"},{code:"11",name:"11"},{code:"12",name:"12",selected:true},]},
                         ]},
                    	 {code: "hierarchy", text: "架构", fields: [
                                 {caption: "大区经理", field: "RSM", filterField: "name",showField: "RSMname",type:'select',defaultadded:true, url:"root/bi/getPosition?userType=RSM"},
                                 {caption: "主管", field: "Supervisor", filterField: "name",showField: "Supervisorname", type:'select', parentField:"RSM", url:"root/bi/getPosition?userType=Supervisor"},
                                 {caption: "销售", field: "Salesperson",filterField: "name", showField: "Salespersonname", type:'select', parentField:"Supervisor", url:"root/bi/getPosition?userType=Salesperson"}
                             ]},
                    	 {code: "area", text: "区域", fields: [
                    	   {caption: "大区", field: "region", filterField: "region", type: "select", defaultvalue:[ {code:"east",name:"east"},{code:"west",name:"west"},{code:"south",name:"south"},{code:"southeast",name:"southeast"},{code:"northeast",name:"northeast"},{code:"north",name:"north"}]},
                    	 ]},
                    	 {code: "porduct", text: "产品", fields: [
                    	    {caption: "产品线", field: "brand", filterField: "brand", type: "select", defaultvalue:[{code:"Straumann",name:"Straumann"},{code:"Anthogyr",name:"Anthogyr"}]},
                    	 ]},
                    	 {code: "customer", text: "客户", fields: [
                    		    {caption: "终端代码", field: "companycode", filterField: "companycode"},
                    		    {caption: "终端名称", field: "conpanyname", filterField: "conpanyname"}
                    		 ]}
                    ];