package org.developerworld.codegenerator.freemarker.method;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

/**
 * 根据外键字段转换为对象名
 * @author Roy Huang
 * @version 20130611
 *
 */
public class ForeignColumnNameToFieldObjectName implements TemplateMethodModelEx {

	public Object exec(List arguments) throws TemplateModelException {
		if (arguments.size() < 1)
			throw new TemplateModelException("ill argument!");
		String rst = "";
		String columnName=arguments.get(0).toString();
		if(arguments.size()>1 && arguments.get(1)!=null){
			//判除外键字段信息部分
			String foreignColumnName=arguments.get(1).toString();
			if(StringUtils.isNotBlank(foreignColumnName))
				columnName=columnName.replaceAll(foreignColumnName, "");
		}
		String[] words=columnName.split("_");
		// 转换成驼峰式写法
		for (String word : words) {
			if (StringUtils.isNotBlank(word)) {
				word = word.trim();
				rst += word.substring(0, 1).toUpperCase();
				if (word.length() > 1)
					rst += word.substring(1).toLowerCase();
			}
		}
		// 把第一个字母变成小写
		if (rst.length() > 0)
			rst = rst.substring(0, 1).toLowerCase() + rst.substring(1);
		return rst;
	}
}
