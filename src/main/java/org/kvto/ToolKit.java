package org.kvto;

import javax.swing.*;
import java.awt.*;

public class ToolKitFrame extends JFrame {

    public ToolKitFrame() {
        initComponents();
        setTitle("ToolKit");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 将窗口居中显示
    }

    private void initComponents() {


        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel firstTab = new JPanel();
        firstTab.setLayout(new FlowLayout());
        JLabel label1 = new JLabel("这是第一个选项卡");
        JButton button1 = new JButton("按钮1");
        firstTab.add(label1);
        firstTab.add(button1);

        JPanel secondTab = new JPanel();
        secondTab.setLayout(new FlowLayout());
        JLabel label2 = new JLabel("这是第二个选项卡");
        JButton button2 = new JButton("按钮2");
        secondTab.add(label2);
        secondTab.add(button2);

        tabbedPane.addTab("选项卡1", firstTab);
        tabbedPane.addTab("选项卡2", secondTab);

        getContentPane().add(tabbedPane, BorderLayout.CENTER);

        pack();
    }


}