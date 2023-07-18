package com.kvto;

import org.junit.Test;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Printer {
    private static final Method method;
    static Logger logger = LoggerFactory.getLogger();
    private static BufferedWriter bufferedWriter;

    static {
        try {
            method = Class.forName("Invoker").getMethod("print", String.class);
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public static void printBuffer() {

        SaveBuffer();

        print(FileManager.getFile("Printer", "Buffer.txt"));

//            BufferedWriter bufferedWriter1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileManager.getFile("Printer", "Buffer.txt"), false)));
//            bufferedWriter = bufferedWriter1;

    }

    public static void SaveBuffer() {
        try {
            bufferedWriter.close();
            bufferedWriter = null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void print(File file) {
        try {
            method.invoke(null, file.getCanonicalPath());
        } catch (IllegalAccessException | InvocationTargetException | IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void clearQueue() {
        new Thread(() -> {
            logger.info("clearing print queue");

            Cmder.executeBatScript("""
                    net stop spooler
                    del /Q /F %systemroot%\\System32\\spool\\PRINTERS\\*
                    net start spooler""", FileManager.getFile("Printer", ".bat").getAbsolutePath());

            logger.info("print queue cleared");
        }).start();

    }

    public static void BufferedWrite(String str) throws IOException {
        if (bufferedWriter == null) {
            try {
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileManager.getFile("Printer", "Buffer.txt"), false)));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        bufferedWriter.write(str);

    }

    public static void BufferedFlush() throws IOException {
        bufferedWriter.flush();
    }

    @Test
    public void testPrint() throws IOException {
        print(new File("E:\\OUTPUT\\outForLearningHelper.txt"));
    }
}



