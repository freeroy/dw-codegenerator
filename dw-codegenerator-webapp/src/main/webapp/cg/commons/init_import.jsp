<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.Enumeration"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.Comparator"%>
<%@page import="java.util.Collections"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.HashSet"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="java.net.URL"%>
<%@page import="java.io.File"%>
<%@page import="java.io.FileFilter"%>
<%@page import="java.io.Writer"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.sql.DriverManager"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="org.apache.commons.io.FilenameUtils"%>
<%@page import="org.apache.commons.io.FileUtils"%>
<%@page import="org.developerworld.commons.compress.utils.CompressUtils"%>
<%@page import="org.developerworld.commons.dbutils.info.DBInfo"%>
<%@page import="org.developerworld.commons.dbutils.info.TablenameFilter"%>
<%@page import="org.developerworld.commons.dbutils.info.object.Table"%>
<%@page import="org.developerworld.codegenerator.Log"%>
<%@page import="org.developerworld.codegenerator.Generator"%>
<%@page import="org.developerworld.codegenerator.freemarker.method.ForeignColumnNameToFieldObjectName"%>
<%@page import="org.developerworld.codegenerator.freemarker.method.ColumnTypeToFieldType"%>
<%@page import="org.developerworld.codegenerator.freemarker.method.ColumnNameToFieldName"%>
<%@page import="org.developerworld.codegenerator.freemarker.method.TableNameToModuleName"%>
<%@page import="org.developerworld.codegenerator.freemarker.method.TableNameToObjectName"%>
<%@page import="org.developerworld.codegenerator.freemarker.method.TableNameToClassName"%>