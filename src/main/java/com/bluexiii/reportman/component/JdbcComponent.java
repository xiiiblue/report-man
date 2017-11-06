package com.bluexiii.reportman.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bluexiii on 17/10/2017.
 */
public class JdbcComponent {
    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcComponent.class);
    private Map<String, JdbcTemplate> jdbcTemplateMap = new HashMap<>();
    private Map<String, String> sysParamMap;

    public JdbcComponent(Map<String, String> sysParamMap) {
        this.sysParamMap = sysParamMap;
    }

    /**
     * 获取数据源
     *
     * @param dbDriver
     * @param dbUrl
     * @param dbUsername
     * @param dbPassword
     * @return
     */
    private DataSource getDataSource(String dbDriver, String dbUrl, String dbUsername, String dbPassword) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(dbDriver);
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(dbUsername);
        dataSource.setPassword(dbPassword);
        return dataSource;
    }

    /**
     * 获取JdbcTemplate
     *
     * @param connTag
     * @return
     */
    public JdbcTemplate getJdbcTemplate(String connTag) {
        LOGGER.debug("获取JdbcTemplate: {}", connTag);
        if (!jdbcTemplateMap.containsKey(connTag)) {
            String dbDriver = sysParamMap.get("db." + connTag + ".driver");
            String dbUrl = sysParamMap.get("db." + connTag + ".url");
            String dbUsername = sysParamMap.get("db." + connTag + ".username");
            String dbPassword = sysParamMap.get("db." + connTag + ".password");
            DataSource source = getDataSource(dbDriver, dbUrl, dbUsername, dbPassword);
            JdbcTemplate jdbcTemplate = new JdbcTemplate(source);
            jdbcTemplateMap.put(connTag, jdbcTemplate);
        }
        return jdbcTemplateMap.get(connTag);
    }

    /**
     * 获取查询结果集
     *
     * @param connTag
     * @param sql
     * @return
     */
    public SqlRowSet queryForRowSet(String connTag, String sql) {
        return getJdbcTemplate(connTag).queryForRowSet(sql);
    }

    /**
     * 插入/更新/删除/DDL操作
     *
     * @param connTag
     * @param sql
     */
    @Transactional
    public void execute(String connTag, String sql) {
        getJdbcTemplate(connTag).execute(sql);
    }
}
