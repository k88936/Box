import java.io.IOException;
import java.util.Objects;

import static java.lang.Thread.sleep;

public class PrinterController {

    public static void main(String[] args) throws InterruptedException, IOException {


        Selector selector=new Selector();
        Printer printer=new Printer();

        Server server = new Server();


        boolean PrevPrinting=false;

        boolean autoNext=false;

        server.send("hello !!! ready to print !!!");

        String HELP_SETTING= "----next 下一个文件--------redo 重新打印这份文件，auto下请在这份打印完前进行--------info 获取文件名，是否在打印等信息--------auto 开启自动换文件--------stop 停止自动换文件--------exit 结束程序----";



        String message;

        boolean nowPrinting = false;

        while (true){
            nowPrinting=printer.isPrinting(nowPrinting);

           // System.out.println(nowPrinting );

            if (selector.noMoreFiles){
                selector.removeCurrentFile();
                System.exit(0);
            }

            if (server.messageIsNew) {

                message = server.getMessageReceived();
                String[] methods =message  .split(" ");

                if(methods.length ==0){
                    methods[0]=message;
                }

                switch (methods[0]) {
                    case "next" -> {




                        if (!nowPrinting) {

                            selector.removeCurrentFile();
                            if(printer.allPdf){
                                    server.send("强制使用小纸，转换花费时间，请耐心等待");
                                }
                            printer.print(selector.getFile());


                            if (selector.currentFile != null) {
                                server.send("begin printing " + selector.currentFile.getName());


                                if(!printer.allPdf){
                                     if (selector.currentFile.getName().contains(".doc")) {
                                    server.send("----TIPS: USE A4 ");
                                }else{
                                    server.send("----TIPS: USE B5 ");
                                }
                                }


                                // System.out.println("begin printing "+ selector.currentFile.getName());
                            }


                        } else {
                            server.send("REFUSED: Printer is busy ");

                        }

                        break;
                    }
                    case "redo" -> {
                        server.send("redo ");
                        printer.rePrint(selector.currentFile);
                        if (selector.currentFile.getName().contains(".doc")) {
                            server.send("----TIPS: USE A4 ");
                        }


                        break;
                    }
                    case "info" -> {

                        if (selector.currentFile != null) {
                            server.send("now is " + selector.currentFile.getName());
                            // System.out.println("now is " + selector.currentFile.getName());
                        }
                        server.send("isPrinting " + nowPrinting);
                        //System.out.println("is printing "+nowPrinting);

                        break;
                    }
                    case "auto" -> {
                        autoNext = true;
                        server.send("autoNext start send --STOP-- to stop and tap NEXT to start");

                        break;
                    }
                    case "stop" -> {
                        server.send("autoNext  stop");
                        autoNext = false;
                        break;
                    }
                    case "test" -> {
                        selector.getFile();
                        if (selector.currentFile != null) {
                            server.send("begin printing " + selector.currentFile.getName());
                            if (selector.currentFile.getName().contains(".doc")) {
                                server.send("----TIPS: USE A4 ");
                            }
                            // System.out.println("begin printing "+ selector.currentFile.getName());
                        }
                        break;
                    }
                    case "force" -> {

                        server.send("强制打印开启，任何next将导致立刻打印");
                        printer.forcePrint=true;
                        break;
                    }
                    case "help" -> {

                        server.send(HELP_SETTING);
                        break;
                    }
                    case "exit" -> {
                        selector.removeCurrentFile();
                        printer.finl();
                        System.exit(0);

                        break;
                    }
                    case "apdf"->{
                        server.send("使用小纸，转换可能花费时间，请耐心等待");
                        printer.allPdf=true;
                    }
                    case "npdf"->{
                        printer.allPdf=false;
                    }
                    default -> {
                        System.out.println(methods[0]);
                        autoNext = false;
                    }
                }


            }


            sleep(100);


             if ( PrevPrinting&(!nowPrinting) ){

                 if (selector.currentFile != null) {
                     System.out.println("printed " + selector.currentFile.getName());
                     server.send("printed " + selector.currentFile.getName());
                 }


                 if ( autoNext ){

                     server.send("autoNext "+ Objects.requireNonNull(selector.currentFile).getName());
                     System.out.println("autoNext "+ selector.currentFile.getName());
                     sleep(2000);
                     selector.removeCurrentFile();
                     printer.print(selector.getFile());
                 }

             }



             PrevPrinting=nowPrinting;
        }

        


    }



}