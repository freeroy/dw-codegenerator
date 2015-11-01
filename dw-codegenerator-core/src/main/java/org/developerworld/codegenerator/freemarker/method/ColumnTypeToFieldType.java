package org.developerworld.codegenerator.freemarker.method;

import java.sql.Types;
import java.util.List;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

/**
 * 字段类型转成员变量类型
 * 
 * @author Roy Huang
 * @version 20130607
 * 
 */
public class ColumnTypeToFieldType implements TemplateMethodModelEx {

	private final static int NCHAR = -15;
	private final static int NVARCHAR = -9;
	private final static int LONGNVARCHAR = -16;
	private final static int NCLOB = 2011;
	private final static int ROWID = -8;
//	private final static int OTHER = 1111;

	public Object exec(List arguments) throws TemplateModelException {
		if (arguments.size() < 1)
			throw new TemplateModelException("ill argument!");
		int type = Integer.parseInt(arguments.get(0).toString());
		String typeName = null;
		if (arguments.size() > 1)
			typeName = arguments.get(1).toString();
		String rst = null;
		switch (type) {
		case Types.BIT:
		case Types.BOOLEAN:
			rst = "Boolean";
			break;
		case Types.BLOB:
		case Types.BINARY:
		case Types.LONGVARBINARY:
		case Types.VARBINARY:
			rst = "Byte[]";
			break;
		case Types.TINYINT:
		case Types.SMALLINT:
			rst = "Short";
			break;
		case Types.INTEGER:
			rst = "Integer";
			break;
		case Types.BIGINT:
			rst = "Long";
			break;
		case Types.FLOAT:
		case Types.REAL:
			rst = "Float";
			break;
		case Types.DOUBLE:
			rst = "Double";
			break;
		case Types.CHAR:
		case Types.VARCHAR:
		case NCHAR:
		case NVARCHAR:
		case LONGNVARCHAR:
		case Types.CLOB:
		case NCLOB:
		case ROWID:
			rst = "String";
			break;
		case Types.TIME:
		case Types.DATE:
		case Types.TIMESTAMP:
			rst = "java.util.Date";
			break;
		case Types.NUMERIC:
		case Types.DECIMAL:
			rst = "java.math.BigDecimal";
			break;
		default:
			rst = "Object";
		}
		if (rst.equals("Object") && typeName != null)
			rst = findByTypeName(typeName);
		return rst;
	}

	private String findByTypeName(String typeName) {
		String rst = "Object";
		typeName = typeName.toLowerCase();
		if (typeName.equals("varchar2") || typeName.equals("nvarchar2")
				|| typeName.equals("nrowid") || typeName.equals("graphics")
				|| typeName.equals("vargraphics")
				|| typeName.equals("long vargraphic")
				|| typeName.equals("text") || typeName.equals("ntext")
				|| typeName.equals("tinytext") || typeName.equals("mediumtext")
				|| typeName.equals("longtext")
				|| typeName.equals("uniqueidentifier"))
			rst = "String";
		else if (typeName.equals("mediumint"))
			rst = "Integer";
		else if (typeName.equals("year"))
			rst = "Short";
		else if (typeName.equals("raw") || typeName.equals("bfile")
				|| typeName.equals("image") || typeName.equals("tinyblob")
				|| typeName.equals("mediumblob") || typeName.equals("longblob"))
			rst = "Byte[]";
		else if (typeName.equals("real") || typeName.equals("smallmoney")
				|| typeName.equals("money")
				|| typeName.equals("double precision"))
			rst = "Double";
		return rst;
	}
}
