<#include "/ftl_inc/inc_assign.ftl"/>
package ${basePackage}.${projectName}.${moduleName}.dao;

import java.util.*;

import org.developerworld.commons.command.OrderCommand;
import org.developerworld.commons.command.PageCommand;

import ${basePackage}.${projectName}.${moduleName}.model.${className};
/**
 * ${className}实体DAO
 * 
 * @author ${author}
 * @version ${version}
 */
<#--获取实体主键类型-->
<#assign pkType="Integer"/>
<#if table.primaryKey??>
	<#assign pkColumns=table.primaryKey.columns/>
	<#--复合主键-->
	<#if (pkColumns?size>1)>
		<#assign pkType=className+"."+className+"Pk"/>
	<#--普通主键-->
	<#else>
		<#assign pkType=columnTypeToFieldType(pkColumns[0].type,pkColumns[0].typeName)/>
	</#if>
</#if>
public interface ${className}Dao extends BaseDao<${className}, ${pkType}> {
	
	<#--用于记录本模板内已经定义的方法，避免重复定义-->
	<#assign methodsStr=","/>

	<#--根据外键情况，生成常用的查询方法-->
	<#list table.foreignKeys as foreignKey>
		
		<#--判断是多对一还是一对一-->
		<#assign isPrimaryKeyAndForeignKeySame=true/>
		<#--无主键，就肯定与主键不一致了-->
		<#if table.primaryKey??>
			<#list table.primaryKey.columns as pkColumn>
				<#assign hasSame=false/>
				<#list foreignKey.columns as fkColumn>
					<#if pkColumn.name==fkColumn.name>
						<#assign hasSame=true/>
						<#break/>
					</#if>
				</#list>
				<#if !hasSame>
					<#assign isPrimaryKeyAndForeignKeySame=false/>
					<#break/>
				</#if>
			</#list>
		<#else>
			<#assign isPrimaryKeyAndForeignKeySame=false/>
		</#if>
		<#--一对一情况-->
		<#if isPrimaryKeyAndForeignKeySame>
			<#assign methodName=""/>
			<#assign args=""/>
			<#assign columnStr=""/>
			<#list foreignKey.columns as column>
				<#assign fieldName=columnNameToFieldName(column.name)/>
				<#assign methodName=methodName+fieldName?cap_first/>
				<#assign args=args+columnTypeToFieldType(column.type,column.typeName)+" "+fieldName/>
				<#assign columnStr=columnStr+fieldName/>
				<#if column_has_next>
					<#assign methodName=methodName+"And"/>
					<#assign args=args+","/>
					<#assign columnStr=columnStr+","/>
				</#if>
			</#list>
			<#--判断方法是否已经存在-->
			<#if methodsStr?index_of(","+methodName+",")==-1>
				<#assign methodsStr=methodsStr+methodName+","/>
				/**
				 * 根据关联实体${tableNameToClassName(foreignKey.primaryTable.name)}的外键${columnStr}进行查询
				 *
				<#list foreignKey.columns as column>
					<#assign fieldName=columnNameToFieldName(column.name)/>
				 * @param ${fieldName}
				</#list>
				 * @return ${className}实体对象
				 */
				public ${className} findBy${methodName}(${args});
			</#if>
			
		<#--多对一情况-->
		<#else>
			<#assign foreignClass=tableNameToClassName(foreignKey.primaryTable.name)/>
			<#assign methodName=""/>
			<#assign args=""/>
			<#assign columnStr=""/>
			<#assign paramsStr=""/>
			<#list foreignKey.columns as column>
				<#if column_index!=0>
					<#assign methodName=methodName+"And"/>
					<#assign args=args+","/>
					<#assign columnStr=columnStr+","/>
					<#assign paramsStr=paramsStr+"\n"/>
				</#if>
				<#assign fieldName=columnNameToFieldName(column.name)/>
				<#assign methodName=methodName+fieldName?cap_first/>
				<#assign args=args+columnTypeToFieldType(column.type,column.typeName)+" "+fieldName/>
				<#assign columnStr=columnStr+fieldName/>
				<#assign paramsStr=paramsStr+"* @param "+fieldName/>
				<#--判断方法是否已经存在-->
				<#if methodsStr?index_of(","+methodName+",")==-1>
					<#assign methodsStr=methodsStr+methodName+","/>
					/**
					 * 根据关联实体${foreignClass}的外键${columnStr}进行查询
					 *
					 ${paramsStr}
					 *
					 * @return ${className}实体对象集合
					 */
					public List<${className}> findListBy${methodName}(${args});
					
					/**
					 * 根据关联实体${foreignClass}的外键${columnStr}进行查询
					 *
					 ${paramsStr}
					 *
					 * @return ${className}实体对象条目数
					 */
					public long findCountBy${methodName}(${args});
					
					/**
					 * 根据关联实体${foreignClass}的外键${columnStr}进行查询
					 *
					 ${paramsStr}
					 * @param pageCommand
					 * @return ${className}实体对象集合
					 */
					public List<${className}> findListBy${methodName}(${args},PageCommand pageCommand);
					
					/**
					 * 根据关联实体${foreignClass}的外键${columnStr}进行查询
					 *
					 ${paramsStr}
					 * @param orderCommand
					 * @return ${className}实体对象集合
					 */
					public List<${className}> findListBy${methodName}(${args},OrderCommand orderCommand);
					
					/**
					 * 根据关联实体${foreignClass}的外键${columnStr}进行查询
					 *
					 ${paramsStr}
					 * @param orderCommand
					 * @param pageCommand
					 * @return ${className}实体对象集合
					 */
					public List<${className}> findListBy${methodName}(${args},OrderCommand orderCommand,PageCommand pageCommand);
				</#if>
			</#list>
		</#if>
	</#list>
	
	<#--根据索引情况，生成常用的查询方法-->
	<#list table.indexs as index>
		<#--判断是否唯一索引-->
		<#if index.unique>
			<#assign methodName=""/>
			<#assign args=""/>
			<#assign columnStr=""/>
			<#list index.columns as column>
				<#assign fieldName=columnNameToFieldName(column.name)/>
				<#assign methodName=methodName+fieldName?cap_first/>
				<#assign args=args+columnTypeToFieldType(column.type,column.typeName)+" "+fieldName/>
				<#assign columnStr=columnStr+fieldName/>
				<#if column_has_next>
					<#assign methodName=methodName+"And"/>
					<#assign args=args+","/>
					<#assign columnStr=columnStr+","/>
				</#if>
			</#list>
			<#--判断方法是否已经存在-->
			<#if methodsStr?index_of(","+methodName+",")==-1>
				<#assign methodsStr=methodsStr+methodName+","/>
				/**
				 * 根据索引字段${columnStr}进行查询
				 *
				<#list index.columns as column>
					<#assign fieldName=columnNameToFieldName(column.name)/>
				 * @param ${fieldName}
				</#list>
				 * @return ${className}实体对象
				 */
				public ${className} findBy${methodName}(${args});
			</#if>
		<#else>
			<#assign methodName=""/>
			<#assign args=""/>
			<#assign columnStr=""/>
			<#assign paramsStr=""/>
			<#list index.columns as column>
				<#if column_index!=0>
					<#assign methodName=methodName+"And"/>
					<#assign args=args+","/>
					<#assign columnStr=columnStr+","/>
					<#assign paramsStr=paramsStr+"\n"/>
				</#if>
				<#assign fieldName=columnNameToFieldName(column.name)/>
				<#assign methodName=methodName+fieldName?cap_first/>
				<#assign args=args+columnTypeToFieldType(column.type,column.typeName)+" "+fieldName/>
				<#assign columnStr=columnStr+fieldName/>
				<#assign paramsStr=paramsStr+"* @param "+fieldName/>
				<#--判断方法是否已经存在-->
				<#if methodsStr?index_of(","+methodName+",")==-1>
					<#assign methodsStr=methodsStr+methodName+","/>
					/**
					 * 根据索引字段${columnStr}进行查询
					 *
					 ${paramsStr}
					 *
					 * @return ${className}实体对象集合
					 */
					public List<${className}> findListBy${methodName}(${args});
					
					/**
					 * 根据索引字段${columnStr}进行查询
					 *
					 ${paramsStr}
					 *
					 * @return ${className}实体对象条目数
					 */
					public long findCountBy${methodName}(${args});
					
					/**
					 * 根据索引字段${columnStr}进行查询
					 *
					 ${paramsStr}
					 * @param pageCommand
					 * 
					 * @return ${className}实体对象集合
					 */
					public List<${className}> findListBy${methodName}(${args},PageCommand pageCommand);
					
					/**
					 * 根据索引字段${columnStr}进行查询
					 *
					 ${paramsStr}
					 * @param orderCommand
					 * 
					 * @return ${className}实体对象集合
					 */
					public List<${className}> findListBy${methodName}(${args},OrderCommand orderCommand);
					
					/**
					 * 根据索引字段${columnStr}进行查询
					 *
					 ${paramsStr}
					 * @param orderCommand
					 * @param pageCommand
					 * 
					 * @return ${className}实体对象集合
					 */
					public List<${className}> findListBy${methodName}(${args},OrderCommand orderCommand,PageCommand pageCommand);
				</#if>
			</#list>
		</#if>
	</#list>
}
