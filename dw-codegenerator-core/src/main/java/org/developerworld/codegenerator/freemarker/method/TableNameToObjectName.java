package org.developerworld.codegenerator.freemarker.method;

import java.util.List;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

/**
 * 数据库表名转类名
 * @author Roy Huang
 * @version 20130605
 *
 */
public class TableNameToObjectName implements TemplateMethodModelEx{
	
	public Object exec(List arguments) throws TemplateModelException {
		String rst=(String) new TableNameToClassName().exec(arguments);
		// 把第一个字母变成小写
		if (rst.length() > 0)
			rst = rst.substring(0, 1).toLowerCase() + rst.substring(1);
		return rst;
	}
}
