package com.huiju.srm.commons.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.huiju.module.i18n.MessageBundle;
import com.huiju.module.util.StringUtils;

/**
 * poi 常用工具
 * 
 * @author xufq\zhengjf
 */
@SuppressWarnings("deprecation")
public class PoiUtils {

	public static final int BUFFER = 1024;
	public static final String FILE_TYPE_XLSX = "xlsx";
	public static final String FILE_TYPE_XLS = "xls";
	/**
	 * 字体风格：微软雅黑，14号字体，黑色，加粗
	 */
	public static final int W_14_BOLD = 1;
	/**
	 * 字体风格：微软雅黑，14号字体，黑色，正常
	 */
	public static final int W_14_NORMAL = 2;
	/**
	 * 字体风格：微软雅黑，12号字体，黑色，加粗
	 */
	public static final int W_12_BOLD = 3;
	/**
	 * 字体风格：微软雅黑，12号字体，黑色，正常
	 */
	public static final int W_12_NORMAL = 4;
	/**
	 * 字体风格：微软雅黑，11号字体，黑色，加粗
	 */
	public static final int W_11_BOLD = 5;
	/**
	 * 字体风格：微软雅黑，11号字体，黑色，正常
	 */
	public static final int W_11_NORMAL = 6;
	/**
	 * 字体风格：微软雅黑，10号字体，黑色，加粗
	 */
	public static final int W_10_BOLD = 7;
	/**
	 * 字体风格：微软雅黑，10号字体，黑色，正常
	 */
	public static final int W_10_NORMAL = 8;
	/**
	 * 字体风格：微软雅黑，9号字体，黑色，加粗
	 */
	public static final int W_9_BOLD = 9;
	/**
	 * 字体风格：微软雅黑，9号字体，黑色，正常
	 */
	public static final int W_9_NORMAL = 10;
	/**
	 * 字体风格：宋体，14号字体，黑色，加粗
	 */
	public static final int S_14_BOLD = 1;
	/**
	 * 字体风格：宋体，14号字体，黑色，正常
	 */
	public static final int S_14_NORMAL = 2;
	/**
	 * 字体风格：宋体，12号字体，黑色，加粗
	 */
	public static final int S_12_BOLD = 3;
	/**
	 * 字体风格：宋体，12号字体，黑色，正常
	 */
	public static final int S_12_NORMAL = 4;
	/**
	 * 字体风格：宋体，11号字体，黑色，加粗
	 */
	public static final int S_11_BOLD = 5;
	/**
	 * 字体风格：宋体，11号字体，黑色，正常
	 */
	public static final int S_11_NORMAL = 6;
	/**
	 * 字体风格：宋体，10号字体，黑色，加粗
	 */
	public static final int S_10_BOLD = 7;
	/**
	 * 字体风格：宋体，10号字体，黑色，正常
	 */
	public static final int S_10_NORMAL = 8;
	/**
	 * 字体风格：宋体，9号字体，黑色，加粗
	 */
	public static final int S_9_BOLD = 9;
	/**
	 * 字体风格：宋体，9号字体，黑色，正常
	 */
	public static final int S_9_NORMAL = 10;
	/**
	 * 字体风格：垂直水平居中 无边框
	 */
	public static final int VC_AC_N = 1;
	/**
	 * 字体风格：垂直水平居中 有边框
	 */
	public static final int VC_AC_B = 2;
	/**
	 * 字体风格：垂直居中 无边框
	 */
	public static final int VC_N = 3;
	/**
	 * 字体风格：垂直居中 有边框
	 */
	public static final int VC_B = 4;
	/**
	 * 字体风格：垂直居中水平居右 有边框
	 */
	public static final int VC_AR_B = 5;
	/**
	 * 字体风格：垂直居中水平居右 无边框
	 */
	public static final int VC_AR_N = 6;
	/**
	 * 字体风格：垂直居中水平居左 有边框
	 */
	public static final int VC_AL_B = 7;
	/**
	 * 字体风格：垂直居中水平居左 无边框
	 */
	public static final int VC_AL_N = 8;
	/**
	 * 字体风格：默认
	 */
	public static final int DEFAULT = 1000000000;

	/**
	 * <p>
	 * methodName: getTemplateFont
	 * </p>
	 * <p>
	 * Description: 获取模板样式
	 * </p>
	 * 
	 * @param wb
	 * @return
	 */
	public static Font getTemplateFont(HSSFWorkbook wb, int fontType) {
		Font font = wb.createFont();
		font.setColor(HSSFColor.BLACK.index);
		switch (fontType) {
		case 1:// 字体风格：微软雅黑，12号字体，黑色，加粗
			font.setFontName("微软雅黑");
			font.setFontHeightInPoints((short) 14);
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			break;
		case 2:// 字体风格：微软雅黑，12号字体，黑色，正常
			font.setFontName("微软雅黑");
			font.setFontHeightInPoints((short) 14);
			font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
			break;
		case 3:// 字体风格：微软雅黑，12号字体，黑色，加粗
			font.setFontName("微软雅黑");
			font.setFontHeightInPoints((short) 12);
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			break;
		case 4:// 字体风格：微软雅黑，12号字体，黑色，加粗
			font.setFontName("微软雅黑");
			font.setFontHeightInPoints((short) 12);
			font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
			break;
		case 5:// 字体风格：微软雅黑，11号字体，黑色，加粗
			font.setFontName("微软雅黑");
			font.setFontHeightInPoints((short) 11);
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			break;
		case 6:// 字体风格：微软雅黑，11号字体，
			font.setFontName("微软雅黑");
			font.setFontHeightInPoints((short) 11);
			break;
		case 7:// 字体风格：微软雅黑，10号字体，黑色
			font.setFontName("微软雅黑");
			font.setFontHeightInPoints((short) 10);
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			break;
		case 8:// 字体风格：微软雅黑，10号字体，黑色
			font.setFontName("微软雅黑");
			font.setFontHeightInPoints((short) 10);
			break;
		case 9:// 字体风格：微软雅黑，9号字体，黑色
			font.setFontName("微软雅黑");
			font.setFontHeightInPoints((short) 9);
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			break;
		case 10:// 字体风格：微软雅黑，9号字体，黑色
			font.setFontName("微软雅黑");
			font.setFontHeightInPoints((short) 9);
			break;
		case 11:// 字体风格：宋体，12号字体，黑色，加粗
			font.setFontName("宋体");
			font.setFontHeightInPoints((short) 14);
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			break;
		case 12:// 字体风格：宋体，12号字体
			font.setFontName("宋体");
			font.setFontHeightInPoints((short) 14);
			break;
		case 13:// 字体风格：宋体，10号字体，黑色
			font.setFontName("宋体");
			font.setFontHeightInPoints((short) 12);
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			break;
		case 14:// 字体风格：宋体，10号字体，黑色
			font.setFontName("宋体");
			font.setFontHeightInPoints((short) 12);
			break;
		case 15:// 字体风格：宋体，9号字体，黑色
			font.setFontName("宋体");
			font.setFontHeightInPoints((short) 11);
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			break;
		case 16:// 字体风格：宋体，9号字体，黑色
			font.setFontName("宋体");
			font.setFontHeightInPoints((short) 11);
			break;
		case 17:// 字体风格：宋体，9号字体，黑色
			font.setFontName("宋体");
			font.setFontHeightInPoints((short) 10);
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			break;
		case 18:// 字体风格：宋体，9号字体，黑色
			font.setFontName("宋体");
			font.setFontHeightInPoints((short) 10);
			break;
		case 19:// 字体风格：宋体，9号字体，黑色
			font.setFontName("宋体");
			font.setFontHeightInPoints((short) 9);
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			break;
		case 20:// 字体风格：宋体，9号字体，黑色
			font.setFontName("宋体");
			font.setFontHeightInPoints((short) 9);
			break;
		default:
			break;
		}
		return font;
	}

	/**
	 * <p>
	 * methodName: getTemplateCellStyle
	 * </p>
	 * <p>
	 * Description: 获取字体垂直水平样式
	 * </p>
	 * 
	 * @param wb
	 * @param cellStyleType
	 * @param font
	 * @return
	 */
	public static CellStyle getTemplateCellStyle(HSSFWorkbook wb, int cellStyleType, Font font) {
		HSSFCellStyle style = wb.createCellStyle();
		switch (cellStyleType) {
		// 单元格格式一:水平居中，垂直居中
		case 1:
			style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			style.setFont(font);
			break;
		// 单元格格式二:水平居中，垂直居中，有边框
		case 2:
			style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			style.setBorderBottom(HSSFCellStyle.BORDER_THIN); // 下边框
			style.setBorderLeft(HSSFCellStyle.BORDER_THIN);// 左边框
			style.setBorderTop(HSSFCellStyle.BORDER_THIN);// 上边框
			style.setBorderRight(HSSFCellStyle.BORDER_THIN);// 右边框
			style.setFont(font);
			break;
		// 单元格格式三:垂直居中
		case 3:
			style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			style.setFont(font);
			break;
		case 4:
			style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			style.setBorderBottom(HSSFCellStyle.BORDER_THIN); // 下边框
			style.setBorderLeft(HSSFCellStyle.BORDER_THIN);// 左边框
			style.setBorderTop(HSSFCellStyle.BORDER_THIN);// 上边框
			style.setBorderRight(HSSFCellStyle.BORDER_THIN);// 右边框
			style.setFont(font);
			break;
		case 5:
			style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
			style.setBorderBottom(HSSFCellStyle.BORDER_THIN); // 下边框
			style.setBorderLeft(HSSFCellStyle.BORDER_THIN);// 左边框
			style.setBorderTop(HSSFCellStyle.BORDER_THIN);// 上边框
			style.setBorderRight(HSSFCellStyle.BORDER_THIN);// 右边框
			style.setFont(font);
			break;
		case 6:
			style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
			style.setFont(font);
			break;
		case 7:
			style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
			style.setBorderBottom(HSSFCellStyle.BORDER_THIN); // 下边框
			style.setBorderLeft(HSSFCellStyle.BORDER_THIN);// 左边框
			style.setBorderTop(HSSFCellStyle.BORDER_THIN);// 上边框
			style.setBorderRight(HSSFCellStyle.BORDER_THIN);// 右边框
			style.setFont(font);
			break;
		case 8:
			style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
			style.setFont(font);
			break;
		default:
			style.setBorderBottom(HSSFCellStyle.BORDER_NONE); // 下边框
			style.setBorderLeft(HSSFCellStyle.BORDER_NONE);// 左边框
			style.setBorderTop(HSSFCellStyle.BORDER_NONE);// 上边框
			style.setBorderRight(HSSFCellStyle.BORDER_NONE);// 右边框
			style.setFont(font);
			break;
		}
		return style;

	}

	/**
	 * 设置单元格 常用风格类型，特殊定义可在自己的代码自行添加
	 * 
	 * @param cellStyle 单元格样式
	 * @param alignment 水平对齐方式
	 * @param verticalAlignment 垂直对齐方式
	 * @param flag 是否自动换行
	 * @param bottom 底边框
	 * @param left 左边框
	 * @param right 右边框
	 * @param top 上边框
	 */
	public static void setCellStyle(HSSFCellStyle cellStyle, short alignment, short verticalAlignment, boolean flag, short bottom,
			short left, short right, short top) {
		// 指定单元格居中对齐
		cellStyle.setAlignment(alignment);
		// 指定单元格垂直居中对齐
		cellStyle.setVerticalAlignment(verticalAlignment);
		// 指定当单元格内容显示不下时自动换行
		cellStyle.setWrapText(flag);
		// 边框
		cellStyle.setBorderBottom(bottom);
		cellStyle.setBorderLeft(left);
		cellStyle.setBorderRight(right);
		cellStyle.setBorderTop(top);
	}

	/**
	 * 设置单元格字体
	 * 
	 * @param font 字体
	 * @param boldweight 加粗
	 * @param fontName 字体类型
	 * @param height 字体大小
	 * @param color 字体颜色
	 */
	public static void setFont(HSSFFont font, short boldweight, String fontName, short height, short color) {
		font.setBoldweight(boldweight);
		font.setFontName(fontName);
		font.setFontHeight(height);
		font.setColor(color);
	}

	/**
	 * 设置单元格字体
	 * 
	 * @param font 字体
	 * @param boldweight 加粗
	 * @param fontName 字体类型
	 * @param height 字体大小
	 */
	public static void setFont(HSSFFont font, short boldweight, String fontName, short height) {
		font.setBoldweight(boldweight);
		font.setFontName(fontName);
		font.setFontHeight(height);
	}

	/**
	 * 
	 * @param row 数据行
	 * @param rowCellIndex 当前行的某个单元格
	 * @param cellStyle 设置单元格样式
	 * @param rts 单元格值
	 */
	public static void cretaRowCell(HSSFRow row, int rowCellIndex, HSSFCellStyle cellStyle, RichTextString rts) {
		HSSFCell rowCell = row.createCell(rowCellIndex);
		rowCell.setCellStyle(cellStyle);
		rowCell.setCellValue(rts);
	}

	/**
	 * @param sheet 工作薄
	 * @param rowNums 起始合并行
	 * @param rowNume 结束和并行
	 * @param cellNums 开始合并列
	 * @param cellNume 结束合并列
	 * @param wb 工作簿对象
	 */
	public static void cellRange(Sheet sheet, int rowNums, int rowNume, int cellNums, int cellNume, HSSFWorkbook wb) {
		CellRangeAddress wfCell = new CellRangeAddress(rowNums, rowNume, cellNums, cellNume);
		sheet.addMergedRegion(wfCell);
		RegionUtil.setBorderBottom(HSSFCellStyle.BORDER_THIN, wfCell, sheet, wb);
		RegionUtil.setBorderTop(HSSFCellStyle.BORDER_THIN, wfCell, sheet, wb);
		RegionUtil.setBorderLeft(HSSFCellStyle.BORDER_THIN, wfCell, sheet, wb);
		RegionUtil.setBorderRight(HSSFCellStyle.BORDER_THIN, wfCell, sheet, wb);
	}

	/**
	 * 设置单元格的值
	 * 
	 * @param wb 工作对象
	 * @param sheet 工作簿
	 * @param rowMap 每一行对应的值
	 */
	public static void setCellValue(HSSFWorkbook wb, HSSFSheet sheet, Map<Integer, Object> rowMap) {
		// 设置无边框单元格样式
		HSSFCellStyle cellStyle = wb.createCellStyle();
		PoiUtils.setCellStyle(cellStyle, HSSFCellStyle.ALIGN_CENTER, HSSFCellStyle.VERTICAL_CENTER, true, HSSFCellStyle.BORDER_NONE,
				HSSFCellStyle.BORDER_NONE, HSSFCellStyle.BORDER_NONE, HSSFCellStyle.BORDER_NONE);
		// 设置字体
		HSSFFont font = wb.createFont();
		PoiUtils.setFont(font, HSSFFont.BOLDWEIGHT_NORMAL, "宋体", (short) 200);
		cellStyle.setFont(font);
		setCellValue(wb, sheet, rowMap, cellStyle);
	}

	/**
	 * 设置单元格的值
	 * 
	 * @param wb 工作对象
	 * @param sheet 工作簿
	 * @param rowMap 每一行对应的值
	 * @param cellStyle 单元格样式
	 */
	public static void setCellValue(HSSFWorkbook wb, HSSFSheet sheet, Map<Integer, Object> rowMap, HSSFCellStyle cellStyle) {
		Map<Integer, HSSFCellStyle> styleMap = new HashMap<Integer, HSSFCellStyle>();
		styleMap.put(0, cellStyle);
		setCellValue(wb, sheet, rowMap, styleMap);
	}

	/**
	 * 设置单元格的值
	 * 
	 * @param wb 工作对象
	 * @param sheet 工作簿
	 * @param rowMap 每一行对应的值
	 * @param cellStyleMap 每一行的单元格样式
	 */
	@SuppressWarnings("unchecked")
	public static void setCellValueStyle(HSSFWorkbook wb, HSSFSheet sheet, Map<Integer, Object> rowMap,
			Map<Integer, HSSFCellStyle> cellStyleMap, Boolean setValueFlag) {
		// 设置无边框单元格样式
		HSSFCellStyle textStyle = getTextStyle(wb);// 文本样式
		HSSFCellStyle dateStyle = getDateStyle(wb);// 日期样式
		HSSFCellStyle integerStyle = getIntegerStyle(wb);// 整数
		HSSFCellStyle doubleStyle = getDoubleStyle(wb);// double

		// 定义字体
		HSSFFont redFont = wb.createFont();
		redFont.setColor(HSSFColor.RED.index);// 红色
		HSSFFont blueFont = wb.createFont();
		PoiUtils.setFont(blueFont, HSSFFont.BOLDWEIGHT_NORMAL, "宋体", (short) 200, HSSFColor.BLUE.index);

		for (Integer rowIndex : rowMap.keySet()) {
			if (rowMap.get(rowIndex) instanceof String[]) {
				HSSFCellStyle cellStyle = cellStyleMap.get(rowIndex) == null ? textStyle : cellStyleMap.get(rowIndex);
				HSSFRow row = sheet.createRow(rowIndex);
				String[] arr = (String[]) rowMap.get(rowIndex);
				for (int i = 0; i < arr.length; i++) {
					HSSFCell cell = row.createCell(i);
					cell.setCellStyle(cellStyle);
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					sheet.setDefaultColumnStyle(i, cellStyle);
					if (arr[i] != null && arr[i].startsWith("*")) {
						// 创建富文本字符串
						HSSFRichTextString richString = new HSSFRichTextString(arr[i]);
						richString.applyFont(arr[i].indexOf("*"), arr[i].indexOf("*") + 1, redFont);
						richString.applyFont(arr[i].indexOf("*") + 1, arr[i].length(), blueFont);
						cell.setCellValue(richString);
					} else {
						cell.setCellValue(arr[i]);
					}
				}
			} else if (rowMap.get(rowIndex) instanceof Integer[]) {
				HSSFCellStyle cellStyle = cellStyleMap.get(rowIndex) == null ? integerStyle : cellStyleMap.get(rowIndex);
				HSSFRow row = sheet.createRow(rowIndex);
				Integer[] arr = (Integer[]) rowMap.get(rowIndex);
				for (int i = 0; i < arr.length; i++) {
					HSSFCell cell = row.createCell(i);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(arr[i]);
				}
			} else if (rowMap.get(rowIndex) instanceof List<?>) {
				List<Object> list = (List<Object>) rowMap.get(rowIndex);
				HSSFRow row = sheet.createRow(rowIndex);
				for (int i = 0; i < list.size(); i++) {
					HSSFCell cell = row.createCell(i);
					if (list.get(i) instanceof Integer) {
						HSSFCellStyle cellStyle = cellStyleMap.get(rowIndex) == null ? integerStyle : cellStyleMap.get(rowIndex);
						if (setValueFlag) {
							Integer value = (Integer) list.get(i);
							cell.setCellValue(value);
						}
						cell.setCellStyle(cellStyle);
						sheet.setDefaultColumnStyle(i, cellStyle);
					} else if (list.get(i) instanceof String) {
						HSSFCellStyle cellStyle = cellStyleMap.get(rowIndex) == null ? textStyle : cellStyleMap.get(rowIndex);
						if (setValueFlag) {
							String value = (String) list.get(i);
							cell.setCellValue(value);
						}
						cell.setCellStyle(cellStyle);
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						sheet.setDefaultColumnStyle(i, cellStyle);
					} else if (list.get(i) instanceof Double) {
						HSSFCellStyle cellStyle = cellStyleMap.get(rowIndex) == null ? doubleStyle : cellStyleMap.get(rowIndex);
						if (setValueFlag) {
							Double value = (Double) list.get(i);
							cell.setCellValue(value);
						}
						cell.setCellStyle(cellStyle);
						sheet.setDefaultColumnStyle(i, cellStyle);
					} else if (list.get(i) instanceof Calendar) {
						HSSFCellStyle cellStyle = cellStyleMap.get(rowIndex) == null ? dateStyle : cellStyleMap.get(rowIndex);
						if (setValueFlag) {
							Calendar value = (Calendar) list.get(i);
							cell.setCellValue(value);
						}
						cell.setCellStyle(cellStyle);
						sheet.setDefaultColumnStyle(i, cellStyle);
					} else if (list.get(i) instanceof Date) {
						HSSFCellStyle cellStyle = cellStyleMap.get(rowIndex) == null ? dateStyle : cellStyleMap.get(rowIndex);
						if (setValueFlag) {
							Date value = (Date) list.get(i);
							cell.setCellValue(value);
						}
						cell.setCellStyle(cellStyle);
						sheet.setDefaultColumnStyle(i, cellStyle);
					} else if (list.get(i) != null) {
						HSSFCellStyle cellStyle = cellStyleMap.get(rowIndex) == null ? dateStyle : cellStyleMap.get(rowIndex);
						if (setValueFlag) {
							String value = list.get(i).toString();
							cell.setCellValue(value);
						}
						cell.setCellStyle(cellStyle);
						sheet.setDefaultColumnStyle(i, cellStyle);
					}
				}
			}
		}
	}

	/**
	 * <p>
	 * methodName: setTemplateCellValue
	 * </p>
	 * <p>
	 * Description: 参照统一规范进行开发的方法
	 * </p>
	 * 
	 * @param wb 工作book
	 * @param sheet 工作脚簿
	 * @param otherMap 标题 列表头等信息
	 * @param rowMap 每一行的数据
	 * @param cellStyleMap 自定义样式
	 * @param setValueFlag 是否需要设置值
	 */
	public static void setTemplateCellValue(HSSFWorkbook wb, HSSFSheet sheet, Map<String, Object> otherMap, Map<Integer, Object> rowMap,
			Map<Integer, HSSFCellStyle> cellStyleMap, Boolean setValueFlag) {
		if (otherMap.containsKey("sheetmame") && otherMap.get("sheetmame") != null) {
			wb.setSheetName(0, otherMap.get("sheetmame").toString());
		}
		// 统一规范字体微软雅黑11号
		Font font = getTemplateFont(wb, PoiUtils.W_11_NORMAL);
		// 设置无边框单元格样式
		CellStyle textStyle = getTemplateTextStyle(wb, font);// 文本样式
		CellStyle dateStyle = getTemplateDateStyle(wb, font);// 日期样式
		CellStyle integerStyle = getTemplateIntegerStyle(wb, font);// 整数
		CellStyle doubleStyle = getTemplateDoubleStyle(wb, font, 2);// double

		// 设置标题
		if (otherMap.containsKey("title")) {
			setTemplateTitle(wb, sheet, otherMap);
		}

		// 设置列头
		if (otherMap.containsKey("hearder")) {
			setTemplateHearder(wb, sheet, otherMap);
		}

		for (Integer rowIndex : rowMap.keySet()) {
			if (rowMap.get(rowIndex) instanceof String[]) {
				CellStyle cellStyle = cellStyleMap.get(rowIndex) == null ? textStyle : cellStyleMap.get(rowIndex);
				HSSFRow row = sheet.createRow(rowIndex);
				String[] arr = (String[]) rowMap.get(rowIndex);
				for (int i = 0; i < arr.length; i++) {
					HSSFCell cell = row.createCell(i);
					cell.setCellValue(arr[i]);
					cell.setCellStyle(cellStyle);
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					sheet.setDefaultColumnStyle(i, cellStyle);
				}
			} else if (rowMap.get(rowIndex) instanceof Integer[]) {
				CellStyle cellStyle = cellStyleMap.get(rowIndex) == null ? integerStyle : cellStyleMap.get(rowIndex);
				HSSFRow row = sheet.createRow(rowIndex);
				Integer[] arr = (Integer[]) rowMap.get(rowIndex);
				for (int i = 0; i < arr.length; i++) {
					HSSFCell cell = row.createCell(i);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(arr[i]);
				}
			} else if (rowMap.get(rowIndex) instanceof List<?>) {
				List<Object> list = (List<Object>) rowMap.get(rowIndex);
				HSSFRow row = sheet.createRow(rowIndex);
				for (int i = 0; i < list.size(); i++) {
					HSSFCell cell = row.createCell(i);
					if (list.get(i) instanceof Integer) {
						CellStyle cellStyle = cellStyleMap.get(rowIndex) == null ? integerStyle : cellStyleMap.get(rowIndex);
						if (setValueFlag) {
							Integer value = (Integer) list.get(i);
							cell.setCellValue(value);
						}
						cell.setCellStyle(cellStyle);
						sheet.setDefaultColumnStyle(i, cellStyle);
					} else if (list.get(i) instanceof String) {
						CellStyle cellStyle = cellStyleMap.get(rowIndex) == null ? textStyle : cellStyleMap.get(rowIndex);
						if (setValueFlag) {
							String value = (String) list.get(i);
							cell.setCellValue(value);
						}
						cell.setCellStyle(cellStyle);
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						sheet.setDefaultColumnStyle(i, cellStyle);
					} else if (list.get(i) instanceof Double) {
						CellStyle cellStyle = cellStyleMap.get(rowIndex) == null ? doubleStyle : cellStyleMap.get(rowIndex);
						if (setValueFlag) {
							Double value = (Double) list.get(i);
							cell.setCellValue(value);
						}
						cell.setCellStyle(cellStyle);
						sheet.setDefaultColumnStyle(i, cellStyle);
					} else if (list.get(i) instanceof BigDecimal) {
						CellStyle cellStyle = cellStyleMap.get(rowIndex) == null ? doubleStyle : cellStyleMap.get(rowIndex);
						if (setValueFlag) {
							if (list.get(i) != null) {
								BigDecimal value = (BigDecimal) list.get(i);
								cell.setCellValue(value.doubleValue());
							}
						}
						cell.setCellStyle(cellStyle);
						sheet.setDefaultColumnStyle(i, cellStyle);
					} else if (list.get(i) instanceof Calendar) {
						CellStyle cellStyle = cellStyleMap.get(rowIndex) == null ? dateStyle : cellStyleMap.get(rowIndex);
						if (setValueFlag) {
							Calendar value = (Calendar) list.get(i);
							cell.setCellValue(value);
						}
						cell.setCellStyle(cellStyle);
						sheet.setDefaultColumnStyle(i, cellStyle);
					} else if (list.get(i) instanceof Date) {
						CellStyle cellStyle = cellStyleMap.get(rowIndex) == null ? dateStyle : cellStyleMap.get(rowIndex);
						if (setValueFlag) {
							Date value = (Date) list.get(i);
							cell.setCellValue(value);
						}
						cell.setCellStyle(cellStyle);
						sheet.setDefaultColumnStyle(i, cellStyle);
					}
				}
			}
		}
	}

	/**
	 * <p>
	 * methodName: setTitle
	 * </p>
	 * <p>
	 * Description: 设置标题
	 * </p>
	 * 
	 * @param wb 工作book
	 * @param sheet 工作脚簿
	 * @param otherMap 标题 列表头等信息
	 */
	public static void setTemplateTitle(HSSFWorkbook wb, HSSFSheet sheet, Map<String, Object> otherMap) {
		HSSFRow row = sheet.createRow(0);
		Font templateFont = getTemplateFont(wb, PoiUtils.W_14_BOLD);
		CellStyle templateCellStyle = getTemplateCellStyle(wb, PoiUtils.VC_AC_N, templateFont);
		// 设置列头
		if (otherMap.containsKey("hearder") && otherMap.get("hearder") != null) {
			Object object = otherMap.get("hearder");
			if (object instanceof String[]) {
				String[] arr = (String[]) object;
				if (arr.length > 1) {
					int hearderLength = arr.length;
					// 合并列
					cellRange(sheet, 0, 0, 0, hearderLength - 1, wb);
					HSSFCell cell = row.createCell(0);
					if (otherMap.get("title") != null) {
						cell.setCellValue(otherMap.get("title").toString());
						cell.setCellStyle(templateCellStyle);
					}
					for (int i = 1; i <= hearderLength; i++) {
						HSSFCell cell1 = row.createCell(i);
						cell1.setCellValue("");
					}
				} else {
					HSSFCell cell = row.createCell(0);
					if (otherMap.get("title") != null) {
						cell.setCellValue(otherMap.get("title").toString());
						cell.setCellStyle(templateCellStyle);
					}
				}
			}
		} else {
			HSSFCell cell = row.createCell(0);
			if (otherMap.get("title") != null) {
				cell.setCellValue(otherMap.get("title").toString());
				cell.setCellStyle(templateCellStyle);
			}
		}
	}

	/**
	 * <p>
	 * methodName: setTitle
	 * </p>
	 * <p>
	 * Description: 设置列头
	 * </p>
	 * 
	 * @param wb 工作book
	 * @param sheet 工作脚簿
	 * @param otherMap 标题 列表头等信息
	 */
	public static void setTemplateHearder(HSSFWorkbook wb, HSSFSheet sheet, Map<String, Object> otherMap) {
		HSSFRow row = null;
		if (otherMap.containsKey("title")) {
			row = sheet.createRow(1);
		} else {
			row = sheet.createRow(0);
		}
		Font templateFont = getTemplateFont(wb, PoiUtils.W_11_BOLD);
		Font redFont = getTemplateFont(wb, PoiUtils.W_11_BOLD);
		redFont.setColor(HSSFColor.RED.index);
		CellStyle templateCellStyle = getTemplateCellStyle(wb, PoiUtils.DEFAULT, templateFont);
		if (otherMap.containsKey("hearder") && otherMap.get("hearder") != null) {
			Object object = otherMap.get("hearder");
			if (object instanceof String[]) {
				String[] arr = (String[]) object;
				int i = 0;
				for (String hearder : arr) {
					HSSFCell cell = row.createCell(i);
					cell.setCellStyle(templateCellStyle);
					if (hearder != null && hearder.startsWith("*")) {
						// 创建富文本字符串
						HSSFRichTextString richString = new HSSFRichTextString(hearder);
						richString.applyFont(hearder.indexOf("*"), hearder.indexOf("*") + 1, redFont);
						richString.applyFont(hearder.indexOf("*") + 1, hearder.length(), templateFont);
						cell.setCellValue(richString);
					} else {
						cell.setCellValue(hearder);
					}
					i++;
				}
			}
		}
	}

	/**
	 * 设置单元格的值
	 * 
	 * @param wb 工作对象
	 * @param sheet 工作簿
	 * @param rowMap 每一行对应的值
	 * @param cellStyleMap 每一行的单元格样式
	 */
	public static void setCellValue(HSSFWorkbook wb, HSSFSheet sheet, Map<Integer, Object> rowMap,
			Map<Integer, HSSFCellStyle> cellStyleMap) {
		setCellValueStyle(wb, sheet, rowMap, cellStyleMap, true);
	}

	/**
	 * <p>
	 * methodName: getTemplateTextStyle
	 * </p>
	 * <p>
	 * Description: 获取文本规范样式
	 * </p>
	 * 
	 * @param wb
	 * @return
	 */
	public static CellStyle getTemplateTextStyle(HSSFWorkbook wb, Font font) {
		// 设置无边框单元格样式
		if (font == null) {
			font = getTemplateFont(wb, PoiUtils.W_11_NORMAL);
		}
		CellStyle textStyle = getTemplateCellStyle(wb, PoiUtils.VC_N, font);
		// 设置列的格式
		HSSFDataFormat format = wb.createDataFormat();
		textStyle.setDataFormat(format.getFormat("@"));
		return textStyle;
	}

	/**
	 * 获取cell文本样式
	 * 
	 * @param wb
	 * @return
	 */
	public static HSSFCellStyle getTextStyle(HSSFWorkbook wb) {
		// 设置无边框单元格样式
		HSSFCellStyle textStyle = wb.createCellStyle();
		HSSFDataFormat format = wb.createDataFormat();
		PoiUtils.setCellStyle(textStyle, HSSFCellStyle.ALIGN_CENTER, HSSFCellStyle.VERTICAL_CENTER, true, HSSFCellStyle.BORDER_NONE,
				HSSFCellStyle.BORDER_NONE, HSSFCellStyle.BORDER_NONE, HSSFCellStyle.BORDER_NONE);
		// 设置字体
		HSSFFont fontBlue = wb.createFont();
		PoiUtils.setFont(fontBlue, HSSFFont.BOLDWEIGHT_NORMAL, "宋体", (short) 200);
		// 设置列的格式
		textStyle.setDataFormat(format.getFormat("@"));
		textStyle.setFont(fontBlue);
		return textStyle;
	}

	/**
	 * <p>
	 * methodName: getTemplateDateStyle
	 * </p>
	 * <p>
	 * Description: 获取日期类型文本规范样式
	 * </p>
	 * 
	 * @param wb
	 * @return
	 */
	public static CellStyle getTemplateDateStyle(HSSFWorkbook wb, Font font) {
		// 设置无边框单元格样式
		if (font == null) {
			font = getTemplateFont(wb, PoiUtils.W_11_NORMAL);
		}
		CellStyle textStyle = getTemplateCellStyle(wb, PoiUtils.VC_AL_N, font);
		HSSFDataFormat format = wb.createDataFormat();
		// 设置列的格式
		textStyle.setDataFormat(format.getFormat("yyyy-mm-dd"));
		return textStyle;
	}

	/**
	 * 获取cell日期样式
	 * 
	 * @param wb
	 * @return
	 */
	public static HSSFCellStyle getDateStyle(HSSFWorkbook wb) {
		// 设置无边框单元格样式
		HSSFCellStyle dateStyle = wb.createCellStyle();
		HSSFDataFormat format = wb.createDataFormat();
		PoiUtils.setCellStyle(dateStyle, HSSFCellStyle.ALIGN_CENTER, HSSFCellStyle.VERTICAL_CENTER, true, HSSFCellStyle.BORDER_NONE,
				HSSFCellStyle.BORDER_NONE, HSSFCellStyle.BORDER_NONE, HSSFCellStyle.BORDER_NONE);
		// 设置字体
		HSSFFont fontBlue = wb.createFont();
		PoiUtils.setFont(fontBlue, HSSFFont.BOLDWEIGHT_NORMAL, "宋体", (short) 200);
		// 设置列的格式
		dateStyle.setDataFormat(format.getFormat("yyyy/mm/dd"));
		dateStyle.setFont(fontBlue);
		return dateStyle;
	}

	/**
	 * <p>
	 * methodName: getTemplateDoubleStyle
	 * </p>
	 * <p>
	 * Description: 获取cellDouble样式保留两位小数
	 * </p>
	 * 
	 * @param wb
	 * @param font
	 * @param point 小数点位数
	 * @return
	 */
	public static CellStyle getTemplateDoubleStyle(HSSFWorkbook wb, Font font, int point) {
		// 设置无边框单元格样式
		if (font == null) {
			font = getTemplateFont(wb, PoiUtils.W_11_NORMAL);
		}
		CellStyle textStyle = getTemplateCellStyle(wb, PoiUtils.VC_AR_N, font);
		// 设置列的格式
		if (point > 0) {
			StringBuffer pointStr = new StringBuffer();
			pointStr.append("0.");
			for (int i = 0; i < point; i++) {
				pointStr.append("0");
			}
			if (pointStr.length() > 0) {
				textStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat(pointStr.toString()));
			}
		} else {
			textStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
		}
		return textStyle;
	}

	/**
	 * 获取cellDouble样式保留两位小数
	 * 
	 * @param wb
	 * @return
	 */
	public static HSSFCellStyle getDoubleStyle(HSSFWorkbook wb) {
		// 设置无边框单元格样式
		HSSFCellStyle doubleStyle = wb.createCellStyle();
		PoiUtils.setCellStyle(doubleStyle, HSSFCellStyle.ALIGN_CENTER, HSSFCellStyle.VERTICAL_CENTER, true, HSSFCellStyle.BORDER_NONE,
				HSSFCellStyle.BORDER_NONE, HSSFCellStyle.BORDER_NONE, HSSFCellStyle.BORDER_NONE);
		// 设置字体
		HSSFFont fontBlue = wb.createFont();
		PoiUtils.setFont(fontBlue, HSSFFont.BOLDWEIGHT_NORMAL, "宋体", (short) 200);
		// 设置列的格式
		doubleStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
		doubleStyle.setFont(fontBlue);
		return doubleStyle;
	}

	/**
	 * <p>
	 * methodName: getTemplateIntegerStyle
	 * </p>
	 * <p>
	 * Description: 获取整数型文本样式
	 * </p>
	 * 
	 * @param wb
	 * @param font
	 * @return
	 */
	public static CellStyle getTemplateIntegerStyle(HSSFWorkbook wb, Font font) {
		if (font == null) {
			font = getTemplateFont(wb, PoiUtils.W_11_NORMAL);
		}
		// 设置无边框单元格样式
		CellStyle textStyle = getTemplateCellStyle(wb, PoiUtils.VC_AR_N, font);
		// 设置列的格式
		textStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0"));
		return textStyle;
	}

	/**
	 * 获取cellInteger样式保无小数
	 * 
	 * @param wb
	 * @return
	 */
	public static HSSFCellStyle getIntegerStyle(HSSFWorkbook wb) {
		// 设置无边框单元格样式
		HSSFCellStyle integerStyle = wb.createCellStyle();
		PoiUtils.setCellStyle(integerStyle, HSSFCellStyle.ALIGN_CENTER, HSSFCellStyle.VERTICAL_CENTER, true, HSSFCellStyle.BORDER_NONE,
				HSSFCellStyle.BORDER_NONE, HSSFCellStyle.BORDER_NONE, HSSFCellStyle.BORDER_NONE);
		// 设置字体
		HSSFFont fontBlue = wb.createFont();
		PoiUtils.setFont(fontBlue, HSSFFont.BOLDWEIGHT_NORMAL, "宋体", (short) 200);
		// 设置列的格式
		integerStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0"));
		integerStyle.setFont(fontBlue);
		return integerStyle;
	}

	/**
	 * <p>
	 * methodName: getTemplateBigDecimalStyle
	 * </p>
	 * <p>
	 * Description: 获取cell货币格式样式
	 * </p>
	 * 
	 * @param wb
	 * @param font
	 * @return
	 */
	public static CellStyle getTemplateBigDecimalStyle(HSSFWorkbook wb, Font font) {
		if (font == null) {
			font = getTemplateFont(wb, PoiUtils.W_11_NORMAL);
		}
		// 设置无边框单元格样式
		CellStyle textStyle = getTemplateCellStyle(wb, PoiUtils.VC_AR_N, font);
		HSSFDataFormat format = wb.createDataFormat();
		// 设置列的格式
		textStyle.setDataFormat(format.getFormat("?#,##0"));
		return textStyle;
	}

	/**
	 * 获取cell货币格式样式
	 * 
	 * @param wb
	 * @return
	 */
	public static HSSFCellStyle getBigDecimalStyle(HSSFWorkbook wb) {
		// 设置无边框单元格样式
		HSSFCellStyle bigDecimalStyle = wb.createCellStyle();
		PoiUtils.setCellStyle(bigDecimalStyle, HSSFCellStyle.ALIGN_CENTER, HSSFCellStyle.VERTICAL_CENTER, true, HSSFCellStyle.BORDER_NONE,
				HSSFCellStyle.BORDER_NONE, HSSFCellStyle.BORDER_NONE, HSSFCellStyle.BORDER_NONE);
		HSSFDataFormat format = wb.createDataFormat();
		// 设置字体
		HSSFFont fontBlue = wb.createFont();
		PoiUtils.setFont(fontBlue, HSSFFont.BOLDWEIGHT_NORMAL, "宋体", (short) 200);
		// 设置列的格式
		bigDecimalStyle.setDataFormat(format.getFormat("?#,##0"));
		bigDecimalStyle.setFont(fontBlue);
		return bigDecimalStyle;
	}

	/**
	 * <p>
	 * methodName: getTemplateBuiltinStyle
	 * </p>
	 * <p>
	 * Description: 获取cell百分比格式样式
	 * </p>
	 * 
	 * @param wb
	 * @param font
	 * @return
	 */
	public static CellStyle getTemplateBuiltinStyle(HSSFWorkbook wb, Font font) {
		// 设置无边框单元格样式
		if (font == null) {
			font = getTemplateFont(wb, PoiUtils.W_11_NORMAL);
		}
		// 设置无边框单元格样式
		CellStyle textStyle = getTemplateCellStyle(wb, PoiUtils.VC_AR_N, font);
		HSSFDataFormat format = wb.createDataFormat();
		// 设置列的格式
		textStyle.setDataFormat(format.getFormat("?#,##0"));
		return textStyle;
	}

	/**
	 * 获取cell百分比格式样式
	 * 
	 * @param wb
	 * @return
	 */
	public static HSSFCellStyle getBuiltinStyle(HSSFWorkbook wb) {
		// 设置无边框单元格样式
		HSSFCellStyle builtinStyle = wb.createCellStyle();
		PoiUtils.setCellStyle(builtinStyle, HSSFCellStyle.ALIGN_CENTER, HSSFCellStyle.VERTICAL_CENTER, true, HSSFCellStyle.BORDER_NONE,
				HSSFCellStyle.BORDER_NONE, HSSFCellStyle.BORDER_NONE, HSSFCellStyle.BORDER_NONE);
		HSSFDataFormat format = wb.createDataFormat();
		// 设置字体
		HSSFFont fontBlue = wb.createFont();
		PoiUtils.setFont(fontBlue, HSSFFont.BOLDWEIGHT_NORMAL, "宋体", (short) 200);
		// 设置列的格式
		builtinStyle.setDataFormat(format.getFormat("?#,##0"));
		builtinStyle.setFont(fontBlue);
		return builtinStyle;
	}

	/**
	 * <p>
	 * methodName: getTemplateDbNumStyle
	 * </p>
	 * <p>
	 * Description:获取cell中文大写样式
	 * </p>
	 * 
	 * @param wb
	 * @param font
	 * @return
	 */
	public static CellStyle getTemplateDbNumStyle(HSSFWorkbook wb, Font font) {
		if (font == null) {
			font = getTemplateFont(wb, PoiUtils.W_11_NORMAL);
		}
		// 设置无边框单元格样式
		CellStyle textStyle = getTemplateCellStyle(wb, PoiUtils.VC_AR_N, font);
		HSSFDataFormat format = wb.createDataFormat();
		// 设置列的格式
		textStyle.setDataFormat(format.getFormat("[DbNum2][$-804]0"));
		return textStyle;
	}

	/**
	 * 获取cell中文大写样式
	 * 
	 * @param wb
	 * @return
	 */
	public static HSSFCellStyle getDbNumStyle(HSSFWorkbook wb) {
		// 设置无边框单元格样式
		HSSFCellStyle dbNumStyle = wb.createCellStyle();
		PoiUtils.setCellStyle(dbNumStyle, HSSFCellStyle.ALIGN_CENTER, HSSFCellStyle.VERTICAL_CENTER, true, HSSFCellStyle.BORDER_NONE,
				HSSFCellStyle.BORDER_NONE, HSSFCellStyle.BORDER_NONE, HSSFCellStyle.BORDER_NONE);
		HSSFDataFormat format = wb.createDataFormat();
		// 设置字体
		HSSFFont fontBlue = wb.createFont();
		PoiUtils.setFont(fontBlue, HSSFFont.BOLDWEIGHT_NORMAL, "宋体", (short) 200);
		// 设置列的格式
		dbNumStyle.setDataFormat(format.getFormat("[DbNum2][$-804]0"));
		dbNumStyle.setFont(fontBlue);
		return dbNumStyle;
	}

	/**
	 * 设置某些列的值只能输入预制的数据,显示下拉框.
	 * 
	 * @param sheet 要设置的sheet.
	 * @param textlist 下拉框显示的内容
	 * @param firstRow 开始行
	 * @param endRow 结束行
	 * @param firstCol 开始列
	 * @param endCol 结束列
	 * @return 设置好的sheet.
	 */
	public static HSSFSheet setHSSFValidation(HSSFSheet sheet, String[] textlist, int firstRow, int endRow, int firstCol, int endCol) {
		// 加载下拉列表内容
		DVConstraint constraint = DVConstraint.createExplicitListConstraint(textlist);
		// 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
		CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);
		// 数据有效性对象
		HSSFDataValidation data_validation_list = new HSSFDataValidation(regions, constraint);
		sheet.addValidationData(data_validation_list);
		return sheet;
	}

	/**
	 * 设置单元格上提示
	 * 
	 * @param sheet 要设置的sheet.
	 * @param promptTitle 标题
	 * @param promptContent 内容
	 * @param firstRow 开始行
	 * @param endRow 结束行
	 * @param firstCol 开始列
	 * @param endCol 结束列
	 * @return 设置好的sheet.
	 */
	public static HSSFSheet setHSSFPrompt(HSSFSheet sheet, String promptTitle, String promptContent, int firstRow, int endRow, int firstCol,
			int endCol) {
		// 构造constraint对象
		DVConstraint constraint = DVConstraint.createCustomFormulaConstraint("BB1");
		// 四个参数分别是：起始行、终止行、起始列、终止列
		CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);
		// 数据有效性对象
		HSSFDataValidation data_validation_view = new HSSFDataValidation(regions, constraint);
		data_validation_view.createPromptBox(promptTitle, promptContent);
		sheet.addValidationData(data_validation_view);
		return sheet;
	}

	/**
	 * 
	 * 去掉字符串右边的空格
	 * 
	 * @param str 要处理的字符串
	 * @return 处理后的字符串
	 */
	public static String rightTrim(String str) {
		if (str == null) {
			return "";
		}
		int length = str.length();
		for (int i = length - 1; i >= 0; i--) {
			if (str.charAt(i) != 0x20) {
				break;
			}
			length--;
		}
		return str.substring(0, length);
	}

	/**
	 * 
	 * 读取Excel的内容，List为行集合，list里面的元素为每个行对象对应map类型
	 * 
	 * @param file 读取数据的源Excel
	 * @param ignoreRows 读取数据忽略的行数，比喻行头不需要读入 忽略的行数为1
	 * @return 读出的Excel中数据的内容
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static List<Map<Integer, Object>> getData(File file, int ignoreRows, String fileType) throws FileNotFoundException, IOException {
		if (FILE_TYPE_XLSX.equals(fileType)) {
			return getData4Xlsx(file, ignoreRows);
		} else {
			return getData4Xls(file, ignoreRows);
		}
	}

	private static List<Map<Integer, Object>> getData4Xlsx(File file, int ignoreRows) throws FileNotFoundException, IOException {
		List<Map<Integer, Object>> list = new ArrayList<Map<Integer, Object>>();
		// int rowSize = 0;
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));

		// 打开HSSFWorkbook
		// POIFSFileSystem fs = new POIFSFileSystem(in);
		XSSFWorkbook wb = new XSSFWorkbook(in);
		XSSFCell cell = null;
		for (int sheetIndex = 0; sheetIndex < wb.getNumberOfSheets(); sheetIndex++) {
			XSSFSheet st = wb.getSheetAt(sheetIndex);
			// 第一行为标题，不取
			for (int rowIndex = ignoreRows; rowIndex <= st.getLastRowNum(); rowIndex++) {
				// 列Map,<列索引,值>
				Map<Integer, Object> result = new HashMap<Integer, Object>();
				XSSFRow row = st.getRow(rowIndex);
				if (row == null) {
					continue;
				}
				boolean hasValue = false;
				for (int columnIndex = 0; columnIndex < row.getLastCellNum(); columnIndex++) {

					String value = "";
					cell = row.getCell(columnIndex);
					if (cell != null) {
						// 注意：一定要设成这个，否则可能会出现乱码
						// cell.setEncoding(HSSFCell.ENCODING_UTF_16);
						switch (cell.getCellType()) {
						case XSSFCell.CELL_TYPE_STRING:
							value = cell.getStringCellValue();
							break;
						case XSSFCell.CELL_TYPE_NUMERIC:
							if (HSSFDateUtil.isCellDateFormatted(cell)) {
								Date date = cell.getDateCellValue();
								if (date != null) {
									value = new SimpleDateFormat("yyyy-MM-dd").format(date);
								} else {
									value = "";
								}
							} else {
								if (cell.getNumericCellValue() * 1000000 % 1000000 > 0) {
									value = Double.toString(cell.getNumericCellValue());
								} else {
									value = new DecimalFormat("0").format(cell.getNumericCellValue());
								}
							}
							break;
						case XSSFCell.CELL_TYPE_FORMULA:
							// 导入时如果为公式生成的数据则无值
							if (StringUtils.isNotBlank(cell.getStringCellValue())) {
								value = cell.getStringCellValue();
							} else {
								value = cell.getNumericCellValue() + "";
							}
							break;
						case XSSFCell.CELL_TYPE_BLANK:
							break;
						case XSSFCell.CELL_TYPE_ERROR:
							value = "";
							break;
						case XSSFCell.CELL_TYPE_BOOLEAN:
							value = (cell.getBooleanCellValue() == true ? "Y" : "N");
							break;
						default:
							value = "";
						}
					}
					if (columnIndex == 0 && value.trim().equals("")) {
						break;
					}
					result.put(columnIndex, rightTrim(value));
					hasValue = true;
				}
				if (hasValue) {
					list.add(result);
				}
			}
		}
		wb.close();
		in.close();
		return list;
	}

	private static List<Map<Integer, Object>> getData4Xls(File file, int ignoreRows) throws FileNotFoundException, IOException {
		List<Map<Integer, Object>> list = new ArrayList<Map<Integer, Object>>();

		// int rowSize = 0;
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));

		// 打开HSSFWorkbook
		POIFSFileSystem fs = new POIFSFileSystem(in);
		HSSFWorkbook wb = new HSSFWorkbook(fs);
		HSSFCell cell = null;
		for (int sheetIndex = 0; sheetIndex < wb.getNumberOfSheets(); sheetIndex++) {
			HSSFSheet st = wb.getSheetAt(sheetIndex);

			// 第一行为标题，不取
			for (int rowIndex = ignoreRows; rowIndex <= st.getLastRowNum(); rowIndex++) {
				// 列Map,<列索引,值>
				Map<Integer, Object> result = new HashMap<Integer, Object>();

				HSSFRow row = st.getRow(rowIndex);
				if (row == null || isBlankRow(row)) {
					continue;
				}
				boolean hasValue = false;
				for (int columnIndex = 0; columnIndex < row.getLastCellNum(); columnIndex++) {
					String value = "";
					cell = row.getCell(columnIndex);
					if (cell != null) {
						// 注意：一定要设成这个，否则可能会出现乱码
						// cell.setEncoding(HSSFCell.ENCODING_UTF_16);
						switch (cell.getCellType()) {
						case HSSFCell.CELL_TYPE_STRING:
							value = cell.getStringCellValue();
							break;
						case HSSFCell.CELL_TYPE_NUMERIC:
							if (HSSFDateUtil.isCellDateFormatted(cell)) {
								Date date = cell.getDateCellValue();
								if (date != null) {
									value = new SimpleDateFormat("yyyy-MM-dd").format(date);
								} else {
									value = "";
								}
							} else {
								if (cell.getNumericCellValue() * 1000000 % 1000000 >= 1) {
									if (cell.getNumericCellValue() * 100000 % 100000 >= 1) {
										if (cell.getNumericCellValue() * 10000 % 10000 >= 1) {
											if (cell.getNumericCellValue() * 1000 % 1000 >= 1) {
												if (cell.getNumericCellValue() * 100 % 100 >= 1) {
													if (cell.getNumericCellValue() * 10 % 10 >= 1) {
														value = new DecimalFormat("0.0").format(cell.getNumericCellValue());
													} else {
														value = new DecimalFormat("0.00").format(cell.getNumericCellValue());
													}
												} else {
													value = new DecimalFormat("0.000").format(cell.getNumericCellValue());
												}
											} else {
												value = new DecimalFormat("0.0000").format(cell.getNumericCellValue());
											}
										} else {
											value = new DecimalFormat("0.00000").format(cell.getNumericCellValue());
										}
									} else {
										value = new DecimalFormat("0.000000").format(cell.getNumericCellValue());
									}
								} else {
									value = new DecimalFormat("0").format(cell.getNumericCellValue());
								}
							}
							break;
						case HSSFCell.CELL_TYPE_FORMULA:
							// 导入时如果为公式生成的数据则无值
							if (StringUtils.isNotBlank(cell.getStringCellValue())) {
								value = cell.getStringCellValue();
							} else {
								value = cell.getNumericCellValue() + "";
							}
							break;
						case HSSFCell.CELL_TYPE_BLANK:
							break;
						case HSSFCell.CELL_TYPE_ERROR:
							value = "";
							break;
						case HSSFCell.CELL_TYPE_BOOLEAN:
							value = (cell.getBooleanCellValue() == true ? "Y" : "N");
							break;
						default:
							value = "";
						}
					}
					if (columnIndex == 0 && value.trim().equals("")) {
						break;
					}
					result.put(columnIndex, rightTrim(value));
					hasValue = true;
				}
				if (hasValue) {
					list.add(result);
				}
			}
		}
		in.close();
		wb.close();
		return list;
	}

	/**
	 * 判断是否空行
	 * 
	 * @param row 行对象
	 * @return true:空行;false:非空行
	 */
	public static boolean isBlankRow(HSSFRow row) {
		if (row == null)
			return true;
		boolean result = true;
		for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
			HSSFCell cell = row.getCell(i, HSSFRow.RETURN_BLANK_AS_NULL);
			String value = "";
			if (cell != null) {
				switch (cell.getCellType()) {
				case Cell.CELL_TYPE_STRING:
					value = cell.getStringCellValue();
					break;
				case Cell.CELL_TYPE_NUMERIC:
					value = String.valueOf((int) cell.getNumericCellValue());
					break;
				case Cell.CELL_TYPE_BOOLEAN:
					value = String.valueOf(cell.getBooleanCellValue());
					break;
				case Cell.CELL_TYPE_FORMULA:
					value = String.valueOf(cell.getCellFormula());
					break;
				// case Cell.CELL_TYPE_BLANK:
				// break;
				default:
					break;
				}

				if (!value.trim().equals("")) {
					result = false;
					break;
				}
			}
		}

		return result;
	}

	/**
	 * 判断是否空行
	 * 
	 * @param row 行对象
	 * @param fristCellNum 开始列
	 * @param row 结束列
	 * @return true:空行;false:非空行
	 */
	public static boolean isBlankRow(HSSFRow row, int fristCellNum, int lastCellNum) {
		if (row == null)
			return true;
		boolean result = true;
		for (int i = fristCellNum; i < lastCellNum; i++) {
			HSSFCell cell = row.getCell(i, HSSFRow.RETURN_BLANK_AS_NULL);
			String value = "";
			if (cell != null) {
				switch (cell.getCellType()) {
				case Cell.CELL_TYPE_STRING:
					value = cell.getStringCellValue();
					break;
				case Cell.CELL_TYPE_NUMERIC:
					value = String.valueOf((int) cell.getNumericCellValue());
					break;
				case Cell.CELL_TYPE_BOOLEAN:
					value = String.valueOf(cell.getBooleanCellValue());
					break;
				case Cell.CELL_TYPE_FORMULA:
					value = String.valueOf(cell.getCellFormula());
					break;
				// case Cell.CELL_TYPE_BLANK:
				// break;
				default:
					break;
				}

				if (!value.trim().equals("")) {
					result = false;
					break;
				}
			}
		}

		return result;
	}

	/**
	 * 获取excel列的值，并将其转换成数值类型
	 * 
	 * @param cell 列对象
	 * @return
	 */
	public static BigDecimal getCellBigDecimalValue(Cell cell) {
		BigDecimal bigDecimal = null;

		if (null == cell) {
			return bigDecimal;
		}

		if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
			bigDecimal = BigDecimal.valueOf(cell.getNumericCellValue());

		} else if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
			String str = cell.getStringCellValue().trim();

			if (str.contains(".")) {
				str = str.substring(0, str.indexOf("."));
			}

			if (!"".equals(str)) {
				bigDecimal = BigDecimal.valueOf(Double.parseDouble(str));
			}
		}

		// 当数量小于0时，置为0
		if (null != bigDecimal && bigDecimal.compareTo(BigDecimal.ZERO) < 0) {
			bigDecimal = BigDecimal.ZERO;
		}

		return bigDecimal;
	}

	/**
	 * 获取excel列数值，并将其转换成时间类型的数据
	 * 
	 * @param cell列对象
	 * @return
	 * @throws Exception
	 */
	public static Calendar getCellCalendarValue(Cell cell) throws Exception {
		Calendar calendar = null;

		if (null == cell) {
			return calendar;
		}

		if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date = sdf.parse(cell.getStringCellValue().trim());
			calendar = Calendar.getInstance();
			calendar.setTime(date);
		} else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
			calendar = Calendar.getInstance();
			calendar.setTime(cell.getDateCellValue());
		}

		return calendar;
	}

	/**
	 * 获取excel列值，并转换成String类型的数据
	 * 
	 * @param cell 列对象
	 * @return
	 */
	public static String getCellStringValue(Cell cell) {
		String value = "";

		if (null == cell) {
			return value;
		}

		if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
			NumberFormat nf = NumberFormat.getInstance();
			nf.setGroupingUsed(false);// true时的格式：1,234,567,890
			double acno = cell.getNumericCellValue();
			value = nf.format(acno);

		} else if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
			value = cell.getStringCellValue().trim();
		}

		return value;
	}

	/**
	 * 获取excel列的值，并将其转换成数值类型
	 * 
	 * @param cell 列对象
	 * @return
	 */
	public static Long getCellLongValue(Cell cell) {
		Long returnLong = null;

		if (null == cell) {
			return returnLong;
		}

		if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
			returnLong = (long) cell.getNumericCellValue();

		} else if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
			String str = cell.getStringCellValue().trim();

			if (str.contains(".")) {
				str = str.substring(0, str.indexOf("."));
			}

			if (!"".equals(str)) {
				returnLong = (long) cell.getNumericCellValue();
			}
		}

		return returnLong;
	}

	/**
	 * 获取excel列的值，并将其转换成数值类型
	 * 
	 * @param cell 列对象
	 * @return
	 */
	public static Integer getCellIntegerValue(Cell cell) {
		Integer returnInteger = null;

		if (null == cell) {
			return returnInteger;
		}

		if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
			returnInteger = (int) cell.getNumericCellValue();

		} else if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
			String str = cell.getStringCellValue().trim();

			if (str.contains(".")) {
				str = str.substring(0, str.indexOf("."));
			}

			if (!"".equals(str)) {
				returnInteger = (int) cell.getNumericCellValue();
			}
		}

		return returnInteger;
	}

	/**
	 * 设置excel国际化标题
	 * 
	 * @param wb excel 对象
	 * @param endRow 结束行
	 * @return
	 */
	public static HSSFWorkbook setExcelTitle(HSSFWorkbook wb, int endRow) {
		HSSFSheet sheet = wb.getSheetAt(0);
		HSSFFont font = wb.createFont();
		font.setColor(Font.COLOR_RED);

		for (int rowIndex = 0; rowIndex < endRow; rowIndex++) {
			HSSFRow titleRow = sheet.getRow(rowIndex);
			for (int i = 0, len = titleRow.getLastCellNum(); i < len && null != titleRow; i++) {
				HSSFCell cell = titleRow.getCell(i);
				if (null != cell && cell.getCellType() == Cell.CELL_TYPE_STRING) {
					String key = cell.getStringCellValue();
					String allowBlank = "";

					if (key.contains("*")) {
						key = key.substring(1);
						allowBlank = "*";
					}

					key = MessageBundle.getInstance().getText(
							((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getLocale(), key);
					HSSFRichTextString richString = new HSSFRichTextString(key + allowBlank);
					richString.applyFont(key.length(), key.length() + allowBlank.length(), font);

					if (StringUtils.isNotBlank(key)) {
						cell.setCellValue(richString);
					}
				}
			}
		}

		return wb;
	}

	/**
	 * 判断是否空行
	 * 
	 * @param row 行对象
	 * @param fristCellNum 开始列
	 * @param row 结束列
	 * @return true:空行;false:非空行
	 */
	public static Boolean isCellBlank(HSSFCell cell) {
		Boolean result = false;
		String value = new String();
		if (cell != null) {
			switch (cell.getCellType()) {
			case Cell.CELL_TYPE_STRING:
				value = cell.getStringCellValue();
				break;
			case Cell.CELL_TYPE_NUMERIC:
				value = String.valueOf((int) cell.getNumericCellValue());
				break;
			case Cell.CELL_TYPE_BOOLEAN:
				value = String.valueOf(cell.getBooleanCellValue());
				break;
			case Cell.CELL_TYPE_FORMULA:
				value = String.valueOf(cell.getCellFormula());
				break;
			default:
				break;
			}
			if (value == null || StringUtils.isBlank(value)) {
				result = true;
			}
		} else {
			result = true;
		}

		return result;
	}

	/**
	 * <p>
	 * methodName: isCellBlank
	 * </p>
	 * <p>
	 * Description: 校验必填项
	 * </p>
	 * 
	 * @param numRows
	 * @param sb
	 * @param hfList
	 * @param validMap
	 * @return
	 */
	public static void isCellBlank(int numRows, StringBuilder sb, List<HSSFCell> hfList, Map<Integer, String> validMap) {
		// 获取值
		for (int i = 0; i < hfList.size(); i++) {
			if (validMap.containsKey(i) && validMap.get(i) != null) {
				if (isCellBlank(hfList.get(i))) {
					sb.append("第" + numRows + "行：" + validMap.get(i) + "不能为空!\\n");
				}
			}
		}
	}

	public static void main(String[] args) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		try {
			Map<Integer, Object> rowMap = new HashMap<Integer, Object>();

			Map<String, Object> otherMap = new HashMap<String, Object>();

			// 列头
			String[] hearders = new String[] { "物料编码", "物料名称", "物料组", "订单单位", "价格单位", "招标数量", "工厂", "需求日期", "交货地点", "备注", "*含税价", "*税率",
					"*付款条件", "*付款方式", "投标数量", "交货期", "投标说明" };
			otherMap.put("title", "报价模板");
			otherMap.put("hearder", hearders);
			otherMap.put("sheetmame", "报价模板");

			Map<Integer, HSSFCellStyle> hcsMap = new HashMap<Integer, HSSFCellStyle>();
			List<Object> rowData = new ArrayList<Object>();
			rowData.add("DFDFD");
			rowData.add(12);
			rowData.add(12D);
			rowData.add(12.001D);
			rowData.add(12.222);
			rowData.add(12.2);
			rowData.add(Calendar.getInstance());
			rowMap.put(2, rowData);

			// 设置单元格值
			PoiUtils.setTemplateCellValue(wb, sheet, otherMap, rowMap, hcsMap, true);

			for (int i = 0; i < 17; i++) {
				sheet.setColumnWidth(i, 5000);
			}

			String fileName = "投标单模板.xls";
			fileName = fileName.replaceAll(" ", "");
			fileName = new String(fileName.getBytes("utf-8"), "iso8859-1");

			FileOutputStream outputStream = new FileOutputStream("G://投标单模板.xls");
			wb.write(outputStream);
			outputStream.flush();
			outputStream.close();
			;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (wb != null) {
					wb.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

}
