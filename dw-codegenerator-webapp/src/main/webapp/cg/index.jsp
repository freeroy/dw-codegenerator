<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@include file="commons/init_import.jsp"%>
<%@include file="commons/init_define.jsp"%>
<%@include file="commons/web_top.jsp"%>
<div class="body">
	<div style="width: 700px; text-align: left; margin: auto">
		<p style="line-height: 20px">代码生成器就是根据数据库信息（数据库表），配合按照规范定义好的代码模板（基于freemarker实现），生成目标程序代码（jsp、Java等）。</p>
		<div style="text-align: center"><img src="images/img_1.png"/></div>
		<p>当开发人员需要生成代码，需要执行如下步骤：</p>
		<p>1. 根据数据库设计，在数据库中创建数据表</p>
		<p>2. 根据开发规范定义，编写好生成模板</p>
		<p>3. 配置代码生成器相关信息</p>
		<p>4. 执行代码生成方法</p>
		<p>5. 获取生成的代码</p>
		<div style="text-align: center"><img src="images/img_2.png" /></div>
		<p style="line-height: 20px">值得注意的是，代码生成器是可以生成任何平台（Java、.Net、Php）任何架构的项目代码的！主要原因为其最终代码的表现形式，是根据模板定义的，而模板是由开发人员根据其架构环境所定义的规范编写制作的。因此不同架构环境，可以编写符合不同规范的代码生成模板，生成不同的代码。
		</p>
		<div class="controller"><input type="button" value="参考信息" onclick="location.href='help.jsp'"/><input type="button" value="开始使用" onclick="location.href='step_1.jsp'" /></div>
	</div>
</div>
<%@include file="commons/web_bottom.jsp"%>