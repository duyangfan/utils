package utils;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.CellStyle;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.*;

/**
 * 读取Excel
 *
 * @author du
 */
public class ExcelUtils {

    /**
     *
     * @param header
     * @param dataList   map 中的key，，就是header的内容
     * @param sheetName
     * @return
     */
    public static HSSFWorkbook getHSSFWorkbook(List<String> header, List<Map<String, Object>> dataList, String sheetName){
        // Properties pro=CommonUtils.loadProperties("hessian.properties");
        int excelSize = 60000;// 6w行分sheet
        if ("".equals(sheetName)) {
            sheetName = "sheet未命名";
        }
        if (header == null || header.size() == 0) {
            header.add("错误信息");
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("错误信息", "表头为空!");
            dataList = new ArrayList<Map<String, Object>>();
            dataList.add(map);
        }
        HSSFWorkbook wb = new HSSFWorkbook();
        // 创建单元格样式
        HSSFCellStyle cellStyle = wb.createCellStyle();
        // 指定单元格居中对齐
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        cellStyle.setBorderBottom(CellStyle.BORDER_THIN); // 下边框边框
        cellStyle.setBorderTop(CellStyle.BORDER_THIN); // 上边框
        cellStyle.setBorderLeft(CellStyle.BORDER_THIN); // 左边框
        cellStyle.setBorderRight(CellStyle.BORDER_THIN); // 有边框
        // 指定单元格垂直居中对齐
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        // 指定当单元格内容显示不下时自动换行
        cellStyle.setWrapText(true);
        // 设置单元格字体
        HSSFFont font = wb.createFont();
        // 字体置粗体
        // font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        font.setFontName("宋体");
        font.setFontHeight((short) 250);
        cellStyle.setFont(font);
        List<List<Map<String, Object>>> list = Util.getPageList(excelSize, dataList);
        // 如果没有数据则导出是空数据
        if (list.isEmpty()) {
            // 列数
            int cellLine = header.size();
            // 建立新的sheet对象（excel的表单）
            HSSFSheet sheet = wb.createSheet(sheetName);

            HSSFRow row0 = sheet.createRow(0);
            for (int j = 0; j < cellLine; j++) {
                // 单元格宽度
                sheet.setColumnWidth(j, 20 * 256);
                HSSFCell cellw = row0.createCell(j);
                cellw.setCellType(HSSFCell.CELL_TYPE_STRING);
                cellw.setCellStyle(cellStyle);
                // 设置单元格内容
                cellw.setCellValue(header.get(j));
            }
        }
        int m = 0;
        for (List<Map<String, Object>> list2 : list) {
            String sheetNameStr = sheetName + m;
            // 行数
            int rowLine = list2.size();
            // 列数
            int cellLine = header.size();
            // 建立新的sheet对象（excel的表单）
            HSSFSheet sheet = wb.createSheet(sheetNameStr);
            HSSFRow row0 = sheet.createRow(0);
            for (int j = 0; j < cellLine; j++) {
                // 单元格宽度
                sheet.setColumnWidth(j, 20 * 256);
                HSSFCell cellw = row0.createCell(j);
                cellw.setCellStyle(cellStyle);
                // 设置单元格内容
                cellw.setCellValue(header.get(j));
            }

            if (list2 != null && list2.size() > 0) {
                for (int i = 1; i <= rowLine; i++) {
                    HSSFRow roww = sheet.createRow(i);
                    Map<String, Object> map = list2.get(i - 1);
                    for (int j = 0; j < cellLine; j++) {
                        HSSFCell cellw = roww.createCell(j);
                        cellw.setCellStyle(cellStyle);
                        // 设置单元格内容
                        cellw.setCellValue(map.get(header.get(j)) == null ? "" : map.get(header.get(j)).toString());
                    }
                }
            }
            m++;
        }

        return wb;
    }
    /**
     * 导出excel文件(sheet分页)
     * @param header
     * @param dataList
     * @param sheetName
     * @param response
     * @throws ParseException
     */
    public static void writeExcelFile(List<String> header, List<Map<String, Object>> dataList, String sheetName,
                                      HttpServletResponse response) throws ParseException {
        HSSFWorkbook hss=getHSSFWorkbook(header, dataList, sheetName);
        try {
            response.setContentType("application/msexcel");
            // 设置文件名称
            response.setHeader("Content-disposition",
                    "attachment; filename=" + DateUtil.formatDate(new Date(), "yyyyMMddHHmmss") + "export.xls");
            OutputStream fileOut = response.getOutputStream();
            hss.write(fileOut);
            fileOut.flush();
            fileOut.close();
            hss = null;
            fileOut = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 导出excel文件(sheet分页)
     * @param header
     * @param dataList
     * @param sheetName
     * @param
     * @throws ParseException
     */
    public static void writeExcelFile(List<String> header, List<Map<String, Object>> dataList, String sheetName,
                                      String path) throws ParseException {
        HSSFWorkbook hss=getHSSFWorkbook(header, dataList, sheetName);
        try {
            OutputStream fileOut =new FileOutputStream(new File(path+Util.formatDateToString(new Date(), "yyyyMMddHHmmss") + "export.xls"));
            hss.write(fileOut);
            fileOut.flush();
            fileOut.close();
            hss = null;
            fileOut = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static HSSFWorkbook getHSSFWorkbook(List<List<String>> headers, List<List<Map<String, Object>>> dataLists,
                                               List<String> sheetNames){
        HSSFWorkbook wb = new HSSFWorkbook();
        // 创建单元格样式
        HSSFCellStyle cellStyle = wb.createCellStyle();
        // 指定单元格居中对齐
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        cellStyle.setBorderBottom(CellStyle.BORDER_THIN); // 下边框边框
        cellStyle.setBorderTop(CellStyle.BORDER_THIN); // 上边框
        cellStyle.setBorderLeft(CellStyle.BORDER_THIN); // 左边框
        cellStyle.setBorderRight(CellStyle.BORDER_THIN); // 有边框
        // 指定单元格垂直居中对齐
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        // 指定当单元格内容显示不下时自动换行
        cellStyle.setWrapText(true);
        // 设置单元格字体
        HSSFFont font = wb.createFont();
        font.setFontName("宋体");
        font.setFontHeight((short) 250);
        cellStyle.setFont(font);

        for (int i = 0; i < sheetNames.size(); i++) {
            String sheetName = sheetNames.get(i);
            List<String> header = headers.get(i);
            List<Map<String, Object>> dataList = dataLists.get(i);
            if ("".equals(sheetName)) {
                sheetName = "sheet未命名";
            }
            if (header == null || header.size() == 0) {
                header.add("错误信息");
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("错误信息", "表头为空!");
                dataList = new ArrayList<Map<String, Object>>();
                dataList.add(map);
            }
            // 行数
            int rowLine = dataList.size();
            // 列数
            int cellLine = header.size();

            // 建立新的sheet对象（excel的表单）
            HSSFSheet sheet = wb.createSheet(sheetName);
            HSSFRow row0 = sheet.createRow(0);
            for (int j = 0; j < cellLine; j++) {
                // 单元格宽度
                sheet.setColumnWidth(j, 20 * 256);
                HSSFCell cellw = row0.createCell(j);
                cellw.setCellStyle(cellStyle);
                // 设置单元格内容
                cellw.setCellValue(header.get(j));
            }

            if (dataList != null && dataList.size() > 0) {
                for (int n = 1; n <= rowLine; n++) {
                    HSSFRow roww = sheet.createRow(n);
                    Map<String, Object> map = dataList.get(n - 1);
                    for (int j = 0; j < cellLine; j++) {
                        HSSFCell cellw = roww.createCell(j);
                        cellw.setCellStyle(cellStyle);
                        // 设置单元格内容
                        cellw.setCellValue(map.get(header.get(j)) == null ? "" : map.get(header.get(j)).toString());
                    }
                }
            }
        }
        return wb;
    }

    /**
     * 导出excel文件(多页签)
     * @param headers
     * @param dataLists
     * @param sheetNames
     * @param response
     * @throws ParseException
     */
    public static void writeExcelFile(List<List<String>> headers, List<List<Map<String, Object>>> dataLists,
                                      List<String> sheetNames, HttpServletResponse response) throws ParseException {
        HSSFWorkbook hss=getHSSFWorkbook(headers, dataLists, sheetNames);
        try {
            response.setContentType("application/msexcel");
            // 设置文件名称
            response.setHeader("Content-disposition",
                    "attachment; filename=" + Util.formatDateToString(new Date(), "yyyyMMddHHmmss") + "export.xls");
            OutputStream fileOut = response.getOutputStream();
            hss.write(fileOut);
            fileOut.flush();
            fileOut.close();
            hss = null;
            fileOut = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 导出excel文件(多页签)
     * @param headers
     * @param dataLists
     * @param sheetNames
     * @param
     * @throws ParseException
     */
    public static void writeExcelFile(List<List<String>> headers, List<List<Map<String, Object>>> dataLists,
                                      List<String> sheetNames, String path) throws ParseException {
        HSSFWorkbook hss=getHSSFWorkbook(headers, dataLists, sheetNames);
        try {
            OutputStream fileOut =new FileOutputStream(new File(path+Util.formatDateToString(new Date(), "yyyyMMddHHmmss") + "export.xls"));
            hss.write(fileOut);
            fileOut.flush();
            fileOut.close();
            hss = null;
            fileOut = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
//		List<String> header =new ArrayList<>();
//		header.add("姓名");
//		header.add("姓别");
//		header.add("年龄");
//		header.add("地址");
//
//		List<Map<String, Object>> dataList =new ArrayList<>();
//
//		for (int i = 0; i < 40; i++) {
//			Map<String, Object> map =new HashMap<>();
//			map.put(header.get(0), "郑正雷"+i);
//			map.put(header.get(1), "男"+i);
//			map.put(header.get(2), "30"+i);
//			map.put(header.get(0), "北京"+i);
//			dataList.add(map);
//		}
//		ExcelUtils.writeExcelFile(header, dataList, "人员", "e:/");

//		String[] titles = new String[]{"名称","性别","国籍"};
//		List<List<String>> entityDataList = new ArrayList<List<String>>();
//		for (int i = 0; i < 200; i++) {
//			List<String> list1 = new ArrayList<String>();
//			list1.add("张三");
//			list1.add("男");
//			list1.add("中国");
//			entityDataList.add(list1);
//		}
//		System.out.println(ExcelPoiUtil.createExcel("D://111//",titles, entityDataList));
//		Util.download(response, fileName, filePath);
    }

}