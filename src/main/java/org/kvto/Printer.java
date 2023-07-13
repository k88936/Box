import com.profesorfalken.jpowershell.PowerShell;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Printer {

    static final String printerName = "Jolimark FP-530K (副本 3)";
    static final String command = "get-wmiobject -class win32_printer | Select-Object Name, PrinterState, PrinterStatus | where {$_.Name -eq '" + printerName + "'}";
    static PowerShell powerShell;

    final ArrayList<Boolean> history = new ArrayList<>();

    final  Word2Pdf converter= new Word2Pdf();
    public Boolean allPdf=false
            ;

    Printer() {


        for (int i = 0; i < 10; i++) {
            history.add(false);
        }



        new Thread("openPowerShell") {
            @Override
            public void run() {
                powerShell = PowerShell.openSession("C:\\Windows\\System32\\WindowsPowerShell\\v1.0\\powershell.exe");
                System.out.println("PowerShell started successfully");
                while(true){
                    String state=powerShell.executeCommand(command).getCommandOutput();
                    try {
                        sleep(50);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    synchronized (history) {
                        //System.out.println(state);
                        history.remove(0);
                        history.add(state.contains("Jolimark FP-530K (副本 3)                             0                         3"));


                    }

                }
            }
        }.start();

    }
static  final File txt =new File(Selector.FilePath + File.separator + "current.txt");
  static   final File pdf =new File(Selector.FilePath + File.separator + "current.pdf");
static final File doc=new File(Selector.FilePath + File.separator + "current.doc");
static final File docx =new File(Selector.FilePath + File.separator + "current.docx");


public Boolean forcePrint=false;
    public Boolean isPrinting(Boolean bool) {

if (forcePrint){
    return  false;
}

        if (powerShell == null) {
            return false;
        }


        //count how many times an object emerge in a list
        int num=0;
        synchronized (history){

            for (boolean story:
                    history) {
                if (story) {
                    num++;
                }
            }
        }


        //System.out.println(num);

        if (num>7){
            return false;
        }else if (num<3){
            return true;
        }else {
            return bool;
        }






    }

    void print(File file) throws IOException {
        int id=0;
        if (file != null) {
            if (file.getName().startsWith("current")){
                Invoker.print(file.getPath());
            }else {
                File type;
                if (file.getName().endsWith(".pdf")) {
                    type = pdf;
                } else if (file.getName().endsWith(".txt")) {
                    type = txt;
                } else if (file.getName().endsWith(".doc")) {


                    if (allPdf){


                        converter.convert(file.getPath());
                        Invoker.print(pdf.getPath());
                        return;
                    }



                    id=1;
                    type = doc;
                } else if (file.getName().endsWith(".docx")) {
                     if (allPdf){


                        converter.convert(file.getPath());
                        Invoker.print(pdf.getPath());
                        return;
                    }
                    id=1;
                    type = docx;
                } else {
                    return;
                }

                FileUtils.copyFile(file, type);
                Invoker.print(type.getPath());
            }



        }

    }
    void rePrint(File file){
        int id=0;
        File type;
        if (file.getName().endsWith(".pdf")) {
            type = pdf;
        } else if (file.getName().endsWith(".txt")) {
            type = txt;
        } else if (file.getName().endsWith(".doc")) {
            id=1;
            type = doc;
        } else if (file.getName().endsWith(".docx")) {
            id=1;
            type = docx;
        } else {
            return;
        }
        Invoker.print(type.getPath());

    }

    public void finl() {
        this.converter.finl();
    }
}



