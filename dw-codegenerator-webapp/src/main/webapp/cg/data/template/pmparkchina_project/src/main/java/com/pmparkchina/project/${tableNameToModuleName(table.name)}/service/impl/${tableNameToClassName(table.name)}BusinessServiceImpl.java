<#include "/ftl_inc/inc_assign.ftl"/>
package ${basePackage}.${projectName}.${moduleName}.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import javax.annotation.Resource;

import org.developerworld.commons.command.OrderCommand;
import org.developerworld.commons.command.PageCommand;
import org.developerworld.commons.beanutils.BeanUtils;

import org.springframework.stereotype.Service;

import ${basePackage}.${projectName}.${moduleName}.model.*;
import ${basePackage}.${projectName}.${moduleName}.dao.*;
import ${basePackage}.${projectName}.${moduleName}.service.${className}BusinessService;

/**
 * ${className}实体业务服务接口实现
 * 
 * @author ${author}
 * @version ${version}
 * 
 */
@Service("${moduleName}${className}BusinessService")
public class ${className}BusinessServiceImpl extends AbstractBaseServiceImpl implements
		${className}BusinessService {
	
	@Resource(name = "${moduleName}${className}Dao")
	private ${className}Dao ${objectName}Dao;
	
	<#--用于记录本模板内已经定义的方法，避免重复定义-->
	<#assign methodsStr=","/>

	<#--获取外键属性字段信息-->
	<#assign foreignArgs=""/>
	<#list table.foreignKeys as foreignKey>
		<#list foreignKey.columns as fkColumn>
			<#assign foreignArgs=foreignArgs+columnTypeToFieldType(fkColumn.type,fkColumn.typeName)+" "+columnNameToFieldName(fkColumn.name)+","/>
		</#list>
	</#list>
	public void create${className}(${foreignArgs} ${className} ${objectName}) throws Exception{
		<#list table.foreignKeys as foreignKey>
			<#assign foreignClass=tableNameToClassName(foreignKey.primaryTable.name)/>
			<#assign foreignObject=foreignColumnNameToFieldObjectName(foreignKey.columns[0].name,foreignKey.primaryTable.columns[0].name)/>
			<#assign foreignModuleName=tableNameToModuleName(foreignKey.primaryTable.name)/>
			<#assign foreignPackage=""/>
			<#if foreignModuleName!=moduleName>
				<#assign foreignPackage=basePackage+"."+projectName+"."+foreignModuleName+".model."/>
			</#if>
			${foreignPackage+foreignClass} _${foreignObject}=null;
			<#list foreignKey.columns as fkColumn>
				<#assign fieldName=columnNameToFieldName(fkColumn.name)/>
				<#assign foreignField=columnNameToFieldName(foreignKey.primaryColumns[fkColumn_index].name)/>
				<#--判断字段类型-->
				if(${fieldName}!=null){
					if(_${foreignObject}==null)
						_${foreignObject}=new ${foreignPackage+foreignClass}();
					_${foreignObject}.set${foreignField?cap_first}(${fieldName});
				}
			</#list>
			${objectName}.set${foreignObject?cap_first}(_${foreignObject});
		</#list>
		${objectName}Dao.save(${objectName});
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
			<#assign pkType=columnTypeToFieldType(pkColumns[0].type,pkColumns[0].typeName)/>
			<#assign pkName=columnNameToFieldName(pkColumns[0].name)/>
		</#if>
		public ${className} get${className}By${pkName?cap_first}(${pkType} ${pkName}){
			return ${objectName}Dao.findByPk(${pkName});
		}
		<#assign methodsStr=methodsStr+pkName?cap_first+","/>
		
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
					<#assign foreignArgs=foreignArgs+columnTypeToFieldType(fkColumn.type,fkColumn.typeName)+" "+columnNameToFieldName(fkColumn.name)+","/>
				</#if>
			</#list>
		</#list>
		public void update${className}(${pkType} ${pkName},${foreignArgs} ${className} ${objectName}) throws Exception{
			//设置关联实体属性
			${className} curr${className}=get${className}By${pkName?cap_first}(${pkName});
			<#list table.foreignKeys as foreignKey>
				<#assign foreignClass=tableNameToClassName(foreignKey.primaryTable.name)/>
				<#assign foreignObject=foreignColumnNameToFieldObjectName(foreignKey.columns[0].name,foreignKey.primaryTable.columns[0].name)/>
				<#assign foreignModuleName=tableNameToModuleName(foreignKey.primaryTable.name)/>
				<#assign foreignPackage=""/>
				<#if foreignModuleName!=moduleName>
					<#assign foreignPackage=basePackage+"."+projectName+"."+foreignModuleName+".model."/>
				</#if>
				${foreignPackage+foreignClass} _${foreignObject}=null;
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
						<#assign fieldName=columnNameToFieldName(fkColumn.name)/>
						<#assign foreignField=columnNameToFieldName(foreignKey.primaryColumns[fkColumn_index].name)/>
						if(${fieldName}!=null){
							if(_${foreignObject}==null)
								_${foreignObject}=new ${foreignPackage+foreignClass}();
							_${foreignObject}.set${foreignField?cap_first}(${fieldName});
						}
					</#if>
				</#list>
				if(_${foreignObject}!=null)
					curr${className}.set${foreignObject?cap_first}(_${foreignObject});
			</#list>
			//复制新属性信息
			<#assign includeFields=""/>
			<#list table.columns as column>
				<#--主键-->
				<#assign isPkColumn=false/>
				<#if table.primaryKey??>
					<#list table.primaryKey.columns as pkColumn>
						<#if pkColumn.name==column.name>
							<#assign isPkColumn=true/>
							<#break/>
						</#if>
					</#list>
				</#if>
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
				<#if !isPkColumn && !isFkColumn>
					<#assign includeFields=includeFields+"\""+columnNameToFieldName(column.name)+"\","/>
				</#if>
			</#list>
			<#if includeFields?ends_with(",")>
				<#assign includeFields=includeFields?substring(0,includeFields?length-1)/>
				BeanUtils.copyPropertiesIncludeFields(curr${className},${objectName},new String[]{${includeFields}});
			<#else>
				BeanUtils.copyProperties(curr${className},${objectName});
			</#if>
			${objectName}Dao.update(curr${className});
		}

		public void delete${className}By${pkName?cap_first}(${pkType} ${pkName}) throws Exception{
			${objectName}Dao.delete(${pkName});
		}
		
		public void delete${className}By${pkName?cap_first}s(${pkType}[] ${pkName}s) throws Exception{
			${objectName}Dao.delete(${pkName}s);
		}
	</#if>

	public long get${className}Count(){
		return ${objectName}Dao.findCount();
	}
	
	public List<${className}> get${className}List(){
		return ${objectName}Dao.findList();
	}
	
	public List<${className}> get${className}List(OrderCommand orderCommand){
		return ${objectName}Dao.findList(orderCommand.toSqlOrderString());
	}
	
	public List<${className}> get${className}List(PageCommand pageCommand){
		return ${objectName}Dao.findList(pageCommand.getPageNum(),pageCommand.getPageSize());
	}
	
	public List<${className}> get${className}List(OrderCommand orderCommand,PageCommand pageCommand){
		return ${objectName}Dao.findList(orderCommand.toSqlOrderString(),pageCommand.getPageNum(),pageCommand.getPageSize());
	}
	
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
			<#assign args2=""/>
			<#list foreignKey.columns as column>
				<#assign fieldName=columnNameToFieldName(column.name)/>
				<#assign methodName=methodName+fieldName?cap_first/>
				<#assign args=args+columnTypeToFieldType(column.type,column.typeName)+" "+fieldName/>
				<#assign args2=args2+fieldName/>
				<#if column_has_next>
					<#assign methodName=methodName+"And"/>
					<#assign args=args+","/>
					<#assign args2=args2+","/>
				</#if>
			</#list>
			<#if methodsStr?index_of(","+methodName+",")==-1>
				<#assign methodsStr=methodsStr+methodName+","/>
				public ${className} get${className}By${methodName}(${args}){
					return ${objectName}Dao.findBy${methodName}(${args2});
				}
			</#if>
		<#--多对一情况-->
		<#else>
			<#assign methodName=""/>
			<#assign args=""/>
			<#assign args2=""/>
			<#list foreignKey.columns as column>
				<#if (column_index>0)>
					<#assign methodName=methodName+"And"/>
					<#assign args=args+","/>
					<#assign args2=args2+","/>
				</#if>
				<#assign fieldName=columnNameToFieldName(column.name)/>
				<#assign methodName=methodName+fieldName?cap_first/>
				<#assign args=args+columnTypeToFieldType(column.type,column.typeName)+" "+fieldName/>
				<#assign args2=args2+fieldName/>
				<#if methodsStr?index_of(","+methodName+",")==-1>
					<#assign methodsStr=methodsStr+methodName+","/>
					<#assign foreignClassName=tableNameToClassName(foreignKey.primaryTable.name)/>
					<#assign foreignObjectName=foreignClassName?uncap_first/>
					public List<${className}> get${className}ListBy${methodName}(${args}){
						return ${objectName}Dao.findListBy${methodName}(${args2});
					}
					
					public long get${className}CountBy${methodName}(${args}){
						return ${objectName}Dao.findCountBy${methodName}(${args2});
					}
		
					public List<${className}> get${className}ListBy${methodName}(${args},OrderCommand orderCommand){
						return ${objectName}Dao.findListBy${methodName}(${args2},orderCommand);
					}
					
					public List<${className}> get${className}ListBy${methodName}(${args},PageCommand pageCommand){
						return ${objectName}Dao.findListBy${methodName}(${args2},pageCommand);
					}
					
					public List<${className}> get${className}ListBy${methodName}(${args},OrderCommand orderCommand,PageCommand pageCommand){
						return ${objectName}Dao.findListBy${methodName}(${args2},orderCommand,pageCommand);
					}
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
			<#assign args2=""/>
			<#list index.columns as column>
				<#assign fieldName=columnNameToFieldName(column.name)/>
				<#assign methodName=methodName+fieldName?cap_first/>
				<#assign args=args+columnTypeToFieldType(column.type,column.typeName)+" "+fieldName/>
				<#assign args2=args2+fieldName/>
				<#if column_has_next>
					<#assign methodName=methodName+"And"/>
					<#assign args=args+","/>
					<#assign args2=args2+","/>
				</#if>
			</#list>
			<#if methodsStr?index_of(","+methodName+",")==-1>
				<#assign methodsStr=methodsStr+methodName+","/>
				public ${className} get${className}By${methodName}(${args}){
					return ${objectName}Dao.findBy${methodName}(${args2});
				}
			</#if>
		<#else>
			<#assign methodName=""/>
			<#assign args=""/>
			<#assign args2=""/>
			<#list index.columns as column>
				<#if (column_index>0)>
					<#assign methodName=methodName+"And"/>
					<#assign args=args+","/>
					<#assign args2=args2+","/>
				</#if>
				<#assign fieldName=columnNameToFieldName(column.name)/>
				<#assign methodName=methodName+fieldName?cap_first/>
				<#assign args=args+columnTypeToFieldType(column.type,column.typeName)+" "+fieldName/>
				<#assign args2=args2+fieldName/>
				<#if methodsStr?index_of(","+methodName+",")==-1>
					<#assign methodsStr=methodsStr+methodName+","/>
					public List<${className}> get${className}ListBy${methodName}(${args}){
						return ${objectName}Dao.findListBy${methodName}(${args2});
					}
					
					public long get${className}CountBy${methodName}(${args}){
						return ${objectName}Dao.findCountBy${methodName}(${args2});
					}
					
					public List<${className}> get${className}ListBy${methodName}(${args},OrderCommand orderCommand){
						return ${objectName}Dao.findListBy${methodName}(${args2},orderCommand);
					}
					
					public List<${className}> get${className}ListBy${methodName}(${args},PageCommand pageCommand){
						return ${objectName}Dao.findListBy${methodName}(${args2},pageCommand);
					}
					
					public List<${className}> get${className}ListBy${methodName}(${args},OrderCommand orderCommand,PageCommand pageCommand){
						return ${objectName}Dao.findListBy${methodName}(${args2},orderCommand,pageCommand);
					}
				</#if>
			</#list>
		</#if>
	</#list>
	
}
