/**
 * 
 */
package com.huiju.srm.commons.utils;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xml.sax.SAXException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;

/**
 * @author ideapad
 *
 */
public class PdfUtil {

	/**
	 * 查找复杂json取值问题
	 * 
	 * @param fields 取值字段层次名
	 * @param obj 解析的对象
	 * @param index 字段fields 解析到哪个
	 * @param list 结果集
	 * 
	 *            如果解析过程中的对象存在数组里则 返回的结果是 >= 1 如果解析过程中的对象不存在数组则 返回的结果 == 1
	 */
	public static void getFieldValue(String[] fields, Object obj, int index, List<Object> list) {
		if (obj instanceof String) {
			obj = JSON.parseObject(String.valueOf(obj));
		}
		if (obj instanceof JSONObject) {
			if (fields.length == index + 1) {
				JSONObject jsonObject = (JSONObject) obj;
				list.add(jsonObject.get(fields[index]));
			} else {
				JSONObject jsonObject = (JSONObject) obj;

				Object odd = jsonObject.get(fields[index]);
				getFieldValue(fields, odd, index + 1, list);
			}
		} else if (obj instanceof JSONArray) {
			if (fields.length == index + 1) {
				JSONArray jsonArray = (JSONArray) obj;
				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject jsonObject = (JSONObject) jsonArray.get(i);
					list.add(jsonObject.get(fields[index]));
				}
			} else {
				JSONArray jsonArray = (JSONArray) obj;
				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject jsonObject = (JSONObject) jsonArray.get(i);
					Object odd = jsonObject.get(fields[index]);
					getFieldValue(fields, odd, index + 1, list);
				}
			}
		}
	}

	public static void main(String[] args) throws DocumentException, IOException, ParserConfigurationException, SAXException {
		StringBuffer buf = new StringBuffer();
		buf.append("<html>");
		// put in some style
		buf.append("<head><style language='text/css'>");
		buf.append("h3 { border: 1px solid #aaaaff; background: #ccccff; ");
		buf.append("padding: 1em; text-transform: capitalize; font-family: SimSun; font-weight: normal;}");
		buf.append("p { margin: 1em 1em 4em 3em; } p:first-letter { color: red; font-size: 150%; }");
		buf.append("h2 { background: #5555ff; color: white; border: 10px solid black; padding: 3em; font-size: 200%; }");
		buf.append("</style></head>");
		// generate the body
		buf.append("<body>");
		for (int i = 6; i > 0; i--) {
			buf.append("<h3>" + i + "你你你你你你你你你你你你你你你你你你你你你你你你你你你你你你你你你你你你你你你你你你你你你!</h3>");
		}
		buf.append("<h2>No more bottles , no more bottles of beer. ");
		buf.append("Go to the store and buy some more, 99 bottles of beer on the wall.</h2>");
		buf.append("</body>");
		buf.append("</html>");
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		ByteArrayInputStream tInputStringStream = new ByteArrayInputStream(buf.toString().getBytes("utf-8"));
		Document doc = builder.parse(tInputStringStream);

		ITextRenderer renderer = new ITextRenderer();
		// 解决中文支持问题
		ITextFontResolver fontResolver = renderer.getFontResolver();
		// 微软雅黑,Microsoft YaHei
		fontResolver.addFont("C:/Users/ideapad/Desktop/html/ziti/msyh/msyh.ttc", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
		fontResolver.addFont("C:/Users/ideapad/Desktop/html/ziti/msyh/msyhbd.ttc", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
		fontResolver.addFont("C:/Users/ideapad/Desktop/html/ziti/msyh/msyhl.ttc", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
		// 楷体
		fontResolver.addFont("C:/Users/ideapad/Desktop/html/ziti/simkai/simkai.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
		// 黑体
		fontResolver.addFont("C:/Users/ideapad/Desktop/html/ziti/simhei/simhei.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
		// 隶书
		fontResolver.addFont("C:/Users/ideapad/Desktop/html/ziti/SIMLI/SIMLI.TTF", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
		// 其他
		fontResolver.addFont("C:/Users/ideapad/Desktop/html/ziti/SIMLI/SIMLI.TTF", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);

		renderer.setDocument(doc, null);
		String outputFile = "C:/Users/ideapad/Desktop/html/100bottles.pdf";
		OutputStream os = new FileOutputStream(outputFile);
		renderer.layout();
		renderer.createPDF(os);
		os.close();
	}

}
