var report = {
    topic:"Purchase",
    theme: {
        name: "",
        chart: ["line", "pie", "bar"],
        dimension: [
            {code: "md_peroid", text: "期间", fields: [
                    {caption: "年份", field: "year", filterField: "year", type:'select',defaultadded: true,
                        defaultvalue:[
                            {code:"2017",name:"2017"},{code:"2018",name:"2018"},{code:"2019",name:"2019"},

                        ]},
                    {caption: "季度", field: "quarter", filterField: "quarter",
                        type:'select',
                        defaultvalue:[
                            {code:"1",name:"Q1"},{code:"2",name:"Q2"},{code:"3",name:"Q3"},
                            {code:"4",name:"Q4"}
                        ]},

                    {caption: "月份", field: "month", filterField: "month",type:'select',defaultadded: true,
                        defaultvalue:[
                            {code:"1",name:"1"},{code:"2",name:"2"},{code:"3",name:"3"},
                            {code:"4",name:"4"},{code:"5",name:"5"},{code:"6",name:"6"},{code:"7",name:"7"},
                            {code:"8",name:"8"},{code:"9",name:"9"},{code:"10",name:"10"},{code:"11",name:"11"},{code:"12",name:"12"},
                        ]},
                    /*{caption: "库存时间", field: "BizDate", filterField: "BizDate",type:'date',defaultadded:true, url:"root/data/procedure/getInventoryLastDataTime"
					},*/

                ]},
            {code: "territory", text: "架构", fields: [
                    {caption: "大区经理", field: "RSM", filterField: "name",showField: "RSMname",type:'select',defaultadded:true, url:"root/bi/getPosition?userType=RSM"},
                    {caption: "主管", field: "Supervisor", filterField: "name",showField: "Supervisorname", type:'select', parentField:"RSM", url:"root/bi/getPosition?userType=Supervisor"},
                    //{caption: "销售", field: "Salesperson",filterField: "name", showField: "Salespersonname", type:'select', parentField:"Supervisor", url:"root/bi/getPosition?userType=Salesperson"}
                ]},
            {code: "area", text: "区域", fields: [
                    {caption: "大区", field: "region", filterField: "region", type:'select',defaultadded:true, url:"root/data/procedure/getDataSet?tablename=RSMRegion"},

                ]}, {code: "Distributor", text: "经销商", fields: [
                    {caption: "经销商名称", field: "DistributorName", filterField: "distributorname", defaultadded:true},
                    {caption: "经销商编码", field: "DistributorCode", filterField: "distributorcode", defaultadded:true},
                    {caption: "经销商省份", field: "ProvinceName", filterField: "ProvinceName", },
                    {caption: "经销商城市", field: "CityName", filterField: "CityName", },
                    {caption: "经销商级别", field: "DistributorLevel", filterField: "distributorLevel", defaultadded:true, type:'select',
                        defaultvalue:[
                            {code:"一级商",name:"一级商"},{code:"分销商",name:"分销商"}]
                    },
                ]},
            {code: "Product", text: "产品", fields: [
                    {caption: "产品线", field: "brand", filterField: "brand", type:'select', defaultvalue:[ {code:"Anthogyr",name:"Anthogyr"},{code:"Straumann",name:"Straumann"}]},
                    {caption: "产品编码", field: "ProductCode", filterField: "ProductCode"},
                    {caption: "产品名称", field: "ProductName", filterField: "ProductName"},
                    {caption: "产品归类", field: "ProductType", filterField: "ProductType"},
                    {caption: "产品类型", field: "ProductCategory", filterField: "ProductCategory"},
                    {caption: "类型", field: "Category", filterField: "Category"},
                    {caption: "材质", field: "Material", filterField: "Material"},
                    {caption: "单位", field: "Unit", filterField: "Unit"},
                    {caption: "规格", field: "Specification", filterField: "Specification"},
                    {caption: "价格", field: "Price", filterField: "Price"},
                    {caption: "PRH4", field: "PRH4", filterField: "PRH4"},
                    {caption: "PRH5", field: "PRH5", filterField: "PRH5"},
                ]}
        ],
        value: [
            {caption: "销量", field: "Quantity",},
            {caption: "YTD销售额", field: "Amount",defaultadded:true,},
        ]
    },

};