package com.bluexiii.reportman.component;

import org.apache.poi.xssf.usermodel.XSSFSheet;
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
public class ExcelComponentTest {
    @Autowired
    private ExcelComponent excelComponent;

    @Test
    public void readSheet() throws Exception {
        XSSFSheet sheet = excelComponent.readSheet(1);
        int lastRowNum = sheet.getLastRowNum();
        assertThat(lastRowNum).isGreaterThan(0);
    }

    @Test
    public void removeSheet() throws Exception {
        excelComponent.removeSheet(1);
    }
}