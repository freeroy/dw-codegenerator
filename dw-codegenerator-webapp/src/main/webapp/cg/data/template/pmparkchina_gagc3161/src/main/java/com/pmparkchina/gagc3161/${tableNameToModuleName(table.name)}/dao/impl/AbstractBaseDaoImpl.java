<#include "/ftl_inc/inc_assign.ftl"/>
package com.pmparkchina.${projectName}.${moduleName}.dao.impl;

import java.io.Serializable;
import org.developerworld.frameworks.spring.orm.hibernate3.AbstractHibernateGenericDaoImpl;
import com.pmparkchina.${projectName}.${moduleName}.dao.BaseDao;

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
AbstractHibernateGenericDaoImpl<T, PK> implements BaseDao<T, PK> {

	
}
