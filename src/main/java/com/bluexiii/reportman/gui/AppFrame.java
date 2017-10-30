package com.bluexiii.reportman.gui;

import com.bluexiii.reportman.ReportManApplication;
import com.bluexiii.reportman.property.StaticProperty;
import com.bluexiii.reportman.service.ReportManService;
import com.google.common.io.Files;
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
import java.io.*;

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
    private StaticProperty staticProperty;
    private JPanel mainPanel;
    private JButton submitBtn;
    private JTextField reportTextField;
    private JLabel reportLabel;
    private JTextField templateTextField;
    private JLabel templateLabel;
    private JButton templateButton;
    private JButton reportButton;
    private JLabel messageLabel;
    private JScrollPane LogScrollPane;
    private JTextArea logTextArea;

    public JTextArea getLogTextArea() {
        return logTextArea;
    }

    @PostConstruct
    public void init() {
        templateTextField.setText(staticProperty.getTemplatePath());
        reportTextField.setText(staticProperty.getReportPath());
        LOGGER.info("窗体初始化完成");
    }

    public AppFrame() throws HeadlessException, FileNotFoundException, UnsupportedEncodingException {
        // 初始化窗体
        setSize(600, 400);
        setContentPane(mainPanel);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Report-Man");
        logTextArea.setEditable(false);

        // 开始按钮点击事件
        submitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 按钮置失效
                submitBtn.setEnabled(false);
                templateButton.setEnabled(false);
                reportButton.setEnabled(false);

                // 重置日志显示
                logTextArea.setText("");
                LOGGER.info("开始生成报表...");


                SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                    public String doInBackground() {
                        // 生成报表
                        reportManService.init();
                        reportManService.makeReport();

                        // 按钮置生效
                        submitBtn.setEnabled(true);
                        templateButton.setEnabled(true);
                        reportButton.setEnabled(true);
                        JOptionPane.showMessageDialog(null, "报表生成成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                        return "";
                    }
                };
                worker.execute();
            }
        });

        // 更改模板点击事件
        templateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(staticProperty.getTemplateDir()));
                int returnVal = fileChooser.showOpenDialog(mainPanel);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    String templateDir = file.getParent();
                    String fileName = file.getName();
                    String filePrefix = Files.getNameWithoutExtension(fileName);
                    String fileExtension = Files.getFileExtension(fileName);
                    if (!fileExtension.equals("xlsx")) {
                        JOptionPane.showMessageDialog(null, "模版必须是后缀为.xlsx的Excel文件", "错误", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // 重设模板路径
                    staticProperty.setTemplateDir(templateDir);
                    staticProperty.setFilePrefix(filePrefix);

                    // 界面元素赋值
                    templateTextField.setText(staticProperty.getTemplatePath());
                    reportTextField.setText(staticProperty.getReportPath());
                } else {
                    LOGGER.info("用户取消了文件选取");
                }
            }
        });

        // 更改报表目录点击事件
        reportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(staticProperty.getReportDir()));
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = fileChooser.showOpenDialog(mainPanel);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    String reportDir = file.getPath();

                    // 重设报表路径
                    staticProperty.setReportDir(reportDir);

                    // 界面元素赋值
                    reportTextField.setText(staticProperty.getReportPath());
                } else {
                    LOGGER.info("用户取消了文件选取");
                }
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
        mainPanel.setLayout(new GridLayoutManager(4, 3, new Insets(15, 15, 15, 15), -1, -1));
        reportLabel = new JLabel();
        reportLabel.setText("报表路径");
        mainPanel.add(reportLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        reportTextField = new JTextField();
        mainPanel.add(reportTextField, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        templateTextField = new JTextField();
        mainPanel.add(templateTextField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        templateLabel = new JLabel();
        templateLabel.setText("模版路径");
        mainPanel.add(templateLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        templateButton = new JButton();
        templateButton.setText("更改模版");
        mainPanel.add(templateButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        reportButton = new JButton();
        reportButton.setText("更改目录");
        mainPanel.add(reportButton, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        submitBtn = new JButton();
        submitBtn.setText("生成报表");
        mainPanel.add(submitBtn, new GridConstraints(3, 0, 1, 3, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        messageLabel = new JLabel();
        messageLabel.setText("运行日志");
        mainPanel.add(messageLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        LogScrollPane = new JScrollPane();
        mainPanel.add(LogScrollPane, new GridConstraints(2, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        logTextArea = new JTextArea();
        LogScrollPane.setViewportView(logTextArea);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}
