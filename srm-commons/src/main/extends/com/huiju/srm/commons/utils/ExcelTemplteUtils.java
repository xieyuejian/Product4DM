package com.huiju.srm.commons.utils;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author TcT
 *         Date: 2018/5/23.
 *         Time: 下午2:14.
 * @Title:
 * @Description: Excel导出工具类
 */
public class ExcelTemplteUtils {


    public static Workbook createHssfWorkBook() {
        return new HSSFWorkbook();
    }

    public static Workbook createXssfWorkBook() {
        return new XSSFWorkbook();
    }

//    private   static final String serverFielPath="/opt/persist/hr/";
    
    private   static final String serverFielPath="../upload/";

    //private   static final String serverFielPath="E:\\dev\\";

    /**
     * 简单导出模板,只有列头
     *
     * @param workbook
     * @param sheetName
     * @param titleCol
     * @return
     */
    public static Workbook createTemple(Workbook workbook, String sheetName, ArrayList<String> titleCol) {
        //创建文本风格
        CellStyle cellStyle = workbook.createCellStyle();
        DataFormat dataFormat = workbook.createDataFormat();
        cellStyle.setDataFormat(dataFormat.getFormat("@"));
        Sheet st = workbook.createSheet(sheetName);
        //设置列宽度
        for (int i = 0, j = titleCol.size(); i < j; i++) {
            st.setColumnWidth(i + 1, 25 * 256);
        }
        //创建第一行
        Row row = st.createRow(0);
        CellStyle style = CatalogExcelUtil.getHeadStyle(workbook);
        for (int i = 0, j = titleCol.size(); i < j; i++) {
            CatalogExcelUtil.initCell(row.createCell(i), style, titleCol.get(i));
        }
        //创建200行文本单元格
        for (int i = 1; i < 1000; i++) {
            Row row1 = st.createRow(i);
            for (int j = 0, y = titleCol.size(); j < y; j++) {
                row1.createCell(j).setCellStyle(cellStyle);
            }
        }
        return workbook;
    }


    /**
     * 创建带下拉的03Excel模板
     * pullDownData,拉下列表的key,取自于titleCol的index
     *
     * @param workbook
     * @param sheetName
     * @return
     */
    public static Workbook createTemple(Workbook workbook, String sheetName, ArrayList<String> titleCol, Map<String, List<String>> pullDownData) {
        Sheet st = workbook.createSheet(sheetName);
        //设置列宽度
        for (int i = 0, j = titleCol.size(); i < j; i++) {
            st.setColumnWidth(i + 1, 25 * 256);
        }
        //创建第一行
        Row row = st.createRow(0);
        CellStyle style = CatalogExcelUtil.getHeadStyle(workbook);
        for (int i = 0, j = titleCol.size(); i < j; i++) {
            CatalogExcelUtil.initCell(row.createCell(i), style, titleCol.get(i));
        }
        //查看有无下拉列表
        if (pullDownData != null && pullDownData.size() > 0) {
            Iterator<String> iterator = pullDownData.keySet().iterator();
            int sheetIndex = 1;
            while (iterator.hasNext()) {
                String key = iterator.next();
                if (titleCol.contains(key)) {
                    //取下拉标题位置
                    int index = titleCol.indexOf(key);
                    //获得下拉数据
                    List<String> data = pullDownData.get(key);
                    //创建下拉列表
                    try {
                        String hiddenSheetName = "hidden_level" + String.valueOf(index);
                        if (workbook instanceof HSSFWorkbook) {
                            workbook = createDownList2003(workbook, st, data, 1, 40, index, index, hiddenSheetName, sheetIndex);
                        } else {
                            workbook = createDownList2007(workbook, st, data, 1, 40, index, index, hiddenSheetName, sheetIndex);
                        }
                        sheetIndex++;
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException("生成下拉列表模板失败,请联系管理员!:" + e.getMessage());
                    }
                }
            }
        }

        return workbook;
    }


    /**
     * @param wb               HSSFWorkbook对象
     * @param realSheet        需要操作的sheet对象
     * @param datas            下拉的列表数据
     * @param startRow         开始行
     * @param endRow           结束行
     * @param startCol         开始列
     * @param endCol           结束列
     * @param hiddenSheetName  隐藏的sheet名
     * @param hiddenSheetIndex 隐藏的sheet索引
     * @return
     * @throws Exception
     */
    private static HSSFWorkbook createDownList2003(Workbook wb, Sheet realSheet, List<String> datas, int startRow, int endRow,
                                                   int startCol, int endCol, String hiddenSheetName, int hiddenSheetIndex)
            throws Exception {

        HSSFWorkbook workbook = (HSSFWorkbook) wb;
        // 创建一个数据源sheet
        HSSFSheet hidden = workbook.createSheet(hiddenSheetName);
        // 数据源sheet页不显示
        workbook.setSheetHidden(hiddenSheetIndex, true);
        // 将下拉列表的数据放在数据源sheet上
        HSSFRow row = null;
        HSSFCell cell = null;
        //创建下拉列表的数据源
        for (int i = 0, length = datas.size(); i < length; i++) {
            row = hidden.createRow(i);
            cell = row.createCell(0);
            cell.setCellValue(datas.get(i));
        }
        DVConstraint constraint = DVConstraint.createFormulaListConstraint(hiddenSheetName + "!$A$1:$A" + datas.size());
        CellRangeAddressList addressList = null;
        HSSFDataValidation validation = null;
        row = null;
        cell = null;
        // 单元格样式
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(HSSFDataFormat.getBuiltinFormat("0"));
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        // 循环指定单元格下拉数据
        for (int i = startRow; i <= endRow; i++) {
            row = (HSSFRow) realSheet.createRow(i);
            cell = row.createCell(startCol);
            cell.setCellStyle(style);
            addressList = new CellRangeAddressList(i, i, startCol, endCol);
            validation = new HSSFDataValidation(addressList, constraint);
            realSheet.addValidationData(validation);
        }

        return workbook;
    }


    /**
     * 2007版本,无下拉校验,建议用03版本做模板
     *
     * @param wb
     * @param realSheet
     * @param datas
     * @param startRow
     * @param endRow
     * @param startCol
     * @param endCol
     * @param hiddenSheetName
     * @param hiddenSheetIndex
     * @return
     * @throws Exception
     */
    private static XSSFWorkbook createDownList2007(Workbook wb, Sheet realSheet, List<String> datas, int startRow, int endRow,
                                                   int startCol, int endCol, String hiddenSheetName, int hiddenSheetIndex)
            throws Exception {

        XSSFWorkbook workbook = (XSSFWorkbook) wb;
        // 创建一个数据源sheet
        XSSFSheet hidden = workbook.createSheet(hiddenSheetName);
        // 数据源sheet页不显示
        workbook.setSheetHidden(hiddenSheetIndex, true);
        // 将下拉列表的数据放在数据源sheet上
        XSSFRow row;
        XSSFCell cell;
        String[] strings = new String[datas.size()];
        //创建下拉列表的数据源
        for (int i = 0, length = datas.size(); i < length; i++) {
            row = hidden.createRow(i);
            cell = row.createCell(0);
            cell.setCellValue(datas.get(i));
            strings[i] = datas.get(i);
        }
        XSSFDataValidationHelper validationHelper = new XSSFDataValidationHelper(hidden);
        XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) validationHelper.createExplicitListConstraint(strings);
        CellRangeAddressList addressList;
        XSSFDataValidation validation;
        // 单元格样式
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(HSSFDataFormat.getBuiltinFormat("0"));
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        // 循环指定单元格下拉数据
        for (int i = startRow; i <= endRow; i++) {
            row = (XSSFRow) realSheet.createRow(i);
            cell = row.createCell(startCol);
            cell.setCellStyle(style);
            addressList = new CellRangeAddressList(i, i, startCol, endCol);
            validation = (XSSFDataValidation) validationHelper.createValidation(dvConstraint, addressList);
            realSheet.addValidationData(validation);
        }
        return workbook;
    }

    /**
     * 文件下载
     *
     * @param workbook
     * @param response
     */
    public static void renderOutExport(Workbook workbook, HttpServletResponse response, HttpServletRequest request, String fileName) {
        //version
        if (workbook instanceof HSSFWorkbook) {
            fileName = fileName + ".xls";
        } else {
            fileName = fileName + ".xlsx";
        }
        FileInputStream in = null;
        ServletOutputStream outputStream = null;
        try {
            //ie
            if (isIE(request)) {
                fileName = URLEncoder.encode(fileName, "UTF8");
            } else {
                fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
            }
            FileOutputStream out = new FileOutputStream(serverFielPath + fileName);
 //           FileOutputStream out = new FileOutputStream("/opt/persist/hr/" + fileName);
            workbook.write(out);
            out.flush();
            out.close();
            //输出
            response.setCharacterEncoding("utf-8");
            response.setContentType("multipart/form-data");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            in = new FileInputStream(serverFielPath + fileName);
 //           in = new FileInputStream("/opt/persist/hr/" + fileName);
            outputStream = response.getOutputStream();
            int i = 0;
            byte b[] = new byte[1024];
            while (i != -1) {
                i = in.read(b);
                outputStream.write(b);
            }
            outputStream.flush();
            outputStream.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
//            log.error("读写文件异常:" + e.getMessage());
        } finally {
            //删除本地文件
            String filePath = serverFielPath + fileName;
//            String filePath = "/opt/persist/hr/" + fileName;
            File f = new File(filePath);
            if (!f.delete()) {
//                log.error("删除文件失败:" + filePath);
            }
        }
    }

    private static boolean isIE(HttpServletRequest request) {
        return request.getHeader("USER-AGENT").toLowerCase().indexOf("msie") > 0 || request.getHeader("USER-AGENT").toLowerCase().indexOf("rv:11.0") > 0 || request.getHeader("USER-AGENT").toLowerCase().indexOf("edge") > 0;
    }


}
