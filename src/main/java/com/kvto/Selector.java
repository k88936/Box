package com.kvto;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


public class Selector {


    public static final String FilePath = "E:\\PRINT";

    ArrayList<File> fileList;

    Selector() {


        StringBuilder rexConstructor;
        try {

            rexConstructor = new StringBuilder();
            Scanner IgnoreScanner = new Scanner(new File(FilePath + File.separator + ".ignore.txt"));
            while (IgnoreScanner.hasNextLine()) {

                String data = IgnoreScanner.nextLine();
                rexConstructor.append(data);
                rexConstructor.append("|");
            }
            rexConstructor.append(".ignore.txt");

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }


        String[] Files = new File(FilePath).list();


        fileList = new ArrayList<>();

        for (String s : Objects.requireNonNull(Files)) {
            File file = new File(FilePath + File.separator + s);
            System.out.println(file.getName());

            if (s.matches(rexConstructor.toString())) {
                // System.out.println(file.getName());
                continue;

            }

            if (file.isDirectory()) {
                System.out.println("added all in"+file.getName());
                fileList.addAll(Arrays.asList(Objects.requireNonNull(file.listFiles())));
            }else{
                fileList.add(file);
            }


        }

        FileIterator = fileList.iterator();


        //currentFile=FileIterator.next();
    }

    Iterator<File> FileIterator;
    File currentFile =null;
    boolean noMoreFiles = false;

    File getFile(){
        if (FileIterator.hasNext()) {
            return currentFile=FileIterator.next();


        }else {
            noMoreFiles = true;
//
            return null;

        }
    }

    void removeFile(File file){
        if (file != null) {
            //fileList.remove(file);

            Desktop.getDesktop().moveToTrash(file);
            // file.deleteOnExit();
        }

    }

    void removeCurrentFile(){
        removeFile(currentFile);
        currentFile = null;
    }
}

