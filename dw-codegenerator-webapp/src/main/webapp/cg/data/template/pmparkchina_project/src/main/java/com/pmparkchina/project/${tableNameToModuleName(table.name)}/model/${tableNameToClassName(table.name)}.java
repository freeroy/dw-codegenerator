<#include "/ftl_inc/inc_assign.ftl"/>
package ${basePackage}.${projectName}.${moduleName}.model;

import java.util.*;

import javax.persistence.*;

/**
 * ${tableName}表实体类
 * 
 * @author ${author}
 * @version ${version}
 * 
 */
@Entity(name="${basePackage}.${projectName}.${moduleName}.model.${className}")
@Table(name = "${tableName}"
<#assign uniqueIndexCount=0/>
<#list table.indexs as index>
	<#if index.unique==true>
		<#assign uniqueIndexCount=uniqueIndexCount+1/>
	</#if>
</#list>
<#-- 若存在唯一索引则执行 -->
<#if (uniqueIndexCount>0)>
	<#assign setIniqueIndexCount=0/>
	,uniqueConstraints={
		<#list table.indexs as index>
			<#if index.unique==true>
				@UniqueConstraint(columnNames={
					<#list index.columns as column>
						"${column.name}"
						<#if column_has_next>
						,
						</#if>
					</#list>
				})
				<#assign setIniqueIndexCount=setIniqueIndexCount+1/>
				<#if setIniqueIndexCount<uniqueIndexCount>
				,
				</#if>
			</#if>
		</#list>
	}
</#if>
)
<#--复合主键配置-->
<#if (table.primaryKey?? && table.primaryKey.columns?size>1)>
@IdClass(value=${className}.${className}Pk.class)
</#if>
public class ${className} implements java.io.Serializable {

	<#--设置主键信息-->
	<#if table.primaryKey??>
		<#--判断是否与别的表共享主键，即当前对象的主键，也是其他表的外键-->
		<#list table.foreignKeys as foreignKey>
			<#assign isSharePk=true/>
			<#--无主键，就肯定与主键不一致了-->
			<#list table.primaryKey.columns as pkColumn>
				<#assign hasSame=false/>
				<#list foreignKey.columns as fkColumn>
					<#if pkColumn.name==fkColumn.name>
						<#assign hasSame=true/>
						<#break/>
					</#if>
				</#list>
				<#if !hasSame>
					<#assign isSharePk=false/>
					<#break/>
				</#if>
			</#list>
			<#--确定是共享主键-->
			<#if isSharePk>
			@GeneratedValue(generator="foreignKey", strategy=GenerationType.AUTO) 
		    @org.hibernate.annotations.GenericGenerator(name="foreignKey", strategy="foreign", parameters=@org.hibernate.annotations.Parameter(name="property", value="${tableNameToClassName(foreignKey.primaryTable.name)?uncap_first}"))  
			<#break/>
			</#if>
		</#list>
		<#--若主键是复合主键，需要创建主键内部类-->
		<#if (table.primaryKey.columns?size>1)>
			
			@Embeddable
			public static class ${className}Pk implements java.io.Serializable{
				
				<#--定义主键组成的字段-->
				<#list table.primaryKey.columns as column>
					@Column(name="${column.name}",nullable=${column.nullable?string}
					<#if (column.length>0)>
						,length=${column.length}
					</#if>
					)
					<#if columnTypeToFieldType(column.type,column.typeName)=="java.util.Date">
						@Temporal(
								<#switch column.type>
									<#case 92>
										TemporalType.TIME
										<#break>
									<#case 91>
										TemporalType.DATE
										<#break>
									<#default>
										TemporalType.TIMESTAMP
								</#switch>
						)
					</#if>
					private ${columnTypeToFieldType(column.type,column.typeName)} ${columnNameToFieldName(column.name)};
					
				</#list>
				
				<#--定义字段get/set方法-->
				<#list table.primaryKey.columns as column>
					<#assign fieldName=columnNameToFieldName(column.name)/>
					<#assign fieldType=columnTypeToFieldType(column.type,column.typeName)/>
					public void set${fieldName?cap_first}(${fieldType+" "+fieldName}){
						this.${fieldName}=${fieldName};
					}
					
					<#if fieldType=="boolean" || fieldType=="Boolean">
						public ${fieldType} is${fieldName?cap_first}(){
							return this.${fieldName};
						}
					<#else>
						public ${fieldType} get${fieldName?cap_first}(){
							return this.${fieldName};
						}
					</#if>
					
				</#list>

				<#--设定hashCode函数-->
				@Override
				public int hashCode() {
					final int prime = 31;
					int result = 1;
					<#list table.primaryKey.columns as column>
						<#assign fieldName=columnNameToFieldName(column.name)/>
						result = prime * result+ ((${fieldName} == null) ? 0 : ${fieldName}.hashCode());
					</#list>
					return result;
				}
				
				<#--设定equals函数-->
				@Override
				public boolean equals(Object obj) {
					if (this == obj)
						return true;
					if (obj == null)
						return false;
					if (getClass() != obj.getClass())
						return false;
					${className}Pk other = (${className}Pk) obj;
					<#list table.primaryKey.columns as column>
						<#assign fieldName=columnNameToFieldName(column.name)/>
						if (${fieldName} == null) {
							if (other.${fieldName} != null)
								return false;
						} else if (!${fieldName}.equals(other.${fieldName}))
							return false;
					</#list>
					return true;
				}

				<#--设定toString函数-->
				@Override
				public String toString() {
					return "${className}Pk ["
							<#list table.primaryKey.columns as column>
								<#assign fieldName=columnNameToFieldName(column.name)/>
								+"${fieldName}=" + ${fieldName}
								<#if column_has_next>
									+","
								</#if>
							</#list>
							+"]";
				}

			}
			<#--设定主键对象-->
			@EmbeddedId
			private ${className}Pk ${objectName}Pk;
			
		<#--若主键是普通主键-->
		<#else>
			<#assign column=table.primaryKey.columns[0]>
			@Id
			@Column(name = "${column.name}", unique = true, nullable = ${column.nullable?string})
			<#--若是自动生成键-->
			<#if column.autoIncrement>
				@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
				@SequenceGenerator(name = "generator", sequenceName = "S_${tableName}")
			</#if>
			private ${columnTypeToFieldType(column.type,column.typeName)} ${columnNameToFieldName(column.name)};
			
		</#if>
	</#if>

	<#--定义普通字段-->
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
			<#assign fieldType=columnTypeToFieldType(column.type,column.typeName)/>
			@Column(name="${column.name}",nullable=${column.nullable?string}
			<#if (column.length>0)>
				,length=${column.length}
			</#if>
			<#if column.type==2004>
				, columnDefinition = "BLOB"
			</#if>
			<#if column.type==2005>
				, columnDefinition = "CLOB"
			</#if>
			)
			<#if fieldType=="java.util.Date">
				@Temporal(
				<#switch column.type>
					<#case 92>
						TemporalType.TIME
						<#break>
					<#case 91>
						TemporalType.DATE
						<#break>
					<#default>
						TemporalType.TIMESTAMP
				</#switch>
				)
			</#if>
			<#if column.type==2004 || column.type==2005>
				@Lob
				//@Basic(fetch = FetchType.LAZY)
			</#if>
			private ${columnTypeToFieldType(column.type,column.typeName)} ${columnNameToFieldName(column.name)};
			
		</#if>
	</#list>
	
	<#--定义关联字段 多对一、一对一-->
	<#list table.foreignKeys as foreignKey>
		<#--判断是一对一、还是多对一，-->
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
			@OneToOne(fetch=FetchType.LAZY)
			<#--复合主键关联-->
			<#if (foreignKey.columns?size>1)>
				@PrimaryKeyJoinColumns({
					<#list foreignKey.columns as column>
						@PrimaryKeyJoinColumn(name="${column.name}",referencedColumnName="${foreignKey.primaryColumns[column_index].name}")
						<#if column_has_next>
						,
						</#if>
					</#list>
				})
			<#--单一主键关联-->
			<#elseif (foreignKey.columns?size==1)>
				@PrimaryKeyJoinColumn(name = "${foreignKey.columns[0].name}",referencedColumnName="${foreignKey.primaryColumns[0].name}")
			</#if>
		<#--多对一情况-->
		<#else>
			@ManyToOne(fetch=FetchType.LAZY)
			<#--复合主键关联-->
			<#if (foreignKey.columns?size>1)>
				@JoinColumns({
					<#list foreignKey.columns as column>
						@JoinColumn(name="${column.name}",referencedColumnName="${foreignKey.primaryColumns[column_index].name}",nullable=${column.nullable?string})
						<#if column_has_next>
						,
						</#if>
					</#list>
				})
			<#--单一主键关联-->
			<#elseif (foreignKey.columns?size==1)>
				@JoinColumn(name = "${foreignKey.columns[0].name}",referencedColumnName="${foreignKey.primaryColumns[0].name}", nullable = ${foreignKey.columns[0].nullable?string})
			</#if>
		</#if>
		<#assign foreignModuleName=tableNameToModuleName(foreignKey.primaryTable.name)/>
		<#assign foreignPackage=""/>
		<#if foreignModuleName!=moduleName>
			<#assign foreignPackage=basePackage+"."+projectName+"."+foreignModuleName+".model."/>
		</#if>
		private ${foreignPackage+tableNameToClassName(foreignKey.primaryTable.name)} ${foreignColumnNameToFieldObjectName(foreignKey.columns[0].name,foreignKey.primaryTable.columns[0].name)};
	</#list>
	
	<#--定义一对一、一对多-->
	<#if (table.primaryKey?? && table.primaryKey.foreignTables?size>0)>
		<#list table.primaryKey.foreignTables as foreignTable>
			<#assign foreignColumns=table.primaryKey.foreignColumns[foreignTable_index]/>
			<#--若外键等于该表的主键，代表是一对一-->
			<#assign isPrimaryKeyAndForeignKeySame=true/>
			<#--若外表无主键，则不用判断-->
			<#if (foreignTable.primaryKey?? && foreignTable.primaryKey.columns?size>0)>
				<#list foreignTable.primaryKey.columns as foreignColumn>
					<#assign hasSame=false/>
					<#list foreignColumns as foreignColumn2>
						<#if foreignColumn.name==foreignColumn2.name>
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
			<#assign foreignClassName=tableNameToClassName(foreignTable.name)/>
			<#assign foreignObjectName=foreignColumnNameToFieldObjectName(table.primaryKey.foreignColumns[foreignTable_index][0].name,table.primaryKey.columns[0].name)/>
			<#assign foreignModuleName=tableNameToModuleName(foreignTable.name)/>
			<#assign foreignPackage=""/>
			<#if foreignModuleName!=moduleName>
				<#assign foreignPackage=basePackage+"."+projectName+"."+foreignModuleName+".model."/>
			</#if>
			<#--一对一-->
			<#if isPrimaryKeyAndForeignKeySame>
				@OneToOne(fetch=FetchType.LAZY,cascade=CascadeType.REMOVE,mappedBy="${foreignObjectName}")
				@PrimaryKeyJoinColumn
				private ${foreignPackage+foreignClassName} ${foreignClassName?uncap_first};
				
			<#--一对多-->
			<#else>
				@OneToMany(mappedBy = "${foreignObjectName}", cascade = CascadeType.REMOVE)
				private Set<${foreignPackage+foreignClassName}> ${foreignClassName?uncap_first}s = new HashSet<${foreignPackage+foreignClassName}>();
				
			</#if>
		</#list>
	</#if>
	
	<#--生成主键get/set-->
	<#if table.primaryKey??>
		<#--若主键是复合主键，需要创建主键内部类-->
		<#if (table.primaryKey.columns?size>1)>
			public void set${className}Pk(${className}Pk ${objectName}Pk){
				this.${objectName}Pk=${objectName}Pk;
			}
			
			public ${className}Pk get${className}Pk(){
				return this.${objectName}Pk;
			}
			
		<#else>
			<#assign column=table.primaryKey.columns[0]/>
			<#assign fieldName=columnNameToFieldName(column.name)/>
			<#assign fieldType=columnTypeToFieldType(column.type,column.typeName)/>
			public void set${fieldName?cap_first}(${fieldType} ${fieldName}){
				this.${fieldName}=${fieldName};
			}
			
			public ${fieldType} get${fieldName?cap_first}(){
				return this.${fieldName};
			}
			
		</#if>
	</#if>
	
	<#--普通字段get/set-->
	<#list table.columns as column>
		<#assign fieldName=columnNameToFieldName(column.name)/>
		<#assign fieldType=columnTypeToFieldType(column.type,column.typeName)/>
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
			public void set${fieldName?cap_first}(${fieldType+" "+fieldName}){
				this.${fieldName}=${fieldName};
			}
			
			<#if fieldType=="boolean" || fieldType=="Boolean">
				public ${fieldType} is${fieldName?cap_first}(){
					return this.${fieldName};
				}
			<#else>
				public ${fieldType} get${fieldName?cap_first}(){
					return this.${fieldName};
				}
			</#if>
			
		</#if>
	</#list>
	
	<#--多对一/一对一字段get/set-->
	<#list table.foreignKeys as foreignKey>
		<#assign foreignClassName=tableNameToClassName(foreignKey.primaryTable.name)/>
		<#assign foreignObjectName=foreignColumnNameToFieldObjectName(foreignKey.columns[0].name,foreignKey.primaryTable.columns[0].name)/>
		<#assign foreignModuleName=tableNameToModuleName(foreignKey.primaryTable.name)/>
		<#assign foreignPackage=""/>
		<#if foreignModuleName!=moduleName>
			<#assign foreignPackage=basePackage+"."+projectName+"."+foreignModuleName+".model."/>
		</#if>
		public void set${foreignObjectName?cap_first}(${foreignPackage+foreignClassName} ${foreignObjectName}){
			this.${foreignObjectName}=${foreignObjectName};
		}
		
		public ${foreignPackage+foreignClassName} get${foreignObjectName?cap_first}(){
			return this.${foreignObjectName};
		}
		
	</#list>
	
	<#--一对多/一对一字段get/set-->
	<#if table.primaryKey??>
		<#list table.primaryKey.foreignTables as foreignTable>
			<#assign foreignColumns=table.primaryKey.foreignColumns[foreignTable_index]/>
			<#--若外键等于该表的主键，代表是一对一-->
			<#assign isPrimaryKeyAndForeignKeySame=true/>
			<#--若外表无主键，则不用判断-->
			<#if (foreignTable.primaryKey?? && foreignTable.primaryKey.columns?size>0)>
				<#list foreignTable.primaryKey.columns as foreignColumn>
					<#assign hasSame=false/>
					<#list foreignColumns as foreignColumn2>
						<#if foreignColumn.name==foreignColumn2.name>
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
			<#assign foreignClassName=tableNameToClassName(foreignTable.name)/>
			<#assign foreignObjectName=foreignClassName?uncap_first/>
			<#assign foreignModuleName=tableNameToModuleName(foreignTable.name)/>
			<#assign foreignPackage=""/>
			<#if foreignModuleName!=moduleName>
				<#assign foreignPackage=basePackage+"."+projectName+"."+foreignModuleName+".model."/>
			</#if>
			<#--一对一-->
			<#if isPrimaryKeyAndForeignKeySame>
				public void set${foreignClassName}(${foreignPackage+foreignClassName} ${foreignObjectName}){
					this.${foreignObjectName}=${foreignObjectName};
				}
				
				public ${foreignPackage+foreignClassName} get${foreignClassName}(){
					return this.${foreignObjectName};
				}
			<#--一对多-->
			<#else>
				<#-- 不做一对多的get/set
				public void set${foreignClassName}s(Set<${foreignPackage+foreignClassName}> ${foreignObjectName}s){
					this.${foreignObjectName}s=${foreignObjectName}s;
				}
				
				public Set<${foreignPackage+foreignClassName}> get${foreignClassName}s(){
					return this.${foreignObjectName}s;
				}
				-->
			</#if>
			
		</#list>
	</#if>
	
	<#--设定hashCode函数-->
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		<#--先设置主键信息-->
		<#if table.primaryKey??>
			<#--复合主键-->
			<#if (table.primaryKey.columns?size>1)>
				result = prime * result + ${objectName}Pk.hashCode();
			<#else>
				result = prime * result + ${columnNameToFieldName(table.primaryKey.columns[0].name)}.hashCode();
			</#if>
		</#if>
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
			<#if !isFkColumn && !isPkColumn>
				<#assign fieldName=columnNameToFieldName(column.name)/>
				result = prime * result+ ((${fieldName} == null) ? 0 : ${fieldName}.hashCode());
			</#if>
		</#list>
		return result;
	}
	
	<#--设定equals函数-->
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		${className} other = (${className}) obj;
		<#--先设置主键信息-->
		<#if table.primaryKey??>
			<#--复合主键-->
			<#if (table.primaryKey.columns?size>1)>
				if (${objectName}Pk == null) {
					if (other.${objectName}Pk != null)
						return false;
				} else if (!${objectName}Pk.equals(other.${objectName}Pk))
					return false;
			<#else>
				<#assign fieldName=columnNameToFieldName(table.primaryKey.columns[0].name)/>
				if (${fieldName} == null) {
					if (other.${fieldName} != null)
						return false;
				} else if (!${fieldName}.equals(other.${fieldName}))
					return false;
			</#if>
		</#if>
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
			<#if !isFkColumn && !isPkColumn>
				<#assign fieldName=columnNameToFieldName(column.name)/>
				if (${fieldName} == null) {
					if (other.${fieldName} != null)
						return false;
				} else if (!${fieldName}.equals(other.${fieldName}))
					return false;
			</#if>
		</#list>
		return true;
	}

	<#--设定toString函数-->
	@Override
	public String toString() {
		return "${className} ["
				<#--先设置主键信息-->
				<#if table.primaryKey??>
					<#--复合主键-->
					<#if (table.primaryKey.columns?size>1)>
						+"${objectName}Pk=" + ${objectName}Pk+","
					<#else>
						<#assign fieldName=columnNameToFieldName(table.primaryKey.columns[0].name)/>
						+"${fieldName}=" + ${fieldName}+","
					</#if>
				</#if>
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
					<#if !isFkColumn && !isPkColumn>
						<#assign fieldName=columnNameToFieldName(column.name)/>
						+"${fieldName}=" + ${fieldName}
						<#if column_has_next>
							+","
						</#if>
					</#if>
				</#list>
				+"]";
	}
	
}
