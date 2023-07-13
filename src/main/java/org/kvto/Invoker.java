public class Invoker {

    public static native void print(String FilePath);


    static{
        System.loadLibrary("FileOperator");

    }

}