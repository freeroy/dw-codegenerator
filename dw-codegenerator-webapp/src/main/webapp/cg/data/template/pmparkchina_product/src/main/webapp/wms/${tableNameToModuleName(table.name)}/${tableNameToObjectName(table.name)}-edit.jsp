<#include "/ftl_inc/inc_assign.ftl"/>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@include file="../commons/init_taglib.jsp"%>
<script type="text/javascript">
	$(function() {
		var win = ModalWindow.getLastModalWindow();
		var dataGrid = window.dataGrid;

		//初始化表单
		var initForm = function() {

			var form = $("#${objectName}Form");
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
			if (Utils.isEditForm(form)) {
				form.attr("action", "${objectName}/" + form.find("input[name='${pkName}']").val());
				form.find("input[name='_method']").val("put");
			} else {
				form.attr("action", "${objectName}");
				form.find("input[name='_method']").val("post");
			}
			</#if>

			var formValidate = new FormValidate();
			<#--遍历关联字段 多对一、一对一-->
			<#list table.foreignKeys as foreignKey>
				<#--判断是一对一、还是多对一，-->
				<#assign isPrimaryKeyAndForeignKeySame=true/>
				<#--无主键，就肯定与主键不一致了-->
				<#if table.primaryKey??>
					<#list table.primaryKey.columns as pkColumn>
						<#assign hasSame=false/>
						<#list foreignKey.columns as fkColumn>
							<#if pkColumn.name==fkColumn.name>
								<#assign hasSame=true/>
								<#break/>
							</#if>
						</#list>
						<#if !hasSame>
							<#assign isPrimaryKeyAndForeignKeySame=false/>
							<#break/>
						</#if>
					</#list>
				<#else>
					<#assign isPrimaryKeyAndForeignKeySame=false/>
				</#if>
			
				<#if !isPrimaryKeyAndForeignKeySame>
					<#list foreignKey.columns as foreignColumn>
						<#assign foreignObjectName=foreignColumnNameToFieldObjectName(foreignColumn.name,foreignKey.primaryColumns[foreignColumn_index].name)/>
						<#assign foreignColumnName=columnNameToFieldName(foreignKey.primaryColumns[foreignColumn_index].name)/>
						<#if !foreignColumn.nullable>
							formValidate
							.addValidate("#${objectName}Form input[name='${foreignObjectName}.${foreignColumnName}']")
							.isNotBlank()
							.tips("<spring:message code="${moduleName}.format.error.blank"/>");
						</#if>
					</#list>
				</#if>
			</#list>
			<#--遍历普通字段-->
			<#list table.columns as column>
				<#--跳过主键-->
				<#assign isPkColumn=false/>
				<#if table.primaryKey??>
					<#list table.primaryKey.columns as pkColumn>
						<#if pkColumn.name==column.name>
							<#assign isPkColumn=true/>
							<#break/>
						</#if>
					</#list>
				</#if>
				<#--跳过外键-->
				<#assign isFkColumn=false/>
				<#list table.foreignKeys as foreignKey>
					<#list foreignKey.columns as fkColumn>
						<#--若当前字段属于外键之一，就设置变量，并跳出循环-->
						<#if column.name==fkColumn.name>
							<#assign isFkColumn=true/>
							<#break/>
						</#if>
					</#list>
					<#if isFkColumn>
						<#break/>
					</#if>
				</#list>
				<#if !isPkColumn && !isFkColumn>
					<#assign fieldName=columnNameToFieldName(column.name)/>
					<#if !column.nullable>
						formValidate
						.addValidate("#${objectName}Form input[name='${fieldName}']")
						.isNotBlank()
						.tips("<spring:message code="${moduleName}.format.error.blank"/>");
					</#if>
				</#if>
			</#list>
			
			form.submit(function() {
				if (formValidate.validate(true)) {
					var url = this.action;
					var params = $(this).serialize();
					Utils.lockLoading("#" + win.id);
					$.post(url, params, function(data) {
						Utils.unlockComplate("#" + win.id);
						if (data.status) {
							//关闭窗体
							win.close();
							//创建信息窗口
							Utils.openMessageWindow(data.message);
							//刷新数据
							dataGrid.refreshData();
						} else {
							//创建信息窗口
							Utils.openBanWindow(data.message);
						}
					}, "json");
				}
				return false;
			});
		};

		initForm();
		//渲染表单
	});
</script>
<form:form id="${objectName}Form" method="post" cssClass="ui_form" commandName="${objectName}">
	<input type="hidden" name="_method" />
	<#if table.primaryKey??>
		<#assign pkColumns=table.primaryKey.columns/>
		<#--复合主键-->
		<#if (pkColumns?size>1)>
			<#assign pkObjectName=objectName+"Pk"/>
			<#list table.primaryKey.columns as column>
				<form:hidden path="${pkObjectName+"."+columnNameToFieldName(column.name)}" /> 		
			</#list>
		<#--普通主键-->
		<#else>
			<form:hidden path="${columnNameToFieldName(pkColumns[0].name)}" />
		</#if>
	</#if>
	<#--遍历关联字段 多对一、一对一-->
	<#list table.foreignKeys as foreignKey>
		<#--判断是一对一、还是多对一，-->
		<#assign isPrimaryKeyAndForeignKeySame=true/>
		<#--无主键，就肯定与主键不一致了-->
		<#if table.primaryKey??>
			<#list table.primaryKey.columns as pkColumn>
				<#assign hasSame=false/>
				<#list foreignKey.columns as fkColumn>
					<#if pkColumn.name==fkColumn.name>
						<#assign hasSame=true/>
						<#break/>
					</#if>
				</#list>
				<#if !hasSame>
					<#assign isPrimaryKeyAndForeignKeySame=false/>
					<#break/>
				</#if>
			</#list>
		<#else>
			<#assign isPrimaryKeyAndForeignKeySame=false/>
		</#if>
	
		<#if !isPrimaryKeyAndForeignKeySame>
			<#list foreignKey.columns as foreignColumn>
				<#assign foreignObjectName=foreignColumnNameToFieldObjectName(foreignColumn.name,foreignKey.primaryColumns[foreignColumn_index].name)/>
				<#assign foreignColumnName=columnNameToFieldName(foreignKey.primaryColumns[foreignColumn_index].name)/>
				<ul class="ui_row">
					<li class="li_title"><spring:message code="${moduleName}.${objectName}.${foreignObjectName}.${foreignColumnName}" />:</li>
					<li><form:input cssClass="ui_input_text" path="${foreignObjectName}.${foreignColumnName}" /></li>
					<li>
						<#if !foreignColumn.nullable>
						<span class="important">*</span>
						</#if>
					</li>
				</ul>
			</#list>
		</#if>
	</#list>
	<#--遍历普通字段-->
	<#list table.columns as column>
		<#--跳过主键-->
		<#assign isPkColumn=false/>
		<#if table.primaryKey??>
			<#list table.primaryKey.columns as pkColumn>
				<#if pkColumn.name==column.name>
					<#assign isPkColumn=true/>
					<#break/>
				</#if>
			</#list>
		</#if>
		<#--跳过外键-->
		<#assign isFkColumn=false/>
		<#list table.foreignKeys as foreignKey>
			<#list foreignKey.columns as fkColumn>
				<#--若当前字段属于外键之一，就设置变量，并跳出循环-->
				<#if column.name==fkColumn.name>
					<#assign isFkColumn=true/>
					<#break/>
				</#if>
			</#list>
			<#if isFkColumn>
				<#break/>
			</#if>
		</#list>
		<#if !isPkColumn && !isFkColumn>
			<#assign fieldName=columnNameToFieldName(column.name)/>
	<ul class="ui_row">
		<li class="li_title"><spring:message code="${moduleName}.${objectName}.${fieldName}" />:</li>
		<li><form:input cssClass="ui_input_text" path="${fieldName}" /></li>
		<li>
			<#if !column.nullable>
			<span class="important">*</span>
			</#if>
		</li>
	</ul>
		</#if>
	</#list>
</form:form>