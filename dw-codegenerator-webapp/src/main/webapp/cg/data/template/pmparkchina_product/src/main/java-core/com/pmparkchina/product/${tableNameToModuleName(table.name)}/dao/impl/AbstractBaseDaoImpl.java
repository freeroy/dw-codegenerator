<#include "/ftl_inc/inc_assign.ftl"/>
package ${basePackage}.${projectName}.${moduleName}.dao.impl;

import java.io.Serializable;
import org.developerworld.frameworks.spring.orm.jpa.AbstractJpaGenericDaoImpl;
import ${basePackage}.${projectName}.${moduleName}.dao.BaseDao;

/**
 * 抽象DAO基础类
 * 
 * @author ${author}
 * @version ${version}
 * 
 * @param <T>
 * @param <PK>
 */
public abstract class AbstractBaseDaoImpl<T, PK extends Serializable> extends
		AbstractJpaGenericDaoImpl<T, PK> implements BaseDao<T, PK> {

	
}
