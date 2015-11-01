<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%!

	//模板存放位置
	private String templateWebPath="/cg/data/template";
	//代码生成位置
	private String generateWebPath="/cg/data/generate";
	
	
	
	String getRequestString(HttpServletRequest request,String name,String defaultValue){
		String rst=request.getParameter(name);
		rst=rst==null?defaultValue:rst;
		return rst;
	}
	
	void forwardError(HttpServletRequest request,HttpServletResponse response,int status,String msg) throws Exception{
		request.setAttribute("status",status);
		request.setAttribute("msg",msg);
		request.getRequestDispatcher("error.jsp").forward(request, response);
	}

%>