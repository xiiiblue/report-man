package com.bluexiii.reportman.util;

import com.bluexiii.reportman.model.Task;
import com.google.common.io.Files;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by bluexiii on 17/10/2017.
 */
public class ExcelUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelUtils.class);
    private Map<String, XSSFCellStyle> styleMap = new HashMap<>();
    private XSSFWorkbook workbook;
    private String reportPath;

    public ExcelUtils(String templatePath, String reportPath) throws IOException {
        this.reportPath = reportPath;
        // 打开模板
        this.workbook = new XSSFWorkbook(new FileInputStream(templatePath));
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

    /**
     * 读取参数
     *
     * @return
     */
    public Map<String, String> getSysParamMap(Map<String, String> customMap) {
        Map<String, String> sysParamMap = new LinkedHashMap<>();

        // 从Sheet2读取配置
        XSSFSheet configSheet = readSheet(2);
        int configCount = configSheet.getLastRowNum();
        for (int configId = 1; configId <= configCount; configId++) {
            XSSFRow configRow = configSheet.getRow(configId);
            String configKey = configRow.getCell(1).toString();
            String configValue = configRow.getCell(2).toString();
            sysParamMap.put(configKey, configValue);
        }

        // 并入自定义配置
        if (customMap != null) {
            for (String key : customMap.keySet()) {
                sysParamMap.put(key, customMap.get(key));
            }
        }

        return sysParamMap;
    }

    /**
     * 读取任务
     *
     * @return
     */
    public List<Task> getTaskList(Map<String, String> sysParamMap) {
        // 取出SQL配置
        Map<String, String> sqlParamMap = new LinkedHashMap<>();
        for (String key : sysParamMap.keySet()) {
            if (key.startsWith("sql.")) {
                sqlParamMap.put(key.replaceFirst("sql.", ""), sysParamMap.get(key));
            }
        }

        // 从Sheet1读取任务
        List<Task> taskList = new LinkedList<>();
        XSSFSheet taskSheet = readSheet(1);
        int taskCount = taskSheet.getLastRowNum();
        for (int statId = 1; statId <= taskCount; statId++) {
            XSSFRow taskRow = taskSheet.getRow(statId);
            Task task = new Task();
            task.setTaskName(taskRow.getCell(0).getStringCellValue());
            task.setTaskStatus(taskRow.getCell(1).getStringCellValue());
            task.setConnTag(taskRow.getCell(2).getStringCellValue());
            String sql = taskRow.getCell(3).getStringCellValue();
            // SQL语句中拼入参数
            if (sqlParamMap.size() > 0) {
                sql = StringUtils.replaceAllaram(sql, sqlParamMap);
            }
            task.setSql(sql);
            task.setSheetId(Integer.parseInt(taskRow.getCell(4).getRawValue()));
            task.setOffsetX(Integer.parseInt(taskRow.getCell(5).getRawValue()));
            task.setOffsetY(Integer.parseInt(taskRow.getCell(6).getRawValue()));
            task.setCellStyle(taskRow.getCell(7).getStringCellValue());
            if (task.getTaskStatus().equals("Y")) {
                taskList.add(task);
            }
        }
        LOGGER.debug("taskList: {}", taskList);
        return taskList;
    }

}
