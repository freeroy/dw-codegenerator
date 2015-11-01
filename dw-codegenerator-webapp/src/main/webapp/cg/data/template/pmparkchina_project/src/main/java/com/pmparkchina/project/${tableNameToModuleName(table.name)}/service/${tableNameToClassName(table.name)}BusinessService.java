<#include "/ftl_inc/inc_assign.ftl"/>
package ${basePackage}.${projectName}.${moduleName}.service;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import org.developerworld.commons.lang.OperationException;
import org.developerworld.commons.command.OrderCommand;
import org.developerworld.commons.command.PageCommand;

import ${basePackage}.${projectName}.${moduleName}.model.*;

/**
 * ${className}实体业务服务接口
 * @author ${author}
 * @version ${version}
 *
 */
public interface ${className}BusinessService extends BaseService{
	
	<#--用于记录本模板内已经定义的方法，避免重复定义-->
	<#assign methodsStr=","/>
	
	<#--获取外键属性字段信息-->
	<#assign foreignArgs=""/>
	<#list table.foreignKeys as foreignKey>
		<#list foreignKey.columns as fkColumn>
			<#assign foreignArgs=foreignArgs+columnTypeToFieldType(fkColumn.type,fkColumn.typeName)+" "+columnNameToFieldName(fkColumn.name)+","/>
		</#list>
	</#list>
	/**
	 * 创建${className}实体对象
	 * 
	<#list table.foreignKeys as foreignKey>
		<#list foreignKey.columns as fkColumn>
	 * @param ${columnNameToFieldName(fkColumn.name)}
		</#list>
	</#list>
	 * @param ${objectName}
	 * 
	 * @throws OperationException
	 */
	public void create${className}(${foreignArgs} ${className} ${objectName}) throws OperationException;
	
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
		/**
		 * 根据主键，获取${className}实体对象
		 * 
		 * @param ${pkName}
		 * 
		 * @return 实体对象或空
		 */
		public ${className} get${className}By${pkName?cap_first}(${pkType} ${pkName});
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
		/**
		 * 修改${className}实体对象信息
		 * 
		 * @param ${pkName}
		<#list table.foreignKeys as foreignKey>
			<#list foreignKey.columns as fkColumn>
		 * @param ${columnNameToFieldName(fkColumn.name)}
			</#list>
		</#list>
		 * @param ${objectName}
		 * 
		 * @throws NoSuchMethodException 
		 * @throws InvocationTargetException 
		 * @throws IllegalAccessException 
		 * @throws InstantiationException
		 * @throws OperationExcception
		 */
		public void update${className}(${pkType} ${pkName},${foreignArgs} ${className} ${objectName})  throws IllegalAccessException, InvocationTargetException, NoSuchMethodException,InstantiationException, OperationException;
		
		/**
		 * 根据主键删除${className}实体对象信息
		 * 
		 * @param ${pkName}
		 * 
		 * @throws OperationException
		 */
		public void delete${className}By${pkName?cap_first}(${pkType} ${pkName}) throws OperationException;
		
		/**
		 * 根据主键集合删除${className}实体对象信息
		 * 
		 * @param ${pkName}s
		 * 
		 * @throws OperationException
		 */
		public void delete${className}By${pkName?cap_first}s(${pkType}[] ${pkName}s) throws OperationException;
	</#if>
	
	/**
	 * 获取${className}实体的对象数目
	 * 
	 * @return 实体对象数目
	 */
	public long get${className}Count();
	
	/**
	 * 获取${className}实体所有对象集合
	 * 
	 * @return ${className}实体所有对象集合
	 */
	public List<${className}> get${className}List();
	
	/**
	 * 获取${className}实体所有根据排序信息排序后的对象集合
	 * 
	 * @param orderCommand
	 * 
	 * @return ${className}实体所有对象集合
	 */
	public List<${className}> get${className}List(OrderCommand orderCommand);
	
	/**
	 * 获取${className}实体对象集合
	 * 
	 * @param pageCommand
	 * 
	 * @return ${className}实体所有对象集合
	 */
	public List<${className}> get${className}List(PageCommand pageCommand);
	
	/**
	 * 获取${className}实体根据排序信息排序后的对象集合
	 * 
	 * @param orderCommand
	 * @param pageCommand
	 * 
	 * @return ${className}实体所有对象集合
	 */
	public List<${className}> get${className}List(OrderCommand orderCommand,PageCommand pageCommand);
		
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
				public ${className} get${className}By${methodName}(${args});
			</#if>
		<#--多对一情况-->
		<#else>
			<#assign foreignClass=tableNameToClassName(foreignKey.primaryTable.name)/>
			<#assign methodName=""/>
			<#assign args=""/>
			<#assign columnStr=""/>
			<#assign paramsStr=""/>
			<#list foreignKey.columns as foreignColumn>
				<#if (foreignColumn_index>0)>
					<#assign methodName=methodName+"And"/>
					<#assign args=args+","/>
					<#assign columnStr=columnStr+","/>
					<#assign paramsStr=paramsStr+"\n"/>
				</#if>
				<#assign fieldName=columnNameToFieldName(foreignColumn.name)/>
				<#assign methodName=methodName+fieldName?cap_first/>
				<#assign args=args+columnTypeToFieldType(foreignColumn.type,foreignColumn.typeName)+" "+fieldName/>
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
					public List<${className}> get${className}ListBy${methodName}(${args});
					
					/**
					 * 根据关联实体${foreignClass}的外键${columnStr}进行查询
					 *
					 ${paramsStr}
					 *
					 * @return ${className}实体对象条目数
					 */
					public long get${className}CountBy${methodName}(${args});
					
					/**
					 * 根据关联实体${foreignClass}的外键${columnStr}进行查询
					 *
					 ${paramsStr}
					 * @param pageCommand
					 * 
					 * @return ${className}实体对象集合
					 */
					public List<${className}> get${className}ListBy${methodName}(${args},PageCommand pageCommand);
					
					/**
					 * 根据关联实体${foreignClass}的外键${columnStr}进行查询
					 *
					 ${paramsStr}
					 * @param orderCommand
					 * 
					 * @return ${className}实体对象集合
					 */
					public List<${className}> get${className}ListBy${methodName}(${args},OrderCommand orderCommand);
					
					/**
					 * 根据关联实体${foreignClass}的外键${columnStr}进行查询
					 *
					 ${paramsStr}
					 * @param orderCommand
					 * @param pageCommand
					 * 
					 * @return ${className}实体对象集合
					 */
					public List<${className}> get${className}ListBy${methodName}(${args},OrderCommand orderCommand,PageCommand pageCommand);
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
				<#if (column_index>0)>
					<#assign methodName=methodName+"And"/>
					<#assign args=args+","/>
					<#assign columnStr=columnStr+","/>
				</#if>
				<#assign fieldName=columnNameToFieldName(column.name)/>
				<#assign methodName=methodName+fieldName?cap_first/>
				<#assign args=args+columnTypeToFieldType(column.type,column.typeName)+" "+fieldName/>
				<#assign columnStr=columnStr+fieldName/>
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
				public ${className} get${className}By${methodName}(${args});
			</#if>
		<#else>
			<#assign methodName=""/>
			<#assign args=""/>
			<#assign columnStr=""/>
			<#assign paramsStr=""/>
			<#list index.columns as column>
				<#assign fieldName=columnNameToFieldName(column.name)/>
				<#assign methodName=methodName+fieldName?cap_first/>
				<#assign args=args+columnTypeToFieldType(column.type,column.typeName)+" "+fieldName/>
				<#assign columnStr=columnStr+fieldName/>
				<#assign paramsStr=paramsStr+"\n"/>
				<#if column_has_next>
					<#assign methodName=methodName+"And"/>
					<#assign args=args+","/>
					<#assign columnStr=columnStr+","/>
					<#assign paramsStr=paramsStr+"* @param "+fieldName/>
				</#if>
			</#list>
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
				public List<${className}> get${className}ListBy${methodName}(${args});
				
				/**
				 * 根据索引字段${columnStr}进行查询
				 *
				 ${paramsStr}
				 *
				 * @return ${className}实体对象条目数
				 */
				public long get${className}CountBy${methodName}(${args});
				
				/**
				 * 根据索引字段${columnStr}进行查询
				 *
				 ${paramsStr}
				 * @param pageCommand
				 * 
				 * @return ${className}实体对象集合
				 */
				public List<${className}> get${className}ListBy${methodName}(${args},PageCommand pageCommand);
				
				/**
				 * 根据索引字段${columnStr}进行查询
				 *
				 ${paramsStr}
				 * @param orderCommand
				 * 
				 * @return ${className}实体对象集合
				 */
				public List<${className}> get${className}ListBy${methodName}(${args},OrderCommand orderCommand);
				
				/**
				 * 根据索引字段${columnStr}进行查询
				 *
				 ${paramsStr}
				 * @param orderCommand
				 * @param pageCommand
				 * 
				 * @return ${className}实体对象集合
				 */
				public List<${className}> get${className}ListBy${methodName}(${args},OrderCommand orderCommand,PageCommand pageCommand);
			</#if>
		</#if>
	</#list>
}
