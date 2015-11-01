package org.developerworld.codegenerator.freemarker.method;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

/**
 * 数据库表明转模块名
 * @author Roy Huang
 * @version 20130605
 *
 */
public class TableNameToModuleName implements TemplateMethodModelEx{
	
	public Object exec(List arguments) throws TemplateModelException {
		if(arguments.size()<1)
			throw new TemplateModelException("ill argument!");
		String[] words=arguments.get(0).toString().split("_");
		//去除第一个分隔符之前的，因为那个默认代表的是模块号
		if(words.length>0 && StringUtils.isNotBlank(words[0]))
			return words[0].toLowerCase();
		return "";
	}
}
