package org.kvto;

public interface Logger {

    public static void log(String message) {
        System.out.println(message);
    }

    public static void log(Exception e) {
        System.out.println(e.getMessage());
    }


}
