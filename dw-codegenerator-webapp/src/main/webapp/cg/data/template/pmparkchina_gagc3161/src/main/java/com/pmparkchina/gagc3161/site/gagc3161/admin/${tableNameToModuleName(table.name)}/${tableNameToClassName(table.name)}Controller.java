<#include "/ftl_inc/inc_assign.ftl"/>

package com.pmparkchina.${projectName}.site.${projectName}.admin.${moduleName};

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.developerworld.commons.command.OrderCommand;
import org.developerworld.commons.command.PageCommand;
import org.developerworld.commons.command.ResultCommand;
import org.developerworld.commons.beanutils.BeanUtils;
import org.developerworld.commons.lang.OperationException;
import org.developerworld.frameworks.webui.datagrid.DataGrid;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.pmparkchina.${projectName}.${moduleName}.model.${className};
import com.pmparkchina.${projectName}.${moduleName}.service.${className}BusinessService;

/**
 * ${className}对象Controller
 * 
 * @author ${author}
 * @version ${version}
 * 
 */
@Controller("${moduleName}${className}Controller")
@RequestMapping("/${projectName}/admin/${moduleName}/${objectName}")
public class ${className}Controller extends AbstractBaseController {
	
	@Resource(name="${moduleName}${className}BusinessService")
	private ${className}BusinessService businessService;

	/**
	 * 首页
	 * @return
	 */
	@RequestMapping
	public String index() {
		return indexView();
	}
	
	/**
	 * 数据列表数据
	 * @param orderCommand
	 * @param pageCommand
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> list(OrderCommand orderCommand, PageCommand pageCommand)
			throws Exception {
		// 获取条件查询总数
		long total = businessService.get${className}Count();
		// 设置总数
		pageCommand.setTotal(total);
		pageCommand.reload();
		// 获取数据列表
		List<${className}> ${objectName}List = businessService.get${className}List(orderCommand, pageCommand);
		DataGrid<Map<String, Object>> dg = new DataGrid<Map<String, Object>>();
		dg.setDatas(buildDtos(${objectName}List));
		// 设置排序参数
		dg.setOrderCommand(orderCommand);
		// 设置分页参数
		dg.setPageCommand(pageCommand);
		<#if table.primaryKey??>
			<#assign pkName=""/>
			<#assign pkColumns=table.primaryKey.columns/>
			<#--复合主键-->
			<#if (pkColumns?size>1)>
				<#assign pkName=objectName+"Pk"/>
			<#--普通主键-->
			<#else>
				<#assign pkName=columnNameToFieldName(pkColumns[0].name)/>
			</#if>
			dg.addTitle().setKey("${pkName}").setName(getMessage("${moduleName}.${objectName}.${pkName}"));
		</#if>
		// 设置标题
		<#list table.columns as column>
			<#--跳过主键-->
			<#assign isPkColumn=false/>
			<#if table.primaryKey??>
				<#list table.primaryKey.columns as pkColumn>
					<#if pkColumn.name==column.name>
						<#assign isPkColumn=true/>
						<#break/>
					</#if>
				</#list>
			</#if>
			<#--跳过外键-->
			<#assign isFkColumn=false/>
			<#list table.foreignKeys as foreignKey>
				<#list foreignKey.columns as fkColumn>
					<#--若当前字段属于外键之一，就设置变量，并跳出循环-->
					<#if column.name==fkColumn.name>
						<#assign isFkColumn=true/>
						<#break/>
					</#if>
				</#list>
				<#if isFkColumn>
					<#break/>
				</#if>
			</#list>
			<#if !isPkColumn && !isFkColumn>
				<#assign fieldName=columnNameToFieldName(column.name)/>
				dg.addTitle().setKey("${fieldName}").setName(getMessage("${moduleName}.${objectName}.${fieldName}"));
			</#if>
		</#list>
		return dg.toMap();
	}
	
	/**
	 * 创建数据传输对象集合
	 * 
	 * @param ${objectName}s
	 * 
	 * @return 数据传输对象集合
	 * 
	 * @throws InstantiationException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	private List<Map<String,Object>> buildDtos(List<${className}> ${objectName}s) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException{
		List<Map<String,Object>> rst=new ArrayList<Map<String,Object>>();
		for(${className} ${objectName}:${objectName}s)
			rst.add(buildDto(${objectName}));
		return rst;
	}
	
	/**
	 * 创建数据传输对象
	 * 
	 * @param ${objectName}
	 * 
	 * @return 数据传输对象
	 * 
	 * @throws InstantiationException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	private Map<String,Object> buildDto(${className} ${objectName}) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException{
		//复制新属性信息
		<#assign includeFields=""/>
		<#list table.columns as column>
			<#--外键-->
			<#assign isFkColumn=false/>
			<#if (table.foreignKeys?? && table.foreignKeys?size>0)>
				<#list table.foreignKeys as foreignKey>
					<#list foreignKey.columns as fkColumn>
						<#--若当前字段属于外键之一，就设置变量，并跳出循环-->
						<#if column.name==fkColumn.name>
							<#assign isFkColumn=true/>
							<#break/>
						</#if>
					</#list>
					<#if isFkColumn>
						<#break/>
					</#if>
				</#list>
			</#if>
			<#if !isFkColumn>
				<#assign includeFields=includeFields+"\""+columnNameToFieldName(column.name)+"\","/>
			</#if>
		</#list>
		<#if includeFields=="">
		return BeanUtils.describe(${objectName});
		<#else>
		return BeanUtils.describeIncludeFields(${objectName}, new String[]{${includeFields?substring(0,(includeFields?length-1))}});
		</#if>
	}

	/**
	 * 新建页
	 * @return
	 */
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public ModelAndView editNew() {
		return new ModelAndView(newView()).addObject("${objectName}", new ${className}());
	}

	/**
	 * 处理创建请求
	 * @param ${objectName}
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> create(${className} ${objectName}) {
		try {
			
			<#--获取外键属性字段信息-->
			<#assign foreignArgs=""/>
			<#list table.foreignKeys as foreignKey>
				<#list foreignKey.columns as fkColumn>
					<#assign foreignObject=foreignColumnNameToFieldObjectName(fkColumn.name,foreignKey.primaryColumns[fkColumn_index].name)/>
					<#assign foreignArgs=foreignArgs+objectName+".get"+foreignObject?cap_first+"().get"+columnNameToFieldName(foreignKey.primaryColumns[fkColumn_index].name)?cap_first+"(),"/>
				</#list>
			</#list>
			businessService.create${className}(${foreignArgs} ${objectName});
			return new ResultCommand<Map<String, Object>>()
					.status(ResultCommand.STATUS_SUCCESS)
					.message(getMessage("${moduleName}.message.create.success")).toMap();
		} catch (OperationException e) {
			return new ResultCommand<Map<String, Object>>()
					.status(ResultCommand.STATUS_ERROR)
					.message(getMessage(e.getMessage())).toMap();
		} catch (Exception e) {
			return new ResultCommand<Map<String, Object>>()
					.status(ResultCommand.STATUS_ERROR)
					.message(
							getMessage("${moduleName}.message.create.error") + ":"
									+ e.getMessage()).toMap();
		}
	}

	<#if table.primaryKey??>
		<#assign pkType=""/>
		<#assign pkName=""/>
		<#assign pkColumns=table.primaryKey.columns/>
		<#--复合主键-->
		<#if (pkColumns?size>1)>
			<#assign pkType=className+"."+className+"Pk"/>
			<#assign pkName=objectName+"Pk"/>
		<#--普通主键-->
		<#else>
			<#assign pkType=columnTypeToFieldType(pkColumns[0].type)/>
			<#assign pkName=columnNameToFieldName(pkColumns[0].name)/>
		</#if>
	/**
	 * 编辑页
	 * @param ${pkName}
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/{${pkName}}/edit", method = RequestMethod.GET)
	public ModelAndView edit(@PathVariable ${pkType} ${pkName}) throws Exception{
		${className} ${objectName} = businessService.get${className}By${pkName?cap_first}(${pkName});
		return new ModelAndView(editView()).addObject("${objectName}", ${objectName});
	}
	</#if>
	
	<#if table.primaryKey??>
		<#assign pkType=""/>
		<#assign pkName=""/>
		<#assign pkColumns=table.primaryKey.columns/>
		<#--复合主键-->
		<#if (pkColumns?size>1)>
			<#assign pkType=className+"."+className+"Pk"/>
			<#assign pkName=objectName+"Pk"/>
		<#--普通主键-->
		<#else>
			<#assign pkType=columnTypeToFieldType(pkColumns[0].type)/>
			<#assign pkName=columnNameToFieldName(pkColumns[0].name)/>
		</#if>
		
		<#--获取外键属性字段信息-->
		<#assign foreignArgs=""/>
		<#list table.foreignKeys as foreignKey>
			<#list foreignKey.columns as fkColumn>
				<#--若外键等于主键，则跳过-->
				<#assign isPkColumn=false/>
				<#list table.primaryKey.columns as pkColumn>
					<#if pkColumn.name==fkColumn.name>
						<#assign isPkColumn=true/>
						<#break/>
					</#if>
				</#list>
				<#if !isPkColumn>
					<#assign foreignObject=foreignColumnNameToFieldObjectName(fkColumn.name,foreignKey.primaryColumns[fkColumn_index].name)/>
					<#assign foreignArgs=foreignArgs+objectName+".get"+foreignObject?cap_first+"().get"+columnNameToFieldName(foreignKey.primaryColumns[fkColumn_index].name)?cap_first+"(),"/>
				</#if>
			</#list>
		</#list>
	/**
	 * 处理修改请求
	 * @param ${pkName}
	 * @param ${objectName}
	 * @return
	 */
	@RequestMapping(value = "/{${pkName}}", method = RequestMethod.PUT)
	@ResponseBody
	public Map<String, Object> update(@PathVariable ${pkType} ${pkName}, ${className} ${objectName}) {
		try {
			businessService.update${className}(${pkName},${foreignArgs} ${objectName});
			return new ResultCommand<Map<String, Object>>()
					.status(ResultCommand.STATUS_SUCCESS)
					.message(getMessage("${moduleName}.message.update.success")).toMap();
		} catch (Throwable t) {
			return new ResultCommand<Map<String, Object>>()
					.status(ResultCommand.STATUS_ERROR)
					.message(
							getMessage("${moduleName}.message.update.error") + ":"
									+ t.getMessage()).toMap();
		}
	}
	</#if>

	<#if table.primaryKey??>
		<#assign pkType=""/>
		<#assign pkName=""/>
		<#assign pkColumns=table.primaryKey.columns/>
		<#--复合主键-->
		<#if (pkColumns?size>1)>
			<#assign pkType=className+"."+className+"Pk"/>
			<#assign pkName=objectName+"Pk"/>
		<#--普通主键-->
		<#else>
			<#assign pkType=columnTypeToFieldType(pkColumns[0].type)/>
			<#assign pkName=columnNameToFieldName(pkColumns[0].name)/>
		</#if>
	/**
	 * 处理删除数据
	 * @param ${pkName}
	 * @return
	 */
	@RequestMapping(method = RequestMethod.DELETE)
	@ResponseBody
	public Map<String, Object> destory(${pkType}[] ${pkName}) throws OperationException{
		businessService.delete${className}By${pkName?cap_first}s(${pkName});
		return new ResultCommand<Map<String, Object>>()
				.status(ResultCommand.STATUS_SUCCESS)
				.message(getMessage("${moduleName}.message.delete.success")).toMap();
	}
	</#if>
}
