package com.easy.common;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.util.StringUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class ExcelUtil{
    public static String NO_DEFINE = "no_define";//未定义的字段
    public static String DEFAULT_DATE_PATTERN="yyyy年MM月dd日";//默认日期格式
    public static int DEFAULT_COLOUMN_WIDTH = 17;
    /**
     * 导出Excel 2007 OOXML (.xlsx)格式
     * @param title 标题行
     * @param headMap 属性-列头
     * @param jsonArray 数据集
     * @param datePattern 日期格式，传null值则默认 年月日
     * @param colWidth 列宽 默认 至少17个字节
     * @param out 输出流
     */
    public static void exportExcelX(String title,Map<String, String> headMap,JSONArray jsonArray,String datePattern,int colWidth, OutputStream out) {
        if(datePattern==null) datePattern = DEFAULT_DATE_PATTERN;
        // 声明一个工作薄
        SXSSFWorkbook  workbook = new SXSSFWorkbook(1000);//缓存
        workbook.setCompressTempFiles(true);
         //表头样式
        CellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        Font titleFont = workbook.createFont();
        titleFont.setFontHeightInPoints((short) 20);
        titleFont.setBoldweight((short) 700);
        titleStyle.setFont(titleFont);
        // 列头样式
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        headerStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        headerStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        headerStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);
        headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        headerStyle.setFont(headerFont);
        // 单元格样式
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        Font cellFont = workbook.createFont();
        cellFont.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        cellStyle.setFont(cellFont);
        // 生成一个(带标题)表格
        SXSSFSheet sheet = (SXSSFSheet) workbook.createSheet();
        //设置列宽
        int minBytes = colWidth<DEFAULT_COLOUMN_WIDTH?DEFAULT_COLOUMN_WIDTH:colWidth;//至少字节数
        int[] arrColWidth = new int[headMap.size()];
        // 产生表格标题行,以及设置列宽
        String[] properties = new String[headMap.size()];
        String[] headers = new String[headMap.size()];
        int ii = 0;
        for (Iterator<String> iter = headMap.keySet().iterator(); iter
                .hasNext();) {
            String fieldName = iter.next();

            properties[ii] = fieldName;
            headers[ii] = headMap.get(fieldName);

            int bytes = fieldName.getBytes().length;
            arrColWidth[ii] =  bytes < minBytes ? minBytes : bytes;
            sheet.setColumnWidth(ii,arrColWidth[ii]*256);
            ii++;
        }
        // 遍历集合数据，产生数据行
        int rowIndex = 0;
        for (Object obj : jsonArray) {
            if(rowIndex == 65535 || rowIndex == 0){
                if ( rowIndex != 0 ) sheet = (SXSSFSheet) workbook.createSheet();//如果数据超过了，则在第二页显示

                //设置表头 第一行
                //SXSSFRow titleRow = (SXSSFRow) sheet.createRow(0);//表头 rowIndex=0
               // titleRow.createCell(0).setCellValue(title);
               // titleRow.getCell(0).setCellStyle(titleStyle);
//                sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headMap.size() - 1));

                SXSSFRow headerRow = (SXSSFRow) sheet.createRow(0); //列头 rowIndex =1
                for(int i=0;i<headers.length;i++)                          
                {
                    headerRow.createCell(i).setCellValue(headers[i]);
                    headerRow.getCell(i).setCellStyle(headerStyle);
                      
                }            
                rowIndex = 1;//数据内容从 rowIndex=2开始  //去掉了表头所以 从第一行开始
            }
            JSONObject jo = (JSONObject) JSONObject.toJSON(obj);
            SXSSFRow dataRow = (SXSSFRow) sheet.createRow(rowIndex);
            for (int i = 0; i < properties.length; i++)
            {
                SXSSFCell newCell = (SXSSFCell) dataRow.createCell(i);

                Object o =  jo.get(properties[i]);
                //过滤html
                if("daily".equals(properties[i])){
                    o =  html2Text(jo.get(properties[i])+"");
                }

                String cellValue = ""; 
                if(o==null) cellValue = "";
                else if(o instanceof Date) cellValue = new SimpleDateFormat(datePattern).format(o);
                else if(o instanceof Float || o instanceof Double) 
                    cellValue= new BigDecimal(o.toString()).setScale(2,BigDecimal.ROUND_HALF_UP).toString();
                else cellValue = o.toString();

                newCell.setCellValue(cellValue);
                newCell.setCellStyle(cellStyle);
            }
            rowIndex++;
        }
        // 自动调整宽度
        /*for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }*/
        try {
            workbook.write(out);
            workbook.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //*************************************************zp excel exp  start*****************************************************************
	public static Map ConvertObjToMap(Object obj) {
		Map<String, Object> reMap = new HashMap<String, Object>();
		if (obj == null)
			return null;
		Field[] fields = obj.getClass().getDeclaredFields();
		try {
			for (int i = 0; i < fields.length; i++) {
				try {
					Field f = obj.getClass().getDeclaredField(
							fields[i].getName());
					f.setAccessible(true);
					Object o = f.get(obj);
					reMap.put(fields[i].getName(), o);
				} catch (NoSuchFieldException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return reMap;
	}

	public static JSONArray getJSONArrayByList(List<?> list) {
		JSONArray jsonArray = new JSONArray();
		if (list == null || list.isEmpty()) {
			return jsonArray;// nerver return null
		}

		for (Object object : list) {
			jsonArray.add(object);
		}
		return jsonArray;
	}

    /**
     * //web 导出excel 完整版
     * @param fileName 文件名字
     * @param columnNames
     * @param tableData     数据源
     * @param response
     */
    public static void expExcel(String fileName,Map<String, String> columnNames,List<Object> tableData,HttpServletResponse response){
		//文件名字 是否 为空
    	if("".equals(fileName)){
			fileName = "default";
		}
		
		//如果不传 columnNames 列名，通过 list第一行 自动生成              
		if(columnNames == null){
			columnNames = new LinkedHashMap<String, String>();
			
			String objType = tableData.get(0).toString();
			Map<String, String> tempMap;  //用来临时存放 列名的
			
			if (objType.indexOf("{") != -1) {  //是否是Map 
				tempMap = (Map<String, String>) tableData.get(0);
			} else {						   //实体类 调用 ConvertObjToMap 转map
				tempMap = ConvertObjToMap(tableData.get(0));
			}

			//生成 表格 表头
			for (Entry<String, String> entry : tempMap.entrySet()) {
				columnNames.put(entry.getKey() + "", entry.getKey() + "");
			}
		}

		ExcelUtil.downloadExcelFile(fileName, columnNames, getJSONArrayByList(tableData), response);
    }
    public static String html2Text(String inputString) {
        if (StringUtils.isEmpty(inputString)) {
            return null;
        }
        String htmlStr = inputString;
        String textStr = "";
        java.util.regex.Pattern p_html;
        java.util.regex.Matcher m_html;

        java.util.regex.Pattern p_html1;
        java.util.regex.Matcher m_html1;

        try {
            String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
            String regEx_html1 = "<[^>]+";

            p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
            m_html = p_html.matcher(htmlStr);
            htmlStr = m_html.replaceAll(""); // 过滤html标签

            p_html1 = Pattern
                    .compile(regEx_html1, Pattern.CASE_INSENSITIVE);
            m_html1 = p_html1.matcher(htmlStr);
            htmlStr = m_html1.replaceAll(""); // 过滤html标签

            textStr = htmlStr;

            // 替换&amp;nbsp;
            textStr = textStr.replaceAll("&amp;", "").replaceAll("nbsp;", "");

        } catch (Exception e) {
            System.err.println("Html2Text: " + e.getMessage());
        }

        return textStr;// 返回文本字符串
    }
    //*************************************************zp excel exp  end  ***************************************************************************
    //Web 导出excel
    public static void downloadExcelFile(String title, Map<String,String> headMap, JSONArray ja, HttpServletResponse response){
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ExcelUtil.exportExcelX(title,headMap,ja,null,0,os);
            byte[] content = os.toByteArray();
            InputStream is = new ByteArrayInputStream(content);
            // 设置response参数，可以打开下载页面
            response.reset();

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8"); 
            response.setHeader("Content-Disposition", "attachment;filename="+ new String((title + ".xlsx").getBytes(), "iso-8859-1"));
            response.setContentLength(content.length);
            ServletOutputStream outputStream = response.getOutputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            BufferedOutputStream bos = new BufferedOutputStream(outputStream);
            byte[] buff = new byte[8192];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);

            }
            bis.close();
            bos.close();
            outputStream.flush();
            outputStream.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}