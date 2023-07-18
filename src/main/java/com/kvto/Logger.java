package com.kvto;

import java.io.IOException;

public interface Logger {


    void error(String message, IOException e);

    void info(String message);

    void print(String message);

    void println(String message);


}
