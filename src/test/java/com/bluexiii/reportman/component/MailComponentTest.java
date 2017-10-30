package com.bluexiii.reportman.component;

import com.bluexiii.reportman.property.DynamicProperty;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

/**
 * Created by bluexiii on 20/10/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MailComponentTest {
    @Autowired
    private MailComponent mailComponent;
    @Autowired
    private DynamicProperty dynamicProperty;

    @Test
    public void send() throws Exception {
        Map<String, String> configMap = dynamicProperty.getConfigMap();
        String mailFrom = configMap.get("mail.from");
        String mailToList = configMap.get("mail.to.list");
        String mailCcList = configMap.get("mail.cc.list");
        String mailSubject = "测试标题";
        String mailMessage = "测试内容";

        mailComponent.send(mailFrom, mailToList, mailCcList, mailSubject, mailMessage, null, null);
    }
}