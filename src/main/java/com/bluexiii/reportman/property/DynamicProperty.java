package com.bluexiii.reportman.property;

import com.bluexiii.reportman.component.ExcelComponent;
import com.bluexiii.reportman.model.TaskModel;
import com.bluexiii.reportman.util.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by bluexiii on 17/10/2017.
 */
@Component
public class DynamicProperty {
    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicProperty.class);
    @Autowired
    private ExcelComponent excelComponent;
    private Map<String, String> configMap;  // 系统配置
    private Map<String, String> sqlParamMap;  // SQL参数
    private List<TaskModel> taskList;  // 任务列表

    public void init() {
        init(null);
    }

    public void init(Map paramMap) {
        initConfigMap(paramMap);
        initTaskList();
    }

    /**
     * 读取系统配置与SQL参数
     */
    public void initConfigMap(Map<String, String> paramMap) {
        configMap = new LinkedHashMap<>();
        sqlParamMap = new LinkedHashMap<>();

        // 从Excel读入配置
        XSSFSheet configSheet = excelComponent.readSheet(2);
        int configCount = configSheet.getLastRowNum();
        for (int configId = 1; configId <= configCount; configId++) {
            XSSFRow configRow = configSheet.getRow(configId);
            String configKey = configRow.getCell(1).toString();
            String configValue = configRow.getCell(2).toString();
            if (configKey.startsWith("sql.")) {
                sqlParamMap.put(configKey.replaceFirst("sql.", ""), configValue);
            } else {
                configMap.put(configKey, configValue);
            }
        }

        // 从外部传参读入配置
        if (paramMap != null) {
            for (String configKey : paramMap.keySet()) {
                String configValue = paramMap.get(configKey);
                if (configKey.startsWith("sql.")) {
                    sqlParamMap.put(configKey.replaceFirst("sql.", ""), configValue);
                } else {
                    configMap.put(configKey, configValue);
                }
            }
        }

        LOGGER.info("参数-系统: {}", configMap);
        LOGGER.info("参数-SQL: {}", sqlParamMap);
    }


    /**
     * 读取任务配置
     */
    public void initTaskList() {
        taskList = new LinkedList<>();
        XSSFSheet taskSheet = excelComponent.readSheet(1);
        int taskCount = taskSheet.getLastRowNum();
        for (int statId = 1; statId <= taskCount; statId++) {
            XSSFRow taskRow = taskSheet.getRow(statId);
            TaskModel taskModel = new TaskModel();
            taskModel.setTaskName(taskRow.getCell(0).getStringCellValue());
            taskModel.setTaskStatus(taskRow.getCell(1).getStringCellValue());
            taskModel.setConnTag(taskRow.getCell(2).getStringCellValue());
            String sql = taskRow.getCell(3).getStringCellValue();
            // SQL语句中拼入参数
            if (sqlParamMap.size() > 0) {
                sql = StringUtils.replaceAllaram(sql, sqlParamMap);
            }
            taskModel.setSql(sql);
            taskModel.setSheetId(Integer.parseInt(taskRow.getCell(4).getRawValue()));
            taskModel.setOffsetX(Integer.parseInt(taskRow.getCell(5).getRawValue()));
            taskModel.setOffsetY(Integer.parseInt(taskRow.getCell(6).getRawValue()));
            taskModel.setCellStyle(taskRow.getCell(7).getStringCellValue());
            if (taskModel.getTaskStatus().equals("Y")) {
                taskList.add(taskModel);
            }
        }
        LOGGER.debug("taskList: {}", taskList);
    }


    public List<TaskModel> getTaskList() {
        return taskList;
    }

    public Map<String, String> getConfigMap() {
        return configMap;
    }

    public Map<String, String> getSqlParamMap() {
        return sqlParamMap;
    }
}