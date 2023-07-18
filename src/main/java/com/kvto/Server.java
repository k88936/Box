package com.kvto;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {

    public static final int SERVER_PORT = 3333;
    public String messageToSend ="";

    Server() {
        new Thread("console server") {
            @Override
            public void run() {
                while (true) {
                    try {
                        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                        String str = in.readLine();
                        synchronized (messageReceived){
                            messageReceived=str;

                            messageIsNew=true;
                        }

                        sleep(500);
                        System.out.println("Server: Received: '" + str + "'");

                        synchronized (messageToSend){
                            System.out.println(messageToSend);
                            messageToSend="";
                        }
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        }.start();










        /*

        connect to confirm who to send



        recieve

        sent












         */


        new Thread("net server"){
            @Override
            public void run() {
                try {

                    System.out.println("Server: Connecting...");
                    ServerSocket serverSocket = new ServerSocket(SERVER_PORT);

                    serverSocket.setSoTimeout(1000000000);

                    System.out.println("Server:Connected.");
                    while (true) {

                        //System.out.println("Server: Receiving...");


                        try (Socket client = serverSocket.accept()) {

                            BufferedReader in = new BufferedReader(
                                    new InputStreamReader(client.getInputStream()));
                            String str = in.readLine();


                            //TODO receive
                            System.out.println("Server: Received: '" + str + "'");

                            synchronized (messageReceived) {
                                messageReceived = str;

                                messageIsNew = true;
                            }


                            // 发送给客户端的消息
                            PrintWriter out = new PrintWriter(new BufferedWriter(
                                    new OutputStreamWriter(client.getOutputStream())), true);


                            //TODO feedback
                            sleep(500);
                            synchronized (messageToSend) {
                                //System.out.println(messageToSend);
                                out.print(messageToSend);
                                //out.println("sent to android message is:" +messageToSend);
                                messageToSend = "";
                            }


                            out.flush();


                        } catch (Exception e) {
                            System.out.println("Server: Error");
                            e.printStackTrace();
                        }
                        //System.out.println("Server: Done.");
                    }
                } catch (Exception e) {
                    System.out.println("Server: Error");
                    e.printStackTrace();
                }
            }
        }.start();

    }

    void send(String message){
        //System.out.println(message);
        synchronized (messageToSend){
            messageToSend=messageToSend+message;
        }

    }

    public String messageReceived="";

    String getMessageReceived(){
        synchronized (messageReceived){
            messageIsNew=false;
            return messageReceived;
        }


    }

    boolean messageIsNew=false;

}