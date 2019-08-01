(function ($) {
	
	$.fm.Grid = Control.subClass({
		template: [
  	      	'<div id="grid-body" class="grid-body_min">',
				'<table id="tab_self" cellpadding=0 cellspacing=0 class="tab_self_min">',
					'<thead id="table_header" class="tab_self_header_min">',
						'<tr id="header-row" class="grid-header"></tr>',
						'<tr id="header-row1" class="grid-header"></tr>',
					'</thead>',	
					'<tbody id="table_body" class="tab_self_body_min">',
					'</tbody>',
				'</table>',
			'</div>',
		],
		template_footer: [
	  		'<div class="grid-bar" id="grid-toolbar">',
				'<div class="grid-bar-placeholder" id="grid-toolbar-placehoder">',
					'<label id="grid-bar_message"></label>',
				'</div>',
				'<div class="grid-bar-area">',
				    '<div class="grid-bar-group">',
			    		'<div class="grid-button-outer" id="grid-first-outer">',
			    			'<span class="grid-button grid-btn-first" id="grid-first"></span>', 
			    		'</div>',
			    		'<div class="grid-button-outer" id="grid-prior-outer">',
		    				'<span class="grid-button grid-btn-prior" id="grid-prior"></span>',		    		
			    		'</div>',
			    		'<div class="grid-button-outer" id="grid-next-outer">',
	    					'<span class="grid-button grid-btn-next" id="grid-next"></span>',		    		
			    		'</div>',
			    		'<div class="grid-button-outer" id="grid-last-outer">',
							'<span class="grid-button grid-btn-last" id="grid-last"></span>',		    		
			    		'</div>',
			    	'</div>',		 
			   	
				   	'<div class="grid-bar-group">',
				   		'<input type="text" class="grid-bar-edit" id="currentPage" value="1"/>',	
			    		'/',			   		
		    			'<label class="grid-bar-label" id="totalPage">0</label>',			    	
			    	'</div>',
		    		
			       	'<div class="grid-bar-group">',
			    		'<div class="grid-button-outer" id="grid-load-outer">',
							'<span class="grid-button grid-btn-load" id="grid-load"></span>',		    		
			    		'</div>',
			       	'</div>',
			       	
			       	'<div class="grid-bar-group">',
				       	'<input  type="number" min="1" class="grid-button grid-bar-edit" id="setGridRowno" style="width: 45px"/>',    		
			       	'</div>',
				'</div>',
		     '</div>'		                  
		],
		
		template_header_indicator: [
		    '<td class="grid-indicator">',
		    	'<div id="header-indicator" class="grid-hd-indicator-inner"></div>',
		    '</td>'
		],
		
		template_header_cell: [
			'<td class="grid-hd-cell">',
				'<div id="header-cell" class="grid-hd-cell-inner"></div>',
			'</td>'
		],
		
		template_body_indicator: [
		    '<td class="grid-cell">',
				'<div id="indicator-body" class="grid-cell-inner grid-indicator-unselected" />',
			'</td>'
		],	
		
		init: function(options) {
    		this.headerHeight = 32;
    		this.footerHeight = 31;
    		this.gridContentHeight;
    		this.limit = 15;
    		this.maxlineno = 50;
    		this.lineno = 15;
    		this.colunmUnitWidth = 100;
    		this.indicatorWidth = 30;
			this.rowHeight = 23.4;
			this.editable = false;
			this.editors = {};
			this.usedWidth = 0;
			this.showPage = false;
			this.hasSubTitle = false;
			this.showIndicator = true;
			this.showToolBar = true;
			this.multiSelect = false;//是否可多选
			this.singleSelect = true;
			this.enableAllSelect = false;//是否全选
			this.selected = null;
			this.selectedArray = [];
			this.dataObject = [];
			this.rows = [];
			this.headerFixed = true;  //表头是否固定
			this.isDragWidth = true;  //宽度是否可拖动

    		Control.call(this, options);
    		if(!this.gridContentHeight) {
    			this.gridContentHeight = $(this.element).parent().height() - 2;
    		}
    		    		
    		this.createElements();
    		this.refresh();
    	},
    	
    	createElements: function() {
    		var me = this;
    		//1.
    		this.canvas.addClass("grid");
    		//this.bodyHeight = this.rowHeight * this.limit_;
    		//this.height = this.headerHeight + this.bodyHeight;
    		
    		this.bodyHeight = this.gridContentHeight - this.headerHeight - this.footerHeight;
    		this.height = this.gridContentHeight - this.footerHeight;
    		//2.
    		this.element = $(this.template.join(""));
    		this.element.tab_self = $("#tab_self", this.element);
    		this.element.table_header = $("#table_header", this.element);
    		this.element.table_body = $("#table_body", this.element);
    		
    		this.setGridType(this.headerFixed);
    		
    		this.canvas.append(this.element);
    		
    		if (me.isDragWidth) {
    			$("body").append("<p id='line' style='width:1px;border-left:1px solid #aaa; position:absolute;display:none' ></p> ");
    		}
    		
    		this.createHeader(this.canvas);
    		this.createBody(this.canvas);
    		
    		if (this.showToolBar) {
    			if (this.customFooter) {
    				this.customFooter = this.getEl(this.customFooter);
                	this.createCustomFooter(this.canvas);
        		}
    			else {
    				this.createDefaultFooter(this.canvas);
    			}
    		} else {
    			if (this.customFooter) {
    				this.customFooter = this.getEl(this.customFooter);
    				this.customFooter.hide();
    			}
    		}
    		
    		this.tableBody.scroll(function() {
    			var left = $(this).scrollLeft();
				me.table_header.scrollLeft(left);
				
				me.submitCell();
    		});
    	},
    	
    	setGridType: function(headerFixed) {
    		if (headerFixed) {
    			this.element.attr("class", "grid-body");
    			this.element.tab_self.attr("class", "tab_self");
    			this.element.table_header.attr("class", "tab_self_header");
    			this.element.table_body.attr("class", "tab_self_body");
    		}
    	},
    	
    	createHeader: function(area) {
    		var columns = this.columns, me = this, rowspan = 1;
    		if (!columns) return;
    		
    		var tableEl = $('#tab_self', area);
    		var header = $('.grid-header', area);
    		header.height(this.headerHeight);
    		header.css("line-height", this.headerHeight + "px");
    		
    		var row = $('#header-row', area);
    		var row1 = $('#header-row1', area);
    		
    		if (this.titleClass) {
    			row.attr("class", this.titleClass);
    			row1.attr("class", this.titleClass);
    		}
    		
    		
    		this.table_header = $('#table_header', area);
    		
    		if(!this.hasSubTitle) {
    			row1.hide();
    		}
    		else{
    			rowspan = 2;
    		}
    		
			//1.
    		if (this.showIndicator) {
    			var template = $(this.template_header_indicator.join(""));
    			template.attr("rowspan", rowspan);
    			
    			var indicator = this.headerIndicator = $('#header-indicator', template);
    			
    			if (this.enableAllSelect) {
    				indicator.addClass("grid-indicator-unselected");
    				indicator.click(function() {
    					me.toggleAllSelected();
    				});
    			}
    			
    			indicator.width(this.indicatorWidth);
    			row.append(template);
    		}
			
			//2.
    		var flag = false, m = 0, colspan, pcolumn;
        	for (var i = 0; i < columns.length; i++) {
        		var column = columns[i];
        		this.usedWidth = this.usedWidth + column.width;
        		var cell = null;
        		
        		if(column.parent) {
        			flag = true, m++;
        			
        			if(column.colspan) {
        				colspan = column.colspan;
        				pcolumn = column;
        			}
        			
        			cell = $(this.template_header_cell.join(""));
        			cell.attr("index", i);
        			
            		cell.body = $('#header-cell', cell);
            		cell.body.width(column.width);
            		cell.body.html(column.caption);

        			if (me.isDragWidth) {
        				cell.body.height(this.headerHeight);
                		cell.body.css("overflow", "hidden");
            			this.addDragWidth(tableEl, i, cell, this);
            		}
            		
            		row1.append(cell);
        		}
        		else{
        			cell = $(this.template_header_cell.join(""));
        			cell.attr("rowspan", rowspan);
        			cell.attr("index", i);
        			
            		cell.body = $('#header-cell', cell);
            		cell.body.width(column.width);
            		cell.body.html(column.caption);
            		
            		if (me.isDragWidth) {
            			cell.body.height(this.headerHeight);
                		cell.body.css("overflow", "hidden");
            			this.addDragWidth(tableEl, i, cell, this);
            		}
            		
            		row.append(cell);
        		}
        		
        		if(flag && m % colspan == 0) {
        			row.append("<td colspan='" + colspan + "' align='center' style='border-bottom: 1px solid #A3C0E8; border-right: 1px solid #A3C0E8'>" + pcolumn.parent + "</td>");
        			flag = false;
        			m = 0;
        		}
        	}
    	},
    	
    	addDragWidth: function(tableEl, index, cell, me) {
    		var tTD = cell[0];
    		var tTDwidth = tTD.offsetWidth;
    		var oldX = tTD.oldX;
    		var lineMove = false;
    		$("body").mousemove(function(event) {
				if (lineMove == true) {
					$("#line").css({"left" : event.clientX}).show();
				}
			});
    		
    		$("body").mouseup(function(event) {
    			$("body").removeClass("none-select");
    			
				if (lineMove == true) {
					$("#line").hide();
					lineMove = false;
					var pos = currTh.offset();
					var Th_index = currTh.prevAll().length;
					var index = currTh.attr("index") * 1 + 1;
					if (!me.showIndicator) {
						index = currTh.attr("index") * 1;
					}
					
					currTh.parent().each(function() {
						$(this).children().eq(Th_index).width(event.clientX - pos.left);
						$(this).children().eq(Th_index).children().width(event.clientX - pos.left-2);
					});
					currTh.parent().parent().parent().find("tbody tr").each(function() {
						$(this).children().eq(index).width(event.clientX - pos.left);
						$(this).children().eq(index).children().width(event.clientX - pos.left-2);
					});
				}
			});
    		
    		cell.mousedown(function() {
    			var th = $(this);
    			var pos = th.offset();
				if (event.clientX - pos.left < 4 || (th.width() - (event.clientX - pos.left)) < 4) {
					$("body").addClass("none-select");
					var height = th.parent().parent().parent().height();
					var top = pos.top;
					$("#line").css({
						"height" : height,
						"top" : top,
						"left" : event.clientX,
						"display" : ""
					});
					//全局变量，代表当前是否处于调整列宽状态
					lineMove = true;
					//总是取前一个TH对象
					if (event.clientX - pos.left < th.width() / 2) {
						currTh = th.prev();
					} else {
						currTh = th;
					}
				}
			});
    		
			cell.mouseup(function() {
				$("body").removeClass("none-select");
				
				if (lineMove == true) {
					$("#line").hide();
					lineMove = false;
					var pos = currTh.offset();
					var Th_index = currTh.prevAll().length;
					var index = currTh.attr("index") * 1 + 1;
					currTh.parent().parent().each(function() {
						$(this).children().eq(Th_index).width(event.clientX - pos.left);
						$(this).children().eq(Th_index).children().width(event.clientX - pos.left-2);
					});
					
					currTh.parent().parent().parent().find("tbody tr").each(function() {
						$(this).children().eq(index).width(event.clientX - pos.left);
						$(this).children().eq(index).children().width(event.clientX - pos.left-2);
					});
				}
			});
			cell.mousemove(function() {
				//更改鼠标样式   
				var th = $(this);
    			var pos = th.offset();
				if (event.clientX - pos.left < 4 || (th.width() - (event.clientX - pos.left)) < 4)
					this.style.cursor = 'e-resize';
				else
					this.style.cursor = 'default';
			});
    	},
    	
    	createBody: function(element, columns) {
    		this.body = $('#grid-body', element);
    		this.body.height(this.height);
    		
    		this.tableBody = $('#table_body', element);
    		this.table_header = $('#table_header', element);
    		this.tableBody.css("top", this.table_header[0].clientHeight);
    		this.tableBody.height(this.height - this.table_header[0].clientHeight);
    	},
    	
    	createDefaultFooter: function(element) {
    		var me = this;
    		var footer = $(this.template_footer.join(""));
    		var toolbar = element.toolbar = $('#grid-toolbar', element);
    		
    		var placeholder =  $('#grid-toolbar-placehoder', footer);
    		placeholder.width(200);
    		
    		var btn_first = toolbar.btn_first = $('#grid-first', footer);
    		var btn_prior = toolbar.btn_prior = $('#grid-prior', footer);
    		var btn_next = toolbar.btn_next = $('#grid-next', footer);
    		var btn_last = toolbar.btn_last = $('#grid-last', footer);
    		var btn_load = toolbar.btn_load = $('#grid-load', footer);
    		
    		var setGridRowno = toolbar.setGridRowno = $('#setGridRowno', footer);
    		if (me.setGridRownoEvent) {
    			setGridRowno.val(this.lineno);
    		}
    		else {
    			setGridRowno.hide();
    		}
    		
    		btn_load.active = true;
    		var currentPage = toolbar.currentPage = $('#currentPage', footer);
    		var totalPage = toolbar.totalPage = $('#totalPage', footer);    		
    		var information = toolbar.information = $('#grid-bar_message', footer);
    		
    		me.initButton(btn_first, me.firstPage);
    		me.initButton(btn_prior, me.priorPage);
    		me.initButton(btn_next, me.nextPage);
    		me.initButton(btn_last, me.lastPage);    		
    		me.initButton(btn_load, me.refresh);
    		
    		$('#currentPage', footer).change(function(){
    			if(Math.ceil($(this).val())>totalPage.text()*1){
    				$(this).val(totalPage.text());
    			}else if(Math.ceil($(this).val())<=1){
    				$(this).val(1);
    			}else{
    				$(this).val(Math.ceil($(this).val()));
    			}
    			me.pageTo($(this).val());
    		});
    		
    		if (me.setGridRownoEvent) {
    			setGridRowno[0].onchange=function(){
    				var thislineno = 0;
    				if (Math.ceil(setGridRowno.val()) > me.maxlineno) {
    					thislineno = me.maxlineno;
    				}
    				else {
    					thislineno = Math.ceil(setGridRowno.val()) || me.lineno;
    				}
    				
					var no = thislineno;
					setGridRowno.val(no);
					me.setLineno(no);
					me.setGridRownoEvent();
				};
			};
    		
    		information.html("没有数据");
    		element.append(footer);
    	},
    	
    	setLineno: function(no) {
    		return this.lineno = no;
    	},
    	getLineno: function() {
    		return this.lineno;
    	},
    	
    	createCustomFooter: function(canvas, array) {
    		this.customFooter.remove();
    		this.canvas.append(this.customFooter);
    		this.customFooter.show();
    	},
    	
    	setURL: function(url) {
    		this.url = url;
    		this.refresh();
    	},
    	
    	refresh: function() {
			this.tableBody.empty();
    		var me = this;
    		this.selected = null; this.selectedArray = [];
    		
    		if (!this.url) {
    			return;
    		}
    		Loading.show("加载中。。。");
    		Server.getData(this.url, function(data, page) {
    			Loading.hide();
    			
				if (me.onGetData) {
					data = me.onGetData(data);
				}
				me.loadData(data, page);
			});			
		},
		
		fireDataChange: function() {
			if (this.onDataChagne) {
				this.onDataChagne();
			} 
		},
		
		loadData: function(array, page) {
			this.tableBody.empty();
			this.rows = [];
			this.dataObject = [];
		
			if (!array || !array.length){
				return;
			}
			if(page.total) {
                $('#grid-bar_message', this.canvas).html("总共 " + page.total + " 条记录");
			}else {
                $('#grid-bar_message', this.canvas).html("总共 " + array.length + " 条记录");
			}

        	
        	for (var m = 0; m < array.length; m++) {
        		var data = array[m];
        		var row = this.loadLine(data, m, this.table_header);
        		
        		this.tableBody.append(row.element);
        		this.rows.push(row);
        		this.dataObject.push(row.data);
        		this.dataObject.changed = true;
        	//	this.fireDataChange();
        	}
        	
        	if (this.showPage && page) {
        		this.loadPage(page);
        	}
        	
        	if (this.afterLoad){
        		this.afterLoad(array, this.selected);
        	}
        	
        	if (this.defaultSelectedIdx) {
        		this.select(this.defaultSelectedIdx - 1);
        	}
        	
        	if (this.columnsAverage) {
        		this.columnsAverage(this.rows);
        	}
        	
        	this.bodyIndicator = $("#indicator-body", this.canvas);
		},
		
		loadLine: function(data, index, table_header) {
			var me = this; var columns = this.columns;
			
        	var row = {"index": index};
       		
        	var rowEl = $('<tr class="l-grid-row"></tr>');
    		rowEl.data('row', row);
    		
    		/*if (!me.doubleClickSelect) {
        		rowEl.click(function() {
        			if(me.singleSelect)
        				me.setRowSelected.call(me, $(this).data('row'));
        		});
    		}
    		else {
        		rowEl.dblclick(function() {
        			if(me.singleSelect)
        				me.setRowSelected.call(me, $(this).data('row'));
        		});
    		}
    		*/
    		rowEl.hover(
    			function() {
    				$(this).addClass("grid-row-over");
    			},
    			function() {
    				$(this).removeClass("grid-row-over");
    			}
    		);
    		
    		row['element'] = rowEl;
    		var dataObject = new DataObject(data);
    		dataObject.onDataChange = function() {
    			me.refreshLine(row, rowEl);
    		};
    		row['data'] = dataObject;
    		
    		
    		if (this.onLineRender) {
    			this.onLineRender(row, data);
    		}
    		
    		var lineCells = me.loadLineCells(row, data, columns, table_header, me);
    		row['cells'] = lineCells.cells;
    		row['rowEditors'] = lineCells.editors;
    		
    		if (row.select) {
    			row.select = false;
    			this.setRowSelected(row);
    		}
    		
    		return row;
		},
		
		loadLineCells: function(row, data, columns, table_header, gridme) {
			var lineCells = {};
			var cells = new Array(length);
			var editors = {};
			
			//1.
			if (this.showIndicator) {
				var indicator = $(this.template_body_indicator.join(""));
				row.indicator = $("#indicator-body", indicator);
				
				row.indicator.width(this.indicatorWidth);
				row.element.append(indicator);
				
			}
			
			//2.
        	for (var i = 0; i < columns.length; i++) {
        		var column = columns[i];
        		var thead_cell = $("td[index=" + i + "]", table_header);
        		var cell = new $.fm.Grid.Cell({
        			grid: this,
        			column: column,
        			row: row,
        			gridme: gridme,
        			value: data[column.field]
        		});
        		cell.element.attr("index", i);
        		if (this.isDragWidth) {
        			if (thead_cell && thead_cell[0].offsetWidth) {
        				cell.element.css("width", thead_cell[0].offsetWidth);
        				cell.element.children().css("width", thead_cell[0].offsetWidth - 1);
        			}
        		}
/*        		if (thead_cell.css("width")) {
        			cell.element.css("width", thead_cell.css("width"));
        		}
*/        		
        		row.element.append(cell.element);
        		
        		cells[i] = cell;
        		editors[column.field] = cell;
        	}	
        	lineCells.cells = cells;
        	lineCells.editors = editors;
        	return lineCells;
		},
		
		refreshLine: function(row, rowEl) {
			var cells = row.cells;
			
			for (var i = 0; i < cells.length; i++) {
				var cell = cells[i];
				cell.refresh();
			}
			
		},
		
		loadPage: function(page) {
			this.page = page; var bar = this.canvas.toolbar;
			
			bar.currentPage.val(page.pageno);
			bar.totalPage.text(page.pagecount);
			
			var message = '没有数据';
			if (page.pagecount > 0) {
				message = '[ 显示' + page.beginrecordno + '-' + page.endrecordno + ' / ' + page.recordcount + '条 ]';				
			}
			
			bar.information.html(message);	
			
			this.setButtonActive(bar.btn_first, page.pageno > 1);
			this.setButtonActive(bar.btn_prior, page.pageno > 1);
			this.setButtonActive(bar.btn_next, page.pageno < page.pagecount);			
			this.setButtonActive(bar.btn_last, page.pageno < page.pagecount);	
		},

		pageTo: function(pageNo) {
			var start = this.url.indexOf('pageno');
			var end = this.url.indexOf('&', start);
			if(end == -1) {
				end = this.url.length;
			}
			var replaceStr = this.url.substring(start, end);
			if(start != -1) {
				this.url = this.url.replace(replaceStr, 'pageno=' + pageNo);
			}
			else {
				this.url += '&pageno=' + pageNo + "&pagesize=20";
			}
			
			
			this.refresh();
		},
		
		firstPage: function() {
			this.pageTo(1);
		},
		
		priorPage: function() {
			this.pageTo(this.page.pageno - 1);
		},
		
		nextPage: function() {
			this.pageTo(this.page.pageno + 1);
		},
		
		lastPage: function() {
			this.pageTo(this.page.pagecount);
		},
		
		setRowSelected: function(row, call) {
			if (this.enableAllSelect && this.allSelect) {
				//this.clearAllSelected();
			}
			
			if ($.isNumeric(row)) {
				row = this.rows[row];
			}
			
			if (!row.select) {
				if (this.selected) {
					this.doSetRowUnselected(this.selected);
				}
				
				this.doSetRowSelected(row, call);
			}
			else {
				this.doSetRowUnselected(row);
				this.allSelect = false;
			}
			
			if (this.onSelect) {
				this.onSelect(this.allSelect, this);
			}
		},
		
		toggleAllSelected: function() {
			if (!this.multiSelect) {
				return;
			}
			
			//1. clear single selected
			if (this.selected) {
				this.doSetRowUnselected(this.selected);
			}

			//2. set select
			var select = this.allSelect = !this.allSelect;
			
			var rows = this.rows;
			for (var i = 0; i < rows.length; i++) {
				rows[i].select = select;
			}
			
			//3. toggle class
			if (select) {
				this.headerIndicator.removeClass("grid-indicator-unselected");
				this.headerIndicator.addClass("grid-indicator-selected");
				
				this.bodyIndicator.removeClass("grid-indicator-unselected");
				this.bodyIndicator.addClass("grid-indicator-selected");
			}
			else {
				this.headerIndicator.removeClass("grid-indicator-selected");
				this.headerIndicator.addClass("grid-indicator-unselected");	
				
				this.bodyIndicator.removeClass("grid-indicator-selected");
				this.bodyIndicator.addClass("grid-indicator-unselected");
			}
			
			//4. fire event				
			if (this.onSelect) {
				this.onSelect(this.allSelect, this);
			}
		},		
		
		doSetRowSelected: function(row, call) {
			//1. add to selected
			row.select = true;
			if (!this.multiSelect) {
				this.selected = row;
			}
			
			//2. set selected class
			if (row.indicator) {
				row.indicator.removeClass("grid-indicator-unselected");
				row.indicator.addClass("grid-indicator-selected");
			}
			row.element.addClass("grid-row-selected");
			
			//3. fire event
			//notOnRowSelected单元格属性：设置该单元格没有行点击事件grid.onRowSelected
			if ($(call.element[0]).find("a").length > 0 || $(call.element[0]).find("button").length > 0 || call.column.notOnRowSelected) {
				return;
			}
			else if (this.onRowSelected) {
				this.onRowSelected(row);
			}
		},
		
		doSetRowUnselected: function(row, call) {
			//1. set selected null
			row.select = false;
			this.selected = null;
			
			//2. remove selected class
			if (row.indicator) {
				row.indicator.removeClass("grid-indicator-selected");
				row.indicator.addClass("grid-indicator-unselected");
			}
			row.element.removeClass("grid-row-selected");
			
			//3. fire event
			if (!call) {
				return;
			}
			if ($(call.element[0]).find("a").length > 0 || $(call.element[0]).find("button").length > 0 || call.column.notOnRowSelected) {
				return;
			}
			else if (this.onRowSelected) {
				this.onRowSelected(row);
			}
		},
		
		getSelectedRow: function() {
			//1. single select
			if (!this.multiSelect) {
				return this.selected ? this.selected : null;
			}
			
			//2. multiple select
			var result = [], rows = this.rows;
			for (var i = 0; i < rows.length; i++) {
				var row = rows[i];
				if (row.select) {
					result.push(row);
				}
			}
			return result;
		},
		
		getSelected: function() {
			//1. single select
			if (!this.multiSelect) {
				return this.selected ? this.selected.data : null;
			}
			
			//2. multiple select
			var result = [], rows = this.rows;
			for (var i = 0; i < rows.length; i++) {
				var row = rows[i];
				if (row.select) {
					result.push(row.data);
				}
			}
			return result;
		},
		
    	initButton: function(button, handler) {
			var me = this;
			
			button.hover(
				function(){			
					if (button.active) {
						button.addClass('grid-button-over');
					}
				}, function(){
					button.removeClass('grid-button-over');    			
				}
			); 
			
			button.click(function() {
				if (button.active) {
					handler.call(me);
				}
    		});
    	},
    	
		setButtonActive: function(button, active) {
			button.active = active;
			
			if (active) {
				button.removeClass("grid-btn-disable");
			}
			else {
				button.addClass("grid-btn-disable");
			}
		},
		
		editCell: function (cell, e) {
			/*if (this.activeCell == cell) {
				return;
			}*/
			
			var me = this;
			
//			var submitResult = me.submitCell();
//			if (!submitResult) return;
			
			me.activeCell = cell;
			
            var column = cell.column;
            var editor = me.editors[column.type];
            
      //      if (!editor) {
      //      	editor = ControlCreator.create(column); 
            	if (column.type == "text") {
            		editor = $("input", ControlCreator.create(column).editor);
            	}
            	else if (column.type == "select") {
            		editor = ControlCreator.create(column, null, cell).input;
            	}
            	else {
            		editor = $("input", ControlCreator.create(column).editor);
            	}
            	editor.height(this.rowHeight);
            	
            	editor.on("blur", function() {
            		return me.submitCell();
            	});
            	
            	this.canvas.append(editor);
            	me.editors[column.type] = editor;
      //      }
            	
            var offset = cell.element.offset();
            var parentOffset = me.canvas.offset();
            
            editor.val(cell.value);
            editor.css({
                position: 'absolute',
            	left: offset.left - parentOffset.left,
            	width: column.width - 2,
            	top: offset.top - parentOffset.top + 1
            });
            
//        	RootParent.registerActiveEditor(editor);
            editor.focus();
            
            me.activeEditor = editor;
		},
		
        submitCell: function () {
			var success = false;
			
			var activeEditor = this.activeEditor;
			
			if (!this.activeCell || !activeEditor) return true;
			
			var value = activeEditor.val();
			var selectvalue = null;
			if (this.activeCell.column.type == "select") {
				value = activeEditor.find("option:selected").text();
				selectvalue = activeEditor.find("option:selected").val();
			}
			
			var text = activeEditor.getText ? activeEditor.getText() : null;
			
			var eventResult = this.fire('setValue', this.activeCell, value, text);
			
			if (eventResult == undefined || eventResult == null || eventResult) {
				this.activeCell.setValue(value, text);
				if (this.activeCell.column.onChanged) {
					this.activeCell.column.onChanged(value, this.activeCell.row, this.activeCell.column, this, selectvalue);
				}
				this.activeEditor.hide();
				this.fireDataChange();
				
				this.activeCell = null;
				this.activeEditor = null;
				
				success = true;
			}
			
			return success;
        },
        
        getCall: function(row, field) {
        	var cells = row.cells;
        	
        	var cell = row.rowEditors[field];
        	if (cell) {
        		return cell.value;
        	}
        	
        	return null;
        },
        
        setCall: function(row, field, value) {
        	var cells = row.cells;
        	
        	var cell = row.rowEditors[field];
        	if (cell) {
        		cell.setValue(value);
        	}
        },
        
        callRowFunction: function(eventFunc, internalRowFunc, record, index) {
        	var me = this;
        	
        	if (eventFunc) {
        		eventFunc(record, function() {
        			internalRowFunc.call(me, record, index);
        		});
        	}
        	else {
        		internalRowFunc.call(me, record, index);
        	}
        	
        	this.fireDataChange(record);
        },
        
        appendRow: function(record) {
        	this.callRowFunction(this.onAddRow, this.doAppendRow, record, 0);
        },
        
        doAppendRow: function(record) {
        	var index = this.rows.length;
        	var row = this.loadLine(record, index);
        	
        	this.tableBody.append(row.element);
    		this.rows.push(row);
    		this.dataObject.push(row.data);
    		this.dataObject.changed = true;
    		
    		this.setRowSelected(row);
    		
    		if (this.rows.length > this.limit) {
    			this.deleteRow(0);
    		}
        },
        
        insertRow: function(record, index) {
        	this.callRowFunction(this.onAddRow, this.doInsertRow, record, index);
        },
        
        doInsertRow: function(record, index) {
        	var index = this.rows.length;
        	var row = this.loadLine(data, index);
        	var baseRow = this.rows[index];
        	
        	baseRow.element.prepend(row.element);
        	this.rows.splice(index, 0, row);
        	this.dataObject.splice(index, 0, row.data);
    		
    		this.setRowSelected(row);
    		
    		if (this.rows.length > this.limit) {
    			this.deleteLastRow();
    		}    		
        },
        
        deleteRow: function(index) {
        	this.callRowFunction(this.onDeleteRow, this.doDeleteRow, this.rows[index].data.record, index);
        },
        
        doDeleteRow: function(me, index) {
        	var row = this.rows[index];
        	
        	this.rows.splice(index, 1);
        	this.dataObject.splice(index, 1);
        	row.element.remove();
        },
        
        deleteLastRow: function() {
        	this.callRowFunction(this.onDeleteRow, this.doDeleteLastRow, null, 0);
        },
        
        doDeleteLastRow: function() {
        	var row = this.rows.pop(); 
        	this.dataObject.pop(); 
        	row.element.remove();
        },
        
        empty: function() {
    		if (this.customFooter) {
    			this.customFooter.hide();
    			this.customFooter.remove();
    			$("body").append(this.customFooter);
    		}
    		
    		this.data = null;
    		this.dataObject = [];
    		this.rows = [];
    		this.canvas.empty();
    	},
    	
    	getData: function() {
    		return this.dataObject;
    	}
	});
	
    
    $.fm.Grid.Cell = Control.subClass({
		template: [
		    '<td class="grid-cell">',
		    	'<div class="grid-cell-inner" />',
		    '</td>'
		],    	
		
    	init: function(options) {
			Control.call(this, options); 
			this.width = this.column.width;
			
			this.createElement();
    	},
    	
    	createElement: function() {
    		var me = this, column = this.column; 
    		
    		var element = $(me.template.join("")); 
    		me.element = element;
    		
			element.body = $('div', element);
			element.body.width(column.width);
			element.body.attr("align", column.align || "center");
			
			this.renderValue();
			
			if (me.grid.editable || me.column.editable) {
				if (me.grid.quickEdit) {
					element.click(function() {
						if (me.column.editableCondition) {
							var isEditable = me.column.editableCondition(me);
							if (isEditable) {
								me.grid.editCell(me);
							}
						}
						else {
							me.grid.editCell(me);
						}
						
					});				
				}
				else {
					element.dblclick(function() {
						me.grid.editCell(me);
					});					
				}				
			}
			/////////////
			//单元格加行点击事件
			else {
				if (!me.gridme.doubleClickSelect) {
					element.click(function() {
	        			if(me.gridme.singleSelect)
	        				me.gridme.setRowSelected.call(me.gridme, me.row, me);
	        		});
	    		}
	    		else {
	    			element.dblclick(function() {
	        			if(me.gridme.singleSelect)
	        				me.gridme.setRowSelected.call(me.gridme, me.row, me);
	        		});
	    		}
				
			}
			/////////
    	},
    	
    	renderValue: function() {
    		var element = this.element; var column = this.column;
    		
			if (column.render) {
				column.render(element.body, this.value, this.row, this.grid);
			}
			else {
				if(column.field.substring(0, 8) == 'constant'){
					element.body.html(column.field.substring(8));
				}
				else{
					element.body.html(this.value);
				}
			}
			
			if (this.column.istitle && this.value && this.value!="") {
				this.element.body.attr("title", this.value);
    		}
    	},
    	
    	refresh: function() {
    		var fieldName = this.column.field;
    		var dataObject = this.row.data;
    		
    		this.value = dataObject.getValue(fieldName);
    		this.renderValue();
    	},
    	
    	setValue: function(value, text) {
    		var fieldName = this.column.field;
    		var dataObject = this.row.data;
    		
    		this.value = value;
    		this.text = text;
    		dataObject.setValue(fieldName, value);
    		
    		this.element.body.html(this.text || this.value);
    		if (this.column.istitle && this.value && this.value!="") {
    			this.element.body.attr("title", this.value);
    		}
    		
    	},
    	
    	setControl: function(control) {
    		this.addChild(control);
    	}
    });
		
})(jQuery);