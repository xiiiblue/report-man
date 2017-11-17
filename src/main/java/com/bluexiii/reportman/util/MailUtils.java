package com.bluexiii.reportman.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

/**
 * Created by bluexiii on 17/10/2017.
 */
public class MailUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(MailUtils.class);
    private JavaMailSenderImpl mailSender;
    private Map<String, String> sysParamMap;

    public MailUtils(Map<String, String> sysParamMap) {
        this.sysParamMap = sysParamMap;
    }

    /**
     * 初始化邮箱
     */
    public void init(String host, String username, String password, String proxyEnable, String proxyHost, String proxyPort) {
        // 配置邮箱
        mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        // 配置属性及代理
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.ssl.enable", "true");
        if (proxyEnable != null && proxyEnable.equals("Y")) {
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
     * 使用系统参数发送邮件
     *
     * @throws MessagingException
     */
    public void sendWithParamMap(String reportPath) throws MessagingException {
        // 初始化邮件
        String host = sysParamMap.get("mail.smtp.host");
        String username = sysParamMap.get("mail.sender.username");
        String password = sysParamMap.get("mail.sender.password");
        String proxyEnable = sysParamMap.get("mail.proxy.enable");
        String proxyHost = sysParamMap.get("mail.proxy.host");
        String proxyPort = sysParamMap.get("mail.proxy.port");

        init(host, username, password, proxyEnable, proxyHost, proxyPort);

        // 发送邮件
        String mailFrom = sysParamMap.get("mail.from");
        String mailToList = sysParamMap.get("mail.to.list");
        String mailCcList = sysParamMap.get("mail.cc.list");
        String mailSubject = sysParamMap.get("mail.subject");
        String mailMessage = sysParamMap.get("mail.message") + "\n\n" + "统计时间:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\n\n";
        String attachmentPath = reportPath;
        String attachmentName = sysParamMap.get("mail.attachment") + ".xlsx";

        LOGGER.info("收件人: {}", mailToList);
        LOGGER.info("抄送人: {}", mailCcList);

        send(mailFrom, mailToList, mailCcList, mailSubject, mailMessage, attachmentPath, attachmentName);
    }
}
