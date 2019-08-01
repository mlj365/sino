//0.define
	
	ButtonList = {
		"area": {text: "面积图", img: "area.png"},
		"area_c": {text: "面积图C", img: "area_c.png"},
		"area_pa": {text: "面积图PA", img: "area_pa.png"},
		"area_r": {text: "面积图Ｒ", img: "area_r.png"},
		"bar": {text: "柱形图", img: "bar.png"},
		"bar_a": {text: "柱形图A", img: "bar_a.png"},
		"bar_c": {text: "柱形图C", img: "bar_c.png"},
		"bubble": {text: "汽泡图", img: "bubble.png"},
		"combine": {text: "组合图", img: "combine.png"},
		"combine_m": {text: "组合图M", img: "combine_m.png"},
		"dot": {text: "点图", img: "dot.png"},
		"funnel": {text: "图", img: "funnel.png"},
		"heat": {text: "热力图", img: "heat.png"},
		"line": {text: "折线图", img: "line.png"},
		"line_map": {text: "折线图M", img: "line_map.png"},
		"map_c": {text: "地图C", img: "map_c.png"},
		"map_g": {text: "地图G", img: "map_g.png"},
		"map_gis": {text: "地图GIS", img: "map_gis.png"},
		"map_s": {text: "地图Ｓ", img: "map_s.png"},
		"pie": {text: "饼图", img: "pie.png"},
		"multiPie": {text: "复合饼图", img: "multiPie.png"},
		"multiPieLighter": {text: "复合饼图L", img: "multiPieLighter.png"},
		"multiPieThicker": {text: "复合饼图T", img: "multiPieThicker.png"},
		"radar": {text: "雷达图", img: "radar.png"},
		"radar_a": {text: "雷达图A", img: "radar_a.png"},
		"rectTree": {text: "矩形树", img: "rectTree.png"},
		"wordCloud": {text: "文字云", img: "wordCloud.png"}
	};

//1.OlapTab                                        =========

	OlapTab = {
		items: {},
		
		init: function(items) {
			for (var i = 0; i < items.length; i++) {
				var option = items[i];
				
				var item = {code: option.code};
				
				item.header = $("#" + option.header);
				item.body = $("#" + option.body);
				
				if (i > 0) {
					item.body.hide();
				}
				
				this.items[option.code] = item;
				
				if (i == 0) {
					this.setHeaderActive(item.header, true);
				}
			}
		},
		
		changeActive: function(id) {
			for (var code in this.items) {
				var item = this.items[code];
				item.body.hide();
				this.setHeaderActive(item.header, false);
			}
			
			var item = this.items[id];
			item.body.show();
			this.setHeaderActive(item.header, true);
		},
		
		setHeaderActive: function(header, active) {
			if (active) {
				header.addClass("tab-avtiveHeader");
			}
			else {
				header.removeClass("tab-avtiveHeader");
			}
		}
	};
	
	
//2.Toolbar                                        =========
	
	Toolbar = $.fm.Toolbar = Control.subClass({
		items: {},
		element: null,
		template_message: '<div id="tool_message" align="center" style="color: red; font-size: 15px;"><label></label><a href="#"></a></div>',
		message: null,
		init: function(options) {
			Control.call(this, options);
			
			if (!options.items) {
				return;
			}
			
			for (var i = 0; i< options.items.length; i++) {
				this.createOneItem(options.items[i]);
			}
			this.createMessage();
		},
		
		createMessage: function() {
			var message = this.message = $(this.template_message);
			message.label = $("label", message);
			message.a = $("a", message);
			this.element.append(message);
		},
		createOneItem: function(itemOption) {
			if ("spliter" == itemOption.type) {
				this.createSpliter();
			}
			else if (this.btnTemplate) {
				this.createOneButton(itemOption, this.btnTemplate);
			}
			else {
				var template = [
					"<div class='tool-btn'>",
					"</div>"].join("");
				this.createOneButton(itemOption, template);
			}
		},
		
		createOneButton: function(option, template) {
			var me = this;
			
			//1.
			var btn = $(template);
			
			//2. image and icon
			if (option.img) {
				var image = $("#img", btn);
				
				if (!image.length) {
					image = $("<img class='tool-btn-img'>");
					btn.append(image);
				}
				
				image.attr("src", (this.imgPath || "") + option.img);
				btn.image = image;
			}
			else if (option.icon) {
				var icon = $("#icon", btn);
				
				if (!icon.length) {
					icon = $("<span class='tool-btn-icon iconfont'>");
					btn.append(icon);
				}
				
				icon.addClass(option.icon);
				btn.append(icon);
			}
			
			//3. text
			if (option.text) {
				var text = $("#text", btn);
				
				if (!text.length) {
					text = $("<div id='text' class='tool-btn-text'></div>");
					btn.append(text);
				}
				
				text.html(option.text);
				btn.text = text;
			}
			
			//4. click
			btn.click(function() {
				if (option.onClick) {
					option.onClick(option.code, btn);
				}
				else if (me.onClick) {
					me.onClick.call(me, option.code, btn);
				}
			});
			
			//5. 
			this.element.append(btn);
			this.items[option.code] = btn;
			
			return btn;
		},
		
		createSpliter: function() {
			var spliter = $([
				"<div class='tool-btn-spliter'>|</div>"].join(""));
			this.element.append(spliter);
		}
	});
	
	
//3.FieldArea                                        =========
	
	FieldArea = {
		items: {},
		checkItems:{},
		groups: [],
		element: null,
		
		init: function(options) {
			this.options = options;
			
			this.element = $(options.element);
			this.dimensionArea = $("#olap-fields-dimension", this.element);
			this.valueArea = $("#olap-fields-value", this.element);
			
			var dimensions = options.report.theme.dimension;
			this.appendDimensions(dimensions);
			
			var values = options.report.theme.value;
			this.appendValues(values);

		},
		
		appendDimensions: function(dimensions) {

			for (var i = 0; i < dimensions.length; i++) {
				var group = dimensions[i];
				group.level = i;
                this.groups.push(group);
				var groupElement = $([
				  	"<div class='fields-group'>",
						"<div class='fields-group-header'>",
							"<span id='icon' class='iconfont' style='font-size:16px'></span>",
							"<span id='groupText' class='groupText'></span>",							
						"<div>",				  		
					"</div>"
				].join(""));
				
				groupElement.icon = $("#icon", groupElement);
				groupElement.icon.addClass(this.options.groupIcon);
				
				groupElement.text = $("#groupText", groupElement);
				groupElement.text.html(group.text);
				
				var fields = group.fields;
				for (var j = 0; j < fields.length; j++) {
					var fieldOption = fields[j];
					
					if (!fieldOption.group) {
						fieldOption.group = group;
					}
					
					var item = this.appendOneField(groupElement, fieldOption);
					item.group = group;
					item.type = "dimension";
					item.level = j;
				}
				
				this.dimensionArea.append(groupElement);
			}
		},
		
		appendValues: function(values) {
			for (var i = 0; i < values.length; i++) {
				var valueOption = values[i];
				var item = this.appendOneField(this.element, valueOption);
				item.type = "value";
				item.level = i;
				this.valueArea.append(item);
				this.items[valueOption.field] = item;
			}			
		},
		
		appendOneField: function(parent, fieldOption) {
			var me = this;
			var item = $([
				"<div class='fields-item'>",
					"<label id='text'></label>",
					"<div id='checkBox' class='field-check'>",
						"<input type='checkbox'>",
					"<div>",
				"</div>"			
			].join(""));
			
			item.option = fieldOption;
			if (!item.option.showField) {
                item.option.showField = item.option.field;
            }

			item.text = $("#text", item);
			item.text.html(fieldOption.caption);
			item.checked = false;
			item.checkboxDiv = $("#checkBox", item);
			item.checkbox = $("input", item.checkboxDiv);
			item.checkbox.change(function() {
                item.checked = !item.checked;
                if (item.checked) {
                	me.checkItems[item.option.field] = item;
				}
				else {
                    delete me.checkItems[item.option.field];
				}


				if (me.options.onChangeFilterSetting) {
					me.options.onChangeFilterSetting(fieldOption, item.checked);
				}
			});

            if(item.option.defaultadded) {
                item.checked = true;
                item.checkbox.prop('checked', true);
                this.checkItems[fieldOption.field] = item;
            }

			parent.append(item);
			this.items[fieldOption.field] = item;
			
			item.draggable({
				helper: function( event ) {
			        return $( "<div style='position: fixed; z-index:100; background-color: #dad2d2; width: 120px; height: 22px; text-align: center; line-height: 22px;'>" + fieldOption.caption + "</div>" );
			      },
			    containment: "window",
				revert: "invalid",
				scope: "axis",
				cursorAt: {left: 110, top: 15}
			});
			
			return item;
		}
	};



//3.FiltersArea                                        =========
		
	FilterArea = {
		items: {},
		groups: {},
		element: null,
		onFilterChange: null,
		init: function(options) {
			this.options = options;
			
			this.onFilterChange = options.onFilterChange;
			this.element = $(options.element);
			this.createFilterItems(options.items);
		},
		
		createFilterItems: function(items) {
			for (var no in items) {
				var option = items[no].option;
				
				if ("value" == option.type) {
					continue;
				}
				
				var group = this.addGroup(option.group);

				if (!group) {
					continue;
				}

				this.addFilter(group.code, option);
			}
		},
		
		addGroup: function(option) {
			if (!option) {
				return;
			}
			
			var code = option.code;

			if (this.groups[code]) {
				return this.groups[code];
			}
			
			var group = $([
			  	"<div class='fields-group'>",
					"<div class='fields-group-header'>",
						"<span id='icon' class='iconfont' style='font-size:16px'></span>",
						"<span id='groupText' class='groupText'></span>",
					"<div>",				  		
				"</div>"
			].join(""));

			group.code = code;
			group.icon = $("#icon", group);
			group.icon.addClass(this.options.groupIcon);
			
			group.text = $("#groupText", group);
			group.text.html(option.text);
			
			this.element.append(group);
			this.groups[code] = group;
			return group;
		},

        addFilter: function(groupCode, itemOption) {
			var me = this;
			var group = this.groups[groupCode];
			if (!group) {
				group = me.addGroup(itemOption.group);
			}

			if ("select" == itemOption.type) {
				var item = $([
					"<div class='filter-line'>",
						"<div id='text' class='filter-label'></div>",
						"<div id='editorContainer' class='filter-edit'><select id='select_' style='width: 100%;height: 60%'></select></div>",
					"</div>"
				].join(""));
				
				item.type = itemOption.type;
				item.text = $("#text", item);
				item.text.html(itemOption.caption + "：");
				item.text.attr("title", itemOption.caption);
				item.must = typeof(itemOption.must) == 'undefined' ? false: itemOption.must;
				item.equal = typeof(itemOption.equal) == 'undefined' ? false: itemOption.equal;
				item.option = itemOption;

				var input = $("#select_", item);
				var data = itemOption.defaultvalue;
				input.empty();
				
				if (itemOption.existsEmpty) {
					input.append($("<option class='select_' value=\"empty\" >---</option>"));
				}else {
					input.append($("<option class='select_' value=\"ALL\" >ALL</option>"));
				}
				
				var dataurl = itemOption.url;
				if(dataurl) {
                    if (itemOption.parentField) {
                        var value = me.getValue(itemOption.parentField)
                        if (value) {
                            dataurl += "&parentField=" + itemOption.parentField + "&parentId=" + value;
                        }
                    }

					Server.getData(dataurl, function(data) {

						if (data && data.length) {
							for (var i = 0; i < data.length; i++) {
								var line = data[i];
								var option = $("<option ></option>");
								if (!itemOption.showField) {
                                    itemOption.showField = itemOption.field;
								}
								option.val(line[(itemOption.field.toLowerCase())]);
								option.html(line[(itemOption.showField.toLowerCase())]);
								
								input.append(option);
							}
						}
					});		
				}else if (data && data.length) {
					for (var i = 0; i < data.length; i++) {
						var line = data[i];
						var option = $("<option ></option>");
						
						option.val(line.code);
						option.html(line.name);
						
						input.append(option);
					}
				}
				
				input.on('change',function() {
					var value = input.find("option:selected").val();
					me.onFilterChange(itemOption.field, value, this);
				});
				this.items[itemOption.field] = item;
				group.append(item);
                item.group = group;
			} 
			else if ("date" == itemOption.type) {
				var item = $([
					"<div class='filter-line'>",
						"<div id='text' class='filter-label'></div>",
						"<div id='editorContainer' class='filter-edit'><input id='date_' class='filter-item' type='date'/></div>",
					"</div>"
				].join(""));
				
				item.type = itemOption.type;
				item.text = $("#text", item);
				item.text.html(itemOption.caption + "：");
				item.text.attr("title", itemOption.caption);
				item.must = typeof(itemOption.must) == 'undefined' ? false: itemOption.must;
				item.equal = typeof(itemOption.equal) == 'undefined' ? false: itemOption.equal;
                item.option = itemOption;

				item.editorContainer = $("#editorContainer", item);
				item.input = $("#date_", item);

                var dataurl = itemOption.url;
                if(dataurl) {
                    Server.getData(dataurl, function(data) {
                        if (data && data.length == 1) {
                            var line = data[0];
							var date = line[itemOption.field.toLowerCase()];
                            item.input.val(date);
                            me.onFilterChange(itemOption.field, date, this);
                        }
                    });
                }else {
                   /* var datetime = new DateTime();
                    var endtime = datetime.str;
                    item.input.val(endtime);*/
				}


				item.input.on('change',function() {
					var value = item.input.val();
					me.onFilterChange(itemOption.field, value, this);
				});
				
				this.items[itemOption.field] = item;
				group.append(item);
                item.group = group;
			}
			else{
				var item = $([
					"<div class='filter-line'>",
						"<div id='text' class='filter-label'></div>",
						"<div id='editorContainer' class='filter-edit'><input id='input' class='filter-item'/></div>",
					"</div>"
				].join(""));
				
				item.type = typeof(itemOption.type) == 'undefined'? 'text' : itemOption.type;
				item.text = $("#text", item);
				item.text.html(itemOption.caption + "：");
				item.text.attr("title", itemOption.caption);
				item.must = typeof(itemOption.must) == 'undefined' ? false: itemOption.must;
				item.equal = typeof(itemOption.equal) == 'undefined' ? false: itemOption.equal;
                item.option = itemOption;

				item.editorContainer = $("#editorContainer", item);
				
				item.editor = ControlCreator.create({
					type: "text"
				}, item.editorContainer);
				
				item.input = $("#input", item);
				
				item.input.on("keydown",function(e) {
					if(e && e.keyCode == 13) {
						item.input.blur();
					}
				});
					
				item.input.on('blur',function() {
					var value = item.input.val();
					if(typeof(item.input.preval) == "undefined" && value != "") {
						me.onFilterChange(itemOption.field, value, this);
						item.input.preval = value;
					}
					else if(typeof(item.input.preval) != "undefined" && item.input.preval != value) {
						me.onFilterChange(itemOption.field, value, this);
						item.input.preval = value;
					}
					
				});
				
				this.items[itemOption.field] = item;
				group.append(item);
				item.group = group;
			}


		},
		
		deleteFilter: function(itemOption) {
			var item = this.items[itemOption.field];
			if (item) {
				item.remove();
			}
		},
		getItem : function(field) {
			var item = this.items[field];
			return item;
		},
		getValue: function (field) {
			var value;
			var oneitem = this.items[field];
			if ("select" == oneitem.type) {
				value = $("#select_ option:selected", oneitem).val();
			}
			else {
				value  = $("input", oneitem).val().trim();
			}
			return value;
		},
		getOnlyFilterUrl : function() {
			//不含必填字段
			var filter = "";
			for(var one in this.items) {
				var val = this.getValue(one);
				if(typeof(val) == "undefined" || val == "" || val.toLowerCase() == "all") {
					continue;
				}
				
				if(this.items[one].must  && this.items[one].must == false) {
					var dateType = this.items[one].type;
					if("select" == dateType || "date" == dateType || this.items[one].equal == false) {
						filter += "and " + one + " like '%" + val + "%'";
					}else {
						filter += "and " + one + "='" + val + "'";
					}
				}
				
			}
			return filter.substring(4,filter.length) == "" ? "1=1" : filter.substring(4,filter.length);
		},
		getFilterUrl: function(raw) {
			//含必填字段 不含peroid头
			if (!raw) {
				raw = false;
			}
			var url = "";
			var filter = "";
			for(var one in this.items) {
				var val = this.getValue(one);
				var group = this.items[one].group;

				var oneFilterName ="";
                var groupCode = group.code;

				 if(raw && "peroid" != groupCode && "area" != groupCode){
					oneFilterName = groupCode + "." + one;
				}else{
                    oneFilterName = one;
                }

				if(typeof(val) == "undefined" || val == "" || val.toLowerCase() == "all") {
					continue;
				}
				
				if(this.items[one].must  && this.items[one].must == true) {
					url += "&" + oneFilterName + "=" +  val;
				}else {
					var dataType = this.items[one].type;
					if(!dataType) {
						filter += " and " + oneFilterName + " like '%" + val + "%'";
					}
					else if ("select" == dataType || "date" == dataType){
                        filter += " and " + oneFilterName + "='" + val + "'";
					}else {
                        filter += " and " + oneFilterName + " like '%" + val + "%'";
					}

				}
			}
			
			if(filter == "") {
				url = url.substring(1,url.length);
			}
			else {
				url = url.substring(1,url.length);
				url += "&filter=" + filter.substring(4,filter.length);
			}
			return url;
		},
		getFilterWithPeroidUrl: function(raw) {
			//含必填字段 含 peroid头
			if (!raw) {
				raw = false;
			}
			var url = "";
			var filter = "";
			for(var one in this.items) {
				var val = this.getValue(one);
				var group = this.items[one].group;

				var oneFilterName ="";
                var groupCode = group.code;

				 if(raw && "area" != groupCode){
					oneFilterName = groupCode + "." + one;
				}else{
                    oneFilterName = one;
                }

				if(typeof(val) == "undefined" || val == "" || val.toLowerCase() == "all") {
					continue;
				}

				if(this.items[one].must  && this.items[one].must == true) {
					url += "&" + oneFilterName + "=" +  val;
				}else {
					var dataType = this.items[one].type;
					if(!dataType) {
						filter += " and " + oneFilterName + " like '%" + val + "%'";
					}
					else if ("select" == dataType || "date" == dataType){
                        filter += " and " + oneFilterName + "='" + val + "'";
					}else {
                        filter += " and " + oneFilterName + " like '%" + val + "%'";
					}

				}
			}

			if(filter == "") {
				url = url.substring(1,url.length);
			}
			else {
				url = url.substring(1,url.length);
				url += "&filter=" + filter.substring(4,filter.length);
			}
			return url;
		},
		resetItemUrl: function (field, url) {
			var me = this;
			var item = this.getItem(field);
			var itemOption = item.option;
            itemOption.url = url;
			if (itemOption.type == "date") {
                if(url) {
                    Server.getData(url, function(data) {
                        if (data && data.length == 1) {
                            var line = data[0];
                            var date = line[itemOption.field.toLowerCase()];
                            item.input.val(date);
                            me.onFilterChange(itemOption.field, date, this);
                        }
                    });
                }else {
                    var datetime = new DateTime();
                    var endtime = datetime.str;
                    item.input.val(endtime);
                }

			}else if (item.option.type == "select"){
                var input = $("#select_", item);
                if(url) {

                    input.empty();
                    Server.getData(url, function(data) {
                        if (data && data.length) {
                            for (var i = 0; i < data.length; i++) {
                                var line = data[i];
                                var option = $("<option ></option>");

                                option.val(line[(itemOption.field).toLowerCase()]);
                                option.html(line[(itemOption.showField).toLowerCase()]);

                                input.append(option);
                            }
                        }
                    });
                }else if (data && data.length) {
                    for (var i = 0; i < data.length; i++) {
                        var line = data[i];
                        var option = $("<option ></option>");

                        option.val(line.code);
                        option.html(line.name);

                        input.append(option);
                    }
                }

			}else {
				//text
			}
        }
	};
	
//4.AxisArea                                        =========
	
	AxisArea = {
		items: {},
		buttons: {},
		element: null,
		
		init: function(options) {
			this.options = options;
			this.element = $(options.element);

            this.axisX1 = $("#axisX-1", this.element);
            this.axisX2 = $("#axisX-2", this.element);
            this.axisY1 = $("#axisY-1", this.element);
            this.axisY2 = $("#axisY-2", this.element);

            this.createButtons();
            this.createAxises(this.options.axises);

            this.createDragEvent();
		},

        createButtons: function() {
			this.createOneButton("axisY-add", {segments: ["y-segment1", "y-segment2"], axises: [this.axisY1, this.axisY2], operation: "expand"});
            this.createOneButton("axisY-delete", {segments: ["y-segment1", "y-segment2"], axises: [this.axisY1, this.axisY2], operation: "collapse"});
            this.createOneButton("axisX-add", {segments: ["x-segment1", "x-segment2"], axises: [this.axisX1, this.axisX2], operation: "expand"});
            this.createOneButton("axisX-delete", {segments: ["x-segment1", "x-segment2"], axises: [this.axisX1, this.axisX2], operation: "collapse"});
		},

		createOneButton: function(btnCode, option) {
            var btn = this.buttons[btnCode] = $("#" + btnCode, this.element);
            var me = this;
            btn.click(function() {
				if ("expand" == option.operation) {
                    $("#" + option.segments[0]).width("50%");
                    $("#" + option.segments[1]).show();
				}
				else if ("collapse" == option.operation) {
                    me.transferFields("moveTo", option.axises[1], option.axises[0]);

                    $("#" + option.segments[0]).width("100%");
                    $("#" + option.segments[1]).hide();
                }
			});
		},

        transferFields: function(operation, from, to) {
			if ("move" == operation) {
				var items = from.items;
				for (var no in items) {
                    items[no].remove();
                    to.append(items[no]);
				}
                from.items = [];
			}
			else if ("exchange" == operation) {

			}
		},

        createAxises: function(axises) {
            var route = {
                x: {count: 0, axis: [this.axisX1, this.axisX2]},
                y: {count: 0, axis: [this.axisY1, this.axisY2]}
            };

            for (var i = 0; i < axises.length; i++) {
                var axisOption = axises[i];
                var type = axisOption.axis;

                var index = route[type];
                var axis = route[type].axis[index];
                route[type].count = route[type].count + 1;

                this.createOneAxis(axis, index, axisOption);

            }
        },

        createOneAxis: function(axis, index, option) {
            this.addAxisFields(axis, option.fields);
        },

       addAxisFields: function(axis, fields) {
            if (!fields) {
                return;
            }

            for (var i = 0; i < fields.length; i++) {
                this.addOneAxisField(axis, fields[i]);
            }
        },

        addOneAxisField: function(axis, option) {
            var me = this;

            var fieldItem = $([
                "<div class='axix-field'>",
                	"<div id='text'></div><span class='iconfont axix-field-close icon-guanbi' style='font-size: 10px'></span>",
                "</div>"
            ].join(""));

            var text = fieldItem.text = $("#text", fieldItem);
            text.html(option.caption || option.name);

            fieldItem.click(function() {
                if (me.options.onFieldClick) {
                    me.options.onFieldClick.call(me, option.name, option);
                }
            });

            axis.append(fieldItem);
            this.items[option.name] = fieldItem;
        },

        createDragEvent: function() {
            var me = this;

            var fieldItem =[
                "<div id='selectedItem' class='selectedItem'>",
					"",
            		"<span onclick='close()' class='closeicon'></span>",
                "</div>"
            ];


            this.axisX1.droppable({
                scope: "axis",
                activeClass: "axis-hotBody",
                drop: function(event, ui) {
                    fieldItem[1] = ui.draggable.text().replace("：", "");
                    var value = fieldItem.join("");
                    me.axisX1.html(me.axisX1.html() + value);
                }
            });
            this.axisX2.droppable({
                scope: "axis",
                activeClass: "axis-hotBody",
                drop: function(event, ui) {
                    fieldItem[1] = ui.draggable.text().replace("：", "");
                    var value = fieldItem.join("");
                    me.axisX2.html(me.axisX2.html() + value);
                }
            });

            this.axisY1.droppable({
                scope: "axis",
                activeClass: "axis-hotBody",
                drop: function(event, ui) {
                    fieldItem[1] = ui.draggable.text().replace("：", "");
                    var value = fieldItem.join("");
                    me.axisY1.html(me.axisY1.html() + value);
                }
            });
            this.axisY2.droppable({
                scope: "axis",
                activeClass: "axis-hotBody",
                drop: function(event, ui) {
                    fieldItem[1] = ui.draggable.text().replace("：", "");
                    var value = fieldItem.join("");
                    me.axisY2.html(me.axisY2.html() + value);
                }
            });
        }

    };
	
	
//5.Spliter                                        =========
	
	Spliter = {
		element: null,
		leftEl: null,
		rightEl: null,
		width: 4, 
		
		init: function(option) {
			var me = this;
			
			this.element = $(option.element);
			this.leftEl = $(option.left);
			this.rightEl = $(option.right);
			
			this.element.draggable({
				axis: "x",
				zIndex: 200,
				containment: "document",
				stop: function(event, ui) {
					var left = ui.position.left;
					me.reposition(left);
				}
    		});
		},
	
		reposition: function(left) {
			if (left <= 0) {
				left = 3;
				this.setElementLeft(this.element, left);
			}
			
			this.leftEl.width(left);
			this.setElementLeft(this.rightEl, left + this.width);
		},
		
		setElementLeft: function(el, left) {
			var offset = el.offset();
			offset.left = left;
			el.offset(offset);
		}
	};
	function toggleleft() {
		var width;
        if(olapFiltersHide) {
        	 width = $("#area-olap").data("prewidth");
        
        }else {
        	var prewidth = $("#area-olap").width();
        	$("#area-olap").data("prewidth", prewidth);
        	width = 0;
        }
        
       	var leftshow ={
        	 	width: width
      	 };
      	  var leftoffset ={
      	 	left: width
      	 };
      	 $("#area-olap").animate(leftshow, "normal", null);
      	 $("#area-content").animate(leftoffset, "normal", null);
         olapFiltersHide = !olapFiltersHide;
	}