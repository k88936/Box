package com.kvto;

public class LoggerFactory {

    private static com.kvto.Logger Logger;

    public static void resisterLogger(Logger logger) {
        LoggerFactory.Logger = logger;
    }

    public static com.kvto.Logger getLogger() {
        return Logger;
    }
}
