package com.bluexiii.reportman.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * Created by bluexiii on 20/10/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ReportManServiceTest {
    @Test
    public void getTemplateList() throws Exception {
        List<String> templateList = reportManService.getTemplateList();
        System.out.println(templateList.toString());
    }

    @Autowired
    private ReportManService reportManService;

    @Test
    public void makeReport() throws Exception {
        reportManService.makeReport("demo", null, null, false);
    }
}