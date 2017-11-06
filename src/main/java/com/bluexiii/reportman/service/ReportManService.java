package com.bluexiii.reportman.service;

import com.bluexiii.reportman.component.ExcelComponent;
import com.bluexiii.reportman.component.JdbcComponent;
import com.bluexiii.reportman.component.MailComponent;
import com.bluexiii.reportman.domain.Task;
import com.bluexiii.reportman.util.CommonUtils;
import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by bluexiii on 17/10/2017.
 */
@Service
public class ReportManService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportManService.class);

    /**
     * 生成报表
     */
    public void makeReport(String filePrefix, Map<String, String> customMap, boolean mailEnable) throws IOException {

        LOGGER.info("初始化:报表名称-{}", filePrefix);
        String fileSuffix = CommonUtils.getFileSuffix();
        LOGGER.info("初始化:报表后缀-{}", fileSuffix);

        LOGGER.info("初始化:加载Excel模板");
        String templatePath = CommonUtils.getTemplatePath(filePrefix);
        String reportPath = CommonUtils.getReportPath(filePrefix, fileSuffix);
        ExcelComponent excelComponent = new ExcelComponent(templatePath, reportPath);

        LOGGER.info("初始化:参数配置");
        Map<String, String> sysParamMap = excelComponent.getSysParamMap(customMap);

        LOGGER.info("初始化:任务配置");
        List<Task> taskList = excelComponent.getTaskList(sysParamMap);

        LOGGER.info("初始化:数据源");
        JdbcComponent jdbcComponent = new JdbcComponent(sysParamMap);

        LOGGER.info("初始化:邮件");
        MailComponent mailComponent = new MailComponent(sysParamMap);

        LOGGER.info("任务执行开始");
        int taskSize = taskList.size();
        int taskId = 1;
        for (Task task : taskList) {
            LOGGER.info("[{}/{}]: {}", taskId++, taskSize, task.getTaskName());
            // 查询生成结果集
            String connTag = task.getConnTag();
            String sql = task.getSql();
            LOGGER.debug("SQL: {}", sql);

            if (sql.toLowerCase().startsWith("select ")) {
                // 查询生成结果集
                SqlRowSet sqlRowSet = jdbcComponent.queryForRowSet(connTag, sql);
                // 写入Sheet页
                excelComponent.writeSheet(sqlRowSet, task.getSheetId(), task.getOffsetX(), task.getOffsetY(), task.getCellStyle());
            } else {
                // 插入/更新/删除/DDL操作
                jdbcComponent.execute(connTag, sql);
            }
        }
        LOGGER.info("任务执行结束");


        LOGGER.info("保存中:删除配置Sheet页");
        excelComponent.removeSheet(1);
        excelComponent.removeSheet(1);

        LOGGER.info("保存中:刷新Excel公式");
        excelComponent.refresh();

        LOGGER.info("保存中:保存Excel");
        try {
            excelComponent.save();
        } catch (IOException e) {
            LOGGER.error("保存Excel失败");
            e.printStackTrace();
        }

        if (mailEnable && sysParamMap.get("mail.enable").equals("Y")) {
            LOGGER.info("发送邮件: {}", sysParamMap.get("mail.subject"));
            try {
                mailComponent.sendWithParamMap(reportPath);
            } catch (MessagingException e) {
                LOGGER.info("发送邮件失败");
                e.printStackTrace();
            }
        }

        LOGGER.info("完成\n");
    }

    /**
     * 生成报表(异步)
     */
    @Async
    public void makeReportAsync(String filePrefix, Map<String, String> customMap, boolean mailEnable) throws IOException {
        makeReport(filePrefix, customMap, mailEnable);
    }

    /**
     * 取模版清单
     *
     * @return
     */
    public List<String> getTemplateList() {
        List<String> templateList = new LinkedList<>();
        // 填充下拉列表
        File templateDirFile = new File(CommonUtils.getTemplateDir());
        File[] files = templateDirFile.listFiles();
        Arrays.sort(files);
        for (File file : files) {
            if (file.isFile()) {
                String fileExtension = Files.getFileExtension(file.getName());
                String fileName = Files.getNameWithoutExtension(file.getName());
                if (!fileName.startsWith("~") && fileExtension.equals("xlsx")) {
                    templateList.add(fileName);
                }
            }
        }
        return templateList;
    }
}
