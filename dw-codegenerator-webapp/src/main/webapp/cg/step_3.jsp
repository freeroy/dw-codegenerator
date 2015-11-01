<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@include file="commons/init_import.jsp" %>
<%@include file="commons/init_define.jsp" %>
<%@include file="commons/web_top.jsp"%>
<%
int status=0;
String msg="";

String[] tableNames=request.getParameterValues("tableName");
String tableNameRegular=getRequestString(request, "tableNameRegular", "");
//检查参数
if((tableNames==null || tableNames.length<=0) && tableNameRegular.length()<=0){
	status=-1;
	msg="没有包含任何待生成表信息";
}
if(status!=0){
	forwardError(request, response, status, msg);
	return;
}
%>
<div class="body">
	<form action="step_4.jsp" method="post">
		<%@include file="commons/web_form_request_hidden.jsp" %>
		<div class="title">设置生成环境变量</div>
		<ul>
			<li>设置生成变量：("名=值"方式，多个用回车分隔)</li>
			<li>
			<textarea name="envVars" style="width:400px; height:200px"></textarea>
			</li>
		</ul>
		<div class="controller">
			<input type="button" value="上一步" onclick="history.go(-1)" />  
			<input type="submit" value="下一步" />  
		</div>
	</form>
</div>
<%@include file="commons/web_bottom.jsp"%>