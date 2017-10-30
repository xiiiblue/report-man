package com.bluexiii.reportman;

import com.bluexiii.reportman.gui.AppFrame;
import com.bluexiii.reportman.gui.JTextAreaOutputStream;
import com.bluexiii.reportman.service.ReportManService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Created by bluexiii on 17/10/2017.
 */
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
@SpringBootApplication
@EnableAsync
public class ReportManApplication implements ApplicationRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportManApplication.class);
    @Autowired
    private ReportManService reportManService;
    @Autowired
    private ApplicationContext context;

    public static void main(String[] args) {
        if (System.getProperty("web") == null) {
            new SpringApplicationBuilder(ReportManApplication.class).headless(false).web(false).run(args);
        } else {
            LOGGER.info("启用WEB界面");
            SpringApplication.run(ReportManApplication.class, args);
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (System.getProperty("web") == null) {
            if (args.getOptionValues("custom.file.prefix") == null) {
                LOGGER.info("启动图形界面");
                // 延迟初始化窗体
                AppFrame appFrame = context.getBean(AppFrame.class);
                appFrame.setVisible(true);
                // 在窗口中显示日志
                OutputStream out = new JTextAreaOutputStream(appFrame.getLogTextArea());
                System.setOut(new PrintStream(out));
                System.setErr(new PrintStream(out));
            } else {
                LOGGER.info("启动文本界面");
                reportManService.init();
                reportManService.makeReport();
            }
        }
    }
}
