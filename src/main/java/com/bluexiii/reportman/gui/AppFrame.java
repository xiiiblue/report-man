package com.bluexiii.reportman.gui;

import com.bluexiii.reportman.ReportManApplication;
import com.bluexiii.reportman.model.Progress;
import com.bluexiii.reportman.service.ProgressService;
import com.bluexiii.reportman.service.ReportManService;
import com.bluexiii.reportman.util.StringUtils;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by bluexiii on 23/10/2017.
 */
@Lazy
@Component
public class AppFrame extends JFrame {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportManApplication.class);
    @Autowired
    private ReportManService reportManService;
    @Autowired
    private ProgressService progressService;

    private JPanel mainPanel;
    private JButton submitBtn;
    private JScrollPane LogScrollPane;
    private JTextArea logTextArea;
    private JComboBox filePrefixList;
    private JProgressBar reportProgress;

    public JTextArea getLogTextArea() {
        return logTextArea;
    }

    @PostConstruct
    public void init() {
        // 填充下拉列表
        List<String> templateList = reportManService.getTemplateList();
        if (templateList.size() == 0) {
            submitBtn.setEnabled(false);
            JOptionPane.showMessageDialog(null, "未找到模板，请先将模板放置于[template]目录中！", "提示", JOptionPane.INFORMATION_MESSAGE);
        } else {
            for (String templateName : templateList) {
                filePrefixList.addItem(templateName);
            }
        }

        // 显示帮助
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            public String doInBackground() throws IOException {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logTextArea.setText("操作步骤:\n\n1. 在下拉列表中选择一个模板\n2. 点击[生成报表]按钮\n3. 稍后报表会出现在[report]目录中\n\n");
                return "";
            }
        };
        worker.execute();


    }

    public AppFrame() throws HeadlessException, FileNotFoundException, UnsupportedEncodingException {
        // 初始化窗体
        setSize(400, 300);
        setContentPane(mainPanel);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Report-Man");
        setResizable(false);
        logTextArea.setEditable(false);
        logTextArea.setLineWrap(true);

        // 进度条
        reportProgress.setMinimum(0);
        reportProgress.setMaximum(100);

        // 使用系统主题
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 开始按钮点击事件
        submitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 按钮置失效
                submitBtn.setEnabled(false);

                // 重置日志显示
                logTextArea.setText("");

                SwingWorker<String, Void> swingWorker = new SwingWorker<String, Void>() {
                    public String doInBackground() throws IOException {
                        // 生成报表
                        final String filePrefix = (String) filePrefixList.getSelectedItem();
                        final String fileSuffix = StringUtils.getFileSuffix();

                        // 进度条
                        SwingWorker<String, Void> swingWorker = new SwingWorker<String, Void>() {
                            public String doInBackground() throws IOException, InterruptedException {
                                reportProgress.setValue(0);
                                for (int i = 0; i <= 600; i++) {
                                    Progress progress = progressService.getProgress(fileSuffix);
                                    int percent = progress.getPercent();
                                    reportProgress.setValue(percent);
                                    if (percent >= 100) {
                                        return "";
                                    }
                                    Thread.sleep(1000);
                                }
                                return "";
                            }
                        };
                        swingWorker.execute();

                        // 执行报表服务
                        try {

                            reportManService.makeReport(filePrefix, fileSuffix, null, false);
                        } catch (RuntimeException e) {
                            e.printStackTrace();
                            submitBtn.setEnabled(true);
                            JOptionPane.showMessageDialog(null, "报表生成失败，请根据日志检查模板配置！", "失败", JOptionPane.INFORMATION_MESSAGE);
                            return "";
                        }

                        // 按钮置生效
                        submitBtn.setEnabled(true);
                        JOptionPane.showMessageDialog(null, "报表生成成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                        return "";
                    }
                };
                swingWorker.execute();
            }
        });


    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(3, 2, new Insets(15, 15, 10, 15), -1, -1));
        LogScrollPane = new JScrollPane();
        mainPanel.add(LogScrollPane, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        logTextArea = new JTextArea();
        LogScrollPane.setViewportView(logTextArea);
        submitBtn = new JButton();
        submitBtn.setText("生成报表");
        mainPanel.add(submitBtn, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        filePrefixList = new JComboBox();
        mainPanel.add(filePrefixList, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(236, 26), null, 0, false));
        reportProgress = new JProgressBar();
        mainPanel.add(reportProgress, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}
