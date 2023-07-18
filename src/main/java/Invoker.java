

import org.junit.Test;

public class Invoker {

    static {
        System.loadLibrary("FileOperator");


    }

    @Test
    public void testInvoke() {


        print("E:\\OUTPUT\\outForLearningHelper.txt");

    }

    public static native void print(String FilePath);


}