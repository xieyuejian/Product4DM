package com.huiju.srm.commons.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RequestMapping;

import com.huiju.module.mvc.controller.CloudController;
import com.huiju.module.util.StringUtils;

import net.sf.jasperreports.engine.JasperPrint;

/**
 * 导出报表Controller父类
 * @author caiwq
 *
 */
public abstract class CloudReportController extends CloudController {

	@Autowired
	private DataSource dataSource;
    
    /**
     * 自定义查询条件转换 </br>
     * map key 如:"IN_synErpState"等
     * @return Map<String, Object> params
     */
    public abstract Map<String,Object> buildCondition();
    
    
    protected String jasperFile() {
    	return "";
    }
    
	/**
	 * 导出报表
	 * 
	 * @return
	 * @throws SQLException 
	 */
    @RequestMapping(value = "/export")
	public void export(String reportFileType, String jasperFile, String exportFile, Boolean isDownLoad) throws Exception {
		Connection conn = dataSource.getConnection();

		String jasperFile2 = jasperFile();
		if(StringUtils.isNotBlank(jasperFile2)) {
			jasperFile = jasperFile2;
		}
		
		if(null == isDownLoad) {
			isDownLoad = true;
		}
		
		Locale.setDefault(new Locale(request.getLocale().getLanguage(), request.getLocale().getCountry()));
		ReportFileType reportType = ReportWebUtils.judgeFileType(reportFileType);
		InputStream inputStream = fetchTemplatePath(jasperFile);
		if (null == inputStream) {
			ReportWebUtils.closeDataSourceConn(conn);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/html");
			try {
				response.getWriter().write("导出模版加载失败！");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else {
			// 连接数据库查询并填充数据,转换为JasperPrint对象
			JasperPrint jasperPrint = ReportUtils.getJasperPrint(inputStream, getSearchParam(), conn);
			ReportWebUtils.closeDataSourceConn(conn);
			// 导出的文件名字默认跟模版名称一致
			if (StringUtils.isEmpty(exportFile)) {
				exportFile = jasperFile;
			}
			ReportWebUtils.dealResponse(response, jasperPrint, new String(exportFile.getBytes("gb2312"), "ISO8859-1"), reportType,isDownLoad);
		}
	}

	public Map<String, Object> getSearchParam() {
		// 获取从页面传过来的查询条件,格式如filter_id=1234;filter_name=报表
		Map<String, Object> params = buildParams();
		for (String key : params.keySet()) {
			String value = (String) params.get(key);
			if (StringUtils.isNotBlank(value)) {
				try {
					value = new String(value.getBytes("ISO_8859-1"), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} // 报表导出公共方法查询条件转码
			}
			params.put(key, value);
			// LIKE 处理
			if (key.contains("LIKE_") && StringUtils.isNotBlank(value)) {
				if (!value.startsWith("%") && !value.endsWith("%")) {
					params.put(key, "%" + value + "%");
				}
			}
		}

		Map<String, Object> buildParams = buildCondition();
		if (buildParams != null && !buildParams.isEmpty()) {
			params.putAll(buildParams);
		}
		return params;
	}
	
	/**
    * 获取模板路径
    * 
    * @param jasperFile
    * @return
	 * @throws IOException 
    */
   private InputStream fetchTemplatePath(String jasperFile) throws IOException {
//	   String path = request.getServletContext().getRealPath("/static/jasper/" + jasperFile + ".jasper");
	   Resource res = new ClassPathResource("/jasper/" + jasperFile + ".jasper");
	   
	   if(res.exists()) {
		   return res.getInputStream();
//		    res.getFile().getPath();
	   }
	   
//	   String path = ClassUtils.getDefaultClassLoader().getResource("").getPath();
//	   path = path + "/static/jasper/" + jasperFile + ".jasper";
       return null;
   }
}
