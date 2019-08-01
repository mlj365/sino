pageArray = [];
senderPage = null;
serverAddress = "";


function BrowserType() {  
    var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串  
    var isOpera = userAgent.indexOf("Opera") > -1; //判断是否Opera浏览器  
    var isIE = userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1 && !isOpera; //判断是否IE浏览器  
    var isEdge = userAgent.indexOf("Windows NT 6.1; WOW64; Trident/7.0;") > -1 && !isIE; //判断是否IE的Edge浏览器
    var isFF = userAgent.indexOf("Firefox") > -1; //判断是否Firefox浏览器  
    var isSafari = userAgent.indexOf("Safari") > -1 && userAgent.indexOf("Chrome") == -1; //判断是否Safari浏览器  
    var isChrome = userAgent.indexOf("Chrome") > -1 && userAgent.indexOf("Safari") > -1; //判断Chrome浏览器  

    if (isIE)   
    {  
         var reIE = new RegExp("MSIE (\\d+\\.\\d+);");  
         reIE.test(userAgent);  
         var fIEVersion = parseFloat(RegExp["$1"]);  
         if(fIEVersion == 7)  
         { return {type: "IE", version: 7};}  
         else if(fIEVersion == 8)  
         { return {type: "IE", version: 8};}  
         else if(fIEVersion == 9)  
         { return {type: "IE", version: 9};}  
         else if(fIEVersion == 10)  
         { return {type: "IE", version: 10};}  
         else if(fIEVersion == 11)  
         { return {type: "IE", version: 11};}  
         else  
         { return {type: "IE", version: 0};}//IE版本过低  
     }//isIE end  
       
     if (isFF) {  return {type: "FF"};}  
     if (isOpera) {  return {type: "Opera"};}  
     if (isSafari) {  return {type: "Safari"};}  
     if (isChrome) { return {type: "Chrome"};}  
     if (isEdge) { return {type: "Edge"};}  
     return {type: "IE", version: 0};
 }

function banBackSpace(e) {
	var ev = e || window.event;
	//各种浏览器下获取事件对象
	var obj = ev.relatedTarget || ev.srcElement || ev.target || ev.currentTarget;
	//按下Backspace键
	if (ev.keyCode == 8) {
		var tagName = obj.nodeName //标签名称
		//如果标签不是input或者textarea则阻止Backspace
		if (tagName != 'INPUT' && tagName != 'TEXTAREA') {
			return stopIt(ev);
		}
		var tagType = obj.type.toUpperCase();//标签类型
		//input标签除了下面几种类型，全部阻止Backspace
		if (tagName == 'INPUT' && (tagType != 'TEXT' && tagType != 'TEXTAREA' && tagType != 'PASSWORD')) {
			return stopIt(ev);
		}
		//input或者textarea输入框如果不可编辑则阻止Backspace
		if ((tagName == 'INPUT' || tagName == 'TEXTAREA') && (obj.readOnly == true || obj.disabled == true)) {
			return stopIt(ev);
		}
	}
}
function stopIt(ev) {
	if (ev.preventDefault) {
		//preventDefault()方法阻止元素发生默认的行为
		ev.preventDefault();
	}
	if (ev.returnValue) {
		//IE浏览器下用window.event.returnValue = false;实现阻止元素发生默认的行为
		ev.returnValue = false;
	}
	return false;
}

function checkNumber(inputData) {
    if (parseFloat(inputData).toString() == "NaN") {
        return false;
    } else {
        return true;
    }
}

$(function() {
	//实现对字符码的截获，keypress中屏蔽了这些功能按键
	document.onkeypress = banBackSpace;
	//对功能按键的获取
	document.onkeydown = banBackSpace;
})

function debug(value) {
	if (console) {
		console.log(value);
	}
}

function ceshi(data) {
	alert(JSON.stringify(data));
}

function isWide() {
	var result = $(window).width() >= 640;
	return result;
}

//替换特殊字符
function replaceSpecialCharacter(result) {
	if (result) {
		result = result.replace(/\r\n/g, "<br>");    
		result = result.replace(/\t/g, "");    
		result = result.replace(/\r/g, "<br>");    
		result = result.replace(/\n/g, "<br>");    
		result = result.replace(/\n\r/g, "<br>");    
		result = result.replace(/%/g,"%25");
		result = result.replace(/#/g,"%23");
		result = result.replace(/\"/g,"“");
		result = result.replace(/\'/g,"‘");
		
	}
	return result;
}

//替换特殊字符_清空空格和回车
function replaceSpecialCharacter_empty(result) {
	if (result) {
		result = result.replace(/(^\s*)|(\s*$)/g, "");    
		result = result.replace(/\r\n/g, "");    
		result = result.replace(/\t/g, "");    
		result = result.replace(/\r/g, "");    
		result = result.replace(/\n/g, "");    
		result = result.replace(/\n\r/g, "");    
	}
	return result;
}

//替换回特殊字符
function doReplaceSpecialCharacter(result) {
	if (result) {
		result = result.replace(/<br>/g,"\r\n");  
		result = result.replace(/%25/g,"%");
		result = result.replace(/%23/g,"#");
	}
	return result;
}

jQuery.fn.onShow = function(fun) {
	this.data("onShow", fun);
};

jQuery.fn.disabled = function(flag) {
	if (flag) {
		$("#mask").remove();
		var width = this.width();
		var height = this.height();
		
		this.css("position", "relative");
		this.append("<div style='position:absolute; left:0px; top:0px' id='mask'></div>");
		$("#mask").css({width:width, height: height, background:'#7482e0', opacity:0.1});
	}
	else{
		$("#mask").remove();
	}
};

jQuery.fn.mask = function(config) {
	if (!config) { config = ""; };
	
	if (!this.skin) {
		this.skin = $("<div style='position:absolute; left:0px; top:0px; width:100%; height:100%' align='center'></div>");
		this.skin.css(config.css || {"line-height": 30, background:'#7482e0', opacity:0.6});
		this.append(this.skin);
	}
	
	this.skin.html(config.text || config);
	this.skin.show();
};

jQuery.fn.hideMask = function() {
	if (this.skin) {
		this.skin.hide();
	}
};

jQuery.fn.setActive = function(flag) {
	if (flag) {
		this.removeAttr("disabled");
		this.css("background-color", "white");
	}
	else {
		this.attr("disabled", "disabled");
		this.css("background-color", "#fde1e1");		
	}
};

function jumpTo(target) {
	window.top.window.location.href = target;
}

function windowTo(target) {
	window.top.windowTo_target = window.location.href;
	window.location.href = target;
}

function pageTo(target, frontway) {
	senderPage = target;

	var prior = null;
	if (pageArray.length > 0) {
		prior = pageArray[pageArray.length - 1]; 
	}
	
	var onShow = target.data("onShow");
	if (onShow) { onShow(); }
	
	target.frontway = frontway;
	pageArray.push(target);
	
	if (isWide() || !prior) {
		if (prior && !prior.frontway) {
			prior.hide();
		}
		target.fadeIn(300);
	} 
	else {
		if (prior.is(":animated")) {
			prior.stop(true, true);
		}
		if (target.is(":animated")) {
			target.stop(true, true);
		}
		
		var width = $(window).width(), halfWidth = Math.floor(width / 2);
		
		target.css({left: width, right: -width});
		target.show();		
		target.animate({left:0, right:0}, 300);
		
		prior.css({left:0, right:0});
		prior.animate({left: -halfWidth, right: halfWidth}, 300, function() {
			prior.hide(); 
			prior.css({left:0, right:0});
		});
	}	
}

function pageBack() {
	var next = pageArray.pop();
	var target = pageArray[pageArray.length - 1];
	
	var onClose = next.data("onClose");
	if (onClose) { onClose(); }
	
	var onShow = target.data("onShow");
	if (onShow) { onShow(); }
	
	if (isWide()) {
		next.hide();
		if (!target.frontway) {
			target.fadeIn(300);
		}
	} 
	else {
		if (next.is(":animated")) {
			next.stop(true, true);
		}
		if (target.is(":animated")) {
			target.stop(true, true);
		}
		
		var width = $(window).width(), halfWidth = Math.floor(width / 2);
		
		next.css({left:"0", right:"0"});
		next.animate({left: width, right: -width}, 300, function(){
			next.hide(); 
			next.css({left:"0", right:"0"}); 
		});
		
		target.css({left: -halfWidth, right: halfWidth});
		target.show();
		target.animate({left:"0", right:"0"}, 300);
	}		
}

function windowBack() {
	if (window.top.windowTo_target) {
		if (window.top.windowTo_target.indexOf("?") > 0) {
			window.location.href = window.top.windowTo_target + "&" + new Date().getTime();
		}
		else {
			window.location.href = window.top.windowTo_target + "?" + new Date().getTime();
		}
	}
}

function page_iframe_action(config) {
	var actionCode = config.actionCode;
	var element = config.element;
	var url = config.url;
	var callback = config.callback;
	
	if (actionCode == "open" && element && url) {
		element.removeClass("iframe_right_close");
		element.addClass("iframe_open");
		$("iframe", element).attr("src", url);
		if (callback) {
			callback();
		}
	}
	else if (actionCode == "close" && element) {
		element.removeClass("iframe_open");
		element.addClass("iframe_right_close");
		if (callback) {
			callback();
		}
	}
}

function getSessionData(name) {
	var data = sessionStorage.getItem(name);
	return data ? JSON.parse(data) : null;
};

function setSessionData(name, value) {
	sessionStorage.setItem(name, JSON.stringify(value));
};

function getLocalData(name) {
	var data = localStorage.getItem(name);
	return data ? JSON.parse(data) : null;
};

function setLocalData(name, value) {
	localStorage.setItem(name, JSON.stringify(value));
};

function time2date(element, value) {
	if (value) {
		value = value.replace(/-/g,"/");
		var date = new Date(value).format("yyyy-MM-dd");
		element.html(date);
	}
	else {
		element.html(value);
	}
}

function qtyRender(element, value) {
	element.html(value || 0);
}

function rateRender(element, value) {
	element.html((value || 0) + "%");
}

function moneyRender(element, value) {
	element.html("￥" + Number(value).toFixed(2));
}

function fmoney(s, n) {   
   n = n >= 0 && n <= 20 ? n : 2;   
   s = parseFloat((s + "").replace(/[^\d\.-]/g, "")).toFixed(n) + "";   
   var l = s.split(".")[0].split("").reverse(); 
   var suffix = s.split(".")[1];   
   var result = "";   
   
   for (var i = 0; i < l.length; i ++ ) {   
	   result += l[i] + ((i + 1) % 3 == 0 && (i + 1) != l.length ? "," : "");   
   }
   
   result = result.split("").reverse().join("");
   
   if (suffix) {
	   result = result + "." + suffix;
   }
   
   return result;   
}

function getCurMonthDays (year, month) {  //获取当前月的天数(Max)
	var new_year = year; 
	var new_month = month++;
	
	if(month > 12) {          
		new_month = 1;
		new_year++;
	}          
	var new_date = new Date(new_year, new_month, 1);          
	var count =   (new Date(new_date.getTime()-1000*60*60*24)).getDate();       
	
	return count;  
}

function toTenThousand(data) {
	if (!data) { return 0; }
	return Math.round(data / 10000.0, 2);
}

function formatFileSize(size) {
	if (size > 1024 * 1024) {
		return (Math.round(size * 100 / (1024 * 1024)) / 100).toString() + 'MB';
	}
    else {
    	return (Math.round(size * 100 / 1024) / 100).toString() + 'KB';
    }
}

function toURL(data){
	var result = null;
	if (!data) {
		return result;
	}
	
	for (var prop in data) { 
		if ("function" == typeof data[prop]) {
			continue;
		}
		if(!data[prop]){
			continue;
		}
		
		result = result ? result + "&" + prop + "=" + $.fm.encode(data[prop]) : prop + "=" + $.fm.encode(data[prop]);
	}
	return result;
}

var getURLParams;

function setVisible(element, visible) {
	setElAttr(element, visible, setOneVisible);
}

function setEnable(element, enable) {
	setElAttr(element, enable, setOneEnable);
}

function setElAttr(element, attrs, func) {
	if ($.isArray(element)) {
		if ($.isArray(attrs)) {
			for (var i = 0 ; i < element.length; i++) {
				func(getEl(element[i]), attrs[i]);
			}			
		}
		else {
			for (var i = 0 ; i < element.length; i++) {
				func(getEl(element[i]), attrs);
			}
		}
	}
	else {
		func(getEl(element[i]), attrs);
	}
}

function setOneVisible(element, visible) {
	if (visible) {
		element.show();
	}
	else {
		element.hide();
	}
}

function setChildrenHide(element) {
	element.children(".btn").hide();
}

function setOneEnable(element, enable) {
	if (enable) {
		element.removeAttr("disabled");
		element.removeClass("btn-disabe");
	}
	else {
		element.attr("disabled", !enable);
		element.addClass("btn-disabe");
	}
}

function getEl(element) {
	if (typeof element == "string") {
		if ("#" != element.substring(0, 1)) {
			element = "#" + element;
		}
			
		element = $(element);
	}
	
	return element;
}

function objectToArray(obj, begin, end) {
	begin = begin || 0; end = end || 1000;
	var result = [];
	
	var idx = 0;
	for (var prop in obj) {
		if (idx < begin || idx >= end) {
			idx++;
			continue;
		}
		
		result.push(obj[prop]);	
		idx++;
	}
	
	return result;
}


(function ($) {
	
	$.fm = {
			
		decode: function (obj) {
			if (!obj) {
				return null;
			}
			
        	if ($.isArray(obj)) {
        		return this.decodeArray(obj);
        	} 
        	else if (typeof obj == "object") {
        		return this.decodeObject(obj);
        	}
        	else if (typeof obj == "string") {
        		try {
        			return decodeURIComponent(obj);
				} catch (e) {
					return obj;
				}
           	} 
        	else {
        		return obj;
        	}
		},
		
		decodeArray: function(data) {
			for (var i = 0; i < data.length; i++) {
				if (typeof data[i] == 'string') {
					data[i] = this.decode(data[i]);
				}
				else {
					data[i] = this.decodeObject(data[i]);					
				}
			}
			
			return data;
		},
		
		decodeObject: function(data) {
			for (var prop in data) {
				if (data[prop]) {
					data[prop] = this.decode(data[prop]);					
				}
			}
			
			return data;
		},
		
		encode: function(obj) {
        	if ($.isArray(obj)) {
        		return this.encodeArray(obj);
        	} 
        	else if (typeof obj == "object") {
        		return this.encodeObject(obj);
        	} 
        	else if (typeof obj == "string") {
        		return encodeURI(obj);
           	} 
        	else if (typeof obj == "boolean") {
        		return String(obj);
        	} 
        	else if (typeof obj == "number") {
        		return String(obj);
         	} 
        	else if (typeof obj === "function") {
        		return "";
        	}
		},
		
	    encodeArray: function(o) {
	        var a = ["[", ""],
	        
	        len = o.length,
	        i;
	        for (i = 0; i < len; i += 1) {
	            a.push(this.encode(o[i]), ',');
	        }
	        
	        a[a.length - 1] = ']';
	        return a.join("");
	    },
	    
	    encodeObject: function(o) {
	    	if (!o) {
	    		return "null";
	    	}
	    	if (o.length) {
	    		return "el";
	    	}
	        var a = ["{", ""];
	        
	        for (var i in o) {
	            if (i == 'parent') {
	            	continue;
	            }
	            a.push(this.encode(i), ":", this.encode(o[i]), ',');
	        }
	        
	        a[a.length - 1] = '}';
	        return a.join("");
	    },
	    
		objectToURI: function(object) {
			if (!object) {
				return null;
			}
			
			if (typeof object == "string") {
				return encodeURI(object);
			}
			
    		var param = null;
    		for (var prop in object) {
    			if (object[prop] || object[prop] == "") {
    				if (param) {
    					param = param + "&" + prop + "=" + encodeURI(object[prop]);
    				}
    				else {
    					param = prop + "=" + encodeURI(object[prop]);
    				}
    			}
    		}	
    		
    		return param;
		},
	    
	    formatBarcode: function(value) {
	    	if (!value) {
	    		return value;
	    	}
	    	
	    	var pos = value.indexOf(",");
	    	if (pos > 0) {
	    		value = value.substring(pos + 1);
	    	}
	    	
	    	return value;
	    },
	    
		S4 : function(){// 验证码
			return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
		},

		guid : function(){
			return (this.S4() + this.S4() + this.S4() + this.S4() + this.S4() + this.S4() + this.S4() + this.S4());
		}    
	};
	
	getURLParams = function (url) {
		var result = {};
		if (!url) { return result; };
		
		var begin = url.indexOf("?") + 1;
		var end = url.length;
		var value = url.substring(begin, end);
		var params = value.split("&");
		
		for (var i = 0; i < params.length; i++) {
			var param = params[i];
			var idx = param.indexOf("=");
			if (idx >= 0) {
				result[param.substring(0, idx)] = param.substring(idx + 1, param.length);
			}
			else {
				result[param] = null;
			}
		}
		
		result = $.fm.decode(result);
		return result;
	};
	
	Date.prototype.format = function(fmt) {
	  var o = {   
	    "M+" : this.getMonth()+1,                 // 月份
	    "d+" : this.getDate(),                    // 日
	    "h+" : this.getHours(),                   // 小时
	    "m+" : this.getMinutes(),                 // 分
	    "s+" : this.getSeconds(),                 // 秒
	    "q+" : Math.floor((this.getMonth()+3)/3), // 季度
	    "S"  : this.getMilliseconds()             // 毫秒
	  };   
	  if (/(y+)/.test(fmt)) {
		  fmt = fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));
	  }   
	  for (var k in o) {
		  if (new RegExp("("+ k +")").test(fmt)) {
			  fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));   
		  }
	  }  
	  return fmt;   
	};
	
	Array.prototype.exists = function(prop, value) {
		if (!value) {
			value = prop;
			prop = null;
		}
		
		for (var i = 0; i < this.length; i++) {
			var line = this[i];
			line = this[i].record ? this[i].record : this[i];
			
			if (prop) {
				if (line[prop] == value) {
					return true;
				}
			}
			else {
				if (line == value) {
					return true;
				}
			}
		}
		
		return false;
	};
	
	Array.prototype.getItem = function(prop, value) {
		if (!value) {
			value = prop;
			prop = null;
		}
		
		for (var i = 0; i < this.length; i++) {
			var line = this[i];
			line = this[i].record ? this[i].record : this[i];
			
			if (prop) {
				if (line[prop] == value) {
					return line;
				}
			}
			else {
				if (line == value) {
					return line;
				}
			}
		}
		
		return null;
	};	

    Object.init = function(options) {
		if (prototyping) {  
			return;
		}
		
		if (options) {
        	var name = null, value;
        	
        	for (name in options) {
        		value = options[name];
    			this[name] = value;	
        	}
		}
	};
	
	Object.subClass = function(properties) {
		
		if (!properties) {
			return;
		}
		
		var clazz = properties.init;
		delete properties.init;
		
		if (!clazz) {
			clazz = new Function();
		}
		
		prototyping = true;
		try {
			var prototype = new this();
		} 
		finally {
			prototyping = false;
		}
		
		$.extend(prototype, properties);
		
		clazz.prototype = prototype;
		clazz.subClass = arguments.callee;
		
		return clazz;
	};
	
	Object.toURL = function() {
		var result = null;
		
		if (!data) {
			return result;
		}
		
		for (var prop in this) {
			if ("function" == typeof this[prop]) {
				continue;
			}
			
			result = result ? result + "&" + prop + "=" + $.fm.encode(this[prop]) : prop + "=" + $.fm.encode(this[prop]);
		}
		
		return result;
	};
	
	DataObject = Object.subClass({
		init: function(data) {
			this.record = data || {};
			this.old = {};
			this.changed = false;
			
			Object.call(this, {});
		},		
		
		setValue: function(prop, value) {
			this.record[prop] = value;
			
			if (this.old[prop] != this.record[prop]) {
				this.changed = true;
			}
			
			if (this.onDataChange) {
				this.onDataChange();
			}
		},
		
		getValue: function(prop) {
			return this.record[prop];
		},
		
		valueChanged: function(prop) {
			return this.old[prop] != this.record[prop];
		},

		commit: function() {
			this.old = $.extend({}, this.record);
			this.changed = false;	
		}
	});
	
	var ServerClass = Object.subClass({
		dataNames: ["line", "dataset", "entityset", "rows", "tree", "user"],
		excludeNames: {"success": true, "error": true},
		sysParams: {
			timeoutPage: "root/page/system/timeout.html"
		},
		
		ajaxRequest: function(url, param, callback, onSuccess, onFail) {
			var me = this;
			if (param) {
				if ($.isFunction(param)) {
					callback = param;
					param = null;
				}
				else {
					url = url + "?" + param;
				}
			}
			
			url = serverAddress + url;
			if (url.indexOf("?") > 0) {
				url = url + "&" + new Date().getTime();
			}
			else {
				url = url + "?" + new Date().getTime();
			}

			try {
				$.ajax(url, {
					dataType: 'json',
					success: function(result) {
						if (result) {
							if ("timeout" == result.errorcode) {
								window.top.location.href = me.sysParams.timeoutPage;
							}
							else if (!result.success) {
								if (onFail) { 
									onFail(callback, result); 
								}
								else {
									var error = result.errorcode + ": " + $.fm.decode(result.errormessage);
									try {
										if (console.log) {
											console.log(error);
										}						
									}
									catch (e) {	}
								}
							}
						}
						
						onSuccess(callback, result);
					},
					error: function(d1, error, message) {
						try {
							if (console.log) {
								console.log(error);
							}						
						}
						catch (e) {	}
					}
				});				
			}
			catch(e) {
			}
		},
		
		getResultData: function(result) {
			if (!result) return;
			
			// 1.
			for (var i = 0; i < this.dataNames.length; i++) {
				var name = this.dataNames[i];
				
				if (result[name]) {
					var data = result[name];
					var page = result['page'];
					return [$.fm.decode(data), $.fm.decode(page), "byPage"];
				}
			}
			
			// 2.
			var names = [];
			for (var prop in result) {
				if (!this.excludeNames[prop]) {
					names.push(prop);
				}
			}
			
			if (names.length == 1) {
				var data = result[names[0]];
				return $.fm.decode(data);
			}
		},
		
		getSysParam: function(name, callBack) {
			var aServer = window.top.window.Server ? window.top.window.Server : Server;
			
			if (aServer.loaded) {
				callBack(aServer.sysParams[name]);
			}
			else {
				var url = 'root/sysParam/' + name + '/get';
				me.ajaxRequest(url, null, callback, function(doCallback, result) {
					if (doCallback) {
						var data = me.getResultData(result);
						doCallback(data);
					}
				});				
			}
		},
		
		newObject: function(url, callback) {
			if (!url) {return;} var me = this;
			url = this.addUrl(url, "/newObject");
			
			me.ajaxRequest(url, null, callback, function(doCallback, result) {
				if (doCallback) {
					doCallback(result);
				}
			});
		},
		
		saveData: function(url, data, callback) {
			var me = this;
			if (!url) {return;}
			url = this.addUrl(url, "/saveLine");

			var param = $.fm.objectToURI(data);
			
			me.ajaxRequest(url, param, callback, function(doCallback, result) {
				if (doCallback) {
					doCallback(result.success);
				}
			});
		},
		
		addData: function(url, data, callback) {
			var me = this;
			if (!url) {return;}
			url = this.addUrl(url, "/addLine");

			var param = null;
			if (!$.isFunction(data)) {
				param = $.fm.objectToURI(data);
			}
			else {
				callback = data;
			}
			
			me.ajaxRequest(url, param, callback, function(doCallback, result) {
				if (doCallback) {
					doCallback(result);
				}
			});
		},
		
		getData: function(url, param, callback) {
			if (!url) {return;} var me = this;
			
			if ($.isFunction(param)) {
				callback = param;
				param = null;
			}
			
			param = $.fm.objectToURI(param);
			url = encodeURI(url);
			me.ajaxRequest(url, param, callback, function(doCallback, result) {
				if (doCallback) {
					var data = me.getResultData(result) || result;
					
					if($.isArray(data) && data[2] == "byPage") {
						doCallback(data[0], data[1]);
						return;
					}
					
					doCallback(data);
				}
			});
		},
		
		deleteById: function(url, data, callback, asynchroniz) {
			if (!url) {return;} var me = this;
			url = this.addUrl(url, "/deleteById");
			
			var id = 'string' == typeof (data) ? data : data.id;
			url = serverAddress + url + '?id=' + id;	
			
			me.ajaxRequest(url, null, callback, function(doCallback, result) {
				if (doCallback) {				
					doCallback.call({}, result);				
				}
			}, "get");
		},
		
		call: function(url, data, callback) {
			if (!url) {return;} var me = this;
			
			var afterRequest = function(doCallback, result) {
				if (doCallback) {
					try {
						result = $.fm.decode(result);
					}
					finally {
						doCallback(result);
					}
				}
			};
			 
			if ($.isFunction(data)) {
				callback = data;
				data = null;
			}
			
			if (data) {
				data = $.fm.objectToURI(data);
			}
			
			me.ajaxRequest(url, data, callback, afterRequest, afterRequest);
		},
		
		postData: function(url, data, callBack, onError, asynchroniz) {
			var me = this;
			if (!url || !data) {
				return;
				}

			$.ajax(url, {
				type: 'post',				
				async: asynchroniz,
				dataType: 'json',
				data: data,
				success: function(result){
					if (!result.success) {
						if (onError) {
							onError(result);
							return;
						}
					}
					
					if (callBack) {
						callBack($.fm.decode(result));
					}
				},
				
				error: function(d1, error, message) {
					if (onError) {
						onError();
					}
				}	
			});
		},

		upload: function(url, file, onProgress, onSuccess, onError) {
			if (file.innerHTML) {
				file.upload({
					url: url,
					params: { },
					dataType: 'json',
					onSend: function (obj, str) { 
						return true; 
					},
					onComplate: function (data) {
	                	if (onSuccess) {
	                		data = $.fm.decode(data);
	                		onSuccess(data); 
	                	}
					}
				});				
			}
			else {
				var formdata = new FormData();
				formdata.append("fileList", file);
				try {
					$.ajax({
		                cache: true,
		                type: "POST",
						url: serverAddress + url,
		                data: formdata,
		                dataType: "json",
						processData: false, //必须的参数false
	        			contentType: false, //必须的参数false
						xhr: function(){
							var xhr = $.ajaxSettings.xhr();
							if (onProgress && xhr.upload) {
								xhr.upload.addEventListener("progress" , onProgress, false);
								return xhr;
							}
						},
		                error: function(request) {
		                	if (onError) { onError(); }
		                },
		                success: function(data) {
		                	data = $.fm.decode(data);
		                	if (onSuccess) { onSuccess(data); }
		                }
		            });
				}
				catch(e) {
					if (onError) { onError(); }
				}				
			}
		},
		
		download: function(url, onSucess, onError) {
        	var a = document.createElement('a');
        	a.href = url;
        	$("body").append(a);  
        	a.click();
        	$(a).remove();
		},
		
		getColumns: function(id, callback) {
			if (!id) {return [[]];} var me = this;
			
			var url = "root/columns/" + id + ".js";

			me.ajaxRequest(url, null, callback, function(doCallback, result) {
				if (doCallback) {
					try {
						//result = $.fm.decode(result);
					}
					finally {
						doCallback(result);
					}
				}
			});
		},
		
		addUrl: function(url, segment) {
			if (!url) {
				return segment;
			}
			
			if ("/" == url.charAt(url.length - 1)) {
				url = url.substring(0, url.length - 1);
			}
			
			if ("/" == segment.charAt(0)) {
				segment = segment.substring(1);
			}
			
			var lowerUrl = url.toLowerCase();
			var lowerSegment = segment.toLowerCase();
			
			if (lowerSegment == lowerUrl.substr(lowerUrl.lastIndexOf("/") + 1)) {
				return url;
			}
			
			return url + "/" + segment;
		}
	});
	
	Server = $.fm.Server = new ServerClass();
	
	
	$.fm.RootParentClass = Object.subClass({
		init: function() {
			this.controls = [];
			this.win = $(window);
			this.document = $(document);
			this._width = this.win.width();
			this._height = this.win.height();
		},
		
		width: function() {
			return this._width;
		},
		
		height: function() {
			return this._height;
		},
		
		append: function(element) {
			if (!this.body) {
				this.body = $('body');
				var me = this;
				this.document.click(function() {
					me.notifyAll();
				});				
			}
		
			this.body.append(element);
		},
		
		register: function(control) {
			this.controls.push(control);
		},
		
		notifyAll: function() {
			for (var i = 0 ; i < this.controls.length; i++) {
				this.notifyOne(this.controls[i]);
			}
		},
		
		notifyOne: function(control) {
			if (control && control.onNotify) {
				control.onNotify.call(control, "rootClick");
			}
		}
	});
	RootParent = new $.fm.RootParentClass();

	
	// ================================Control（未初始化）===================================//
    Control = $.fm.Control = Object.subClass({
    	
        init: function(options) {
    		if (prototyping) {
    			return;
    		}
    		
    		this.eventListener = {};
			Object.init.call(this, options);
			
			// 创建画布
    		this.createCanvas(options);
    	},
    	
    	createCanvas: function(options) {
    		if (!this.element) {
    			this.canvas = $("body");
    			return;
    		}
    		
    		this.getEl();
    		this.canvas = this.element;
    	},
    	
    	averageTo: function(total, size) {
    		if (size <= 0) {
    			return null;
    		}
    		
    		var result = new Array(size);
    		var max = size - 1;
    		var value = Math.floor(total / size);
    		var sum = 0;
    		
    		for (var i = 0; i < max; i++) {
    			result[i] = value;
    			sum = sum + value;
    		}
    		
    		result[max] = total - sum;
    		return result;
    	},
    	
		getEl: function(el) {
			if (el) {
	    		if (typeof el == "string") {
	    			if ("#" != el.substring(0, 1)) {
	    				el = "#" + el;
	    			}
	    				
	    			return $(el);
	    		}
	    		return el;
			}
			else {
				var element = this.element;
				
	    		if (typeof element == "string") {
	    			if ("#" != element.substring(0, 1)) {
	    				element = "#" + element;
	    			}
	    				
	    			this.element = $(element);
	    			return this.element;
	    		}
	    		return this.element;
			}
		},
		
		registEventListener: function(consumer, event) {
			var listener = this.eventListener[event];
			
			if (!listener) {
				listener = [];
				this.eventListener[event] = listener;
			}
			
			listener.push(consumer);
		},
		
		fire: function(event) {
			var name = event.name;
			var listenerList = this.eventListener[name];
			
			if (!listenerList) {
				return;
			}
			
			for (var i = 0; i < listenerList.length; i++) {
				var listener = listenerList[i];
				if (listener.notify) {
					listener.notify(event);
				}
			}
		}
    });   	
    
	// ========================URLParameterClass==================================================
	
	$.fm.URLParameterClass = Object.subClass({
		
		encode: function() {
			var result;
			for (var i = 0; i < arguments.length; i++) {
				var arg = arguments[i];
				if (arg) {
					if (typeof arg == "string") {
						result = result ? result + "&" + encodeURI(arg) : encodeURI(arg);
					}
					else if (arg.toURL) {
						var val = arg.toURL();
						if (val) {
							result = result ? result + "&" + encodeURI(val) : encodeURI(val);
						}
					}
				}
			}
			return result ? "?" + result : "";
		}
	});
	
	URLParameter = $.fm.URLParameter = new $.fm.URLParameterClass();
	
	// ===============================Navigator=================================//
	Navigator = $.fm.Navigator = Control.subClass({
		template: [
		    '<div class="navigator">',
				'<div class="navigator-prior" id="btn_prior"></div>',
					'<input class="navigator-text" value="共0条, 每页10条, 第1页" id="btn_text"></input>',
				'<div class="navigator-next" id="btn_next"></div>',		    
		    '</div>'
		],
		
		init: function(options) {
			this.buttons = {};
			this.page = new $.fm.Page();
			this.subscribers = [];
			
			options = options ? options : {};
			Control.call(this, options);
			
			if (this.control) {
				this.subscribers.push(this.control);
			}
			this.initButtons();
		},
		
		initButtons: function() {
			var me = this;
			this.body = $(this.template.join(""));
			var buttons = this.buttons;
			
			buttons.prior = $("#btn_prior", this.body);
			buttons.prior.click(function() {
				if (!me.url) {
					return;
				}
				
				me.page.prior();
				me.moveto();				
			});
			
			buttons.next = $("#btn_next", this.body);
			buttons.next.click(function() {
				if (!me.url) {
					return;
				}
				
				me.page.next();
				me.moveto();
			});
			
			this.text = $("#btn_text", this.body);
			this.text.val(this.page.toText());
			
			this.canvas.append(this.body);
		},
		
		checkStatus: function() {
			// var buttons = this.buttons;
			// this.setStatus(buttons.prior, this.page.isBof());
			// this.setStatus(buttons.next, this.page.isEof());
		},
		
		open: function() {
			this.moveto();
		},
		
		moveto: function() {
			var me = this, param = null;
			
			if (me.filter) {
				param = me.filter.toURL(this.page);
			}
			
			if (me.param) {
				param = param ? param + "&" + me.param : me.param;
			}
			
			Server.getData(this.url, param, function(data, page) {
				var offset = 0;
				if (page) {
					offset = page.pagesize * Math.max((page.pageno - 1), 0);
				};
				
				me.data = data;
				me.page.setData(page);
				
				me.text.val(me.page.toText());
				me.checkStatus();
				me.notifySubscribers(offset);					
			});			
		},
		
		notifySubscribers: function(offset) {
			for (var i = 0; i < this.subscribers.length; i++) {
				var subscriber = this.subscribers[i];
				
				if (subscriber.loadData) {
					subscriber.loadData(this.data, offset);
				}
			}
		}
	});
	
	// ================================page===================================//
	$.fm.Page = Object.subClass ({
		init: function(options) {
			if (prototyping) {
				return;
			}
			
			this.recordCount = 0;
			this.beginRecordNo = 0;
			this.pageSize = 8;
			this.endRecordNo = 0;
			this.pageCount = 0;
			this.pageNo = 1;
			
			Object.init.call(this, options);
    	},
    	
    	setData: function(data) {
    		if (!data) {return;}
    		this.recordCount = data.recordcount || this.recordCount;
    		this.pageSize = data.pagesize || this.pageSize;
    		this.pageNo = data.pageno || this.pageNo;
    		
    		if (this.recordCount > 0) {
    			this.pageCount = Math.ceil(this.recordCount / this.pageSize);
    			this.beginRecordNo = (this.pageNo - 1) * this.pageSize + 1;
    			this.endRecordNo = Math.min((this.beginRecordNo + this.pageSize - 1), this.recordCount); 
    		}
    	},
    	
    	first: function() {
    		if (this.recordCount > 0) {
    			this.beginRecordNo = 1;
    			this.pageNo = 1;
    		}
    	},
    	
    	prior: function() {
    		if (this.pageNo > 1) {
    			this.pageNo--;
    			this.beginRecordNo = this.beginRecordNo - this.pageSize;
    			this.endRecordNo = this.endRecordNo - this.pageSize;
    		}
    	},
    	
    	next: function() {
    		if (this.pageNo < this.pageCount) {
    			this.pageNo++;
    			this.beginRecordNo = this.beginRecordNo + this.pageSize;
    			this.endRecordNo = this.endRecordNo + this.pageSize;
    		}    		
    	},
    	
    	last: function() {
    		if (this.count > 0) {
    			this.beginRecordNo = (this.pageCount - 1) * this.pageSize + 1;
    			this.endRecordNo = this.count;
    			this.pageNo = this.pageCount;
    		}
    	},
    	
    	setPageNo: function(pageNo) {
    		if (this.pageNo != pageNo) {
    			if (pageNo > this.pageCount) {
    				pageNo = this.pageCount;
    			}
    			
    			if (pageNo < 1) {
    				pageNo = 1;
    			}
    			
    			this.pageNo = pageNo;
    			this.beginRecordNo = (pageNo - 1) * this.pageSize + 1;
    			this.endRecordNo = this.beginRecordNo + this.pageSize;
    		}
    	},
    	
    	isBof: function() {
    		
    	},
    	
    	isEof: function() {
    		
    	},
    	
    	toText: function() {
    		return "共" + this.recordCount + "条记录, 每页" + this.pageSize + "条,第" + this.pageNo + "页";
    	},
    	
    	toURL: function() {
    		return "pagesize=" + this.pageSize + "&pageno=" + this.pageNo;
    	}
    });
	
	
	// ================================Mask===================================//
	Mask = $.fm.Mask = Control.subClass({
		template: [
		    '<div class="mask" style="position: fixed; z-index: 0; top: 0; left: 0; right: 0; bottom: 0;" />',
			'</div>'
		],
	
		init: function(options) {
			options = options ? options : {};
			Control.call(this, options);
			
			this.mask = $(this.template.join(""));
			this.canvas.append(this.mask);
		}
	});
	
	// ================================Message===================================//
	Message = $.fm.Message = Control.subClass({
		template: [
		    '<div align="center" class="mask" style="position: fixed; z-index: 0; top: 0; left: 0; right: 0; bottom: 0;" />',
		    	'<div id="message" class="message" style="position: fixed; z-index: 100; left: 30px; right: 30px;" />',
			'</div>'
		],
	
		init: function(options) {
			options = options ? options : {};
			Control.call(this, options);
			
			this.mask = $(this.template.join(""));
			this.message = $("#message", this.mask);
			
			this.canvas.append(this.mask);
		},
		
		show: function(text) {
			this.message.html(text);
			this.mask.show();
		}
	});
	
	
	// ================================Window===================================//
	Win = {};
		
	$.fm.WinClass = Control.subClass({
		template: [
		    '<div class="win_mask"></div>',
		    '<div align="center" class="win-content" id="win-content">',
		    	'<iframe class="win-iframe" id="win-iframe"/></iframe>',
		    '</div>'
		],
	
		init: function(options) {
			options = options ? options : {};
			Control.call(this, options);
			this.zindex = 1000;
			
			this.observers = [];
			this.winList = {};
			this.activeWin = [];
		},
		
		getWinItem: function(url) {
			if (!url) return;
			
			var pos = url.lastIndexOf("/");
			var name = url.substring(pos + 1);
			pos = name.indexOf(".");
			name = name.substring(0, pos);
			this.winList[name] = "";	
			
			var item = this.winList[name];

			if (!item) {
				item = {"name": name};
				this.winList[name] = item;
				 
				item.element = $(this.template.join(""));
				item.content = $("#win-content", item.element);
				
				item.iframe = $("#win-iframe", item.element);
				item.iframe.attr("src", url + "?" + Math.random());
				
				this.canvas.append(item.element);
			}
			else {
				item.created = true;
			}
			
			item.element.css("z-index", this.zindex++);
			
			this.activeWin.push(item);
			return item;
		},
		
		popup: function(config) {
			var url = "string" == typeof config ? config : config.url;
			var winItem = this.getWinItem(url);
			
			winItem.iframe.css({
			    "transform": "translateY(0)",
			    "background": "rgba(255, 255, 255, 1)",
				"margin-top": config.top || "10%",
				"width": config.width || "30%",
				"height": config.height || "52%"
			});
			
			this.show(winItem, url, config);
		},
		
		open: function(config) {
			var url = "string" == typeof config ? config : config.url;
			var winItem = this.getWinItem(url);
			
			winItem.iframe.css({
				"background-color": "white"
			});
			
			this.show(winItem, url, config);
		},
		
		show: function(winItem, url, config) {
			$.extend(winItem, config);
			
			winItem.element.show();
			
			if (config.sender) {
				this.notifyObservers(winItem.id, config.sender);
			}
			
			if (winItem.created && winItem.onShowFunc) {
				winItem.onShowFunc(config.data);
			}
			
			return winItem;			
		},
		
		notifyObservers: function(id, sender) {
			if (id) {
				for (var i = 0; i < this.observers.length; i++) {
					var observer = this.observers[i];
					if (observer.onWinShow && (observer != sender)) {
						observer.onWinShow(id);
					}
				}
			}
		},
		
		registObserver: function(observer) {
			if (observer) {
				this.observers.push(observer);
			}
		},
		
		close: function(data) {
			var winItem = this.activeWin.pop();
			
			if (winItem) {
				if (winItem.beforeClose) {
					winItem.beforeClose(data);
				}
				
				if (winItem.callback) {
					if (winItem.callbackReciver) {
						winItem.callback.call(winItem.callbackReciver, data);
					}
					else {
						winItem.callback(data);
					}
				}
				
			//	winItem.element.hide();
				
				winItem.element.remove();
				
				if (winItem.afterClose) {
					winItem.afterClose(data);
				}
			}
		},
		
		getData: function() {
			if (this.activeWin.length == 0) {return;};
			
			var winItem = this.activeWin[this.activeWin.length - 1];
			if (winItem) {
				return winItem.data;
			}
		},
		
		onShow: function(func) {
			if (this.activeWin.length == 0) {return;};
			
			var winItem = this.activeWin[this.activeWin.length - 1];
			if (winItem) {
				winItem.onShowFunc = func;
				func(winItem.data);
				return;
			}
		}
	});
	
	Win.getInstance = function(config) {
		if (this.instance) {
			return this.instance;
		}
		
		if (window.top.Win.instance) {
			return window.top.Win.instance;
		}
		
		this.instance = window.top.Win.instance = new window.top.$.fm.WinClass(config);
		return this.instance;		
	};
	
	Win.init = function(config) {
		instance = this.getInstance(config);
	};
	
	Win.popup = function(config) {
		var instance = this.getInstance();
		instance.popup(config);
	};
	
	Win.open = function(config) {
		var instance = this.getInstance();
		instance.close();
		instance.open(config);
	};
	
	Win.close = function(data) {
		var instance = this.getInstance();
		instance.close(data);
	};
	
	Win.getData = function() {
		var instance = this.getInstance();
		return instance.getData();
	};
	
	Win.onShow = function(func) {
		var instance = this.getInstance();
		
		if (instance.onShow) {
			instance.onShow(func);
		}
	};
	
	Win.registObserver = function(observer) {
		var instance = this.getInstance();
		instance.registObserver(observer);
	};
	
	
	// ================================Dialog===================================//
	Dialog = {};
		
	$.fm.DialogClass = Control.subClass({
		template: [
		     '<div class="win_mask"></div>',
		     '<div class="dialog_mask" style="display: none; z-index: 1001;" align="center">',
		     '<div class="dialog">',
		    	'<div class="dialog_header" id="title">',
		    		'信息提示',
		    	'</div>',
		    	'<div class="dialog_body" id="dialog_body">',
		    		'<div class="dialog_content" id="dialog_content">',
		    			'<div class="dialog_image" id="image"></div>',
		    			'<span id="body_title"></span>',
		    			'<div class="dialog_text" id="text"></div>',
		    		'</div>',
		    	'</div>',
		    	'<div class="dialog_footer" id="dialog_footer">',
		    		'<button class="button btn_light" id="btn_cancel">取消</button>',
		    		'<button class="button btn_blue" id="btn_ok" style="margin-left:8px">确定</button>',
		    	'</div>',			
		    '</div>',
		    '</div>'
		],
	
		init: function(options) {
			options = options ? options : {};
			Control.call(this, options);
			
			this.createEl();
		},
		
		createEl: function() {
			var me = this;
			var el = this.el = $(this.template.join(""));
			
			this.dialog = $(".dialog", el);
			this.title = $("#title", el);
			this.body = $("#dialog_body", el);
			this.body_title = $("#body_title", el);
			this.dialog_content = $("#dialog_content", el);
			this.dialog_footer = $("#dialog_footer", el);
			
			this.image = $("#image", el);
			this.text = $("#text", el);
			this.btn_cancel = $("#btn_cancel", el);
			this.btn_ok = $("#btn_ok", el);
			
			this.btn_cancel.click(function() {
				me.el.hide();
				if (me.callback) {
					me.callback(false);
				}
			});
			
			this.btn_ok.click(function() {
				me.el.hide();
				if (me.callback) {
					me.callback(true);
				}
			});
			
			this.canvas.append(this.el);
		},
		
		databox: function(title, callback, config) {
			var input = config.input || "";
			var body_title = config.body_title || "";
			
			if (config.onShow) {
				onShow();
			}
			
			this.dialog.removeAttr("class");
			this.dialog.addClass("dialog_databox");
			
			this.title.html(title);
			this.body.removeAttr("class");
			this.dialog_content.removeAttr("class");
			this.image.removeAttr("class");
			this.text.removeAttr("class");
			this.dialog_footer.removeAttr("class");
			this.body.addClass("databox_body");
			this.dialog_content.addClass("databox_content");
			this.image.addClass("databox_image");
			this.text.addClass("databox_text");
			this.dialog_footer.addClass("databox_footer");
			
			this.body_title.html(body_title);
			this.text.html(input);
			this.btn_cancel.show();			
			
			this.callback = callback;
			
			this.el.show();
		},
		
		confirm: function (title, text, callback, but_btn_cancel, but_btn_ok) {
			this.title.html(title);
			this.text.html(text);
			
			this.btn_cancel.html(but_btn_cancel ? but_btn_cancel : "取消");
			this.btn_ok.html(but_btn_ok ? but_btn_ok : "确定");
			
			this.image.removeClass("icon_alert");
			this.image.addClass("icon_confirm");
			this.btn_cancel.show();
			
			this.callback = callback;	
			
			this.el.show();
		},
		
		alert: function (title, text1, text2, callback, but_btn_ok) {
			this.title.html(title);
			this.btn_ok.html(but_btn_ok ? but_btn_ok : "确定");
			
			if (text2) {
				this.text.html(text1 + "<br>" + text2);
				this.text.css("line-height", "23px");
			}
			else {
				this.text.html(text1);
				this.text.css("line-height", "36px");
			}
			
			this.image.removeClass("icon_confirm");
			this.image.addClass("icon_alert");			
			this.btn_cancel.hide();
			
			this.callback = callback ? callback : null;
			
			this.el.show();
		}
	});		
	
	Dialog.databox = function(title, callback, config) {
		config = config || {};
		
		if (!this.instance) {
			this.instance = new $.fm.DialogClass();
		}
		this.instance.databox(title, callback, config);
	};
	
	Dialog.confirm = function(title, text, callback, but_btn_cancel, but_btn_ok) {
		if (!this.instance) {
			this.instance = new $.fm.DialogClass();
		}
		this.instance.confirm(title, text, callback, but_btn_cancel, but_btn_ok);
	};
	
	Dialog.alert = function(text1, text2, callback, but_btn_ok) {
		if (!this.instance) {
			this.instance = new $.fm.DialogClass();
		}
		this.instance.alert("提醒", text1, text2, callback, but_btn_ok);
	};
	
	Dialog.wait = function(text) {
		if (!this.instance) {
			this.instance = new $.fm.DialogClass();
		}
		this.instance.wait("提醒", text1, text2);		
	};

	
	// ================================Loading===================================//
	Loading = {};
		
	$.fm.LoadingClass = Control.subClass({
		template: [
		    '<div class="loading_mask" style="display: none" align="center">',
			    '<div id="area" class="loading_area" align="center">',
			    	'<div class="loading_image"></div>',
			    '</div>',
		    '</div>'
		],
		itemTemplate: '<div class="loading_text"></div>',
		itemList: {},
	
		init: function(options) {
			options = options ? options : {};
			Control.call(this, options);
			this.canvas = $(window.top.window.document.body);
			
			this.createEl();
		},
		
		createEl: function() {
			var el = this.el = $(this.template.join(""));
			this.area = $("#area", el);
			this.canvas.append(this.el);
		},
		
		show: function (code, text) {
			text = text || code;
			code = code || "item";
			
			var item = this.itemList[code];
			
			if (!item) {
				item = this.itemList[code] = $(this.itemTemplate);
				this.area.append(item);
			}
			
			item.html(text);
			this.el.show();
		},
		
		hide: function(code) {
			if (code) {
				var item = this.itemList[code];
				if (item) {
					item.remove();
				}
				
				for (var n in this.itemList) {
					return;
				}
				
				this.el.hide();
				return;
			}
			
			this.el.hide();
			
			for (var n in this.itemList) {
				var item = this.itemList[n];
				if (item.remove) {
					item.remove();
				}
			}
			this.itemList = {};
		}
	});		
	
	Loading.show = function(code, text) {
		var instance = window.top.window.Loading.instance || this.instance;
		
		if (!instance) {
			instance = this.instance = new $.fm.LoadingClass();
		}
		
		instance.show(code, text);
	};
	
	Loading.hide = function(code) {
		var instance = window.top.window.Loading.instance || this.instance;
		
		if (!instance) {
			instance = this.instance = new $.fm.LoadingClass();
		}
		
		instance.hide(code);
	};

	$(window.document).ready(function() {
		Loading.hide();
	});

	
	// ================================FileUploader===================================//
	
	FileUploader = $.fm.FileUploader = Object.subClass({

		init: function(options) {
    		if (prototyping) {
    			return;
    		}
    		
    		this.pos = -1;
    		this.fileList = [];
    		
    		Object.init.call(this, options);
		},
		
		uploadFiles: function(fileList, callback) {
			if (!fileList || fileList.length == 0) { return; }
			
			this.pos = 0;
			this.fileList = fileList;
			this.callback = callback;
			this.tryUploadOneFile();
		},
		
		tryUploadOneFile: function() {
			if (this.pos >= this.fileList.length) {
				if (this.onComplete) {
					this.onComplete();
				}
				if (this.callback) {
					this.callback();
				}
				this.fileList = [];
				return;
			} 
			
			var file = this.fileList[this.pos];
			this.uploadOneFile(file, this.pos, this.fileList.length);
			
			this.pos++;
		},
		
		uploadOneFile: function(file, index, count) {
			var me = this;
			var xhr = new XMLHttpRequest();
			
			if (this.onProgress) {
			    xhr.upload.addEventListener("progress",	 function(e) {
			    	me.onProgress(file, e.loaded, e.total);
			    }, false);
			}
			
		    xhr.addEventListener("load", function(e){
		    	me.afterLoadOneFile.call(me, true, file, xhr.responseText);
		    }, false);
	
		    xhr.addEventListener("error", function(e){
		    	me.afterLoadOneFile.call(me, false, file, xhr.responseText);
		    }, false);
			
			var formdata = new FormData();
			
			this.sendData(xhr, formdata, file, index, count);
		},
		
		afterLoadOneFile: function(success, file, responseText) {
			var object, me = this;
			try {
				object = JSON.parse(responseText);
			}
			catch (e) {
				object = {};
			}
			
			if (this.onSuccess) {
				this.onSuccess(success, file, object, function() {
					me.tryUploadOneFile();
				});
			}
			else {
				this.tryUploadOneFile();
			}
		},
		
		sendData: function(xhr, formdata, file) {
			formdata.append("fileList", file);
			
			xhr.open("POST", this.url, true);
			xhr.send(formdata);
		}
	});	
	
	
	// ================================DateTime===================================//
	DateTime = $.fm.DateTime = Object.subClass({
		
		init: function(millisecond) {
    		if (prototyping) {
    			return;
    		}
    		 
    		this.date = millisecond ? new Date(millisecond) : new Date();
    		this.parseField();
    	},
    	
    	parseField: function() {
			this.year = this.date.getFullYear();
			this.month = this.date.getMonth() + 1;
			this.day = this.date.getDate();
			this.week = this.date.getDay();
			this.str = this.year + (this.month <10 ? "-0" : "-") + this.month + (this.day <10 ? "-0" : "-") + this.day;
			this.ms = Date.parse(this.date);
    	},
    	
    	clone: function() {
    		var result = new DateTime(this.ms);
    		return result;
    	},
    	
    	setDate: function(value) {
    		this.date.setDate(value);
    		this.parseField();
    	},
    	
    	incDate: function() {
    		this.ms = this.ms + 1000 * 60 * 60 * 24;
    		this.date = new Date(this.ms);
    		this.parseField();
    	},
    	
    	incMonth: function(value) {
    		if (value > 0) {
    			var beginMonth = this.date.getMonth(), beginDate = this.day, beginYear = this.year;
    			var currMonth = this.date.getMonth(), currDate = 1, currYear = this.date.getFullYear();
    			
    			while ((currYear - beginYear) * 12 + (currMonth - beginMonth) < value) {
    				this.ms = this.ms + 1000 * 60 * 60 * 24;
    				this.date = new Date(this.ms);
    				currMonth = this.date.getMonth();
    				currYear = this.date.getFullYear();
    			}
    			
    			while (currDate < beginDate) {
    				this.ms = this.ms + 1000 * 60 * 60 * 24;
    				this.date = new Date(this.ms);
    				currDate = this.date.getDate();
    			}
        		
    			this.parseField();
    		}
    		else if (value < 0) {
    			var beginMonth = this.date.getMonth(), beginDate = this.day, beginYear = this.year;
    			var currMonth = this.date.getMonth(), currYear = this.date.getFullYear();
    			
    			while (((currYear - beginYear) * 12 + (currMonth - beginMonth)) > value) {
    				this.ms = this.ms - 1000 * 60 * 60 * 24;
    				this.date = new Date(this.ms);
    				currMonth = this.date.getMonth();
    				currYear = this.date.getFullYear();
    			}
    			
    			currDate = this.date.getDate();
    			
    			while (currDate > beginDate) {
    				this.ms = this.ms - 1000 * 60 * 60 * 24;
    				this.date = new Date(this.ms);
    				currDate = this.date.getDate();
    			}
        		
    			this.parseField();    			
    		}
    	},
    	
    	toMonthBegin: function() {
    		this.date.setDate(1);
    		this.date.setHours(0, 0 , 0, 1);
    		this.parseField();
    	},
    	
    	toMonthEnd: function() {
    		this.date.setDate(1);
    		this.date.setHours(0, 0 , 0, 1);
    		
    		var month = this.date.getMonth();
    		if (month < 11) {
    			this.date.setMonth(month + 1);
    		}
    		else {
    			this.date.setYear(this.date.getFullYear() + 1);
    			this.date.setMonth(0);
    			this.date.setDate(1);
    		}
    		
    		this.parseField();  		
    	},
    	toFormerMonth: function() {
    		this.date.setDate(1);
    		this.date.setHours(0, 0 , 0, 1);
    		
    		var month = this.date.getMonth();
    		var year =  this.date.getFullYear();
    		if (month !=0) {
    			this.date.setMonth(month - 1);
    		}
    		else {
    			this.date.setYear(this.date.getFullYear());
    			this.date.setMonth(0);
    			this.date.setDate(1);
    		}
    		
    		this.parseField();  		
    	},
    	toStr: function() {
    		return this.year + (this.month <10 ? "-0" : "-") + this.month + (this.day <10 ? "-0" : "-") + this.day;
    	}
	});
	
	// ================================Filter===================================//
	Filter = $.fm.Filter = Control.subClass ({
		init: function(options) {
			this.links = [];
			this.defaultValue = "";
			
			Control.call(this, options);
			this.createLink();
		},
		
		createLink: function() {
			if (!this.element) {
				return;
			}
			
			this.linkElements("input");
			this.linkElements("select");
		},
		
		addLink: function(item) {
			if ($.isArray(item)) {
				for (var i = 0; i < item.length; i++) {
					this.links.push(item[i]);
				}
				return;
			}
			this.links.push(item);
		},
		
		linkElements: function(elName) {
			var els = $(elName, this.element);
			
			for (var i = 0; i < els.length; i++) {
				var el = $(els.get(i));
				
				var fieldName = el.attr("field");
				if (!fieldName) { fieldName = el.attr("name");}
				
				if (!fieldName) {
					continue;
				}
				
				//fieldName = fieldName.replace(" ", "").replace(",", ";");
				fieldName = fieldName.replace(" ", "");
				el.fieldNames = fieldName.split(";");
				el.operator = el.attr("operator");
				
				this.links.push(el);
			}			
		},
		
		clear: function() {
			for (var i = 0; i < this.links.length; i++) {
				var link = this.links[i];
				if (link.val) {
					if(link[0].tagName == "SELECT") {
						link.val("Empty");
					}else{
						link.val("");
					}
				}
				else if (link.clearSelected) {
					link.clearSelected();
				}
			}
		},
		
		toStringValue: function(prefix, value) {
			if (prefix) {
				return prefix + encodeURI(value);
			}
			else {
				return encodeURI(value);
			}
		},
		
		toQuotedStringValue: function(prefix, value) {
			if (prefix) {
				return prefix + encodeURI("'" + value + "'");
			}
			else {
				return encodeURI("'" + value + "'");
			}
		},
		
		toResult: function(page, func, prefix, linkOperator, subLinkOperator, likeOperator) {
			var result = "";
			
			var filter = this.defaultValue;
			
			for (var i = 0; i < this.links.length; i++) {
				var link = this.links[i];
				filter = this.toOneLinkResult(link, func, linkOperator, subLinkOperator, likeOperator, filter);
			}
				
			if (filter) {
				result = prefix ? prefix + filter : filter;
			}	
			
			if (page) {
				result = result ? result + "&" + page.toURL() : page.toURL();
			}
			
			return result;			
		},
		
		toOneLinkResult: function(link, func, linkOperator, subLinkOperator, likeOperator, filter) { 
			var segment = ""; var empty = (filter == ""); var quoted = false;
			
			var fieldNames = link.fieldNames;
			
			for (var i = 0; i < fieldNames.length; i++) { 
				var fieldName = fieldNames[i];
				
				if (link.toResult) {
					segment = link.toResult(linkOperator);
				}
				else {
					var value = link.val();
					if (!value || value.toLowerCase() == "empty") {
						continue;
					};
					
					if (link["operator"] == "like") {
						segment = func(likeOperator, "%" + value + "%");
					}
					else if (link["operator"] == "min") {
						segment = func(">=", value);
					}
					else if (link["operator"] == "max") {
						segment = func("<=", value);
					}
					else {
						segment = func("=", value);
					}
				}
				
				if (!segment || segment == "") {
					continue;
				}
				
				if (i == 0) {
					if (!empty) {
						filter = filter + linkOperator;
					}
					
					if (fieldNames.length > 1) {
						filter = filter + " (";
						quoted = true;
					}
				}
				else {
					filter = filter + subLinkOperator;
				}
				
				filter = filter + fieldName + segment;					
			}
			
			if (quoted) {
				filter = filter + ")";
			}

			return filter;
		},
		
		toURL: function(page) {
			return this.toResult(page, this.toStringValue, null, "&", "&", "=");
		},
		
		toFilter: function(page) {
			return this.toResult(page, this.toQuotedStringValue, "filter=", " and ", " or ", " like ");
		},
		
		getValue: function(fieldName) {
			for (var i = 0; i < this.links.length; i++) {
				var link = this.links[i];
				if (link.fieldName == fieldName) {
					return link.val();
				}
			}
		}
	});
	

	
})(jQuery);
