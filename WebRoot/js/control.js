(function ($) {
	//================================ControlCreator===================================//
	ControlCreatorClass = $.fm.ControlCreatorClass = Object.subClass({
		template: {
			"input": "<input id='input' type='text' class='popup_input'/>",
			"month": "<input id='input' type='month' class='popup_input'/>",
			"date": "<input id='input' type='date' class='popup_input'/>",
			"datetime": "<input id='input' type='datetime' class='popup_input'/>",
			"time": "<input id='input' type='time' class='popup_input'/>",
			"number": "<input id='input' type='number' class='popup_input'/>",
			"select": "<select ></select>",
			"label": "<label />"
		},
	
		create: function(config, parent, cell) {
			config.type = config.type || "text";
			var code = config.type.toLowerCase();
			
			var result = null;
			
			if ("select" == code) {
				result =  this.createSelect(config, cell);
			}
			else if ("popup" == code) {
				result =  this.createPopup(config);
			}
			else if ("textarea" == code) {
				result =  this.createTextarea(config);
			}
			else {
				result =  this.createInput(config);
			}
			
			if (result.publishEvents) {
				result.publishEvents(parent);
			}
			
			return result;
		},
		
		createInput: function(config) {
			var editor = new Input(config);
			return editor;
		},
		
		createSelect: function(config, cell) {
			var editor = new Select(config);
			
			if (config.dictionary) {
				Server.getData("root/dictionary/" + config.dictionary, null, function(data) {
					editor.loadData(data);
				});
			}
			else if (config.datasource) {
				Server.getData("root/data/procedure/" + config.datasource + "/dataset", null, function(data) {
					editor.loadData(data);
				});
			}
			else if (config.getdata) {
				var filter = null;
				
				if (config.filterkey && cell.row.data) {
					var SelectedRow = cell.row.data.record;
					var filterkeys = config.filterkey.split(",");
					for (var i = 0; i < filterkeys.length; i++) {
						var filterkey = filterkeys[i];
						if (i == 0) {
							filter = filterkey + "=" + encodeURI(SelectedRow[filterkey]);
						}
						else{
							filter += "&" + filterkey + "=" + encodeURI(SelectedRow[filterkey]);
						}
					}
				}
				Server.getData("root/data/procedure/" + config.getdata + "/dataset", filter, function(data) {
					editor.loadData(data);
				});
			}
			
			return editor;
		},
		
		createPopup: function(config) {
			var popup = new PopupEditor(config);
			return popup;
		},
		
		createTextarea: function(config) {
			var textarea = new TextareaEditor(config);
			return textarea;
		}
	});
	
	ControlCreator = $.fm.ControlCreatorClass = new ControlCreatorClass();

	
	// ================================Input===================================//
	Input = $.fm.Input = Control.subClass({
		template: [
		    "<div class='input_editor'>",
		    	"<input id='input' type='@{type}' class='popup_input' style='width: 90%'>",
		    "</div>"
		],		
		template_validator: '<div id="validator" class="Validform_checktip datacard_value_validator"></div>',
		
		init: function(options) {
			this.field = null;
			this.readOnly = false;
			this.type = "text";
			
			Control.call(this, options);
			
			this.createElements();
		},
		
		createElements: function() {
			var me = this;
			
			var template = this.template.join("").replace("@{type}", this.type);
			this.editor = this.datatype ? $(template + this.template_validator) : $(template);
			
			this.input = $("#input", this.editor);
			this.input.attr("datatype", this.datatype);
			this.input.attr("errormsg", this.errormsg);
			this.input.attr("nullmsg", this.nullmsg);
			
			if (!this.datatype) {
				this.input.css("width", "100%");
			}
			
			if (this.type == "date" || this.type == "month") {
				var initdate = new Date;
				if (this.datemode == "basics") {
					
				}
				else {
					if (this.datetype == "endyear") {
						initdate.setMonth(11, 31);
					}
					
					if (this.datetype == "beginyear") {
						initdate.setMonth(0, 1);
					}
					this.input.val(initdate.format("yyyy-MM-dd"));
					this.input.addClass("medate");
				}
				
				/*if (BrowserType().type == "IE") {
					this.input.bind('propertychange', function(){
						if (me.onValueChange) {
							me.onValueChange();
						}
					});
				}
				else {
					this.input.bind('oninput', function(){
						if (me.onValueChange) {
							me.onValueChange();
						}
					});
				}*/
			}
			
			this.input.change(function() {
				if (me.onChange) {
					var event = {
						name: "onChange",
						senderKey: me.index,
						newValue: me.input.val(),
						record: null
					}
					me.fire(event);
				}
				
				if (me.onValueChange) {
					me.onValueChange();
				}
			});
			
			if (this.readonly && "undefined" != typeof this.customized) {
				this.input.val(this.customized);
			}
			
			this.setReadOnly(this.readonly);
		},
		
		appendTo: function(parent) {
			parent.append(this.editor);
		},
		
		publishEvents: function(parent) {
			if (this.onChange) {
				this.registEventListener(parent, "onChange");
			}
		},
		
		setOneColor: function(color) {
			this.input.css("background-color", color);
		},
		
		setValue: function(data) {
			this.input.val(data);
		},
		
		getValue: function() {
			return this.input.val() == "" ? null : this.input.val();
		},
		
		setReadOnly: function(readonly, force) {
			var me = this;
			
			if (readonly) {
				this.input.attr("readonly", "readonly");
				this.input.addClass("readonly");
				this.input.removeClass("medate");
			}
			else {
				this.input.removeAttr("readonly");
				this.input.removeClass("readonly");
			}
			
			if (!force && (typeof this.forceReadonly != "undefined")) {
				return;
			}
			
			this.forceReadonly = force;
			this.readOnly = readonly;
		}
	});
	
	// ================================Textarea===================================//
	TextareaEditor = $.fm.TextareaEditor = Control.subClass({
		template: [
		           "<div class='input_editor'>",
		           "<textarea maxlength='250' id='textarea' type='@{type}' class='popup_input' style='width: 90%'></textarea>",
		           "</div>"
       ],		
       template_validator: '<div id="validator" class="Validform_checktip datacard_value_validator"></div>',
       
       init: function(options) {
    	   this.field = null;
    	   this.readOnly = false;
    	   this.type = "textarea";
    	   
    	   Control.call(this, options);
    	   
    	   this.createElements();
       },
       
       createElements: function() {
    	   var me = this;
    	   
    	   var template = this.template.join("").replace("@{type}", this.type);
    	   this.editor = this.datatype ? $(template + this.template_validator) : $(template);
    	   
    	   this.textarea = $("#textarea", this.editor);
    	   this.textarea.attr("datatype", this.datatype);
    	   this.textarea.attr("errormsg", this.errormsg);
    	   this.textarea.attr("nullmsg", this.nullmsg);
    	   
    	   if (!this.datatype) {
    		   this.textarea.css("width", "100%");
    	   }
    	   
    	   this.textarea.change(function() {
    		   if (me.onChange) {
    			   var event = {
    					   name: "onChange",
    					   senderKey: me.index,
    					   newValue: me.textarea.val(),
    					   record: null
    			   }
    			   me.fire(event);
    		   }
    		   
    		   if (me.onValueChange) {
    			   me.onValueChange();
    		   }
    	   });
    	   
    	   if (this.readonly && "undefined" != typeof this.customized) {
    		   this.textarea.val(this.customized);
    	   }
    	   
    	   this.setReadOnly(this.readonly);
       },
       
       appendTo: function(parent) {
    	   parent.append(this.editor);
       },
       
       publishEvents: function(parent) {
    	   if (this.onChange) {
    		   this.registEventListener(parent, "onChange");
    	   }
       },
       
       setOneColor: function(color) {
    	   this.textarea.css("background-color", color);
       },
       
       setValue: function(data) {
    	   this.textarea.val(doReplaceSpecialCharacter(data));
       },
       
       getValue: function() {
    	   return this.textarea.val() == "" ? null : replaceSpecialCharacter(this.textarea.val());
       },
       
       setReadOnly: function(readonly, force) {
    	   var me = this;
    	   
    	   if (readonly) {
    		   this.textarea.attr("readonly", "readonly");
    		   this.textarea.addClass("readonly");
    		   this.textarea.removeClass("medate");
    	   }
    	   else {
    		   this.textarea.removeAttr("readonly");
    		   this.textarea.removeClass("readonly");
    	   }
    	   
    	   if (!force && (typeof this.forceReadonly != "undefined")) {
    		   return;
    	   }
    	   
    	   this.forceReadonly = force;
    	   this.readOnly = readonly;
       }
	});
	
	// ================================Select===================================//
	Select = $.fm.Select = Control.subClass({
		template: "<div class='input_editor'><select id='text'></select></div>",
		template_validator: '<div id="validator" class="Validform_checktip datacard_value_validator" style="right: 18px;"></div>',
		
		init: function(options) {	
			this.keyField = "code";
			this.valueField = "name";
			this.existsEmpty = true;
			this.loaded = false;
			
			Control.call(this, options);
			this.createElement();
			
			if (this.data && this.onRender) {
				this.loadData(this.data);				
			}
			
			if(this.url){
				this.getData(this.url);
			}
		},	
		
		createElement: function() {
			var me = this;
			if (this.element) {
				this.input = this.element;
				return;
			}
			this.element = this.datatype ? $(this.template + this.template_validator) : $(this.template);
			
			this.input = $("#text", this.element);
			
			this.input.attr("datatype", this.datatype);
			this.input.attr("errormsg", this.errormsg);	
			this.input.attr("nullmsg", this.nullmsg);	
			
			this.input.on("change", function() {
				if (me.onChange) {
					var event = {
						name: "onChange",
						senderKey: me.index,
						newValue: me.input.val(),
						record: me.input.find("option:selected").text()
					}
					me.fire(event);
				//	me.onChange(me.input.val(), me.input.find("option:selected").text());
				}
				
				if (me.onValueChange) {
					me.onValueChange();
				}
			});
		},
		
		setURL: function(url) {
			this.url = url;
			this.getData(this.url);
		},
		
		getData: function(url){
			var me = this;
			Server.getData(url, function(result){
				me.loadData(result);
				if (me.defaultValue) {
					me.input.val(me.defaultValue);
				}
    		});
		},
		
		loadData: function(data) {
			this.data = data;
			this.input.empty();
			
			if (this.existsEmpty) {
				this.input.append($("<option value=\"empty\" >---</option>"));
			}
			
			if (data && data.length) {
				for (var i = 0; i < data.length; i++) {
					var line = data[i];
					this.option = $("<option></option>");
					
					if (this.onRender) {
						this.onRender(line, option);
					}
					else {
						this.option.val(line[this.keyField]);
						this.option.html(line[this.valueField]);
					}
					
					this.input.append(this.option);
				}
			}
			
			this.loaded = true;
			this.setValue(this.key);
		},
		
		appendTo: function(parent) {
			parent.append(this.element);
		},
		
		publishEvents: function(parent) {
			if (this.onChange) {
				this.registEventListener(parent, "onChange");
			}
		},
		
		onValueChange2: function() {
			var key = this.canvas.val();
			var option = $("option:selected", this.canvas);
			var value = option.html();
			
			var event = {
				name: "onChange",
				senderKey: this.index,
				newValue: key,
				record: {key: key, value: value}
			}
			this.fire(event);
		},
		
		setValue: function(key) {
			key = key || "empty";
			this.key = key;
			
			if (this.loaded) {
				var option = $("option[value='" + key + "']", this.input);
				option.attr("selected", "selected");
			}
		},
		
		setOneColor: function(color) {
			this.input.css("background-color", color);
		},
		
		getValue: function() {
			var val = this.input.val();
			
			if ("empty" == val) {
				return null;
			}
			
			return val;			
		},
		
		selectByText: function(name) {
			var option = $("option[text='" + name + "']", this.input);
			option.attr("selected", "selected");
		},
		
		disable: function() {
			this.input.attr("disabled", "disabled");
		},
		
		enable: function() {
			this.input.attr("disabled", "");
		},
		
		setReadOnly: function(readonly, force) {
			if (!force && (typeof this.forceReadonly != "undefined")) {
				return;
			}
			
			this.forceReadonly = force;
			
			if (readonly) {
				this.input.attr("disabled", "true");
				this.input.addClass("readonly");
			}
			else {
				this.input.removeAttr("disabled");
				this.input.removeClass("readonly");
			}
		},
		
		toURL: function() {
			if (!this.fieldname) { return; }
			
			var value = this.input.val();
			if (value) {
				return this.fieldname + "=" + value;
			}
		},
		
		isEmpty: function() {
			return this.input.val() == "empty";
		},
		
		empty : function() {
			this.input.val("empty");
		}
	});
	
	
	// ================================PopupEditor===================================//
	PopupEditor = $.fm.PopupEditor = Control.subClass({
		template: [
		    "<div class='popup_editor'>",
		    	"<input id='input' type='text' class='popup_input'>",
		    	"<div id='btn' class='popup_button'>",
		    		"<img src='root/image/popup.png' style='width: 16px; margin-top: 5px; vertical-align: top'>",
		    	"</div>",
		    "</div>"
		],
		template_validator: '<div id="validator" class="Validform_checktip datacard_value_validator" style="right: 24px;"></div>',
		
		init: function(options) {
			this.field = null;
			this.readOnly = false;
			
			Control.call(this, options);
			
			this.popupConfig = $.extend({
					width: 600,
					height: 260,
					callback: this.doCallback,
					callbackReciver: this
				}, 
				this.popupConfig
			);
			
			this.createElements();
		},
		
		createElements: function() {
			var me = this;
			
			this.editor = this.datatype ? $(this.template.join("") + this.template_validator) : $(this.template.join(""));
			
			this.input = $("#input", this.editor);
			this.input.attr("datatype", this.datatype);
			this.input.attr("errormsg", this.errormsg);	
			this.input.attr("nullmsg", this.nullmsg);	
			this.input.attr("readonly", "readonly");
			
			this.input.change(function() {
				if (me.onValueChange) {
					me.onValueChange();
				}
			});
			
			if (this.notReadonly) {
				this.input.removeAttr("readonly");
			}
			
			this.button = $("#btn", this.editor);
			
			this.button.click(function() {
				if (!me.readOnly) {
					me.popup();
				}
			});
			
			if (this.element) {
				this.element.append(this.editor);
			}
		},
		
		popup: function() {
			Win.popup(this.popupConfig);
		},
		
		appendTo: function(parent) {
			parent.append(this.editor);
		},
		
		publishEvents: function(parent) {
			if (this.onChange) {
				this.registEventListener(parent, "onChange");
			}
		},
		
		doCallback: function(data) {
			if (!data) {
				return;
			}
			
			if (data.record) {
				data = data.record;
			}
			
			var field = this.popupConfig || this.field;
			var newValue = field ? data[field] : null;
			this.onClose(newValue, data);
		},
		
		onClose: function(newValue, record) {
			var event = {
				name: "onChange",
				senderKey: this.index,
				newValue: newValue,
				record: record
			}
			this.fire(event);
		},
		
		setValue: function(data) {
			this.input.val(data);
		},
		
		setOneColor: function(color) {
			this.input.css("background-color", color);
		},
		
		getValue: function() {
			return this.input.val() == "" ? null : this.input.val();
		},
		
		setReadOnly: function(readonly, force) {
			var me = this;
			
			if (readonly) {
				this.input.addClass("readonly");
			}
			else {
				this.input.removeClass("readonly");
			}			
			
			if (!force && (typeof this.forceReadonly != "undefined")) {
				return;
			}
			
			this.forceReadonly = force;
			this.readOnly = readonly;
		}
	});
	
	// ================================MonthSelector===================================//
	DateEditor = $.fm.DateEditor = Control.subClass({
		init: function(options) {
			this.spliter = "-";
			this.date = new Date(); 
			
			Control.call(this, options);
			
			this.render();
			this.setValue();
		},
		
		render: function() {
			var me = this;
			
			if (this.canvas) {
				this.canvas.change(function() {
					if (me.onValueChange) {
						me.onValueChange();
					}
				});
			}
		},
		
		getYear: function() {
			var value = this.canvas.val();
			
			if (value) {
				array = value.split(this.spliter);
				return array && array.length > 0 ? array[0] : null;
			}
		},
		
		getMonth: function() {
			var value = this.canvas.val();
			
			if (value) {
				array = value.split(this.spliter);
				return array && array.length > 1 ? array[1] : null;
			}			
		},
		
		appendTo: function(parent) {
			parent.append(this.element);
		},

		getValue: function() {
			return this.element.val();	
		},
		
		setValue: function(date) {
			if (!date) {
				date = this.date;
			}
			
			if ("string" == typeof date) {
				var value = date.length < 10 ? date : date.substring(0,10);
				this.element.val(value);
			}
			else {
			  	var year = date.getFullYear();  
				var month = date.getMonth() + 1;   
				month = month < 10 ? "0" + month : month;
			   	
				var value = year + this.spliter + month + this.spliter + "01";
				this.canvas.val(value);				
			}
		},
		
		setReadOnly: function(readonly, force) {
			if (!force && (typeof this.forceReadonly != "undefined")) {
				return;
			}
			
			this.forceReadonly = force;
			
			if (readonly) {
				this.element.attr("readonly", "readonly");
			}
			else {
				this.element.removeAttr("readonly");
			}
		}

	});
	
	// ================================SelectGroup===================================//
	SelectGroup = $.fm.SelectGroup = Control.subClass({
		init: function(options) {	
			this.selects = [];
			
			Control.call(this, options);
			this.linkSelects();
		},	
		
		linkSelects: function() {
			var me = this;
			var els = $("select", this.element);
			
			for (var i = 0; i < els.length; i++) {
				var el = $(els.get(i));
				
				if (this.onchange) {
					el.on("change", function() {
						me.onchange.call(me);
					});
				}

				this.selects.push(el);
			}			
		},
		
		doChange: function() {
			if (this.onchage) {
				this.onchage();
			}
		},
		
		toURL: function() {
			if (!this.fieldname) { return; }
			
			var max = this.selects.length - 1;
			for (var i = max; i >= 0; i--) {
				var select = this.selects[i];
				var value = select.val();
				
				if (value) {
					return this.fieldname + "=" + value;
				}
			}
		},
		
		isEmpty: function() {
			for (var i = 0; i < this.selects.length; i++) {
				var select = this.selects[i];
				var value = select.val();
				
				if (value) {
					return false;
				}
			}
			
			return true;
		}
	});
	
	//===============================checkboxTable=========================================
	checkboxTable = $.fm.checkboxTable = Control.subClass({
		template: [
		    "<table style='width: 100%;'>",
			"</table>"
		],		
		template_tr:[
			"<tr>",
			"</tr>"
   		],
		template_checkbox:[
			"<td>",
				"<label class='radioTable'><input style='vertical-align: -2px;' type='checkbox'/></label>",
			"</td>"
		],
		
		init: function(options) {
			var me = this;
			this.columnsNo = 1;//默认只有一列
			
			Control.call(this, options);
			
			if (this.url) {
				var me = this;
				Server.getData(this.url, function(data) {
					if (me.onGetData) {
						data = me.onGetData(data);
					}
					me.createItems(data);
				});
			}
			else if (this.data) {
				this.createItems(this.data);
			}
		},
		
		createItems: function(data) {
			var me = this; 
			
			if (!data || data.length == 0) {
				return;
			}
			
			this.template =  $(this.template.join(""));
			
			for (var i = 0; i < data.length; i++) {
				var template_tr = $(this.template_tr.join(""));
				for (var idx = 0; idx < this.columnsNo + 1; idx++) {
					var line = data[i];
					var template_checkbox = $(this.template_checkbox.join(""));
					template_checkbox.input = $("input", template_checkbox);
					template_checkbox.label = $("label", template_checkbox);
					template_checkbox.label.append(line.name);
					template_checkbox.input.val(line.code);
					template_checkbox.input.attr("id", line.id);
					
					template_checkbox.input.click(function() {
						me.onSelectFun(this);
					});
					
					if (line.checked == 1) {
						template_checkbox.input.attr("checked", "checked");
					}
					template_tr.append(template_checkbox);
					if (idx == this.columnsNo) {
						i = i-1;
					}
					i < data.length - 1 ? i++ : idx = this.columnsNo + 1;
				}
				
				this.template.append(template_tr);
			}
			this.canvas.append(this.template);
		},
		
		onSelectFun: function(item) {
			if (this.onSelect) {
				var isSelect = item.checked;
				this.onSelect(this, item, isSelect);
			}
		},
		
		setUrl: function(url) {
			var me = this;
			url = encodeURI(url);
			Server.getData(url, function(data) {
				if (me.onGetData) {
					data = me.onGetData(data);
				}
				me.loadData(data);
			});
		},
		
		loadData: function(data) {
			this.createItems(data);
		},
		
		setSelect: function (data) {
			if (data.length > 0) {
				for (var i = 0; i < data.length; i++) {
					var me = this;
					var selectcheckbox = $("#" + data[i], this.canvas);
					selectcheckbox.attr("checked", "checked");
				}
			}
		},
		
		emptySelect: function () {
			var allcheckbox = $("input[type='checkbox']", this.canvas);
			for (var i = 0; i < allcheckbox.length; i++) {
				var checkbox = $(allcheckbox.get(i));
				checkbox.removeAttr("checked");
			}
		},
		
		getSelectedData: function() {
			var me = this;
			var allcheckbox = $("input[type='checkbox']", this.canvas);
			var selectedData = "";
			var idx = 0;
			
			for (var i = 0; i < allcheckbox.length; i++) {
				var checkbox = $(allcheckbox.get(i));
				if (checkbox.is(':checked')) {
					idx ++;
					if (selectedData == "") {
						selectedData = checkbox.attr('value');
					}
					else {
						selectedData += "、" + checkbox.attr('value');
					}
				}
			}
			
			if (idx == allcheckbox.length) {
				selectedData = "allSelectedData";
			}
			
			return selectedData;
		}
	});
	
	//===============================radio=========================================
	radioTable = $.fm.radioTable = Control.subClass({
		template: [
		    "<table style='width: 100%;'>",
			"</table>"
		],		
		template_tr:[
			"<tr>",
			"</tr>"
   		],
		template_radio:[
			"<td>",
				"<label class='radioTable'><input style='vertical-align: -2px;' type='radio'/></label>",
			"</td>"
		],
		
		init: function(options) {
			var me = this;
			this.columnsNo = 1;//默认只有一列
			this.name = "radioname";
			Control.call(this, options);
			
			if (this.url) {
				var me = this;
				Server.getData(this.url, function(data) {
					if (me.onGetData) {
						data = me.onGetData(data);
					}
					me.createItems(data);
				});
			}
			else if (this.data) {
				this.createItems(this.data);
			}
		},
		
		createItems: function(data) {
			var me = this; 
			
			if (!data || data.length == 0) {
				return;
			}
			
			this.template =  $(this.template.join(""));
			for (var i = 0; i < data.length; i++) {
				var template_tr = $(this.template_tr.join(""));
				for (var idx = 0; idx < this.columnsNo + 1; idx++) {
					var line = data[i];
					var template_radio = $(this.template_radio.join(""));
					template_radio.input = $("input", template_radio);
					template_radio.label = $("label", template_radio);
					template_radio.label.append(line.name);
					template_radio.input.val(line.code);
					template_radio.input.attr("name", this.name);
					if (line.remark) {
						template_radio.attr("title", line.remark);
					}
					
					if (line.checked == 1) {
						template_radio.input.attr("checked", "checked");
					}
					template_tr.append(template_radio);
					if (idx == this.columnsNo) {
						i = i-1;
					}
					i < data.length - 1 ? i++ : idx = this.columnsNo + 1;
				}
				
				this.template.append(template_tr);
			}
			this.canvas.append(this.template);
		},
		
		loadData: function(data) {
			this.createItems(data);
		},
		
		emptySelect: function () {
			var allradio = $("input[type='radio']", this.canvas);
			for (var i = 0; i < allradio.length; i++) {
				var radio = $(allradio.get(i));
				radio.removeAttr("checked");
			}
		},
		
		getSelectedData: function() {
			var me = this;
			var allcheckbox = $("input[type='radio']", this.canvas);
			var selectedData = "";
			var idx = 0;
			
			for (var i = 0; i < allcheckbox.length; i++) {
				var checkbox = $(allcheckbox.get(i));
				if (checkbox.is(':checked')) {
					idx ++;
					if (selectedData == "") {
						selectedData = checkbox.attr('value');
					}
					else {
						selectedData += "、" + checkbox.attr('value');
					}
				}
			}
			
			if (idx == allcheckbox.length) {
				selectedData = "allSelectedData";
			}
			
			return selectedData;
		}
	});
	
    // ================================Record===================================//
	Record = $.fm.Record = Control.subClass ({
		init: function(options) {
			this.datalink = {};
			this.labellink = {};
			this.imglink = {};
			this.selectlink = {};
			this.data = {};
			this.editClasss = ["input", "textarea"];
			
			Control.call(this, options);
			
			this.linkTo(this.canvas);
			this.publishData();
		},
		
		linkTo: function(element) {
			if (!element) return;
			
			for (var no in this.editClasss) {
				var editClass = this.editClasss[no];
				var valueEl = $(editClass, element);
				this.doLinkTo(valueEl, this.datalink);
			}
			
			var valueEl = $("select", element);
			this.doLinkTo(valueEl, this.selectlink);
			
			var valueEl = $("label", element);
			this.doLinkTo(valueEl, this.labellink);

			var valueEl = $("div", element);
			this.doLinkTo(valueEl, this.labellink);
			
			var valueEl = $("img", element);
			this.doLinkTo(valueEl, this.imglink);
		},
		
		doLinkTo: function(valueEl, link) {
			for (var i = 0; i < valueEl.length; i++) {
				var item = $(valueEl.get(i));
				var fieldname = item.attr("id");
				
				if (fieldname) {
					fieldname = fieldname.toLowerCase();
					link[fieldname] = item;
				}
			}
			
			this.publishData();
		},
		
		setData: function(data) {
			this.data = data;
			this.publishData();
		},
		
		setValue: function(name, value) {
			this.data[name] = value;
		},
		
		publishData: function() {
			if (!this.data) return;
			
			for (var name in this.datalink) {
				this.datalink[name].val(this.data[name]);
			}
			
			for (var name in this.selectlink) {
				var link = this.selectlink[name];
				var value = this.data[name];
				if (!value) { value = ""; };
				var option = $("option[value='" + value + "']", link);
				option.attr("selected", "selected");
			}
			
			for (var name in this.labellink) {
				var name_data = this.data[name];
				this.labellink[name].html(this.data[name]);
			}
			
			for (var name in this.labellink) {
				var name_data = this.data[name];
				this.labellink[name].html(this.data[name]);
			}
			
			for (var name in this.imglink) {
				this.imglink[name].attr("src", this.data[name]);
			}
		},
		
		setEditable: function(editable) {
			for (var name in this.datalink) {
				if (editable) {
					this.datalink[name].removeAttr("readonly");
				}
				else {
					this.datalink[name].attr("readonly", "readonly");
				}
			} 
		},
		
		getData: function() {
			var result = this.data || {};
			
			for (var name in this.datalink) {
				var link = this.datalink[name];
				var value = link.val();
				
				if (!value || value == "") { continue; };
				result[name] = value;
			}
			
			for (var name in this.selectlink) {
				var link = this.selectlink[name];
				var value = $("option:selected", link).val();
				
				if (!value || value == "") { continue; };
				result[name] = value;
			}
			
			return result;
		},
		
		validate: function() {
			for (var name in this.datalink) {
				var link = this.datalink[name];
				var required = link.attr("require");
				
				if (required) {
					var value = link.val();
					if (!value) {
						Dialog.alert("请填写“" + required + "”");
						return false;
					}
				}
			}
			
			return true;
		}
	});
	
	//================================MenuBar===================================//
	MenuBar = $.fm.MenuBar = Control.subClass({
		template: [
		   	'<div class="bar_item" align="center">',
				'<img id="img" class="bar_item_img">',
				'<div id="txt" class="bar_item_text"></div>',
			'</div>'
		],
		template_left: [
		   	'<div class="bar_item_left"></div>'
		],
				
		init: function(options) {
			var me = this;
			this.items = new Array();
			
			this.defaultIndex = 0;
			this.barClass = "bar";
			this.bgColor = "#EFEFEF";
			this.touchColor = "#F7B3AF";
			this.selectColor = "#F7B3AF";
			this.align = "left";
			
			Control.call(this, options);
			
			this.canvas.addClass(this.barClass);
			
			if (this.url) {
				Server.getData(this.url, function(data) {
					me.createItems(data);
				});
			}
			else if (this.data) {
				this.createItems(this.data);
			}
		},
		
		createItems: function(data) {
			var me = this; 
			
			if (!data || data.length == 0) {
				return;
			}
			
			this.items.left =  $(this.template_left.join(""));
			this.canvas.append(this.items.left);
			
			for (var i = 0; i < data.length; i++) {
				var line = data[i];
				
				//1. create element
				var item = $(this.template.join(""));
				this.items.push(item);
				
				//2. get image
				item.img = $("#img", item);
				item.img.attr("src", line.img);
				
				//3. get text
				item.text = $("#txt", item);
				item.text.html(line.text);
				item.text.addClass(this.unselectClass);
				item.css({"background-color": this.bgColor});
				
				//4. set data
				item.data('el', item);
				item.data('record', line);
				
				//5. event
				item.click(function(e) {
					var sender = $(this);
					var el = sender.data('el');
					me.selectItem(el);
				});
				
				item.on("touchstart", function() {
					var el = $(this); 
					el.css("background-color", this.touchColor);
				});
				
				item.on("touchend", function() {
					var el = $(this);
					var record = el.data("record");
					el.css("background-color", record.bgColor);
				});
				
				//6. add to canvas
				if (this.onRender) {
					this.onRender(line, item, i);
				}
				
				//7. add to canvas
				this.canvas.append(item);
			}
			
			if (this.align == "center") {
				var margin_left = ((5 - this.items.length) * 10) + "%";
				this.items.left.width(margin_left);
			}
			
			this.selectByIndex(this.defaultIndex);
			
			if (this.afterLoad) {
				this.afterLoad(this, this.items);
			}
		},
		
		selectByIndex: function(index) {
			if (index < this.items.length) {
				var item = this.items[index];
				this.selectItem(item);
			}			
		},
		
		selectItem: function(item) {
			if (this.selected) {
				this.selected.css("background-color", this.bgColor);
			}
			
			this.selected = item;
			this.selected.css("background-color", this.selectColor);
			
			if (this.onSelect) {
				var record = item.data('record');
				this.onSelect.call(this, record, item);	
			}
		},
		
		getItemById: function(id) {
			for (var i = 0; i < this.items.length; i++) {
				var item = this.items[i];
				var record = item.data('record');
				
				if (record && record.id == id) {
					return item;
				} 
			}
		},
		
		onWinShow: function(id) {
			var item = this.getItemById(id);
			
			if (item) {
				this.selectItem(item);
			}
		}
	});
	
	
	//================================ListMenu===================================//
	ListMenu = $.fm.ListMenu = Control.subClass({
		template: [
		    '<div align="center" class="listmenu_item">',
				'<div style="width: 185px; height: 40px; margin-top: 15px;">',
					'<div style="float: left; width: 30px">',
						'<img id="image" style="width: 30px; height: 30px">',
					'</div>',
					'<div id="text" class="listmenu_item_text"></div>',
				'</div>',
			'</div>'
		],
	
		init: function(options) {
			options = options ? options : {};
			Control.call(this, options);
			
			if (this.url) {
				var me = this;
				Server.getData(this.url, function(data) {
					me.items = data;
					this.createItems();
				});
			}
			else {
				this.createItems();
			}
		},
		
		createItems: function() {
			var me = this, items = this.items;
			
			if (!items || items.length == 0) {
				return;
			}
			
			if (!this.canvas) {
				return;
			}
			
			for (var i = 0; i < items.length; i++) {
				var itemData = items[i];
				var item = $(this.template.join(""));
				
				item.data('el', item);
				item.data('data', itemData);
				
				item.text = $("#text", item);
				item.image = $("#image", item);
				
				item.text.html(itemData.text);
				item.image.attr("src", itemData.imageurl);				
				
				item.click(function(e) {
					var el = $(this).data('el');
					var data = $(this).data('data');
					me.onItemSelected.call(me, el, data);
				});
				
				this.canvas.append(item);
			}
		},
		
		onItemSelected: function(el, data) {
			if (data && data.url) {
				window.location.href = data.url;
			}
		}
	});
	
	GroupMenu = $.fm.GroupMenu = Control.subClass({
		template: [
		   	'<div id="gm_group" class="gm_group">',
		   		'<div class="gm_header" id="gm_header">',
		   			'<div id="indicator" class="gm_header_indicator"></div>',
		   			'<img id="gm_header_img" class="gm_header_img"/>',
		   			'<div id="gm_header_text" class="gm_header_text"></div>',
		   		'</div>',
			'</div>'
		],
		
		template_item: [
			'<div class="gm_menu" id="gm_menu">',
				'<img id="gm_menu_img" class="gm_menu_img"/>',
				'<div id="gm_menu_text" class="gm_menu_text"></div>',
			'</div>',
		],
	
		init: function(options) {
			var me = this;
			this.mutex = true;    		//menu间展开相斥，默认相斥
			this.activeMenuIdx = 0;    	//默认选中第一个menu的第一个item
			this.activeGroupIdx = 0;    	//默认第二个menu为开启状态
			this.groups = [];
			this.groupMap = {};
			this.selectedGroup = null;
			this.selectedMenu = null;
			this.color = {
	            indicator: "#8DC7E6",
	            header: "#2A58AD",
	            headerFont: "white",
	            headerSelected: "#1D4B9E",
	            headerSelectedFont: "white",
	            headerHover: "#23a9f0",
	            headerHoverFont: "white",
	            menu: "#2A58AD",
	            menuFont: "white",
	            menuSelected: "#1D4B9E",
	            menuSelectedFont: "white",
	            menuHover: "#23a9f0",
	            menuHoverFont: "white"
	        };
			
			Control.call(this, options);

			this.canvas.css("background", this.color.background);
			
			if (this.url) {
				Server.getData(this.url, function(data) {
					me.createElements(data);
				});
			}
			else if (this.data) {
				this.createElements(this.data);
			}
		},
		
		setURL: function(url) {
			var me = this;	this.url = url;
			
			Server.getData(this.url, function(data) {
				me.createElements(data);
			});			
		},
		
		clear: function() {
			this.groups = [];
			this.groupMap = new Object();
			this.selectedGroup = null;
			this.selectedMenu = null;
			this.canvas.empty();
		},
		
		queryType: function(record) {
			if (record.text == "-") {
				return "spliter";
			}
			
			if (!record.parentid) {
				return "group";
			}
			
			return "menu";
		},
		
		createElements: function(data) {
			this.clear();
			if (!data || data.length == 0) {
				return;
			}
			
			//1. create all groups
			for (var i = 0; i < data.length; i++) {
				var line = data[i];
				var type = this.queryType(line);
				
				if ("group" == type) {
					this.createOneGroup(line);
				}
			}

			//2. create all menus
			for (var i = 0; i < data.length; i++) {
				var line = data[i];
				var type = this.queryType(line);
				
				if ("menu" == type) {
					this.createOneMenu(line);
				}
			}
			
			//3. set default active
			this.selectByIndex(this.activeGroupIdx, this.activeMenuIdx);
		},
		
		createOneGroup: function(line) {
			var me = this;
			
			var group = $(this.template.join(""));
			var header = group.header = $("#gm_header", group);
			group.menus = [];
			group.record = line;
			group.active = false;
			
			header.indicator = $("#indicator", header);
			header.image = $("#gm_header_img", header);
			header.text = $("#gm_header_text", header);
			
			header.image.attr("src", line.img);
			header.text.html(line.text);
			this.setHeaderColor(header, header.indicator, "");
			
			header.click(function() {
				me.selectGroup(group, true);
			});
			
			header.hover(
				function() {
					me.setHeaderColor(header, header.indicator, "hover");
    			},
    			function() {
    				var status = (me.selectedGroup == group) ? "selected" : "";
    				me.setHeaderColor(header, header.indicator, status);
    			}
    		);
			
			this.groups.push(group);
			this.groupMap[line.id] = group;
			this.canvas.append(group);
		},
		
		createOneMenu: function(line) {
			var me = this;
			
			var group = this.groupMap[line.parentid];
			if (group == null) {
				return;
			}
			line.parent = group.record;
			
			var menu = $(this.template_item.join(""));
			menu.record = line;
			
			menu.image = $("#gm_menu_img", menu);
			menu.text = $("#gm_menu_text", menu);
			
			menu.image.attr("src", line.img);
			
			menu.text.attr("title", line.text);
			menu.text.html(line.text);
			this.setMenuColor(menu, "");
			
			menu.click(function() {
				me.selectMenu(menu);
			});
			
			menu.hover(
    			function() {
    				me.setMenuColor($(this), "hover");
    			},
    			function() {
    				var status = (me.selectedMenu == menu) ? "selected" : "";
    				me.setMenuColor($(this), status);
    			}
    		);
			
			menu.group = group;
			group.menus.push(menu);
			group.append(menu);
		},
		
		selectGroup: function(group) { // 选择一级菜单  不必须选中二级菜单
			if (!group || group.active) {
				return;
			}
			
			//1. change selected group
			if (this.selectedGroup != group) {
				if (this.selectedGroup) {
					var old = this.selectedGroup;
					this.setHeaderColor(old.header, old.header.indicator, "");
				}
				
				this.selectedGroup = group;
				this.setHeaderColor(group.header, group.header.indicator, "selected");
				group.header.nextAll().slideDown("fast");
				group.active = true;
			}
			
			//2. unselected menu
			if (!group.menus) {
				if (this.selectedMenu) {
					this.setMenuColor(this.selectedMenu, "");
				}
			}
			
			//3. close other group
			if (this.mutex) {
				for (var i = 0; i < this.groups.length; i++) {
					var temp = this.groups[i];
					
					if (temp.active && temp != group && temp.menus) {
						temp.header.nextAll().slideUp("fast");
						temp.active = false;
					}
				}
			}			
			
			//4. invoke selected event
			if (this.onSelected) {
				this.onSelected.call(this, group.record);
			}
		},
		
		selectMenu: function(menu) { // 选择二级菜单  必须选中对应的一级菜单
			//1.
			if (this.selectedMenu != menu) {
				if (this.selectedMenu) {
					this.setMenuColor(this.selectedMenu, "");
				}
				
				this.selectedMenu = menu;
				this.setMenuColor(menu, "selected");
			}
			
			//2.
			this.selectGroup(menu.group);
			
			//3.
			if (this.onSelected) {
				this.onSelected.call(this, menu.record);	
			}
		},
		
		selectByIndex: function(groupidx, menuidx) {
			var group = this.groups[groupidx];
			
			if (group && group.menus && group.menus[menuidx]) {
				this.selectMenu(group.menus[menuidx]);
				return;
			}
			
			this.selectGroup(group);
		},
		
		setHeaderColor: function(header, indicator, status) {
			var color = this.color;
			
			if ("hover" == status) {
				indicator.css("background", "");
				header.css({"background": color.headerHover, "color": color.headerHoverFont});
			}
			else if ("selected" == status) {
				indicator.css("background", color.indicator);
				header.css({"background": color.headerSelected, "color": color.headerSelectedFont});
			}
			else {
				indicator.css("background", "");
				header.css({"background": color.header, "color": color.headerFont});
			}
		},
		
		setMenuColor: function(menu, status) {
			var color = this.color;
			
			if ("hover" == status) {
				menu.css({"background": color.menuHover, "color": color.menuHoverFont});
			}
			else if ("selected" == status) {
				menu.css({"background": color.menuSelected, "color": color.menuSelectedFont});
			}
			else {
				menu.css({"background": color.menu, "color": color.menuFont});
			}
		}
	});
	

	//================================LeftMenu===================================//
	LeftMenu = $.fm.LeftMenu = Control.subClass({
		template: [
		   	'<div class="menu">',
		   		'<div id="indicator" class="menu_indicator"></div>',
				'<div class="menu_img">',
					'<img id="image" style="width: 28px">',
				'</div>',
				'<div id="text" class="menu_txt">',
				'</div>',
			'</div>'
		],
		template_seperator: [
     		'<div class="menu-seperator">',
			'</div>'
		],
	
		init: function(options) {
			var me = this;
			this.items = [];
			this.defaultIndex = 0;
			this.selectClass = "menu_select";
			this.unselectClass = "menu_unselect";
			this.indicatorSelectClass = "menu_indicator_select";
			
			Control.call(this, options);
			
			if (this.url) {
				Server.getData(this.url, function(data) {
					me.createItems(data);
				});
			}
			else if (this.data) {
				this.createItems(this.data);
			}
		},
		
		setURL: function(url) {
			var me = this;	this.url = url;
			
			Server.getData(this.url, function(data) {
				me.createItems(data);
			});			
		},
		
		createItems: function(data) {
			var me = this;
			
			if (!data || data.length == 0) {
				return;
			}
			
			for (var i = 0; i < data.length; i++) {
				var line = data[i];
				
				if (line.text == "-") {
					this.canvas.append($(this.template_seperator.join("")));
					continue;
				}
				
				var item = $(this.template.join(""));
				this.items.push(item);
				
				item.data('el', item);
				item.data('record', line);
				
				item.indicator = $("#indicator", item);
				item.text = $("#text", item);
				item.image = $("#image", item);
				
				item.text.html(line.text);
				item.image.attr("src", line.img);				
				
				item.click(function(e) {
					if (me.beforeClick) {
						me.beforeClick.call(me);
					}
					var el = $(this).data('el');
					me.selectItem(el);
				});
				
				this.canvas.append(item);
			}
			
			this.selectByIndex(this.defaultIndex);
		},

		selectByIndex: function(index) {
			if (index < this.items.length) {
				var item = this.items[index];
				this.selectItem(item);
			}			
		},
		
		selectItem: function(item) {
			if (this.selected) {
				this.selected.removeClass(this.selectClass);
				this.selected.addClass(this.unselectClass);
				this.selected.indicator.removeClass(this.indicatorSelectClass);
			}
			
			item.removeClass(this.unselectClass);
			item.addClass(this.selectClass);
			item.indicator.addClass(this.indicatorSelectClass);
			
			this.selected = item;
			
			if (this.onSelected) {
				var record = item.data('record');
				this.onSelected.call(this, record, item);	
			}
		},
		
		clear: function() {
			this.items = [];
			this.selected = null;
			this.canvas.empty();
		}
	});
	
	
	//================================Menu===================================//
	Menu = $.fm.Menu = Control.subClass({
		template: [
		   	'<div class="menu_item">',
				'<img id="img" class="menu_item_img">',
				'<div id="txt" class="menu_item_text"></div>',
				'<div class="menu_item_spliter"></div>',
			'</div>'
		],
	
		init: function(options) {
			var me = this;
			this.items = [];
			
			this.defaultIndex = 0;
			this.barClass = "menu";
			this.bgColor = "#EFEFEF";
			this.touchColor = "#F7B3AF";
			this.selectColor = "#F7B3AF";
			
			Control.call(this, options);
			
			this.canvas.addClass(this.barClass);
			this.canvas.attr("align", "center");
			this.canvas.attr("left", -101);
			this.canvas.width(100);
			this.closed = true;
			
			if (this.url) {
				Server.getData(this.url, function(data) {
					me.createItems(data);
				});
			}
			else if (this.data) {
				this.createItems(this.data);
			}
		},
		
		setURL: function(url) {
			this.url = url; var me = this;
			Server.getData(this.url, function(data) {
				me.createItems(data);
			});			
		},
		
		createItems: function(data) {
			var me = this;
			
			this.clear();
			
			if (!data || data.length == 0) {
				return;
			}
			
			for (var i = 0; i < data.length; i++) {
				var line = data[i];
				
				//1. create element
				var item = $(this.template.join(""));
				this.items.push(item);
				
				//2. get image
				item.img = $("#img", item);
				item.img.attr("src", line.img);
				
				//3. get text
				item.text = $("#txt", item);
				item.text.html(line.text);
				item.text.addClass(this.unselectClass);
				item.css({"background-color": this.bgColor});
				
				//4. set data
				item.data('el', item);
				item.data('record', line);
				
				//5. event
				item.click(function(e) {
					var sender = $(this);
					var el = sender.data('el');
					me.selectItem(el);
				});
				
				item.on("touchstart", function() {
					var el = $(this); 
					el.css("background-color", this.touchColor);
				});
				
				item.on("touchend", function() {
					var el = $(this);
					var record = el.data("record");
					el.css("background-color", record.bgColor);
				});
				
				//6. add to canvas
				if (this.onRender) {
					this.onRender(line, item, i);
				}
				
				//7. add to canvas
				this.canvas.append(item);
			}
			
			this.selectByIndex(this.defaultIndex);
			
			if (this.afterLoad) {
				this.afterLoad(this, this.items);
			}
		},
		
		clear: function() {
			this.selected = null;
			this.items = [];
			this.canvas.empty();
		},
		
		selectByIndex: function(index) {
			if (index < this.items.length) {
				var item = this.items[index];
				this.selectItem(item);
			}			
		},
		
		selectItem: function(item) {
			if (this.selected) {
				this.selected.css("background-color", this.bgColor);
			}
			
			this.close();
			this.selected = item;
			this.selected.css("background-color", this.selectColor);
			
			if (this.onSelect) {
				var record = item.data('record');
				this.onSelect.call(this, record, item);	
			}
		},
		
		open: function() {
			if (!this.closed) {
				return;
			} 
			
			this.closed = false;
			this.canvas.animate({left:0, width: 100}, 200);
		},
		
		close: function() {
			if (this.closed) {
				return;
			} 
			
			this.closed = true;
			this.canvas.animate({left: -101, width: 100}, 200);
		},
		
		toggle: function(e) {
			if (this.closed) {
				this.open();
			}
			else {
				this.close();
			}
			
			if (e) {
				e.stopPropagation();
			}
		}
	});	


	//================================PopupMenu===================================//
	PopupMenu = $.fm.PopupMenu = Control.subClass({
		template: [
		   	'<div class="l-menu" style="width: 140px; display: none;" ligeruiid="Menu1000">',
				'<div class="l-menu-yline"></div>',
				'<div class="l-menu-over" style="top: -24px;">',
					'<div class="l-menu-over-l"></div>',
					'<div class="l-menu-over-r"></div>',
				'</div>',
				'<div id="menu-body" class="l-menu-inner">',
				'</div>',
			'</div>'
		],
		template_item: [
            '<div class="l-menu-item">',
				'<div id="img" class="l-menu-item-icon"></div>',
				'<div id="txt" class="l-menu-item-text"></div>',
			'</div>',
		],
		template_line: [
		    '<div class="l-menu-item-line"></div>'
		],
			
		init: function(options) {
			this.items = [];
			Control.call(this, options);
			
			this.createElement();
			this.createItems();
		},
	
		createElement: function() {
			var me = this;
			
			this.element = $(this.template.join(""));
			this.body = $("#menu-body", this.element);
			this.canvas.append(this.element);
			
			var item_new = $("#item_new", this.element);
			item_new.click(function() {
				me.element.hide();
				Win.popup("root/page/work/bidding.html");
			});
			
			var item_delete = $("#item_delete", this.element);
			item_delete.click(function() {
				me.element.hide();
				Dialog.confirm("删除", "是否删除当前单元格内容?", function() {
					
				});
			});
		},
		
		createItems: function() {
			if (this.items) {
				for (var i = 0; i < this.items.length; i++) {
					this.createOneItem(this.items[i]);
				}
			}
		},
		
		createOneItem: function(item) {
			var me = this;
			
			if (item.text == "-") {
				this.body.append($(this.template_line.join("")));
				return;
			}
			
			var el = item.el = $(this.template_item.join(""));
			
			//1. 
			if (item.disable) {
				el.addClass("l-menu-item-disable");
			}
			
			//2. get image
			el.img = $("#img", el);
			
			//3. get text
			el.text = $("#txt", el);
			el.text.html(item.text);
			
			//4. set data
			el.data('el', el);
			el.data('item', item);
			
			//5. event
			el.click(function(e) {
				me.hide();
				
				var sender = $(this);
				var el = sender.data('el');
				var item = sender.data('item');
				
				if (item.onClick && !item.disable) {
					item.onClick(el, item);
				}
			});
			
			//6.
			this.body.append(el);
		},
		
		show: function(left, top) {
			if (left && top) {
				this.element.css({
					"left": left,
					"top": top
				});
			}
			
			this.element.show();
		},
		
		hide: function() {
			this.element.hide();
		},
		
		setDisable: function() {
			var disable = {};
			for (var i = 0; i < arguments.length; i++) {
				disable[arguments[i]] = true;
			}
			
			for (var i = 0; i < this.items.length; i++) {
				var item = this.items[i];
				
				if (!item.el) {
					continue;	
				}
				
				if (disable[item.id]) {
					item.disable = true;
					item.el.addClass("l-menu-item-disable");
				}
				else {
					item.disable = false;
					item.el.removeClass("l-menu-item-disable");
				}
			}
		}
		
	});	
	
	//================================Tab===================================//
	Tab = $.fm.Tab = Control.subClass({
		init: function(options) {
			this.items = [];
			this.headers = [];
			this.defaultIndex = 0;
			
			Control.call(this, options);
			
			this.collectPages();
			this.collectHeaders();
		},
		
		collectPages: function() {
			for (var i = 0; i < this.pages.length; i++) {
				var page = this.pages[i];
				
				if ("string" == $.type(page)) {
					if (page.charAt(0) != "#") {
						page = "#" + page;
					}
					
					this.pages[i] = $(page);
				}
			}
		},
		
		collectHeaders: function() {
			var me = this;

			for (var i = 0; i < this.headers.length; i++) {
				var header = this.headers[i];
				
				if ("string" == $.type(header)) {
					if (header.charAt(0) != "#") {
						header = "#" + header;
					}
					
					this.items.push($(header));
				}
			}
			
			var width = this.headerWidth || Math.floor(100 / this.headers.length) + "%";
			
			for (var i = 0; i < this.items.length; i++) {
				var item = this.items[i];
				item.attr("class", "div_tab_off");
				item.index = i;
				item.width(width);
				if (this.headerHeight) {
					item.height(this.headerHeight);
					item.css({"line-height": this.headerHeight + "px"});
				}
				item.data("el", item);
				
				item.click(function() {
					var el = $(this).data("el"); 
					me.select(el);
				});
			}
			
			if ((me.defaultIndex >= 0) && (me.defaultIndex < me.items.length)) {
				me.select(me.defaultIndex);
			}
		},
		
		select: function(item) {
			if ($.isNumeric(item)) {
				item = this.items[item];
			}
			
			//1.
			if (this.selected) {
				if (this.pages && this.pages[this.selected.index]) {
					this.pages[this.selected.index].hide();
				}
				
				this.selected.attr("class", "div_tab_off");
			}
			
			//2.
			this.selected = item;
			
			if (this.pages && this.pages[item.index]) {
				this.pages[item.index].show();
			}
			
			item.attr("class", "div_tab_on");
			
			//3.
			if (this.onSelect) {
				var id = item.attr("id");
				this.onSelect(id, this);
			} 
		}
	});
	
	//================================Options===================================//
	Options = $.fm.Options = Control.subClass({
		init: function(options) {
			this.items = [];
			this.defaultIndex = 0;
			
			Control.call(this, options);
			
			//this.canvas.addClass("options");
			this.canvas.attr("align", "center");
			this.renderItems();
		},
		
		renderItems: function() {
			var me = this, items = $("button", this.canvas);
			
			for (var i = 0; i < items.length; i++) {
				//1. create item
				var item = $(items.get(i));
				
				if (i == 0) {
					item.attr("class", "options_off options_left");
				}
				else if (i == items.length - 1) {
					item.attr("class", "options_off options_right");
				}
				else {
					item.attr("class", "options_off");
				}
				
				item.data("el", item);
				this.items.push(item);
				
				//2. event
				item.click(function() {
					var el = $(this).data("el"); 
					me.select(el);
				});
			}
			
			if (me.defaultIndex < me.items.length) {
				me.select(me.defaultIndex);
			}
		},
		
		select: function(item) {
			if ($.isNumeric(item)) {
				item = this.items[item];
			}
			
			if (this.selected) {
				this.selected.removeClass("options_on");
				this.selected.addClass("options_off");
			}
			
			this.selected = item;
			this.selected.removeClass("options_off");
			this.selected.addClass("options_on");
			
			if (this.onSelect) {
				this.onSelect(item, this);
			}
		},
		
		val: function() {
			if (this.selected) {
				return this.selected.attr("id");
			}
		},
		
		toURL: function() {
			if (this.fieldname) {
				if (this.selected) {
					return this.fieldname + "=" + this.selected.attr("id");
				}
			}
		}
	});
	
	//================================List===================================//
	List = $.fm.List = Control.subClass({
		init: function(options) {
			var me = this;
			me.items = [];
			me.lazyLoad = false;
			Control.call(this, options);
			
			if (me.url && !me.lazyLoad) {
				Server.getData(this.url, function(data) {
					me.createItems(data);
				});
			}
			else if (me.data) {
				me.createItems(me.data);
			}			
		},
		
		setData: function(data) {
			if (data) {
				this.clear();
				this.createItems(data);
			}
		},
		
		setURL: function(url) {
			var me = this;
			this.url = url;
			this.clear();
			Server.getData(this.url, function(data) {
				me.createItems(data);
			});			
		},
		
		createItems: function(data) {
			if (!data) { return; }
			
			for (var i = 0; i < data.length; i++) {
				var item = this.createOneItem(data[i], 0);
				this.canvas.append(item);
			}
			
			this.select(this.defaultIndex);
		},
		
		appendItems: function(currentItem, data) {
			if (!data) { return; }
			currentItem = currentItem || this.items[this.items.length - 1];
			
			for (var i = 0; i < data.length; i++) {
				var item = this.createOneItem(data[i]);
				currentItem.after(item);
				currentItem = item;
			}
		},
		
		appendSubItems: function(currentItem, data) {
			if (!data) { return; }
			currentItem = currentItem || this.items[this.items.length - 1];
			
			for (var i = 0; i < data.length; i++) {
				var item = this.createOneItem(data[i]);
				currentItem.append(item);
			}
		},
		
		createOneItem: function(line, level) {
			var me = this; 
			
			var item = $(me.template.join(""));
			item.record = line;
			item.level = level;
			item.data("el", item);
			me.items.push(item);
			
			if (me.onRender) {
				me.onRender(line, item, me);
			}
			
			item.click(function() {
				var el = $(this).data("el"); 
				me.select(el);
			});
			
			return item;
		},
		
		select: function(item) {
			if ($.isNumeric(item)) {
				item = this.items[item];
			}
			
			if (this.onSelect) {
				this.onSelect(item.record, item);
			}
		},
		
		refresh: function() {
			var me = this;
			if (me.url) {
				Server.getData(this.url, function(data) {
					me.createItems(data);
				});
			}			
		},
		
		clear: function() {
			this.data = null;
			this.items = [];
			
			if (this.empty) {
				this.empty();
			}
			else {
				this.canvas.empty();
			}
		},
		
		renderItme: function(item){
			this.canvas.append(item);
		}
	});
	
	
	//================================List===================================//
	DataList = $.fm.DataList = Control.subClass({
		headerTemplate: [
		    '<div></div>'
		],		
		rowTemplate: [
		    '<div></div>'
		],
		cellTemplate: [
			'<div></div>',
		],
		checkTemplate: [
		    '<div align = right class="datalist_cell" >',
		    	'<div class="datalist_click_outer">',
			    	'<div id="innerCheck" class="datalist_click_selected"></div>',
				'</div>',		    
		    '</div>'
		],		
		  		
		init: function(options) {
			options = options ? options : {};
			
			this.data = [];
			this.items = [];
			this.rows = [];
			this.selected = null;
			this.dblclick = false;
			this.closeOnSelect = true;
			this.showLineNo = true;
			this.lineNoWidth = 10;
			this.headerHtml = null;
			this.headerClass = "datalist_header";
			this.rowClass = "datalist_row";
			this.cellClass = "datalist_cell";
			this.evenClass = "datalist_row_interval";
			this.evenFlag = true;
			this.evenActive = true;
			this.orderby = "asc";
			this.sequence = 0;

			
			Control.call(this, options);
			
			this.initItems();
			this.createHeader();
			
			if (this.data) {
				this.loadData(this.data);
			}
		},
		
		initItems: function() {
			//add line no
			if (this.showLineNo) {
				var items = this.items;
				
				this.items = new Array(items.length + 1);
				this.items[0] = {text: "行", field: "lineno", width: this.lineNoWidth + "%", align: "center"};
				
				for (var i = 0; i < items.length; i++) {
					this.items[i + 1] = items[i];
				}
			}
		},
		
		createHeader: function() {
			if (this.headerHtml) {
				this.header = $(this.headerHtml.join(""));
				this.canvas.append(this.header);
				return;
			}
			
			var items = this.items;
			if (!items) {
				return;
			}
			
			this.header = $(this.headerTemplate.join(""));
			this.header.addClass(this.headerClass);
			
			for (var i = 0; i < items.length; i++) {
				var item = items[i];
				this.createOneCell(this.header, item, "center", item.text);
			}
			
			this.canvas.append(this.header);
		},
		
		toggleSelect: function(row) {
			this.setSelect(row, !row.selected);
		},
		
		createBlankRow: function() {
			var blankRow = $(this.rowTemplate.join(""));
			this.canvas.append(blankRow);
		},
		
		loadData: function(dataList, offset) {
			if (!offset) {
				offset = 0;
			}
			
			this.clear();
			this.data = dataList;
			
			if (!dataList || !this.items) {
				return;
			}
			
			this.rows = [];
			
			for (var i = 0; i < dataList.length; i++) {
				var data = dataList[i];
				
				row = this.createOneRow(i + 1 + offset, data);
				this.rows.push(row);
			}
		},
		
		createOneRow: function(index, data) {
			var me = this;
			var items = this.items;
			
			var row = $(this.rowTemplate.join(""));
			row.cells = {};
			row.index = index;
			row.record = data;
			row.addClass(this.rowClass);
			data.el = row;
			
			if (this.isEven()) {
				row.addClass(this.evenClass);
			}
			
			row.data('el', row);
			
			row.bind("touchstart", function() {
				if (me.touched) {
					me.touched.removeClass("datalist_row_touch");
				}
				
				me.touched = row;
				row.addClass("datalist_row_touch");
			});
			row.bind("touchend", function() {
				row.removeClass("datalist_row_touch");
			});
			
			if (!this.dblclick) {
				row.click(function(e) {
					var el = $(this).data('el');
					me.selectRow.call(me, el);
				});
			}
			else {
				row.dblclick(function(e) {
					var el = $(this).data('el');
					me.selectRow.call(me, el);
				});				
			}
			
			if (data) {
				var min = 0;
				
				//行号
				if (this.showLineNo) {
					min = 1;
					var linenoCell = this.createOneCell(row, items[0], items[0].align, index);
					row.lineno = linenoCell;
					row.index = index;
				}
				
				//数据
				var text;
				for (var i = min; i < items.length; i++) {
					var item = items[i];
					
					if (item.vender) {
						text = item.vender(data);
					}
					else {
						text = data[item.field];
					}
					
					var cell = this.createOneCell(row, item, item.align, text);
					row.cells[item.field] = cell;
				}
			}
			
			if (this.orderby == "desc") {
				this.header.after(row);
			}
			else {
				this.canvas.append(row);
			}
			
			return row;
		},
		
		createOneCell: function(row, item, align, value) {
			var cell = $(this.cellTemplate.join(""));
			
			cell.addClass(this.cellClass);
			cell.html(value);
			
			var css = item.css || {};
			css["text-align"] = align;
			cell.css(css);
			
			cell.width(item.width);
			if (item.height) {
				cell.height(item.height);
			}
			
			row.append(cell);
			return cell;
		},
		
		onRowClick: function(el, data) {},
		onRowSelected: function(data) {},
		onRowUnselected: function(data) {},
		
		selectRow: function(el) {
			el.removeClass("datalist_row_touch");
			
			if (!el) {
				return;
			}

			if (this.selected) {
				this.selected.el.removeClass("datalist_row_selected");
			}
			
			el.addClass("datalist_row_selected");
			this.selected = el.record;
			this.onRowSelected(this.selected);
		},
		
		isEven: function() {
			if (!this.evenActive) {
				return false;
			} 
			
			this.evenFlag = !this.evenFlag;
			return this.evenFlag;
		},
		
		append: function(data) {
			var index = this.data.length + 1;
			this.data.push(data);
			var row = this.createOneRow(index, data);
			this.rows.push(row);			
		},
		
		save: function() {
			if (!this.selected) {
				return;
			}
			
			var el = this.selected.el;
			var data = this.selected;
			
			for (var prop in data) {
				var cell = el.cells[prop];
				if (cell) {
					cell.html(data[prop]);
				}
			}
		},
		
		deleteLine: function(record) {
			if (this.isEmpty() || !record) {
				return;
			}

			var index = record.el.index - 1;
			var data = this.data;
			var line, lineEl;
			
			for (var i = index; i < data.length - 1; i++) {
				line = data[i + 1];
				lineEl = line.el;
				
				lineEl.index = i;
				lineEl.lineno.html(i + 1);
				data[i] = line;
			}
			data.pop();				

			record.el.remove();
		},
		
		deleteLast: function() {
			if (this.isEmpty()) {
				return;
			}
			
			var last = this.data[this.data.length - 1];
			this.data.pop();
			last.el.remove();
		},
		
		isEmpty: function() {
			return !this.data || this.data.length == 0;
		},
		
		clear: function() {
			for (var i = 0; i < this.rows.length; i++) {
				var row = this.rows[i];
				row.remove();
			};
			
			this.data = [];
			this.rows = [];
			this.selected = null;
			this.sequence = 0;
		},
		
		nextSequence: function() {
			this.sequence = this.sequence + 1;
			return this.sequence;
		},
		
		getLength: function() {
			return this.data.length;
		},
		
		exists: function(field, source) {
			if (!field || !source || !source[field]) {
				return false;
			}
			
			var value = source[field], data = this.data;
			
			for (var i = 0; i < data.length; i++) {
				if (source.el && source.el.index == (i + 1)) {
					continue;
				}
				
				var line = data[i];
				if (line[field] == value) {
					return true;
				}
			}
			
			return false;
		}
		
	});
	
	
	//================================Circle===================================//
	Indicator = $.fm.Indicator = Control.subClass({
		template: [
		    '<div>',
		       	'<div id="title" class="targetor_txt"></div>',
		       	'<div id="targetor_indicator" class="targetor_indicator">',
					'<svg xmlns="http://www.w3.org/2000/svg" version="1.1" viewBox="0 0 200 200" class="indicator">',
						'<circle cx="100" cy="100" r="76" class="indicator_full"></circle>',
						'<circle id="targetor_indicator_progress" cx="100" cy="100" r="76" class="indicator_progress" transform="rotate(-90, 100, 100)"></circle>',
						'<text id="targetor_indicator_percent" x="100" y="100" class="indicator_percent"></text>',
						'<text id="targetor_indicator_txt" x="100" y="130" class="indicator_text"></text>',
					'</svg>',
		       	'</div>',
		       	'<div id="targetor_msg" class="targetor_msg">',
		       		'<div id="targetor_msg_a" class="targetor_msg_a">',
		       			'<div id="label_a" class="targetor_msg_a_txt">指标A：指标值</div>',
		       			'<div class="targetor_msg_bar">',
		       				'<div id="bar_a" class="targetor_msg_innerbar"></div>',
		       			'</div>',
		       		'</div>',
		       		'<div style="clear: both;"></div>',
		       		'<div id="targetor_msg_b" class="targetor_msg_b">',
		       			'<div id="label_b" class="targetor_msg_b_txt">指标B：指标值</div>',
		       			'<div class="targetor_msg_bar">',
		       				'<div id="bar_b" class="targetor_msg_innerbar"></div>',
		       			'</div>',
		       		'</div>',  		
		       	'</div>',
		       	'<button class="targetor_btn" style="display: none">详细</button>',
		    '</div>'
		],
	
		init: function(options) {
			this.percent = 75;
			this.title = "标题";
			this.series = [
				{
					text: "指标A",
					length: 100
				},
				{
					text: "指标A",
					length: 100
				}				
			];
			
			this.text = "指标";
			this.coefficient = (76.0 * 3.6) / 57;
			
			Control.call(this, options);
			
			this.renderItems();			
		},

		renderItems: function() {
			var items = $(this.template.join(""));
			
			this.el = {};
			
			//1. create title
			this.el.title = $("#title", items);
			this.el.title.html(this.title);
			
			//2. create indicator
			this.el.indicator = $("#targetor_indicator", items);
			
			//2.1 create indicator.progress circle
			var circleLine = Math.round(this.percent * this.coefficient);
			this.el.indicator.progress = $("#targetor_indicator_progress", items);
			this.el.indicator.progress.attr("stroke-dasharray", circleLine + ", 10000");			
			
			//2.2 create indicator.percent number
			this.el.indicator.percent = $("#targetor_indicator_percent", items);
			this.el.indicator.percent.html(this.percent);
			
			//2.2 create indicator.text
			this.el.indicator.text = $("#targetor_indicator_txt", items);
			this.el.indicator.text.html(this.text);
			
			//3.create message
			this.el.message = $("#targetor_msg", items);
			
			//3.1 create message
			this.el.message.a = $("#targetor_msg_a", this.el.message); 
			this.el.message.a.txt = $("#label_a", this.el.message.a);
			this.el.message.a.txt.html(this.series[0].text);
			this.el.message.a.bar = $("#bar_a", this.el.message.a);
			this.el.message.a.bar.width(this.series[0].length + "%");
			
			this.el.message.b = $("#targetor_msg_b", this.el.message); 
			this.el.message.b.txt = $("#label_b", this.el.message.b);
			this.el.message.b.txt.html(this.series[1].text);
			this.el.message.b.bar = $("#bar_b", this.el.message.b);
			this.el.message.b.bar.width(this.series[1].length + "%");
			
			this.canvas.append(items);
		},
		
		setData: function() {
			if (!arguments) { return; }
			
			this.el.indicator.percent.text(arguments[0].text);
			this.el.indicator.progress.attr("stroke-dasharray", Math.round(arguments[0].length * this.coefficient) + ", 10000");
			if (arguments[0].color){
				this.el.indicator.progress.css("stroke", arguments[0].color);
			}
			
			this.el.message.a.txt.text(arguments[1].text);
			this.el.message.a.bar.width(arguments[1].length + "%");
			if (arguments[1].color){
				this.el.message.a.bar.css("background-color", arguments[1].color);
			}
			
			this.el.message.b.txt.text(arguments[2].text);
			this.el.message.b.bar.width(arguments[2].length + "%");
			if (arguments[2].color){
				this.el.message.b.bar.css("background-color", arguments[2].color);
			}
		}
	});	
	
	/*==========================延时触发==================
	 * 例
	 *  
	 *  var timeout = new Timeout();

		$(window).resize(function() {
			timeout.exec(function() {
				lbl_hello.html(lbl_hello.html() + "1");
			});
		});	
		
	 * 
	 * 
	 * 
	 * 
	 * ==========*/
	Timeout = $.fm.Timeout = Object.subClass({
		waitfor: 1000,
		todoList: [],
		
		init: function(options) {
    		if (prototyping) {
    			return;
    		}
    		
    		Object.init.call(this, options);
		},
		
		exec: function(todo) {
			if (!todo) {
				return;
			}
			
			var todoList = this.todoList;
			
			for (var i = 0; i < todoList.length; i++) {
				todoList[i].active = false;
			}
			
			todo.active = true;
			todoList.push(todo);
			
			setTimeout(function() {
				if (!todo.active) {
					return;
				}
				
				todoList = [];
				todo();
			}, this.waitfor);
		}

	});

	/*======================================================*/
	ChartHelperClass = $.fm.ChartHelperClass = Object.subClass({
		size: {
			month: 12,
			dmonth: 6,
			season: 4,
			year: 1
		},
		
		getPeriodValue: function(periodtype, month) {
			if ("year" == periodtype) {
				return 0;
			}
			else if ("dmonth" == periodtype) {
				return Math.ceil(month / 2.0);
			}
			else {
				return month;
			}
		},
		
		getPeriod: function(type, month) {
			if (!type) return [];
			
			type = type.toLowerCase(); month = month || 12;
			var max = 0, result = [];
			
			if ("month" == type) {
				max = month;
			}
			else if ("dmonth" == type) {
				max = Math.ceil(month / 2.0);
			}
			else if ("season" == type) {
				max = Math.ceil(month / 3.0);
			}
			
			for (var i = 1; i <= max; i++) {
				result.push(i); 
			}
			return result;
		},
		
		getPeriodData: function(periodType, data, multi) {
			if (!periodType) return {};
			multi = multi || 1;
			
			result = {actual: [], target: [], achieve: [], onyear: []};
			
			for (var i = 0; i < data.length; i++) {
				var line = data[i];
				result.actual.push(line.amt_actual ? line.amt_actual / multi : 0);
				result.target.push(line.amt_target ? line.amt_target / multi: 0);
				result.achieve.push(line.rate_achieve ? line.rate_achieve : 0);
				result.onyear.push(line.rate_onyear ? line.rate_onyear : 0);
			}
			
			return result;
		},
		
		getSeriesData: function(namefield, valuefields, array, multi) {
			if (!namefield || !array) return {};
			multi = multi || 1;
			
			var result = {categories: []}, line, name, value;
			
			for (var i = 0; i < valuefields.length; i++) {
				result[valuefields[i]] = [];
			}
			
			for (var i = 0; i < array.length; i++) {
				line = array[i];
				result.categories.push(line[namefield]);
				
				data = result[line[namefield]] = [];
				
				for (var j = 0; j < valuefields.length; j++) {
					name = valuefields[j];
					value = line[name];
					
					if (name && (name.length > 4) && (name.substring(0, 4) == "amt_")) {
						value = value ? value / multi : 0;
					}
					else {
						value = value || 0;
					}
					
					result[name].push(value);
				}
			}
			
			return result;
		},
		
		getSeries: function(fieldname, array, valueFields) {
			var result = [];

			for (var i = 0; i < array.length; i++) {
				array[i].amt_actual = array[i].amt_actual/10000;
			//	array[i].rate_achieve = array[i].rate_achieve.toFixed(2);
				array[i].rate_achieve = parseFloat(Highcharts.numberFormat(array[i].rate_achieve,2));
				
				var line = array[i];
				var item = {name: line[fieldname], data: [[]]};
				var data = item.data[0];
				
				for (var j = 0; j < valueFields.length; j++) {
					data.push(line[valueFields[j]] || 0);
				}
				
				result.push(item);
			}
			
			return result;
		},
		
		getPeriodSeries: function(periodType, namefield, valuefield, array, multi, type) {
			multi = multi || 1; 
			var result = [], prior = null, item = null, value;

			for (var i = 0; i < array.length; i++) {
				var line = array[i], name = line[namefield];
				
				if (prior != name) {
					item = {"name": name, data: [], "type": type || "spline"};
					prior = name;
					result.push(item);
				}
				
				value = line[valuefield];
				value = Math.round((value ? value / multi : 0));
				item.data.push(value);
			}
			
			return result;
		},
		
		getPercent: function(namefield, valuefield, array) {
			result = [];
			
			for (var i = 0; i < array.length; i++) {
				var line = array[i];
				result.push({name: line[namefield], y: line[valuefield] || 0});
			}
				
			return result;
		},
		
		getStackSeriesMax: function(series) {
			var result = {max: 0, total: 0}, max = 0, total = 0;
			
			for (var no in  series) {
				var data = series[no].data || series[no];
				total = 0;
				
				for (var i = 0; i < data.length; i++) {
					if (max < data[i]) {
						max = data[i];
					}
					
					total = total + data[i];
				}
				
				if (result.max < max) {
					result.max = max;
				}
				
				if (result.total < total) {
					result.total = total;
				}
			}
			
			return result;
		} 
	});
	
	ChartHelper = new ChartHelperClass();
	
	
	/*======================================================*/
	SizeToggler = {
		resize: function(element) {
			if (this.maxedElement && this.maxedElement.attr("id") != element.attr("id")) {
				this.toNormal(this.maxedElement);
			}
			
			var status = element.data("status");
			if (!status) {
				status = "max";
				this.toMax(element);
			}
			else if ("normal" == status) {
				status = "max";
				this.toMax(element);
			}
			else {
				status = "normal";
				this.toNormal(element);
			}
		},
		toNormal: function(element) {
			var status = "normal";
			var canvas = $("canvas", element);
			var chart = element.data("chart");
			var toSize = element.data("originalSize");
			var toCss = {"z-index": 0};
			
			this.doResize(element, canvas, chart, status, toSize, toCss);
		},
		toMax: function(element) {
			var status = "max";
			var canvas = $("canvas", element);
			var charts = [];
			for(var i = 0; i <canvas.length; i++) {
				var onecanvas = $(canvas.get(i));
				var chartid = onecanvas.parent().parent().attr("id");
				onechart = echarts.init(document.getElementById(chartid));
				charts[i] = onechart;
			}
			
			var position = element.position();
			element.data("originalSize", {
				left: position.left,
				top: position.top,
				width: element.width(),
				height: element.height()
			});
			
			var toSize = this.calculateMaxSize(element);
			
			var toCss = {"z-index": 500, "background-color": "white"};
			element.css(toCss);
			
			element.data("chart", charts);
			
			
			this.doResize(element, canvas, charts, status, toSize, null);			
		},
		doResize: function(element, canvas, charts, status, toSize, toCss) {
			var me = this;
			
			canvas.hide();
			
			element.animate(toSize, "normal", null, function() {
				for(var i = 0; i < charts.length; i++) {
					var onechart = charts[i];
					onechart.resize();
				}
				canvas.show();
				
				if (toCss) {
					element.css(toCss);
				}
				
				element.data("status", status);
				
				if ("max" == status) {
					me.maxedElement = element;
				}
				else {
					me.maxedElement = null;
				}
			});			
		},
		calculateMaxSize: function(element) {
			var toSize = {
					"left":	"20%",
					"top": "10%",
					"width": "60%",
					"height": "80%"
				};
			
			var originalSize = element.data("originalSize");
		
			var rate = parseFloat(originalSize.width/originalSize.height);
			
			var initMaxWidth = parseInt(RootParent.width() * 0.6);
			var initMaxHeight = parseInt(initMaxWidth / rate);
			
			
			if (originalSize.width >= initMaxWidth) {
				toSize.width = originalSize.width + parseInt((RootParent.width() - initMaxWidth) * 0.6);
				toSize.left = parseInt((RootParent.width() - toSize.width) / 2);
			}
			if (originalSize.height >= initMaxHeight) {
				toSize.height = originalSize.height + parseInt((RootParent.height() - initMaxHeight) / rate);
				toSize.top = parseInt((RootParent.height() - toSize.height) / 2);
			}
			
			return toSize;
		}
	}
		
})(jQuery);	


sizeToggle = function(element) {
	SizeToggler.resize(element);
};


