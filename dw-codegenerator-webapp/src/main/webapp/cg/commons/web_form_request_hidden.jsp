<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
{
	Enumeration e=request.getParameterNames();
	while(e.hasMoreElements()){
		String name=(String)e.nextElement();
		String[] values=request.getParameterValues(name);
		if(values==null)
			continue;
		for(String value:values){
			if(value==null)
				continue;
%>
	<input type="hidden" name="<%=name%>" value="<%=value%>"/>
<%
		}
	}
}
%>