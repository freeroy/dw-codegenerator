<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@include file="commons/init_import.jsp" %>
<%@include file="commons/init_define.jsp" %>
<%@include file="commons/web_top.jsp"%>
<script type="text/javascript">
$(function(){
	$("#dbDriverSelect").change(function(e){
		if(this.value=="")
			$("#dbDriverInput").removeAttr("readonly").val("");
		else
			$("#dbDriverInput").val(this.value).attr("readonly","readonly");
		var dbUrl="";
		switch(this.value){
			case "com.microsoft.sqlserver.jdbc.SQLServerDriver":
			dbUrl="jdbc:sqlserver://localhost:1433;DatabaseName=test";
			break;
			case "com.mysql.jdbc.Driver":
				dbUrl="jdbc:mysql://localhost:3306/test?useUnicode=true&amp;characterEncoding=UTF-8";
				break;
			case "com.ibm.db2.jcc.DB2Driver":
				dbUrl="jdbc:db2://localhost:50000/test";
				break;
			case "oracle.jdbc.driver.OracleDriver":
				dbUrl="jdbc:oracle:thin:@localhost:1521:test";
				break;
		}
		$("#dbUrlInput").val(dbUrl);
	});
});
</script>
<div class="body">
	<form action="step_2.jsp" method="post">
		<div class="title">设置数据库信息</div>
		<ul>
			<li>数据库驱动：</li>
			<li>
			<select id="dbDriverSelect">
				<option value="">自定义</option>
				<option value="com.microsoft.sqlserver.jdbc.SQLServerDriver">SQL Server 2000+</option>
				<option value="com.mysql.jdbc.Driver">MySQL 5+</option>
				<option value="com.ibm.db2.jcc.DB2Driver">DB2 9+</option>
				<option value="oracle.jdbc.driver.OracleDriver">Oracle 10g+</option>
			</select>
			<input id="dbDriverInput" type="text" name="dbDriver" style="width:300px"/></li>
		</ul>
		<ul>
			<li>数据库链接：</li>
			<li><input id="dbUrlInput" type="text" name="dbUrl" style="width:500px"/></li>
		</ul>
		<ul>
			<li>数据库账号：</li>
			<li><input type="text" name="dbUser" value="test" /></li>
		</ul>
		<ul>
			<li>数据库密码：</li>
			<li><input type="text" name="dbPassword" value="test" /></li>
		</ul>
		<div class="controller">
			<input type="button" value="返回首页" onclick="location.href='index.jsp'"/>
			<input type="submit" value="下一步" />  
		</div>
	</form>
</div>
<%@include file="commons/web_bottom.jsp"%>