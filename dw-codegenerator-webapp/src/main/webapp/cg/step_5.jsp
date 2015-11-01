<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@include file="commons/init_import.jsp"%>
<%@include file="commons/init_define.jsp"%>
<%@include file="commons/web_top.jsp"%>
<%
	int status = 0;
	String msg = "";

	//获取参数
	String dbDriver = getRequestString(request, "dbDriver", "");
	String dbUrl = getRequestString(request, "dbUrl", "");
	String dbUser = getRequestString(request, "dbUser", "");
	String dbPassword = getRequestString(request, "dbPassword", "");
	String[] tableNames = request.getParameterValues("tableName");
	final String tableNameRegular = getRequestString(request,
			"tableNameRegular", "");
	String envVars = getRequestString(request, "envVars", "");
	String templatePath = getRequestString(request, "templatePath", "");
	String templateEncoding = getRequestString(request,
			"templateEncoding", "");

	//调整参数
	Map<String, Object> envVarMap = new HashMap<String, Object>();
	if (StringUtils.isNotBlank(envVars)) {
		String[] envVarsArray = envVars.replaceAll("\n\r", "<br/>")
				.replaceAll("\r\n", "<br/>").replaceAll("\n", "<br/>")
				.replaceAll("\r", "<br/>").split("<br/>");
		if (envVarsArray != null) {
			for (String envVar : envVarsArray) {
				if (StringUtils.isNotBlank(envVar)) {
					String[] envVarKV = envVar.split("=");
					if (envVarKV != null && envVarKV.length > 1
							&& envVarKV[0] != null
							&& envVarKV[0].length() > 0)
						envVarMap.put(envVarKV[0], envVarKV[1]);
				}
			}
		}
	}

	Set<String> tableNameSet = new HashSet<String>();
	if (tableNames != null) {
		for (String tableName : tableNames) {
			if (tableName != null && tableName.length() > 0)
				tableNameSet.add(tableName);
		}
	}
	//检查参数
	if (templatePath.length() <= 0) {
		status = -1;
		msg = "请选择合适的生成模板";
	}

	if (status != 0) {
		forwardError(request, response, status, msg);
		return;
	}
%>
<div class="body">

	<div
		style="width: 800px; margin: auto; border: solid 1px; text-align: left; padding: 10px">
		<div class="title">开始进行代码生成</div>
		<%
			String outputWebPath = generateWebPath + "/"
					+ System.currentTimeMillis();
			String outputFilePath = application.getRealPath(outputWebPath);
			Generator generator = new Generator();
			// 设置数据库信息
			generator.setDbDriver(dbDriver);
			generator.setDbUrl(dbUrl);
			generator.setDbUser(dbUser);
			generator.setDbPassword(dbPassword);
			// 设置模板位置
			generator.setTemplateFilePath(templatePath);
			// 设置生成输出位置
			generator.setOutputFilePath(outputFilePath);
			// 设置编码
			if (templateEncoding.length() > 0)
				generator.setEncoding(templateEncoding);
			// 设置模板过滤器
			generator.setTemplateFileFilter(new FileFilter() {
				public boolean accept(File file) {
					// 凡是文件名带有inc_开头的，都只作为嵌套用模板，不生成
					if (file.isDirectory())
						return !file.getName().toLowerCase().equals("ftl_inc");
					else
						return !file.getName().toLowerCase().startsWith("inc_");
				}
			});
			if (tableNameRegular.length() > 0) {
				generator.setGeneratorTablenameFilter(new TablenameFilter() {
					public boolean accept(String tableName) {
						Pattern p = Pattern.compile(tableNameRegular);
						return p.matcher(tableName).find();
					}
				});
			}
			// 设置生成表
			if (tableNameSet.size() > 0)
				generator.setGenerateTables(tableNameSet);
			// 设置环境变量信息系统
			if (envVarMap.size() > 0)
				generator.setEnvVar(envVarMap);
			//设置自定义模板标签
			//generator.addTemplateDirectiveModel(name, templateDirectiveModel);
			//设置自定义模板函数
			// 注册方法
			generator.addTemplateMethodModel("tableNameToClassName",
					new TableNameToClassName());
			generator.addTemplateMethodModel("tableNameToObjectName",
					new TableNameToObjectName());
			generator.addTemplateMethodModel("tableNameToModuleName",
					new TableNameToModuleName());
			generator.addTemplateMethodModel("columnNameToFieldName",
					new ColumnNameToFieldName());
			generator.addTemplateMethodModel("columnTypeToFieldType",
					new ColumnTypeToFieldType());
			generator.addTemplateMethodModel(
					"foreignColumnNameToFieldObjectName",
					new ForeignColumnNameToFieldObjectName());

			//设置日志器
			final JspWriter logWriter = out;
			generator.setLog(new Log() {

				public void info(Object msg) {
					try {
						logWriter.println("<p class=\"log\">" + msg + "</p>");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			// 执行代码生成
			generator.generate();
			//把生成后的文件打包
			String compressFilePath = outputFilePath + ".zip";
			String compressWebPath = outputWebPath + ".zip";
			CompressUtils.compressToZip(outputFilePath, compressFilePath);
			//删除文件夹
			FileUtils.deleteDirectory(new File(outputFilePath));
		%>
		<div class="controller">
			<input type="button" value="上一步" onclick="history.go(-1)"> <input
				type="button" value="点击下载"
				onclick="location.href='<%=compressWebPath%>'" /> <input
				type="button" value="返回首页" onclick="location.href='index.jsp'" />
		</div>
	</div>
</div>
<%@include file="commons/web_bottom.jsp"%>