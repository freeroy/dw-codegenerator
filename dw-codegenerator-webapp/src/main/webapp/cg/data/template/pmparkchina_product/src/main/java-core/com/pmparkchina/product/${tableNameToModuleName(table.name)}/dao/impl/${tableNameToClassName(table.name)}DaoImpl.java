<#include "/ftl_inc/inc_assign.ftl"/>
package ${basePackage}.${projectName}.${moduleName}.dao.impl;

import java.util.*;

import org.developerworld.commons.command.OrderCommand;
import org.developerworld.commons.command.PageCommand;
import org.springframework.stereotype.Repository;

import ${basePackage}.${projectName}.${moduleName}.dao.${className}Dao;
import ${basePackage}.${projectName}.${moduleName}.model.${className};

/**
 * ${className}实体DAO实现
 * 
 * @author ${author}
 * @version ${version}
 * 
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
@Repository("${moduleName}${className}Dao")
public class ${className}DaoImpl extends AbstractBaseDaoImpl<${className}, ${pkType}> implements ${className}Dao {
	
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
			<#list foreignKey.columns as column>
				<#assign fieldName=columnNameToFieldName(column.name)/>
				<#assign methodName=methodName+fieldName?cap_first/>
				<#assign args=args+columnTypeToFieldType(column.type,column.typeName)+" "+fieldName/>
				<#if column_has_next>
					<#assign methodName=methodName+"And"/>
					<#assign args=args+","/>
				</#if>
			</#list>
			<#--判断方法是否已经存在-->
			<#if methodsStr?index_of(","+methodName+",")==-1>
				<#assign methodsStr=methodsStr+methodName+","/>
				public ${className} findBy${methodName}(${args}){
					String jpql=" from "+getEntityClassName()+" ${objectName} ";
					String where="";
					Map<String, Object> params = new HashMap<String, Object>();
					<#list foreignKey.columns as column>
						<#assign fieldName=columnNameToFieldName(column.name)/>
						if(${fieldName}!=null){
							where+=" ${objectName}.${foreignColumnNameToFieldObjectName(column.name,foreignKey.primaryColumns[column_index].name)}.${columnNameToFieldName(foreignKey.primaryColumns[column_index].name)}=:${fieldName} and ";
							params.put("${fieldName}",${fieldName});
						}
						else
							where+=" ${objectName}.${foreignColumnNameToFieldObjectName(column.name,foreignKey.primaryColumns[column_index].name)}.${columnNameToFieldName(foreignKey.primaryColumns[column_index].name)} is null and ";
					</#list>
					if(where.length()>0){
						where=where.substring(0,where.lastIndexOf(" and "));
						jpql+=" where "+where;
					}
					return getDwJpaTemplate().findSingleByNamedParams(jpql, params);
				}
			</#if>
		<#--多对一情况-->
		<#else>
			<#assign methodName=""/>
			<#assign args=""/>
			<#list foreignKey.columns as foreignColumn>
				<#if (foreignColumn_index!=0)>
					<#assign methodName=methodName+"And"/>
					<#assign args=args+","/>
				</#if>
				<#assign fieldName=columnNameToFieldName(foreignColumn.name)/>
				<#assign methodName=methodName+fieldName?cap_first/>
				<#assign args=args+columnTypeToFieldType(foreignColumn.type,foreignColumn.typeName)+" "+fieldName/>
				<#--判断方法是否已经存在-->
				<#if methodsStr?index_of(","+methodName+",")==-1>
					<#assign methodsStr=methodsStr+methodName+","/>
					public List<${className}> findListBy${methodName}(${args}){
						return findListBy${methodName}(
						<#list foreignKey.columns as column>
							<#if (column_index>foreignColumn_index)>
								<#break/>
							</#if>
							${columnNameToFieldName(column.name)},
						</#list>
						null,null);
					}
					
					public long findCountBy${methodName}(${args}){
						String jpql=" select count(*) from "+getEntityClassName()+" ${objectName} ";
						String where="";
						Map<String, Object> params = new HashMap<String, Object>();
						<#list foreignKey.columns as column>
							<#if (column_index>foreignColumn_index)>
								<#break/>
							</#if>
							<#assign fieldName=columnNameToFieldName(column.name)/>
							<#assign fieldType=columnTypeToFieldType(column.type,column.typeName)/>
							if(${fieldName}!=null){
								where+=" ${objectName}.${foreignColumnNameToFieldObjectName(column.name,foreignKey.primaryColumns[column_index].name)}.${columnNameToFieldName(foreignKey.primaryColumns[column_index].name)}=:${fieldName} and ";
								params.put("${fieldName}",${fieldName});
							}
							else
								where+=" ${objectName}.${foreignColumnNameToFieldObjectName(column.name,foreignKey.primaryColumns[column_index].name)}.${columnNameToFieldName(foreignKey.primaryColumns[column_index].name)} is null and ";
						</#list>
						if(where.length()>0){
							where=where.substring(0,where.lastIndexOf(" and "));
							jpql+=" where "+where;
						}
						return getDwJpaTemplate().findLong(jpql, params);
					}
					
					public List<${className}> findListBy${methodName}(${args},PageCommand pageCommand){
						return findListBy${methodName}(
							<#list foreignKey.columns as column>
								<#if (column_index>foreignColumn_index)>
									<#break/>
								</#if>
								${columnNameToFieldName(column.name)},
							</#list>
							null,pageCommand);
					}
			
					public List<${className}> findListBy${methodName}(${args},OrderCommand orderCommand){
						return findListBy${methodName}(
								<#list foreignKey.columns as column>
									<#if (column_index>foreignColumn_index)>
										<#break/>
									</#if>
									${columnNameToFieldName(column.name)},
								</#list>
								orderCommand,null);
					}
		
					public List<${className}> findListBy${methodName}(${args},OrderCommand orderCommand,PageCommand pageCommand){
						String jpql=" from "+getEntityClassName()+" ${objectName} ";
						String where="";
						Map<String, Object> params = new HashMap<String, Object>();
						<#list foreignKey.columns as column>
							<#if (column_index>foreignColumn_index)>
								<#break/>
							</#if>
							<#assign fieldName=columnNameToFieldName(column.name)/>
							<#assign fieldType=columnTypeToFieldType(column.type,column.typeName)/>
							if(${fieldName}!=null){
								where+=" ${objectName}.${foreignColumnNameToFieldObjectName(column.name,foreignKey.primaryColumns[column_index].name)}.${columnNameToFieldName(foreignKey.primaryColumns[column_index].name)}=:${fieldName} and ";
								params.put("${fieldName}",${fieldName});
							}
							else
								where+=" ${objectName}.${foreignColumnNameToFieldObjectName(column.name,foreignKey.primaryColumns[column_index].name)}.${columnNameToFieldName(foreignKey.primaryColumns[column_index].name)} is null and ";
						</#list>
						if(where.length()>0){
							where=where.substring(0,where.lastIndexOf(" and "));
							jpql+=" where "+where;
						}
						if (orderCommand != null && orderCommand.size() > 0)
							jpql += " order by " + orderCommand.toSqlOrderString("${objectName}");
						if (pageCommand != null)
							return getDwJpaTemplate().findByNamedParams(jpql, params,
									pageCommand.getStartIndex(), pageCommand.getPageSize());
						else
							return getDwJpaTemplate().findByNamedParams(jpql, params);
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
			<#list index.columns as column>
				<#assign fieldName=columnNameToFieldName(column.name)/>
				<#assign methodName=methodName+fieldName?cap_first/>
				<#assign args=args+columnTypeToFieldType(column.type,column.typeName)+" "+fieldName/>
				<#if column_has_next>
					<#assign methodName=methodName+"And"/>
					<#assign args=args+","/>
				</#if>
			</#list>
			<#--判断方法是否已经存在-->
			<#if methodsStr?index_of(","+methodName+",")==-1>
				<#assign methodsStr=methodsStr+methodName+","/>
				public ${className} findBy${methodName}(${args}){
					String jpql="from "+getEntityClassName()+" ${objectName} ";
					String where="";
					Map<String, Object> params = new HashMap<String, Object>();
					<#list index.columns as column>
						<#--判断该字段是否为外表字段-->
						<#assign foreignColumnName=""/>
						<#list table.foreignKeys as foreignKey>
							<#list foreignKey.columns as fkColumn>
								<#if column.name==fkColumn.name>
									<#assign foreignColumnName=foreignKey.primaryColumns[fkColumn_index].name/>
									<#break/>
								</#if>
							</#list>
							<#if foreignColumnName!="">
								<#break/>
							</#if>
						</#list>
						<#assign fieldName=columnNameToFieldName(column.name)/>
						<#assign fieldType=columnTypeToFieldType(column.type,column.typeName)/>
						<#--是外键-->
						<#if foreignColumnName!="">
							<#assign foreignFieldName=columnNameToFieldName(foreignColumnName)/>
							if(${fieldName}!=null){
								where+=" ${objectName}.${foreignColumnNameToFieldObjectName(column.name,foreignColumnName)}.${foreignFieldName}=:${fieldName} and ";
								params.put("${fieldName}",${fieldName});
							}
							else
								where+=" ${objectName}.${foreignColumnNameToFieldObjectName(column.name,foreignColumnName)}.${foreignFieldName} is null and ";
						<#--不是外键-->
						<#else>
							if(${fieldName}!=null){
								where+=" ${objectName}.${fieldName}=:${fieldName} and ";
								params.put("${fieldName}",${fieldName});
							}
							else
								where+=" ${objectName}.${fieldName} is null and ";
						</#if>
					</#list>
					if(where.length()>0){
						where=where.substring(0,where.lastIndexOf(" and "));
						jpql+=" where "+where;
					}
					return getDwJpaTemplate().findSingleByNamedParams(jpql, params);
				}
			</#if>
		<#else>
			<#assign methodName=""/>
			<#assign args=""/>
			<#list index.columns as indexColumn>
				<#if (indexColumn_index!=0)>
					<#assign methodName=methodName+"And"/>
					<#assign args=args+","/>
				</#if>
				<#assign fieldName=columnNameToFieldName(indexColumn.name)/>
				<#assign methodName=methodName+fieldName?cap_first/>
				<#assign args=args+columnTypeToFieldType(indexColumn.type,indexColumn.typeName)+" "+fieldName/>
				<#--判断方法是否已经存在-->
				<#if methodsStr?index_of(","+methodName+",")==-1>
					<#assign methodsStr=methodsStr+methodName+","/>
					public List<${className}> findListBy${methodName}(${args}){
						return findListBy${methodName}(
								<#list index.columns as column>
									<#if (column_index>indexColumn_index)>
										<#break/>
									</#if>
									${columnNameToFieldName(column.name)},
								</#list>
								null,null);
					}
					
					public long findCountBy${methodName}(${args}){
						String jpql="select count(*) from "+getEntityClassName()+" ${objectName} ";
						String where="";
						Map<String, Object> params = new HashMap<String, Object>();
						<#list index.columns as column>
							<#if (column_index>indexColumn_index)>
								<#break/>
							</#if>
							<#--判断该字段是否为外表字段-->
							<#assign foreignColumnName=""/>
							<#list table.foreignKeys as foreignKey>
								<#list foreignKey.columns as fkColumn>
									<#if column.name==fkColumn.name>
										<#assign foreignColumnName=foreignKey.primaryColumns[fkColumn_index].name/>
										<#break/>
									</#if>
								</#list>
								<#if foreignColumnName!="">
									<#break/>
								</#if>
							</#list>
							<#assign fieldName=columnNameToFieldName(column.name)/>
							<#assign fieldType=columnTypeToFieldType(column.type,column.typeName)/>
							<#--是外键-->
							<#if foreignColumnName!="">
								<#assign foreignFieldName=columnNameToFieldName(foreignColumnName)/>
								if(${fieldName}!=null){
									where+=" ${objectName}.${foreignColumnNameToFieldObjectName(column.name,foreignColumnName)}.${foreignFieldName}=:${fieldName} and ";
									params.put("${fieldName}",${fieldName});
								}
								else
									where+=" ${objectName}.${foreignColumnNameToFieldObjectName(column.name,foreignColumnName)}.${foreignFieldName} is null and ";
							<#--不是外键-->
							<#else>
								if(${fieldName}!=null){
									where+=" ${objectName}.${fieldName}=:${fieldName} and ";
									params.put("${fieldName}",${fieldName});
								}
								else
									where+=" ${objectName}.${fieldName} is null and ";
							</#if>
						</#list>
						if(where.length()>0){
							where=where.substring(0,where.lastIndexOf(" and "));
							jpql+=" where "+where;
						}
						return getDwJpaTemplate().findLong(jpql, params);
					}
					
					public List<${className}> findListBy${methodName}(${args},OrderCommand orderCommand){
						return findListBy${methodName}(
								<#list index.columns as column>
									<#if (column_index>indexColumn_index)>
										<#break/>
									</#if>
									${columnNameToFieldName(column.name)},
								</#list>
								orderCommand,null);
					}
					
					public List<${className}> findListBy${methodName}(${args},PageCommand pageCommand){
						return findListBy${methodName}(
								<#list index.columns as column>
									<#if (column_index>indexColumn_index)>
										<#break/>
									</#if>	
									${columnNameToFieldName(column.name)},
								</#list>
								null,pageCommand);
					}
					
					public List<${className}> findListBy${methodName}(${args},OrderCommand orderCommand,PageCommand pageCommand){
						String jpql="from "+getEntityClassName()+" ${objectName} ";
						String where="";
						Map<String, Object> params = new HashMap<String, Object>();
						<#list index.columns as column>
							<#if (column_index>indexColumn_index)>
								<#break/>
							</#if>
							<#--判断该字段是否为外表字段-->
							<#assign foreignColumnName=""/>
							<#list table.foreignKeys as foreignKey>
								<#list foreignKey.columns as fkColumn>
									<#if column.name==fkColumn.name>
										<#assign foreignColumnName=foreignKey.primaryColumns[fkColumn_index].name/>
										<#break/>
									</#if>
								</#list>
								<#if foreignColumnName!="">
									<#break/>
								</#if>
							</#list>
							<#assign fieldName=columnNameToFieldName(column.name)/>
							<#assign fieldType=columnTypeToFieldType(column.type,column.typeName)/>
							<#--是外键-->
							<#if foreignColumnName!="">
								<#assign foreignFieldName=columnNameToFieldName(foreignColumnName)/>
								if(${fieldName}!=null){
									where+=" ${objectName}.${foreignColumnNameToFieldObjectName(column.name,foreignColumnName)}.${foreignFieldName}=:${fieldName} and ";
									params.put("${fieldName}",${fieldName});
								}
								else
									where+=" ${objectName}.${foreignColumnNameToFieldObjectName(column.name,foreignColumnName)}.${foreignFieldName} is null and ";
							<#--不是外键-->
							<#else>
								if(${fieldName}!=null){
									where+=" ${objectName}.${fieldName}=:${fieldName} and ";
									params.put("${fieldName}",${fieldName});
								}
								else
									where+=" ${objectName}.${fieldName} is null and ";
							</#if>
						</#list>
						if(where.length()>0){
							where=where.substring(0,where.lastIndexOf(" and "));
							jpql+=" where "+where;
						}
						if (orderCommand != null && orderCommand.size() > 0)
							jpql += " order by " + orderCommand.toSqlOrderString("${objectName}");
						if (pageCommand != null)
							return getDwJpaTemplate().findByNamedParams(jpql, params,
									pageCommand.getStartIndex(), pageCommand.getPageSize());
						else
							return getDwJpaTemplate().findByNamedParams(jpql, params);
					}
				</#if>
			</#list>
		</#if>
	</#list>
}
