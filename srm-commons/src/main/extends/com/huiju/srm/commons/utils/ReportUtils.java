package com.huiju.srm.commons.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXmlExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;

/**
 * Jasper报表工具类
 * 
 * @author chensw
 * 
 */
public abstract class ReportUtils {

	/**
	 * 把JasperPrint对象转换为XLS文件格式，并以OutputStream的方式导出
	 * @param jasperPrint JasperPrint导出对象
	 * @param outputStream OutputStream导出目标
	 * @throws JRException
	 */
	public static void exportReportToXlsStream (JasperPrint jasperPrint, OutputStream outputStream) throws JRException {
		JRXlsExporter exporter = new JRXlsExporter();
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
		exporter.exportReport();
	}
	
	/**
	 * 把JasperPrint对象转换为XLS文件格式，并以byte[]的方式导出
	 * @param jasperPrint JasperPrint导出对象
	 * @return byte[]
	 * @throws JRException
	 */
	public static byte[] exportReportToXls (JasperPrint jasperPrint) throws JRException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JRXlsExporter exporter = new JRXlsExporter();
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));
		exporter.exportReport();
		return baos.toByteArray();
	}
	
	/**
	 * 把JASPER文件导出为XLS文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @return 最后导出的XLS文件名
	 * @throws JRException
	 */
	public static String exportReportToXlsFile (String sourceFileName) throws JRException {
		Map<String, Object> params = new HashMap<String, Object>();
		return exportReportToXlsFile(sourceFileName, params);
	}
	
	/**
	 * 把JASPER文件导出为XLS文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param params 填充JASPER文件所需的参数值对
	 * @return 最后导出的XLS文件名
	 * @throws JRException
	 */
	public static String exportReportToXlsFile (String sourceFileName, Map<String, Object> params) throws JRException {
		JasperPrint jasperPrint = JasperFillManager.fillReport(sourceFileName, params);
		File sourceFile = new File(sourceFileName);
		File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".xls");
		String destFileName = destFile.toString();
		exportReportToXlsFile(jasperPrint, destFileName);
		return destFileName;
	}
	
	/**
	 * 把JASPER文件导出为XLS文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param conn 报表所需的数据库查询连接
	 * @return 最后导出的XLS文件名
	 * @throws JRException
	 */
	public static String exportReportToXlsFile (String sourceFileName, Connection conn) throws JRException {
		Map<String, Object> params = new HashMap<String, Object>();
		return exportReportToXlsFile(sourceFileName, params, conn);
	}
	
	/**
	 * 把JASPER文件导出为XLS文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param beanArray 欲填充到报表中的JavaBean数据
	 * @return 最后导出的XLS文件名
	 * @throws JRException
	 */
	public static String exportReportToXlsFile (String sourceFileName, Object[] beanArray) throws JRException {
		Map<String, Object> params = new HashMap<String, Object>();
		return exportReportToXlsFile(sourceFileName, params, beanArray);
	}
	
	/**
	 * 把JASPER文件导出为XLS文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param params 填充JASPER文件所需的参数值对
	 * @param conn 报表所需的数据库查询连接
	 * @return 最后导出的XLS文件名
	 * @throws JRException
	 */
	public static String exportReportToXlsFile (String sourceFileName, Map<String, Object> params, 
			Connection conn) throws JRException {
		JasperPrint jasperPrint = JasperFillManager.fillReport(sourceFileName, params, conn);
		File sourceFile = new File(sourceFileName);
		File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".xls");
		String destFileName = destFile.toString();
		exportReportToXlsFile(jasperPrint, destFileName);
		return destFileName;
	}
	
	/**
	 * 把JASPER文件导出为XLS文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param params 填充JASPER文件所需的参数值对
	 * @param beanArray 欲填充到报表中的JavaBean数据
	 * @return 最后导出的XLS文件名
	 * @throws JRException
	 */
	public static String exportReportToXlsFile (String sourceFileName, Map<String, Object> params, 
			Object[] beanArray) throws JRException {
		JRBeanArrayDataSource dataSource = new JRBeanArrayDataSource(beanArray);
		JasperPrint jasperPrint = JasperFillManager.fillReport(sourceFileName, params, dataSource);
		File sourceFile = new File(sourceFileName);
		File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".xls");
		String destFileName = destFile.toString();
		exportReportToXlsFile(jasperPrint, destFileName);
		return destFileName;
	}
	
	/**
	 * 把JASPER文件导出为XLS文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param destFileName 最后导出的XLS文件名
	 * @throws JRException
	 */
	public static void exportReportToXlsFile(String sourceFileName, String destFileName) throws JRException {
		Map<String, Object> params = new HashMap<String, Object>();
		exportReportToXlsFile(sourceFileName, destFileName, params);
	}
	
	/**
	 * 把JASPER文件导出为XLS文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param destFileName 最后导出的XLS文件名
	 * @param params 填充JASPER文件所需的参数值对
	 * @throws JRException
	 */
	public static void exportReportToXlsFile(String sourceFileName, String destFileName, 
			Map<String, Object> params) throws JRException {
		JasperPrint jasperPrint = JasperFillManager.fillReport(sourceFileName, params);
		exportReportToXlsFile(jasperPrint, destFileName);
	}
	
	/**
	 * 把JASPER文件导出为XLS文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param destFileName 最后导出的XLS文件名
	 * @param conn 报表所需的数据库查询连接
	 * @throws JRException
	 */
	public static void exportReportToXlsFile(String sourceFileName, String destFileName, 
			Connection conn) throws JRException {
		Map<String, Object> params = new HashMap<String, Object>();
		exportReportToXlsFile(sourceFileName, destFileName, params, conn);
	}
	
	/**
	 * 把JASPER文件导出为XLS文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param destFileName 最后导出的XLS文件名
	 * @param params 填充JASPER文件所需的参数值对
	 * @param conn 报表所需的数据库查询连接
	 * @throws JRException
	 */
	public static void exportReportToXlsFile(String sourceFileName, String destFileName, 
			Map<String, Object> params, Connection conn) throws JRException {
		JasperPrint jasperPrint = JasperFillManager.fillReport(sourceFileName, params, conn);
		exportReportToXlsFile(jasperPrint, destFileName);
	}
	
	/**
	 * 把JASPER文件导出为XLS文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param destFileName 最后导出的XLS文件名
	 * @param params 填充JASPER文件所需的参数值对
	 * @param beanArray 欲填充到报表中的JavaBean数据
	 * @throws JRException
	 */
	public static void exportReportToXlsFile(String sourceFileName, String destFileName, 
			Map<String, Object> params, Object[] beanArray) throws JRException {
		JRBeanArrayDataSource dataSource = new JRBeanArrayDataSource(beanArray);
		JasperPrint jasperPrint = JasperFillManager.fillReport(sourceFileName, params, dataSource);
		exportReportToXlsFile(jasperPrint, destFileName);
	}
	
	/**
	 * 把JASPER文件导出为XLS文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param destFileName 最后导出的XLS文件名
	 * @param beanArray 欲填充到报表中的JavaBean数据
	 * @throws JRException
	 */
	public static void exportReportToXlsFile (String sourceFileName, String destFileName, 
			Object[] beanArray) throws JRException {
		Map<String, Object> map = new HashMap<String, Object>();
		exportReportToXlsFile(sourceFileName, destFileName, map, beanArray);
	}
	
	/**
	 * 把JasperPrint对象导出为XLS文件
	 * @param jasperPrint JasperPrint导出对象
	 * @param destFileName 最后导出的XLS文件名
	 * @throws JRException
	 */
	public static void exportReportToXlsFile (JasperPrint jasperPrint, String destFileName) throws JRException {
		JRXlsExporter exporter = new JRXlsExporter();
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(new File(destFileName)));
		exporter.exportReport();
	}
	
	/**
	 * 把JasperPrint对象转换为XLSX文件格式，并以OutputStream的方式导出
	 * @param jasperPrint JasperPrint导出对象
	 * @param outputStream OutputStream导出目标
	 * @throws JRException
	 */
	public static void exportReportToXlsxStream (JasperPrint jasperPrint, OutputStream outputStream) throws JRException {
		JRXlsxExporter exporter = new JRXlsxExporter();
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
		exporter.exportReport();
	}
	
	/**
	 * 把JasperPrint对象转换为XLSX文件格式，并以byte[]的方式导出
	 * @param jasperPrint JasperPrint导出对象
	 * @return byte[]
	 * @throws JRException
	 */
	public static byte[] exportReportToXlsx (JasperPrint jasperPrint) throws JRException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JRXlsxExporter exporter = new JRXlsxExporter();
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));
		exporter.exportReport();
		return baos.toByteArray();
	}
	
	/**
	 * 把JASPER文件导出为XLSX文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @return 最后导出的XLSX文件名
	 * @throws JRException
	 */
	public static String exportReportToXlsxFile (String sourceFileName) throws JRException {
		Map<String, Object> params = new HashMap<String, Object>();
		return exportReportToXlsxFile(sourceFileName, params);
	}
	
	/**
	 * 把JASPER文件导出为XLSX文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param params 填充JASPER文件所需的参数值对
	 * @return 最后导出的XLSX文件名
	 * @throws JRException
	 */
	public static String exportReportToXlsxFile (String sourceFileName, Map<String, Object> params) throws JRException {
		JasperPrint jasperPrint = JasperFillManager.fillReport(sourceFileName, params);
		File sourceFile = new File(sourceFileName);
		File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".xlsx");
		String destFileName = destFile.toString();
		exportReportToXlsxFile(jasperPrint, destFileName);
		return destFileName;
	}
	
	/**
	 * 把JASPER文件导出为XLSX文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param conn 报表所需的数据库查询连接
	 * @return 最后导出的XLSX文件名
	 * @throws JRException
	 */
	public static String exportReportToXlsxFile (String sourceFileName, Connection conn) throws JRException {
		Map<String, Object> params = new HashMap<String, Object>();
		return exportReportToXlsxFile(sourceFileName, params, conn);
	}
	
	/**
	 * 把JASPER文件导出为XLSX文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param beanArray 欲填充到报表中的JavaBean数据
	 * @return 最后导出的XLSX文件名
	 * @throws JRException
	 */
	public static String exportReportToXlsxFile (String sourceFileName, Object[] beanArray) throws JRException {
		Map<String, Object> params = new HashMap<String, Object>();
		return exportReportToXlsxFile(sourceFileName, params, beanArray);
	}
	
	/**
	 * 把JASPER文件导出为XLSX文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param params 填充JASPER文件所需的参数值对
	 * @param conn 报表所需的数据库查询连接
	 * @return 最后导出的XLSX文件名
	 * @throws JRException
	 */
	public static String exportReportToXlsxFile (String sourceFileName, Map<String, Object> params, 
			Connection conn) throws JRException {
		JasperPrint jasperPrint = JasperFillManager.fillReport(sourceFileName, params, conn);
		File sourceFile = new File(sourceFileName);
		File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".xlsx");
		String destFileName = destFile.toString();
		exportReportToXlsxFile(jasperPrint, destFileName);
		return destFileName;
	}
	
	/**
	 * 把JASPER文件导出为XLSX文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param params 填充JASPER文件所需的参数值对
	 * @param beanArray 欲填充到报表中的JavaBean数据
	 * @return 最后导出的XLSX文件名
	 * @throws JRException
	 */
	public static String exportReportToXlsxFile (String sourceFileName, Map<String, Object> params, 
			Object[] beanArray) throws JRException {
		JRBeanArrayDataSource dataSource = new JRBeanArrayDataSource(beanArray);
		JasperPrint jasperPrint = JasperFillManager.fillReport(sourceFileName, params, dataSource);
		File sourceFile = new File(sourceFileName);
		File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".xlsx");
		String destFileName = destFile.toString();
		exportReportToXlsxFile(jasperPrint, destFileName);
		return destFileName;
	}
	
	/**
	 * 把JASPER文件导出为XLSX文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param destFileName 最后导出的XLSX文件名
	 * @throws JRException
	 */
	public static void exportReportToXlsxFile(String sourceFileName, String destFileName) throws JRException {
		Map<String, Object> params = new HashMap<String, Object>();
		exportReportToXlsxFile(sourceFileName, destFileName, params);
	}
	
	/**
	 * 把JASPER文件导出为XLSX文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param destFileName 最后导出的XLSX文件名
	 * @param params 填充JASPER文件所需的参数值对
	 * @throws JRException
	 */
	public static void exportReportToXlsxFile(String sourceFileName, String destFileName, 
			Map<String, Object> params) throws JRException {
		JasperPrint jasperPrint = JasperFillManager.fillReport(sourceFileName, params);
		exportReportToXlsxFile(jasperPrint, destFileName);
	}
	
	/**
	 * 把JASPER文件导出为XLSX文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param destFileName 最后导出的XLSX文件名
	 * @param conn 报表所需的数据库查询连接
	 * @throws JRException
	 */
	public static void exportReportToXlsxFile(String sourceFileName, String destFileName, 
			Connection conn) throws JRException {
		Map<String, Object> params = new HashMap<String, Object>();
		exportReportToXlsxFile(sourceFileName, destFileName, params, conn);
	}
	
	/**
	 * 把JASPER文件导出为XLSX文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param destFileName 最后导出的XLSX文件名
	 * @param params 填充JASPER文件所需的参数值对
	 * @param conn 报表所需的数据库查询连接
	 * @throws JRException
	 */
	public static void exportReportToXlsxFile(String sourceFileName, String destFileName, 
			Map<String, Object> params, Connection conn) throws JRException {
		JasperPrint jasperPrint = JasperFillManager.fillReport(sourceFileName, params, conn);
		exportReportToXlsxFile(jasperPrint, destFileName);
	}
	
	/**
	 * 把JASPER文件导出为XLSX文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param destFileName 最后导出的XLSX文件名
	 * @param params 填充JASPER文件所需的参数值对
	 * @param beanArray 欲填充到报表中的JavaBean数据
	 * @throws JRException
	 */
	public static void exportReportToXlsxFile(String sourceFileName, String destFileName, 
			Map<String, Object> params, Object[] beanArray) throws JRException {
		JRBeanArrayDataSource dataSource = new JRBeanArrayDataSource(beanArray);
		JasperPrint jasperPrint = JasperFillManager.fillReport(sourceFileName, params, dataSource);
		exportReportToXlsxFile(jasperPrint, destFileName);
	}
	
	/**
	 * 把JASPER文件导出为XLSX文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param destFileName 最后导出的XLSX文件名
	 * @param beanArray 欲填充到报表中的JavaBean数据
	 * @throws JRException
	 */
	public static void exportReportToXlsxFile (String sourceFileName, String destFileName, 
			Object[] beanArray) throws JRException {
		Map<String, Object> map = new HashMap<String, Object>();
		exportReportToXlsxFile(sourceFileName, destFileName, map, beanArray);
	}
	
	/**
	 * 把JasperPrint对象导出为XLSX文件
	 * @param jasperPrint JasperPrint导出对象
	 * @param destFileName 最后导出的XLSX文件名
	 * @throws JRException
	 */
	public static void exportReportToXlsxFile (JasperPrint jasperPrint, String destFileName) throws JRException {
		JRXlsxExporter exporter = new JRXlsxExporter();
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(new File(destFileName)));
		exporter.exportReport();
	}

	/**
	 * 把JASPER文件导出为PDF文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @return 最后导出的PDF文件名
	 * @throws JRException
	 */
	public static String exportReportToPdfFile (String sourceFileName) throws JRException {
		return JasperExportManager.exportReportToPdfFile(sourceFileName);
	}
	
	/**
	 * 把JASPER文件导出为PDF文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param params 填充JASPER文件所需的参数值对
	 * @return 最后导出的PDF文件名
	 * @throws JRException
	 */
	public static String exportReportToPdfFile (String sourceFileName, Map<String, Object> params) throws JRException {
		JasperPrint jasperPrint = JasperFillManager.fillReport(sourceFileName, params);
		File sourceFile = new File(sourceFileName);
		File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".pdf");
		String destFileName = destFile.toString();
		exportReportToPdfFile(jasperPrint, destFileName);
		return destFileName;
	}
	
	/**
	 * 把JASPER文件导出为PDF文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param conn 报表所需的数据库查询连接
	 * @return 最后导出的PDF文件名
	 * @throws JRException
	 */
	public static String exportReportToPdfFile (String sourceFileName, Connection conn) throws JRException {
		Map<String, Object> params = new HashMap<String, Object>();
		return exportReportToPdfFile(sourceFileName, params, conn);
	}
	
	/**
	 * 把JASPER文件导出为PDF文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param beanArray 欲填充到报表中的JavaBean数据
	 * @return 最后导出的PDF文件名
	 * @throws JRException
	 */
	public static String exportReportToPdfFile (String sourceFileName, Object[] beanArray) throws JRException {
		Map<String, Object> params = new HashMap<String, Object>();
		return exportReportToPdfFile(sourceFileName, params, beanArray);
	}
	
	/**
	 * 把JASPER文件导出为PDF文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param params 填充JASPER文件所需的参数值对
	 * @param conn 报表所需的数据库查询连接
	 * @return 最后导出的PDF文件名
	 * @throws JRException
	 */
	public static String exportReportToPdfFile (String sourceFileName, Map<String, Object> params, 
			Connection conn) throws JRException {
		JasperPrint jasperPrint = JasperFillManager.fillReport(sourceFileName, params, conn);
		File sourceFile = new File(sourceFileName);
		File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".pdf");
		String destFileName = destFile.toString();
		exportReportToPdfFile(jasperPrint, destFileName);
		return destFileName;
	}
	
	/**
	 * 把JASPER文件导出为PDF文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param params 填充JASPER文件所需的参数值对
	 * @param beanArray 欲填充到报表中的JavaBean数据
	 * @return 最后导出的PDF文件名
	 * @throws JRException
	 */
	public static String exportReportToPdfFile (String sourceFileName, Map<String, Object> params, 
			Object[] beanArray) throws JRException {
		JRBeanArrayDataSource dataSource = new JRBeanArrayDataSource(beanArray);
		JasperPrint jasperPrint = JasperFillManager.fillReport(sourceFileName, params, dataSource);
		File sourceFile = new File(sourceFileName);
		File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".pdf");
		String destFileName = destFile.toString();
		exportReportToPdfFile(jasperPrint, destFileName);
		return destFileName;
	}
	
	/**
	 * 把JASPER文件导出为PDF文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param destFileName 最后导出的PDF文件名
	 * @throws JRException
	 */
	public static void exportReportToPdfFile(String sourceFileName, String destFileName) throws JRException {
		JasperExportManager.exportReportToPdfFile(sourceFileName, destFileName);
	}
	
	/**
	 * 把JASPER文件导出为PDF文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param destFileName 最后导出的PDF文件名
	 * @param params 填充JASPER文件所需的参数值对
	 * @throws JRException
	 */
	public static void exportReportToPdfFile(String sourceFileName, String destFileName, 
			Map<String, Object> params) throws JRException {
		JasperPrint jasperPrint = JasperFillManager.fillReport(sourceFileName, params);
		exportReportToPdfFile(jasperPrint, destFileName);
	}
	
	/**
	 * 把JASPER文件导出为PDF文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param destFileName 最后导出的PDF文件名
	 * @param conn 报表所需的数据库查询连接
	 * @throws JRException
	 */
	public static void exportReportToPdfFile(String sourceFileName, String destFileName, 
			Connection conn) throws JRException {
		Map<String, Object> params = new HashMap<String, Object>();
		exportReportToPdfFile(sourceFileName, destFileName, params, conn);
	}
	
	/**
	 * 把JASPER文件导出为PDF文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param destFileName 最后导出的PDF文件名
	 * @param params 填充JASPER文件所需的参数值对
	 * @param conn 报表所需的数据库查询连接
	 * @throws JRException
	 */
	public static void exportReportToPdfFile(String sourceFileName, String destFileName, 
			Map<String, Object> params, Connection conn) throws JRException {
		JasperPrint jasperPrint = JasperFillManager.fillReport(sourceFileName, params, conn);
		exportReportToPdfFile(jasperPrint, destFileName);
	}
	
	/**
	 * 把JASPER文件导出为PDF文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param destFileName 最后导出的PDF文件名
	 * @param params 填充JASPER文件所需的参数值对
	 * @param beanArray 欲填充到报表中的JavaBean数据
	 * @throws JRException
	 */
	public static void exportReportToPdfFile(String sourceFileName, String destFileName, 
			Map<String, Object> params, Object[] beanArray) throws JRException {
		JRBeanArrayDataSource dataSource = new JRBeanArrayDataSource(beanArray);
		JasperPrint jasperPrint = JasperFillManager.fillReport(sourceFileName, params, dataSource);
		exportReportToPdfFile(jasperPrint, destFileName);
	}
	
	/**
	 * 把JASPER文件导出为PDF文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param destFileName 最后导出的PDF文件名
	 * @param beanArray 欲填充到报表中的JavaBean数据
	 * @throws JRException
	 */
	public static void exportReportToPdfFile (String sourceFileName, String destFileName, 
			Object[] beanArray) throws JRException {
		Map<String, Object> map = new HashMap<String, Object>();
		exportReportToPdfFile(sourceFileName, destFileName, map, beanArray);
	}
	
	/**
	 * 把JasperPrint对象导出为PDF文件
	 * @param jasperPrint JasperPrint导出对象
	 * @param destFileName 最后导出的PDF文件名
	 * @throws JRException
	 */
	public static void exportReportToPdfFile (JasperPrint jasperPrint, String destFileName) throws JRException {
		JasperExportManager.exportReportToPdfFile(jasperPrint, destFileName);
	}

	/**
	 * 把JasperPrint对象转换为PDF文件格式，并以byte[]的方式导出
	 * @param jasperPrint JasperPrint导出对象
	 * @return byte[]
	 * @throws JRException
	 */
	public static byte[] exportReportToPdf (JasperPrint jasperPrint) throws JRException {
		return JasperExportManager.exportReportToPdf(jasperPrint);
	}
	
	/**
	 * 把JasperPrint对象转换为PDF文件格式，并以OutputStream的方式导出
	 * @param jasperPrint JasperPrint导出对象
	 * @param outputStream OutputStream导出目标
	 * @throws JRException
	 */
	public static void exportReportToPdfStream (JasperPrint jasperPrint, OutputStream outputStream) throws JRException {
		JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
	}
//--------------------------------------------------word-------------------	
    /**
     * 把JasperPrint对象转换为DOCX文件格式，并以OutputStream的方式导出
     * @param jasperPrint JasperPrint导出对象
     * @param outputStream OutputStream导出目标
     * @throws JRException
     */
    public static void exportReportToDocxStream (JasperPrint jasperPrint, OutputStream outputStream) throws JRException {
        JRDocxExporter exporter = new JRDocxExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
        exporter.exportReport();
    }
    
    /**
     * 把JasperPrint对象转换为DOCX文件格式，并以byte[]的方式导出
     * @param jasperPrint JasperPrint导出对象
     * @return byte[]
     * @throws JRException
     */
    public static byte[] exportReportToDocx (JasperPrint jasperPrint) throws JRException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JRDocxExporter exporter = new JRDocxExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));
        exporter.exportReport();
        return baos.toByteArray();
    }
    
    /**
     * 把JASPER文件导出为DOCX文件
     * @param sourceFileName JASPER文件名，后缀名JASPER
     * @return 最后导出的DOCX文件名
     * @throws JRException
     */
    public static String exportReportToDocxFile (String sourceFileName) throws JRException {
        Map<String, Object> params = new HashMap<String, Object>();
        return exportReportToDocxFile(sourceFileName, params);
    }
    
    /**
     * 把JASPER文件导出为DOCX文件
     * @param sourceFileName JASPER文件名，后缀名JASPER
     * @param params 填充JASPER文件所需的参数值对
     * @return 最后导出的DOCX文件名
     * @throws JRException
     */
    public static String exportReportToDocxFile (String sourceFileName, Map<String, Object> params) throws JRException {
        JasperPrint jasperPrint = JasperFillManager.fillReport(sourceFileName, params);
        File sourceFile = new File(sourceFileName);
        File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".xls");
        String destFileName = destFile.toString();
        exportReportToDocxFile(jasperPrint, destFileName);
        return destFileName;
    }
    
    /**
     * 把JASPER文件导出为DOCX文件
     * @param sourceFileName JASPER文件名，后缀名JASPER
     * @param conn 报表所需的数据库查询连接
     * @return 最后导出的DOCX文件名
     * @throws JRException
     */
    public static String exportReportToDocxFile (String sourceFileName, Connection conn) throws JRException {
        Map<String, Object> params = new HashMap<String, Object>();
        return exportReportToDocxFile(sourceFileName, params, conn);
    }
    
    /**
     * 把JASPER文件导出为DOCX文件
     * @param sourceFileName JASPER文件名，后缀名JASPER
     * @param beanArray 欲填充到报表中的JavaBean数据
     * @return 最后导出的DOCX文件名
     * @throws JRException
     */
    public static String exportReportToDocxFile (String sourceFileName, Object[] beanArray) throws JRException {
        Map<String, Object> params = new HashMap<String, Object>();
        return exportReportToDocxFile(sourceFileName, params, beanArray);
    }
    
    /**
     * 把JASPER文件导出为DOCX文件
     * @param sourceFileName JASPER文件名，后缀名JASPER
     * @param params 填充JASPER文件所需的参数值对
     * @param conn 报表所需的数据库查询连接
     * @return 最后导出的DOCX文件名
     * @throws JRException
     */
    public static String exportReportToDocxFile (String sourceFileName, Map<String, Object> params, 
            Connection conn) throws JRException {
        JasperPrint jasperPrint = JasperFillManager.fillReport(sourceFileName, params, conn);
        File sourceFile = new File(sourceFileName);
        File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".xls");
        String destFileName = destFile.toString();
        exportReportToDocxFile(jasperPrint, destFileName);
        return destFileName;
    }
    
    /**
     * 把JASPER文件导出为DOCX文件
     * @param sourceFileName JASPER文件名，后缀名JASPER
     * @param params 填充JASPER文件所需的参数值对
     * @param beanArray 欲填充到报表中的JavaBean数据
     * @return 最后导出的DOCX文件名
     * @throws JRException
     */
    public static String exportReportToDocxFile (String sourceFileName, Map<String, Object> params, 
            Object[] beanArray) throws JRException {
        JRBeanArrayDataSource dataSource = new JRBeanArrayDataSource(beanArray);
        JasperPrint jasperPrint = JasperFillManager.fillReport(sourceFileName, params, dataSource);
        File sourceFile = new File(sourceFileName);
        File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".xls");
        String destFileName = destFile.toString();
        exportReportToDocxFile(jasperPrint, destFileName);
        return destFileName;
    }
    
    /**
     * 把JASPER文件导出为DOCX文件
     * @param sourceFileName JASPER文件名，后缀名JASPER
     * @param destFileName 最后导出的DOCX文件名
     * @throws JRException
     */
    public static void exportReportToDocxFile(String sourceFileName, String destFileName) throws JRException {
        Map<String, Object> params = new HashMap<String, Object>();
        exportReportToDocxFile(sourceFileName, destFileName, params);
    }
    
    /**
     * 把JASPER文件导出为DOCX文件
     * @param sourceFileName JASPER文件名，后缀名JASPER
     * @param destFileName 最后导出的DOCX文件名
     * @param params 填充JASPER文件所需的参数值对
     * @throws JRException
     */
    public static void exportReportToDocxFile(String sourceFileName, String destFileName, 
            Map<String, Object> params) throws JRException {
        JasperPrint jasperPrint = JasperFillManager.fillReport(sourceFileName, params);
        exportReportToDocxFile(jasperPrint, destFileName);
    }
    
    /**
     * 把JASPER文件导出为DOCX文件
     * @param sourceFileName JASPER文件名，后缀名JASPER
     * @param destFileName 最后导出的DOCX文件名
     * @param conn 报表所需的数据库查询连接
     * @throws JRException
     */
    public static void exportReportToDocxFile(String sourceFileName, String destFileName, 
            Connection conn) throws JRException {
        Map<String, Object> params = new HashMap<String, Object>();
        exportReportToDocxFile(sourceFileName, destFileName, params, conn);
    }
    
    /**
     * 把JASPER文件导出为DOCX文件
     * @param sourceFileName JASPER文件名，后缀名JASPER
     * @param destFileName 最后导出的DOCX文件名
     * @param params 填充JASPER文件所需的参数值对
     * @param conn 报表所需的数据库查询连接
     * @throws JRException
     */
    public static void exportReportToDocxFile(String sourceFileName, String destFileName, 
            Map<String, Object> params, Connection conn) throws JRException {
        JasperPrint jasperPrint = JasperFillManager.fillReport(sourceFileName, params, conn);
        exportReportToDocxFile(jasperPrint, destFileName);
    }
    
    /**
     * 把JASPER文件导出为DOCX文件
     * @param sourceFileName JASPER文件名，后缀名JASPER
     * @param destFileName 最后导出的DOCX文件名
     * @param params 填充JASPER文件所需的参数值对
     * @param beanArray 欲填充到报表中的JavaBean数据
     * @throws JRException
     */
    public static void exportReportToDocxFile(String sourceFileName, String destFileName, 
            Map<String, Object> params, Object[] beanArray) throws JRException {
        JRBeanArrayDataSource dataSource = new JRBeanArrayDataSource(beanArray);
        JasperPrint jasperPrint = JasperFillManager.fillReport(sourceFileName, params, dataSource);
        exportReportToDocxFile(jasperPrint, destFileName);
    }
    
    /**
     * 把JASPER文件导出为DOCX文件
     * @param sourceFileName JASPER文件名，后缀名JASPER
     * @param destFileName 最后导出的DOCX文件名
     * @param beanArray 欲填充到报表中的JavaBean数据
     * @throws JRException
     */
    public static void exportReportToDocxFile (String sourceFileName, String destFileName, 
            Object[] beanArray) throws JRException {
        Map<String, Object> map = new HashMap<String, Object>();
        exportReportToDocxFile(sourceFileName, destFileName, map, beanArray);
    }
    
    /**
     * 把JasperPrint对象导出为DOCX文件
     * @param jasperPrint JasperPrint导出对象
     * @param destFileName 最后导出的DOCX文件名
     * @throws JRException
     */
    public static void exportReportToDocxFile (JasperPrint jasperPrint, String destFileName) throws JRException {
        JRDocxExporter exporter = new JRDocxExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(new File(destFileName)));
        exporter.exportReport();
    }

//--------------------------------------------------------------------------
    
	
	/**
	 * 把JasperPrint对象转换为XML文件格式，并以OutputStream的方式导出
	 * @param jasperPrint JasperPrint导出对象
	 * @param outputStream OutputStream导出目标
	 * @throws JRException
	 */
	public static void exportReportToXmlStream (JasperPrint jasperPrint, OutputStream outputStream) throws JRException {
		JRXmlExporter exporter = new JRXmlExporter();
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleWriterExporterOutput(outputStream));
		exporter.exportReport();
	}
	
	/**
	 * 把JasperPrint对象转换为XML文件格式，并以byte[]的方式导出
	 * @param jasperPrint JasperPrint导出对象
	 * @return byte[]
	 * @throws JRException
	 */
	public static byte[] exportReportToXml(JasperPrint jasperPrint) throws JRException {
		JRXmlExporter exporter = new JRXmlExporter();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleWriterExporterOutput(baos));
		exporter.exportReport();
		return baos.toByteArray();
	}
	
	/**
	 * 把JASPER文件导出为XML文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param isEmbeddingImages 是否嵌入图片
	 * @return 最后导出的XML文件名
	 * @throws JRException
	 */
	public static String exportReportToXmlFile (String sourceFileName, boolean isEmbeddingImages) throws JRException {
		return JasperExportManager.exportReportToXmlFile(sourceFileName, isEmbeddingImages);
	}
	
	/**
	 * 把JASPER文件导出为XML文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param params 填充JASPER文件所需的参数值对
	 * @param isEmbeddingImages 是否嵌入图片
	 * @return 最后导出的XML文件名
	 * @throws JRException
	 */
	public static String exportReportToXmlFile (String sourceFileName, Map<String, Object> params, boolean isEmbeddingImages) throws JRException {
		JasperPrint jasperPrint = JasperFillManager.fillReport(sourceFileName, params);
		File sourceFile = new File(sourceFileName);
		File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".jrpxml");
		String destFileName = destFile.toString();
		exportReportToXmlFile(jasperPrint, destFileName, isEmbeddingImages);
		return destFileName;
	}
	
	/**
	 * 把JASPER文件导出为XML文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param conn 报表所需的数据库查询连接
	 * @param isEmbeddingImages 是否嵌入图片
	 * @return 最后导出的XML文件名
	 * @throws JRException
	 */
	public static String exportReportToXmlFile (String sourceFileName, Connection conn, boolean isEmbeddingImages) throws JRException {
		Map<String, Object> params = new HashMap<String, Object>();
		return exportReportToXmlFile(sourceFileName, params, conn, isEmbeddingImages);
	}
	
	/**
	 * 把JASPER文件导出为XML文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param beanArray 欲填充到报表中的JavaBean数据
	 * @param isEmbeddingImages 是否嵌入图片
	 * @return 最后导出的XML文件名
	 * @throws JRException
	 */
	public static String exportReportToXmlFile (String sourceFileName, Object[] beanArray, boolean isEmbeddingImages) throws JRException {
		Map<String, Object> params = new HashMap<String, Object>();
		return exportReportToXmlFile(sourceFileName, params, beanArray, isEmbeddingImages);
	}
	
	/**
	 * 把JASPER文件导出为XML文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param params 填充JASPER文件所需的参数值对
	 * @param conn 报表所需的数据库查询连接
	 * @param isEmbeddingImages 是否嵌入图片
	 * @return 最后导出的XML文件名
	 * @throws JRException
	 */
	public static String exportReportToXmlFile (String sourceFileName, Map<String, Object> params, 
			Connection conn, boolean isEmbeddingImages) throws JRException {
		JasperPrint jasperPrint = JasperFillManager.fillReport(sourceFileName, params, conn);
		File sourceFile = new File(sourceFileName);
		File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".jrpxml");
		String destFileName = destFile.toString();
		exportReportToXmlFile(jasperPrint, destFileName, isEmbeddingImages);
		return destFileName;
	}
	
	/**
	 * 把JASPER文件导出为XML文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param params 填充JASPER文件所需的参数值对
	 * @param beanArray 欲填充到报表中的JavaBean数据
	 * @param isEmbeddingImages 是否嵌入图片
	 * @return 最后导出的XML文件名
	 * @throws JRException
	 */
	public static String exportReportToXmlFile (String sourceFileName, Map<String, Object> params, 
			Object[] beanArray, boolean isEmbeddingImages) throws JRException {
		JRBeanArrayDataSource dataSource = new JRBeanArrayDataSource(beanArray);
		JasperPrint jasperPrint = JasperFillManager.fillReport(sourceFileName, params, dataSource);
		File sourceFile = new File(sourceFileName);
		File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".jrpxml");
		String destFileName = destFile.toString();
		exportReportToXmlFile(jasperPrint, destFileName, isEmbeddingImages);
		return destFileName;
	}
	
	/**
	 * 把JASPER文件导出为XML文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param destFileName 最后导出的XML文件名
	 * @param isEmbeddingImages 是否嵌入图片
	 * @throws JRException
	 */
	public static void exportReportToXmlFile(String sourceFileName, String destFileName, boolean isEmbeddingImages) throws JRException {
		Map<String, Object> params = new HashMap<String, Object>();
		exportReportToXmlFile(sourceFileName, destFileName, params, isEmbeddingImages);
	}
	
	/**
	 * 把JASPER文件导出为XML文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param destFileName 最后导出的XML文件名
	 * @param params 填充JASPER文件所需的参数值对
	 * @param isEmbeddingImages 是否嵌入图片
	 * @throws JRException
	 */
	public static void exportReportToXmlFile(String sourceFileName, String destFileName, 
			Map<String, Object> params, boolean isEmbeddingImages) throws JRException {
		JasperPrint jasperPrint = JasperFillManager.fillReport(sourceFileName, params);
		exportReportToXmlFile(jasperPrint, destFileName, isEmbeddingImages);
	}
	
	/**
	 * 把JASPER文件导出为XML文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param destFileName 最后导出的XML文件名
	 * @param conn 报表所需的数据库查询连接
	 * @param isEmbeddingImages 是否嵌入图片
	 * @throws JRException
	 */
	public static void exportReportToXmlFile(String sourceFileName, String destFileName, 
			Connection conn, boolean isEmbeddingImages) throws JRException {
		Map<String, Object> params = new HashMap<String, Object>();
		exportReportToXmlFile(sourceFileName, destFileName, params, conn, isEmbeddingImages);
	}
	
	/**
	 * 把JASPER文件导出为XML文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param destFileName 最后导出的XML文件名
	 * @param params 填充JASPER文件所需的参数值对
	 * @param conn 报表所需的数据库查询连接
	 * @param isEmbeddingImages 是否嵌入图片
	 * @throws JRException
	 */
	public static void exportReportToXmlFile(String sourceFileName, String destFileName, 
			Map<String, Object> params, Connection conn, boolean isEmbeddingImages) throws JRException {
		JasperPrint jasperPrint = JasperFillManager.fillReport(sourceFileName, params, conn);
		exportReportToXmlFile(jasperPrint, destFileName, isEmbeddingImages);
	}
	
	/**
	 * 把JASPER文件导出为XML文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param destFileName 最后导出的XML文件名
	 * @param params 填充JASPER文件所需的参数值对
	 * @param beanArray 欲填充到报表中的JavaBean数据
	 * @param isEmbeddingImages 是否嵌入图片
	 * @throws JRException
	 */
	public static void exportReportToXmlFile(String sourceFileName, String destFileName, 
			Map<String, Object> params, Object[] beanArray, boolean isEmbeddingImages) throws JRException {
		JRBeanArrayDataSource dataSource = new JRBeanArrayDataSource(beanArray);
		JasperPrint jasperPrint = JasperFillManager.fillReport(sourceFileName, params, dataSource);
		exportReportToXmlFile(jasperPrint, destFileName, isEmbeddingImages);
	}
	
	/**
	 * 把JASPER文件导出为XML文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param destFileName 最后导出的XML文件名
	 * @param beanArray 欲填充到报表中的JavaBean数据
	 * @param isEmbeddingImages 是否嵌入图片
	 * @throws JRException
	 */
	public static void exportReportToXmlFile (String sourceFileName, String destFileName, 
			Object[] beanArray, boolean isEmbeddingImages) throws JRException {
		Map<String, Object> map = new HashMap<String, Object>();
		exportReportToXmlFile(sourceFileName, destFileName, map, beanArray, isEmbeddingImages);
	}
	
	/**
	 * 把JasperPrint对象导出为XML文件
	 * @param jasperPrint JasperPrint导出对象
	 * @param destFileName 最后导出的XML文件名
	 * @param isEmbeddingImages 是否嵌入图片
	 * @throws JRException
	 */
	public static void exportReportToXmlFile (JasperPrint jasperPrint, String destFileName, boolean isEmbeddingImages) throws JRException {
		JasperExportManager.exportReportToXmlFile(jasperPrint, destFileName, isEmbeddingImages);
	}
	
	/**
	 * 把JasperPrint对象转换为HTML文件格式，并以OutputStream的方式导出
	 * @param jasperPrint JasperPrint导出对象
	 * @param outputStream OutputStream导出目标
	 * @throws JRException
	 */
	public static void exportReportToHtmlStream (JasperPrint jasperPrint, OutputStream outputStream) throws JRException {
		HtmlExporter exporter = new HtmlExporter();
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleHtmlExporterOutput(outputStream));
		exporter.exportReport();
	}
	
	/**
	 * 把JasperPrint对象转换为HTML文件格式，并以byte[]的方式导出
	 * @param jasperPrint JasperPrint导出对象
	 * @return byte[]
	 * @throws JRException
	 */
	public static byte[] exportReportToHtml(JasperPrint jasperPrint) throws JRException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		HtmlExporter exporter = new HtmlExporter();
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleHtmlExporterOutput(baos));
		exporter.exportReport();
		return baos.toByteArray();
	}
	
	/**
	 * 把JASPER文件导出为HTML文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @return 最后导出的HTML文件名
	 * @throws JRException
	 */
	public static String exportReportToHtmlFile (String sourceFileName) throws JRException {
		Map<String, Object> params = new HashMap<String, Object>();
		return exportReportToHtmlFile(sourceFileName, params);
	}
	
	/**
	 * 把JASPER文件导出为HTML文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param params 填充JASPER文件所需的参数值对
	 * @return 最后导出的HTML文件名
	 * @throws JRException
	 */
	public static String exportReportToHtmlFile (String sourceFileName, Map<String, Object> params) throws JRException {
		JasperPrint jasperPrint = JasperFillManager.fillReport(sourceFileName, params);
		File sourceFile = new File(sourceFileName);
		File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".html");
		String destFileName = destFile.toString();
		exportReportToHtmlFile(jasperPrint, destFileName);
		return destFileName;
	}
	
	/**
	 * 把JASPER文件导出为HTML文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param conn 报表所需的数据库查询连接
	 * @return 最后导出的HTML文件名
	 * @throws JRException
	 */
	public static String exportReportToHtmlFile (String sourceFileName, Connection conn) throws JRException {
		Map<String, Object> params = new HashMap<String, Object>();
		return exportReportToHtmlFile(sourceFileName, params, conn);
	}
	
	/**
	 * 把JASPER文件导出为HTML文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param beanArray 欲填充到报表中的JavaBean数据
	 * @return 最后导出的HTML文件名
	 * @throws JRException
	 */
	public static String exportReportToHtmlFile (String sourceFileName, Object[] beanArray) throws JRException {
		Map<String, Object> params = new HashMap<String, Object>();
		return exportReportToHtmlFile(sourceFileName, params, beanArray);
	}
	
	/**
	 * 把JASPER文件导出为HTML文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param params 填充JASPER文件所需的参数值对
	 * @param conn 报表所需的数据库查询连接
	 * @return 最后导出的HTML文件名
	 * @throws JRException
	 */
	public static String exportReportToHtmlFile (String sourceFileName, Map<String, Object> params, 
			Connection conn) throws JRException {
		JasperPrint jasperPrint = JasperFillManager.fillReport(sourceFileName, params, conn);
		File sourceFile = new File(sourceFileName);
		File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".html");
		String destFileName = destFile.toString();
		exportReportToHtmlFile(jasperPrint, destFileName);
		return destFileName;
	}
	
	/**
	 * 把JASPER文件导出为HTML文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param params 填充JASPER文件所需的参数值对
	 * @param beanArray 欲填充到报表中的JavaBean数据
	 * @return 最后导出的HTML文件名
	 * @throws JRException
	 */
	public static String exportReportToHtmlFile (String sourceFileName, Map<String, Object> params, 
			Object[] beanArray) throws JRException {
		JRBeanArrayDataSource dataSource = new JRBeanArrayDataSource(beanArray);
		JasperPrint jasperPrint = JasperFillManager.fillReport(sourceFileName, params, dataSource);
		File sourceFile = new File(sourceFileName);
		File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".html");
		String destFileName = destFile.toString();
		exportReportToHtmlFile(jasperPrint, destFileName);
		return destFileName;
	}
	
	/**
	 * 把JASPER文件导出为HTML文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param destFileName 最后导出的HTML文件名
	 * @throws JRException
	 */
	public static void exportReportToHtmlFile(String sourceFileName, String destFileName) throws JRException {
		Map<String, Object> params = new HashMap<String, Object>();
		exportReportToHtmlFile(sourceFileName, destFileName, params);
	}
	
	/**
	 * 把JASPER文件导出为HTML文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param destFileName 最后导出的HTML文件名
	 * @param params 填充JASPER文件所需的参数值对
	 * @throws JRException
	 */
	public static void exportReportToHtmlFile(String sourceFileName, String destFileName, 
			Map<String, Object> params) throws JRException {
		JasperPrint jasperPrint = JasperFillManager.fillReport(sourceFileName, params);
		exportReportToHtmlFile(jasperPrint, destFileName);
	}
	
	/**
	 * 把JASPER文件导出为HTML文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param destFileName 最后导出的HTML文件名
	 * @param conn 报表所需的数据库查询连接
	 * @throws JRException
	 */
	public static void exportReportToHtmlFile(String sourceFileName, String destFileName, 
			Connection conn) throws JRException {
		Map<String, Object> params = new HashMap<String, Object>();
		exportReportToHtmlFile(sourceFileName, destFileName, params, conn);
	}
	
	/**
	 * 把JASPER文件导出为HTML文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param destFileName 最后导出的HTML文件名
	 * @param params 填充JASPER文件所需的参数值对
	 * @param conn 报表所需的数据库查询连接
	 * @throws JRException
	 */
	public static void exportReportToHtmlFile(String sourceFileName, String destFileName, 
			Map<String, Object> params, Connection conn) throws JRException {
		JasperPrint jasperPrint = JasperFillManager.fillReport(sourceFileName, params, conn);
		exportReportToHtmlFile(jasperPrint, destFileName);
	}
	
	/**
	 * 把JASPER文件导出为HTML文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param destFileName 最后导出的HTML文件名
	 * @param params 填充JASPER文件所需的参数值对
	 * @param beanArray 欲填充到报表中的JavaBean数据
	 * @throws JRException
	 */
	public static void exportReportToHtmlFile(String sourceFileName, String destFileName, 
			Map<String, Object> params, Object[] beanArray) throws JRException {
		JRBeanArrayDataSource dataSource = new JRBeanArrayDataSource(beanArray);
		JasperPrint jasperPrint = JasperFillManager.fillReport(sourceFileName, params, dataSource);
		exportReportToHtmlFile(jasperPrint, destFileName);
	}
	
	/**
	 * 把JASPER文件导出为HTML文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param destFileName 最后导出的HTML文件名
	 * @param beanArray 欲填充到报表中的JavaBean数据
	 * @throws JRException
	 */
	public static void exportReportToHtmlFile (String sourceFileName, String destFileName, 
			Object[] beanArray) throws JRException {
		Map<String, Object> map = new HashMap<String, Object>();
		exportReportToHtmlFile(sourceFileName, destFileName, map, beanArray);
	}
	
	/**
	 * 把JasperPrint对象导出为HTML文件
	 * @param jasperPrint JasperPrint导出对象
	 * @param destFileName 最后导出的HTML文件名
	 * @throws JRException
	 */
	public static void exportReportToHtmlFile (JasperPrint jasperPrint, String destFileName) throws JRException {
		JasperExportManager.exportReportToHtmlFile(jasperPrint, destFileName);
	}
	
	/**
	 * 把JasperPrint对象转换为RTF文件格式，并以OutputStream的方式导出
	 * @param jasperPrint JasperPrint导出对象
	 * @param outputStream OutputStream导出目标
	 * @throws JRException
	 */
	public static void exportReportToRtfStream (JasperPrint jasperPrint, OutputStream outputStream) throws JRException {
		JRRtfExporter exporter = new JRRtfExporter();
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleWriterExporterOutput(outputStream));
		exporter.exportReport();
	}
	
	/**
	 * 把JasperPrint对象转换为RTF文件格式，并以byte[]的方式导出
	 * @param jasperPrint JasperPrint导出对象
	 * @return byte[]
	 * @throws JRException
	 */
	public static byte[] exportReportToRtf (JasperPrint jasperPrint) throws JRException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JRRtfExporter exporter = new JRRtfExporter();
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleWriterExporterOutput(baos));
		exporter.exportReport();
		return baos.toByteArray();
	}
	
	/**
	 * 把JASPER文件导出为RTF文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @return 最后导出的RTF文件名
	 * @throws JRException
	 */
	public static String exportReportToRtfFile (String sourceFileName) throws JRException {
		Map<String, Object> params = new HashMap<String, Object>();
		return exportReportToRtfFile(sourceFileName, params);
	}
	
	/**
	 * 把JASPER文件导出为RTF文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param params 填充JASPER文件所需的参数值对
	 * @return 最后导出的RTF文件名
	 * @throws JRException
	 */
	public static String exportReportToRtfFile (String sourceFileName, Map<String, Object> params) throws JRException {
		JasperPrint jasperPrint = JasperFillManager.fillReport(sourceFileName, params);
		File sourceFile = new File(sourceFileName);
		File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".rtf");
		String destFileName = destFile.toString();
		exportReportToRtfFile(jasperPrint, destFileName);
		return destFileName;
	}
	
	/**
	 * 把JASPER文件导出为RTF文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param conn 报表所需的数据库查询连接
	 * @return 最后导出的RTF文件名
	 * @throws JRException
	 */
	public static String exportReportToRtfFile (String sourceFileName, Connection conn) throws JRException {
		Map<String, Object> params = new HashMap<String, Object>();
		return exportReportToRtfFile(sourceFileName, params, conn);
	}
	
	/**
	 * 把JASPER文件导出为RTF文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param beanArray 欲填充到报表中的JavaBean数据
	 * @return 最后导出的RTF文件名
	 * @throws JRException
	 */
	public static String exportReportToRtfFile (String sourceFileName, Object[] beanArray) throws JRException {
		Map<String, Object> params = new HashMap<String, Object>();
		return exportReportToRtfFile(sourceFileName, params, beanArray);
	}
	
	/**
	 * 把JASPER文件导出为RTF文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param params 填充JASPER文件所需的参数值对
	 * @param conn 报表所需的数据库查询连接
	 * @return 最后导出的RTF文件名
	 * @throws JRException
	 */
	public static String exportReportToRtfFile (String sourceFileName, Map<String, Object> params, 
			Connection conn) throws JRException {
		JasperPrint jasperPrint = JasperFillManager.fillReport(sourceFileName, params, conn);
		File sourceFile = new File(sourceFileName);
		File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".rtf");
		String destFileName = destFile.toString();
		exportReportToRtfFile(jasperPrint, destFileName);
		return destFileName;
	}
	
	/**
	 * 把JASPER文件导出为RTF文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param params 填充JASPER文件所需的参数值对
	 * @param beanArray 欲填充到报表中的JavaBean数据
	 * @return 最后导出的RTF文件名
	 * @throws JRException
	 */
	public static String exportReportToRtfFile (String sourceFileName, Map<String, Object> params, 
			Object[] beanArray) throws JRException {
		JRBeanArrayDataSource dataSource = new JRBeanArrayDataSource(beanArray);
		JasperPrint jasperPrint = JasperFillManager.fillReport(sourceFileName, params, dataSource);
		File sourceFile = new File(sourceFileName);
		File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".rtf");
		String destFileName = destFile.toString();
		exportReportToRtfFile(jasperPrint, destFileName);
		return destFileName;
	}
	
	/**
	 * 把JASPER文件导出为RTF文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param destFileName 最后导出的RTF文件名
	 * @throws JRException
	 */
	public static void exportReportToRtfFile(String sourceFileName, String destFileName) throws JRException {
		Map<String, Object> params = new HashMap<String, Object>();
		exportReportToRtfFile(sourceFileName, destFileName, params);
	}
	
	/**
	 * 把JASPER文件导出为RTF文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param destFileName 最后导出的RTF文件名
	 * @param params 填充JASPER文件所需的参数值对
	 * @throws JRException
	 */
	public static void exportReportToRtfFile(String sourceFileName, String destFileName, 
			Map<String, Object> params) throws JRException {
		JasperPrint jasperPrint = JasperFillManager.fillReport(sourceFileName, params);
		exportReportToRtfFile(jasperPrint, destFileName);
	}
	
	/**
	 * 把JASPER文件导出为RTF文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param destFileName 最后导出的RTF文件名
	 * @param conn 报表所需的数据库查询连接
	 * @throws JRException
	 */
	public static void exportReportToRtfFile(String sourceFileName, String destFileName, 
			Connection conn) throws JRException {
		Map<String, Object> params = new HashMap<String, Object>();
		exportReportToRtfFile(sourceFileName, destFileName, params, conn);
	}
	
	/**
	 * 把JASPER文件导出为RTF文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param destFileName 最后导出的RTF文件名
	 * @param params 填充JASPER文件所需的参数值对
	 * @param conn 报表所需的数据库查询连接
	 * @throws JRException
	 */
	public static void exportReportToRtfFile(String sourceFileName, String destFileName, 
			Map<String, Object> params, Connection conn) throws JRException {
		JasperPrint jasperPrint = JasperFillManager.fillReport(sourceFileName, params, conn);
		exportReportToRtfFile(jasperPrint, destFileName);
	}
	
	/**
	 * 把JASPER文件导出为RTF文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param destFileName 最后导出的RTF文件名
	 * @param params 填充JASPER文件所需的参数值对
	 * @param beanArray 欲填充到报表中的JavaBean数据
	 * @throws JRException
	 */
	public static void exportReportToRtfFile(String sourceFileName, String destFileName, 
			Map<String, Object> params, Object[] beanArray) throws JRException {
		JRBeanArrayDataSource dataSource = new JRBeanArrayDataSource(beanArray);
		JasperPrint jasperPrint = JasperFillManager.fillReport(sourceFileName, params, dataSource);
		exportReportToRtfFile(jasperPrint, destFileName);
	}
	
	/**
	 * 把JASPER文件导出为RTF文件
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param destFileName 最后导出的RTF文件名
	 * @param beanArray 欲填充到报表中的JavaBean数据
	 * @throws JRException
	 */
	public static void exportReportToRtfFile (String sourceFileName, String destFileName, 
			Object[] beanArray) throws JRException {
		Map<String, Object> map = new HashMap<String, Object>();
		exportReportToRtfFile(sourceFileName, destFileName, map, beanArray);
	}
	
	/**
	 * 把JasperPrint对象导出为RTF文件
	 * @param jasperPrint JasperPrint导出对象
	 * @param destFileName 最后导出的RTF文件名
	 * @throws JRException
	 */
	public static void exportReportToRtfFile (JasperPrint jasperPrint, String destFileName) throws JRException {
		JRRtfExporter exporter = new JRRtfExporter();
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleWriterExporterOutput(destFileName));
		exporter.exportReport();
	}
	
	/**
	 * 把JASPER文件转换为JasperPrint对象
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param conn 报表所需的数据库查询连接
	 * @return 最后导出的JasperPrint对象
	 * @throws JRException
	 */
	public static JasperPrint getJasperPrint(String sourceFileName, Connection conn) throws JRException {
		Map<String, Object> params = new HashMap<String, Object>();
		return getJasperPrint(sourceFileName, params, conn);
	}
	
	/**
	 * 把JASPER文件转换为JasperPrint对象
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param params 填充JASPER文件所需的参数值对
	 * @param conn 报表所需的数据库查询连接
	 * @return 最后导出的JasperPrint对象
	 * @throws JRException
	 */
	public static JasperPrint getJasperPrint(String sourceFileName, Map<String, Object> params, 
			Connection conn) throws JRException {
		return JasperFillManager.fillReport(sourceFileName, params, conn);
	}
	
	/**
	 * 把JASPER文件转换为JasperPrint对象
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param params 填充JASPER文件所需的参数值对
	 * @param beanArray 欲填充到报表中的JavaBean数据
	 * @return 最后导出的JasperPrint对象
	 * @throws JRException
	 */
	public static JasperPrint getJasperPrint(String sourceFileName, Map<String, Object> params, 
			Object[] beanArray) throws JRException {
		JRBeanArrayDataSource dataSource = new JRBeanArrayDataSource(beanArray);
		return JasperFillManager.fillReport(sourceFileName, params, dataSource);
	}
	
	/**
	 * 把JASPER文件转换为JasperPrint对象
	 * @param sourceFileName JASPER文件名，后缀名JASPER
	 * @param beanArray 欲填充到报表中的JavaBean数据
	 * @return 最后导出的JasperPrint对象
	 * @throws JRException
	 */
	public static JasperPrint getJasperPrint(String sourceFileName, Object[] beanArray) throws JRException {
		Map<String, Object> params = new HashMap<String, Object>();
		return getJasperPrint(sourceFileName, params, beanArray);
	}
	
	/**
	 * 把JASPER文件转换为JasperPrint对象
	 * @param inputStream JASPER文件输入流
	 * @param params 填充JASPER文件所需的参数值对
	 * @param conn 报表所需的数据库查询连接
	 * @return 最后导出的JasperPrint对象
	 * @throws JRException
	 */
	public static JasperPrint getJasperPrint(InputStream inputStream, Map<String, Object> params, 
	Connection conn) throws JRException {
	return JasperFillManager.fillReport(inputStream, params, conn);
	}

	/**
	 * 把JASPER文件转换为JasperPrint对象
	 * @param inputStream JASPER文件输入流
	 * @param conn 报表所需的数据库查询连接
	 * @return 最后导出的JasperPrint对象
	 * @throws JRException
	 */
	public static JasperPrint getJasperPrint(InputStream inputStream, Connection conn) throws JRException {
	Map<String, Object> params = new HashMap<String, Object>();
	return getJasperPrint(inputStream, params, conn);
	}

	/**
	 * 把JASPER文件转换为JasperPrint对象
	 * @param inputStream JASPER文件输入流
	 * @param params 填充JASPER文件所需的参数值对
	 * @param beanArray 欲填充到报表中的JavaBean数据
	 * @return 最后导出的JasperPrint对象
	 * @throws JRException
	 */
	public static JasperPrint getJasperPrint(InputStream inputStream, Map<String, Object> params, 
	Object[] beanArray) throws JRException {
	JRBeanArrayDataSource dataSource = new JRBeanArrayDataSource(beanArray);
	return JasperFillManager.fillReport(inputStream, params, dataSource);
	}
	/**
	 * 把JASPER文件转换为JasperPrint对象
	 * @param inputStream JASPER文件输入流
	 * @param beanArray 欲填充到报表中的JavaBean数据
	 * @return 最后导出的JasperPrint对象
	 * @throws JRException
	 */
	public static JasperPrint getJasperPrint(InputStream inputStream, Object[] beanArray) throws JRException {
	Map<String, Object> params = new HashMap<String, Object>();
	return getJasperPrint(inputStream, params, beanArray);
	}
}
