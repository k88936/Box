package com.kvto;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.google.common.io.Files;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class Box extends JFrame {

    static final String OFF = "关闭";
    static final String ON = "开启";
    Logger logger;
    JTextArea LOG;


    public Box() {


        setIconImage(Toolkit.getDefaultToolkit().getImage("logo.png"));

        LoggerFactory.resisterLogger(new Logger() {
            @Override
            public void error(String message, IOException e) {
                LOG.append(message + '\n');


            }

            @Override
            public void info(String message) {
                println(message);
            }

            @Override
            public void print(String message) {
                LOG.append(message);
            }

            @Override
            public void println(String message) {
                LOG.append(message + '\n');

            }
        });

        initComponents();
        this.logger = LoggerFactory.getLogger();

        ComputeTimeLeft();

        //setIconImage();
        setTitle("Box");
        setSize(800, 480);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 将窗口居中显示

    }

    private void initComponents() {

        LOG = new JTextArea();
        LOG.setText("Box 1.0             " + new Date());
        LOG.setEditable(false);
        LOG.setLineWrap(true);
        LOG.setBackground(new Color(32, 32, 32));
        LOG.setAutoscrolls(true);
        JScrollPane scrollPane = new JScrollPane(LOG);
        scrollPane.setViewportView(LOG);
        ((DefaultCaret) LOG.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        // 设置滚动窗格的大小和位置
        scrollPane.setBounds(10, 10, 380, 200);
        scrollPane.setPreferredSize(new Dimension(380, 150));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JTabbedPane tabbedPane = new JTabbedPane();


        //firstTab
        JPanel Tab1 = new JPanel();
        GroupLayout layout;
        layout = new GroupLayout(Tab1);
        Tab1.setLayout(layout);

        JLabel label1 = new JLabel("人民日报评论");
        JButton button1 = new JButton("crawl");
        button1.addActionListener(e -> new Thread(() -> {
            new HMLYSpider(30917322);
            logger.info("Crawling is over");
        }).start());


        layout.setHorizontalGroup(layout.createParallelGroup()

                .addGroup(layout.createSequentialGroup().addGap(10).addComponent(label1).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(button1).addGap(10))

        );

        layout.setVerticalGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(label1).addComponent(button1)).addGap(10)
//
        );


        JPanel Tab2 = new JPanel();
        layout = new GroupLayout(Tab2);
        Tab2.setLayout(layout);

        JLabel label2 = new JLabel("记录剪切板");
        JCheckBox checkbox2 = new JCheckBox(OFF);
        checkbox2.setSelected(false);


        var clipboardRecorder = new ClipboardRecorder();
        checkbox2.addItemListener(e -> {
            if (checkbox2.isSelected()) {
                checkbox2.setText(ON);
                clipboardRecorder.beginRecord();
            } else {
                checkbox2.setText(OFF);
                clipboardRecorder.endRecord();
            }
        });

        var ocrRecorder = new OCRRecorder();

        JLabel label3 = new JLabel("记录OCR");
        JCheckBox checkbox3 = new JCheckBox(OFF);
        checkbox3.setSelected(false);

        checkbox3.addItemListener(e -> {

            if (checkbox3.isSelected()) {
                checkbox3.setText(ON);
                try {
                    ocrRecorder.beginRecord();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }

            } else {
                checkbox3.setText(OFF);
                try {
                    ocrRecorder.endRecord();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        JLabel label4 = new JLabel("打印记录");
        JButton button4 = new JButton("打印");
        button4.addActionListener(e -> {
            try {
                Printer.BufferedFlush();
                Printer.printBuffer();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        JLabel label4b = new JLabel("保存记录");
        JButton button4b = new JButton("保存");
        button4b.addActionListener(e -> {


            Printer.SaveBuffer();
            try {
                var file = FileManager.getFile("Printer", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt");
                Files.copy(FileManager.getFile("Printer", "Buffer.txt"), file);
                Desktop.getDesktop().open(file);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            // 调用系统默认软件打开文件


        });
        JLabel label5 = new JLabel("清空打印队列");
        JButton button5 = new JButton("clear");
        button5.addActionListener(e -> {
            Printer.clearQueue();
        });


        layout.setHorizontalGroup(layout.createParallelGroup()

                .addGroup(layout.createSequentialGroup().addGap(10).addComponent(label2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(checkbox2).addGap(10))

                .addGroup(layout.createSequentialGroup().addGap(10).addComponent(label3).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(checkbox3).addGap(10))

                .addGroup(layout.createSequentialGroup().addGap(10).addComponent(label4).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(button4).addGap(10))

                .addGroup(layout.createSequentialGroup().addGap(10).addComponent(label4b).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(button4b).addGap(10))

                .addGroup(layout.createSequentialGroup().addGap(10).addComponent(label5).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(button5).addGap(10))

        );


        layout.setVerticalGroup(layout.createSequentialGroup()

                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(label2).addComponent(checkbox2)).addGap(10)

                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(label3).addComponent(checkbox3)).addGap(10)

                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(label4).addComponent(button4)).addGap(10)

                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(label4b).addComponent(button4b)).addGap(10)

                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(label5).addComponent(button5)).addGap(10)

        );


        JPanel Tab3 = new JPanel();
        layout = new GroupLayout(Tab3);
        Tab3.setLayout(layout);

        JLabel label31 = new JLabel("000");
        JCheckBox checkbox31 = new JCheckBox(OFF);
        checkbox31.setSelected(false);

        checkbox31.addItemListener(e -> {
            if (checkbox31.isSelected()) {

                checkbox31.setText(ON);


            } else {
                checkbox31.setText(OFF);
            }
        });


        JLabel label32 = new JLabel("OCR");
        JCheckBox checkbox32 = new JCheckBox(ON);
        checkbox32.setSelected(true);

        checkbox32.addItemListener(e -> {
            if (checkbox32.isSelected()) {
                checkbox32.setText(ON);
            } else {
                checkbox32.setText(OFF);
            }
        });
        layout.setHorizontalGroup(layout.createParallelGroup()

                .addGroup(layout.createSequentialGroup().addGap(10).addComponent(label31).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(checkbox31).addGap(10))

                .addGroup(layout.createSequentialGroup().addGap(10).addComponent(label32).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(checkbox32).addGap(10)));

        layout.setVerticalGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(label31).addComponent(checkbox31)).addGap(10).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(label32).addComponent(checkbox32)));


        tabbedPane.addTab("Text", Tab2);
        tabbedPane.addTab("Spider", Tab1);
        tabbedPane.addTab("Image", Tab3);

        getContentPane().add(tabbedPane, BorderLayout.CENTER);


        getContentPane().add(scrollPane, BorderLayout.SOUTH);


        pack();
    }

    private void ComputeTimeLeft() {
        // 获取当前日期和时间
        LocalDateTime now = LocalDateTime.now();


        // 设置高考日期（假设为2024年6月7日上午9点）
        LocalDate gaokaoDate = LocalDate.of(2024, Month.JUNE, 7);
        LocalTime gaokaoTime = LocalTime.of(9, 0);
        LocalDateTime gaokaoDateTime = LocalDateTime.of(gaokaoDate, gaokaoTime);

        // 计算时间差
        long months = ChronoUnit.MONTHS.between(now, gaokaoDateTime);
        long days = ChronoUnit.DAYS.between(now, gaokaoDateTime);
        long hours = ChronoUnit.HOURS.between(now, gaokaoDateTime);
        long minutes = ChronoUnit.MINUTES.between(now, gaokaoDateTime);
        long seconds = ChronoUnit.SECONDS.between(now, gaokaoDateTime);

        // 打印结果
        logger.print("\n 距离高考还有:");
        logger.println("\t" + months + "\t 个月");
        logger.println("\t" + days + "\t 天");
        logger.println("\t" + hours + "\t 小时");
        logger.println("\t" + minutes + "\t 分钟");
        logger.println("\t" + seconds + "\t 秒");
    }

    public static void main(String[] args) {


        FlatDarculaLaf.setup();


        EventQueue.invokeLater(() -> {
            JFrame frame = new Box();
            frame.setVisible(true);
        });
    }


}