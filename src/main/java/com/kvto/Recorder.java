package com.kvto;

import com.baidu.ai.aip.imageOCR;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;

public abstract class Recorder {


    Logger logger;
    boolean running = true;

    //   abstract public void collect() throws Exception;
    Recorder() {

        logger = LoggerFactory.getLogger();
    }

    abstract public void beginRecord() throws Exception;

    abstract public void endRecord() throws Exception;


}

class ClipboardRecorder extends Recorder implements Runnable {


//public final ArrayList<String> clipboardContent=new ArrayList<>();

    Thread thread = null;

    ClipboardRecorder() {
        super();
    }

    @Override
    public void beginRecord() {
        this.running = true;
        thread = new Thread(this);
        thread.start();
        logger.info("ClipboardRecord start");
    }

    @Override
    public void endRecord() {
        logger.info("ClipboardRecord end");
        running = false;
    }

//     @Override
//     public void collect() throws IOException {
//         BufferedWriter bufferedWriter = Printer.bufferedWriter;
//         for (String s : clipboardContent) {
//             bufferedWriter.write(s);
//         }
//         synchronized (clipboardContent) {
//             clipboardContent.clear();
//         }
//     }


    @Override
    public void run() {
        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable prevContents = clipboard.getContents(null);

            this.running = true;

            while (running) {
                Transferable contents = clipboard.getContents(null);


                if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    String text = (String) contents.getTransferData(DataFlavor.stringFlavor);

                    if (prevContents != null && !text.equals(prevContents.getTransferData(DataFlavor.stringFlavor))) {
//                        synchronized (clipboardContent){
//                         clipboardContent.add(text);
//                        }

                        logger.info("Recorded：" + text);
                        Printer.BufferedWrite(text);
                        logger.print("\r\n");
                        Printer.BufferedWrite("\r\n");
                    }
                }

                prevContents = contents;
                Thread.sleep(800);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

class OCRRecorder extends Recorder {


    OCRRecorder() {

        super();
        GlobalScreen.addNativeKeyListener(new NativeKeyListener() {
            @Override
            public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
                NativeKeyListener.super.nativeKeyTyped(nativeEvent);
//                System.out.println("Key Released: " + NativeKeyEvent.getKeyText(nativeEvent.getKeyCode()));
//                System.out.println(nativeEvent.getKeyCode());
//                System.out.println(running);
                if (running && nativeEvent.getKeyCode() == 3639) {
                    var file = new File(FileManager.getDictionaryPath("OCR") + "\\" + System.currentTimeMillis() + ".png");

                    Cmder.executeCmdCommand("D:\\Snipaste\\Snipaste.exe snip -o " + file.getAbsolutePath());


                    new Thread("ocr") {
                        @Override
                        public void run() {
                            while (!file.exists() && running) {
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            try {
                                OCR(file);

                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }

                        }
                    }.start();


                }
            }


        });
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            throw new RuntimeException(e);
        }

    }


    private void OCR(File file) throws Exception {
        String content = imageOCR.imageOCR(file);
        if (content == null) {
            return;
        }

        logger.info("OCR got");
        JSONObject jsonObject = new JSONObject(content.replaceAll("[\n]", ""));
        JSONArray wordList;
        try {
            wordList = jsonObject.getJSONArray("words_result");
        } catch (Exception e) {
            return;
        }

        for (int j = 0; j < wordList.length(); j++) {
            String word = wordList.getJSONObject(j).getString("words");
            Printer.BufferedWrite(word);
            logger.print(word);
        }
        logger.print("\r\n");
        Printer.BufferedWrite("\r\n");
    }

    @Override
    public void beginRecord() throws Exception {
        logger.info("OCRRecord start");
        running = true;

    }

//    @Override
//    public void collect() throws Exception {
//
//
//        var ocr = FileManager.getDictionary("OCR");
//        for (File file : Objects.requireNonNull(ocr.listFiles())) {
//            OCR(file);
//        }
//    }

    @Override
    public void endRecord() throws Exception {
        logger.info("OCRRecord end");
        running = false;
//        GlobalScreen.unregisterNativeHook();
    }


}