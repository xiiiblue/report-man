package com.bluexiii.reportman.service;

import com.bluexiii.reportman.util.ExcelUtils;
import com.bluexiii.reportman.util.JdbcUtils;
import com.bluexiii.reportman.util.MailUtils;
import com.bluexiii.reportman.model.Task;
import com.bluexiii.reportman.util.StringUtils;
import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private ProgressService progressService;

    /**
     * 生成报表
     */
    public void makeReport(String filePrefix, String fileSuffix, Map<String, String> customMap, boolean mailEnable) throws IOException {

        LOGGER.info("初始化:报表名称-{}", filePrefix);
        progressService.setProgress(fileSuffix, 0, "初始化:报表名称");

        if (fileSuffix == null) {
            fileSuffix = StringUtils.getFileSuffix();
        }
        LOGGER.info("初始化:报表后缀-{}", fileSuffix);
        progressService.setProgress(fileSuffix, 3, "初始化:报表后缀");

        LOGGER.info("初始化:加载Excel模板");
        progressService.setProgress(fileSuffix, 6, "初始化:加载Excel模板");
        String templatePath = StringUtils.getTemplatePath(filePrefix);
        String reportPath = StringUtils.getReportPath(filePrefix, fileSuffix);
        ExcelUtils excelUtils = new ExcelUtils(templatePath, reportPath);

        LOGGER.info("初始化:参数配置");
        progressService.setProgress(fileSuffix, 10, "初始化:参数配置");
        Map<String, String> sysParamMap = excelUtils.getSysParamMap(customMap);
        LOGGER.info("sysParamMap: {}", sysParamMap);

        LOGGER.info("初始化:任务配置");
        progressService.setProgress(fileSuffix, 13, "初始化:任务配置");
        List<Task> taskList = excelUtils.getTaskList(sysParamMap);

        LOGGER.info("初始化:数据源");
        progressService.setProgress(fileSuffix, 16, "初始化:数据源");
        JdbcUtils jdbcUtils = new JdbcUtils(sysParamMap);

        LOGGER.info("初始化:邮件");
        progressService.setProgress(fileSuffix, 20, "初始化:邮件");
        MailUtils mailUtils = new MailUtils(sysParamMap);

        LOGGER.info("任务执行开始");
        progressService.setProgress(fileSuffix, 20, "任务执行开始");

        int taskSize = taskList.size();
        int taskId = 1;
        for (Task task : taskList) {
            LOGGER.info("[{}/{}]: {}", taskId++, taskSize, task.getTaskName());
            progressService.setProgress(fileSuffix, 20 + 60 * taskId / taskSize, "任务" + taskId + "/" + taskSize + ":" + task.getTaskName());

            // 查询生成结果集
            String connTag = task.getConnTag();
            String sql = task.getSql();
            LOGGER.debug("SQL: {}", sql);

            if (sql.toLowerCase().startsWith("select ")) {
                // 查询生成结果集
                SqlRowSet sqlRowSet = jdbcUtils.queryForRowSet(connTag, sql);
                // 写入Sheet页
                excelUtils.writeSheet(sqlRowSet, task.getSheetId(), task.getOffsetX(), task.getOffsetY(), task.getCellStyle());
            } else {
                // 插入/更新/删除/DDL操作
                jdbcUtils.execute(connTag, sql);
            }
        }
        LOGGER.info("任务执行结束");
        progressService.setProgress(fileSuffix, 80, "任务执行结束");

        LOGGER.info("保存中:删除配置Sheet页");
        progressService.setProgress(fileSuffix, 83, "保存中:删除配置Sheet页");
        excelUtils.removeSheet(1);
        excelUtils.removeSheet(1);

        LOGGER.info("保存中:刷新Excel公式");
        progressService.setProgress(fileSuffix, 86, "保存中:刷新Excel公式");
        excelUtils.refresh();

        LOGGER.info("保存中:保存Excel");
        progressService.setProgress(fileSuffix, 90, "保存中:保存Excel");
        try {
            excelUtils.save();
        } catch (IOException e) {
            LOGGER.error("保存Excel失败");
            e.printStackTrace();
        }

        if (mailEnable && sysParamMap.get("mail.enable").equals("Y")) {
            LOGGER.info("发送邮件: {}", sysParamMap.get("mail.subject"));
            progressService.setProgress(fileSuffix, 95, "发送邮件");

            try {
                mailUtils.sendWithParamMap(reportPath);
            } catch (MessagingException e) {
                LOGGER.info("发送邮件失败");
                e.printStackTrace();
            }
        }

        LOGGER.info("完成\n");
        progressService.setProgress(fileSuffix, 100, "完成");
    }

    /**
     * 生成报表(异步)
     */
    @Async
    public void makeReportAsync(String filePrefix, String fileSuffix, Map<String, String> customMap, boolean mailEnable) throws IOException {
        makeReport(filePrefix, fileSuffix, customMap, mailEnable);
    }

    /**
     * 取模版清单
     *
     * @return
     */
    public List<String> getTemplateList() {
        List<String> templateList = new LinkedList<>();
        // 填充下拉列表
        File templateDirFile = new File(StringUtils.getTemplateDir());
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
