<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@include file="commons/init_import.jsp" %>
<%@include file="commons/init_define.jsp" %>
<%@include file="commons/web_top.jsp"%>
<div class="body">
	<div class="msg">
	    <div class="title">出错了</div>
		status:<%=request.getAttribute("status") %><br/>
		msg:<%=request.getAttribute("msg")%><br/>
		<div class="controller"><input type="button" value="返回" onclick="history.go(-1)"/></div>
	</div>
</div>
<%@include file="commons/web_bottom.jsp"%>