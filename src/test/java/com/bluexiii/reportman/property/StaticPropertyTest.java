package com.bluexiii.reportman.property;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by bluexiii on 20/10/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class StaticPropertyTest {
    @Autowired
    private StaticProperty staticProperty;

    @Test
    public void getProperties() throws Exception {
        String dayStr = staticProperty.getDayStr();
        assertThat(dayStr).isNotEmpty();

        String dateStr = staticProperty.getDateStr();
        assertThat(dateStr).isNotEmpty();

        String timeStr = staticProperty.getTimeStr();
        assertThat(timeStr).isNotEmpty();

        String templatePath = staticProperty.getTemplatePath();
        assertThat(templatePath).isNotEmpty();

        String reportPath = staticProperty.getReportPath();
        assertThat(reportPath).isNotEmpty();
    }


}