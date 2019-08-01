
 function randomData() {
        return Math.round(Math.random() * 2500);
    }

    function randomData1() {
        return Math.round(Math.random() * 200);
    }
var data2 = [
        {name: '北京', value: '16869'},

    ];

    var data1 = [
        {name: '广西', value: "143"},

    ];

    
    var tabledata = [{brand:'',region:'',type:"",account:""},
                     {brand:'',region:'',type:"",account:""},
                     {brand:'',region:'',type:"",account:""},
                     {brand:'',region:'',type:"",account:""},];

 var topicMap = [

     {
         id:"accounts",
         topicCode: "achieve",
         //fields: "Amount;target;achieve",
         dataname:"companycount",
         //type:"total",
         //dataType:"sellout",
         elementMap:{
             "series[0];data.value":"num",
             "series[0];data.name":"brand",
             "series[0];data[2];value.0":"overlap",

         },
         filter:" and countfilter = 1=1"
     },
     {
         id:"map",
         topicCode: "achieve",
         //fields: "Amount;target;achieve",
         dataname:"getCompanyProvince",
         aggcode:"CompanyCode;peroid;brand",
         func:"resetMapMax",
         k:"Amount",
         //dataType:"sellout",
         elementMap:{
             "series[0];data.value":"num",
             "series[0];data.name":"ProvinceName",
             "series[1];data.value":"Amount",
             "series[1];data.name":"ProvinceName",

         },
     },

     {
         id:"volatility",
         topicCode: "achieve",
         //fields: "Amount;target;achieve",
         dataname:"companycountRegion",
         //type:"total",
         //dataType:"sellout",
         elementMap:{
             "xAxis[0];data":"region",
             "series[0];data":"prenum",
             "series[1];data":"num",
             "series[2];data":"target",
             "series[3];data":"new",
             "series[4];data":"overlap",

         },

     },


 ];