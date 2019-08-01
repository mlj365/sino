var menutree = {
    "总经理": [
        {name: "home_gm", text: "首页", img: "../../image/home.png", url: "home_gm.html"},
        {name: "biReport", text: "BI", img: "../../image/file.png", children: [
	        {name: "saleOverview", text: "SellOut达成", img: "../../image/report.png", url: "../bi/sellout_gm.html"},
	        {name: "bizOverview", text: "经销商采购达成", img: "../../image/report.png", url: "../bi/distributorsale_gm.html"},
	        {name: "bizOverview", text: "SellIn达成", img: "../../image/report.png", url: "../bi/sellin_gm.html"},
	        {name: "channelOverview", text: "经销商管理", img: "../../image/report.png", url: "../bi/distributor_gm.html"},
	        {name: "terminalOverview", text: "终端管理", img: "../../image/report.png", url: "../bi/terminal.html"},
	        {name: "1", text: "团队绩效", img: "../../image/report.png", url: "../bi/team.html"},
            {name: "5", text: "非活跃客户统计", img: "../../image/report.png", url: "../bi/totalterminal.html"},
            {name: "6", text: "经销商账期", img: "../../image/report.png", url: "../bi/distributoraccount.html"},
            ]},
        
        {name: "detailReport", text: "BU", img: "../../image/file.png", children: [
                {name: "1", text: "商业采购数据", img: "../../image/report.png", url: "../bi/report/purchase.html"},
                {name: "2", text: "终端销售数据", img: "../../image/report.png", url: "../bi/report/sale.html"},
                {name: "3", text: "经销商库存数据", img: "../../image/report.png", url: "../bi/report/inventory.html"},
                // {name: "4", text: "终端分析", img: "../../image/report.png", url: "../bi/customer.html"}
            ]},
        {name: "flow", text: "基础流向", img: "../../image/file.png", children: [
                {name: "5", text: "销售流向", img: "../../image/report.png", url: "../bi/sales.html"},
                {name: "6", text: "采购流向", img: "../../image/report.png", url: "../bi/purchase.html"},
                {name: "7", text: "库存流向", img: "../../image/report.png", url: "../bi/inventory.html"}
                // {name: "4", text: "终端分析", img: "../../image/report.png", url: "../bi/customer.html"}
            ]},
         
         /*{name: "summaryReport", text: "汇总报表", img: "../../image/file.png", children: [
   	        {name: "1", text: "终端销售汇总", img: "../../image/report.png", url: "../bi/totalsales.html"}, 
  	        {name: "2", text: "一级经销商进销存", img: "../../image/report.png", url: "../bi/invoicingcomparisontotal.html"},
  	        {name: "3", text: "经销商库存汇总", img: "../../image/report.png", url: "../bi/inventorysum.html"},       


  	    ]},
         
        {name: "detailReport", text: "标准流向", img: "../../image/file.png", children: [
 	        {name: "1", text: "销售流向", img: "../../image/report.png", url: "../bi/sales.html"}, 
	        {name: "2", text: "采购流向", img: "../../image/report.png", url: "../bi/purchase.html"},
	        {name: "3", text: "库存流向", img: "../../image/report.png", url: "../bi/inventory.html"}       
	   ]}*/
    ],
    "大区总监": {},
    "主管": {},
    "商务经理": {},
    "销售": {},
    "经销商": {}
};