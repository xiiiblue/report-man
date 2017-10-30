package com.bluexiii.reportman.property;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by bluexiii on 14/11/2016.
 */
@Component
public class StaticProperty {
    @Value("${custom.file.prefix}")
    private String filePrefix;
    private String timeStr;
    private String dateStr;
    private String dayStr;
    private String templateDir;
    private String reportDir;

    public StaticProperty() {
        Date now = new Date();
        dayStr = new SimpleDateFormat("MMdd").format(now);
        dateStr = new SimpleDateFormat("yyyyMMdd").format(now);
        timeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now);
        templateDir = System.getProperty("user.dir") + File.separator + "template";
        reportDir = System.getProperty("user.dir") + File.separator + "report";
    }

    public String getDayStr() {
        return dayStr;
    }

    public String getDateStr() {
        return dateStr;
    }

    public String getTimeStr() {
        return timeStr;
    }

    public void setFilePrefix(String filePrefix) {
        this.filePrefix = filePrefix;
    }

    public String getTemplateDir() {
        return templateDir;
    }

    public void setTemplateDir(String templateDir) {
        this.templateDir = templateDir;
    }

    public String getReportDir() {
        return reportDir;
    }

    public void setReportDir(String reportDir) {
        this.reportDir = reportDir;
    }

    public String getTemplatePath() {
        return templateDir + "/" + filePrefix + ".xlsx";
    }

    public String getReportPath() {
        return reportDir + "/" + filePrefix + "@" + dateStr + ".xlsx";
    }
}
