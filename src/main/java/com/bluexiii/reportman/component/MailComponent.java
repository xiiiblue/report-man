package com.bluexiii.reportman.component;

import com.bluexiii.reportman.property.DynamicProperty;
import com.bluexiii.reportman.property.StaticProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Map;
import java.util.Properties;

/**
 * Created by bluexiii on 17/10/2017.
 */
@Component
public class MailComponent {
    private static final Logger LOGGER = LoggerFactory.getLogger(MailComponent.class);
    @Autowired
    private StaticProperty staticProperty;
    @Autowired
    private DynamicProperty dynamicProperty;
    private JavaMailSenderImpl mailSender;

    /**
     * 初始化邮箱
     */
    public void init() {
        // 读取配置
        Map<String, String> configMap = dynamicProperty.getConfigMap();

        // 配置邮箱
        mailSender = new JavaMailSenderImpl();
        mailSender.setHost(configMap.get("mail.smtp.host"));
        mailSender.setUsername(configMap.get("mail.sender.username"));
        mailSender.setPassword(configMap.get("mail.sender.password"));

        // 配置属性及代理
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.ssl.enable", "true");
        String proxyEnable = configMap.get("mail.proxy.enable");
        if (proxyEnable != null && proxyEnable.equals("Y")) {
            String proxyHost = configMap.get("mail.proxy.host");
            String proxyPort = configMap.get("mail.proxy.port");
            LOGGER.info("使用代理服务器: {}:{}", proxyHost, proxyPort);

            properties.setProperty("mail.smtp.socks.host", proxyHost);
            properties.setProperty("mail.smtp.socks.port", proxyPort);
            properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        }
        mailSender.setJavaMailProperties(properties);
    }

    /**
     * 发送邮件
     *
     * @param mailFrom
     * @param mailToList
     * @param mailCcList
     * @param mailSubject
     * @param mailMessage
     * @param attachmentPath
     * @param attachmentName
     * @throws MessagingException
     */
    public void send(String mailFrom, String mailToList, String mailCcList, String mailSubject, String mailMessage, String attachmentPath, String attachmentName) throws MessagingException {
        // 配置邮件
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom(mailFrom);
        helper.setTo(mailToList.split(","));
        helper.setCc(mailCcList.split(","));
        helper.setSubject(mailSubject);
        helper.setText(mailMessage);

        // 添加附件
        if (attachmentPath != null & attachmentName != null) {
            FileSystemResource attachmentFile = new FileSystemResource(new File(attachmentPath));
            helper.addAttachment(attachmentName, attachmentFile);
        }

        // 发送邮件
        mailSender.send(mimeMessage);
    }

    /**
     * 发送邮件
     *
     * @throws MessagingException
     */
    public void send() throws MessagingException {
        // 读取配置
        Map<String, String> configMap = dynamicProperty.getConfigMap();
        String mailFrom = configMap.get("mail.from");
        String mailToList = configMap.get("mail.to.list");
        String mailCcList = configMap.get("mail.cc.list");
        String mailSubject = configMap.get("mail.subject") + "@" + staticProperty.getDateStr();
        String mailMessage = configMap.get("mail.message") + "\n\n" + "统计时间:" + staticProperty.getTimeStr() + "\n\n";
        String attachmentPath = staticProperty.getReportPath();
        String attachmentName = configMap.get("mail.attachment") + staticProperty.getDayStr() + ".xlsx";

        LOGGER.info("收件人: {}", mailToList);
        LOGGER.info("抄送人: {}", mailCcList);

        // 发送邮件
        send(mailFrom, mailToList, mailCcList, mailSubject, mailMessage, attachmentPath, attachmentName);
    }
}
