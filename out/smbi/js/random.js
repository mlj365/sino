function randomData(length, multiple, fixed) {
    if (!length) {
        length = 1;
    }
    if (!multiple) {
        multiple = 1;
    }
    if (!fixed) {
        fixed = 0;
    }
    if (length == 1) {
        return getOneRandom(multiple, fixed);
    }
    var data = [];
    for (var i = 0; i < length; i++) {
        data[i] = getOneRandom(multiple, fixed);
    }
    return data;
}



function getOneRandom(multiple, fixed) {
    return parseFloat((Math.random() * multiple).toFixed(fixed));
}

function changeDivtext(ele, text) {
    $("#" + ele).text(text);
}

function changeOption(option, key, value, shouldK) {

    var keys = key.split(";");
    for (var i = 0; i < keys.length - 1; i++) {
        var onekey = keys[i];

        if (onekey.indexOf("[") != -1 && onekey.indexOf("]") != -1) {
            var onekeys = onekey.split("[");
            var num = onekeys[1].split("]")[0];
            var suboption = option[onekeys[0]][num];
            if(!suboption) {
                suboption = {};

            }
            option =suboption;

            continue;
        }
        option = option[keys[i]];
    }
    var endkey = keys[keys.length - 1];
    var idx = endkey.indexOf(".");
    if(idx != -1) {
        var prekey = endkey.substring(0, idx);
        var endkey = endkey.substring(idx + 1, endkey.length);

        if (value instanceof Array) {
            if (!isNaN(endkey)) {
                var val = value[endkey];
                if(checkNumber(val)) {
                    if (shouldK) {
                        val = parseInt(val)/ 1000;
                    }

                }

                option[prekey] = val;
            }

            for(var i = 0; i <value.length; i++) {
                var one = option[prekey][i];
                if (!one) {
                    one = {};
                }
                var val = value[i];
                if(checkNumber(val)) {
                    if (shouldK) {
                        val = parseInt(val)/ 1000;
                    }
                }
                one[endkey] = val;
            }
        }

    }else {
        var val;
        if("formatter" == endkey && value instanceof Array) {
            val = value[0];

        }else {
           val = value;
        }
        if (val instanceof Array) {
            for(var i = 0; i <val.length; i++) {

                var oneval = val[i];
                if(checkNumber(oneval)) {
                    if (shouldK) {
                        val[i] = parseInt(oneval)/ 1000;
                    }
                }
            }
        }else {
            if(checkNumber(val)) {
                if (shouldK) {
                    val = parseInt(val)/ 1000;
                }
            }
        }

        option[endkey] = val;
    }
}

function clone(obj) {
	var result,oClass=isClass(obj);
    //确定result的类型
	if(oClass==="Object"){
	    result={};
	}else if(oClass==="Array"){
	    result=[];
	}else{
	    return obj;
	}
	for(var key in obj){
	    var copy=obj[key];
	    if(isClass(copy)=="Object"){
	        result[key]=arguments.callee(copy);//递归调用
	    }else if(isClass(copy)=="Array"){
	        result[key]=arguments.callee(copy);
	    }else{
	        result[key]=obj[key];
	    }
	}
	return result;
};

function isClass(o){
    if(o===null) return "Null";
    if(o===undefined) return "Undefined";
    return Object.prototype.toString.call(o).slice(8,-1);
}

function changetab(url, groupId, itemId) {
    self.parent.document.getElementById("content").src = url;
    
    if(typeof(groupId)=="undefined" || typeof(itemId)=="undefined") {
    	return;
    }
    
    var leftMenu = $("#leftMenu");
    if (leftMenu.length ==0) {
    	leftMenu = $("#leftMenu",window.parent.document);
	}
    var preGroupIndex = leftMenu.attr("groupIndex");
	var preItemIndex = leftMenu.attr("itemIndex");
	if(typeof(preGroupIndex)=="undefined" || typeof(preItemIndex)=="undefined") {
		preGroupIndex = -1;
    }
	toggleClickClass(preGroupIndex, preItemIndex);
	
	leftMenu.attr("groupIndex", groupId);
	leftMenu.attr("itemIndex", itemId);
    toggleClickClass(groupId, itemId);
}

function toggleClickClass(groupIndex, itemIndex){
		//group -1  首页    group  从0开始  item从1开始
		var leftMenu = $("#leftMenu");
		if (leftMenu.length ==0) {
	    	leftMenu = $("#leftMenu",window.parent.document);
		}
		var targetEle;
		if(typeof(groupIndex)=="undefined" || groupIndex == -1) {
			targetEle = leftMenu.children()[0];
		}
		else {
			var groupele;
			if($(".gm_group").length == 0) {
				groupele = $(".gm_group", window.parent.document);
			}
			else{
				groupele = $(".gm_group");
			}
			targetEle = $(groupele[groupIndex]).children()[itemIndex];
		}
		
	$(targetEle).toggleClass("cilcked");      		
}

function resizeChart() {
	var resizeTimer = null; 
	if (resizeTimer) clearTimeout(resizeTimer);
	 
	resizeTimer = setTimeout(function () {
		var canvas = $("canvas");
			
		for(var i = 0; i <canvas.length; i++) {
			var onecanvas = $(canvas.get(i));
			var chartid = onecanvas.parent().parent().attr("id");
			onechart = echarts.getInstanceByDom(document.getElementById(chartid));
			var opt = onechart.getOption();
			onechart.resize();
		}
	}, 300);
}


function formatterOneMoney(money) {
	  if(money && money!=null && "''" != money){
	        money = String(money);
	        if("" == money.trim()) {
                return "";
            }
	        var left=money.split('.')[0],right=money.split('.')[1];
	        right = right ? (right.length>=2 ? '.'+right.substr(0,2) : '.'+right+'0') : '.00';
	        var temp = left.split('').reverse().join('').match(/(\d{1,3})/g);
	        return (Number(money)<0?"-":"") + temp.join(',').split('').reverse().join('')+right;
	    }else if(money===0){   //注意===在这里的使用，如果传入的money为0,if中会将其判定为boolean类型，故而要另外做===判断
	        return '0.00';
	    }else{
	        return "0";
	    }

}
function unformatterOneMoney(money) {
	if(money && money!=null){
		money = String(money);
		var group = money.split('.');
		var left = group[0].split(',').join('');
		return Number(left+"."+group[1]);
	}else{
        return "";
	}
}


function showMask() {
    $("#mask").css("height", $(document).height());
    $("#mask").css("width", $(document).width());
    $("#mask").show();
}
//隐藏遮罩层
function hideMask() {
    $("#mask").hide();
}


function initDateime(type, func) {
	if('start' == type) {
		//1. set begin date, default this month begin
		var starttime = FilterArea.getItem('startDate').input.val();
		if (starttime == "") {
			var datetime = new DateTime();
			datetime.toFormerMonth();
			starttime = datetime.str;
			FilterArea.getItem('startDate').input.val(starttime);
		}
	}
	if('end' == type) {
		//2. set end date, default now
		var endtime = FilterArea.getItem('endDate').input.val();
		if (endtime == "") {
			var datetime = new DateTime();
			endtime = datetime.str;
			FilterArea.getItem('endDate').input.val(endtime);
		}	
	}

    if('year' == type) {
        var yeardiv = FilterArea.getItem('year');
        var endtime = yeardiv.val();
        if (endtime == "") {
            var datetime = new DateTime();
            endtime = datetime.year;
            var select = $("#select_", yeardiv);
            select.val(endtime);
        }
    }

    if('month' == type) {
        var yeardiv = FilterArea.getItem('month');
        var endtime = yeardiv.val();
        if (endtime == "") {
            var datetime = new DateTime();
            endtime = datetime.month + 1;
            var select = $("#select_", yeardiv);
            select.val(endtime);
        }
    }
    if (func) {
        eval(func + "();");
    }
}

function statusToText(status) {
	if (status == "getData") {
		return "正在获取数据。。。";
	}
	else if (status == "fileCreated") {
		return "正在生成文件。。。";
	}
	else if (status == "downloading") {
		return "文件已生成，请点击下载！";
	}
	else if (status == "error") {
		return "下载失败，请稍后重试！";
	}
	else {
		return "正在准备获取数据";
	}
}

function stopScroll(exportTimer){
    window.clearInterval(exportTimer);
}

function initPeroidMonth() {
    var year = getLocalData("year");
    if(!year) {
        var lastdate = getLocalData("lastdate");
        if(!lastdate) {
            var geted = false;
            var timeout = setInterval(function () {
                if(geted) {
                    lastdate = new Date(lastdate);
                    year = lastdate.getFullYear();
                        setLocalData("year", year);
                    $("#year").val(year);
                    $("#year").multiselect("refresh");
                    clearInterval(timeout);
                    initPeroidOther();
                }
                lastdate = getLocalData("lastdate");
                if(lastdate) {
                    geted = true;
                }
            }, 500)
        }else {
            lastdate = new Date(lastdate);
            year = lastdate.getFullYear();
            setLocalData("year", year);
            $("#year").val(year);
            $("#year").multiselect("refresh");
            initPeroidOther();
        }

    }else {
        existAdd("year");
        initPeroidOther();
	}
}

function initPeroidOther() {
    existAdd("brand");

    existAdd("quarter");
    var quarter = getLocalData("quarter");
    var monthselect = $("#month");
    monthselect.empty();
    if (quarter) {
        var quarters = quarter.split(";");
        quarters.forEach(function (one) {
            var start = (one - 1) <0? 0 : (one - 1);
            for(var i = start*3; i < one*3; i++) {

                var opt = $('<option />', {
                    value: i + 1,
                    text: i + 1
                });
                opt.appendTo(monthselect);

            }
        });


        monthselect.multiselect('refresh');
        monthselect.multiselect('uncheckAll');
    }

    existAdd("month");
    refreshChart(chartRefresh);

}

function getArrMax(arr) {
    var max = arr[0];
    var len = arr.length;
    for (var i = 1; i < len; i++){
        if (arr[i] > max) {
            max = arr[i];
        }
    }
    return max;
}
function existAdd(code) {
    var val = getLocalData(code);
    var ele = $("#" + code);

    if(!val&& code == "month") {
        var arrMax;
        var quarterVal = getLocalData("quarter");
        if (!quarterVal) {
            arrMax = "4";
        }
        var qqval = quarterVal+"";
        var multipleQuarterVal = qqval.split(";");

        if(multipleQuarterVal.length >= 1 && multipleQuarterVal[0] != "null") {
            arrMax = getArrMax(multipleQuarterVal);
            switch(arrMax) {
                case "1":
                    val = "1;2;3";
                    break;
                case "2":
                    val = "1;2;3;4;5;6";
                    break;
                case "3":
                    val = "1;2;3;4;5;6;7;8;9";
                    break;
                case "4":
                    val = "1;2;3;4;5;6;7;8;9;10;11;12";
                    break;
                default:
                    return;
            }

        } else{
            switch(arrMax) {
                case "1":
                    val = "1;2;3";
                    break;
                case "2":
                    val = "1;2;3;4;5;6";
                    break;
                case "3":
                    val = "1;2;3;4;5;6;7;8;9";
                    break;
                case "4":
                    val = "1;2;3;4;5;6;7;8;9;10;11;12";
                    break;
                default:
                    return;
            }
        }

    } else {
        if(!val) {
            return;
        }
    }
    val = val+"";
    var multipleval = val.split(";");
    var widget = ele.multiselect("widget");

    if(multipleval.length > 1) {
        for(var i = 0; i < multipleval.length; i++) {
            var one = multipleval[i];
            var chedked = $("input[value="+one+"]", widget);
            chedked.attr("checked", true);
            chedked.attr("selected", true);
            chedked.attr("aria-selected",true);
        }
        //ele.multiselect().val(multipleval);
    }else {
        var chedked = $("input[value="+multipleval+"]", widget);
        chedked.attr("checked", true);
        chedked.attr("selected", true);
        chedked.attr("aria-selected",true);
        //ele.multiselect().val(val);
    }

    var inputs = $("input", widget);
    var check = inputs.filter(':checked');
    ele.multiselect("update");
    //ele.multiselect("refresh");
    //refreshChart(chartRefresh);

}

function initCommonMutiSelect() {
    $("#brand").multiselect({
        selectedList: 4,
        multiple:true,
        header:true,
        close:function(event, ui){

            var brands = getMutiInputData("brand");
            var preBrands = getLocalData("brand");
            if(preBrands == brands) {
                return;
            }
            setLocalData("brand", brands);

            refreshChart(chartRefresh);
        }
    });
    $("#year").multiselect({
        click:function(event, ui){
            var brands = ui.value;
            var preBrands = getLocalData("year");
            if(preBrands == brands) {
                return;
            }
            setLocalData("year", brands);
            refreshChart(chartRefresh);
        }
	});
    $("#quarter").multiselect({
        selectedList: 4,
        multiple:true,
        header:true,
        close:function(event, ui){
            var quarter = getMutiInputData("quarter");
            var preBrands = getLocalData("quarter");
            if(preBrands == quarter) {
                return;
            }
            setLocalData("quarter", quarter);
            if("all" == quarter.toLowerCase()) {

                var monthselect = $("#month");
                monthselect.empty();

                for(var i = 0; i < 12; i++) {

                    var opt = $('<option />', {
                        value: i + 1,
                        text: i + 1
                    });
                    opt.appendTo(monthselect);

                }
                monthselect.multiselect('refresh');
                setLocalData("month", quarter);
            }else {
                var monthselect = $("#month");
                monthselect.empty();

                var quarters = quarter.split(";");
                for(var j = 0; j< quarters.length; j++) {
                    var one = quarters[j];
                    var start = (one - 1) <0? 0 : (one - 1);
                    for(var i = start*3; i < one*3; i++) {

                        var opt = $('<option />', {
                            value: i + 1,
                            text: i + 1
                        });
                        opt.appendTo(monthselect);

                    }
                }

                monthselect.multiselect('refresh');
                monthselect.multiselect("uncheckAll");
            }
            refreshChart(chartRefresh);
        }
    });
    $("#month").multiselect({
        selectedList: 4,
        multiple:true,
        header:true,
        close:function(event, ui){
            var month = getMutiInputData("month");
            var preBrands = getLocalData("month");
            if(preBrands == month) {
                return;
            }
            setLocalData("month", month);
            refreshChart(chartRefresh);
        }
	});

    $("#brand").multiselect("uncheckAll");
    $("#quarter").multiselect("uncheckAll");
    $("#month").multiselect("uncheckAll");

}

function getHeaderFilter(filters) {
    var filter = getCommonFilter();

    if(!filters) {
        return filter;
    }

    var filterArray = filters.split(";");
    filterArray.map(function (one) {
        var onefilter = getMutiInputData(one);
        var subOneFilter =  getMutiFilter(one, onefilter);
        filter += " and (" + subOneFilter + ")";
    });

    return filter;
}

function getCommonFilter(unNeeded) {
    var year = "";
    var quarter = "";
    var month = "";
    var brands = "";
    if (unNeeded) {
        var unneededs = unNeeded.split(";");
        unneededs.forEach(function (one) {
            if (!("year" == one)) {
                year = $($("#year").multiselect("getChecked")[0]).val();
            }
            if (!("quarter" == one)) {
                quarter = getMutiInputData("quarter");
            }
            if (!("month" == one)) {
                month = getMutiInputData("month");
            }
            if (!("brand" == one)) {
                brands = getMutiInputData("brand");
            }
        })
    }else {
        year = $($("#year").multiselect("getChecked")[0]).val();

        quarter = getMutiInputData("quarter");
        month = getMutiInputData("month");
        brands = getMutiInputData("brand");
    }


    var filter = "";
    if(year) {
        filter = "year=" + year;
    }

    if (quarter.length > 0) {
        var subMutiFilter = getMutiFilter("quarter", quarter);
        filter += " and " + subMutiFilter;
    }

    if (month.length > 0) {
        var subMutiFilter = getMutiFilter("month", month);
        filter += " and " + subMutiFilter;
    }
    if(brands.length > 0) {
        var subMutiFilter = getMutiFilter("brand", brands);
        filter += " and " + subMutiFilter;
    }
    return filter;
}

function getMutiFilter(field, rawdata, type) {

    var datas = rawdata.split(";");
    var result = datas.map(function(one) {
        if(one instanceof  Number) {
            return field + " = " + one;
        }else {
            return field + " = '" + one + "'";
        }

    }).join(' or ');

    return result;
}

function resetChartData(one, user, filter) {
    if (!filter) {
        filter = getHeaderFilter();
    }

    if(!user) {
        user = getLocalData("user");
    }

    if(!user) {
        return;
    }

    /**
     * e.g.  one{
			 * 			id: chartid
			 * 			topicCode: achieve,Sale,inventory
			 * 			fields: field
			 * 		    type: total roxilid
			 * 		    dataname:dataname
			 * 		    datatype:sellin,sellout,
			 * 		    filter: special filter id
			 * 			elementMap:{
			 * 				0: sum
			 * 				1: target
			 * 				sumid: sum
			 * 			},
			 *
			 * 		}
     */
    var eleId = one.id;
    var topicCode = one.topicCode;
    var fields = one.fields;
    var map = one.elementMap;
    var type = one.type;
    var dataType = one.dataType;
    var dataname = one.dataname;
    var onefilter = one.filter;
    var orderBy = one.orderBy;
    var aggcode = one.aggcode;
    var func = one.func;
    var k = one.k;
    var q = one.q;
    var mayK = [];
    var mayQ = [];
    if (k) {
        mayK = k.split(";");
    }

    if (q) {
        mayQ = q.split(";");
    }

    var chartInstance = echarts.getInstanceByDom(document.getElementById(eleId));

    var params = "code=" + topicCode + "&userType="+user.type + "&userId=" + user.name;

    if (fields) {
        params += "&fields=" + fields;
    }
    if (aggcode) {
        params += "&aggcode=" + aggcode;
    }
    if(type) {
        params += "&type=" + type;
    }
    if(dataType) {
        params += "&dataType=" + dataType;
    }
    if(dataname) {
        params += "&dataName=" + dataname;
    }
    if (onefilter) {
        filter += onefilter;
    }

    if(filter) {
        params += "&filter=" + filter;
    }

    if(orderBy) {
        params += "&orderBy=" + orderBy;
    }



    chartInstance.showLoading('default', {text:'获取中..'});
    Server.call("root/bi/data", params, function (result) {
        if(!result) {
            return;
        }
        var option = chartInstance.getOption();
        for (var one in map){
            var val = map[one];
            var shouldK = false;
            var shouldQ = false;
            if (mayK) {
               mayK.map(function (mayVal, index) {
                   if (val == mayVal) {
                       shouldK = true;
                   }
               })
            }

            if (mayQ) {
                mayQ.map(function (mayVal, index) {
                    if (val == mayVal) {
                        shouldQ = true;
                    }
                })
            }
            var top = one.substring(0, 1);

            if("#" == top) {
                if(shouldK) {
                    $(one).html(formatterOneMoney(parseInt(result[val])/1000));
                } else {
                    $(one).html(formatterOneMoney(result[val]));
                }
            }else if ("$" == top) {
                var variable = one.substring(1, one.length);
                if (shouldK) {
                    setLocalData(variable,parseInt(result[val])/1000);
                }else {
                    setLocalData(variable,result[val]);
                }

            } else if ("*" == top) {
                var variable = one.substring(1, one.length);
                if (shouldQ) {
                    $(one).html(Math.round(result[val]));
                }else {
                    $(one).html(Math.round(result[val]));
                }
            }
            else {
                changeOption(option, one, result[val], shouldK);
            }

        }
        var opt = JSON.stringify(option);
        //TODO webman
        //option.toolbox[0].feature.myDownload.onclick = downloadExcel(eleId, "root/bi/data?" + params);
        chartInstance.setOption(option);
        chartInstance.hideLoading();
        if (func) {
            eval(func+"();");
        }
    });
}

function refreshChart(callback) {
    if (typeof callback === "function") {
        callback();
    }
}

function getMutiInputData(id) {
    var inputs = $("#" + id).multiselect("getChecked");
    var checkedResult = inputs.filter(':checked');
    var result = checkedResult.map(function() {
        var val = $(this).val();
        return val;
    }).get().join(';');

    return result;
}

function downloadExcel(id, url) {
    var chartInstance = echarts.getInstanceByDom(document.getElementById(id));
    chartInstance.showLoading('default', {text:'正在生成excel..'});
    //TODO  webman 支持

}