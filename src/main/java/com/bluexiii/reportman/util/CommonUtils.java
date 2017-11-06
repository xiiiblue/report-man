package com.bluexiii.reportman.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by bluexiii on 24/10/2017.
 */
public class CommonUtils {
    /**
     * SQL模板替换
     *
     * @param template
     * @param paramMap
     * @return
     */
    public static String replaceAllaram(String template, Map<String, String> paramMap) {
        for (String key : paramMap.keySet()) {
            template = template.replaceAll("\\$\\{" + key + "\\}", paramMap.get(key));
        }
        return template;
    }

    /**
     * 生成文件后缀
     *
     * @return
     */
    public static String getFileSuffix() {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }

    /**
     * 取模板目录
     *
     * @return
     */
    public static String getTemplateDir() {
        return System.getProperty("user.dir") + File.separator + "template";
    }

    /**
     * 取报表目录
     *
     * @return
     */
    public static String getReportDir() {
        return System.getProperty("user.dir") + File.separator + "report";
    }

    /**
     * 取模板路径
     *
     * @return
     */
    public static String getTemplatePath(String filePrefix) {
        return getTemplateDir() + "/" + filePrefix + ".xlsx";
    }

    /**
     * 取报表路径
     *
     * @return
     */
    public static String getReportPath(String filePrefix, String fileSuffix) {
        return getReportDir() + "/" + filePrefix + "@" + fileSuffix + ".xlsx";
    }
}
