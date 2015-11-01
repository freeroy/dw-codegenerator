<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@include file="commons/init_import.jsp"%>
<%@include file="commons/init_define.jsp"%>
<%@include file="commons/web_top.jsp"%>
<div class="body">
	<div style="width: 700px; text-align: left; margin: auto">
			本模板用于供模板开发者参考，使之了解如何根据数据库元信息生成程序<br /> <br /> 1、背景知识<br />
			要懂得如何编写模板，首先就要懂得FreeMarker模板语言，<br />
			其官网网址为：http://freemarker.org/，上面有大量参考例子。也建议开发人员阅读《FreeMarker
			手册》一书（电子版，上网下载）<br />
			建议开发人员最起码要弄懂常见的freemarker语法，包括循环、递归、各类对象如何定义、嵌套等<br />
			下面内容将假设开发人员懂得如何使用freemarker进行模板开发。<br /> <br /> 2、如果读取环境变量信息<br />
			在配置Generator时，可以定义一下用于生成时的环境变量信息（通过setEnvVar方法配置），在模板中，我们可以通过以下方法获取变量信息：<br />
			读取projectName变量信息：${projectName}<br /> <br /> 3、获取数据库表信息<br />
			3.1、生成器在访问毎一个模板文件的时候，都会带住相关数据库表信息对像，用于供模板读取表信息，该对象面为table，我们可以通过以下代码引用该对象(具体输出结构，请看生成后的文件)<br />
			${table}<br />
			该对象实则上市org.developerworld.commons.db.info.Table类的一个对象实例。<br />
			其包含大量数据库表的信息。<br /> <br /> 3.2、获取表所在数据库名<br /> ${table.catalog!""}<br />
			<br /> 3.3、获取表所在数据库空间<br /> ${table.schema!""}<br /> <br />
			3.4、获取表名<br /> ${table.name!""}<br /> <br /> 3.5、获取表类型(物理表、视图...)<br />
			${table.type!""}<br /> <br /> 3.6、表备注<br /> ${table.remarks!""}<br />
			<br /> 3.7、获取字段集合<br />
			${table.columns}，返回一个Set集合，集合中的元素为org.developerworld.commons.db.info.Column对象<br />
			3.7.1、每个column对象的信息<br /> 字段名称：table.columns[?].name<br />
			字段类型：table.columns[?].type,返回类型对应java.sql.Types<br />
			字段类型名：table.columns[?].typeName，如返回varchar、datetime等，具体视数据库定义而定<br />
			字段长度：table.columns[?].length<br />
			字段小数点后的位数：table.columns[?].decimalDigits<br />
			是否自增值字段：table.columns[?].isAutoIncrement<br />
			是否允许为空：table.columns[?].isAutoIncrement<br />
			默认值：table.columns[?].defaultValue<br /> 备注：table.columns[?].remarks<br />
			<br /> 3.8、获取主键信息<br />
			${table.primaryKey}，返回org.developerworld.commons.db.info.PrimarkKey对象<br />
			<br /> 3.8.1、获取主键其它信息<br /> 主键名：table.primaryKey.name<br />
			主键关联字段：table.primaryKey.columns，返回一个Set集合，集合中的元素为org.developerworld.commons.db.info.Column对象<br />
			主键关联的外键表：table.primaryKey.foreignTables，返回List集合，集合中的元素为org.developerworld.commons.db.info.Table对象<br />
			主键关联的外检表对应外键列：table.primaryKey.foreignColumns，返回一个Set集合，毎个元素代表针对一个外键关联表的关联字段集合，所以元素也为Set，<br />
			<br /> 3.9、获取外键信息<br />
			${table.foreignKeys}，返回一个Set集合，集合中的元素为org.developerworld.commons.db.info.ForeignKey对象<br />
			<br /> 3.9.1、获取外键其它信息<br /> 外键名：table.foreignKeys[?].name<br />
			外键关联本表字段：table.foreignKeys[?].columns，返回一个Set集合，集合中的元素为org.developerworld.commons.db.info.Column对象<br />
			外键对应主表的字段：table.foreignKeys[?].primaryColumns，返回一个Set集合，集合中的元素为org.developerworld.commons.db.info.Column对象<br />
			外键关联的主表：table.foreignKeys[?].primaryTable,返回developerworld.commons.db.info.Table对象<br />
			<br /> 3.10、获取索引信息<br />
			${table.indexs}，返回一个Set集合，集合中的元素为org.developerworld.commons.db.info.Index对象<br />
			<br /> 3.10.1、获取索引其它信息<br /> 索引名：table.indexs[?].name<br />
			索引关联字段：table.indexs[?].columns，返回一个Set集合，集合中的元素为org.developerworld.commons.db.info.Column对象<br />
			索引排列方式：table.indexs[?].columnSortords，返回一个List集合，集合中元素为针对不同索引字段的排序方式(asc/desc)<br />
			索引是否唯一：table.indexs[?].isUnique<br /><br />
			4、关于自定义标签及自定义函数<br /><br />
			4.1、开发者可以根据自己的生成需要，进行相关模板函数、标签的自定义开发（参考freemarker规范）。<br /><br />
			4.2、完成相关程序开发后，可在step_5.jsp文件中，在生成器中注册这些自定义的标签及函数（通过调用addTemplateDirectiveModel、addTemplateMethodModel方法进行注册），并在模板中使用.<br /><br />
			4.3、默认地，会提供以下自定义函数支持(相关程序在org.developerworld.product.codegenerator.freemarker.method包下)：<br />
			columnNameToFieldName(String columnName)——根据字段名返回成员变量名(字段名根据规范，要求多个单词之间，用"_"分隔)<br />
			columnTypeToFieldType(int type)——根据数据库字段类型，返回对应的java类型(数据库字段类型根据java.sql.Types决定)<br />
			foreignColumnNameToFieldObjectName(String columnName,String foreignColumnName)——根据外键字段名返回成员变量对象名<br />
			tableNameToClassName(String tableName)——根据表名返回类名<br />
			tableNameToModuleName(String tableName)——根据表名返回模块名（根据规范，表名第一个"_"之前的值，为模块名）<br />
			tableNameToObjectName(String tableName)——根据表名返回对象名
	</div>
	<div class="controller" style="margin:20px 0px 0px 0px"><input type="button" value="返回首页" onclick="location.href='index.jsp'"/><input type="button" value="开始使用" onclick="location.href='step_1.jsp'" /></div>
</div>
<%@include file="commons/web_bottom.jsp"%>