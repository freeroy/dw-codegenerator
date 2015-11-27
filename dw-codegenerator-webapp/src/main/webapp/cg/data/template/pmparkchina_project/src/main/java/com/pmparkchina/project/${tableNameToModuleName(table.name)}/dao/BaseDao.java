<#include "/ftl_inc/inc_assign.ftl"/>
package ${basePackage}.${projectName}.${moduleName}.dao;

import java.io.Serializable;

import org.developerworld.commons.dbutils.crud.GenericDao;

/**
 * 公共DAO接口
 * 
 * @author ${author}
 * @version ${version}
 * 
 * @param <T>
 * @param <PK>
 */
public interface BaseDao<T, PK extends Serializable> extends GenericDao<T, PK> {

}
