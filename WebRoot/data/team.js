var salesdataset = [
   ['MichaelYang', 'HelenFeng', 'JohnZhou', 'CherryChen', 'AngleLi', 'KenKuang'],
   [2012, 1206, 1182, 755, 697, 584],
   [4825, 2013, 8768, 1291, 843, 600],
   [240, 166, 740, 170, 120, 102]
];

var purchasedataset = [
   ['SherryQu', 'SimonWu', 'RichardYang', 'JennyWu', 'KevinYang', 'HaleYu'],
   [12100, 7755, 3700, 3325, 2250, 688],
   [16953, 9613, 3626, 5546, 2961, 11702],
   [140, 123, 98, 131, 102, 166]
];


var topicMap = [

    {
        id:"teamachieve",
        topicCode: "achieve",
        //fields: "Amount;target;achieve",
        dataname:"getTeamAchieve",
        //type:"total",
        dataType:"sellout",
        k:"target;sumdata",
        elementMap:{
            "series[0];data[0];value":"achieve",
            "#saletarget":"target",
            "#salereal":"sumdata"

        },
    },

    {
        id:"businessrank",
        topicCode: "achieve",
        //fields: "Amount;target;achieve",
        dataname:"getTeamAchieveDetail",
        //type:"total",
        dataType:"sellin",
        k:"ytd;target",
        elementMap:{
            "xAxis[0];data":"name",
            "series[0];data":"ytd",
            "series[1];data":"target",
            "series[2];data":"achieve",

        },
    },

    {
        id:"salerank",
        topicCode: "achieve",
        //fields: "Amount;target;achieve",
        dataname:"getTeamAchieveDetail",
        //type:"total",
        dataType:"sellout",
        k:"ytd;target",
        elementMap:{
            "xAxis[0];data":"name",
            "series[0];data":"ytd",
            "series[1];data":"target",
            "series[2];data":"achieve",

        },
    }



];