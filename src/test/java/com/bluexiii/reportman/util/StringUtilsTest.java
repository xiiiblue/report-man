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
        String template = "select IFNULL(s.ct,0) from t_p_virdepart_eparchy v left join (\n" +
                "select b.eparchy_code,COUNT(*) as ct from\n" +
                "(select staff_id from t_p_logininfo where login_date BETWEEN ${start_date} and ${end_date}) a\n" +
                "join (\n" +
                "SELECT DISTINCT a.eparchy_code,a.staff_id FROM t_p_mobstaff a, t_p_roleowner b\n" +
                "WHERE a.staff_id=b.staff_id AND b.end_date>NOW() AND b.role_code IN ('F0005','F0010','F0014','F0015')\n" +
                "and exists (select * from t_p_roleowner r where r.staff_id=a.staff_id and r.role_code='F0015' and r.end_date>now())\n" +
                ") b on b.staff_id=a.staff_id\n" +
                "GROUP BY b.eparchy_code\n" +
                ") s on s.eparchy_code=v.rsvalue2 order by v.rsvalue2 desc";


        Map<String, String> paramMap = new LinkedHashMap();
        paramMap.put("start_date", "2010");
        paramMap.put("end_date", "2050");

        String result = StringUtils.replaceAllaram(template, paramMap);
        System.out.println(result);
    }

    @Test
    public void replaceParam() throws Exception {
        String template = "select IFNULL(s.ct,0) from t_p_virdepart_eparchy v left join (\n" +
                "select b.eparchy_code,COUNT(*) as ct from\n" +
                "(select staff_id from t_p_logininfo where login_date BETWEEN ${mysql_between}) a\n" +
                "join (\n" +
                "SELECT DISTINCT a.eparchy_code,a.staff_id FROM t_p_mobstaff a, t_p_roleowner b\n" +
                "WHERE a.staff_id=b.staff_id AND b.end_date>NOW() AND b.role_code IN ('F0005','F0010','F0014','F0015')\n" +
                "and exists (select * from t_p_roleowner r where r.staff_id=a.staff_id and r.role_code='F0015' and r.end_date>now())\n" +
                ") b on b.staff_id=a.staff_id\n" +
                "GROUP BY b.eparchy_code\n" +
                ") s on s.eparchy_code=v.rsvalue2 order by v.rsvalue2 desc";


        String key = "mysql_between";
        String value = "x and y...";


        String result = StringUtils.replaceParam(template, key, value);
        System.out.println(result);
    }

}