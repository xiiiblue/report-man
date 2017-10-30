package com.bluexiii.reportman.util;

import java.util.Map;

/**
 * Created by bluexiii on 24/10/2017.
 */
public class StringUtils {
    public static String replaceParam(String template, String key, String value) {
        String result = template.replaceAll("\\$\\{" + key + "\\}", value);
        return result;
    }

    public static String replaceAllaram(String template, Map<String, String> paramMap) {
        for (String key : paramMap.keySet()) {
            template = template.replaceAll("\\$\\{" + key + "\\}", paramMap.get(key));
        }
        return template;
    }
}
