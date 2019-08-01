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