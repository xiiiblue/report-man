package com.bluexiii.reportman.util;

import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by bluexiii on 25/10/2017.
 */
public class StringUtilsTest {
    @Test
    public void replaceAllaram() throws Exception {
        String template = "select a,b,c from foobar where login_date BETWEEN ${start_date} and ${end_date}";
        Map<String, String> paramMap = new LinkedHashMap();
        paramMap.put("start_date", "2010");
        paramMap.put("end_date", "2050");
        String result = StringUtils.replaceAllaram(template, paramMap);
        System.out.println(result);
    }
}