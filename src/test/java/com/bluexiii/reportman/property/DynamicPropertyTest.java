package com.bluexiii.reportman.property;

import com.bluexiii.reportman.model.TaskModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by bluexiii on 20/10/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DynamicPropertyTest {

    @Autowired
    private DynamicProperty dynamicProperty;

    @Test
    public void getConfigMap() throws Exception {
        Map<String, String> configMap = dynamicProperty.getConfigMap();
        assertThat(configMap).isNotEmpty();
    }

    @Test
    public void getParamMap() throws Exception {
        Map<String, String> paramMap = dynamicProperty.getSqlParamMap();
        System.out.println(paramMap);
        assertThat(paramMap).isNotEmpty();
    }

    @Test
    public void getTaskList() throws Exception {
        List<TaskModel> taskList = dynamicProperty.getTaskList();
        System.out.println(taskList);
        assertThat(taskList).isNotEmpty();
    }
}