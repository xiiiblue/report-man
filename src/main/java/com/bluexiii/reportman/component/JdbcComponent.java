package com.bluexiii.reportman.component;

import com.bluexiii.reportman.property.DynamicProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bluexiii on 17/10/2017.
 */
@Component
public class JdbcComponent {
    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcComponent.class);
    @Autowired
    private DynamicProperty dynamicProperty;
    private Map<String, JdbcTemplate> jdbcTemplateMap = new HashMap<>();

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
        Map<String, String> configMap = dynamicProperty.getConfigMap();
        if (!jdbcTemplateMap.containsKey(connTag)) {
            String dbDriver = configMap.get("db." + connTag + ".driver");
            String dbUrl = configMap.get("db." + connTag + ".url");
            String dbUsername = configMap.get("db." + connTag + ".username");
            String dbPassword = configMap.get("db." + connTag + ".password");
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
    public void execute(String connTag, String sql) {
        getJdbcTemplate(connTag).execute(sql);
    }
}
