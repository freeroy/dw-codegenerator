package org.developerworld.codegenerator.freemarker.method;

import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

/**
 * 数据库表名转类名
 * 
 * @author Roy Huang
 * @version 20130605
 * 
 */
public class TableNameToClassName implements TemplateMethodModelEx {

	public Object exec(List arguments) throws TemplateModelException {
		if (arguments.size() < 1)
			throw new TemplateModelException("ill argument!");
		String rst = "";
		String[] words = arguments.get(0).toString().split("_");
		// 去除第一个分隔符之前的，因为那个默认代表的是模块号
		if (words.length > 1)
			words = (String[]) ArrayUtils.subarray(words, 1, words.length);
		for (String word : words) {
			if (StringUtils.isNotBlank(word)) {
				word = word.trim();
				rst += word.substring(0, 1).toUpperCase();
				if (word.length() > 1)
					rst += word.substring(1).toLowerCase();
			}
		}
		return rst;
	}
}
