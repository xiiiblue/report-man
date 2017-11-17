package com.bluexiii.reportman.util;

import com.bluexiii.reportman.util.JdbcUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by bluexiii on 17/10/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class JdbcUtilsTest {
    @Autowired
    private JdbcUtils jdbcUtils;

    @Test
    public void queryForRowSet() throws Exception {
        SqlRowSet sqlRowSet = jdbcUtils.queryForRowSet("mobsale", "SELECT 123");
        assertThat(sqlRowSet).isNotNull();
    }
}