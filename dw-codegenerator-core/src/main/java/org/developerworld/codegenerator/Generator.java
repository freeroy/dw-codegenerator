package org.developerworld.codegenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.developerworld.codegenerator.freemarker.method.ColumnNameToFieldName;
import org.developerworld.codegenerator.freemarker.method.ColumnTypeToFieldType;
import org.developerworld.codegenerator.freemarker.method.ForeignColumnNameToFieldObjectName;
import org.developerworld.codegenerator.freemarker.method.TableNameToClassName;
import org.developerworld.codegenerator.freemarker.method.TableNameToModuleName;
import org.developerworld.codegenerator.freemarker.method.TableNameToObjectName;
import org.developerworld.commons.dbutils.info.DBInfo;
import org.developerworld.commons.dbutils.info.TablenameFilter;
import org.developerworld.commons.dbutils.info.object.Table;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateMethodModelEx;

/**
 * 生成器
 * 
 * @author Roy Huang
 * @version 20130422
 * 
 */
public class Generator {

	private final static String ENV_VAR_TABLE = "table";

	private final static Map<String, TemplateMethodModelEx> METHOD_ENV_VARS = new HashMap<String, TemplateMethodModelEx>();
	static {
		// 注册方法
		METHOD_ENV_VARS.put("tableNameToClassName", new TableNameToClassName());
		METHOD_ENV_VARS.put("tableNameToObjectName",
				new TableNameToObjectName());
		METHOD_ENV_VARS.put("tableNameToModuleName",
				new TableNameToModuleName());
		METHOD_ENV_VARS.put("columnNameToFieldName",
				new ColumnNameToFieldName());
		METHOD_ENV_VARS.put("columnTypeToFieldType",
				new ColumnTypeToFieldType());
		METHOD_ENV_VARS.put("foreignColumnNameToFieldObjectName",
				new ForeignColumnNameToFieldObjectName());
	}

	private Log log;
	private String dbCataLog;
	private String dbSchema;
	private String dbDriver;
	private String dbUrl;
	private String encoding;
	private String dbUser;
	private String dbPassword;
	private String templateFilePath;
	private String outputFilePath;
	private Set<String> generateTables = new HashSet<String>();
	private Configuration templateConfiguration;
	private Configuration templateFileConfiguration;
	private File templateFileDirectory;
	private FileFilter templateFileFilter;
	private Map<String, Object> envVars = new HashMap<String, Object>();
	private Map<String, TemplateMethodModelEx> templateMethodModels = new HashMap<String, TemplateMethodModelEx>(
			METHOD_ENV_VARS);
	private Map<String, TemplateDirectiveModel> templateDirectiveModels = new HashMap<String, TemplateDirectiveModel>();
	private TablenameFilter tablenameFilter;

	public void setLog(Log log) {
		this.log = log;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public void setDbCataLog(String dbCataLog) {
		this.dbCataLog = dbCataLog;
	}

	public void setDbSchema(String dbSchema) {
		this.dbSchema = dbSchema;
	}

	public void setDbDriver(String dbDriver) {
		this.dbDriver = dbDriver;
	}

	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}

	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}

	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}

	public void setTemplateFileFilter(FileFilter templateFileFilter) {
		this.templateFileFilter = templateFileFilter;
	}

	public void setTemplateFilePath(String templateFilePath) {
		this.templateFilePath = templateFilePath;
	}

	public void setOutputFilePath(String outputFilePath) {
		this.outputFilePath = outputFilePath;
	}

	public void setGenerateTable(String table) {
		if (generateTables == null)
			generateTables = new HashSet<String>();
		else
			generateTables.clear();
		generateTables.add(table);
	}

	public void setGenerateTables(Set<String> tables) {
		generateTables = tables;
	}

	public void setEnvVar(Map<String, Object> envVar) {
		this.envVars = envVar;
	}

	public void setTemplateMethodModel(
			Map<String, TemplateMethodModelEx> templateMethodModel) {
		this.templateMethodModels = templateMethodModel;
	}

	public void setTemplateDirectiveModels(
			Map<String, TemplateDirectiveModel> templateDirectiveModels) {
		this.templateDirectiveModels = templateDirectiveModels;
	}

	public Generator addGenerateTable(String table) {
		if (generateTables == null)
			generateTables = new HashSet<String>();
		generateTables.add(table);
		return this;
	}

	public Generator addTemplateMethodModel(String name,
			TemplateMethodModelEx templateMethodModel) {
		if (templateMethodModels == null)
			templateMethodModels = new HashMap<String, TemplateMethodModelEx>();
		templateMethodModels.put(name, templateMethodModel);
		return this;
	}

	public Generator addTemplateDirectiveModel(String name,
			TemplateDirectiveModel templateDirectiveModel) {
		if (templateDirectiveModels == null)
			templateDirectiveModels = new HashMap<String, TemplateDirectiveModel>();
		templateDirectiveModels.put(name, templateDirectiveModel);
		return this;
	}

	public Generator addEnvVar(String name, Object envVar) {
		if (envVars == null)
			envVars = new HashMap<String, Object>();
		envVars.put(name, envVar);
		return this;
	}

	public void setGeneratorTablenameFilter(TablenameFilter tablenameFilter) {
		this.tablenameFilter = tablenameFilter;
	}

	/**
	 * 执行代码生成
	 * 
	 * @throws Exception
	 */
	public void generate() throws Exception {
		Date beginTime = new Date();
		// 定义模板目录对象
		logInfo("开始执行生成");
		logInfo("模板目录为：" + templateFilePath);
		templateFileDirectory = new File(templateFilePath);
		if (!templateFileDirectory.exists())
			throw new Exception("templateFilePath is not found!");
		else if (!templateFileDirectory.isDirectory())
			throw new Exception("templateFilePath must is a directory!");
		logInfo("输出目录为：" + outputFilePath);
		File outputDirectory = new File(outputFilePath);
		if (!outputDirectory.exists())
			outputDirectory.mkdirs();
		logInfo("加载模板及运行配置");
		// 构建templateConfiguration
		buildTemplateConfiguration();
		Set<Table> tableInfos = getTableInfos();
		logInfo("共需生成数据库表：" + tableInfos.size());
		// 遍历要生成的表，开始进行生成
		for (Table table : tableInfos)
			generate(table);
		logInfo("完成生成,用时:" + (System.currentTimeMillis() - beginTime.getTime())
				/ 1000 + "秒");
	}

	/**
	 * 获取数据库表信息
	 * 
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws Exception
	 */
	private Set<Table> getTableInfos() throws ClassNotFoundException,
			SQLException, Exception {
		Set<Table> rst = null;
		logInfo("获取数据库连接");
		Class.forName(dbDriver);
		Connection connection = DriverManager.getConnection(dbUrl, dbUser,
				dbPassword);
		try {
			// 创建数据库信息对象
			DBInfo dbInfo = new DBInfo(connection);
			// 获取待生成数据库表信息
			logInfo("获取数据库表信息");
			if (generateTables == null || generateTables.size() <= 0) {
				if (tablenameFilter == null)
					rst = dbInfo.getAllTableInfos(dbCataLog, dbSchema);
				else
					rst = dbInfo.getAllTableInfos(dbCataLog, dbSchema,
							tablenameFilter);
			} else {
				rst = new HashSet<Table>();
				for (String tableName : generateTables) {
					Table table = dbInfo.getTableInfo(dbCataLog, dbSchema,
							tableName);
					rst.add(table);
				}
			}
			logInfo("获取数据库表信息完成");
		} finally {
			if (connection != null && !connection.isClosed())
				connection.close();
		}
		logInfo("关闭数据库连接");
		return rst;
	}

	/**
	 * 构建模板配置
	 * 
	 * @throws IOException
	 */
	private void buildTemplateConfiguration() throws IOException {
		// 创建模板配置对象
		templateConfiguration = new Configuration();
		templateConfiguration.setNumberFormat("0.##");
		// 设置模板加载位置
		templateConfiguration
				.setDirectoryForTemplateLoading(templateFileDirectory);
		if (encoding != null)
			templateConfiguration.setDefaultEncoding(encoding);
		// 针对文件名用的解析配置
		templateFileConfiguration = (Configuration) templateConfiguration
				.clone();
		// 构建字符串式模板加载器，由于解析文件名
		StringTemplateLoader strLoader = new StringTemplateLoader();
		// 扫描所有模板文件
		fixStringTemplateLoader(strLoader, templateFileDirectory);
		templateFileConfiguration.setTemplateLoader(strLoader);
	}

	/**
	 * 修正字符模板加载器
	 * 
	 * @param strLoader
	 * @param templateFileDirectory
	 * @throws IOException
	 */
	private void fixStringTemplateLoader(StringTemplateLoader strLoader,
			File templateFileDirectory) throws IOException {
		File[] templateFiles = null;
		if (templateFileFilter != null)
			templateFiles = templateFileDirectory.listFiles(templateFileFilter);
		else
			templateFiles = templateFileDirectory.listFiles();
		for (File templateFile : templateFiles) {
			strLoader.putTemplate(templateFile.getPath(),
					templateFile.getPath());
			if (templateFile.isDirectory())
				fixStringTemplateLoader(strLoader, templateFile);
		}
	}

	/**
	 * 生成指定的表
	 * 
	 * @param table
	 * @throws IOException
	 * @throws TemplateException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	private void generate(Table table) throws IOException, TemplateException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		logInfo("==============================开始生成数据库表：" + table.getName()
				+ "==============================");
		// 构建环境变量
		Map<String, Object> envVar = buildEnvVar(table);
		// 遍历模板文件夹，逐个模板进行生成
		File[] templateFiles = null;
		if (templateFileFilter != null)
			templateFiles = templateFileDirectory.listFiles(templateFileFilter);
		else
			templateFiles = templateFileDirectory.listFiles();
		for (File templateFile : templateFiles)
			generate("", templateFile, envVar);
		logInfo("==============================完成生成数据库表：" + table.getName()
				+ "==============================");
	}

	/**
	 * 创建环境变量
	 * 
	 * @param table
	 * @return
	 */
	private Map<String, Object> buildEnvVar(Table table) {
		Map<String, Object> rst = new HashMap<String, Object>();
		// 添加当前表对象数据
		rst.put(ENV_VAR_TABLE, table);
		// 添加函数
		rst.putAll(templateMethodModels);
		// 添加标签
		rst.putAll(templateDirectiveModels);
		// 添加自定义变量
		if (envVars != null)
			rst.putAll(envVars);
		return rst;
	}

	/**
	 * 执行递归文件生成
	 * 
	 * @param path
	 * @param templateFile
	 * @param envVar
	 * @throws IOException
	 * @throws TemplateException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	private void generate(String path, File templateFile,
			Map<String, Object> envVar) throws IOException, TemplateException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		if (templateFile.isDirectory()) {
			String outputFileName = getOutputFileName(templateFile, envVar);
			File outputFile = new File(outputFileName);
			// 创建对应目录
			if (!outputFile.exists())
				outputFile.mkdirs();
			// 遍历模板文件夹，逐个模板进行生成
			File[] templateFiles = null;
			if (templateFileFilter != null)
				templateFiles = templateFile.listFiles(templateFileFilter);
			else
				templateFiles = templateFile.listFiles();
			for (File _templateFile : templateFiles)
				generate(path + templateFile.getName() + File.separator,
						_templateFile, envVar);
		} else {
			logInfo("开始生成模板：" + templateFile.getPath());
			Template template = templateConfiguration.getTemplate(path
					+ templateFile.getName());
			if (encoding != null)
				template.setEncoding(encoding);
			// 获取输出路径
			String outputFileName = getOutputFileName(templateFile, envVar);
			File outputFile = new File(outputFileName);
			if (outputFile.getParentFile() != null
					&& !outputFile.getParentFile().exists())
				outputFile.getParentFile().mkdirs();
			// 创建写入器
			Writer writer = null;
			if (encoding != null)
				writer = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(outputFile), encoding));
			else
				writer = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(outputFile)));
			try {
				// 模板生成
				template.process(envVar, writer);
			} finally {
				writer.flush();
				writer.close();
			}
		}
	}

	/**
	 * 获取生成的文件名
	 * 
	 * @param file
	 * @param envVar
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws TemplateException
	 * @throws IOException
	 */
	private String getOutputFileName(File file, Map<String, Object> envVar)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, TemplateException, IOException {
		Template template = templateFileConfiguration.getTemplate(file
				.getPath());
		StringWriter sw = new StringWriter();
		template.process(envVar, sw);
		String tmp = sw.toString();
		// 去除模板根目录部分的路径
		if (tmp.startsWith(templateFileDirectory.getPath()))
			tmp = tmp.substring(templateFileDirectory.getPath().length());
		while (tmp.startsWith(File.separator))
			tmp = tmp.substring(1);
		if (outputFilePath.endsWith(File.separator))
			tmp = outputFilePath + tmp;
		else
			tmp = outputFilePath + File.separator + tmp;
		return tmp;
	}

	/**
	 * 日志输出
	 * 
	 * @param msg
	 */
	private void logInfo(Object msg) {
		if (log != null)
			log.info(msg);
	}
}
