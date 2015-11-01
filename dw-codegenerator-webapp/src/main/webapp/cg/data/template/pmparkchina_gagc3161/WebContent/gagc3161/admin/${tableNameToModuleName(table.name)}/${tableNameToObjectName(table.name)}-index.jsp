<#include "/ftl_inc/inc_assign.ftl"/>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@include file="../commons/init_taglib.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@include file="../commons/web_head_meta.jsp"%>
<title><spring:message code="${moduleName}.${objectName}" /></title>
<%@include file="../commons/web_head_link.jsp"%>
<%@include file="../commons/web_head_script.jsp"%>
<script type="text/javascript">
	//DataGird控件
	var dataGrid;

	//新建操作
	var editNew = function() {
		new ModalWindow({
			id : "mw_create",
			title : "<spring:message code="${moduleName}.editNew"/> ",
			templateUrl : "${objectName}/new",
			width : 500,
			height : 300,
			isLock : true,
			buttons : [ "<spring:message code="${moduleName}.confirm"/>", "<spring:message code="${moduleName}.cancel"/>" ]
		}).addEventHandler("<spring:message code="${moduleName}.confirm"/>", function() {
			$("#" + this.id + " form").submit();
		}).addEventHandler("<spring:message code="${moduleName}.cancel"/>", function() {
			this.close();
		}).draw();
	};
	
	<#if table.primaryKey??>
		<#assign pkName=""/>
		<#assign pkColumns=table.primaryKey.columns/>
		<#--复合主键-->
		<#if (pkColumns?size>1)>
			<#assign pkName=objectName+"Pk"/>
		<#--普通主键-->
		<#else>
			<#assign pkName=columnNameToFieldName(pkColumns[0].name)/>
		</#if>
	//编辑
	var edit = function(data) {
		if (!data) {
			Utils.openBanWindow("<spring:message code="${moduleName}.message.edit.error"/>");
			return;
		}
		win = new ModalWindow({
			id : "mw_edit_" + data.${pkName},
			title : "<spring:message code='${moduleName}.edit' />",
			templateUrl : "${objectName}/" + data.${pkName} + "/edit",
			width : 500,
			height : 300,
			//是否锁定屏幕
			isLock : true,
			//添加自定义按钮，按钮名为事件名
			buttons : [ "<spring:message code="${moduleName}.confirm"/>", "<spring:message code="${moduleName}.cancel"/>" ]
		}).addEventHandler("<spring:message code="${moduleName}.confirm"/>", function() {
			$("#" + this.id + " form").submit();
		}).addEventHandler("<spring:message code="${moduleName}.cancel"/>", function() {
			this.close();
		}).draw();
	};

	//删除
	var remove = function(datas) {
		if (!datas || datas.length == 0) {
			Utils.openBanWindow("<spring:message code="${moduleName}.message.delete.error"/>");
			return;
		}
		//创建确认窗口
		Utils.openConfirmWindow("<spring:message code="${moduleName}.message.confirm" />", function() {
			Utils.lockLoading();
			var url = "${objectName}/";
			var params = "";
			for ( var i = 0; i < datas.length; i++)
				params += "${pkName}=" + datas[i].${pkName} + "&";
			params += "_method=delete";
			$.post(url, params, function(data) {
				Utils.unlockComplate();
				if (data.status) {
					//创建信息窗口
					Utils.openMessageWindow(data.message);
					//创建信息窗口
					dataGrid.refreshData();
				} else
					Utils.openBanWindow(data.message);
			}, "json");
		});
	};
	</#if>

	//初始化列表控件
	var initGrid = function() {
		//创建DataGrid实例
		dataGrid = Utils.buildDefaultDataGrid("#dataGrid", "${objectName}/list");
		//添加控制列
		dataGrid.addControllerRenderer(function(tr, td, index, data) {
			$("<input type='button' class='ui_input_button'/>").val("<spring:message code="${moduleName}.edit"/>").click(data,
					function(e) {
						edit(e.data);
					}).appendTo(td);
			$("<input type='button' class='ui_input_button'/>").val("<spring:message code="${moduleName}.delete"/>").click(data,
					function(e) {
						remove([ e.data ]);
					}).appendTo(td);
		});
		dataGrid.addMouseMenu({
			name : "<spring:message code="${moduleName}.editNew"/>",
			handler : function() {
				editNew();
			},
			line : 0
		}).addMouseMenu({
			name : "<spring:message code="${moduleName}.deleteSelect"/>",
			handler : function() {
				var datas = [];
				var selectDatas = dataGrid.getSelectDatas();
				for ( var i = 0; i < selectDatas.length; i++)
					datas.push(selectDatas[i].data);
				remove(datas);
			},
			line : 0
		});
		//生成表格
		dataGrid.draw();
	};

	//创建菜单
	var initMenu = function() {
		new ShortcutMenu("#shortcutMenu").addMenu("<spring:message code="${moduleName}.selectAll"/>", function() {
			dataGrid.selectAll();
		}).addMenu("<spring:message code="${moduleName}.unselectAll"/>", function() {
			dataGrid.unselectAll();
		}).addMenu("<spring:message code="${moduleName}.selectInverse"/>", function() {
			dataGrid.selectInverse();
		}).addMenu("<spring:message code="${moduleName}.refresh"/>", function() {
			dataGrid.refreshData();
		}).addMenu("<spring:message code="${moduleName}.editNew"/>", function() {
			editNew();
		}).addMenu("<spring:message code="${moduleName}.deleteSelect"/>", function() {
			var datas = [];
			var selectDatas = dataGrid.getSelectDatas();
			for ( var i = 0; i < selectDatas.length; i++)
				datas.push(selectDatas[i].data);
			remove(datas);
		}).draw();
	};

	$(function() {
		initMenu();
		initGrid();
	});
</script>
</head>
<body>
	<div id="container">
		<div id="shortcutMenu"></div>
		<div id="dataGrid"></div>
	</div>
</body>
</html>