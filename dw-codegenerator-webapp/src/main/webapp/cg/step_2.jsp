<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@include file="commons/init_import.jsp" %>
<%@include file="commons/init_define.jsp" %>
<%@include file="commons/web_top.jsp"%>
<%
int status=0;
String msg="";
//获取参数
String dbDriver=getRequestString(request, "dbDriver", "");
String dbUrl=getRequestString(request,"dbUrl","");
String dbUser=getRequestString(request,"dbUser","");
String dbPassword=getRequestString(request,"dbPassword","");
//检查参数
if(StringUtils.isBlank(dbDriver)
		|| StringUtils.isBlank(dbUrl)
		|| StringUtils.isBlank(dbUser)
		|| StringUtils.isBlank(dbPassword)){
	status=-1;
	msg="数据库参数设置不完整";
}
//测试驱动
if(status==0){
	try{
		Class.forName(dbDriver);
	}
	catch(Exception e){
		status=-2;
		msg="数据库驱动加载失败!<br/>"+e.getMessage();
	}
}
//记录数据库表信息
List<Table> tables=null;
//测试数据库链接
if(status==0){
	Connection connection=null;
	try{
		connection=DriverManager.getConnection(dbUrl,dbUser,dbPassword);
		if(connection==null)
			throw new Exception();
	}
	catch(Exception e){
		status=-3;
		msg="获取数据库链接失败!<br/>"+e.getMessage();
	}
	//获取数据库表信息
	if(status==0){
		try{
			DBInfo dbInfo=new DBInfo(connection);
			Set<Table> _tables=dbInfo.getAllTableInfos(new TablenameFilter(){
				
				public boolean accept(String tableName){
					Pattern p = Pattern.compile("[\\w]+");
					return p.matcher(tableName).matches();
				}
				
			});
			tables=new ArrayList<Table>(_tables);
			Collections.sort(tables, new Comparator<Table>(){
				 public int compare(Table table1, Table table2) {
					  return table1.getName().compareTo(table2.getName());
				 }
			});
		}
		finally{
			if(connection!=null && !connection.isClosed())
				connection.close();
		}
	}
}
if(status!=0){
	forwardError(request, response, status, msg);
	return;
}
%>
<div class="body">
	<form action="step_3.jsp" method="post">
		<%@include file="commons/web_form_request_hidden.jsp" %>
		<div class="title">设置生成表信息</div>
		<ul>
			<li>选择要生成的表：</li>
			<li>
			<%
			for(Table table:tables){
			%>
				<input type="checkbox" name="tableName" value="<%=table.getName()%>"/> <%=table.getName()%> <br/>
			<%
			}
			%>
			</li>
		</ul>
		<ul>
			<li>设置表匹配正则表达式：</li>
			<li><input type="text" name="tableNameRegular"/></li>
		</ul>
		<div class="controller">
			<input type="button" value="上一步" onclick="history.go(-1)" />  
			<input type="submit" value="下一步" />  
		</div>
	</form>
</div>
<%@include file="commons/web_bottom.jsp"%>