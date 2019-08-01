var report = {
	theme: {
		name: "销售达成增长主题",
		chart: ["line", "line", "pie", "pie", "bar", "bar"],
		dimension: [
		    {code: "period", text: "期间", fields: [
				{caption: "年份", field: "year", filterField: "year", type: "select", defaultvalue:[{code:"2018",name:"2018"},{code:"2019",name:"2019"}]},
				{caption: "月份", field: "month", filterField: "month", type: "select", defaultvalue:[{code:"12",name:"12"},{code:"11",name:"11"}]},
		     ]},
			 {code: "hierarchy", text: "架构", fields: [
			    {caption: "RSM", field: "hierarchy1", type: "select", defaultvalue:[{code:"All",name:"All"},{code:"FredZheng",name:"FredZheng"},{code:"EileenFeng",name:"EileenFeng"}]},                    
			    {caption: "主管", field: "hierarchy", type: "select", defaultvalue:[{code:"All",name:"All"},{code:"RichardYang",name:"RichardYang"},{code:"EvenLiu",name:"EvenLiu"}]}                      
			 ]},
			 {code: "area", text: "区域", fields: [
			    {caption: "大区", field: "region", filterField: "regionid", type: "select",defaultvalue:[{code:"All",name:"All"}, {code:"east",name:"east"},{code:"west",name:"west"},{code:"south",name:"south"},{code:"South",name:"South"},{code:"northeast",name:"northeast"},{code:"north",name:"north"}]},
			 ]},
			 {code: "customer", text: "客户", fields: [
  			    {caption: "客户编码", field: "customerCode", filterField: "customerid"},
  			    {caption: "客户名称", field: "customerName", filterField: "customerid"},
  			 ]},
			 {code: "porduct", text: "产品", fields: [
			    {caption: "产品线", field: "brand", filterField: "brandid", type: "select", defaultvalue:[{code:"All",name:"All"},{code:"Straumann",name:"Straumann"},{code:"Anthogyr",name:"Anthogyr"}]},
			    {caption: "产品编码", field: "productCode", filterField: "productid"},
			    {caption: "产品名称", field: "productName", filterField: "productid"},
			    
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
				group: data}
			};
		}
	}
	
	return result;
}

var salesDetailFields = [
    {code: "period", text: "期间", fields: [
 		{caption: "销售日期", field: "saledate"},
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
		    {caption: "客户母公司", field: "family", filterField: "customerid"},
		    {caption: "客户类型", field: "customertype", filterField: "customerid"},
		    {caption: "客户省", field: "province", filterField: "customerid"},
		    {caption: "客户市", field: "city", filterField: "customerid"}
		 ]},
	 {code: "porduct", text: "产品", fields: [
	    {caption: "产品线", field: "brand", filterField: "productid"},
	    {caption: "产品代码", field: "productcode", filterField: "productid"},
	    {caption: "产品名称", field: "productname", filterField: "brandid"},
	    {caption: "产品类型", field: "producttype", filterField: "brandid"},
	    {caption: "产品规格", field: "spec", filterField: "brandid"}
	 ]}
];

var purchaseDetailFields = [
                         {code: "period", text: "期间", fields: [
                      		{caption: "采购日期", field: "purchasedate"},
                          ]},
                     	 {code: "hierarchy", text: "架构", fields: [
                     	    {caption: "架构", field: "hierarchy"}                      
                     	 ]},
                     	 {code: "provided", text: "供应商", fields: [
                     	    {caption: "生产厂家", field: "vendor", filterField: "regionid"},
                     		{caption: "供应商", field: "supplier", filterField: "province"}
                     	 ]},
                     	 {code: "customer", text: "客户", fields: [
                     		    {caption: "客户编码", field: "customerCode", filterField: "customerid"},
                     		    {caption: "客户名称", field: "customerName", filterField: "customerid"},
                     		    {caption: "客户母公司", field: "family", filterField: "customerid"},
                     		    {caption: "客户类型", field: "customertype", filterField: "customerid"},
                     		   {caption: "客户省", field: "province", filterField: "customerid"},
                     		   {caption: "客户市", field: "city", filterField: "customerid"}
                     		 ]},
                     	 {code: "porduct", text: "产品", fields: [
                     	    {caption: "产品线", field: "brand", filterField: "productid"},
                     	    {caption: "产品代码", field: "productcode", filterField: "productid"},
                     	    {caption: "产品名称", field: "productname", filterField: "brandid"},
                     	    {caption: "产品规格", field: "spec", filterField: "brandid"},
                     	 ]}
                     ];

var inventoryDetailFields = [
                         {code: "period", text: "期间", fields: [
                      		{caption: "库存日期", field: "inventory"},
                          ]},
                     	 {code: "hierarchy", text: "架构", fields: [
                     	    {caption: "架构", field: "hierarchy"}                      
                     	 ]},
                     	 {code: "distributor", text: "区域", fields: [
                     	    {caption: "经销商编码", field: "distributorcode", filterField: "regionid"},
                     		{caption: "经销商名称", field: "DistributorName", filterField: "province"},
                     		{caption: "经销商级别", field: "distributortype", filterField: "city"}
                     	 ]},
                     	 {code: "customer", text: "客户", fields: [
                     		    {caption: "客户编码", field: "customerCode", filterField: "customerid"},
                     		    {caption: "客户名称", field: "customerName", filterField: "customerid"},
                     		    {caption: "客户母公司", field: "family", filterField: "customerid"},
                     		    {caption: "客户类型", field: "customertype", filterField: "customerid"},
                     		    {caption: "经销商省", field: "province", filterField: "customerid"},
                     		    {caption: "经销商市", field: "city", filterField: "customerid"}
                     		 ]},
                     	 {code: "porduct", text: "产品", fields: [
                     	    {caption: "产品线", field: "brand", filterField: "productid"},
                     	    {caption: "产品代码", field: "productcode", filterField: "productid"},
                     	    {caption: "产品名称", field: "productname", filterField: "brandid"},
                     	    {caption: "产品类型", field: "producttype", filterField: "brandid"},
                     	    {caption: "产品规格", field: "spec", filterField: "brandid"}
                     	 ]}
                     ];
