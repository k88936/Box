package com.kvto;

import org.junit.Test;

import java.io.*;

public class FileManager {


    public static void write(String str, File file) {

    }

    public static String getDictionaryPath(String DicName) {

        var file = new File(".\\" + DicName);
        file.mkdirs();
        return file.getAbsolutePath();
    }

    public static void s(File file) throws FileNotFoundException {
        BufferedWriter out = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file, true)));

    }

    @Test

    public void test() throws FileNotFoundException {
        System.out.println(getFile("test", "test.txt").getAbsolutePath());
        System.out.println(getFile("test", "test.txt").getPath());
        try {
            System.out.println(getFile("test", "test.txt").getCanonicalPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static File getFile(String DicName, String FileName) {

        getDictionary(DicName);
        var file = new File(".\\" + DicName + "\\" + FileName);


        return file;

    }

    public static File getDictionary(String DicName) {

        var file = new File(".\\" + DicName);
        file.mkdirs();

        return file;
    }


}
