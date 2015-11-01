<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@include file="commons/init_import.jsp" %>
<%@include file="commons/init_define.jsp" %>
<%@include file="commons/web_top.jsp"%>
<%
int status=0;
String msg="";
//获取模板信息
Map<String,String> templates=new LinkedHashMap<String,String>();
//获取系统提供的模板
if(status==0){
	String templateFilePath=application.getRealPath(templateWebPath);
	File templateRootDirectory=new File(templateFilePath);
	if(templateRootDirectory!=null && templateRootDirectory.exists() && templateRootDirectory.isDirectory()){
		File[] templateDirectorys=templateRootDirectory.listFiles();
		if(templateDirectorys!=null){
			for(File templateDirectory:templateDirectorys){
				if(!templateDirectory.isDirectory())
					continue;
				templates.put(templateDirectory.getName(), templateDirectory.getPath());
			}
		}
	}
}
if(status!=0){
	forwardError(request, response, status, msg);
	return;
}
%>
<div class="body">
	<form action="step_5.jsp" method="post">
		<%@include file="commons/web_form_request_hidden.jsp" %>
		<div class="title">设置生成模板信息</div>
		<ul>
			<li>生成模板：</li>
			<li>
			<%
			Iterator<Entry<String, String>> iterator=templates.entrySet().iterator();
			while(iterator.hasNext()){
				Entry<String, String> entry=iterator.next();
			%>
			<input type="radio" name="templatePath" value="<%= entry.getValue()%>"> <%=entry.getKey() %><br/>
			<%
			}
			%>
			</li>
		</ul>
		<ul>
			<li>模板统一编码：</li>
			<li><input type="text" name="templateEncoding" value="UTF-8"></li>
		</ul>
		<ul>
			<li>文件名为“inc_”开头的文件/文件名为“ftl_inc”的文件夹，视为嵌套文件/嵌套文件夹，不被生成!</li>
		</ul>
		<ul>
			<li>若希望使用自定义模板，请把模板文件放置到“<%= templateWebPath%>”目录中</li>
		</ul>
		<div class="controller">
			<input type="button" value="上一步" onclick="history.go(-1)" />  
			<input type="submit" value="下一步" />  
		</div>
	</form>
</div>
<%@include file="commons/web_bottom.jsp"%>