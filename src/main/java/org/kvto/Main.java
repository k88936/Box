import com.baidu.ai.aip.imageOCR;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import controlP5.ControlP5;
import controlP5.Textarea;
import org.json.JSONArray;
import org.json.JSONObject;
import processing.core.PApplet;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;

public class Main extends PApplet {

    ControlP5 cp5;
    private static Robot robot;
    private static Main THIS;

static int times=0;


final static int BACKGROUND=40;

static int shotMode = 0;

static int pictureCount=0;
//static Date date;
    public static void main(String[] args) {
        try{
            robot = new Robot();
        }catch (Exception e){
            System.out.println(e.getMessage());
        e.printStackTrace();

    }


        try {
            GlobalScreen.registerNativeHook();
        }
        catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());

            System.exit(1);
        }

        GlobalScreen.addNativeKeyListener(new NativeKeyListener() {
            @Override
            public void nativeKeyPressed(NativeKeyEvent nativeEvent) {




               // date.setTime();



                NativeKeyListener.super.nativeKeyTyped(nativeEvent);
                System.out.println("Key Released: " + NativeKeyEvent.getKeyText(nativeEvent.getKeyCode()));
                System.out.println(nativeEvent.getKeyCode());
                if (nativeEvent.getKeyCode() == 3639) {




                    pictureCount++;

                    if (shotMode ==0){
                         robot.keyPress(KeyEvent.VK_CONTROL);
                    robot.delay(100);
                    robot.keyPress(KeyEvent.VK_T);
                    robot.delay(100);
                    println("screenShot");
                    robot.keyRelease(KeyEvent.VK_CONTROL);
                    robot.keyRelease(KeyEvent.VK_T);
                    } else if (shotMode==1)  {











                        Cmder.executeCmdCommand("D:\\Snipaste\\Snipaste.exe snip -o E:\\imageFromMyMobile\\Pictures\\"+System.currentTimeMillis()+".png");
                    }


                }
            }
        });

        PApplet.main("Main");
    }

    public void settings() {
        size(200, 400);
          //date = new Date();
    }
static Textarea textarea;
    public void setup() {
        background(BACKGROUND);
        smooth();
        cp5 = new ControlP5(this);
        cp5.addButton("GenAns")
                .setPosition(10,30)
                .setSize(180,20)
        ;
        cp5.addButton("PrintAns")
                .setPosition(10,60)
                .setSize(180,20)
        ;
cp5.addButton("screenshotMode")
                .setPosition(10,90)
                .setSize(180,20)
        ;

        modeShow = new Textarea(cp5, "ModeShow").setPosition(10,120).setSize(180,20).show()
                .setColorBackground(BACKGROUND-10)
           .setText("fullscreen");





//cp5.addButton(cp5, "enableN").setPosition(10,150)
//                .setSize(180,20);
//cp5.addButtonBar("sel").setItems(new String[]{"s", "d"}).

        textarea = new Textarea(cp5,"text" ).setColorBackground(BACKGROUND-10).setSize(180,80)
                .setPosition(10,300).setBorderColor(30).setText("welcome").show();


    }


Textarea modeShow;
    public void draw() {
        background(BACKGROUND);
    }
    public void GenAns(int theValue) throws IOException {
        times++;
       // print("start to generate answers in txt");
        imageToTest();
        print(" Your code is:  "
                +times);


    }
//  static   int N=0;
//    public void enableN(int theValue) throws IOException {
//
//
//
//
//
//
//
//
//
//
//
//    }
    public void screenshotMode(int theValue) throws IOException {

        shotMode++;
if (shotMode == 1) {modeShow.setText("part");

        }else {
            shotMode = 0;modeShow.setText("fullscreen");
        }


    }

    public void PrintAns(int theValue) throws IOException {

      //  Desktop.getDesktop().open(new File());
Invoker.print(OUTPUTPATH);
//        robot.delay(500);
//        robot.keyPress(KeyEvent.VK_CONTROL);
//        robot.delay(100);
//        robot.keyPress(KeyEvent.VK_P);
//        robot.delay(100);
//        robot.keyRelease(KeyEvent.VK_CONTROL);
//        robot.keyRelease(KeyEvent.VK_P);
//        robot.delay(100);
//
//        robot.keyRelease(KeyEvent.VK_ENTER);

    }
    public static void print(String sss){

        textarea.setText(sss);

    }


//     public void keyReleased() {
//
//         if (keyCode == KeyEvent.VK_PRINTSCREEN) {
//
//             robot.keyPress(KeyEvent.VK_CONTROL);
//             robot.delay(100);
//             robot.keyPress(KeyEvent.VK_T);
//             robot.delay(100);
//             println('s');
//             robot.keyRelease(KeyEvent.VK_CONTROL);
//             robot.keyRelease(KeyEvent.VK_T);
//
//         }
//
//    }


    static final String PATH="E:\\imageFromMyMobile\\Pictures";
static  final String OUTPUTPATH="E:\\OUTPUT\\outForLearningHelper.txt";

    public static void imageToTest() throws FileNotFoundException, IOException {
        File outText = new File(OUTPUTPATH);
        if(!outText.exists()) {
            outText.createNewFile();
        }
        FileWriter fileWriter =new FileWriter(outText);
        fileWriter.write("["+times+"]");  //写入空
        fileWriter.flush();
        fileWriter.close();

        BufferedWriter out = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(OUTPUTPATH,true)));

        String[] fileList = new File(PATH).list();



        for (int i = 0; i < fileList.length; i++) {



            String content = imageOCR.imageOCR(PATH + "\\" + fileList[i]);
            if (content==null){
                continue;
            }

            JSONObject jsonObject=new JSONObject(content.replaceAll("[\n]", ""));
            JSONArray wordList = jsonObject.getJSONArray("words_result");
            for (int j = 0; j < wordList.length(); j++) {
                String word = wordList.getJSONObject(j).getString("words");
                out.write(word);
            }
            out.write("\r\n");
        }
        out.close();
        for (int i = 0; i < fileList.length; i++) {

            new File(PATH + "\\" + fileList[i]).delete();

        }

    }

}