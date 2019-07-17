package com.huiju.srm.commons.utils;

/**
 * 报表导出类型枚举类
 * @author chensw
 */
public enum ReportFileType {

	PDF("pdf", "application/pdf"), 
	XLS("xls", "application/vnd.ms-excel"), 
	XLSX("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
	HTML("html", "text/html;charset=UTF-8"), 
	XML("xml", "text/xml;charset=UTF-8"), 
	RTF("rtf", "application/rtf"),
	DOCX("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");

	private String fileExtension;
	private String contentType;

	private ReportFileType(String fileExtension, String contentType) {
		this.fileExtension = fileExtension;
		this.contentType = contentType;
	}

	public String getFileExtension() {
		return this.fileExtension;
	}

	public String getContentType() {
		return this.contentType;
	}

}
