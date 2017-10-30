package com.bluexiii.reportman.service;

import com.bluexiii.reportman.component.ExcelComponent;
import com.bluexiii.reportman.component.JdbcComponent;
import com.bluexiii.reportman.component.MailComponent;
import com.bluexiii.reportman.model.TaskModel;
import com.bluexiii.reportman.property.DynamicProperty;
import com.bluexiii.reportman.property.StaticProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by bluexiii on 17/10/2017.
 */
@Service
public class ReportManService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportManService.class);
    @Autowired
    private StaticProperty staticProperty;
    @Autowired
    private DynamicProperty dynamicProperty;
    @Autowired
    private ExcelComponent excelComponent;
    @Autowired
    private JdbcComponent jdbcComponent;
    @Autowired
    private MailComponent mailComponent;


    /**
     * 初始化
     */
    public void init() {
        init(null, null);
    }

    /**
     * 初始化(传入外部配置)
     */
    public void init(String filePrefix, Map paramMap) {
        if (filePrefix != null) {
            LOGGER.info("初始化:设置模板名称");
            staticProperty.setFilePrefix(filePrefix);
        }

        try {
            LOGGER.info("初始化:加载Excel模板");
            excelComponent.init();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        LOGGER.info("初始化:动态配置信息");
        dynamicProperty.init(paramMap);

        LOGGER.info("初始化:邮件配置");
        mailComponent.init();
    }

    /**
     * 生成报表
     */
    public void makeReport() {
        // 读取参数配置
        Map<String, String> configMap = dynamicProperty.getConfigMap();
        // 读取任务配置
        List<TaskModel> taskList = dynamicProperty.getTaskList();

        LOGGER.info("开始执行任务: " + configMap.get("mail.subject"));
        int taskSize = taskList.size();
        int taskId = 1;
        for (TaskModel taskModel : taskList) {
            LOGGER.info("任务[{}/{}]: {}", taskId++, taskSize, taskModel.getTaskName());
            // 查询生成结果集
            String connTag = taskModel.getConnTag();
            String sql = taskModel.getSql().toLowerCase();

            if (sql.startsWith("select ")) {
                // 查询生成结果集
                SqlRowSet sqlRowSet = jdbcComponent.queryForRowSet(connTag, sql);
                // 写入Sheet页
                excelComponent.writeSheet(sqlRowSet, taskModel.getSheetId(), taskModel.getOffsetX(), taskModel.getOffsetY(), taskModel.getCellStyle());
            } else {
                // 插入/更新/删除/DDL操作
                jdbcComponent.execute(connTag, sql);
            }
        }

        LOGGER.info("删除配置Sheet页");
        excelComponent.removeSheet(1);
        excelComponent.removeSheet(1);

        LOGGER.info("刷新Excel公式");
        excelComponent.refresh();

        LOGGER.info("保存Excel");
        try {
            excelComponent.save();
        } catch (IOException e) {
            LOGGER.error("保存Excel失败");
            e.printStackTrace();
        }

        if (configMap.get("mail.enable").equals("Y")) {
            LOGGER.info("发送邮件");
            try {
                mailComponent.send();
            } catch (MessagingException e) {
                LOGGER.info("发送邮件失败");
                e.printStackTrace();
            }
        }

        LOGGER.info("完成: " + configMap.get("mail.subject") + "\n\n");
    }

    /**
     * 生成报表(异步)
     */
    @Async
    public void makeReportAsync() {
        makeReport();
    }
}
