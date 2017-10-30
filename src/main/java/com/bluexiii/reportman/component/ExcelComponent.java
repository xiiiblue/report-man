package com.bluexiii.reportman.component;

import com.bluexiii.reportman.property.StaticProperty;
import com.google.common.io.Files;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bluexiii on 17/10/2017.
 */
@Component
public class ExcelComponent {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelComponent.class);
    @Autowired
    private StaticProperty staticProperty;
    private XSSFWorkbook workbook;
    private Map<String, XSSFCellStyle> styleMap;

    /**
     * 从模版初始化Excel
     *
     * @throws IOException
     */
    public void init() throws IOException {
        String templatePath = staticProperty.getTemplatePath();
        LOGGER.info("模版路径: {}", templatePath);
        FileInputStream fileInputStream = new FileInputStream(templatePath);
        styleMap = new HashMap<>();
        workbook = new XSSFWorkbook(fileInputStream);
    }

    /**
     * 刷新Excel公式
     */
    public void refresh() {
        workbook.getCreationHelper().createFormulaEvaluator().evaluateAll();
    }

    /**
     * 保存Excel
     *
     * @throws IOException
     */
    public void save() throws IOException {
        String reportPath = staticProperty.getReportPath();


        LOGGER.info("报表路径: {}", reportPath);
        Files.createParentDirs(new File(reportPath));
        FileOutputStream fileOutputSteam = new FileOutputStream(reportPath);

        workbook.write(fileOutputSteam);
        workbook.close();
    }

    /**
     * 读取Sheet页
     *
     * @param sheetId
     * @return
     */
    public XSSFSheet readSheet(int sheetId) {
        LOGGER.debug("读取Sheet页: {}", sheetId);
        return workbook.getSheetAt(sheetId - 1);
    }

    /**
     * 删除Sheet页
     *
     * @param sheetId
     */
    public void removeSheet(int sheetId) {
        LOGGER.debug("删除Sheet页: {}", sheetId);
        workbook.removeSheetAt(sheetId - 1);
    }

    /**
     * 将结果集写入Sheet页
     *
     * @param sqlRowSet
     * @param sheetId
     * @param styleTag
     * @param offsetX
     * @param offsetY
     */
    public void writeSheet(SqlRowSet sqlRowSet, int sheetId, int offsetX, int offsetY, String styleTag) {
        LOGGER.debug("写入Sheet页: {}, 起始行: {}, 起始列: {}, 样式: {}" + sheetId, offsetX, offsetY, styleTag);
        XSSFSheet sheet = readSheet(sheetId);
        int lastRowNum = sheet.getLastRowNum();
        XSSFCell cell;
        while (sqlRowSet.next()) {
            int rowNum = sqlRowSet.getRow() - 2 + offsetY;
            XSSFRow row = (rowNum > lastRowNum) ? sheet.createRow(rowNum) : sheet.getRow(rowNum);
            for (int x = 1; x <= sqlRowSet.getMetaData().getColumnCount(); x++) {
                int columnNum = x - 2 + offsetX;
                cell = row.createCell(columnNum);
                cell.setCellStyle(getCellStyle(styleTag));
                cell.setCellValue(sqlRowSet.getString(x));
            }
        }
    }

    /**
     * 获取单元格样式
     *
     * @param styleTag
     * @return
     */
    private XSSFCellStyle getCellStyle(String styleTag) {
        if (!styleMap.containsKey(styleTag)) {
            XSSFCellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderBottom(BorderStyle.THIN);
            if (styleTag.equals("CENTER")) {
                cellStyle.setAlignment(HorizontalAlignment.CENTER);
            } else {
                cellStyle.setAlignment(HorizontalAlignment.LEFT);
            }
            styleMap.put(styleTag, cellStyle);
        }
        return styleMap.get(styleTag);
    }
}
