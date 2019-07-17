package com.huiju.srm.commons.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * 报表Web工具类
 * 
 * @author chensw
 * 
 */
public abstract class ReportWebUtils {

	/**
	 * 处理JasperPrint返回页面类型
	 * @param response HttpServletResponse
	 * @param jasperPrint 待处理的JasperPrint对象
	 * @param fileName 返回页面的文件命名，不带后缀名
	 * @param type 返回页面的文件类型，目前只支持：XLS(一般都兼容)、XLSX(2007以上版本)、PDF、RTF、XML、HTML
	 * @param isDownLoad 是否直接下载文件;true:下载，false:直接用浏览器打开(浏览器不支持打开的文件格式自动转为下载)
	 * @throws IOException
	 * @throws JRException 
	 */
	public static void dealResponse(HttpServletResponse response, JasperPrint jasperPrint, String fileName, 
			ReportFileType type, Boolean isDownLoad) throws IOException, JRException {
		//转换JasperPrint对象为byte数组
		byte[] bytes = getFileBytes(jasperPrint, type);
        if (bytes != null && bytes.length > 0) {
            ServletOutputStream sos = null;
            try {
                response.reset();
                response.setCharacterEncoding("UTF-8");
                response.setContentType(type.getContentType());
                //默认直接打开文件，如果浏览器不支持文件格式，则自动转为下载文件
                response.setHeader("Content-Disposition", "inline;filename=" + fileName + "." + type.getFileExtension());
                //是否直接下载文件
                if (isDownLoad) {
                	response.setHeader("Content-Disposition", "attachment;filename=" + fileName + "." + type.getFileExtension());
                }
                response.setContentLength(bytes.length);
                sos = response.getOutputStream();
                sos.write(bytes, 0, bytes.length);
                sos.flush();
                sos.close();
            } catch (IOException ex) {
            	throw new IOException(ex);
            } finally {
                try {
                    sos.close();
                } catch (IOException ex) {
                	ex.getStackTrace();
                }
            }
        }
	}
	/**
	 * 根据目标类型转换JasperPrint对象为byte数组
	 * @param jasperPrint JasperPrint对象
	 * @param type 目标类型,支持XLS、XLSX、PDF、RTF、HTML、XML格式
	 * @return byte数组
	 * @throws JRException 
	 */
    public static byte[] getFileBytes(JasperPrint jasperPrint, ReportFileType type) throws JRException {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        switch (type) {
        //生成PDF格式
        case PDF: {
        	ReportUtils.exportReportToPdfStream(jasperPrint, bo);
        	break;
        }
        //生成EXCEL格式
        case XLS: {
        	ReportUtils.exportReportToXlsStream(jasperPrint, bo);
        	break;
        }
        //生成EXCEL格式
        case XLSX: {
        	ReportUtils.exportReportToXlsxStream(jasperPrint, bo);
        	break;
        }
        //生成Rtf格式
        case RTF: {
        	ReportUtils.exportReportToRtfStream(jasperPrint, bo);
        	break;
        }
        //生成HTML格式
        case HTML: {
        	ReportUtils.exportReportToHtmlStream(jasperPrint, bo);
        	break;
        }
        //生成XML格式
        case XML: {
        	ReportUtils.exportReportToXmlStream(jasperPrint, bo);
        	break;
        }
        //生成DOCX格式
        case DOCX: {
            ReportUtils.exportReportToDocxStream(jasperPrint, bo);
            break;
        }
        default:
        	break;
        }
        return bo.toByteArray();
    }
    
    /**
     * 转换字符串为对应的ReportFileType对象，默认返回PDF类型
     * @param reportType 与ReportFileType对应的字符串名称，支持XLS、XLSX、PDF、RTF、HTML、XML，不区分大小写
     * @return ReportFileType
     */
    public static ReportFileType judgeFileType(String reportType) {
        if (("XLS").equalsIgnoreCase(reportType)) {
            return ReportFileType.XLS;
        } else if (("HTML").equalsIgnoreCase(reportType)) {
            return ReportFileType.HTML;
        } else if (("XML").equalsIgnoreCase(reportType)) {
            return ReportFileType.XML;
        } else if (("RTF").equalsIgnoreCase(reportType)) {
            return ReportFileType.RTF;
        } else if (("XLSX").equalsIgnoreCase(reportType)) {
        	return ReportFileType.XLSX;
        } else if (("DOCX").equalsIgnoreCase(reportType)) {
            return ReportFileType.DOCX;
        } else {
            return ReportFileType.PDF;
        }
    }
    
    /**
     * 获取数据库连接
     * @param jdbcName JNDI名称
     * @return Connection
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static Connection getDataSourceConn(String jdbcName) throws ClassNotFoundException, SQLException {
		DataSource ds = null;
		try {
			Context ctx = new InitialContext();
			ds = (DataSource) ctx.lookup(jdbcName);
			return ds.getConnection();
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return null;
	}

    /**
     * 关闭数据库连接
     * @param conn
     */
    public static void closeDataSourceConn(Connection conn) {
		try {
			if (conn != null)
				conn.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
}
